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
package net.sf.taverna.t2.reference;

import java.util.List;

/**
 * Many operations over the reference manager require access to an appropriate
 * context. The context contains hooks out to platform level facilities such as
 * the security agent framework (when used in conjunction with the enactor).
 * <p>
 * This interface is also used to pass in resources required by the external
 * reference translation and construction SPIs. An example might be a translator
 * from File to URL could work by copying the source file to a web share of some
 * kind, but obviously this can't happen unless properties such as the location
 * of the web share folder are known. These properties tend to be properties of
 * the installation rather than of the code, referring as they do to resources
 * on the machine hosting the reference manager (and elsewhere).
 * <p>
 * Where entities in the context represent properties of the platform rather
 * than the 'session' they are likely to be configured in a central location
 * such as a Spring context definition, this interface is neutral to those
 * concerns.
 * 
 * @author Tom Oinn
 * 
 */
public interface ReferenceContext {

	/**
	 * Return a list of all entities in the resolution context which match the
	 * supplied entity type argument.
	 * 
	 * @param <T>
	 *            The generic type of the returned entity list. In general the
	 *            compiler is smart enough that you don't need to specify this,
	 *            it can pick it up from the entityType parameter.
	 * @param entityType
	 *            Class of entity to return. Use Object.class to return all
	 *            entities within the reference context
	 * @return a list of entities from the reference context which can be cast
	 *         to the specified type.
	 */
	public <T extends Object> List<T> getEntities(Class<T> entityType);

	/**
	 * Add an entity to the context.
	 */
	public void addEntity(Object entity);
}
