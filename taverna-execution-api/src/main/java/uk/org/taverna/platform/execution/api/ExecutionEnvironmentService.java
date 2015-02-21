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

import java.util.Set;

import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * Service for finding <code>ExecutionEnvironment</code>s.
 *
 * @author David Withers
 */
public interface ExecutionEnvironmentService {

	/**
	 * Returns the available <code>ExecutionEnvironment</code>s.
	 *
	 * @return the available <code>ExecutionEnvironment</code>s
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments();

	/**
	 * Returns the <code>ExecutionEnvironment</code>s that can execute the specified
	 * <code>Profile</code>.
	 *
	 * @param profile
	 *            the <code>Profile</code> to find <code>ExecutionEnvironment</code>s for
	 * @return the <code>ExecutionEnvironment</code>s that can execute a workflow with the specified
	 *         <code>Profile</code>
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments(Profile profile);

}
