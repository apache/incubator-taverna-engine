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

package org.apache.taverna.reference.impl.external.object;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.apache.taverna.reference.ReferencedDataNature;
import org.apache.taverna.reference.StreamToValueConverterSPI;

/**
 * Builds a VMObjectReference from an InputStream.
 * 
 * @author Alex Nenadic
 */
public class StreamToVMObjectReferenceConverter implements
		StreamToValueConverterSPI<VMObjectReference> {
	@Override
	public Class<VMObjectReference> getPojoClass() {
		return VMObjectReference.class;
	}

	@Override
	public VMObjectReference renderFrom(InputStream stream,
			ReferencedDataNature dataNature, String charset) {
		VMObjectReference vmRef = new VMObjectReference();
		try {
			ObjectInputStream in = new ObjectInputStream(stream);
			vmRef = (VMObjectReference) in.readObject();
			return vmRef;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
