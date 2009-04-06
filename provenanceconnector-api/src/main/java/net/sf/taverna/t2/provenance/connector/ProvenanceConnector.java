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
package net.sf.taverna.t2.provenance.connector;

import java.sql.SQLException;
import java.util.List;

import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.reference.ReferenceService;

/**
 * Collects {@link ProvenanceItem}s as it travels up and down the dispatch stack
 * inside the InvocationContext
 * 
 * @author Ian Dunlop
 * 
 */
public interface ProvenanceConnector {

	/**
	 * Get all the {@link ProvenanceItem}s that the connector currently knows
	 * about
	 * 
	 * @return
	 */
	public List<ProvenanceItem> getProvenanceCollection();

	/**
	 * Store the {@link ProvenanceItem} using whatever method the implementation
	 * of {@link ProvenanceConnector} implements
	 * 
	 * @param provenanceItem
	 */
	public void store(ProvenanceItem provenanceItem);

	/**
	 * Used by database backed provenance stores. Ask the implementation to
	 * create the database.
	 */
	public void createDatabase();

	/**
	 * Used by database backed provenance stores. Ask the implementation to
	 * delete the database.
	 */
	public void deleteDatabase();
	/**
	 * Clear all the values in the database but keep the db there
	 */
	public void clearDatabase();

	/**
	 * For database backed connectors, pass it the location eg localhost or
	 * somewhere.com. The http:// and database name should be added by the
	 * connector implementation eg http://somewhere.com/T2Provenance
	 * 
	 * @param location
	 */
	public void setDBLocation(String location);

	/**
	 * Add a {@link ProvenanceItem} to the connector
	 * 
	 * @param provenanceItem
	 * @param invocationContext 
	 */
	// FIXME: Have to use Object for invocationContext to avoid Maven loop with workflowmodel-api
	public void addProvenanceItem(ProvenanceItem provenanceItem, Object invocationContext);

	/**
	 * Tell the connector what {@link ReferenceService} it should use when
	 * trying to dereference data items inside {@link ProvenanceItem}s
	 * 
	 * @param referenceService
	 */
	public void setReferenceService(ReferenceService referenceService);

	/**
	 * Get the {@link ReferenceService} in use by this connector
	 * 
	 * @return
	 */
	public ReferenceService getReferenceService();

	/**
	 * The name for this type of provenance connector. Is used by the workbench
	 * to ensure it adds the correct one to the InvocationContext
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * A unique identifier for this run of provenance
	 * 
	 * @param identifier
	 */
	public void setSessionId(String identifier);

	/**
	 * What is the unique identifier used by this connector
	 * 
	 * @return
	 */
	public String getSessionId();

	/**
	 * For database backed connectors, set the user name
	 * 
	 * @param user
	 */
	public void setUser(String user);

	/**
	 * For database backed connectors, set the password
	 * 
	 * @param password
	 */
	public void setPassword(String password);

	/**
	 * Return the user name
	 * 
	 * @return
	 */
	public String getUser();

	/**
	 * Return the password
	 * 
	 * @return
	 */
	public String getPassword();
	
	public boolean isClearDB();

	public void setClearDB(boolean isClearDB);
		

	/**
	 * Set up the database. Since it is an SPI you don't want any code
	 * cluttering the default constructor. Call this method after instantiation
	 * (and probably after user, password and location have been set) instead.
	 */
	public void init();
	
	public String getIntermediateValues(String wfInstance,
			String pname, String vname, String iteration) throws SQLException;
	
	public String getDataflowInstance(String dataflowId);
	
	public boolean isFinished();
	
	
	/**
	 * @return the saveEvents
	 */
	public String getSaveEvents();

	/**
	 * @param saveEvents the saveEvents to set
	 */
	public void setSaveEvents(String saveEvents);


}
