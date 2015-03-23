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

/**
 * Implementation of the observer pattern.  {@link Observer}s registers with an 
 * {@link Observable} using {@link Observable#addObserver(Observer)}, and will receive 
 * notifications as a call to {@link Observer#notify(Observable, Object)}. 
 * <p>
 * Typical implementations of {@link Observable} will be delegating to a 
 * {@link MultiCaster} to do the boring observer registration and message 
 * dispatching.
 * </p>
 * <p>
 * Example of Observable:
 * <pre>
 * public class MyObservable implements Observable<MyEvent> {
 * 	 public static class MyEvent {
 * 		// ..
 * 	 }
 * 	 private MultiCaster&lt:MyEvent&gt; multiCaster = new MultiCaster&lt:MyEvent&gt;(this);
 * 
 *	 public void doStuff() {
 *		multiCaster.notify(new MyEvent());
 *	 }
 * 
 * 	 public void addObserver(Observer<MyEvent> observer) {
 * 		multiCaster.addObserver(observer);
 * 	 }
 * 
 * 	 public List<Observer<MyEvent>> getObservers() {
 * 		return multiCaster.getObservers();
 * 	 }
 * 
 * 	 public void removeObserver(Observer<MyEvent> observer) {
 * 		multiCaster.removeObserver(observer);
 * 	 }
 * }
 * </pre>
 * And an observer that is notified when MyObservable.doStuff() is called:
 * <pre>
 * public class MyObserver implements Observer<MyEvent> {
 *	 public void notify(Observable<MyEvent> sender, MyEvent message) {
 *		System.out.println("Receieved " + message + " from " + sender);
 * 	 }
 * }
 * </pre>
 * Example of usage:
 * <pre>
 * 		MyObservable observable = new MyObservable();
 *		MyObserver observer = new MyObserver();
 *		observable.addObserver(observer);
 *		observable.doStuff();
 *	</pre>
 */
package org.apache.taverna.lang.observer;

