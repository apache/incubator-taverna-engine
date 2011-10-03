/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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
package uk.org.taverna.platform.execution.impl.hadoop;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputSplit;

/**
 *
 *
 * @author David Withers
 */
public class TavernaInputSplit extends InputSplit {
	private final Path[] files;
	private long length;
	private String[] hosts;
	private final String[] ports;

	public TavernaInputSplit(String[] ports, Path[] inputFiles, long length, String[] hosts) {
		this.ports = ports;
		this.files = inputFiles;
		this.length = length;
		this.hosts = hosts;
	}

	public String[] getPorts() {
		return ports;
	}

	public Path[] getFiles() {
		return files;
	}

	@Override
	public long getLength() throws IOException, InterruptedException {
		return length;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		if (hosts == null) {
			return new String[] {};
		} else {
			return this.hosts;
		}
	}

}
