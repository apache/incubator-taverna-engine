/**
 * 
 */
package net.sf.taverna.t2.workflowmodel;

/**
 * A RunDeletionListener is notified when a run is deleted. It is then able to
 * take any specific action needed to deal with the deletion of the run, for
 * example deleting data that is not held within Taverna.
 * 
 * @author alanrw
 */
public interface RunDeletionListener {
	void deleteRun(String runToDelete);
}
