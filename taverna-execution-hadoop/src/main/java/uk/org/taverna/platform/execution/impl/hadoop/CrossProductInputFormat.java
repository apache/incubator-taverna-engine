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
