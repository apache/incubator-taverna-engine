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

package org.apache.taverna.activities.testutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.taverna.reference.ExternalReferenceBuilderSPI;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ExternalReferenceTranslatorSPI;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.StreamToValueConverterSPI;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.ValueToReferenceConverterSPI;
import org.apache.taverna.reference.impl.ErrorDocumentServiceImpl;
import org.apache.taverna.reference.impl.InMemoryErrorDocumentDao;
import org.apache.taverna.reference.impl.InMemoryListDao;
import org.apache.taverna.reference.impl.InMemoryReferenceSetDao;
import org.apache.taverna.reference.impl.ListServiceImpl;
import org.apache.taverna.reference.impl.ReferenceServiceImpl;
import org.apache.taverna.reference.impl.ReferenceSetAugmentorImpl;
import org.apache.taverna.reference.impl.ReferenceSetServiceImpl;
import org.apache.taverna.reference.impl.SimpleT2ReferenceGenerator;
import org.apache.taverna.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivity;

/**
 * Helper class to facilitate in executing Activities in isolation.
 * 
 * @author Stuart Owen
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * @author David Withers
 */
public class ActivityInvoker {

	/**
	 * Timeout in seconds
	 */
	public static long TIMEOUT = 30;

	
	/**
	 * Invokes an {@link AsynchronousActivity} with a given set of input Objects
	 * and returns a Map<String,Object> of requested output values.
	 * 
	 * @param activity
	 *            the activity to be tested
	 * @param inputs
	 *            a Map<String,Object> of input Objects
	 * @param requestedOutputs
	 *            a List<String> of outputs to be examined
	 * 
	 * @return a Map<String,Object> of the outputs requested by requestedOutput
	 *         or <code>null</code> if a failure occurs
	 * @throws InterruptedException 
	 * @throws Throwable 
	 */
/*	public static Map<String, Object> invokeAsyncActivity(
			AbstractAsynchronousActivity<?> activity,
			Map<String, Object> inputs, Map<String, Class<?>> requestedOutputs)
			throws Exception {
		Map<String, Object> results = new HashMap<String, Object>();

		ApplicationContext context = new RavenAwareClassPathXmlApplicationContext(
		"inMemoryActivityTestsContext.xml");
		ReferenceService referenceService = (ReferenceService) context.getBean("t2reference.service.referenceService");

		DummyCallback callback = new DummyCallback(referenceService);
		Map<String, T2Reference> inputEntities = new HashMap<String, T2Reference>();
		for (String inputName : inputs.keySet()) {
			Object val = inputs.get(inputName);
			if (val instanceof List) {
				inputEntities.put(inputName, referenceService.register(val, 1, true, callback.getContext()));
			} else {
				inputEntities.put(inputName, referenceService.register(val, 0, true, callback.getContext()));
			}
		}

		activity.executeAsynch(inputEntities, callback);
		callback.thread.join();

		if (callback.failed) {
			results = null;
		} else {
			for (Map.Entry<String, Class<?>> output : requestedOutputs.entrySet()) {
				T2Reference id = callback.data.get(output.getKey());
				if (id != null) {
					Object result;
					result = referenceService.renderIdentifier(id, output.getValue(), callback.getContext());
					results.put(output.getKey(), result);
				}
			}
		}
		return results;
	}
	*/

	// Changed this method to render the T2Reference to an object only if the type of the object in 
	// requestedOutputs is not an instance of ExternalReferenceSPI. Otherwise, the calling test method 
	// should get activity ReferenceSet and render the object itself. This was needed for API consumer activity 
	// testing - see ApiConsumerActivityTest.
	// Also added support for multi-dimensional lists.
	public static Map<String, Object> invokeAsyncActivity(
			AbstractAsynchronousActivity<?> activity,
			Map<String, Object> inputs, Map<String, Class<?>> requestedOutputs) throws InterruptedException
			 {
		
		Map<String, Object> results = new HashMap<String, Object>();

		ReferenceService referenceService = createReferenceService();
		
		DummyCallback callback = new DummyCallback(referenceService);
		Map<String, T2Reference> inputEntities = new HashMap<String, T2Reference>();
		for (String inputName : inputs.keySet()) {
			Object val = inputs.get(inputName);
			int depth = getDepth(val);
			inputEntities.put(inputName, referenceService.register(val, depth, true, callback.getContext()));
		}

		activity.executeAsynch(inputEntities, callback);
		callback.thread.join(TIMEOUT*1000);

		
		if (callback.failed) {
			throw callback.failures.get(0);
		} else {
			for (Map.Entry<String, Class<?>> output : requestedOutputs.entrySet()) {
				T2Reference id = callback.data.get(output.getKey());
				if (ExternalReferenceSPI.class.isAssignableFrom(output.getValue())){
					// Do not render the object - just resolve the T2Reference
					Object result;
					result = referenceService.resolveIdentifier(id, null, callback.getContext());
					results.put(output.getKey(), result);
				}
				else{
					// Try to render the object behind the reference
					Object result;
					result = referenceService.renderIdentifier(id, output.getValue(), callback.getContext());
					results.put(output.getKey(), result);
				}
			}
		}
		return results;
	}

