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

import java.util.List;
import java.util.Set;

/**
 * Contains the definition of an error token within the workflow system.
 * 
 * @author Tom Oinn
 * @author David Withers
 */
public interface ErrorDocument extends Identified {
	/**
	 * If the error document is created from a {@link Throwable} it will have a
	 * stack trace, in this case the stack trace is represented as a list of
	 * {@link StackTraceElement} beans
	 */
	List<StackTraceElementBean> getStackTraceStrings();

	/**
	 * If the error document is created from a {@link Throwable}, this contains
	 * the message part of the {@link Throwable}.
	 */
	String getExceptionMessage();

	/**
	 * Error documents can carry an arbitrary string message, this returns it.
	 */
	String getMessage();

	/**
	 * If the error document is created from set of references that contain
	 * error documents, this method returns them.
	 */
	Set<T2Reference> getErrorReferences();
}
