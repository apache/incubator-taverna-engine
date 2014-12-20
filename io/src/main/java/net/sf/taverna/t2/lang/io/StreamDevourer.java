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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * Devours an input stream and allows the contents to be read as a String once
 * the stream has completed.
 * 
 * @author Tom Oinn
 * @author Alan R Williams
 */
public class StreamDevourer extends Thread {
	
	private static Logger logger = Logger.getLogger(StreamDevourer.class);

	private static byte[] newLine = System.getProperty("line.separator").getBytes();

	BufferedReader br;

	ByteArrayOutputStream output;

	/**
	 * Returns the current value of the internal ByteArrayOutputStream
	 */
	@Override
	public String toString() {
		return output.toString();
	}

	/**
	 * Waits for the stream to close then returns the String representation of
	 * its contents (this is equivalent to doing a join then calling toString)
	 */
	public String blockOnOutput() {
		try {
			this.join();
			return output.toString();
		} catch (InterruptedException ie) {
			logger.error("Interrupted", ie);
			interrupt();
			return "";
		}
	}

	/**
	 * Create the StreamDevourer and point it at an InputStream to consume
	 */
	public StreamDevourer(InputStream is) {
		super("StreamDevourer");
		this.br = new BufferedReader(new InputStreamReader(is));
		this.output = new ByteArrayOutputStream();
	}

	/**
	 * When started this Thread will copy all data from the InputStream into a
	 * ByteArrayOutputStream via a BufferedReader. Because of the use of the
	 * BufferedReader this is only really appropriate for streams of textual
	 * data
	 */
	@Override
	public void run() {
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				// && line.endsWith("</svg>") == false) {
				if (line.endsWith("\\") && !line.endsWith("\\\\")) {
					line = line.substring(0, line.length() - 1);
					output.write(line.getBytes());
				} else {
					output.write(line.getBytes());
					output.write(newLine);
				}
			}
			br.close();
		} catch (IOException ioe) {
			logger.error(ioe);
		}
	}

}
