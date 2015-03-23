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

package org.apache.taverna.invocation;

/**
 * Abstract superclass for event types which have to pass through the iteration
 * system. For this they need the ability to push and pull the iteration index
 * to and from the process identifier, this is done through the popIndex and
 * pushIndex methods. Subclasses of this may be used outside the iteration
 * system but anything which is passed into the iteration system must provide
 * this functionality.
 * 
 * @author Tom Oinn
 * 
 * @param <EventType>
 *            reflexive self type
 */
public abstract class IterationInternalEvent<EventType extends IterationInternalEvent<?>>
		extends Event<EventType> {
	/**
	 * Protected constructor for the minimum fields required by all Event
	 * subclasses
	 * 
	 * @param owner
	 * @param index
	 * @param context
	 */
	protected IterationInternalEvent(String owner, int[] index,
			InvocationContext context) {
		super(owner, index, context);
	}

	/**
	 * Pop a previously pushed index array off the process name and append the
	 * current index array to create the new index array. This is applied to a
	 * new instance of an Event subclass and does not modify the target.
	 * 
	 * @return new Event subclass with modified owning process and index
	 */
	public abstract IterationInternalEvent<EventType> popIndex();

	/**
	 * Push the index array onto the owning process name and return the new
	 * Event subclass object. Does not modify this object, the method creates a
	 * new Event subclass with the modified index array and owning process.
	 * 
	 */
	public abstract IterationInternalEvent<EventType> pushIndex();

	/**
	 * Helper method for the pushIndex operation
	 * 
	 * @return
	 */
	protected final String getPushedOwningProcess() {
		StringBuilder sb = new StringBuilder(owner).append(":");
		String sep = "";
		for (int idx : index) {
			sb.append(sep).append(idx);
			sep = ",";
		}
		return sb.toString();
	}

	/**
	 * Helper method for the popIndex operation, returns the modified index
	 * array. Subclasses must still implement logic to get the modified owning
	 * process but that's relatively easy : <code>
	 * return new &lt;Event subclass&gt;(owner.substring(0, owner.lastIndexOf(':')),getPoppedIndex(), dataMap);
	 * </code>
	 * 
	 * @return
	 */
	protected final int[] getPoppedIndex() {
		int lastLocation = owner.lastIndexOf(':');
		String indexArrayAsString = owner.substring(lastLocation + 1);
		String[] parts = indexArrayAsString.split(",");
		int[] newIndexArray = new int[index.length + parts.length];
		int pos = 0;
		for (String part : parts)
			newIndexArray[pos++] = Integer.parseInt(part);
		System.arraycopy(index, 0, newIndexArray, pos, index.length);
		return newIndexArray;
	}
}
