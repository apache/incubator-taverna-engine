/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.lineageservice;

import static org.apache.taverna.provenance.lineageservice.utils.ProvenanceUtils.iterationToString;
import static org.apache.taverna.provenance.lineageservice.utils.ProvenanceUtils.parentProcess;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.taverna.facade.WorkflowInstanceFacade.State;
import net.sf.taverna.t2.provenance.item.DataflowRunComplete;
import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowDataProvenanceItem;
import org.apache.taverna.provenance.lineageservice.utils.DataBinding;
import org.apache.taverna.provenance.lineageservice.utils.DataflowInvocation;
import org.apache.taverna.provenance.lineageservice.utils.Port;
import org.apache.taverna.provenance.lineageservice.utils.PortBinding;
import org.apache.taverna.provenance.lineageservice.utils.ProcessorEnactment;

import org.apache.log4j.Logger;

/**
 * @author paolo
 * this class manages the outputs from a workflow, as they come along through WorkflowData events
 */
public class WorkflowDataProcessor {
	private static Logger logger = Logger.getLogger(WorkflowDataProcessor.class);

	// set of trees (impl as lists), one for each portName
	// PM portName not enough must use the WFID as context as well, because the same output portName
	// may occur in multiple nested workflows
	Map<String, List<WorkflowDataNode>> workflowDataTrees = new HashMap<>();

	protected Map<String, Timestamp> workflowStarted = new ConcurrentHashMap<>();

	ProvenanceQuery pq=null;
	ProvenanceWriter pw = null;

	protected Map<String, ProcessorEnactment> invocationProcessToProcessEnactment = new ConcurrentHashMap<>();

	/**
	 * adds the input ProvenanceItem event to the tree structure corresponding
	 * to the portName found in the item. Repeated invocations of this method
	 * incrementally reconstruct the tree structure for each of the workflow
	 * outputs
	 *
	 * @param root
	 */
	public void addWorkflowDataItem(ProvenanceItem provenanceItem) {
		WorkflowDataProvenanceItem workflowDataItem = (WorkflowDataProvenanceItem) provenanceItem;

		WorkflowDataNode wdn = new WorkflowDataNode();
		wdn.setProcessId(provenanceItem.getProcessId());
		wdn.setPortName(workflowDataItem.getPortName());
		wdn.setInputPort(workflowDataItem.isInputPort());
		wdn.setValue(workflowDataItem.getData().toString());
		int[] index = workflowDataItem.getIndex();
		String iterationToString = iterationToString(index);
		wdn.setIndex(iterationToString);
		wdn.setWorkflowID(workflowDataItem.getWorkflowId());

		if (wdn.getValue().contains("list"))
			wdn.setList(true);  // HACK
		else
			wdn.setList(false);

		// position this wdn into the tree associated to its portName
		List<WorkflowDataNode> aTree = workflowDataTrees.get(wdn.getPortName());

		if (aTree == null)  { // first item in the tree
			aTree = new ArrayList<WorkflowDataNode>();
			workflowDataTrees.put(wdn.getPortName(), aTree);
		} else
			// update parent pointers
			for (WorkflowDataNode aNode: aTree)
				if (isParent(wdn.getIndex(), aNode.getIndex())) {
					aNode.setParent(wdn);

					// set position in collection as the last index in the vector
					aNode.setRelativePosition(getPosition(aNode));
				}
		aTree.add(wdn);
	}

