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

import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 *
 *
 * @author David Withers
 */
public class Test extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		Configuration configuration = getConf();
		Job job = new Job(configuration);
		job.setJarByClass(Test.class);
		job.setJobName("wordcount");

		job.setOutputKeyClass(int[].class);
		job.setOutputValueClass(Map.class);

		job.setMapperClass(TavernaMapper.class);
//		job.setCombinerClass(Reduce.class);
		job.setReducerClass(TavernaReducer.class);

		job.setInputFormatClass(TavernaInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		TavernaInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new Test(), args);
		System.exit(ret);
	}
}
