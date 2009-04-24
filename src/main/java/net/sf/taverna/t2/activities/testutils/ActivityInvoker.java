/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package net.sf.taverna.t2.activities.testutils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.platform.spring.RavenAwareClassPathXmlApplicationContext;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivity;

import org.springframework.context.ApplicationContext;

/**
 * Helper class to facilitate in executing Activities in isolation.
 * 
 * @author Stuart Owen
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * 
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

		ApplicationContext context = new RavenAwareClassPathXmlApplicationContext(
		"inMemoryActivityTestsContext.xml");
		ReferenceService referenceService = (ReferenceService) context.getBean("t2reference.service.referenceService");

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
