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
package net.sf.taverna.t2.lang.observer;

import static org.junit.Assert.*;

import java.util.List;

import net.sf.taverna.t2.lang.observer.MultiCaster;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;

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
