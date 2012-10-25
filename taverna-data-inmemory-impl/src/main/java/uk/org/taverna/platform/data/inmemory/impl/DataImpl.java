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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.DataLocation;
import uk.org.taverna.platform.data.api.DataNature;
import uk.org.taverna.platform.data.api.DataReference;

/**
 * Implementation of a Data object.
 *
 * @author David Withers
 */
public class DataImpl implements Data {

	private String ID;
	
	private DataNature dataNature;

	private Set<DataReference> references;

	private Object value;

	private List<Data> elements;
	
	private long size = -1;
	
	public DataImpl(String ID, DataNature nature) {
		this.dataNature = nature;
		this.ID = ID;
	}

	public DataImpl(String ID, DataNature nature, Object value) {
		this(ID, nature);
		this.value = value;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public int getDepth() {
		int depth = 0;
		if (elements != null) {
			depth++;
			if (!elements.isEmpty()) {
				depth += elements.get(0).getDepth();
			}
		}
		return depth;
	}

	@Override
	public boolean hasReferences() {
		return (references != null) && !references.isEmpty();
	}

	@Override
	public Object getExplicitValue() {
		return value;
	}

	@Override
	public List<Data> getElements() {
		return elements;
	}

	public Set<DataReference> getReferences() {
		return references;
	}

	@Override
	public DataNature getDataNature() {
		return dataNature;
	}

	@Override
	public boolean hasExplicitValue() {
		return getExplicitValue() != null;
	}

	@Override
	public boolean hasDataNature(DataNature nature) {
		return getDataNature().equals(nature);
	}

	public void setDataNature(DataNature dataNature) throws IOException {
		this.dataNature = dataNature;
	}

	@Override
	public void setReferences(Set<DataReference> references) {
		this.references = references;
	}

	@Override
	public void setExplicitValue(Object value) throws IOException {
		this.value = value;
	}

	@Override
	public void setElements(List<Data> elements) {
		this.elements = elements;
	}

	@Override
	public long getApproximateSizeInBytes() {
		return size;
	}

	@Override
	public void setApproximateSizeInBytes(long size) throws IOException {
		this.size = size;
	}

}
