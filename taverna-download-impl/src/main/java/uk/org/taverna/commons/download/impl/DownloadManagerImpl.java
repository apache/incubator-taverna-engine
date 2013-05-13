/*******************************************************************************
 * Copyright (C) 2013 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package uk.org.taverna.commons.download.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import uk.org.taverna.commons.download.DownloadException;
import uk.org.taverna.commons.download.DownloadManager;

/**
 *
 *
 * @author David Withers
 */
public class DownloadManagerImpl implements DownloadManager {

	private static final Logger logger = Logger.getLogger(DownloadManagerImpl.class);

	public void download(URL source, File destination) throws DownloadException {
		download(source, destination, null);
	}

	public void download(URL source, File destination, String digestAlgorithm) throws DownloadException {
		// TODO Use MessageDigest when Java 7 available
		if (digestAlgorithm != null && !digestAlgorithm.equals("MD5")) {
			throw new IllegalArgumentException("Only MD5 supported");
		}
		URL digestSource = null;
		if (digestAlgorithm != null) {
			try {
				digestSource = new URL(source.toString() + mapAlgorithmToFileExtension(digestAlgorithm));
			} catch (MalformedURLException e) {
				throw new DownloadException("Error creating digest URL", e);
			}
		}
		download(source, destination, digestAlgorithm, digestSource);
	}

	public void download(URL source, File destination, String digestAlgorithm, URL digestSource)
			throws DownloadException {
		// TODO Use MessageDigest when Java 7 available
		if (digestAlgorithm != null && !digestAlgorithm.equals("MD5")) {
			throw new IllegalArgumentException("Only MD5 supported");
		}
		// download the file
		File tempFile;
		try {
			tempFile = File.createTempFile("DownloadManager", "tmp");
			tempFile.deleteOnExit();
			logger.info(String.format("Downloading %1$s to %2$s", source, tempFile));
			FileUtils.copyURLToFile(source, tempFile, 30, 30);
		} catch (IOException e) {
			throw new DownloadException(String.format("Error downloading %1$s to %2$s.", source, destination), e);
		}
		if (digestSource != null) {
			// download the digest file
			File digestFile;
			try {
				digestFile = File.createTempFile("DownloadManager", "tmp");
				digestFile.deleteOnExit();
				logger.info(String.format("Downloading %1$s to %2$s", digestSource, digestFile));
				FileUtils.copyURLToFile(digestSource, digestFile, 30, 30);
			} catch (IOException e) {
				throw new DownloadException(String.format("Error checking digest for %1$s.", source), e);
			}
			// check the digest matches
			try {
				String digestString1 = DigestUtils.md5Hex(new FileInputStream(tempFile));
				String digestString2 = FileUtils.readFileToString(digestFile);
				if (!digestString1.equals(digestString2)) {
					throw new DownloadException(String.format(
							"Error downloading file: digsests not equal. (%1$s != %2$s)",
							digestString1, digestString2));
				}
			} catch (IOException e) {
				throw new DownloadException(String.format("Error checking digest for %1$s", destination),
						e);
			}
		}
		// copy file to destination
		try {
			logger.info(String.format("Copying %1$s to %2$s", tempFile, destination));
			FileUtils.copyFile(tempFile, destination);
		} catch (IOException e) {
			throw new DownloadException(String.format("Error downloading %1$s to %2$s.", source, destination), e);
		}

	}

	private String mapAlgorithmToFileExtension(String algorithm) {
		return "." + algorithm.toLowerCase().replaceAll("-", "");
	}

}
