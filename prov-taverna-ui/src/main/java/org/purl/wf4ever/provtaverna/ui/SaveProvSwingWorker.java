package org.purl.wf4ever.provtaverna.ui;

import java.io.File;

import javax.swing.SwingWorker;

import org.purl.wf4ever.provtaverna.export.Saver;

public class SaveProvSwingWorker extends SwingWorker<String, String>{

	private Saver saver;
	private File bundle;

	public SaveProvSwingWorker(Saver saver, File bundle){
		this.saver = saver;
		this.bundle = bundle;
	}

	@Override
	protected String doInBackground() throws Exception {
		saver.saveData(bundle.toPath());
		return "done";
	}


}
