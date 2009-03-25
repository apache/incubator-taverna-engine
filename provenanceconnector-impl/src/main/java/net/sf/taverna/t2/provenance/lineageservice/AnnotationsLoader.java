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
package net.sf.taverna.t2.provenance.lineageservice;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author paolo
 *
 */
public class AnnotationsLoader {


	/**
	 * 
	 * @param annotationFile  by convention we use <workflow file name>+"annotations"
	 * @return a map pname -> annotation so that the lineage query alg can use the annotation
	 * when processing pname
	 */
	@SuppressWarnings("unchecked")
	public Map<String,List<String>>  getAnnotations(String annotationFile)  {


		Map<String,List<String>>  procAnnotations = new HashMap<String,List<String>>();

		// load XML file as doc
//		parse the event into DOM
		SAXBuilder  b = new SAXBuilder();
		Document d;

		try {
			d = b.build (new FileReader(annotationFile));

			if (d == null)  return null;
			
			Element root = d.getRootElement();

			// look for all processor elements
			List<Element> processors = root.getChildren();
			
			for (Element el:processors) {
				
				String pName = el.getAttributeValue("name");
				System.out.println("processor name: "+pName);
				
				List<String>  annotations = new ArrayList<String>();
				// extract all annotations for this pname

				List<Element> annotEl = el.getChildren();
				
				for (Element annotElement: annotEl) {
					
					String annot = annotElement.getAttributeValue("type");
					System.out.println("annotation: "+annot);

					// add this annotation
					annotations.add(annot);
				}

				procAnnotations.put(pName, annotations);
				
			}
			

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return procAnnotations;


	}
}
