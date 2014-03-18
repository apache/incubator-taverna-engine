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
package net.sf.taverna.t2.lang.ui.icons;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class Icons {
	
	private static Logger logger = Logger.getLogger(Icons.class);



	public static ImageIcon okIcon;
	public static ImageIcon severeIcon;
	public static ImageIcon warningIcon;

	static {
		try {
			Class c = Icons.class;
			okIcon = new ImageIcon(c.getResource("ok.png"));
			severeIcon = new ImageIcon(c.getResource("severe.png"));
			warningIcon = new ImageIcon(c.getResource("warning.png"));

		} catch (Exception ex) {
			logger.error("Unable to load standard icons", ex);
		}
	}
}
