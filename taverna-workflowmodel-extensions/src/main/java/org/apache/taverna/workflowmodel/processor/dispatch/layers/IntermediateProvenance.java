/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.processor.dispatch.layers;

import static java.lang.System.currentTimeMillis;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.taverna.invocation.Event;
import org.apache.taverna.provenance.item.ActivityProvenanceItem;
import org.apache.taverna.provenance.item.ErrorProvenanceItem;
import org.apache.taverna.provenance.item.InputDataProvenanceItem;
import org.apache.taverna.provenance.item.IterationProvenanceItem;
import org.apache.taverna.provenance.item.OutputDataProvenanceItem;
import org.apache.taverna.provenance.item.ProcessProvenanceItem;
import org.apache.taverna.provenance.item.ProcessorProvenanceItem;
import org.apache.taverna.provenance.item.ProvenanceItem;
import org.apache.taverna.provenance.item.WorkflowProvenanceItem;
import org.apache.taverna.provenance.reporter.ProvenanceReporter;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.dispatch.AbstractDispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchCompletionEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchErrorEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobQueueEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchResultEvent;

import org.apache.log4j.Logger;

/**
 * Sits above the {@link Invoke} layer and collects information about the
 * current workflow run to be stored by the {@link ProvenanceConnector}.
 * 
 * @author Ian Dunlop
 * @author Stian Soiland-Reyes
 * 
 */
public class IntermediateProvenance extends AbstractDispatchLayer<String> {
	public static final String URI = "http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/IntermediateProvenance";
	private static final Logger logger = Logger.getLogger(IntermediateProvenance.class);

	private ProvenanceReporter reporter;
	private Map<String, Map<String, IterationProvenanceItem>> processToIndexes = new HashMap<>();
	private Map<ActivityProvenanceItem, List<Object>> activityProvenanceItemMap = new HashMap<>();
	private Map<InputDataProvenanceItem, List<Object>> inputDataProvenanceItemMap = new HashMap<>();

	// private List<ActivityProvenanceItem> activityProvenanceItemList = new ArrayList<>();
	// private List<InputDataProvenanceItem> inputDataProvenanceItemList = new ArrayList<>();

	private WorkflowProvenanceItem workflowItem;

	@Override
	public void configure(String o) {
	}

	/**
	 * A set of provenance events for a particular owning process has been
	 * finished with so you can remove all the {@link IterationProvenanceItem}s
	 * from the map
	 */
	@Override
	public void finishedWith(String owningProcess) {
		processToIndexes.remove(owningProcess);
	}

	@Override
	public String getConfiguration() {
		return null;
	}

	protected Map<String, IterationProvenanceItem> getIndexesByProcess(
			String owningProcess) {
		synchronized (processToIndexes) {
			Map<String, IterationProvenanceItem> indexes = processToIndexes
					.get(owningProcess);
			if (indexes == null) {
				indexes = new HashMap<>();
				processToIndexes.put(owningProcess, indexes);
			}
			return indexes;
		}
	}

