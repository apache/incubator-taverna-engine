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
package uk.org.taverna.platform.data;

import java.net.URI;
import java.util.List;

/**
 *
 *
 * @author David Withers
 */
public interface Data {

	public String getID();

	/**
	 * The depth of the data. Depth 0 is a single value, depth 1 is a list, depth 2 a list of list,
	 * etc.
	 *
	 * @return
	 */
	public int getDepth();

	/**
	 * Returns true if a reference to an external source for the data is available.
	 *
	 * @return true if a reference to an external source for the data is available
	 */
	public boolean isReference();

	public boolean isError();

	/**
	 * A reference to an external source for the data.
	 *
	 * @return
	 */
	public URI getReference();

	public Object getValue();

	public Data getContainer();

	public List<Data> getElements();

}