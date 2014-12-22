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
