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
package net.sf.taverna.t2.reference.impl.external.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.sf.taverna.t2.reference.AbstractExternalReference;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceContext;

/**
 * Implementation of ExternalReference used to refer to data held in a locally
 * accessible file. Inherits from
 * {@link net.sf.taverna.t2.reference.AbstractExternalReference AbstractExternalReference}
 * to enable hibernate based persistence.
 * 
 * @author Tom Oinn
 * 
 */
public class FileReference extends AbstractExternalReference implements
		ExternalReferenceSPI {

	private String filePathString = null;
	private String charset = null;
	private File file = null;

	/**
	 * Explicitly declare default constructor, will be used by hibernate when
	 * constructing instances of this bean from the database.
	 */
	public FileReference() {
		super();
	}

	/**
	 * Construct a file reference pointed at the specified file and with no
	 * character set defined.
	 */
	public FileReference(File theFile) {
		super();
		this.file = theFile.getAbsoluteFile();
		this.filePathString = this.file.getPath();
		this.charset = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public InputStream openStream(ReferenceContext context) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Setter used by hibernate to set the charset property of the file
	 * reference
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCharset() {
		return this.charset;
	}

	/**
	 * Setter used by hibernate to set the file path property of the file
	 * reference
	 */
	public void setFilePath(String filePathString) {
		this.filePathString = filePathString;
		this.file = new File(filePathString).getAbsoluteFile();
	}

	/**
	 * Getter used by hibernate to retrieve the file path string property
	 */
	public String getFilePath() {
		return this.filePathString;
	}

	/**
	 * Human readable string form for debugging, should not be regarded as
	 * stable.
	 */
	@Override
	public String toString() {
		return "file{"+file.getAbsolutePath()+"}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FileReference other = (FileReference) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

}
