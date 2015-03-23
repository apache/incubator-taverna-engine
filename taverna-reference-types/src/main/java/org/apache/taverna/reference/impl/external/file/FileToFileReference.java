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
import java.io.IOException;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ValueToReferenceConversionException;
import org.apache.taverna.reference.ValueToReferenceConverterSPI;

/**
 * Converts java.lang.File instances to FileReference reference type
 * 
 * @author Tom Oinn
 */
public class FileToFileReference implements ValueToReferenceConverterSPI {
	/*
	 * TODO - should probably do more sophisticated checks such as whether the
	 * file is a file or directory etc etc, for now just checks whether the
	 * specified object is a java.io.File
	 */
	@Override
	public boolean canConvert(Object o, ReferenceContext context) {
		return (o instanceof File);
	}

	/**
	 * Return a FileReference
	 */
	@Override
	public ExternalReferenceSPI convert(Object o, ReferenceContext context)
			throws ValueToReferenceConversionException {
		FileReference result = new FileReference();
		try {
			result.setFilePath(((File) o).getCanonicalPath());
		} catch (IOException ioe) {
			throw new ValueToReferenceConversionException(ioe);
		}
		return result;
	}
}
