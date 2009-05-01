/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowDataProvenanceItem;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceUtils;
import net.sf.taverna.t2.provenance.lineageservice.utils.VarBinding;

/**
 * @author paolo
 *
 */
public class WorkflowDataProcessor {
	
	private static Logger logger = Logger.getLogger(WorkflowDataProcessor.class);

	// set of trees (impl as lists), one for each varname
	Map<String, List<WorkflowDataNode>> workflowDataTrees = new HashMap<String, List<WorkflowDataNode>>();  

	ProvenanceQuery pq=null;
	ProvenanceWriter pw = null;

	/**
	 * adds this event to the tree structure for its varname
	 * @param root
	 */
	public void addWorkflowDataItem(ProvenanceItem provenanceItem) {

//		Element portEl = root.getChild("port"); // this determines the tree to use

		WorkflowDataNode wdn = new WorkflowDataNode();
		wdn.setVarName(((WorkflowDataProvenanceItem)provenanceItem).getPortName());
		wdn.setValue(((WorkflowDataProvenanceItem)provenanceItem).getData().toString());
		int[] index = ((WorkflowDataProvenanceItem)provenanceItem).getIndex();
		String iterationToString = ProvenanceUtils.iterationToString(index);
		wdn.setIndex(iterationToString);

		if (wdn.getValue().contains("list")) wdn.setList(true);
		else wdn.setList(false);

		// position this wdn into the tree associated to its varname
		List<WorkflowDataNode> aTree = workflowDataTrees.get(wdn.getVarName());

		if (aTree == null)  { // first item in the tree
			aTree = new ArrayList<WorkflowDataNode>();
			workflowDataTrees.put(wdn.getVarName(), aTree);
		} else {
			// update parent pointers
			for (WorkflowDataNode aNode: aTree) {
				if (isParent(wdn.getIndex(), aNode.getIndex())) {
					aNode.setParent(wdn);
					
					// set position in collection as the last index in the vector
					aNode.setRelativePosition(getPosition(aNode));
				}
			}
		}
		aTree.add(wdn);
	}




	/**
	 * writes records to VarBinding or Collection by traversing the trees<br/>
	 * expect this to be invoked after workflow completion
	 * @param wfInstanceRef
	 */
	public void processTrees(String dataflowID, String wfInstanceRef) {

		logger.info("processing output trees");

		for (Map.Entry<String, List<WorkflowDataNode>> entry:workflowDataTrees.entrySet()) {

			String varName = entry.getKey();
			List<WorkflowDataNode> tree = entry.getValue();

			try {

				logger.info("storing tree for var "+varName);
				for (WorkflowDataNode node:tree) {
					if (node.isList) {

						logger.info("creating collection entry for "+
								node.value+" with index "+
								node.index);
						
						if (node.getParent()!=null) {
							logger.info(" and parent "+node.parent.index);
						// write a collection record to DB
						getPw().addCollection(dataflowID, 
								              node.getValue(), 
								              node.getParent().getValue(), 
								              node.getIndex(), 
								              varName, 
								              wfInstanceRef);
						} else {
							getPw().addCollection(dataflowID, 
						              node.getValue(), 
						              null, 
						              node.getIndex(), 
						              varName, 
						              wfInstanceRef);							
						}

					} else {
						logger.info("creating VarBinding for "+node.value+" with index "+node.index);
						
						VarBinding vb = new VarBinding();

						vb.setWfInstanceRef(wfInstanceRef);
						vb.setPNameRef(dataflowID);
						// vb.setValueType(); // TODO not sure what to set this to
						vb.setVarNameRef(varName);
						vb.setIterationVector(node.getIndex());
						vb.setValue(node.getValue());
						
						if (node.getParent()!=null) {
							logger.info(" in collection "+node.getParent().value+
									" with index "+node.getParent().getIndex());
							
							vb.setCollIDRef(node.getParent().getValue());
							vb.setPositionInColl(node.getRelativePosition());
							
						} else {
							vb.setPositionInColl(1);  // default							
						}						
						getPw().addVarBinding(vb);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	

	/**
	 * @param node
	 * @return the last digit in the index 
	 */
	private int getPosition(WorkflowDataNode node) {
		
		String[] vector = node.getIndex().substring(1, node.getIndex().length()-1).split(",");
		return Integer.parseInt(vector[vector.length-1]) +1;
	}



	private boolean isParent(String index1, String index2) {

		// strip first and last '[' ']'
		String index11 = index1.substring(1, index1.length()-1);
		String index22 = index2.substring(1, index2.length()-1);

		String[] tokens1 = index11.split(",");
		String[] tokens2 = index22.split(",");

		// [] cannot be parent of [x1,x2,...]  with >= 2 elements
		if (index11.equals("") && tokens2.length>1) return false;

		// [] is parent of any [x] 
		if (index11.equals("") && tokens2.length==1) return true;

		// [x1,x2, ...,xk] cannot be parent of [x1,x2,...xh] when k < h-1 
		// because [x1,x2,...xh] is more than one level deeper than [x1,x2, ...,xk] 
		if (tokens1.length != tokens2.length -1) return false;

		return (index22.startsWith(index11));
	}


	class WorkflowDataNode {

		String varName;
		String value;
		String index;
		int  relativePosition;
		boolean isList;
		WorkflowDataNode parent;

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
		/**
		 * @param value the value to set
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
		 * @param index the index to set
		 */
		public void setIndex(String index) {
			this.index = index;
		}

		/**
		 * @return the varName
		 */
		public String getVarName() {
			return varName;
		}
		/**
		 * @param varName the varName to set
		 */
		public void setVarName(String varName) {
			this.varName = varName;
		}
		/**
		 * @return the isList
		 */
		public boolean isList() {
			return isList;
		}
		/**
		 * @param isList the isList to set
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
		 * @param parent the parent to set
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
		 * @param relativePosition the relativePosition to set
		 */
		public void setRelativePosition(int relativePosition) {
			this.relativePosition = relativePosition;
		}

	}



	/**
	 * @return the pq
	 */
	public ProvenanceQuery getPq() {
		return pq;
	}



	/**
	 * @param pq the pq to set
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
	 * @param pw the pw to set
	 */
	public void setPw(ProvenanceWriter pw) {
		this.pw = pw;
	}
}
