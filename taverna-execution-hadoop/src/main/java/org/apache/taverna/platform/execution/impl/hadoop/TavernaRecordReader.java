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
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 *
 *
 * @author David Withers
 */
public class TavernaRecordReader extends RecordReader<LongWritable, MapWritable> {

	private FileSplit fileSplit;
	private String recordName;
	private FileStatus[] files;
	private int index = -1;
	private Map<String, String> datalinks;

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		fileSplit = (FileSplit) split;
		Path path = fileSplit.getPath();
		recordName = path.getName();
		files = path.getFileSystem(context.getConfiguration()).listStatus(path);
		setDatalinks(context);
	}

	/**
	 * @param context
	 */
	private void setDatalinks(TaskAttemptContext context) {
		datalinks = new HashMap<String, String>();
		String datalinkConfig = context.getConfiguration().get("taverna.datalinks");
		if (datalinkConfig != null) {
			String[] datalinksSplit = datalinkConfig.split(",");
			for (String datalink : datalinksSplit) {
				String[] split = datalink.split("\\|");
				if (split.length == 2) {
					datalinks.put(split[0], split[1]);
				}
			}
		}
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		index++;
		return index < files.length;
	}

	@Override
	public LongWritable getCurrentKey() throws IOException, InterruptedException {
		return new LongWritable(Long.valueOf(files[index].getPath().getName()));
	}

	@Override
	public MapWritable getCurrentValue() throws IOException, InterruptedException {
		MapWritable mapWritable = new MapWritable();
		mapWritable.put(new Text("tag"), new Text(datalinks.get(recordName)));
		mapWritable.put(new Text("record"), new Text(files[index].getPath().toString()));
		return mapWritable;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return files.length == 0 ? 1 : (index + 1) / files.length;
	}

	@Override
	public void close() throws IOException {

	}


}
