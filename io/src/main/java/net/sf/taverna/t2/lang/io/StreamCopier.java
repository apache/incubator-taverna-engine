/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package net.sf.taverna.t2.lang.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copies an InputStream to an OutputStream.
 * 
 * @author Tom Oinn
 */
public class StreamCopier extends Thread {

	InputStream is;

	OutputStream os;

	/**
	 * Create a new StreamCopier which will, when started, copy the specified
	 * InputStream to the specified OutputStream
	 */
	public StreamCopier(InputStream is, OutputStream os) {
		super("StreamCopier");
		this.is = is;
		this.os = os;
	}

	/**
	 * Start copying the stream, exits when the InputStream runs out of data
	 */
	public void run() {
		try {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.flush();
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
