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

/**
 * Copied out from net.sf.taverna.t2.workbench.file.impl.SafeFileOutputStream
 * to avoid dependency on file-impl.  
 * 
 * TODO: Move to taverna utils!
 * 
 */

import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SafeFileOutputStream extends FilterOutputStream {

    private final Path desiredFile;
    private Path tempFile;
    boolean desiredAlreadyExisted;

    public SafeFileOutputStream(Path file) throws IOException {
        this(file, tempFile(file));
    }

    public SafeFileOutputStream(Path desiredFile, Path tempFile)
            throws IOException {

        super(Files.newOutputStream(tempFile));
        this.desiredFile = desiredFile;
        this.tempFile = tempFile;
        // Some useful things to check that we preferably don't want to fail on
        // close()
        desiredAlreadyExisted = Files.exists(desiredFile);
        Path desiredFolder = this.desiredFile.getParent();
        if (desiredAlreadyExisted) {
            if (!Files.isWritable(desiredFile)) {
                throw new FileNotFoundException("Can't write to " + desiredFile);
            }
        } else {
            if (!Files.exists(desiredFolder)) {
                throw new FileNotFoundException("Folder does not exist: "
                        + desiredFolder);
            }
            if (!Files.isDirectory(desiredFolder)) {
                throw new FileNotFoundException("Not a directory: "
                        + desiredFolder);
            }
        }
        if (!Files.isWritable(desiredFolder)) {
            throw new FileNotFoundException("Can't modify folder "
                    + desiredFolder);
        }
    }

    private static Path tempFile(Path file) throws IOException {
        return Files.createTempFile(file.getParent(), file.getFileName()
                .toString(), ".tmp");
    }

    @Override
    public void close() throws IOException {
        // If super.close fails - we leave the tempfiles behind
        super.close();
        if (!Files.exists(tempFile)) {
            // Probably something went wrong before close called,
            // like rollback()
            return;
        }
        Path beforeDeletion = null;
        try {
            if (desiredAlreadyExisted) {
                // In case renaming goes wrong, we don't want to have already
                // deleted the
                // desired file. So we'll rename it to another temporary file
                // instead which
                // we can delete on successful rename.
                beforeDeletion = tempFile(desiredFile);
                if (!Files.deleteIfExists(beforeDeletion)) {
                    // Should not happen, we just made it!
                    throw new IOException("Can't delete temporary file "
                            + beforeDeletion);
                }
                Files.move(desiredFile, beforeDeletion,
                        StandardCopyOption.ATOMIC_MOVE);
            }
            try {
                Files.move(tempFile, desiredFile,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                // We'll leave our tempFiles for recovery.
                tempFile = null;
                beforeDeletion = null;

                if (Files.exists(desiredFile)) {
                    // Someone else added or replaced the file afterwards,
                    // kind-a OK
                    return;
                }
                throw e;
            }
        } finally {
            if (beforeDeletion != null) {
                Files.deleteIfExists(beforeDeletion);
            }
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
            }
        }
    }

    public void rollback() throws IOException {
        super.close();
        Files.deleteIfExists(tempFile);
    }

}
