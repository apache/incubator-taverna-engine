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

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Static utility to insert string properties from a Properties object in place
 * of ${property.name} parts of the supplied string.
 * 
 * @author Tom Oinn
 * 
 */
public final class PropertyInterpolator {

	private static String regex = "\\$\\{([\\w\\.]+)\\}";
	private static Pattern pattern;
	private static Log log = LogFactory.getLog(PropertyInterpolator.class);

	static {
		pattern = Pattern.compile(regex);
	}

	/**
	 * Perform property interpolation using the system properties object
	 * 
	 * @param sourceString
	 *            a string containing zero or more ${...} elements corresponding
	 *            to properties to insert
	 * @return the sourceString with property values inserted in place of
	 *         variables *
	 * @throws RuntimeException
	 *             if a referenced property does not exist in the system
	 *             properties
	 */
	public static String interpolate(String sourceString) {
		return interpolate(sourceString, System.getProperties());
	}

	/**
	 * Perform property interpolation using the supplied properties object
	 * 
	 * @param sourceString
	 *            a string containing zero or more ${...} elements corresponding
	 *            to properties to insert
	 * @param props
	 *            the properties object from which property values should be
	 *            extracted
	 * @return the sourceString with property values inserted in place of
	 *         variables
	 * @throws RuntimeException
	 *             if a referenced property does not exist in the supplied
	 *             properties object
	 */
	public static String interpolate(String sourceString, Properties props) {
		Matcher matcher = pattern.matcher(sourceString);
		StringBuffer sb = new StringBuffer();
		int cursor = 0;
		while (matcher.find()) {
			String propertyValue = props.getProperty(matcher.group(1));
			if (propertyValue == null) {
				log.warn("Attempt to interpolate an undefined property '"
						+ matcher.group(1) + "'");
				throw new RuntimeException("Can't locate property '"
						+ matcher.group(1) + "'");
			}
			sb.append(sourceString.substring(cursor, matcher.start()));
			sb.append(propertyValue);
			cursor = matcher.end();
		}
		sb.append(sourceString.substring(cursor));
		return sb.toString();
	}

}
