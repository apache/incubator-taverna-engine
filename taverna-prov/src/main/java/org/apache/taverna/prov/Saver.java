/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.taverna.prov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.provenance.api.ProvenanceAccess;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.ReferenceSetService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.ValueCarryingExternalReference;
import org.apache.taverna.spi.SPIRegistry;
import org.apache.taverna.reference.config.DataManagementConfiguration;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;

public class Saver {

    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    private static Logger logger = Logger.getLogger(Saver.class);

    /**
     * @param saveProvAction
     */
    public Saver(ReferenceService referenceService, InvocationContext context,
            String runId, Map<String, T2Reference> chosenReferences) {
        this.setReferenceService(referenceService);
        this.setContext(context);
        this.setRunId(runId);
        this.setChosenReferences(chosenReferences);
        prepareSesame();
    }

    /**
     * Load
     */
    protected void prepareSesame() {
        RDFParserRegistry parserReg = RDFParserRegistry.getInstance();
        SPIRegistry<RDFParserFactory> parserSPI = new SPIRegistry<>(
                RDFParserFactory.class);
        for (RDFParserFactory service : parserSPI.getInstances()) {
            parserReg.add(service);
        }

        RDFWriterRegistry writerReg = RDFWriterRegistry.getInstance();
        SPIRegistry<RDFWriterFactory> writerSPI = new SPIRegistry<>(
                RDFWriterFactory.class);
        for (RDFWriterFactory service : writerSPI.getInstances()) {
            writerReg.add(service);
        }

    }

    private Map<Path, T2Reference> fileToId = new HashMap<>();

    private Map<Path, String> sha1sums = new HashMap<>();
    private Map<Path, String> sha512sums = new HashMap<>();

    private ReferenceService referenceService;

    private InvocationContext context;

    private String runId;

    private Map<String, T2Reference> chosenReferences;

    private Bundle bundle;

    private Map<T2Reference, String> mediaTypes = new HashMap<>();

    /**
     * @return the bundle
     */
    public Bundle getBundle() {
        return bundle;
    }

    public void saveData(Path bundlePath) throws FileNotFoundException,
            IOException {
        Bundle bundle = DataBundles.createBundle();
        // String folderName = bundlePath.getFileName().toString();
        // if (folderName.endsWith(".")) {
        // bundlePath = bundlePath.resolveSibling(folderName.substring(0,
        // folderName.length() - 1));
        // }
        setBundle(bundle);
        saveToFolder(bundle.getRoot(), getChosenReferences(),
                getReferenceService());
        DataBundles.closeAndSaveBundle(bundle, bundlePath);
    }

    private void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    protected static Tika tika = new Tika();

    public Path saveReference(T2Reference t2Ref, Path file) throws IOException {
        ReferenceSetService refSet = getReferenceService()
                .getReferenceSetService();
        ReferenceSet referenceSet = refSet.getReferenceSet(t2Ref);
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
        String mimeType = findMimeType(externalReferences);
        getMediaTypes().put(t2Ref, mimeType);

        Path targetFile = writeIfLocal(externalReferences, file, mimeType);
        if (targetFile == null) {
            URI uri = referenceAsURI(externalReferences);
            if (uri != null) {
                targetFile = DataBundles.setReference(file, uri);
            }
        }

        if (targetFile != null) {
            getFileToId().put(targetFile, t2Ref);
        } else {
            logger.warn("Could not write out reference " + t2Ref);
        }

        return targetFile;

    }

    private Path writeIfLocal(List<ExternalReferenceSPI> externalReferences,
            Path file, String mimeType) throws IOException {

        ValueCarryingExternalReference<?> valRef = null;
        for (ExternalReferenceSPI ref : externalReferences) {
            if (ref instanceof ValueCarryingExternalReference) {
                valRef = (ValueCarryingExternalReference<?>) ref;
                break;
            }
        }

        if (valRef == null) {
            return null;
        }

        String fileExtension;
        try {
            fileExtension = MimeTypes.getDefaultMimeTypes().forName(mimeType)
                    .getExtension();
        } catch (MimeTypeException e1) {
            fileExtension = "";
        }
        Path targetFile = file.resolveSibling(file.getFileName()
                + fileExtension);

        MessageDigest sha = null;
        MessageDigest sha512 = null;
        OutputStream output = Files.newOutputStream(targetFile);
        try {
            try {
                sha = MessageDigest.getInstance("SHA");
                output = new DigestOutputStream(output, sha);

                sha512 = MessageDigest.getInstance("SHA-512");
                output = new DigestOutputStream(output, sha512);
            } catch (NoSuchAlgorithmException e) {
                logger.info("Could not find digest", e);
            }

            IOUtils.copyLarge(valRef.openStream(getContext()), output);
        } finally {
            output.close();
        }

        if (sha != null) {
            getSha1sums().put(targetFile.toRealPath(), hexOfDigest(sha));
        }
        if (sha512 != null) {
            sha512.digest();
            getSha512sums().put(targetFile.toRealPath(), hexOfDigest(sha512));
        }

        return targetFile;
    }

