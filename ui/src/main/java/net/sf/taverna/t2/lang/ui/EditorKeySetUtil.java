/*******************************************************************************
 * Copyright (C) 2009 Ingo Wassink of University of Twente, Netherlands and
 * The University of Manchester   
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
 * @author Ingo Wassink
 * @author Ian Dunlop
 * @author Alan R Williams
 */
package net.sf.taverna.t2.lang.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * Manager for reading key set file
 * 
 * @author WassinkI
 * @author alanrw
 * 
 */
public class EditorKeySetUtil {
	
	private static Logger logger = Logger.getLogger(EditorKeySetUtil.class);


	public static Set<String> loadKeySet(InputStream stream) {
		Set<String> result = new TreeSet<String>();
				try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(stream));
			                                                     
			String line;
			while ((line = reader.readLine()) != null) {
				result.add(line.trim());
			}
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}
}
