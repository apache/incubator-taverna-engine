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

package org.apache.taverna.reference;

import static org.apache.taverna.reference.ReferencedDataNature.*;

/**
 * A trivial implementation of ExternalReference. This abstract class should be
 * used as the superclass of any ExternalReference implementations as it
 * provides base metadata for the hibernate-based persistence system used by the
 * main reference manager implementation. While the interface contract cannot
 * require this your extensions will likely not work properly unless you use
 * this class.
 * 
 * @author Tom Oinn
 */
public abstract class AbstractExternalReference implements ExternalReferenceSPI {
	// Used internally by Hibernate for this class and subclasses
	private int primaryKey;

	/**
	 * Used by Hibernate internally to establish a foreign key relationship
	 * between this abstract superclass and tables corresponding to
	 * implementations of the ExternalReference interface. Has no impact on any
	 * application level code, this method is only ever used by the internals of
	 * the hibernate framework.
	 */
	public final void setPrimaryKey(int newKey) {
		this.primaryKey = newKey;
	}

	/**
	 * Used by Hibernate internally to establish a foreign key relationship
	 * between this abstract superclass and tables corresponding to
	 * implementations of the ExternalReference interface. Has no impact on any
	 * application level code, this method is only ever used by the internals of
	 * the hibernate framework.
	 */
	public final int getPrimaryKey() {
		return this.primaryKey;
	}

	/**
	 * Default to returning DataReferenceNature.UNKNOWN
	 */
	@Override
	public ReferencedDataNature getDataNature() {
		return UNKNOWN;
	}

	/**
	 * Default to returning null for charset
	 */
	@Override
	public String getCharset() {
		return null;
	}

	/**
	 * Default to a value of 0.0f for the resolution cost, but implementations
	 * should at least attempt to set this to a more sensible level!
	 */
	@Override
	public float getResolutionCost() {
		return 0.0f;
	}

	@Override
	public abstract ExternalReferenceSPI clone()
			throws CloneNotSupportedException;
}
