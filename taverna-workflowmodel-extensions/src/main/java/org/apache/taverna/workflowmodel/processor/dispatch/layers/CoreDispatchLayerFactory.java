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

package org.apache.taverna.workflowmodel.processor.dispatch.layers;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Factory for creating core dispatch layers.
 *
 * The core dispatch layers are :
 * <ul>
 * <li>ErrorBounce</li>
 * <li>Parallelize</li>
 * <li>Failover</li>
 * <li>Retry</li>
 * <li>Stop</li>
 * <li>Invoke</li>
 * <li>Loop</li>
 * <li>IntermediateProvenance</li>
 * </ul>
 *
 * @author David Withers
 */
public class CoreDispatchLayerFactory implements DispatchLayerFactory {
	private static final URI parallelizeLayer = URI.create(Parallelize.URI);
	private static final URI errorBounceLayer = URI.create(ErrorBounce.URI);
	private static final URI failoverLayer = URI.create(Failover.URI);
	private static final URI retryLayer = URI.create(Retry.URI);
	private static final URI invokeLayer = URI.create(Invoke.URI);
	private static final URI loopLayer = URI.create(Loop.URI);
	private static final URI intermediateProvenanceLayer = URI.create(IntermediateProvenance.URI);
	private static final URI stopLayer = URI.create(Stop.URI);

	private final static Set<URI> dispatchLayerURIs = new HashSet<URI>();

	static {
		dispatchLayerURIs.add(parallelizeLayer);
		dispatchLayerURIs.add(errorBounceLayer);
		dispatchLayerURIs.add(failoverLayer);
		dispatchLayerURIs.add(retryLayer);
		dispatchLayerURIs.add(invokeLayer);
		dispatchLayerURIs.add(loopLayer);
		dispatchLayerURIs.add(intermediateProvenanceLayer);
		dispatchLayerURIs.add(stopLayer);
	}

	@Override
	public DispatchLayer<?> createDispatchLayer(URI uri) {
		if (parallelizeLayer.equals(uri))
			return new Parallelize();
		else if (errorBounceLayer.equals(uri))
			return new ErrorBounce();
		else if (failoverLayer.equals(uri))
			return new Failover();
		else if (retryLayer.equals(uri))
			return new Retry();
		else if (invokeLayer.equals(uri))
			return new Invoke();
		else if (loopLayer.equals(uri))
			return new Loop();
		else if (intermediateProvenanceLayer.equals(uri))
			return new IntermediateProvenance();
		else if (stopLayer.equals(uri))
			return new Stop();
		return null;
	}

	@Override
	public JsonNode getDispatchLayerConfigurationSchema(URI uri) {
		// TODO
		return null;
	}

	@Override
	public Set<URI> getDispatchLayerTypes() {
		return dispatchLayerURIs;
	}
}
