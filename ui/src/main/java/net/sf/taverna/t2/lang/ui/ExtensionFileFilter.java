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
/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package net.sf.taverna.t2.lang.ui;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * A FileFilter implementation that can be configured to show only specific file
 * suffixes.
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 */
public class ExtensionFileFilter extends FileFilter {
	List<String> allowedExtensions;

	public ExtensionFileFilter(List<String> allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
	}

	public ExtensionFileFilter(String[] allowedExtensions) {
		this.allowedExtensions = Arrays.asList(allowedExtensions);
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = getExtension(f);
		if (extension != null) {
			for (String allowedExtension : allowedExtensions) {
				if (extension.equalsIgnoreCase(allowedExtension)) {
					return true;
				}
			}
		}
		return false;
	}

	String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	@Override
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Filter for extensions : " );
		for (int i = 0; i < allowedExtensions.size(); i++) {
			sb.append(allowedExtensions.get(i));
			if (i < allowedExtensions.size() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
