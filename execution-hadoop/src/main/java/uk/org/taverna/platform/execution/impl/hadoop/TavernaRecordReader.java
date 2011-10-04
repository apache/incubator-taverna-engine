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

import org.apache.hadoop.io.LongWritable;
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
public class TavernaRecordReader extends RecordReader<LongWritable, Text> {

	private FileSplit split;
	private boolean done;

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		this.split = (FileSplit) split;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (done) {
			return false;
		}
		done = true;
		return true;
	}

	@Override
	public LongWritable getCurrentKey() throws IOException, InterruptedException {
		return new LongWritable(Long.valueOf(split.getPath().getName()));
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return new Text(split.getPath().toString());
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return done?1:0;
	}

	@Override
	public void close() throws IOException {

	}


}
