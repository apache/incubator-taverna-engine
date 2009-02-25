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
package net.sf.taverna.t2.partition.algorithms;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.partition.PartitionAlgorithm;
import net.sf.taverna.t2.partition.PropertyExtractorRegistry;

/**
 * Takes a custom search term and checks against the properties eg
 * "operation" of each of the available items. Adds the item to the activity
 * palette if it matches
 * 
 * @author Ian Dunlop
 * 
 */
public class CustomPartitionAlgorithm implements PartitionAlgorithm<Object> {

	private String searchValue;
	private List<String> properties;
	private static String NO_SEARCH = "No match";
	private static String MATCHING_ITEMS = "Activities with a matching property";

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public CustomPartitionAlgorithm() {
		properties = new ArrayList<String>();
	}

	public CustomPartitionAlgorithm(String searchValue) {
		super();
		this.searchValue = searchValue;
		// this.propertyName = propertyName;
		properties = new ArrayList<String>();
	}

	public void addProperty(String propertyValue) {
		properties.add(propertyValue);
	}

	/**
	 * Checks against the items property to see if it contains the search term.
	 * Search each of the properties in {@link #properties} in turn
	 */
	public Object allocate(Object newItem, PropertyExtractorRegistry reg) {
		for (String property : properties) {
			Object propertyValue = reg.getAllPropertiesFor(newItem).get(
					property);
			String itemString = newItem.toString();
			//search all the properties first
			if (propertyValue != null) {
				if (((String) propertyValue).contains(getSearchValue()
						.toLowerCase())) {
					return MATCHING_ITEMS;
				}
			}
			//then the name of the item
			if (itemString.toLowerCase().contains(
					getSearchValue().toLowerCase())) {
				return MATCHING_ITEMS;
			}
		}
		return NO_SEARCH;

	}

	@Override
	public String toString() {
		return "search term=" + this.searchValue;
	}

}
