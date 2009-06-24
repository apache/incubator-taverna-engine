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
package net.sf.taverna.t2.invocation;

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
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < index.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(index[i]);
		}
		String indexArrayAsString = sb.toString();
		return (owner + ":" + indexArrayAsString);
	}

	/**
	 * Helper method for the popIndex operation, returns the modified index
	 * array. Subclasses must still implement logic to get the modified owning
	 * process but that's relatively easy : <code>
	 * return new <Event subclass>(owner.substring(0, owner.lastIndexOf(':')),getPoppedIndex(), dataMap);
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
		for (String part : parts) {
			newIndexArray[pos++] = Integer.parseInt(part);
		}
		for (int i : index) {
			newIndexArray[pos++] = i;
		}
		return newIndexArray;
	}

}
