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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.InvalidInputException;
import org.apache.hadoop.mapreduce.security.TokenCache;

/**
 *
 *
 * @author David Withers
 */
public class TavernaInputFormat extends FileInputFormat<int[], Map<String, Path>> {

	private static final PathFilter inputFileFilter = new PathFilter() {
		public boolean accept(Path p) {
//			String name = p.getName();
//			return name.matches("[0-9]+") || name.equals("value");
			return true;
		}
	};

	@Override
	public RecordReader<int[], Map<String, Path>> createRecordReader(InputSplit split,
			TaskAttemptContext context) throws IOException, InterruptedException {
		return null;
	}

	public List<InputSplit> getSplits(JobContext job) throws IOException {
		int index = 0;
		List<InputSplit> splits = new ArrayList<InputSplit>();
		Map<String, List<FileStatus>> inputPortFiles = getInputPortFiles(job);
		List<Map<String, FileStatus>> iterations = dotProduct(inputPortFiles);

		for (Map<String, FileStatus> iteration : iterations) {
			long length = 0;
			Map<String, Path> inputs = new HashMap<String, Path>();
			List<String> hosts = new ArrayList<String>();

			for (Entry<String, FileStatus> entry : iteration.entrySet()) {
				FileStatus file = entry.getValue();
				long fileLength = file.getLen();
				length += fileLength;
				Path path = file.getPath();
				FileSystem fs = path.getFileSystem(job.getConfiguration());
				BlockLocation[] blkLocations = fs.getFileBlockLocations(file, 0, length);
				for (BlockLocation blockLocation : blkLocations) {
					hosts.addAll(Arrays.asList(blockLocation.getHosts()));
				}
				inputs.put(entry.getKey(), path);
			}

			splits.add(new TavernaInputSplit(new int[] {index++}, inputs, length, hosts.toArray(new String[hosts.size()])));
		}
		return splits;
	}

	/**
	 * @param inputPortFiles
	 * @return
	 */
	private List<Map<String, FileStatus>> dotProduct(Map<String, List<FileStatus>> inputPortFiles) {
		List<Map<String, FileStatus>> iterations = new ArrayList<Map<String, FileStatus>>();
		for (Entry<String, List<FileStatus>> entry : inputPortFiles.entrySet()) {
			String port = entry.getKey();
			List<FileStatus> paths = entry.getValue();
			for (int i = 0; i < paths.size(); i++) {
				if (iterations.size() < i) {
					iterations.add(new HashMap<String, FileStatus>());
				}
				iterations.get(i).put(port, paths.get(i));
			}
		}
		return iterations;
	}

	protected Map<String, List<FileStatus>> getInputPortFiles(JobContext job) throws IOException {
		Map<String, List<FileStatus>> result = new HashMap<String, List<FileStatus>>();
		Path[] dirs = getInputPaths(job);
		if (dirs.length == 0) {
			throw new IOException("No input paths specified in job");
		}

		// get tokens for all the required FileSystems..
		TokenCache.obtainTokensForNamenodes(job.getCredentials(), dirs, job.getConfiguration());

		List<IOException> errors = new ArrayList<IOException>();

		PathFilter inputFilter = inputFileFilter;

		for (int i = 0; i < dirs.length; ++i) {
			Path path = dirs[i];
			String portName = path.getName();
			FileSystem fs = path.getFileSystem(job.getConfiguration());
			FileStatus[] matches = fs.globStatus(path, inputFilter);
			if (matches == null) {
				errors.add(new IOException("Input path does not exist: " + path));
			} else if (matches.length == 0) {
				errors.add(new IOException("Input Pattern " + path + " matches 0 files"));
			} else {
				List<FileStatus> paths = new ArrayList<FileStatus>();
				for (FileStatus globStat : matches) {
					paths.add(globStat);
				}
				result.put(portName, paths);

			}
		}

		if (!errors.isEmpty()) {
			throw new InvalidInputException(errors);
		}
		// LOG.info("Total input paths to process : " + result.size());
		return result;
	}

}
