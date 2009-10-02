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

import java.io.File;
import java.io.IOException;

import net.sf.taverna.raven.appconfig.ApplicationRuntime;

/**
 * @author Alan R Williams
 */
public class ApplicationJDBC {
	public String getApplicationDerbyJDBC() throws IOException {

		File applicationHomeDir = ApplicationRuntime.getInstance()
				.getApplicationHomeDir();
		File dbFile = new File(applicationHomeDir, "reference-db");
		deleteDirectory(dbFile);
		if (!dbFile.mkdir()) {
			throw new IOException("Could not create database " + dbFile);
		}
		String dbURL = "jdbc:derby:" + dbFile.toString()
				+ "/database;create=true";
		return dbURL;

	}
	
	// copied from http://www.rgagnon.com/javadetails/java-0483.html
	static public boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }
	
}
