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
package net.sf.taverna.platform.spring.jdbc;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

/**
 * Test {@link TemporaryJDBC}
 * 
 * @author Stian Soiland-Reyes
 *
 */
public class TemporaryJDBCTest {

	private static final String DB = ".db";
	private static final String T2PLATFORM = "t2platform-";
	private static final String CREATE_TRUE = ";create=true";
	private static final String JDBC_DERBY = "jdbc:derby:";

	@Test
	public void getDerby() throws Exception {
		TemporaryJDBC temporaryJDBC = new TemporaryJDBC();
		String jdbcURL = temporaryJDBC.getTemporaryDerbyJDBC();
		assertTrue("Not a Derby URL", jdbcURL.startsWith(JDBC_DERBY));
		String url = jdbcURL.split(JDBC_DERBY)[1];
		assertTrue("Did not end with " + CREATE_TRUE, url.endsWith(CREATE_TRUE));
		String location = url.split(CREATE_TRUE)[0];
		assertFalse("Location was an empty string", location.equals(""));
		File locationFile = new File(location);
		assertFalse("File already exists: " + locationFile, locationFile.exists());
		File parentFile = locationFile.getParentFile();
		assertTrue("Parent directory did not exist", parentFile.isDirectory());
		assertTrue("Did not end with " + T2PLATFORM, parentFile.getName().startsWith(T2PLATFORM));
		assertTrue("Did not start with " + DB , parentFile.getName().endsWith(DB));
	}
	
}
