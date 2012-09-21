package org.purl.wf4ever.provtaverna.export;

/**
 * Copied out from net.sf.taverna.t2.workbench.file.impl.SafeFileOutputStream
 * to avoid dependency on file-impl.  
 * 
 * TODO: Move to taverna utils!
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SafeFileOutputStream extends FileOutputStream {

	private final File desiredFile;
	private File tempFile;
	boolean desiredAlreadyExisted;

	public SafeFileOutputStream(File file) throws IOException {
		this(file, tempFile(file));
	}

	public SafeFileOutputStream(File desiredFile, File tempFile)
			throws FileNotFoundException {
		super(tempFile);
		this.desiredFile = desiredFile;
		this.tempFile = tempFile;
		// Some useful things to check that we preferably don't want to fail on
		// close()
		desiredAlreadyExisted = desiredFile.exists();
		File desiredFolder = this.desiredFile.getParentFile();
		if (desiredAlreadyExisted) {
			if (!desiredFile.canWrite()) {
				throw new FileNotFoundException("Can't write to " + desiredFile);
			}
		} else {
			if (!desiredFolder.exists()) {
				throw new FileNotFoundException("Folder does not exist: "
						+ desiredFolder);
			}
			if (!desiredFolder.isDirectory()) {
				throw new FileNotFoundException("Not a directory: " + desiredFolder);
			}
		}
		if (!desiredFolder.canWrite()) {
			throw new FileNotFoundException("Can't modify folder " + desiredFolder);
		}
	}

	private static File tempFile(File file) throws IOException {
		return File
				.createTempFile(file.getName(), ".tmp", file.getParentFile());
	}
	
	@Override
	public void close() throws IOException {
		// If super.close fails - we leave the tempfiles behind	
		super.close();
		if (!tempFile.exists()) {
			// Probably something went wrong before close called,
			// like rollback()
			return;
		}
		File beforeDeletion = null;
		try {
			if (desiredAlreadyExisted) {
				// In case renaming goes wrong, we don't want to have already
				// deleted the
				// desired file. So we'll rename it to another temporary file
				// instead which
				// we can delete on successful rename.
				beforeDeletion = tempFile(desiredFile);
				if (!beforeDeletion.delete()) {
					// Should not happen, we just made it!
					throw new IOException("Can't delete temporary file "
							+ beforeDeletion);
				}
				if (!desiredFile.renameTo(beforeDeletion)) {
					if (!desiredFile.isFile()) {
						// File must have been deleted in transit,
						// so normal FileOutputStream behaviour on .close()
						// would not be
						// to re-instate the file - we'll simply delete both our
						// temporary files
						return;
					}
				}
			}
			if (!tempFile.renameTo(desiredFile)) {
				String msg = "Can't rename temporary " + tempFile
					+ " to " + desiredFile;
				// We'll leave our tempFiles for recovery.
				tempFile = null;
				beforeDeletion = null;
				
				if (desiredFile.exists()) {
					// Someone else added or replaced the file afterwards, kind-a OK
					return;
				}
				// Something else went wrong, like permission problems
				throw new IOException(msg);
			}
		} finally {
			if (beforeDeletion != null) {
				beforeDeletion.delete();
			}
			if (tempFile != null) {
				tempFile.delete();
			}
		}
	}

	public void rollback() throws IOException {
		super.close();
		tempFile.delete();
	}

}
