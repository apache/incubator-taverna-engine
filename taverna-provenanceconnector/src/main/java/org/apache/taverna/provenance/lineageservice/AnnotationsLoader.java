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
package org.apache.taverna.provenance.lineageservice;

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


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
