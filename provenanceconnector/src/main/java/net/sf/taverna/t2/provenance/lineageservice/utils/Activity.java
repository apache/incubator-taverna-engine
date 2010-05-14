package net.sf.taverna.t2.provenance.lineageservice.utils;

public class Activity {
	private String activityId;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activityId == null) ? 0 : activityId.hashCode());
		return result;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Activity [activityDefinition=");
		builder.append(activityDefinition);
		builder.append("]");
		return builder.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Activity other = (Activity) obj;
		if (activityId == null) {
			if (other.activityId != null)
				return false;
		} else if (!activityId.equals(other.activityId))
			return false;
		return true;
	}
	private String activityDefinition;
	private Workflow workflow;
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getActivityDefinition() {
		return activityDefinition;
	}
	public void setActivityDefinition(String activityDefinition) {
		this.activityDefinition = activityDefinition;
	}
	public Workflow getWorkflow() {
		return workflow;
	}
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
}
