package org.purl.wf4ever.provtaverna.export;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.databundle.DataBundles;

import eu.medsea.mimeutil.MimeType;

public class Saver {

	private static final Charset UTF8 = Charset.forName("UTF-8");

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

	
	private Map<Path, T2Reference> fileToId = new HashMap<>();

	private Map<Path, String> sha1sums = new HashMap<>();
	private Map<Path, String> sha512sums = new HashMap<>();
	
	private ReferenceService referenceService;

	private InvocationContext context;

	private String runId;

	private Map<String, T2Reference> chosenReferences;

    private Bundle bundle;
	
	/**
     * @return the bundle
     */
    public Bundle getBundle() {
        return bundle;
    }

	public void saveData(Path bundlePath) throws FileNotFoundException, IOException {
	    Bundle bundle = DataBundles.createBundle();
//		String folderName = bundlePath.getFileName().toString();
//		if (folderName.endsWith(".")) {
//            bundlePath = bundlePath.resolveSibling(folderName.substring(0,
//                    folderName.length() - 1));
//		}
	    setBundle(bundle);
		saveToFolder(bundle.getRoot(), getChosenReferences(), getReferenceService());
		DataBundles.closeAndSaveBundle(bundle, bundlePath);
	}

	private void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    protected void saveToFolder(Path folder, Map<String, T2Reference> chosenReferences, ReferenceService referenceService) throws IOException,
			FileNotFoundException {
		logger.info("Saving provenance and outputs to " + folder.toRealPath());
		Files.createDirectories(folder);
	
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
		logger.info("Saved outputs to " + folder);
		
		
		String connectorType = DataManagementConfiguration.getInstance()
				.getConnectorType();
		ProvenanceAccess provenanceAccess = new ProvenanceAccess(connectorType,
				getContext());
		W3ProvenanceExport export = new W3ProvenanceExport(provenanceAccess,
				getRunId(), this);
		export.setFileToT2Reference(getFileToId());
		export.setBundle(bundle);
		
		Path provFile = DataBundles.getWorkflowRunProvenance(bundle);
		// FIXME: Refactor out and use SafeFileOutputStream
		BufferedOutputStream outStream = new BufferedOutputStream(
				new SafeFileOutputStream(provFile));
		try {
			logger.debug("Saving provenance to " + provFile);
			export.exportAsW3Prov(outStream, provFile);
			logger.info("Saved provenance to " + provFile);
		} catch (Exception e) {
			logger.error("Failed to save the provenance graph to "
					+ provFile, e);
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				logger.error("Potential error on saving " + provFile);
			}
 		}
	}

	protected Path writeDataObject(Path destination, String name,
			T2Reference ref, String defaultExtension) throws IOException {
		Identified identified = getReferenceService().resolveIdentifier(ref, null,
				getContext());
	
		if (identified instanceof IdentifiedList) {
			// Create a new directory, iterate over the collection recursively
			// calling this method
		    Path targetDir = destination.resolve(name);
		    Files.createDirectories(targetDir);
			getFileToId().put(targetDir, identified.getId());
			int count = 0;
			List<T2Reference> elements = getReferenceService().getListService()
					.getList(ref);
			for (T2Reference subRef : elements) {
				writeDataObject(targetDir, "" + count++, subRef,
						defaultExtension);
			}
			logger.debug("Saved list " + targetDir + " from " + identified.getId().toUri());
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
				
				Path targetFile = destination.resolve(name + fileExtension);
				
				OutputStream output = Files.newOutputStream(targetFile);
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
				
				// TODO: Set external references as URIs
				IOUtils.copyLarge(
						externalReferences.get(0).openStream(getContext()),
						output);
				output.close();
				
				if (sha != null) {
					getSha1sums().put(targetFile.toRealPath(),
							hexOfDigest(sha));
				}
				if (sha512 != null) {
					sha512.digest();					
					getSha512sums().put(targetFile.toRealPath(), 
							hexOfDigest(sha512));
				}
				getFileToId().put(targetFile, identified.getId());
				logger.debug("Saved value " + targetFile + " from " + identified.getId().toUri());
				return targetFile;
			} else {
			    Path targetFile = destination.resolve(name + ".err");
				String message = ((ErrorDocument) identified).getMessage();
                Files.write(targetFile, Collections.singletonList(message), UTF8);
				// We don't care about checksums for errors
				getFileToId().put(targetFile, identified.getId());
				logger.debug("Saved error " + targetFile + " from " + identified.getId().toUri());
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
	protected Path writeObjectToFileSystem(Path destination, String name,
			T2Reference ref, String defaultExtension) throws IOException {
		// If the destination is not a directory then set the destination
		// directory to the parent and the name to the filename
		// i.e. if the destination is /tmp/foo.text and this exists
		// then set destination to /tmp/ and name to 'foo.text'
		if (Files.exists(destination) && Files.isRegularFile(destination)) {
			name = destination.getFileName().toString();
			destination = destination.getParent();
		}
		Files.createDirectories(destination);
		Path writtenFile = writeDataObject(destination, name, ref,
				defaultExtension);
		return writtenFile;
	}

	public Path writeToFileSystem(T2Reference ref, Path destination, String name, ReferenceService referenceService)
			throws IOException {
		Identified identified = referenceService.resolveIdentifier(ref, null,
				getContext());
	
		String fileExtension = "";
		if (identified instanceof ReferenceSet) {
	
		} else if (identified instanceof ErrorDocument) {
			fileExtension = ".err";
		}
	
		Path writtenFile = writeObjectToFileSystem(destination, name, ref,
				fileExtension);
		logger.debug("Saved " + writtenFile + " from reference " + ref.toUri());
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

	public Map<Path, T2Reference> getFileToId() {
		return fileToId;
	}

	public void setFileToId(Map<Path, T2Reference> fileToId) {
		this.fileToId = fileToId;
	}

	public Map<Path, String> getSha1sums() {
		return sha1sums;
	}

	public Map<Path, String> getSha512sums() {
		return sha512sums;
	}

}