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
import java.util.List;

import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.ErrorValue;

/**
 * Implementation of a Data object.
 *
 * @author David Withers
 */
public class DataImpl implements Data {

	private String ID;

	private URI reference;

	private Object value;

	private Data container;

	private List<Data> elements;

	private String mimeType;

	public DataImpl(String ID, Object value) {
		this.value = value;
	}

	public DataImpl(String ID, URI reference) {
		this.reference = reference;
	}

	public DataImpl(String ID, URI reference, Object value) {
		this.reference = reference;
		this.value = value;
	}

	public DataImpl(String ID, List<Data> elements) {
		this.elements = elements;
//		for (Data element : elements) {
//			element.container = this;
//		}
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
	public boolean isReference() {
		return reference != null;
	}

	@Override
	public URI getReference() {
		return reference;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Data getContainer() {
		return container;
	}

	@Override
	public List<Data> getElements() {
		return elements;
	}

	@Override
	public boolean isError() {
		return value instanceof ErrorValue;
	}

}
