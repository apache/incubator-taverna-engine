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

package org.apache.taverna.reference;

/**
 * Thrown by setter methods and constructors of ExternalReferenceSPI
 * implementations when fed parameters which cause some kind of format or
 * validation error. These might include badly formed URL or file paths or any
 * other property that fails to validate against some reference type specific
 * scheme.
 * 
 * @author Tom Oinn
 */
public class ExternalReferenceValidationException extends RuntimeException {
	private static final long serialVersionUID = 3031393671457773057L;

	public ExternalReferenceValidationException() {
		//
	}

	public ExternalReferenceValidationException(String message) {
		super(message);
	}

	public ExternalReferenceValidationException(Throwable cause) {
		super(cause);
	}

	public ExternalReferenceValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
