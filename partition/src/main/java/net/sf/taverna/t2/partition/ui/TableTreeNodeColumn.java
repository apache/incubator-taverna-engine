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
package net.sf.taverna.t2.partition.ui;

import java.awt.Color;
import java.awt.Component;

/**
 * A column used in the tree part of the table tree node renderer
 * 
 * @author Tom Oinn
 * 
 */
public interface TableTreeNodeColumn {

	/**
	 * Get a string to use as the header text
	 * 
	 * @return
	 */
	public String getShortName();

	/**
	 * Get a descriptive string for tooltips etc.
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Given a node value render the appropriate property for this column
	 * 
	 * @param value
	 * @return
	 */
	public Component getCellRenderer(Object value);

	/**
	 * Return the width in pixels for this column
	 * 
	 * @return
	 */
	public int getColumnWidth();

	/**
	 * Get a header colour - the actual column colour will be a stripe of the
	 * result of applying the lighter operator twice and once to this colour.
	 */
	public Color getColour();
	
}
