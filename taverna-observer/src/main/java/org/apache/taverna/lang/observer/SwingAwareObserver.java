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