	/**
	 * writes records to PortBinding or Collection by traversing the trees<br/>
	 * expect this to be invoked after workflow completion
	 *
	 * @param workflowId
	 *            the external name of the dataflow (not the UUID)
	 * @param workflowRunId
	 *            the runID
	 */
	public void processTrees(DataflowRunComplete completeEvent,
			String workflowRunId) {
		String workflowId = completeEvent.getWorkflowId();
		logger.debug("processing output trees");

		// i:inputPortName -> t2Ref
		Map<String, String> workflowPortData = new HashMap<>();

		for (Map.Entry<String, List<WorkflowDataNode>> entry : workflowDataTrees
				.entrySet()) {
			String portName = entry.getKey();
			List<WorkflowDataNode> tree = entry.getValue();

			PortBinding vb = null;

			logger.debug("storing tree for var "+portName+" in workflow with ID "+workflowId+" and instance "+workflowRunId);
			for (WorkflowDataNode node:tree) {
				try {
					if (!node.getWorkflowID().equals(workflowId))
						continue;

					if (node.getIndex().equals("[]")) {
						// Store top-level workflow inputs/outputs
						if (! node.getProcessId().equals(completeEvent.getProcessId()))
							//logger.warn("Unexpected process ID " + node.getProcessId() + " expected " + completeEvent.getProcessId());
							continue;
						String portKey = (node.isInputPort() ? "/i:" : "/o:") + node.getPortName();
						workflowPortData.put(portKey, node.getValue());
					}

					if (node.isList) {
						logger.debug("creating collection entry for "
								+ node.value + " with index " + node.index);

						if (node.getParent() != null) {
							logger.debug(" and parent " + node.parent.index);
							// write a collection record to DB
							getPw().addCollection(workflowId, node.getValue(),
									node.getParent().getValue(),
									node.getIndex(), portName, workflowRunId);
						} else
							getPw().addCollection(workflowId, node.getValue(),
									null, node.getIndex(), portName,
									workflowRunId);
					} else {
						logger.debug("creating PortBinding for " + node.value
								+ " with index " + node.index);

						vb = new PortBinding();

						vb.setWorkflowId(workflowId);
						vb.setWorkflowRunId(workflowRunId);

						vb.setProcessorName(pq.getWorkflow(workflowId)
								.getExternalName());

						// vb.setValueType(); // TODO not sure what to set this to
						vb.setPortName(portName);
						vb.setIteration(node.getIndex());
						vb.setValue(node.getValue());

						if (node.getParent() != null) {
							logger.debug(" in collection "
									+ node.getParent().value + " with index "
									+ node.getParent().getIndex());

							vb.setCollIDRef(node.getParent().getValue());
							vb.setPositionInColl(node.getRelativePosition());
						} else
							vb.setPositionInColl(1); // default

						try {
							getPw().addPortBinding(vb);
						} catch (SQLException e) {
							logger.debug("Problem processing trees for workflow: "
									+ workflowId
									+ " instance: "
									+ workflowRunId
									+ " : updating instead of inserting");
							getPw().updatePortBinding(vb);
						}
					}
				} catch (SQLException e) {
					logger.debug(
							"Database problem processing trees for workflow: "
									+ workflowId + " instance: "
									+ workflowRunId + " : " + workflowId, e);
				}
			}
		}

		List<Port> ports = getPq().getPortsForDataflow(workflowId);
		String processId = completeEvent.getProcessId();

		DataflowInvocation invocation = new DataflowInvocation();
		invocation.setDataflowInvocationId(UUID.randomUUID().toString());
		invocation.setWorkflowId(workflowId);
		invocation.setWorkflowRunId(workflowRunId);

		String parentProcessId = parentProcess(processId, 1);
		if (parentProcessId != null) {
			ProcessorEnactment procAct = invocationProcessToProcessEnactment
					.get(parentProcessId);
			if (procAct != null)
				invocation.setParentProcessorEnactmentId(procAct
						.getProcessEnactmentId());
		}

		invocation.setInvocationStarted(workflowStarted.get(completeEvent
				.getParentId()));
		invocation.setInvocationEnded(completeEvent.getInvocationEnded());
		invocation.setCompleted(completeEvent.getState()
				.equals(State.completed));

		// Register data
		String inputsDataBindingId = UUID.randomUUID().toString();
		String outputsDataBindingId = UUID.randomUUID().toString();
		for (Port port : ports) {
			String portKey = (port.isInputPort() ? "/i:" : "/o:")
					+ port.getPortName();
			String t2Reference = workflowPortData.get(portKey);
			if (t2Reference == null) {
				logger.warn("No workflow port data for " + portKey);
				continue;
			}
			DataBinding dataBinding = new DataBinding();
			dataBinding
					.setDataBindingId(port.isInputPort() ? inputsDataBindingId
							: outputsDataBindingId);
			dataBinding.setPort(port);
			dataBinding.setT2Reference(t2Reference);
			dataBinding.setWorkflowRunId(workflowRunId);
			try {
				pw.addDataBinding(dataBinding);
			} catch (SQLException e) {
				logger.warn("Could not add databinding for " + portKey, e);
			}
		}

		invocation.setInputsDataBindingId(inputsDataBindingId);
		invocation.setOutputsDataBindingId(outputsDataBindingId);
		try {
			pw.addDataflowInvocation(invocation);
		} catch (SQLException e) {
			logger.warn("Could not store dataflow invocation for " + processId,
					e);
		}
	}


