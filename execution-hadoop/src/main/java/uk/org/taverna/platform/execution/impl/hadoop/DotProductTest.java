/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 1 of
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

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class DotProductTest extends Configured implements Tool {

	public static class Map extends Mapper<LongWritable, MapWritable, LongWritable, MapWritable> {
		public void map(LongWritable key, MapWritable value, Context context) throws IOException,
				InterruptedException {
			System.out.println("Map key = " + key);
			System.out.println("Map value tag = " + value.get(new Text("tag")));
			System.out.println("Map value record = " + value.get(new Text("record")));
			context.write(key, value);
		}
	}

	public static class Reduce extends Reducer<LongWritable, MapWritable, LongWritable, Text> {
		public void reduce(LongWritable key, Iterable<MapWritable> values, Context context)
				throws IOException, InterruptedException {

			System.out.println("Reduce key = " + key);
			context.write(key, f(values));
		}

		private Text f(Iterable<MapWritable> values) {
			StringBuilder sb = new StringBuilder();
			for (MapWritable value : values) {
				System.out.println("Reduce value = " + value.get(new Text("record")));
				sb.append(value.get(new Text("record")) + " ");
			}
			return new Text(sb.toString());
		}
	}

	public int run(String[] args) throws Exception {
		System.out.println(getConf());
		Job job = new Job(getConf());
		job.setJarByClass(DotProductTest.class);
		job.setJobName("dot product");

		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(MapWritable.class);

		job.setMapperClass(Map.class);
//		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TavernaInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new DotProductTest(), args);
		System.exit(ret);
	}
}
