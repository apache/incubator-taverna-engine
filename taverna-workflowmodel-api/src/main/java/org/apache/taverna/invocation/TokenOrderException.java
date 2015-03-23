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

package org.apache.taverna.invocation;

/**
 * Thrown when tokens are supplied in an invalid order. Examples of this are
 * where duplicate indices are supplied in the same token stream or where list
 * items are emitted at a point where the individual members haven't been fully
 * populated.
 * 
 * @author Tom Oinn
 */
public class TokenOrderException extends Exception {
	public TokenOrderException() {
		super();
	}

	public TokenOrderException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TokenOrderException(String arg0) {
		super(arg0);
	}

	public TokenOrderException(Throwable arg0) {
		super(arg0);
	}

	private static final long serialVersionUID = -7870614853928171878L;
}
