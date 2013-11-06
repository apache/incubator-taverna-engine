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
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.org.taverna.scufl2.api.port.Port;

/**
 * A single invocation of a workflow, processor or activity.
 *
 * @author David Withers
 */
public class Invocation implements Comparable<Invocation> {

	private final String id;

	private final Invocation parent;

	private final SortedSet<Invocation> invocations;

	private final StatusReport<?, ?> report;

	private SortedMap<String, Path> inputs, outputs;

	public Invocation(String id, Invocation parent, StatusReport<?, ?> report) {
		this.id = id;
		this.parent = parent;
		if (parent != null) {
			parent.getInvocations().add(this);
		}
		this.report = report;
		if (report != null) {
			report.getInvocations().add(this);
		}

		invocations = new TreeSet<>();

		inputs = new TreeMap<>();
		for (Port port : report.getSubject().getInputPorts()) {
			inputs.put(port.getName(), null);
		}

		outputs = new TreeMap<>();
		for (Port port : report.getSubject().getOutputPorts()) {
			outputs.put(port.getName(), null);
		}
	}

	/**
	 * Returns the identifier for this invocation.
	 *
	 * @return the identifier for this invocation
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the context identifier for this invocation by prepending the identifier of the parent
	 * invocation.
	 *
	 * @return the context identifier for this invocation
	 */
	public String getContextId() {
		if (parent != null) {
			String parentId = parent.getContextId();
			if (parentId != null && !parentId.isEmpty()) {
				return parent.getContextId() + ":" + id;
			}
		}
		return id;
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
	private SortedSet<Invocation> getInvocations() {
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

	@Override
	public String toString() {
		return "Invocation " + getContextId();
	}

	@Override
	public int compareTo(Invocation o) {
		String thisID = getContextId();
		String otherID = o.getContextId();
		int comparison = thisID.length() - otherID.length();
		if (comparison == 0) {
			return thisID.compareTo(otherID);
		} else {
			return comparison;
		}
	}

}
