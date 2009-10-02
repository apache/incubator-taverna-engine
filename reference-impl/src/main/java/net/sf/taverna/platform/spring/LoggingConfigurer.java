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
package net.sf.taverna.platform.spring;

import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple bean that can be used to set logging options - not really worth
 * bothering with at the moment, use the log4j.xml instead.
 * 
 * @author Tom Oinn
 * 
 */
public class LoggingConfigurer {

	@SuppressWarnings("unchecked")
	public LoggingConfigurer(Properties props) {
		Iterator i = props.keySet().iterator();
		while (i.hasNext()) {
			String loggerName = (String) i.next();
			String levelName = props.getProperty(loggerName);
			try {
				Level level = Level.parse(levelName);
				Logger l = Logger.getLogger(loggerName);
				l.setLevel(level);
			} catch (IllegalArgumentException e) {
				System.err.println("WARNING: Unable to parse '" + levelName
						+ "' as a java.util.Level for logger " + loggerName
						+ "; ignoring...");
			}
		}
	}

}
