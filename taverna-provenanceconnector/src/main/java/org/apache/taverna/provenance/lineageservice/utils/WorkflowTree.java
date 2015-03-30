/**
 * 
 */
package org.apache.taverna.provenance.lineageservice.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * a tree structure used to describe the nested static structure of a workflow
 * as it is found in the provenance DB
 * 
 * @author Paolo Missier
 */
public class WorkflowTree {
	private Workflow node;
	private List<WorkflowTree> children = new ArrayList<>();

	/**
	 * @return the children
	 */
	public List<WorkflowTree> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<WorkflowTree> children) {
		this.children = children;
	}

	/**
	 * @return the node
	 */
	public Workflow getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(Workflow node) {
		this.node = node;
	}

	public void addChild(WorkflowTree childStructure) {
		children.add(childStructure);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder(), 0);
	}

	public String toString(int indent) {
		return toString(new StringBuilder(), indent);
	}

	protected String toString(StringBuilder sb, int indent) {
		sb.append(getNode().getExternalName() + "\n");
		for (WorkflowTree tree : getChildren()) {
			indent++;
			for (int i = 1; i < indent; i++)
				sb.append("-");
			sb.append(tree.toString(indent));
			indent--;
		}
		return sb.toString();
	}
}