	private static ReferenceService createReferenceService() {
		SimpleT2ReferenceGenerator referenceGenerator = new SimpleT2ReferenceGenerator();
		ReferenceSetAugmentorImpl referenceSetAugmentor = new ReferenceSetAugmentorImpl();
		referenceSetAugmentor.setBuilders((List<ExternalReferenceBuilderSPI<?>>) getBuilders());
		referenceSetAugmentor.setTranslators(getTranslators());
		
		ReferenceSetServiceImpl referenceSetService = new ReferenceSetServiceImpl();
		referenceSetService.setT2ReferenceGenerator(referenceGenerator);
		referenceSetService.setReferenceSetDao(new InMemoryReferenceSetDao());
		referenceSetService.setReferenceSetAugmentor(referenceSetAugmentor);
		
		ListServiceImpl listService = new ListServiceImpl();
		listService.setT2ReferenceGenerator(referenceGenerator);
		listService.setListDao(new InMemoryListDao());
		
		ErrorDocumentServiceImpl errorDocumentService = new ErrorDocumentServiceImpl();
		errorDocumentService.setT2ReferenceGenerator(referenceGenerator);
		errorDocumentService.setErrorDao(new InMemoryErrorDocumentDao());
		
		ReferenceServiceImpl referenceService = new ReferenceServiceImpl();
		referenceService.setReferenceSetService(referenceSetService);
		referenceService.setListService(listService);
		referenceService.setErrorDocumentService(errorDocumentService);
		referenceService.setConverters(getConverters());
		referenceService.setValueBuilders(getValueBuilders());
		
		return referenceService;
	}
	
	private static <T> List<T> getImplementations(Class<T> api) {
		List<T> implementations = new ArrayList<T>();
		ServiceLoader<T> serviceLoader = ServiceLoader.load(api);
		for (T implementation : serviceLoader) {
			implementations.add(implementation);
		}
		return implementations;
	}
	
	private static List<StreamToValueConverterSPI> getValueBuilders() {
		return getImplementations(StreamToValueConverterSPI.class);
	}

	private static List<ValueToReferenceConverterSPI> getConverters() {
		return getImplementations(ValueToReferenceConverterSPI.class);
	}

	private static List<ExternalReferenceTranslatorSPI<?, ?>> getTranslators() {
		List<ExternalReferenceTranslatorSPI<?, ?>> implementations = new ArrayList<ExternalReferenceTranslatorSPI<?, ?>>();
		ServiceLoader<ExternalReferenceTranslatorSPI> serviceLoader = ServiceLoader.load(ExternalReferenceTranslatorSPI.class);
		for (ExternalReferenceTranslatorSPI implementation : serviceLoader) {
			implementations.add(implementation);
		}
		return implementations;
	}

	private static List<ExternalReferenceBuilderSPI<?>> getBuilders() {
		List<ExternalReferenceBuilderSPI<?>> implementations = new ArrayList<ExternalReferenceBuilderSPI<?>>();
		ServiceLoader<ExternalReferenceBuilderSPI> serviceLoader = ServiceLoader.load(ExternalReferenceBuilderSPI.class);
		for (ExternalReferenceBuilderSPI implementation : serviceLoader) {
			implementations.add(implementation);
		}
		return implementations;
	}

	/**
	 * If an object is activity list - returns its depth, 0 otherwise (for single objects).
	 * @param obj
	 * @return
	 */
	private static int getDepth(Object obj){

		if (obj instanceof List) {
			// Assumes all sub-lists are of the same depth,
			// so just uses the first sub-list to calculate it.
			Object[] sublists = ((List<?>)obj).toArray();
			int depth = 1;
			depth = getDepth(sublists[0]) + 1;
			return depth;
		} else {
			return 0;
		}
	}
}
