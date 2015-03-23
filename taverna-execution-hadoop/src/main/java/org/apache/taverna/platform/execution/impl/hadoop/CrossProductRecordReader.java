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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.pingel.util.CrossProduct;

public class CrossProductRecordReader extends RecordReader<Text, TextArrayWritable>{

	private static final Log Logger = LogFactory.getLog(CrossProductRecordReader.class);

	// Input directories (one for each port) containing files that are used 
	// as inputs to Taverna processor/activity
	private List<Path> inputPortDirectories;
	
	private CrossProduct<String> crossProduct ;

	private Iterator<List<String>> crossProductIterator;

	private List<String> currentIndexes;

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {

		System.out.println("Inside record reader's initialize");
		
		CrossProductInputSplit crossProductSplit = (CrossProductInputSplit)split;
		inputPortDirectories = crossProductSplit.getInputPortDirectories();
		System.out.println("Record reader received " + +inputPortDirectories.size() + " input port directories");

		List<List<String>> iterables = new ArrayList<List<String>>();
		for (int i=0; i<inputPortDirectories.size();i++ ){
	
			Path inputPortDirectory = inputPortDirectories.get(i);
			//Path inputPortDirectory = inputPortDirectories.get(i);
			FileStatus[] files = inputPortDirectory.getFileSystem(context.getConfiguration()).listStatus(inputPortDirectory);
			List<String> fileNames = new ArrayList<String>();
			for (FileStatus file : files){
				fileNames.add(file.getPath().getName());
			}
			iterables.add(fileNames);
		}

		crossProduct = new CrossProduct<String>(iterables);
		crossProductIterator = crossProduct.iterator();
		
	}

	@Override
	public boolean nextKeyValue(){

		boolean hasNextKey = crossProductIterator.hasNext();
		System.out.println("Has record reader next key value? " + hasNextKey);
		if (hasNextKey){
			currentIndexes = crossProductIterator.next();
		}
		return hasNextKey;
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
	
		StringBuffer sb = new StringBuffer();
		for (String index : currentIndexes){
			sb.append(index + ".");
		}
		// Remove last "."
		String indexesString = sb.toString();
		System.out.println("Get current key: " + indexesString);
		if (indexesString.contains(".")){
			indexesString = indexesString.substring(0, indexesString.length() - 1);
		}
		return new Text(indexesString);
	}

	@Override
	public TextArrayWritable getCurrentValue() {
				
		TextArrayWritable arrayWritable = new TextArrayWritable();
		Text[] array = new Text[currentIndexes.size()];
		for(int i= 0; i< currentIndexes.size(); i++){
			Path file = new Path(inputPortDirectories.get(i).toString(), currentIndexes.get(i));
			array[i] = new Text(file.toString());
		}
		arrayWritable.set(array);
		return arrayWritable;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