	protected IterationProvenanceItem getIterationProvItem(Event<?> event) {
		String owningProcess = event.getOwningProcess();
		int[] originalIndex = event.getIndex();
		int[] index = event.getIndex();
		String indexStr = indexStr(index);
		Map<String, IterationProvenanceItem> indexes = getIndexesByProcess(owningProcess);
		IterationProvenanceItem iterationProvenanceItem = null;
		synchronized (indexes) {
			// find the iteration item for the int index eg [1]
			iterationProvenanceItem = indexes.get(indexStr);
			// if it is null then strip the index and look again

			while (iterationProvenanceItem == null) {
				try {
					index = removeLastIndex(index);
					iterationProvenanceItem = indexes.get(indexStr(index));
					/*
					 * if we have a 'parent' iteration then create a new
					 * iteration for the original index and link it to the
					 * activity and the input data
					 * 
					 * FIXME should this be linked to the parent iteration
					 * instead?
					 */
					if (iterationProvenanceItem != null) {
						// set the index to the one from the event
						IterationProvenanceItem iterationProvenanceItem1 = new IterationProvenanceItem();
						iterationProvenanceItem1.setIteration(originalIndex);
						iterationProvenanceItem1.setProcessId(owningProcess);
						iterationProvenanceItem1.setIdentifier(UUID
								.randomUUID().toString());
						iterationProvenanceItem1.setWorkflowId(workflowItem.getParentId());
						iterationProvenanceItem1.setParentIterationItem(iterationProvenanceItem);
						iterationProvenanceItem1.setParentId(iterationProvenanceItem.getParentId());
						iterationProvenanceItem1.setInputDataItem(iterationProvenanceItem.getInputDataItem());

//						for (Entry<ActivityProvenanceItem, List<Object>> entrySet : activityProvenanceItemMap
//								.entrySet()) {
//							List<Object> value = entrySet.getValue();
//							int[] newIndex = (int[]) value.get(0);
//							String owner = (String) value.get(1);
//							String indexString = indexStr(newIndex);
//							String indexString2 = indexStr(index);
//
//							if (owningProcess.equalsIgnoreCase(owner)
//									&& indexString
//											.equalsIgnoreCase(indexString2))
//								iterationProvenanceItem1.setParentId(entrySet
//										.getKey().getIdentifier());
//						}
//						for (Entry<InputDataProvenanceItem, List<Object>> entrySet : inputDataProvenanceItemMap
//								.entrySet()) {
//							List<Object> value = entrySet.getValue();
//							int[] newIndex = (int[]) value.get(0);
//							String owner = (String) value.get(1);
//							String indexString = indexStr(newIndex);
//							String indexString2 = indexStr(index);
//							if (owningProcess.equalsIgnoreCase(owner)
//									&& indexString
//											.equalsIgnoreCase(indexString2))
//								iterationProvenanceItem1
//										.setInputDataItem(entrySet.getKey());
//						}

						// for (ActivityProvenanceItem item :
						// activityProvenanceItemList) {
						// if (owningProcess.equalsIgnoreCase(item
						// .getProcessId())) {
						// iterationProvenanceItem1.setParentId(item
						// .getIdentifier());
						// }
						// }
						// for (InputDataProvenanceItem item :
						// inputDataProvenanceItemList) {
						// if (owningProcess.equalsIgnoreCase(item
						// .getProcessId())) {
						// iterationProvenanceItem1.setInputDataItem(item);
						// }
						// indexes.put(indexStr, iterationProvenanceItem1);
						// return iterationProvenanceItem1;
						// // }
						// }

						// add this new iteration item to the map
						getIndexesByProcess(event.getOwningProcess()).put(
								indexStr(event.getIndex()),
								iterationProvenanceItem1);
						return iterationProvenanceItem1;
					}
					/*
					 * if we have not found an iteration items and the index is
					 * [] then something is wrong remove the last index in the
					 * int array before we go back through the while
					 */
				} catch (IllegalStateException e) {
					logger
							.warn("Cannot find a parent iteration with index [] for owning process: "
									+ owningProcess
									+ "Workflow invocation is in an illegal state");
					throw e;
				}
			}

			// if (iterationProvenanceItem == null) {
			// logger.info("Iteration item was null for: "
			// + event.getOwningProcess() + " " + event.getIndex());
			// System.out.println("Iteration item was null for: "
			// + event.getOwningProcess() + " " + event.getIndex());
			// iterationProvenanceItem = new IterationProvenanceItem(index);
			// iterationProvenanceItem.setProcessId(owningProcess);
			// iterationProvenanceItem.setIdentifier(UUID.randomUUID()
			// .toString());
			// // for (ActivityProvenanceItem
			// item:activityProvenanceItemList)
			// // {
			// // if (owningProcess.equalsIgnoreCase(item.getProcessId())) {
			// // iterationProvenanceItem.setParentId(item.getIdentifier());
			// // }
			// // }
			// // for (InputDataProvenanceItem item:
			// // inputDataProvenanceItemList) {
			// // if (owningProcess.equalsIgnoreCase(item.getProcessId())) {
			// // iterationProvenanceItem.setInputDataItem(item);
			// // }
			// // }
			// indexes.put(indexStr, iterationProvenanceItem);

		}
		return iterationProvenanceItem;
	}

	private String indexStr(int[] index) {
		StringBuilder indexStr = new StringBuilder();
		for (int ind : index)
			indexStr.append(":").append(ind);
		return indexStr.toString();
	}

	/**
	 * Remove the last index of the int array in the form 1:2:3 etc
	 * 
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unused")
	private String[] stripLastIndex(int[] index) {
		// will be in form :1:2:3
		return indexStr(index).split(":");
	}

	/**
	 * Remove the last value in the int array
	 * 
	 * @param index
	 * @return
	 */
	private int[] removeLastIndex(int[] index) {
		if (index.length == 0)
			throw new IllegalStateException(
					"There is no parent iteration of index [] for this result");
		int[] newIntArray = new int[index.length - 1];
		for (int i = 0; i < index.length - 1; i++)
			newIntArray[i] = index[i];
		return newIntArray;
	}

