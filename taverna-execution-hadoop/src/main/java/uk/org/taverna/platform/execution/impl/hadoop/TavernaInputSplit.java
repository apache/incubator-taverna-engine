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
import java.util.Map;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputSplit;

/**
 *
 *
 * @author David Withers
 */
public class TavernaInputSplit extends InputSplit {
	private int[] index;
	private Map<String, Path> inputs;
	private long length;
	private String[] hosts;

	public TavernaInputSplit(int[] index, Map<String, Path> inputs, long length, String[] hosts) {
		this.index = index;
		this.inputs = inputs;
		this.length = length;
		this.hosts = hosts;
	}

	public int[] getIndex() {
		return index;
	}

	public Map<String, Path> getInputs() {
		return inputs;
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
