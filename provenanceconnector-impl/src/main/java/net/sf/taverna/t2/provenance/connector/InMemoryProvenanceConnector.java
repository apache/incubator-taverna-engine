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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.output.XMLOutputter;

import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.reference.ReferenceService;

/**
 * Simple {@link ProvenanceConnector} which stores all the
 * {@link ProvenanceItem}s in /tmp/UUID
 * 
 * @author Ian Dunlop
 * 
 */
public class InMemoryProvenanceConnector implements ProvenanceConnector {

	private static Logger logger = Logger
			.getLogger(InMemoryProvenanceConnector.class);

	private ArrayList<ProvenanceItem> provenanceCollection;

	private boolean runStarted = false;

	private String provenance;

	private int storedNumber = 0;

	private UUID randomUUID;

	private ReferenceService referenceService;

	private List<String> idList;

	private String identifier;

	private int eventCnt = 0;

	private File file;

	private String saveEvents;

	public InMemoryProvenanceConnector() {
	}

	public String getProvenance() {
		return provenance;
	}

	public void saveProvenance(String annotation) {
		provenance = annotation;
	}

	public List<ProvenanceItem> getProvenanceCollection() {
		return provenanceCollection;
	}

	@SuppressWarnings("unchecked")
	public synchronized void store(ProvenanceItem provenanceItem) {

	}

	public void createDatabase() {
		// TODO Auto-generated method stub

	}

	public void deleteDatabase() {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return "In Memory";
	}

	public void setDBLocation(String location) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "In Memory Connector - use for testing only";
	}

	public void addProvenanceItem(ProvenanceItem provenanceItem) {
		String asString = provenanceItem.getAsString();
		String eventType = provenanceItem.getEventType();
		if (asString != null) {
			try {
				writeProvenanceToFile(asString, eventType);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			XMLOutputter outputter = new XMLOutputter();

			String outputString = outputter.outputString(provenanceItem
					.getAsXML(referenceService));
			try {
				writeProvenanceToFile(outputString, eventType);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void setSessionId(String identifier) {
		this.identifier = identifier;
	}

	public String getSessionId() {
		return identifier;
	}

	public ReferenceService getReferenceService() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;

	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPassword(String password) {
		// TODO Auto-generated method stub

	}

	public void setUser(String user) {
		// TODO Auto-generated method stub

	}

	public void init() {
		eventCnt = 0;
		idList = new ArrayList<String>();
		provenanceCollection = new ArrayList<ProvenanceItem>();
		setSessionId(UUID.randomUUID().toString());
		file = new File("/tmp/" + identifier);
		try {
			FileUtils.forceMkdir(file);
			logger.info("Logging provenance to: " + file.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void writeProvenanceToFile(String provenance,
			String eventType) throws IOException {

		String fname = "event_" + eventCnt + "_" + eventType + ".xml";
		File f = new File(file, fname);

		FileWriter fw = new FileWriter(f);
		fw.write(provenance);
		fw.flush();
		fw.close();

		eventCnt++;
	}

	public String getdbName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setdbName(String dbName) {
		// TODO Auto-generated method stub

	}

	public boolean isClearDB() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setClearDB(boolean isClearDB) {
		// TODO Auto-generated method stub
		
	}

	public String getIntermediateValues(String wfInstance, String pname,
			String vname, String iteration) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDataflowInstance(String dataflowId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearDatabase() {
		// TODO Auto-generated method stub
		
	}

	public String getSaveEvents() {
		return saveEvents;
	}

	public void setSaveEvents(String saveEvents) {
		this.saveEvents = saveEvents;
		
	}

}
