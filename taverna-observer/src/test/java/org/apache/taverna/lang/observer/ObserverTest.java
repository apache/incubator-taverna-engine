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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class ObserverTest {

	@Test
	public void registerObserver() throws Exception {
		MyObservable observable = new MyObservable();
		MyObserver observer1 = new MyObserver();
		MyObserver observer2 = new MyObserver();

		observable.triggerEvent(); // don't notify, but increase count
		assertNull(observer1.lastMessage);
		observable.addObserver(observer1);
		assertNull(observer1.lastMessage);
		assertNull(observer2.lastMessage);
		assertNull(observer1.lastSender);
		assertNull(observer2.lastSender);
		observable.triggerEvent();
		assertEquals("This is message 1", observer1.lastMessage);
		assertSame(observable, observer1.lastSender);
		assertNull(observer2.lastSender);

		observable.addObserver(observer2);
		assertNull(observer2.lastMessage);
		observable.triggerEvent();
		assertEquals("This is message 2", observer1.lastMessage);
		assertEquals("This is message 2", observer2.lastMessage);
		assertSame(observable, observer1.lastSender);
		assertSame(observable, observer2.lastSender);

		MyObservable otherObservable = new MyObservable();
		otherObservable.addObserver(observer2);
		otherObservable.triggerEvent();
		// New instance, should start from 0
		assertEquals("This is message 0", observer2.lastMessage);
		assertSame(otherObservable, observer2.lastSender);

		// observer1 unchanged
		assertEquals("This is message 2", observer1.lastMessage);
		assertSame(observable, observer1.lastSender);

	}

	@Test
	public void concurrencyTest() {
		MyObservable observable = new MyObservable();
		MyObserver dummyObserver = new MyObserver();
		SelvRemovingObserver selfRemoving = new SelvRemovingObserver();
		observable.addObserver(dummyObserver);
		observable.addObserver(selfRemoving);
		assertEquals(2, observable.getObservers().size());
		observable.triggerEvent();
		
		
	}
	
	public class MyObservable implements Observable<String> {

		private int counter = 0;
		MultiCaster<String> multiCaster = new MultiCaster<String>(this);

		public void addObserver(Observer<String> observer) {
			multiCaster.addObserver(observer);
		}

		public void removeObserver(Observer<String> observer) {
			multiCaster.removeObserver(observer);
		}

		public void triggerEvent() {
			multiCaster.notify("This is message " + counter++);
		}

		public List<Observer<String>> getObservers() {
			return multiCaster.getObservers();
		}
	}

	public class MyObserver implements Observer<String> {
		String lastMessage = null;
		Observable<String> lastSender = null;

		public void notify(Observable<String> sender, String message) {
			lastSender = sender;
			lastMessage = message;
		}
	}
	
	public class SelvRemovingObserver implements Observer<String> {

		public int called=0;
		
		public void notify(Observable<String> sender, String message) {
			called++;
			if (called > 1) {
				fail("Did not remove itself");
			}
			sender.removeObserver(this);
		}
		
	}

}
