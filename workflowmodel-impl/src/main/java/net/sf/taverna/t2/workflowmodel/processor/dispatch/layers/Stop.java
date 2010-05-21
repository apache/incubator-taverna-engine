/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.reference.WorkflowRunIdEntity;
import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.AbstractDispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobQueueEvent;

/**
 * This layer allows for the cancellation, pausing and resuming of workflow
 * runs. It does so by intercepting jobs sent to the layer.
 * 
 * @author alanrw
 * 
 */
public class Stop extends AbstractDispatchLayer<Object> {

	/**
	 * The set of ids of workflow runs that have been cancelled.
	 */
	private static Set<String> cancelledWorkflowRuns = new HashSet<String>();

	/**
	 * A map from workflow run ids to the set of Stop layers where jobs have
	 * been intercepted for that run.
	 */
	private static Map<String, Set<Stop>> pausedLayerMap = new HashMap<String, Set<Stop>>();

	/**
	 * A map for a given Stop from ids of suspended workflow runs to the jobs
	 * that have been intercepted.
	 */
	private Map<String, Set<DispatchJobEvent>> suspendedJobEventMap = new HashMap<String, Set<DispatchJobEvent>>();

	public void configure(Object conf) throws ConfigurationException {
		// nothing
	}

	public Object getConfiguration() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.taverna.t2.workflowmodel.processor.dispatch.AbstractDispatchLayer
	 * #receiveJob
	 * (net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobEvent
	 * )
	 */
	@Override
	public void receiveJob(final DispatchJobEvent jobEvent) {
		List<WorkflowRunIdEntity> entities = jobEvent.getContext().getEntities(
				WorkflowRunIdEntity.class);
		if (entities != null && !entities.isEmpty()) {
			final String wfRunId = entities.get(0).getWorkflowRunId();
			// If the workflow run is cancelled then simply "eat" the jobEvent.
			// This does a hard-cancel.
			if (cancelledWorkflowRuns.contains(wfRunId)) {
				return;
			}
			// If the workflow run is paused
			if (pausedLayerMap.containsKey(wfRunId)) {
				synchronized (Stop.class) {
					// double check as pausedLayerMap may have been changed
					// waiting for the lock
					if (pausedLayerMap.containsKey(wfRunId)) {
						// Remember that this Stop layer was affected by the
						// workflow pause
						pausedLayerMap.get(wfRunId).add(this);
						if (!suspendedJobEventMap.containsKey(wfRunId)) {
							suspendedJobEventMap.put(wfRunId,
									new HashSet<DispatchJobEvent>());
						}
						// Remember the suspended jobEvent
						suspendedJobEventMap.get(wfRunId).add(jobEvent);
						return;
					}
				}
			}
		}
		// By default pass the jobEvent down to the next layer
		super.receiveJob(jobEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.taverna.t2.workflowmodel.processor.dispatch.AbstractDispatchLayer
	 * #receiveJobQueue
	 * (net.sf.taverna.t2.workflowmodel.processor.dispatch.events
	 * .DispatchJobQueueEvent)
	 */
	public void receiveJobQueue(DispatchJobQueueEvent jobQueueEvent) {
		super.receiveJobQueue(jobQueueEvent);
	}

	/**
	 * Cancel the workflow run with the specified id
	 * 
	 * @param workflowRunId
	 *            The id of the workflow run to cancel
	 * @return If the workflow run was cancelled then true. If it was already
	 *         cancelled then false.
	 */
	public static synchronized boolean cancelWorkflow(String workflowRunId) {

		if (cancelledWorkflowRuns.contains(workflowRunId)) {
			return false;
		}
		Set<String> cancelledWorkflowRunsCopy = new HashSet<String>(
				cancelledWorkflowRuns);

		cancelledWorkflowRunsCopy.add(workflowRunId);

		cancelledWorkflowRuns = cancelledWorkflowRunsCopy;

		return true;
	}

	/**
	 * Pause the workflow run with the specified id
	 * 
	 * @param workflowRunId
	 *            The id of the workflow run to pause
	 * @return If the workflow run was paused then true. If it was already
	 *         paused or cancelled then false.
	 */
	public static synchronized boolean pauseWorkflow(String workflowRunId) {

		if (cancelledWorkflowRuns.contains(workflowRunId)) {
			return false;
		}
		if (!pausedLayerMap.containsKey(workflowRunId)) {
			Map<String, Set<Stop>> pausedLayerMapCopy = new HashMap<String, Set<Stop>>();
			pausedLayerMapCopy.putAll(pausedLayerMap);
			pausedLayerMapCopy.put(workflowRunId, new HashSet<Stop>());
			pausedLayerMap = pausedLayerMapCopy;
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Resume the workflow run with the specified id
	 * 
	 * @param workflowRunId
	 *            The id of the workflow run to resume
	 * @return If the workflow run was resumed then true. If the workflow run
	 *         was not paused or it was cancelled, then false.
	 */
	public static synchronized boolean resumeWorkflow(String workflowRunId) {

		if (cancelledWorkflowRuns.contains(workflowRunId)) {
			return false;
		}
		if (pausedLayerMap.containsKey(workflowRunId)) {
			Map<String, Set<Stop>> pausedLayerMapCopy = new HashMap<String, Set<Stop>>();
			pausedLayerMapCopy.putAll(pausedLayerMap);
			Set<Stop> stops = pausedLayerMapCopy.remove(workflowRunId);
			pausedLayerMap = pausedLayerMapCopy;
			for (Stop s : stops) {
				s.resumeLayerWorkflow(workflowRunId);
			}
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Resume the workflow run with the specified id on this Stop layer. This
	 * method processes any suspended job events.
	 * 
	 * @param workflowRunId
	 *            The id of the workflow run to resume.
	 */
	private void resumeLayerWorkflow(String workflowRunId) {
		synchronized (Stop.class) {
			for (DispatchJobEvent dje : suspendedJobEventMap
					.remove(workflowRunId)) {

				this.receiveJob(dje);
			}
		}
	}

}
