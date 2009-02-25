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
package net.sf.taverna.t2.lang.observer;

