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

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		fileSplit = (FileSplit) split;
		Path path = fileSplit.getPath();
		recordName = path.getName();
		files = path.getFileSystem(context.getConfiguration()).listStatus(path);
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
		mapWritable.put(new Text("tag"), new Text(recordName));
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