    private URI referenceAsURI(List<ExternalReferenceSPI> externalReferences) {
        for (ExternalReferenceSPI ref : externalReferences) {
            String className = ref.getClass().getName();
            if (className
                    .equals("net.sf.taverna.t2.reference.impl.external.http.HttpReference")) {
                URL url = (URL) getProperty(ref, "httpUrl");
                try {
                    return url.toURI();
                } catch (URISyntaxException e) {
                    logger.warn("Can't convert HttpReference to URI: " + url, e);
                    continue;
                }
            } else if (className
                    .equals("net.sf.taverna.t2.reference.impl.external.file.FileReference")) {
                File file = (File) getProperty(ref, "file");
                return file.toURI();
            } else if (className
                    .equals("de.uni_luebeck.inb.knowarc.usecases.invocation.ssh.SshReference")) {
                String host = (String) getProperty(ref, "host");
                int port = (int) getProperty(ref, "port");
                String path = (String) getProperty(ref, "fullPath");
                try {
                    return new URI("sftp", null, host, port, path, null, null);
                } catch (URISyntaxException e) {
                    logger.warn("Can't convert SshReference to URI: sftp://"
                            + host + ":" + port + path, e);
                    continue;
                }
            }
        }
        return null;
    }

    protected Object getProperty(ExternalReferenceSPI ref, String propertyName) {
        try {
            return PropertyUtils.getSimpleProperty(ref, propertyName);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Can't look up " + propertyName
                    + " in bean " + ref, ex);
        }
    }

    private String findMimeType(List<ExternalReferenceSPI> externalReferences)
            throws IOException, ProtocolException {
        String mimeType = null;
        for (ExternalReferenceSPI externalReference : externalReferences) {
            String className = externalReference.getClass().getName();
            if (className
                    .equals("net.sf.taverna.t2.reference.impl.external.http.HttpReference")) {
                URL url = (URL) getProperty(externalReference, "httpUrl");
                mimeType = tika.detect(url);
            } else if (className
                    .equals("net.sf.taverna.t2.reference.impl.external.file.FileReference")) {
                File file = (File) getProperty(externalReference, "file");
                mimeType = tika.detect(file);
            } else if (className
                    .equals("de.uni_luebeck.inb.knowarc.usecases.invocation.ssh.SshReference")) {
                String filename = (String) getProperty(externalReference,
                        "fileName");
                try (InputStream instream = externalReference
                        .openStream(context)) {
                    mimeType = tika.detect(instream, filename);
                }
            } else if (className
                    .equals("net.sf.taverna.t2.reference.impl.external.object.VMObjectReference")) {
                mimeType = "application/x-java-serialized-object";
            } else {
                try (InputStream instream = externalReference
                        .openStream(context)) {
                    mimeType = tika.detect(instream);
                }
            }
            if (mimeType != null && !mimeType.equals(APPLICATION_OCTET_STREAM)) {
                break;
            }
        }
        if (mimeType == null || mimeType.isEmpty()) {
            return APPLICATION_OCTET_STREAM;
        }
        return mimeType;
    }

    protected void saveToFolder(Path folder,
            Map<String, T2Reference> chosenReferences,
            ReferenceService referenceService) throws IOException,
            FileNotFoundException {
        logger.info("Saving provenance and outputs to " + folder.toRealPath());
        Files.createDirectories(folder);
        String connectorType = DataManagementConfiguration.getInstance()
                .getConnectorType();
        ProvenanceAccess provenanceAccess = new ProvenanceAccess(connectorType,
                getContext());
        W3ProvenanceExport export = new W3ProvenanceExport(provenanceAccess,
                getRunId(), this);
        export.setFileToT2Reference(getFileToId());
        export.setBundle(bundle);

        try {
            logger.debug("Saving provenance");
            export.exportAsW3Prov();
            logger.info("Saved provenance");
        } catch (Exception e) {
            logger.error("Failed to save the provenance graph", e);
        }
    }

    private String hexOfDigest(MessageDigest sha) {
        return new String(Hex.encodeHex(sha.digest()));
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

    public Map<T2Reference, String> getMediaTypes() {
        return mediaTypes;
    }

    public Map<Path, String> getSha512sums() {
        return sha512sums;
    }

    public void setMediaTypes(Map<T2Reference, String> mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

}
