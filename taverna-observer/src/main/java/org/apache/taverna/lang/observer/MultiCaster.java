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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Send notifications to registered observers about changes to models
 * 
 * @author Ian Dunlop
 * @author Stian Soiland
 * 
 * @param <Message>
 */
public class MultiCaster<Message> implements Observable<Message> {

	private static Logger logger = Logger.getLogger(MultiCaster.class);

	private Observable<Message> observable;

	protected List<Observer<Message>> observers = new ArrayList<Observer<Message>>();

	/**
	 * Set the {@link #observable} ie. the class that changes are happening to
	 * and it's Message for this {@link MultiCaster}
	 * 
	 * @param observable
	 */
	public MultiCaster(Observable<Message> observable) {
		this.observable = observable;
	}

	/**
	 * Tell all the registered observers about the change to the model
	 * 
	 * @param message
	 */
	@SuppressWarnings("unchecked")
	public void notify(Message message) {
		// Use a copy that can be iterated even if register/remove is called
		for (Observer<Message> observer : getObservers()) {
			try {
				observer.notify(observable, message);
			} catch (Exception ex) {
				logger.warn("Could not notify " + observer, ex);
			}
		}
	}

	/**
	 * Register an observer ie. someone who wants informed about changes
	 */
	public synchronized void addObserver(Observer<Message> observer) {
		observers.add(observer);
	}

	/**
	 * Remove the observer and no longer send out any notifications about it
	 */
	public synchronized void removeObserver(Observer<Message> observer) {
		observers.remove(observer);
	}

	/**
	 * A list of all the classes currently registered with this
	 * {@link MultiCaster}
	 */
	public synchronized List<Observer<Message>> getObservers() {
		return new ArrayList<Observer<Message>>(observers);
	}

}
