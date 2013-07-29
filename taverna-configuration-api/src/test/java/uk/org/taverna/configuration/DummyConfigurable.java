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
/**
 *
 */
package uk.org.taverna.configuration;

import java.util.HashMap;
import java.util.Map;

import uk.org.taverna.configuration.AbstractConfigurable;
import uk.org.taverna.configuration.ConfigurationManager;

public class DummyConfigurable extends AbstractConfigurable {

	public DummyConfigurable(ConfigurationManager configurationManager) {
		super(configurationManager);
	}

	Map<String,String> defaults = null;

	public String getCategory() {
		return "test";
	}

	public Map<String, String> getDefaultPropertyMap() {
		if (defaults==null) {
			defaults = new HashMap<String, String>();
			defaults.put("name","john");
			defaults.put("colour","blue");
		}
		return defaults;
	}

	public String getUUID() {
		return "cheese";
	}

	public String getDisplayName() {
		return "dummyName";
	}

	public String getFilePrefix() {
		return "dummyPrefix";
	}

}
