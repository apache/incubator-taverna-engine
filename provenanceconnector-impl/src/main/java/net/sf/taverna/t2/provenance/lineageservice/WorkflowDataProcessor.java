/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * @author paolo
 *
 */
public class WorkflowDataProcessor {

	// set of trees (impl as lists), one for each varname
	Map<String, List<WorkflowDataNode>> workflowDataTrees = new HashMap<String, List<WorkflowDataNode>>();  
	
	ProvenanceQuery pq=null;
	
	
	public void addWorkflowDataItem(Element root) {
		
		Element portEl = root.getChild("port");
		
		WorkflowDataNode wdn = new WorkflowDataNode();
		wdn.setVarName(portEl.getAttributeValue("name"));
		wdn.setValue(portEl.getAttributeValue("data"));
		wdn.setIndex(root.getAttributeValue("index"));

		if (wdn.getValue().contains("list")) wdn.setList(true);
		else wdn.setList(false);
		
		// position this wdn into the tree associated to its varname
		List<WorkflowDataNode> aTree = workflowDataTrees.get(wdn.getVarName());
		
		if (aTree == null)  {
			aTree = new ArrayList<WorkflowDataNode>();
		} else {
			for (WorkflowDataNode aNode: aTree) {
				if (isPrefix(wdn.getIndex(), aNode.getIndex())) {
					aNode.setParent(wdn);
				}
			}
		}
		aTree.add(wdn);
	}

	
	
	/**
	 * writes records to VarBinding or Collection by traversing the trees<br/>
	 * expect to be invoked after workflow completion
	 * @param wfInstanceRef
	 */
	public void processTrees(String wfInstanceRef) {
		
	}
	
	
	private boolean isPrefix(String index1, String index2) {
		
		// strip first and last '[' ']'
		String index11 = index1.substring(1, index1.length()-1);
		String index22 = index2.substring(1, index2.length()-1);
		
		return (index22.startsWith(index11));
	}



	class WorkflowDataNode {
		
		String varName;
		String value;
		String index;
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
}
