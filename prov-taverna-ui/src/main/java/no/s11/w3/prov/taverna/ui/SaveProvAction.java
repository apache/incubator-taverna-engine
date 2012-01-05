package no.s11.w3.prov.taverna.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.taverna.t2.lang.results.ResultsUtils;
import net.sf.taverna.t2.provenance.api.ProvenanceAccess;
import net.sf.taverna.t2.provenance.client.ProvenanceExporter;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.Identified;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workbench.icons.WorkbenchIcons;
import net.sf.taverna.t2.workbench.reference.config.DataManagementConfiguration;
import net.sf.taverna.t2.workbench.views.results.saveactions.SaveAllResultsSPI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import eu.medsea.mimeutil.MimeType;

public class SaveProvAction extends SaveAllResultsSPI {

	public SaveProvAction(){
		super();
		putValue(NAME, "Save provenance");
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
	 * @throws IOException 
	 */
	protected void saveData(File file) throws IOException {
		// First convert map of references to objects into a map of real result objects
//		for (String portName : chosenReferences.keySet()) {
//			writeToFileSystem(chosenReferences.get(portName), file, portName);
//		}
//		
		String connectorType = DataManagementConfiguration.getInstance()
				.getConnectorType();
		ProvenanceAccess provenanceAccess = new ProvenanceAccess(connectorType, getContext());
		W3ProvenanceExport export = new W3ProvenanceExport(provenanceAccess, getRunId());
		BufferedOutputStream outStream = new BufferedOutputStream(
				new FileOutputStream(file));		
		try {
			export.exportAsW3Prov(outStream);
		} catch (Exception e) {
			logger.error("Failed to save the provenance graph to " + file, e);
			JOptionPane.showMessageDialog(null,
					"Failed to save the provenance graph to " + file,
					"Failed to save provenance graph", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
			}
		}
		
	}
	
	public File writeToFileSystem(T2Reference ref, File destination, String name)
			throws IOException {
		Identified identified = referenceService.resolveIdentifier(ref, null,
				context);

		String fileExtension = "";
		if (identified instanceof ReferenceSet) {

		} else if (identified instanceof ErrorDocument) {
			fileExtension = ".err";
		}
		
		File writtenFile = writeObjectToFileSystem(destination, name,
				ref, fileExtension);
		return writtenFile;
	}

	/**
	 * Write a specific object to the filesystem this has no access to metadata
	 * about the object and so is not particularly clever. A File object
	 * representing the file or directory that has been written is returned.
	 */
	public File writeObjectToFileSystem(File destination, String name,
			T2Reference ref, String defaultExtension) throws IOException {
		// If the destination is not a directory then set the destination
		// directory to the parent and the name to the filename
		// i.e. if the destination is /tmp/foo.text and this exists
		// then set destination to /tmp/ and name to 'foo.text'
		if (destination.exists() && destination.isFile()) {
			name = destination.getName();
			destination = destination.getParentFile();
		}
		if (destination.exists() == false) {
			// Create the directory structure if not already present
			destination.mkdirs();
		}
		File writtenFile = writeDataObject(destination, name, ref,
				defaultExtension);
		return writtenFile;
	}

	private File writeDataObject(File destination, String name,
			T2Reference ref, String defaultExtension) throws IOException {
		Identified identified = referenceService.resolveIdentifier(ref, null,
				context);

		if (identified instanceof IdentifiedList) {
			// Create a new directory, iterate over the collection recursively
			// calling this method
			File targetDir = new File(destination.toString() + File.separatorChar + name);
			targetDir.mkdir();
			int count = 0;
			List<T2Reference> elements = referenceService.getListService().getList(ref);
			for (T2Reference subRef : elements) {
				writeDataObject(targetDir, "" + count++, subRef,
						defaultExtension);
			}
			return targetDir;
		}

		else {
			String fileExtension = ".text";
			if (identified instanceof ReferenceSet) {
				List<MimeType> mimeTypes = new ArrayList<MimeType>();
				ReferenceSet referenceSet = (ReferenceSet) identified;
				List<ExternalReferenceSPI> externalReferences = new ArrayList<ExternalReferenceSPI>(
						referenceSet.getExternalReferences());
				Collections.sort(externalReferences,
						new Comparator<ExternalReferenceSPI>() {
							public int compare(ExternalReferenceSPI o1,
									ExternalReferenceSPI o2) {
								return (int) (o1.getResolutionCost() - o2
										.getResolutionCost());
							}
						});
				for (ExternalReferenceSPI externalReference : externalReferences) {
					if (externalReference.getDataNature().equals(ReferencedDataNature.TEXT)) {
						break;
					}
					mimeTypes.addAll(ResultsUtils.getMimeTypes(
							externalReference, context));
				}
				if (!mimeTypes.isEmpty()) {

					// Check for the most interesting type, if defined
					String interestingType = mimeTypes.get(0).toString();

					if (interestingType != null
							&& interestingType.equals("text/plain") == false) {
						// MIME types look like 'foo/bar'
						String lastPart = interestingType.split("/")[1];
						if (lastPart.startsWith("x-") == false) {
							fileExtension = "." + lastPart;
						}
					}
				}
				File targetFile = new File(destination.toString()
						+ File.separatorChar + name + fileExtension);
				IOUtils.copyLarge(externalReferences.get(0)
						.openStream(context), new FileOutputStream(targetFile));
				return targetFile;
			} else {
				File targetFile = new File(destination.toString()
						+ File.separatorChar + name + ".err");
				FileUtils.writeStringToFile(targetFile, ((ErrorDocument) identified).getMessage());
				return targetFile;
			}

		}
	}

	@Override
	protected String getFilter() {
		return "prov.rdf";
	}
	
}
