package net.sf.taverna.t2.provenance.item;

import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;

public abstract class AbstractProvenanceItem implements ProvenanceItem {

	public abstract SharedVocabulary getEventType();

	private String identifier, parentId, processId, workflowId;

	public final String getIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractProvenanceItem other = (AbstractProvenanceItem) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public final String getParentId() {
		return parentId;
	}

	public final void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public final String getProcessId() {
		return processId;
	}

	public final void setProcessId(String processId) {
		this.processId = processId;
	}

	public final String getWorkflowId() {
		return workflowId;
	}

	public final void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	
	@Override
	public String toString() {
		return getEventType() + " id:" + getIdentifier() + " parent:" + getParentId();
	}

}
