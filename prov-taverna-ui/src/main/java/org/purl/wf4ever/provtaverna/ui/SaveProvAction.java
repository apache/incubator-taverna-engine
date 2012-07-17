package org.purl.wf4ever.provtaverna.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;

import org.purl.wf4ever.provtaverna.export.Saver;

import net.sf.taverna.t2.workbench.icons.WorkbenchIcons;
import net.sf.taverna.t2.workbench.views.results.saveactions.SaveAllResultsSPI;



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
		Saver saver = new Saver(getReferenceService(), getContext(), getRunId(), getChosenReferences());
		saver.saveData(folder);
	}

	@Override
	protected String getFilter() {
		return "";
	}

}
