/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.taverna.reference.impl.external.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.taverna.reference.AbstractExternalReference;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferencedDataNature;

/**
 * Implementation of ExternalReference used to refer to data held in a locally
 * accessible file. Inherits from
 * {@link org.apache.taverna.reference.AbstractExternalReference
 * AbstractExternalReference} to enable hibernate based persistence.
 * 
 */
public class FileReference extends AbstractExternalReference implements
		ExternalReferenceSPI {
	private String filePathString = null;
	private String charset = null;
	private File file = null;
	private String dataNatureName = ReferencedDataNature.UNKNOWN.name();

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
		this.charset = Charset.defaultCharset().name();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
		return "file{" + file.getAbsolutePath() + "}";
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

	@Override
	public Long getApproximateSizeInBytes() {
		return new Long(file.length());
	}

	/**
	 * @return the dataNature
	 */
	@Override
	public ReferencedDataNature getDataNature() {
		return Enum.valueOf(ReferencedDataNature.class, getDataNatureName());
	}

	/**
	 * @param dataNature
	 *            the dataNature to set
	 */
	public void setDataNature(ReferencedDataNature dataNature) {
		setDataNatureName(dataNature.name());
	}

	/**
	 * @return the file
	 */
	public final File getFile() {
		return file;
	}

	@Override
	public float getResolutionCost() {
		return (float) 100.0;
	}

	/**
	 * @return the dataNatureName
	 */
	public String getDataNatureName() {
		return dataNatureName;
	}

	/**
	 * @param dataNatureName
	 *            the dataNatureName to set
	 */
	public void setDataNatureName(String dataNatureName) {
		this.dataNatureName = dataNatureName;
	}

	public void deleteData() {
		try {
			getFile().delete();
		} catch (SecurityException e) {
			// TODO
		}
	}

	@Override
	public FileReference clone() {
		FileReference result = new FileReference();
		result.setFilePath(this.getFilePath());
		result.setCharset(this.getCharset());
		result.setDataNature(this.getDataNature());
		return result;
	}
}
