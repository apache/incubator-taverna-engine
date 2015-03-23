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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
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

public class CrossProductTest extends Configured implements Tool {

	public static class Map extends Mapper<Text, TextArrayWritable, Text, TextArrayWritable> {
		public void map(Text key, TextArrayWritable value, Context context) throws IOException,
				InterruptedException {
			System.out.println("Map key = " + key);
			System.out.println("Map value = " );

			for (int i = 0; i < value.get().length; i++){
				System.out.println("  " + value.get()[i]);
			}

			context.write(key, value);
		}
	}

	public static class Reduce extends Reducer<Text, TextArrayWritable, Text, Text> {
		public void reduce(Text key, Iterable<TextArrayWritable> values, Context context)
				throws IOException, InterruptedException {

			System.out.println("Reduce key = " + key);
			context.write(key, f(values));
		}

		private Text f(Iterable<TextArrayWritable> values) {
			StringBuilder sb = new StringBuilder();

			// There should be only one array
			TextArrayWritable arrayValue = values.iterator().next();

			for (int i = 0; i < arrayValue.get().length; i++){
				sb.append(arrayValue.get()[i] + "\nx");
			}
			String str = sb.toString();
			if (str.contains("\nx")){
				str = str.substring(0, sb.lastIndexOf("\nx") -1);
			}
			System.out.println("Result of function f(): " + str);

			return new Text(str);
		}
	}

	public int run(String[] args) throws Exception {

		Configuration configuration = getConf();
		configuration.set("taverna.datalinks", "A|X,B|Y");
		System.out.println(configuration);
		Job job = new Job(configuration);
		job.setJarByClass(CrossProductTest.class);
		job.setJobName("crossproduct");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(TextArrayWritable.class);

		job.setMapperClass(Map.class);
//		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(CrossProductInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		System.out.println("Input dir: " + args[0]);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.out.println("Output dir: " + args[1]);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new CrossProductTest(), args);
		System.exit(ret);
	}
}
