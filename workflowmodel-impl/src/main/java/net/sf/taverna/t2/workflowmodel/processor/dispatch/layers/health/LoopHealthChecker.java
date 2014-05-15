/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.health;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.VisitReport.Status;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.health.HealthCheck;
import net.sf.taverna.t2.workflowmodel.health.HealthChecker;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Loop;

/**
 * @author alanrw
 *
 */
public class LoopHealthChecker implements HealthChecker<Processor> {

	public boolean canVisit(Object o) {
		return ((o != null) && (o instanceof Processor));
	}

	public boolean isTimeConsuming() {
		return false;
	}

	public VisitReport visit(Processor o, List<Object> ancestry) {
		List<VisitReport> reports = new ArrayList<VisitReport>();
		DispatchStack ds = o.getDispatchStack();
		for (DispatchLayer<?> dl : ds.getLayers()) {
			if (dl instanceof Loop) {
				Activity<?> conditionActivity = ((Loop)dl).getConfiguration().getCondition();
				if (conditionActivity == null) {
					break;
				}
				for (ActivityInputPort aop : conditionActivity.getInputPorts()) {
					String portName = aop.getName();
					
					// The loop port is used to control whether the main activity should loop
					if (portName.equals("loop")) {
						continue;
					}
					boolean found = false;
					for (OutputPort pop : o.getOutputPorts()) {
						if (pop.getName().equals(portName)) {
							found = true;
							break;
						}
					}
					if (!found) {
						VisitReport vr = new VisitReport(HealthCheck.getInstance(), o, "Missing connection", HealthCheck.LOOP_CONNECTION, Status.SEVERE);
						vr.setProperty("portname", portName);
						reports.add(vr);
					}
				}
			}
		}
		if (reports.isEmpty()) {
			return null;
		} else {
			Status status = VisitReport.getWorstStatus(reports);
			return new VisitReport(HealthCheck.getInstance(), o, "Loop layer report", HealthCheck.NO_PROBLEM,
					status, reports);
		}
	}

}
