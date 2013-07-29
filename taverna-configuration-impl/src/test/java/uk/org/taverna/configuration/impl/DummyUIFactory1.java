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
package uk.org.taverna.configuration.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import uk.org.taverna.configuration.Configurable;
import uk.org.taverna.configuration.ConfigurationUIFactory;

public class DummyUIFactory1 implements ConfigurationUIFactory {

	public boolean canHandle(String uuid) {
		return getConfigurable().getUUID().equals(uuid);
	}

	public Configurable getConfigurable() {
		return new DummyConfigurable1();
	}

	public JPanel getConfigurationPanel() {
		return new JPanel();
	}

	static class DummyConfigurable1 implements Configurable {

		public void deleteProperty(String key) {
			// TODO Auto-generated method stub

		}

		public String getCategory() {
			// TODO Auto-generated method stub
			return null;
		}

		public Map<String, String> getDefaultPropertyMap() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getProperty(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		public Map<String, String> getInternalPropertyMap() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<String> getPropertyStringList(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getUUID() {
			return "123";
		}

		public void restoreDefaults() {
			// TODO Auto-generated method stub

		}

		public void setProperty(String key, String value) {
			// TODO Auto-generated method stub

		}

		public void setPropertyStringList(String key, List<String> value) {
			// TODO Auto-generated method stub

		}

		public String getDefaultProperty(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		public void clear() {
			// TODO Auto-generated method stub

		}

		public Set<String> getKeys() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getDisplayName() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getFilePrefix() {
			// TODO Auto-generated method stub
			return null;
		}



	}

}
