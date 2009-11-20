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
package net.sf.taverna.t2.workflowmodel.serialization.xml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.taverna.raven.appconfig.ApplicationRuntime;
import net.sf.taverna.raven.repository.Artifact;
import net.sf.taverna.raven.repository.ArtifactNotFoundException;
import net.sf.taverna.raven.repository.ArtifactStateException;
import net.sf.taverna.raven.repository.BasicArtifact;
import net.sf.taverna.raven.repository.Repository;
import net.sf.taverna.raven.repository.impl.DummyRepository;
import net.sf.taverna.raven.spi.Profile;
import net.sf.taverna.raven.spi.ProfileFactory;
import net.sf.taverna.t2.annotation.Annotated;
import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.impl.EditsImpl;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * An abstract base class that contains deserialisation methods common across all dataflow elements.
 * 
 * @author Stuart Owen
 *
 */
public abstract class AbstractXMLDeserializer implements
		XMLSerializationConstants {

	private static final String T2_ACTIVITIES_GROUPID = "net.sf.taverna.t2.activities";

	private static final String T2_GROUPID = "net.sf.taverna.t2";

	private static Logger logger = Logger.getLogger(AbstractXMLDeserializer.class);
	
	protected Edits edits = new EditsImpl();

	protected Object createBean(Element element, ClassLoader cl) {
		Element configElement;
		
		// Note - don't check for CONFIG_BEAN/T2_WORKFLOW_NAMESPACE as 
		// the annotations are such beans, but they are not in a <configBean> element.
			
		// Instead, we simply check if the "encoding" attribute is there, which it 
		// should be
		if (element.getAttribute(BEAN_ENCODING) != null) {
			configElement = element;
		} else {
			// Find it one element below
			configElement = element
					.getChild(CONFIG_BEAN, T2_WORKFLOW_NAMESPACE);
			if (configElement == null) {
				throw new IllegalArgumentException("Can't find bean element {" + 
						T2_WORKFLOW_NAMESPACE + "}" + CONFIG_BEAN);
			}
		}
		
		String encoding = configElement.getAttributeValue(BEAN_ENCODING);
		Object result = null;
		if (encoding.equals(XSTREAM_ENCODING)) {
			if (configElement.getChildren().isEmpty()) {
				throw new IllegalArgumentException("XStream encoding expected in element");
			}
			Element beanElement = (Element) configElement.getChildren().get(0);
			XStream xstream = new XStream(new DomDriver());
			xstream.setClassLoader(cl);
			result = xstream.fromXML(new XMLOutputter()
					.outputString(beanElement));
		} else if (encoding.equals(JDOMXML_ENCODING)) {
			if (configElement.getChildren().isEmpty()) {
				throw new IllegalArgumentException("XML encoding expected in element");
			}
			result = (Element) configElement.getChildren().get(0);
		//} else if (encoding.equals(DATAFLOW_ENCODING)) {
		//	// Oh noe
		} else {
			throw new IllegalArgumentException("Unknown encoding " + encoding);
		}

		return result;

	}

	protected ClassLoader getRavenLoader(Element element)
			throws ArtifactNotFoundException, ArtifactStateException {
		// Try to get the current Repository object, if there isn't one we can't
		// do this here
		Repository repository = ApplicationRuntime.getInstance().getRavenRepository();
		if (repository instanceof DummyRepository) { 
			return myClassLoader();
			// TODO - should probably warn that this is happening as it's likely
			// to be because of an error in API usage. There are times it won't
			// be though so leave it for now.
		}
		
		Element ravenElement;
		if (element.getName().equals(RAVEN) && element.getNamespace().equals(T2_WORKFLOW_NAMESPACE)) {
			ravenElement = element;			
		} else {
			ravenElement = element.getChild(RAVEN, T2_WORKFLOW_NAMESPACE);
			if (ravenElement == null) {
				// Not found in XML
				return myClassLoader();
			}
		}
		
		String groupId = ravenElement.getChildTextTrim(GROUP,
				T2_WORKFLOW_NAMESPACE);
		String artifactId = ravenElement.getChildTextTrim(ARTIFACT,
				T2_WORKFLOW_NAMESPACE);
		String version = ravenElement.getChildTextTrim(VERSION,
				T2_WORKFLOW_NAMESPACE);
		
		Artifact artifact;
		// Always use the version of the profile
		Profile profile = ProfileFactory.getInstance().getProfile();
		artifact = profile.discoverArtifact(groupId, artifactId, repository);
		if (artifact == null && groupId.equals(T2_GROUPID)) {
			logger.info("Could not find artifact for " + groupId + ":" + artifactId 
					+ ", attempting compatability groupID " + T2_ACTIVITIES_GROUPID);
			artifact = profile.discoverArtifact(T2_ACTIVITIES_GROUPID, artifactId, repository);
		}
		
		if (artifact != null) {
			if (! (artifact.getVersion().equals(version))) {
				String desired = groupId + ":" + artifactId + ":" + version;
				String actual = artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();	
				logger.warn("XML specified " + desired + " instead using from profile " + actual);
				// TODO: pop up an actual warning box
			}
				
		} else {
			logger.warn("Can't discover artifact for " + groupId + ":" + artifactId);
			artifact = new BasicArtifact(groupId, artifactId, version);
		}
		return repository.getLoader(artifact, null);
	}
	
	private ClassLoader myClassLoader() {
		ClassLoader classLoader = getClass().getClassLoader();
		if (classLoader != null) {
			return classLoader;
		}
		return ClassLoader.getSystemClassLoader();
	}

	protected String elementToString(Element element) {
		return new XMLOutputter().outputString(element);
	}
	
	@SuppressWarnings("unchecked")
	protected void annotationsFromXml(Annotated<?> annotated, Element parent,
			ClassLoader cl) {
		Element annotationsElement = parent.getChild(ANNOTATIONS,
				T2_WORKFLOW_NAMESPACE);
		if (annotationsElement != null) {
			Set<AnnotationChain> newAnnotationChains = new HashSet<AnnotationChain>();
			for (Element annotationChainElement : (List<Element>) (annotationsElement
					.getChildren(ANNOTATION_CHAIN, T2_WORKFLOW_NAMESPACE))) {
				if (annotationChainElement == null) {
					logger.info("annotationChainElement is null");
					continue;
				}
				if (cl == null) {
					logger.info("ClassLoader is null");
					continue;
				}
				AnnotationChain ac = (AnnotationChain) createBean(
						annotationChainElement, XMLDeserializerImpl.class
								.getClassLoader());
				if ((ac == null) || (ac.getAssertions() == null)
						|| (ac.getAssertions().size() == 0)) {
					logger.warn("Null or empty annotation chain");
					continue;
				}
				newAnnotationChains.add(ac);

			}
			annotated.setAnnotations(newAnnotationChains);
		}
	}
}
