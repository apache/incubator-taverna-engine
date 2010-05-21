/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.health;

import java.util.List;

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.VisitReport.Status;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.DisabledActivity;

/**
 * Check on the health of a DisabledActivity
 * 
 * @author alanrw
 *
 */
public class DisabledActivityHealthChecker implements HealthChecker<DisabledActivity> {

	/* *
	 * The visitor can visit DisabledActivitys.
	 * 
	 * (non-Javadoc)
	 * @see net.sf.taverna.t2.visit.Visitor#canVisit(java.lang.Object)
	 */
	public boolean canVisit(Object o) {
		return ((o != null) && (o instanceof DisabledActivity));
	}

	/** 
	 * The check is not time consuming as it simply constructs a VisitReport
	 * 
	 * (non-Javadoc)
	 * @see net.sf.taverna.t2.visit.Visitor#isTimeConsuming()
	 */
	public boolean isTimeConsuming() {
		return false;
	}

	/**
	 * The result of the visit is simply a VisitReport to state that the service is not available.
	 * 
	 * (non-Javadoc)
	 * @see net.sf.taverna.t2.visit.Visitor#visit(java.lang.Object, java.util.List)
	 */
	public VisitReport visit(DisabledActivity o, List<Object> ancestry) {
		return new VisitReport(HealthCheck.getInstance(), o, "Service is not available", HealthCheck.DISABLED, Status.SEVERE);
	}

}
