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
