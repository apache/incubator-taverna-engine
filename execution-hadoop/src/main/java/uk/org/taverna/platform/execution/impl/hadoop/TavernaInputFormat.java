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
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.security.TokenCache;

/**
 *
 *
 * @author David Withers
 */
public class TavernaInputFormat extends FileInputFormat<LongWritable, MapWritable> {

	@Override
	public RecordReader<LongWritable, MapWritable> createRecordReader(InputSplit split,
			TaskAttemptContext context) throws IOException, InterruptedException {
		return new TavernaRecordReader();
	}

	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		return false;
	}

	@Override
	protected List<FileStatus> listStatus(JobContext job) throws IOException {
		List<FileStatus> result = new ArrayList<FileStatus>();
		Path[] dirs = getInputPaths(job);
		if (dirs.length == 0) {
			throw new IOException("No input paths specified in job");
		}

		// get tokens for all the required FileSystems..
		TokenCache.obtainTokensForNamenodes(job.getCredentials(), dirs, job.getConfiguration());

		PathFilter inputFilter = getInputPathFilter(job);

		for (int i = 0; i < dirs.length; ++i) {
			Path p = dirs[i];
			FileSystem fs = p.getFileSystem(job.getConfiguration());
			FileStatus[] matches = fs.globStatus(p/*, inputFilter*/);
			if (matches == null) {
				throw new IOException("Input path does not exist: " + p);
			} else if (matches.length == 0) {
				throw new IOException("Input Pattern " + p + " matches 0 files");
			} else {
				for (FileStatus globStat : matches) {
					if (globStat.isDir()) {
						for (FileStatus stat : fs.listStatus(globStat.getPath()/*, inputFilter*/)) {
							result.add(stat);
						}
					} else {
						result.add(globStat);
					}
				}
			}
		}
		return result;
	}

}
