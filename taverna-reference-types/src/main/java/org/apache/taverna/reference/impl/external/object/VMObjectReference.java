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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.taverna.reference.AbstractExternalReference;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceContext;

/**
 * Implementation of ExternalReferenceSPI used to refer to objects in the local
 * virtual machine.
 * 
 * @author Stian Soiland-Reyes
 * @author Alex Nenadic
 */
public class VMObjectReference extends AbstractExternalReference implements
		ExternalReferenceSPI, Serializable {
	private static final long serialVersionUID = 6708284419760319684L;
	private static final Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * Mapping from objects to their UUIDs.
	 */
	private static Map<Object, UUID> objectToUUID = new HashMap<>();
	/**
	 * Mapping from UUIDs to objects.
	 */
	private static Map<UUID, Object> uuidToObject = new HashMap<>();

	/**
	 * Unique reference to the object.
	 */
	private String uuid;

	@Override
	public InputStream openStream(ReferenceContext context) {
		return new ByteArrayInputStream(getObject().toString().getBytes(UTF8));
	}

	/**
	 * Getter used by hibernate to retrieve the object uuid property.
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Setter used by hibernate to set the object uuid property.
	 */
	public void setUuid(String id) {
		if (uuid != null)
			throw new IllegalStateException("Can't set UUID of an object twice");
		this.uuid = id;
	}

	public void setObject(Object object) {
		if (uuid != null)
			throw new IllegalStateException("Can't set UUID an object twice");
		UUID knownUUID = objectToUUID.get(object);
		if (knownUUID == null) {
			// register object
			knownUUID = UUID.randomUUID();
			objectToUUID.put(object, knownUUID);
			uuidToObject.put(knownUUID, object);
		}
		setUuid(knownUUID.toString());
	}

	public Object getObject() {
		return uuidToObject.get(UUID.fromString(uuid));
	}

	@Override
	public Long getApproximateSizeInBytes() {
		// We do not know the object size
		return new Long(-1);
	}

	@Override
	public VMObjectReference clone() {
		VMObjectReference result = new VMObjectReference();
		result.setUuid(this.getUuid());
		return result;
	}
}
