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
package uk.org.taverna.commons.download;

import java.io.File;
import java.net.URL;

/**
 * Download Manager for handling file download and checking the integrity of the download.
 *
 * @author David Withers
 */
public interface DownloadManager {

	/**
	 * Downloads a file from a URL.
	 * <p>
	 * The destination file will be created if it does not exist. If it does exist it will be
	 * overwritten.
	 *
	 * @param source
	 *            the file to download
	 * @param destination
	 *            the file to write to
	 * @throws DownloadException
	 *             if
	 *             <ul>
	 *             <li>the source does not exist</li> <li>the source cannot be downloaded</li> <li>
	 *             the destination is not a file</li> <li>the destination cannot be written to</li>
	 *             </ul>
	 */
	public void download(URL source, File destination) throws DownloadException;

	/**
	 * Downloads a file from a URL and checks the integrity of the download by downloading and
	 * verifying the a checksum using the specified algorithm.
	 * <p>
	 * Every implementation is required to support the following standard algorithms:
	 * <ul>
	 * <li>MD5</li>
	 * <li>SHA-1</li>
	 * <li>SHA-256</li>
	 * </ul>
	 * <p>
	 * The checksum source will be calculated by appending the algorithm name to the source. e.g.
	 * for an MD5 algorithm and a source of http://www.example.com/test.xml the checksum will be
	 * downloaded from http://www.example.com/test.xml.md5
	 *
	 * @param source
	 *            the file to download
	 * @param destination
	 *            the file to write to
	 * @param digestAlgorithm
	 *            the digest algorithm to use
	 * @throws DownloadException
	 *             if
	 *             <ul>
	 *             <li>the source does not exist</li> <li>the digest source does not exist</li> <li>
	 *             the source cannot be downloaded</li> <li>the destination cannot be written to
	 *             </li> <li>the destination is not a file</li> <li>the checksums do no match</li>
	 *             </ul>
	 */
	public void download(URL source, File destination, String digestAlgorithm)
			throws DownloadException;

	/**
	 * Downloads a file from a URL and checks the integrity of the download by downloading and
	 * verifying the a checksum using the specified algorithm.
	 * <p>
	 * Every implementation is required to support the following standard algorithms:
	 * <ul>
	 * <li>MD5</li>
	 * <li>SHA-1</li>
	 * <li>SHA-256</li>
	 * </ul>
	 * <p>
	 *
	 * @param source
	 *            the file to download
	 * @param destination
	 *            the file to write to
	 * @param digestAlgorithm
	 *            the digest algorithm to use
	 * @param digestSource
	 *            the digest file to check
	 * @throws DownloadException
	 *             if
	 *                <ul>
	 *                <li>the source does not exist</li> <li>the digest source does not exist</li>
	 *                <li> the source cannot be downloaded</li> <li>the destination cannot be
	 *                written to</li> <li>the destination is not a file</li> <li>the digestSource
	 *                does not exist</li> <li>the checksums do no match</li>
	 *                </ul>
	 */
	public void download(URL source, File destination, String digestAlgorithm, URL digestSource)
			throws DownloadException;

}
