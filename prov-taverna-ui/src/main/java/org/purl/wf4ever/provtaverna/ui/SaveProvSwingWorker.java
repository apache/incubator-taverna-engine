package org.purl.wf4ever.provtaverna.ui;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import net.sf.taverna.t2.workbench.ui.impl.Workbench;

import org.apache.log4j.Logger;
import org.purl.wf4ever.provtaverna.export.Saver;

public class SaveProvSwingWorker extends SwingWorker<Boolean, String>{

	private static Logger logger = Logger.getLogger(SaveProvSwingWorker.class);
	private Saver saver;
	private File bundle;

	public SaveProvSwingWorker(Saver saver, File bundle){
		this.saver = saver;
		this.bundle = bundle;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		saver.saveData(bundle.toPath());
		return true;
	}
	
	@Override
	protected void done() {
		try {
			if (get()) {		
				String msg = "Saved provenance data to:\n" + bundle;		
				logger.info(msg);
				JOptionPane.showMessageDialog(Workbench.getInstance(), 
						msg);
			}
		} catch (CancellationException | InterruptedException  e) {
			logger.warn("Cancelled provenance saving to " + bundle);
		} catch (ExecutionException e) {
			String msg = "Could not save provenance to " + bundle + ": " + e.getCause().getMessage();
			logger.error(msg, e.getCause());
				JOptionPane.showMessageDialog(Workbench.getInstance(), 
						msg);
		}		
	}

}
