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
import java.util.List;

import net.sf.taverna.t2.reference.ReferenceService;

/**
 * The ExecutionEnvironment specifies the capabilities of a workflow execution environment.
 *
 * @author David Withers
 */
public interface ExecutionEnvironment {

	/**
	 * Returns the identifier for this ExecutionEnvironment.
	 *
	 * @return the identifier for this ExecutionEnvironment
	 */
	public String getID();

	/**
	 * Returns the name of this ExecutionEnvironment.
	 *
	 * @return the name of this ExecutionEnvironment
	 */
	public String getName();

	/**
	 * Returns a description of this ExecutionEnvironment.
	 *
	 * @return a description of this ExecutionEnvironment
	 */
	public String getDescription();


	/**
	 * Returns the ExecutionService that provides this ExecutionEnvironment.
	 *
	 * @return the ExecutionService that provides this ExecutionEnvironment
	 */
	public ExecutionService getExecutionService();

	/**
	 * Returns a list URI's that identify activities available in this ExecutionEnvironment.
	 *
	 * @return a list URI's that identify activities available in this ExecutionEnvironment
	 */
	public List<URI> getActivityURIs();

	/**
	 * Returns true iff an activity exists for the specified URI in this ExecutionEnvironment.
	 *
	 * @param uri
	 *            the activity URI to check
	 * @return true if an activity exists for the specified URI in this ExecutionEnvironment
	 */
	public boolean activityExists(URI uri);

	/**
	 * Returns a list URI's that identify dispatch layers available in this ExecutionEnvironment.
	 *
	 * @return a list URI's that identify dispatch layers available in this ExecutionEnvironment
	 */
	public List<URI> getDispatchLayerURIs();

	/**
	 * Returns true iff a dispatch layer exists for the specified URI in this ExecutionEnvironment.
	 *
	 * @param uri
	 *            the dispatch layer URI to check
	 * @return true if a dispatch layer exists for the specified URI in this ExecutionEnvironment
	 */
	public boolean dispatchLayerExists(URI uri);

	public ReferenceService getReferenceService();

}
