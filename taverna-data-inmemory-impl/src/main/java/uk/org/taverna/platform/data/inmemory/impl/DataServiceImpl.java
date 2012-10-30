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
package uk.org.taverna.platform.data.inmemory.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.DataLocation;
import uk.org.taverna.platform.data.api.DataNature;
import uk.org.taverna.platform.data.api.DataReference;
import uk.org.taverna.platform.data.api.DataService;

/**
 * Basic implementation of a DataService that stores data in-memory.
 *
 * @author David Withers
 */
public class DataServiceImpl implements DataService {
	
	private URI uri;
	private static Logger logger = Logger.getLogger(DataServiceImpl.class.getName());

	public DataServiceImpl() {
		super();
		try {
			this.uri = new URI("http://www.taverna.org.uk" + UUID.randomUUID().toString());
		} catch (URISyntaxException e) {
			logger.severe(e.getMessage());
		}
	}

	private Map<String, Data> store = new HashMap<String, Data>();

	@Override
	public Data get(String ID) {
		return store.get(ID);
	}

	@Override
	public boolean delete(String ID) {
		return store.remove(ID) != null;
	}
	
	@Override
	public Data create(DataNature nature) {
		return create(nature, UUID.randomUUID().toString());
	}

	@Override
	public Data create(DataNature nature, String id) {
		return store(new DataImpl(this, id, nature));
	}

	private Data store(Data data) {
		store.put(data.getID(), data);
		return data;
	}

	@Override
	public DataReference createDataReference() {
		return new DataReferenceImpl(UUID.randomUUID().toString());
	}

	@Override
	public URI getURI() {
		return uri;
	}

	@Override
	public DataLocation getDataLocation(Data data) {
		return new DataLocation(getURI(), data.getID());
	}

	@Override
	public Data get(DataLocation dl) {
		if (!dl.getDataServiceURI().equals(getURI())) {
			return null;
		}
		return get(dl.getDataID());
	}

}
