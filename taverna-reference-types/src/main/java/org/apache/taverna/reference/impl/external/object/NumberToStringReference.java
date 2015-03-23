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

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ValueToReferenceConversionException;
import org.apache.taverna.reference.ValueToReferenceConverterSPI;

/**
 * Convert a java.lang.Number to a StringReference.
 * 
 * @author Alex Nenadic
 */
public class NumberToStringReference implements ValueToReferenceConverterSPI {
	/**
	 * Can convert if the object is an instance of java.lang.Number
	 */
	@Override
	public boolean canConvert(Object o, ReferenceContext context) {
		return o instanceof Number;
	}

	/**
	 * Return a new InlineStringReference wrapping the supplied String
	 */
	@Override
	public ExternalReferenceSPI convert(Object o, ReferenceContext context)
			throws ValueToReferenceConversionException {
		InlineStringReference result = new InlineStringReference();
		result.setContents(o.toString());
		return result;
	}
}
