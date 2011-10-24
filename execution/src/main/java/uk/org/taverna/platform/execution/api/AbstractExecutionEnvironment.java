/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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
package uk.org.taverna.platform.execution.api;

import java.net.URI;

import net.sf.taverna.t2.reference.ReferenceService;

/**
 * A common super type for concrete implementations of <code>ExecutionEnvironment</code>s.
 *
 * @author David Withers
 */
public abstract class AbstractExecutionEnvironment implements ExecutionEnvironment {

	private final String ID;
	private final String name;
	private final String description;
	private final ExecutionService executionService;
	private final ReferenceService referenceService;

	public AbstractExecutionEnvironment(String ID, String name, String description,
			ExecutionService executionService, ReferenceService referenceService) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.executionService = executionService;
		this.referenceService = referenceService;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ExecutionService getExecutionService() {
		return executionService;
	}

	@Override
	public ReferenceService getReferenceService() {
		return referenceService;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ID + "\n");
		sb.append(name + "\n");
		sb.append(description + "\n");
		sb.append("Activities : \n");
		for (URI uri : getActivityURIs()) {
			sb.append("  " + uri + "\n");
		}
		sb.append("Dispatch Layers : \n");
		for (URI uri : getDispatchLayerURIs()) {
			sb.append("  " + uri + "\n");
		}
		return sb.toString();
	}

}
