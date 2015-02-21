/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
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

import javax.swing.SwingUtilities;

/**
 * Implementation of an {@link Observer} that adds calls to notify to the AWT event dispatching
 * thread.
 *
 * @author David Withers
 */
public abstract class SwingAwareObserver<Message> implements Observer<Message> {

	@Override
	public void notify(final Observable<Message> sender, final Message message) throws Exception {
	    Runnable runnable = new Runnable() {
			@Override
			public void run() {
				notifySwing(sender, message);
			}
	    };
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			// T2-971
			SwingUtilities.invokeLater(runnable);
		}
	}

	public abstract void notifySwing(Observable<Message> sender, Message message);

}
