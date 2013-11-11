/*******************************************************************************
 * Copyright (C) 2013 The University of Manchester
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
package uk.org.taverna.platform.report;

import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.org.taverna.scufl2.api.port.Port;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single invocation of a workflow, processor or activity.
 *
 * @author David Withers
 */
public class Invocation implements Comparable<Invocation> {

	private final String name;

	private final int[] index;

	private final Invocation parent;

	private State state;

	private Date startedDate, completedDate;

	private final SortedSet<Invocation> invocations;

	private final StatusReport<?, ?> report;

	private SortedMap<String, Path> inputs, outputs;

	public Invocation(String name, Invocation parent, StatusReport<?, ?> report) {
		this(name, new int[0], parent, report);
	}

	public Invocation(String name, int[] index, Invocation parent, StatusReport<?, ?> report) {
		this.name = name;
		this.index = index;
		this.parent = parent;
		if (parent != null) {
			parent.getInvocations().add(this);
		}
		this.report = report;
		report.addInvocation(this);

		invocations = new TreeSet<>();

		inputs = new TreeMap<>();
		for (Port port : report.getSubject().getInputPorts()) {
			inputs.put(port.getName(), null);
		}

		outputs = new TreeMap<>();
		for (Port port : report.getSubject().getOutputPorts()) {
			outputs.put(port.getName(), null);
		}

		setStartedDate(new Date());
	}

	/**
	 * Returns the name for this invocation.
	 *
	 * @return the name for this invocation
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public int[] getIndex() {
		return index;
	}

	/**
	 * Returns the  identifier for this invocation by prepending the identifier of the parent
	 * invocation.
	 *
	 * @return the identifier for this invocation
	 */
	@JsonProperty("id")
	public String getId() {
		if (parent != null) {
			String parentId = parent.getId();
			if (parentId != null && !parentId.isEmpty()) {
				return parent.getId() + "/" + name;
			}
		}
		return name;
	}

	@JsonIgnore
	public StatusReport<?, ?> getReport() {
		return report;
	}

	/**
	 * Returns the parent invocation.
	 * <p>
	 * Returns <code>null</code> if there is no parent invocation.
	 *
	 * @return the parent invocation
	 */
	@JsonIgnore
	public Invocation getParent() {
		return parent;
	}

	@JsonProperty("parent")
	public String getParentId() {
	    if (parent == null) {
	        return null;
	    }
	    return parent.getId();
	}

	/**
	 * Returns the child invocations.
	 * <p>
	 * Returns and empty set if there are no child invocations.
	 *
	 * @return the child invocations
	 */
	@JsonIgnore
	public SortedSet<Invocation> getInvocations() {
		return invocations;
	}

	/**
	 * Returns a map of input port names to values.
	 * <p>
	 * Returns an empty map if there are no input ports. If there is no value for an input port the
	 * map will contain a <code>null</code> value.
	 *
	 * @return a map of input port names to values
	 */
	public SortedMap<String, Path> getInputs() {
		return inputs;
	}

	/**
	 * Sets the values of input ports.
	 *
	 * @param inputs
	 *            the values of input ports
	 */
	public void setInputs(Map<String, Path> inputs) {
		this.inputs.putAll(inputs);
	}

	/**
	 * Sets the value of an input port.
	 *
	 * @param port the port name
	 * @param value the port value
	 */
	public void setInput(String port, Path value) {
		inputs.put(port, value);
	}

	/**
	 * Returns a map of output port names to values.
	 * <p>
	 * Returns an empty map if there are no output ports. If there is no value for an output port
	 * the map will contain a <code>null</code> value.
	 *
	 * @return a map of input port names to values
	 */
	public SortedMap<String, Path> getOutputs() {
		return outputs;
	}

	/**
	 * Sets the values of input ports.
	 *
	 * @param inputs
	 *            the values of input ports
	 */
	public void setOutputs(Map<String, Path> outputs) {
		this.outputs.putAll(outputs);
	}

	/**
	 * Sets the value of an output port.
	 *
	 * @param port the port name
	 * @param value the port value
	 */
	public void setOutput(String port, Path value) {
		outputs.put(port, value);
	}

	/**
	 * Returns the current {@link State} of the invocation.
	 * <p>
	 * An invocation state can be RUNNING or COMPLETED.
	 *
	 * @return the current <code>State</code>
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns the date that the status changed to RUNNING.
	 * <p>
	 * If the status has never been RUNNING <code>null</code> is returned.
	 *
	 * @return the date that the status changed to started
	 */
	public Date getStartedDate() {
		return startedDate;
	}

	/**
	 * Sets the date that the status changed to RUNNING.
	 *
	 * @param startedDate
	 *            the date that the status changed to RUNNING
	 */
	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
		state = State.RUNNING;
	}

	/**
	 * Returns the date that the status changed to COMPLETED.
	 * <p>
	 * If the status never been COMPLETED <code>null</code> is returned.
	 *
	 * @return the date that the status changed to COMPLETED
	 */
	public Date getCompletedDate() {
		return completedDate;
	}

	/**
	 * Sets the date that the status changed to COMPLETED.
	 *
	 * @param completedDate
	 *            the date that the status changed to COMPLETED
	 */
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
		state = State.COMPLETED;
	}

	@Override
	public String toString() {
		return "Invocation " + indexToString(index);
	}

	@Override
	public int compareTo(Invocation o) {
		String id = getId();
		String otherId = o.getId();
		if (id.length() == otherId.length()) {
			return id.compareTo(otherId);
		} else  {
			return id.length() - otherId.length();
		}
	}

	private String indexToString(int[] index) {
		StringBuilder indexString = new StringBuilder();
		for (int i = 0; i < index.length; i++) {
			if (i != 0) {
				indexString.append(":");
			}
			indexString.append(index[i] + 1);
		}
		return indexString.toString();
	}

}
