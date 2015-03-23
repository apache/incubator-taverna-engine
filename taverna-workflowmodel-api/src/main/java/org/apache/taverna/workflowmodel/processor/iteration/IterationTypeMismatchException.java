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

package org.apache.taverna.workflowmodel.processor.iteration;

/**
 * Thrown during the typecheck phase when an iteration strategy is configured
 * such that at runtime it would fail. This is generally because a dot product
 * node has been specified where the children of that node will have different
 * cardinalities (in this case the dot product isn't defined)
 * 
 * @author Tom Oinn
 */
public class IterationTypeMismatchException extends Exception {
	private static final long serialVersionUID = -3034020607723767223L;

	public IterationTypeMismatchException() {
		super();
	}

	public IterationTypeMismatchException(String arg0) {
		super(arg0);
	}

	public IterationTypeMismatchException(Throwable arg0) {
		super(arg0);
	}

	public IterationTypeMismatchException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
