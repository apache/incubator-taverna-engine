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

package org.apache.taverna.workflowmodel;

/**
 * Potentially thrown when an edit fails due to naming of entities created or
 * modified by the edit. This could be because there are duplicate names in e.g.
 * processor input ports or invalid characters in the name itself
 * 
 * @author Tom Oinn
 * 
 */
public class NamingException extends EditException {

	private static final long serialVersionUID = -6945542133180017313L;

	public NamingException(String message) {
		super(message);
	}

	public NamingException(Throwable cause) {
		super(cause);
	}

	public NamingException(String message, Throwable cause) {
		super(message, cause);
	}

}
