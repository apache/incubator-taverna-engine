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

package org.apache.taverna.lang.observer;

import java.util.List;

/**
 * Implements this if you want to notify other classes about changes
 * 
 * @author Ian Dunlop
 * @author Stian Soiland
 * 
 * @param <Message>
 */
public interface Observable<Message> {
	/**
	 * Register an {@link Observer}
	 * 
	 * @param observer
	 *            the class who wants notified of changes
	 */
	public void addObserver(Observer<Message> observer);

	/**
	 * Remove a class who is currently observing
	 * 
	 * @param observer
	 *            the class who no longer wants notified
	 */
	public void removeObserver(Observer<Message> observer);

	/**
	 * A list of all the currently registered {@link Observer}s
	 * 
	 * @return
	 */
	public List<Observer<Message>> getObservers();
}
