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
package uk.org.taverna.configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An interface that defines an Object as being configurable.
 * It supports the core properties that allows this items configuration to be stored and re-populated by the ConfigurationManager
 *
 * @author Stuart Owen
 *
 */
public interface Configurable {
	
	/**
	 * @return a Map containing the default value/key pairs of the configured properties
	 */
	Map<String,String> getDefaultPropertyMap();
	/**
	 * @return a globally unique identifier that ensures that when stored this items configuration details will never clash with another
	 */
	String getUUID();
	
	/**
	 * @return a friendly name for the item
	 */
	String getDisplayName();
	
	/**
	 * return a file-system suitable prefix
	 */
	String getFilePrefix();
	
	/**
	 * @return a String defining the category of configurations that this item belongs to.
	 */
	String getCategory();
	/**
	 * Restore the default property map
	 */
	void restoreDefaults();
	
	/**
	 * Provides the default property for a given key
	 * 
	 * @param key
	 * @return
	 */
	String getDefaultProperty(String key);
	
	Set<String> getKeys();
	
	void clear();
	
	/**
	 * Provides access to the internal map.
	 * <br>
	 * Note that this map may contain internal identifiers for deleted entries for deleted values that also have corresponding default values.
	 * For this reason using this map directly is discouraged, and  #getProperty(String)} should be used instead.
	 * @return
	 */
	Map<String, String> getInternalPropertyMap();
	
	/**
	 * Looks up the property for the given key. 
	 * <br>
	 * Using this method is preferable to using the property map directly.
	 * @param key
	 * @return the String represented by the key, the default value, or null
	 */
	String getProperty(String key);
	
	/**
	 * Overwrites or applies a new value against the given key in the property map.
	 * <br>
	 * Setting a value to null is equivalent to calling this{@link #deleteProperty(String)}
	 * <br>
	 * If the value is new, or changed, the the property map is stored.
	 * <br>
	 * Using this method is preferable to using the property map directly.
	 * @param key
	 * @param value
	 */
	void setProperty(String key, String value);
	
	/**
	 * Deletes a property value for a given key.
	 * <br>
	 * Subsequent calls to this{@link #getProperty(String)} will return null.
	 * @param key
	 */
	void deleteProperty(String key);
	
	public List<String> getPropertyStringList(String key);
	
	public void setPropertyStringList(String key, List<String>value);
}
