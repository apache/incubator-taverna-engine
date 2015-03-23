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
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class TavernaMapper extends org.apache.hadoop.mapreduce.Mapper<int[], Map<String, Path>, Object, Object> {

	private org.apache.hadoop.mapreduce.Mapper.Context context;

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		this.context = context;
	}
	
	@Override
	protected void map(int[] key, Map<String, Path> value, 
              Context context) throws IOException, InterruptedException {
		
		// Value contains a map of input ports to data values on those ports 
		// (i.e. file paths to data on the input ports) 

		
		// Get the activity and invoke it with the passed inputs per port
//		
//		String activityClassName = context.getConfiguration().get("taverna.activity.class");
//		String activityConfigurationXML = context.getConfiguration().get("taverna.activity.configuration");
//	
//	    ClassLoader classLoader = this.getClass().getClassLoader();
//	    Class<?> activityClass = null;
//	    AbstractAsynchronousActivity<?> activity = null;
//	    try {
//	        activityClass = classLoader.loadClass(activityClassName);
//	        activity = (AbstractAsynchronousActivity<?>) activityClass.newInstance();
//	    } catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//	        e.printStackTrace();
//	    } catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    
//	    activity.configure(activityConfigurationXML);
//	    activity.executeAsynch(data, callback);

		System.out.println("Index: " + key);

		// Input port names
		Iterator<String> iterator = value.keySet().iterator();
		while(iterator.hasNext()){
			String inputPortName = iterator.next();
			// Simply read values from input files and concatenate them
			Path inputFilePath = value.get(inputPortName);
			FSDataInputStream fileInputStream = inputFilePath.getFileSystem(null).open(inputFilePath);
			//fileInputStream.
			System.out.println("Input port: " + inputPortName + ". Input value: "+ inputFilePath +".");
		}
		
		// Map of output ports to data values on those ports 
		// (i.e. file paths to data on the output ports)
		Map<String, Path> outputValue = new HashMap<String, Path>();
		context.write(key, outputValue);
	}
	  
}
