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

package org.apache.taverna.platform.execution.impl.hadoop;

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
