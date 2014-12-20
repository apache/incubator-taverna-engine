/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.health;

import static net.sf.taverna.t2.visit.VisitReport.Status.SEVERE;
import static net.sf.taverna.t2.workflowmodel.health.HealthCheck.DISABLED;

import java.util.List;

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.workflowmodel.processor.activity.DisabledActivity;

/**
 * Check on the health of a DisabledActivity
 * 
 * @author alanrw
 * 
 */
public class DisabledActivityHealthChecker implements
		HealthChecker<DisabledActivity> {

	/**
	 * The visitor can visit DisabledActivitys.
	 */
	@Override
	public boolean canVisit(Object o) {
		return ((o != null) && (o instanceof DisabledActivity));
	}

	/**
	 * The check is not time consuming as it simply constructs a VisitReport
	 */
	@Override
	public boolean isTimeConsuming() {
		return false;
	}

	/**
	 * The result of the visit is simply a VisitReport to state that the service
	 * is not available.
	 */
	@Override
	public VisitReport visit(DisabledActivity o, List<Object> ancestry) {
		return new VisitReport(HealthCheck.getInstance(), o,
				"Service is not available", DISABLED, SEVERE);
	}
}
