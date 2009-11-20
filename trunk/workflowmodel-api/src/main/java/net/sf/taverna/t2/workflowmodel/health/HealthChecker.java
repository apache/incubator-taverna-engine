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
package net.sf.taverna.t2.workflowmodel.health;

/**
 * An SPI interface whose implementation performs a health check on an arbitrary instance.
 * <br>
 * 
 * @author Stuart Owen
 * @author David Withers
 *
 * @param <Type> the type of the item being checked
 */
public interface HealthChecker<Type extends Object> {
	/**
	 * Returns true if the HealthChecker implementation is targeted at the subject being
	 * passed to this method.
	 * @param subject
	 * @return
	 */
	public boolean canHandle(Object subject);
	
	/**
	 * Carries out a health check on the subject, which should already have been determined as being
	 * suitable by a call to canHandle.
	 * 
	 * @param subject
	 * @return a HealthReport giving a summary of the result of the health check.
	 * @see HealthReport
	 */
	public HealthReport checkHealth(Type subject);
}
