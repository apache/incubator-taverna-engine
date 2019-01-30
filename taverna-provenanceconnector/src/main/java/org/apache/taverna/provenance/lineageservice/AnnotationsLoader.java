/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.lineageservice;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * @author Paolo Missier
 *
 */
public class AnnotationsLoader {
	private static Logger logger = Logger.getLogger(AnnotationsLoader.class);

	/**
	 * @param annotationFile
	 *            by convention we use <workflow file name>+"annotations"
	 * @return a map pname -> annotation so that the lineage query alg can use
	 *         the annotation when processing pname
	 */
	@SuppressWarnings("unchecked")
	public Map<String,List<String>>  getAnnotations(String annotationFile)  {
		Map<String, List<String>> procAnnotations = new HashMap<>();

		// load XML file as doc
//		parse the event into DOM
		SAXBuilder b = new SAXBuilder();

		try {
			Document d = b.build(new FileReader(annotationFile));
			if (d == null)
				return null;

			// look for all processor elements
			for (Element el : (List<Element>) d.getRootElement().getChildren()) {
				String pName = el.getAttributeValue("name");
				logger.info("processor name: " + pName);

				List<String> annotations = new ArrayList<>();
				// extract all annotations for this pname

				for (Element annotElement : (List<Element>) el.getChildren()) {
					String annot = annotElement.getAttributeValue("type");
					logger.info("annotation: " + annot);

					// add this annotation
					annotations.add(annot);
				}

				procAnnotations.put(pName, annotations);
			}
		} catch (JDOMException | IOException e) {
			logger.error("Problem getting annotations from: " + annotationFile,
					e);
		}
		return procAnnotations;
	}
}
