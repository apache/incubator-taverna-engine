package net.sf.taverna.t2.provenance.item;

import java.sql.Date;

import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

public class InvocationStartedProvenanceItem extends AbstractProvenanceItem {

	private Activity<?> activity;
	
	private String invocationProcessId;

	private Date invocationStarted;
	
	public final Date getInvocationStarted() {
		return invocationStarted;
	}

	@Override
	public SharedVocabulary getEventType() {
		return SharedVocabulary.INVOCATION_STARTED_EVENT_TYPE;
	}

	public void setActivity(Activity<?> activity) {
		this.activity = activity;
	}

	public Activity<?> getActivity() {
		return activity;
	}

	public void setInvocationProcessId(String invocationProcessId) {
		this.invocationProcessId = invocationProcessId;
	}

	public String getInvocationProcessId() {
		return invocationProcessId;
	}

	public void setInvocationStarted(Date invocationStarted) {
		this.invocationStarted = invocationStarted;
		
	}

}
