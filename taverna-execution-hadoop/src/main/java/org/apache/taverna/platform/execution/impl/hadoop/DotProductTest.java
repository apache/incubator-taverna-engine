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

import org.apache.hadoop.conf.Configuration;
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
			context.write(key, f(values));
		}

		private Text f(Iterable<MapWritable> values) {
			StringBuilder sb = new StringBuilder();
			for (MapWritable value : values) {
				System.out.println("Reduce tag = " + value.get(new Text("tag")));
				System.out.println("Reduce value = " + value.get(new Text("record")));
				sb.append(value.get(new Text("record")) + " ");
			}
			return new Text(sb.toString());
		}
	}

	public int run(String[] args) throws Exception {
		java.util.Map datalinks = new HashMap();


		Configuration configuration = getConf();
		configuration.set("taverna.datalinks", "A|X,B|Y");
		System.out.println(configuration);
		Job job = new Job(configuration);
		job.setJarByClass(DotProductTest.class);
		job.setJobName("dotproduct");

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
