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

/**
 *
 *
 * @author David Withers
 */
public interface DataService {

	/**
	 * Returns the Data for the ID. Returns <code>null</code> if there is no Data for the ID.
	 *
	 * @param ID
	 *            the data identifier
	 * @return
	 */
	public Data get(String ID);

	/**
	 * @param ID
	 * @return
	 */
	public boolean delete(String ID);

	/**
	 * @param value
	 * @return
	 */
	public Data create(Object value);

}