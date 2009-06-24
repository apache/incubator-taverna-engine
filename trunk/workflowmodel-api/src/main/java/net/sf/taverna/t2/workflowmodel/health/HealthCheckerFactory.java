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

import java.util.ArrayList;
import java.util.List;


/**
 * A factory class that performs a discovery of available HealthCheckers that can handle a given Object.
 * <br>
 * 
 * 
 * @author Stuart Owen
 * @see HealthReport
 * @see HealthChecker
 */
public class HealthCheckerFactory {
	
	private static HealthCheckerFactory instance = new HealthCheckerFactory();
	private HealthCheckerRegistry registry = new HealthCheckerRegistry(); 
	
	private HealthCheckerFactory() {
		
	}
	
	/**
	 * @return a singleton instance of the HealthCheckerFactory.
	 */
	public static HealthCheckerFactory getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param subject the Object for which to discover HealthCheckers
	 * @return a list of HealthCheckers that can handle the subject.
	 */
	public List<HealthChecker<?>> getHealthCheckersForObject(Object subject) {
		List<HealthChecker<?>> result = new ArrayList<HealthChecker<?>>();
		for (HealthChecker<?> checker : registry.getInstances()) {
			if (checker.canHandle(subject)) {
				result.add(checker);
			}
		}
		return result;
	}
}
