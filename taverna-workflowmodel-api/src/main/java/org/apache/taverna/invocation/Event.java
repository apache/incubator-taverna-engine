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
 * Abstract superclass of all 'event' types within a workflow invocation. These
 * are the Job and Completion events which are used internally within a
 * Processor, in particular by the dispatch stack and iteration system, and the
 * WorkflowDataToken which is the only event class that can exist outside of a
 * Processor boundary (and is therefore the most significant one for users of
 * the API)
 * 
 * @author Tom Oinn
 */
public abstract class Event<EventType extends Event<?>> {
	protected String owner;
	protected InvocationContext context;
	protected int[] index;

	protected Event(String owner, int[] index, InvocationContext context) {
		this.owner = owner;
		this.index = index;
		this.context = context;
		if (index == null)
			throw new RuntimeException("Job index cannot be null");
		if (owner == null)
			throw new RuntimeException("Owning process cannot be null");
		if (context == null)
			throw new RuntimeException("Invocation context cannot be null");
	}

	/**
	 * An event is final if its index array is zero length
	 * 
	 * @return true if indexarray.length==0
	 */
	public final boolean isFinal() {
		return (index.length == 0);
	}

	/**
	 * The event has an owner, this is represented as a String object but the
	 * ownership is hierarchical in nature. The String is a colon separated list
	 * of alphanumeric process identifiers, with identifiers being pushed onto
	 * this list on entry to a process and popped off on exit.
	 * 
	 * @return String of colon separated process identifiers owning this Job
	 */
	public final String getOwningProcess() {
		return this.owner;
	}

	public final InvocationContext getContext() {
		return this.context;
	}

	/**
	 * Return a copy of the event subclass with the last owning process removed
	 * from the owning process list. For example, if the event had owner
	 * 'foo:bar' this would return a duplicate event with owner 'foo'. If the
	 * owning process is the empty string this is invalid and will throw a
	 * ProcessIdentifierException
	 * 
	 * @return a copy of the event with the parent process identifier
	 */
	public abstract EventType popOwningProcess()
			throws ProcessIdentifierException;

	/**
	 * Return a copy of the event subclass with the specified local process name
	 * appended to the owning process identifier field. If the original owner
	 * was 'foo' and this was called with 'bar' you'd end up with a copy of the
	 * subclass with owner 'foo:bar'
	 * 
	 * @param localProcessName
	 *            name to add
	 * @return the modified event
	 * @throws ProcessIdentifierException
	 *             if the local process name contains the ':' character
	 */
	public abstract EventType pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException;

	/**
	 * Events have an index placing them in a conceptual tree structure. This
	 * index is carried along with the event and used at various points to drive
	 * iteration and ensure that separate jobs are kept that way
	 */
	public final int[] getIndex() {
		return this.index;
	}

	/**
	 * Helper method for implementations of popOwningProcess, this constructs
	 * the appropriate process identifier after the leaf has been removed and
	 * returns it. If there is no leaf to remove, i.e. the current process
	 * identifier is the empty string, then ProcessIdentifierException is thrown
	 * 
	 * @return
	 * @throws ProcessIdentifierException
	 */
	protected final String popOwner() throws ProcessIdentifierException {
		// Empty string already, can't pop from here, throw exception
		if (owner.isEmpty())
			throw new ProcessIdentifierException(
					"Attempt to pop a null owning process (empty string)");
		// A single ID with no colon in, return the empty string
		if (owner.lastIndexOf(':') < 0)
			return "";
		return owner.substring(0, owner.lastIndexOf(':'));
	}

	/**
	 * Helper method for implementations of pushOwningProcess, appends the
	 * specified local name to the current owning process identifier and returns
	 * the new id. This doesn't change the current process identifier. If there
	 * is a colon ':' in the specified name this is invalid and will throw
	 * ProcessIdentifierException at you.
	 * 
	 * @param newLocalProcess
	 * @return
	 * @throws ProcessIdentifierException
	 */
	protected final String pushOwner(String newLocalProcess)
			throws ProcessIdentifierException {
		if (newLocalProcess.contains(":"))
			throw new ProcessIdentifierException("Can't push '"
					+ newLocalProcess + "' as it contains a ':' character");
		if (owner.isEmpty())
			// If the owner was the empty string we don't need to append the
			// colon
			return newLocalProcess;
		return owner + ":" + newLocalProcess;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(' ');
		sb.append(owner);
		sb.append('[');
		for (int i : index) {
			sb.append(i);
			sb.append(" ");
		}
		sb.append(']');
		return sb.toString();
	}
}