	private static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Create an {@link ErrorProvenanceItem} and send across to the
	 * {@link ProvenanceConnector}
	 */
	@Override
	public void receiveError(DispatchErrorEvent errorEvent) {
		IterationProvenanceItem iterationProvItem = getIterationProvItem(errorEvent);
		// get using errorEvent.getOwningProcess();
		
		ErrorProvenanceItem errorItem = new ErrorProvenanceItem();
		errorItem.setCause(errorEvent
				.getCause());
		errorItem.setErrorType(errorEvent
				.getFailureType().toString());
		errorItem.setMessage(errorEvent.getMessage());
		
		errorItem.setProcessId(errorEvent.getOwningProcess());
		errorItem.setIdentifier(uuid());
		errorItem.setParentId(iterationProvItem.getIdentifier());
		// iterationProvItem.setErrorItem(errorItem);
		// FIXME don't need to add to the processor item earlier
		getReporter().addProvenanceItem(errorItem);
		super.receiveError(errorEvent);
	}

	/**
	 * Create the {@link ProvenanceItem}s and send them all across to the
	 * {@link ProvenanceConnector} except for the
	 * {@link IterationProvenanceItem}, this one is told what it's inputs are
	 * but has to wait until the results are received before being sent across.
	 * Each item has a unique identifier and also knows who its parent item is
	 */
	@Override
	public void receiveJob(DispatchJobEvent jobEvent) {
			try {
			// FIXME do we need this ProcessProvenanceItem?
			ProcessProvenanceItem provenanceItem;
			String[] split = jobEvent.getOwningProcess().split(":");
			provenanceItem = new ProcessProvenanceItem();
			String parentDataflowId = workflowItem.getParentId();
			provenanceItem.setWorkflowId(parentDataflowId);
			provenanceItem.setFacadeID(split[0]);
			provenanceItem.setDataflowID(split[1]);
			provenanceItem.setProcessId(jobEvent.getOwningProcess());
			provenanceItem.setIdentifier(uuid());
			provenanceItem.setParentId(workflowItem.getIdentifier());
			ProcessorProvenanceItem processorProvItem;
			processorProvItem = new ProcessorProvenanceItem();
			processorProvItem.setWorkflowId(parentDataflowId);
			processorProvItem.setProcessId(jobEvent
					.getOwningProcess());
			processorProvItem.setIdentifier(uuid());
			processorProvItem.setParentId(provenanceItem.getIdentifier());
			provenanceItem.setProcessId(jobEvent.getOwningProcess());
			getReporter().addProvenanceItem(provenanceItem);
			getReporter().addProvenanceItem(processorProvItem);
	
			IterationProvenanceItem iterationProvItem = null;
			iterationProvItem = new IterationProvenanceItem();
			iterationProvItem.setWorkflowId(parentDataflowId);
			iterationProvItem.setIteration(jobEvent.getIndex());
			iterationProvItem.setIdentifier(uuid());
			
			ReferenceService referenceService = jobEvent.getContext()
					.getReferenceService();
	
			InputDataProvenanceItem inputDataItem = new InputDataProvenanceItem();
			inputDataItem.setDataMap(jobEvent.getData());
			inputDataItem.setReferenceService(referenceService);
			inputDataItem.setIdentifier(uuid());
			inputDataItem.setParentId(iterationProvItem.getIdentifier());
			inputDataItem.setProcessId(jobEvent.getOwningProcess());
	
			List<Object> inputIndexOwnerList = new ArrayList<>();
			inputIndexOwnerList.add(jobEvent.getIndex());
			inputIndexOwnerList.add(jobEvent.getOwningProcess());
			inputDataProvenanceItemMap.put(inputDataItem, inputIndexOwnerList);
	
			// inputDataProvenanceItemList.add(inputDataItem);
			iterationProvItem.setInputDataItem(inputDataItem);
			iterationProvItem.setIteration(jobEvent.getIndex());
			iterationProvItem.setProcessId(jobEvent.getOwningProcess());
	
			for (Activity<?> activity : jobEvent.getActivities())
				if (activity instanceof AsynchronousActivity) {
					ActivityProvenanceItem activityProvItem = new ActivityProvenanceItem();
					activityProvItem.setWorkflowId(parentDataflowId);
					activityProvItem.setIdentifier(uuid());
					iterationProvItem.setParentId(activityProvItem.getIdentifier());
					// getConnector().addProvenanceItem(iterationProvItem);
					activityProvItem.setParentId(processorProvItem.getIdentifier());
					// processorProvItem.setActivityProvenanceItem(activityProvItem);
					activityProvItem.setProcessId(jobEvent.getOwningProcess());
					List<Object> activityIndexOwnerList = new ArrayList<>();
					activityIndexOwnerList.add(jobEvent.getOwningProcess());
					activityIndexOwnerList.add(jobEvent.getIndex());
					activityProvenanceItemMap.put(activityProvItem,
							inputIndexOwnerList);
					// activityProvenanceItemList.add(activityProvItem);
					// activityProvItem.setIterationProvenanceItem(iterationProvItem);
					getReporter().addProvenanceItem(activityProvItem);
					break;
				}
			getIndexesByProcess(jobEvent.getOwningProcess()).put(
					indexStr(jobEvent.getIndex()), iterationProvItem);
			iterationProvItem.setEnactmentStarted(new Timestamp(currentTimeMillis()));
			getReporter().addProvenanceItem(iterationProvItem);
		} catch (RuntimeException ex) {
			logger.error("Could not store provenance for " + jobEvent, ex);
		}
		
		super.receiveJob(jobEvent);
	}

