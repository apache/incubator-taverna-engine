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

package org.apache.taverna.platform.execution.impl.local;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.databundle.DataBundles;

/**
 * @author David Withers
 */
public class T2ReferenceConverter {
	public static Object convertPathToObject(Path path) throws IOException {
		Object object = null;
		if (DataBundles.isValue(path)) {
			object = DataBundles.getStringValue(path);
		} else if (DataBundles.isReference(path)) {
			URI reference = DataBundles.getReference(path);
			String scheme = reference.getScheme();
			if ("file".equals(scheme)) {
				object = new File(reference);
			} else {
				object = reference.toURL();
			}
		} else if (DataBundles.isList(path)) {
			List<Path> list = DataBundles.getList(path);
			List<Object> objectList = new ArrayList<Object>(list.size());
			for (Path pathElement : list) {
				objectList.add(convertPathToObject(pathElement));
			}
			object = objectList;
		}
		return object;
	}
}
