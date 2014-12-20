package net.sf.taverna.t2.provenance.item;

import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;

public abstract class AbstractProvenanceItem implements ProvenanceItem {
	@Override
	public abstract SharedVocabulary getEventType();

	private String identifier, parentId, processId, workflowId;

	@Override
	public final String getIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		return 31 + (identifier == null ? 0 : identifier.hashCode());
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

	@Override
	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public final String getParentId() {
		return parentId;
	}

	@Override
	public final void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public final String getProcessId() {
		return processId;
	}

	@Override
	public final void setProcessId(String processId) {
		this.processId = processId;
	}

	@Override
	public final String getWorkflowId() {
		return workflowId;
	}

	@Override
	public final void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	
	@Override
	public String toString() {
		return getEventType() + " id:" + getIdentifier() + " parent:" + getParentId();
	}
}