	/**
	 * @param node
	 * @return the last digit in the index
	 */
	private int getPosition(WorkflowDataNode node) {
		String[] vector = node.getIndex()
				.substring(1, node.getIndex().length() - 1).split(",");
		//TODO need some logic  here to avoid trying to parse "" as integer, this is my try

		if ((vector[vector.length-1]).equals(""))
			return 1;
		return Integer.parseInt(vector[vector.length - 1]) + 1;
	}

	private boolean isParent(String index1, String index2) {
		// strip first and last '[' ']'
		String index11 = index1.substring(1, index1.length() - 1);
		String index22 = index2.substring(1, index2.length() - 1);

		String[] tokens1 = index11.split(",");
		String[] tokens2 = index22.split(",");

		// [] cannot be parent of [x1,x2,...] with >= 2 elements
		if (index11.equals("") && tokens2.length > 1)
			return false;

		// [] is parent of any [x]
		if (index11.equals("") && tokens2.length == 1)
			return true;

		// [x1,x2, ...,xk] cannot be parent of [x1,x2,...xh] when k < h-1
		// because [x1,x2,...xh] is more than one level deeper than [x1,x2, ...,xk]
		if (tokens1.length != tokens2.length - 1)
			return false;

		return index22.startsWith(index11);
	}

	static class WorkflowDataNode {
		private String portName;
		private String value;
		private String index;
		private String workflowID;
		private int relativePosition;
		private boolean isList;
		private WorkflowDataNode parent;
		private String processId;
		private boolean isInputPort;

		public String getProcessId() {
			return processId;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		public void setProcessId(String processId) {
			this.processId = processId;

		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * @return the index
		 */
		public String getIndex() {
			return index;
		}

		/**
		 * @param index
		 *            the index to set
		 */
		public void setIndex(String index) {
			this.index = index;
		}

		/**
		 * @return the portName
		 */
		public String getPortName() {
			return portName;
		}

		/**
		 * @param portName
		 *            the portName to set
		 */
		public void setPortName(String portName) {
			this.portName = portName;
		}

		/**
		 * @return the isList
		 */
		public boolean isList() {
			return isList;
		}

		/**
		 * @param isList
		 *            the isList to set
		 */
		public void setList(boolean isList) {
			this.isList = isList;
		}

		/**
		 * @return the parent
		 */
		public WorkflowDataNode getParent() {
			return parent;
		}

		/**
		 * @param parent
		 *            the parent to set
		 */
		public void setParent(WorkflowDataNode parent) {
			this.parent = parent;
		}

		/**
		 * @return the relativePosition
		 */
		public int getRelativePosition() {
			return relativePosition;
		}

		/**
		 * @param relativePosition
		 *            the relativePosition to set
		 */
		public void setRelativePosition(int relativePosition) {
			this.relativePosition = relativePosition;
		}

		/**
		 * @return the workflowID
		 */
		public String getWorkflowID() {
			return workflowID;
		}

		/**
		 * @param workflowID
		 *            the workflowID to set
		 */
		public void setWorkflowID(String workflowID) {
			this.workflowID = workflowID;
		}

		public void setInputPort(boolean isInputPort) {
			this.isInputPort = isInputPort;
		}

		public boolean isInputPort() {
			return isInputPort;
		}
	}

	/**
	 * @return the pq
	 */
	public ProvenanceQuery getPq() {
		return pq;
	}

	/**
	 * @param pq
	 *            the pq to set
	 */
	public void setPq(ProvenanceQuery pq) {
		this.pq = pq;
	}

	/**
	 * @return the pw
	 */
	public ProvenanceWriter getPw() {
		return pw;
	}

	/**
	 * @param pw
	 *            the pw to set
	 */
	public void setPw(ProvenanceWriter pw) {
		this.pw = pw;
	}
}
