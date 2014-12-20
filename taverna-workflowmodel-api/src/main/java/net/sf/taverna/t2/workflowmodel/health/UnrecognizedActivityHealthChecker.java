/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.health;

import static net.sf.taverna.t2.visit.VisitReport.Status.SEVERE;
import static net.sf.taverna.t2.workflowmodel.health.HealthCheck.UNRECOGNIZED;

import java.util.List;

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.workflowmodel.processor.activity.UnrecognizedActivity;

/**
 * Check on the health of a UnrecognizedActivity
 * 
 * @author alanrw
 * 
 */
public class UnrecognizedActivityHealthChecker implements
		HealthChecker<UnrecognizedActivity> {

	/**
	 * The visitor can visit {@link UnrecognizedActivity}s.
	 */
	@Override
	public boolean canVisit(Object o) {
		return ((o != null) && (o instanceof UnrecognizedActivity));
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
	public VisitReport visit(UnrecognizedActivity o, List<Object> ancestry) {
		return new VisitReport(HealthCheck.getInstance(), o,
				"Service is unrecognized", UNRECOGNIZED, SEVERE);
	}
}
