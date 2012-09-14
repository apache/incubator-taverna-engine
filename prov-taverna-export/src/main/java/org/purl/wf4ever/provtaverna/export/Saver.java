package org.purl.wf4ever.provtaverna.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.lang.results.ResultsUtils;
import net.sf.taverna.t2.provenance.api.ProvenanceAccess;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.Identified;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workbench.reference.config.DataManagementConfiguration;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import eu.medsea.mimeutil.MimeType;

public class Saver {

	private static Logger logger = Logger.getLogger(Saver.class);
	

	/**
	 * @param saveProvAction
	 */
	public Saver(ReferenceService referenceService, InvocationContext context, String runId, Map<String, T2Reference> chosenReferences) {
		this.setReferenceService(referenceService);
		this.setContext(context);
		this.setRunId(runId);
		this.setChosenReferences(chosenReferences);
	}

	private Map<File, T2Reference> fileToId = new HashMap<File, T2Reference>();

	private Map<File, String> sha1sums = new HashMap<File, String>();
	private Map<File, String> sha512sums = new HashMap<File, String>();
	
	private ReferenceService referenceService;

	private InvocationContext context;

	private String runId;

	private Map<String, T2Reference> chosenReferences;

	private File intermediatesDirectory;

	
	public File getIntermediatesDirectory() {
		return intermediatesDirectory;
	}

	public void saveData(File folder) throws FileNotFoundException, IOException {
		String folderName = folder.getName();
		if (folderName.endsWith(".")) {
			folder = new File(folder.getParentFile(), folderName.substring(0,
					folderName.length()-1));
		}		
		saveToFolder(folder, getChosenReferences(), getReferenceService());
	}

	protected void saveToFolder(File folder, Map<String, T2Reference> chosenReferences, ReferenceService referenceService) throws IOException,
			FileNotFoundException {
		folder.mkdir();
		if (!folder.isDirectory()) {
			throw new IOException("Could not make/use folder: " + folder);
		}
	
		// First convert map of references to objects into a map of real result
		// objects
		for (String portName : chosenReferences.keySet()) {
			T2Reference ref = chosenReferences.get(portName);
			if (ref == null) {
				logger.warn("No reference for port " + portName + ", workflow unfinished?");
				continue;
			}
			writeToFileSystem(ref, folder, portName, referenceService);
		}
		
		
		
		String connectorType = DataManagementConfiguration.getInstance()
				.getConnectorType();
		ProvenanceAccess provenanceAccess = new ProvenanceAccess(connectorType,
				getContext());
		W3ProvenanceExport export = new W3ProvenanceExport(provenanceAccess,
				getRunId(), this);
		export.setFileToT2Reference(getFileToId());
		export.setBaseFolder(folder);
		export.setIntermediatesDirectory(getIntermediatesDirectory());
		File provenanceFile = new File(folder, "workflowrun.prov.ttl");
		BufferedOutputStream outStream = new BufferedOutputStream(
				new FileOutputStream(provenanceFile ));
		try {
			export.exportAsW3Prov(outStream, provenanceFile.getAbsoluteFile().toURI());
		} catch (Exception e) {
			logger.error("Failed to save the provenance graph to "
					+ provenanceFile, e);
//			JOptionPane.showMessageDialog(null,
//					"Failed to save the provenance graph to " + provenanceFile,
//					"Failed to save provenance graph",
//					JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
			}
		}
	}

	protected File writeDataObject(File destination, String name,
			T2Reference ref, String defaultExtension) throws IOException {
		Identified identified = getReferenceService().resolveIdentifier(ref, null,
				getContext());
	
		if (identified instanceof IdentifiedList) {
			// Create a new directory, iterate over the collection recursively
			// calling this method
			File targetDir = new File(destination.toString()
					+ File.separatorChar + name);
			targetDir.mkdir();
			getFileToId().put(targetDir, identified.getId());
			int count = 0;
			List<T2Reference> elements = getReferenceService().getListService()
					.getList(ref);
			for (T2Reference subRef : elements) {
				writeDataObject(targetDir, "" + count++, subRef,
						defaultExtension);
			}
			return targetDir;
		}
	
		else {
			String fileExtension = ".txt";
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
					if (externalReference.getDataNature().equals(
							ReferencedDataNature.TEXT)) {
						break;
					}
					mimeTypes.addAll(ResultsUtils.getMimeTypes(
							externalReference, getContext()));
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
				
				OutputStream output = new FileOutputStream(targetFile);
				MessageDigest sha = null;
				MessageDigest sha512 = null;
				try {
					sha = MessageDigest.getInstance("SHA");
					output = new DigestOutputStream(output, sha);

					sha512 = MessageDigest.getInstance("SHA-512");
					output = new DigestOutputStream(output, sha512);
				} catch (NoSuchAlgorithmException e) {	
					logger.info("Could not find digest", e);
				}
				
				IOUtils.copyLarge(
						externalReferences.get(0).openStream(getContext()),
						output);
				output.close();
				if (sha != null) {
					getSha1sums().put(targetFile.getAbsoluteFile(), 
							hexOfDigest(sha));
				}
				if (sha512 != null) {
					sha512.digest();					
					getSha512sums().put(targetFile.getAbsoluteFile(), 
							hexOfDigest(sha512));
				}
				getFileToId().put(targetFile, identified.getId());
				return targetFile;
			} else {
				File targetFile = new File(destination.toString()
						+ File.separatorChar + name + ".err");
				FileUtils.writeStringToFile(targetFile,
						((ErrorDocument) identified).getMessage());
				// We don't care about checksums for errors
				getFileToId().put(targetFile, identified.getId());
				return targetFile;
			}
	
		}
	}

	private String hexOfDigest(MessageDigest sha) {
		return new String(Hex.encodeHex(sha.digest()));
	}

	/**
	 * Write a specific object to the filesystem this has no access to metadata
	 * about the object and so is not particularly clever. A File object
	 * representing the file or directory that has been written is returned.
	 */
	protected File writeObjectToFileSystem(File destination, String name,
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

	public File writeToFileSystem(T2Reference ref, File destination, String name, ReferenceService referenceService)
			throws IOException {
		Identified identified = referenceService.resolveIdentifier(ref, null,
				getContext());
	
		String fileExtension = "";
		if (identified instanceof ReferenceSet) {
	
		} else if (identified instanceof ErrorDocument) {
			fileExtension = ".err";
		}
	
		File writtenFile = writeObjectToFileSystem(destination, name, ref,
				fileExtension);
		return writtenFile;
	}

	public ReferenceService getReferenceService() {
		return referenceService;
	}

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	public InvocationContext getContext() {
		return context;
	}

	public void setContext(InvocationContext context) {
		this.context = context;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public Map<String, T2Reference> getChosenReferences() {
		return chosenReferences;
	}

	public void setChosenReferences(Map<String, T2Reference> chosenReferences) {
		this.chosenReferences = chosenReferences;
	}

	public Map<File, T2Reference> getFileToId() {
		return fileToId;
	}

	public void setFileToId(Map<File, T2Reference> fileToId) {
		this.fileToId = fileToId;
	}

	public void setIntermediatesDirectory(File intermediatesDirectory) {
		this.intermediatesDirectory = intermediatesDirectory;
	}

	public Map<File, String> getSha1sums() {
		return sha1sums;
	}

	public Map<File, String> getSha512sums() {
		return sha512sums;
	}

}