	@Override
	public void receiveJobQueue(DispatchJobQueueEvent jobQueueEvent) {
		super.receiveJobQueue(jobQueueEvent);
	}

	/**
	 * Populate an {@link OutputDataProvenanceItem} with the results and attach
	 * it to the appropriate {@link IterationProvenanceItem}. Then send the
	 * {@link IterationProvenanceItem} across to the {@link ProvenanceConnector}
	 */
	@Override
	public void receiveResult(DispatchResultEvent resultEvent) {
		try {
			// FIXME use the connector from the result event context
			IterationProvenanceItem iterationProvItem = getIterationProvItem(resultEvent);
			iterationProvItem.setEnactmentEnded(new Timestamp(currentTimeMillis()));
			
			ReferenceService referenceService = resultEvent.getContext()
					.getReferenceService();

			OutputDataProvenanceItem outputDataItem = new OutputDataProvenanceItem();
			outputDataItem.setDataMap(resultEvent.getData());
			outputDataItem.setReferenceService(referenceService);
			outputDataItem.setIdentifier(uuid());
			outputDataItem.setProcessId(resultEvent.getOwningProcess());
			outputDataItem.setParentId(iterationProvItem.getIdentifier());
			iterationProvItem.setOutputDataItem(outputDataItem);
			
			getReporter().addProvenanceItem(iterationProvItem);
			// getConnector().addProvenanceItem(outputDataItem);
	
			// PM -- testing
			// add xencoding of data value here??
	//		Map<String, T2Reference> inputDataMap = iterationProvItem.getInputDataItem().getDataMap();
	//		for(Map.Entry<String, T2Reference> entry:inputDataMap.entrySet()) {
	//			// create a simpler bean that we can serialize?
	//			
	//			T2Reference ref = entry.getValue();
	//			
	//			SimplerT2Reference t2RefBean = new SimplerT2Reference();
	//			t2RefBean.setReferenceType(ref.getReferenceType());
	//			t2RefBean.setDepth(ref.getDepth());
	//			t2RefBean.setLocalPart(ref.getLocalPart());
	//			t2RefBean.setNamespacePart(ref.getNamespacePart());
	//						
	//			System.out.println("data ref: "+ref);
	//			String serializedInput = SerializeParam(t2RefBean);
	//			System.out.println("serialized reference:" + serializedInput);
	//			
	//			System.out.println(referenceService.renderIdentifier(entry.getValue(), String.class, resultEvent.getContext()));
//		}
		} catch (Exception ex) {
			logger.error("Could not store provenance for "
					+ resultEvent.getOwningProcess() + " "
					+ Arrays.toString(resultEvent.getIndex()), ex);
			// But don't break super.receiveResult() !!
		}
		super.receiveResult(resultEvent);
	}

	@Override
	public void receiveResultCompletion(DispatchCompletionEvent completionEvent) {
		super.receiveResultCompletion(completionEvent);
	}

	/**
	 * Tell this layer what {@link ProvenanceConnector} implementation is being
	 * used to capture the {@link ProvenanceItem}s. NOTE: should probably use
	 * the connector from the result events context where possible
	 * 
	 * @param connector
	 */
	public void setReporter(ProvenanceReporter connector) {
		this.reporter = connector;
	}

	public ProvenanceReporter getReporter() {
		return reporter;
	}

	/**
	 * So that the {@link ProvenanceItem}s know which {@link Dataflow} has been
	 * enacted this layer has to know about the {@link WorkflowProvenanceItem}
	 * 
	 * @param workflowItem
	 */
	public void setWorkflow(WorkflowProvenanceItem workflowItem) {
		this.workflowItem = workflowItem;
	}

	// TODO is this unused?
	public static String SerializeParam(Object ParamValue) {
		ByteArrayOutputStream BStream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(BStream);
		encoder.writeObject(ParamValue);
		encoder.close();
		return BStream.toString();
	}

	// TODO is this unused?
	public static Object DeserializeParam(String SerializedParam) {
		InputStream IStream = new ByteArrayInputStream(
				SerializedParam.getBytes());
		XMLDecoder decoder = new XMLDecoder(IStream);
		Object output = decoder.readObject();
		decoder.close();
		return output;
	}
}
