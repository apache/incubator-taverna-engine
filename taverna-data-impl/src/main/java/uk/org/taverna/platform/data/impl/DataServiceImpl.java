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
package uk.org.taverna.platform.data.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.DataNature;
import uk.org.taverna.platform.data.api.DataReference;
import uk.org.taverna.platform.data.api.DataService;

/**
 * Basic implementation of a DataService that stores data in-memory.
 *
 * @author David Withers
 */
public class DataServiceImpl implements DataService {

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
		return store(new DataImpl(UUID.randomUUID().toString(), nature));
	}

	private Data store(Data data) {
		store.put(data.getID(), data);
		return data;
	}

	@Override
	public DataReference createDataReference() {
		return new DataReferenceImpl(UUID.randomUUID().toString());
	}

}
