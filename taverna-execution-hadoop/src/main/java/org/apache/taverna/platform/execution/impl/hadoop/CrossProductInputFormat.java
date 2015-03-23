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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

/**
 * An input format that receives an input directory containing a number of directories with input files 
 * for each input port to a Taverna processor/activity that will be executed as part of this
 * MapReduce job. Mapping between directory name -> Taverna processor/activity input port name
 * is carried in the job's Context.
 * 
 * @author Alex Nenadic
 *
 */
public class CrossProductInputFormat extends
		FileInputFormat<Text, TextArrayWritable> {

	private static final Log Logger = LogFactory.getLog(CrossProductInputFormat.class);

	// Do not split files into blocks
	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		return false;
	}

	@Override
	public RecordReader<Text, TextArrayWritable> createRecordReader(
			InputSplit split, TaskAttemptContext context) {
		return new CrossProductRecordReader();
	}

	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {

	    // Generate splits. Split is a list of directories where each directory 
		// contains inputs for one input port of the Taverna processor/activity we 
		// are invoking. 
		// We will have only one split for cross product that will know about all
		// the files in all input directories and will generate RecordReaders 
		// for every combination of files inside these directories.
//	    CrossProductInputSplit split = new CrossProductInputSplit();
	    
	    // List the input port directories contained in the input directory passed 
	    // in from the command line.
	    List<FileStatus> inputPortDirectories = listStatus(job); 
	    
		final FileSystem fs = job.getWorkingDirectory().getFileSystem(job.getConfiguration());
		Path workingDirectory = job.getWorkingDirectory();
		System.out.println("Working directory: " + workingDirectory);
		System.out.println("Adding directories to the cross product split:");
		ArrayList<Path> inputPortDirectoriesPaths = new ArrayList<Path>();
    	for (FileStatus inputPortDirectory : inputPortDirectories){
    		// TODO input port directories need to be ordered in the order of the 
    		// input ports of the Taverna processor/activity they are going into
            
    		//inputPortDirectoriesPaths.add(new Text(inputPortDirectory.getPath().toString()));
    		inputPortDirectoriesPaths.add(inputPortDirectory.getPath());
    		System.out.println(inputPortDirectory.getPath());

    	}
	    CrossProductInputSplit split = new CrossProductInputSplit(workingDirectory, inputPortDirectoriesPaths);
	    

	    List<InputSplit> splits = new ArrayList<InputSplit>();
	    splits.add(split);
		
	    return splits;
	}

}
