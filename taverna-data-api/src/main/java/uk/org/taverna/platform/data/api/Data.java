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
package uk.org.taverna.platform.data.api;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * The data output by a workflow port.
 *
 * @author David Withers
 */
public interface Data {

	public String getID();
	
	public DataService getDataService();
	
	public boolean hasDataNature(DataNature nature);
	
	public DataNature getDataNature();

	public abstract void setDataNature(DataNature dataNature) throws IOException;

	/**
	 * Returns the depth of the data. Depth 0 is a single value, depth 1 is a list, depth 2 a list
	 * of list, etc.
	 *
	 * @return the depth of the data
	 */
	public int getDepth();

	/**
	 * Returns true if a reference to an external source for the data is available. If the Data is not a Text or BinaryValue then this will return false.
	 *
	 * @return true if a reference to an external source for the data is available
	 */
	public boolean hasReferences();

	/**
	 * Returns references to external sources for the data. Returns an empty set if no
	 * reference is available. There is no guarantee that the returned references will be resolvable
	 * nor that they will resolve to the same object returned by getExplicitValue().
	 *
	 * @return a set of references to external sources for the data
	 * @throws IOException 
	 */
	public Set<DataReference> getReferences() throws IOException;
	
	public void setReferences(Set<DataReference> references);
	
	/**
	 * Returns true if a value for the data is explicitly specified.
	 *
	 * @return true if a value for the data is explicitly specified.
	 */
	public boolean hasExplicitValue();

	/**
	 * Returns the explicit value of this Data object. It returns null if the Data is not a Text or BinaryValue, or if there is no explicit value
	 *
	 * @return
	 */
	public Object getExplicitValue();
	
	public void setExplicitValue(Object value) throws IOException;

	/**
	 * Returns a list of Data elements if the depth is > 0. If the depth is 0 or the Data does not have any value, <code>null</code> is
	 * returned.
	 *
	 * @return a list of Data elements
	 */
	public List<Data> getElements();

	public void setElements(List<Data> elements);
	
	/**
	 * Returns the approximate size of the data in bytes. -1 if unknown or irrelevant
	 */
	public long getApproximateSizeInBytes();
	
	public void setApproximateSizeInBytes(long size) throws IOException;

	public DataLocation getLocation();

}