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
package net.sf.taverna.t2.reference.h3;

/**
 * A marker used to denote that the class should be pre-loaded into hibernate's
 * class mapping. Used for component classes which are not going to be mapped to
 * the RDBMS but which must be loadable for mapped classes to instantiate
 * correctly. Basically if you refer to a class that isn't itself going to be
 * mapped in hibernate within a mapping definition you'll need to add that
 * component class to this SPI or hibernate won't be able to find it as it won't
 * know that it should associate it with the appropriate class loader.
 * <p>
 * This should be used as an SPI marker, and set as the first SPI registry in
 * the preloadRegistries property of the SpiRegistryAwareLocalSessionFactoryBean
 * 
 * @author Tom Oinn
 * 
 */
public interface HibernateComponentClass {

}
