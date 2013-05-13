/*******************************************************************************
 * Copyright (C) 2013 The University of Manchester
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
package uk.org.taverna.commons.plugin.impl;

import uk.org.taverna.commons.plugin.PluginSite;

/**
 * PluginSite implementation.
 *
 * @author David Withers
 */
public class PluginSiteImpl implements PluginSite {

	private String name, url;

	private PluginSiteType type;

	public PluginSiteImpl() {
	}

	public PluginSiteImpl(String name, String url) {
		this(name, url, PluginSiteType.USER);
	}

	public PluginSiteImpl(String name, String url, PluginSiteType type) {
		this.name = name;
		this.url = url;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public PluginSiteType getType() {
		return type;
	}

	public void setType(PluginSiteType type) {
		this.type = type;
	}

}
