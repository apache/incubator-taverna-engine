package org.purl.wf4ever.provtaverna.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sf.taverna.t2.workbench.icons.WorkbenchIcons;
import net.sf.taverna.t2.workbench.ui.impl.Workbench;
import net.sf.taverna.t2.workbench.views.results.saveactions.SaveAllResultsSPI;

import org.purl.wf4ever.provtaverna.export.Saver;



public class SaveProvAction extends SaveAllResultsSPI {

	public SaveProvAction() {
		super();
		putValue(NAME, "Save provenance (PROV)");
		putValue(SMALL_ICON, WorkbenchIcons.saveAllIcon);
				
	}

	public AbstractAction getAction() {
		return this;
	}

	@Override
	public void setProvenanceEnabledForRun(boolean isProvenanceEnabledForRun) {
		super.setProvenanceEnabledForRun(isProvenanceEnabledForRun);
		setEnabled(isProvenanceEnabledForRun);
	}

	/**
	 * Saves the result data as a file structure
	 * 
	 * @throws IOException
	 */
	protected void saveData(File folder) throws IOException {
		String folderName = folder.getName();		
		if (folderName.endsWith(".")) {
			folder = new File(folder.getParentFile(), 
					folderName.substring(0, folderName.length()-1));
		} 
		
		
		Saver saver = new Saver(getReferenceService(), getContext(), getRunId(), getChosenReferences());
		
		File intermediatesDirectory = new File(folder, "intermediates");
		intermediatesDirectory.mkdir();
		saver.setIntermediatesDirectory(intermediatesDirectory);
		
		saver.saveData(folder);
		final String msg = "Saved provenance data to:\n" + folder;
		logger.info(msg);
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {				
				JOptionPane.showMessageDialog(Workbench.getInstance(), 
						msg);
			}
		});
	}

	@Override
	protected String getFilter() {
		return "";
	}

}
