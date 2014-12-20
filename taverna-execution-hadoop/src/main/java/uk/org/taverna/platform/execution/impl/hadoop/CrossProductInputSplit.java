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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 *
 *
 * @author Alex Nenadic
 */
public class CrossProductInputSplit extends FileSplit {
	//
	// private long length = 0;
	// private String[] hosts;
	private List<Path> inputPortDirectories;
	private Path workingDirectory;

	public CrossProductInputSplit() {
		super(null,0,0,null);
		inputPortDirectories = new ArrayList<Path>();
		System.out.println("Calling default constructor for cross product split");
	}

	public CrossProductInputSplit(Path workingDirectory, List<Path> inputPortDirectories) {
		// this.length = length;
		// this.hosts = hosts;
		super(workingDirectory, 0, 0, new String[0]);
		this.workingDirectory = workingDirectory;
		this.inputPortDirectories = inputPortDirectories;
		System.out.println("Calling non-default constructor for cross product split");
	}

	public void addInputPortDirectory(Path path) {
		inputPortDirectories.add(path);
	}

	public List<Path> getInputPortDirectories() {
		return inputPortDirectories;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);
		Text.writeString(out, workingDirectory.toString());
		out.writeInt(inputPortDirectories.size());
		for (Path path : inputPortDirectories) {
			Text.writeString(out, path.toString());
		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
		workingDirectory = new Path(Text.readString(in));
		int length = in.readInt();
		for (int i = 0; i < length; i++) {
			inputPortDirectories.add(new Path(Text.readString(in)));
		}
	}

}
