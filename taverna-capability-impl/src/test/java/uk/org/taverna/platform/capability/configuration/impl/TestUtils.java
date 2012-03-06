/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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
package uk.org.taverna.platform.capability.configuration.impl;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;

/**
 *
 *
 * @author David Withers
 */
public class TestUtils {

	public static final String nonAnnotatedBeanURI = "test://ns.taverna.org.uk/configurableThing/nonannotated";
	public static final String annotatedBeanURI = "test://ns.taverna.org.uk/configurableThing/annotated";
	public static final URI testBeanURI = URI.create(annotatedBeanURI + "#Configuration");
	public static final URI testBean2URI = URI.create(annotatedBeanURI + "/configuration2");
	public static final URI subclassTestBeanURI = URI.create(annotatedBeanURI + "/subclass");


	public static Configuration createTestConfiguration() {
		Configuration configuration = new Configuration();
		Profile profile = new Profile();
		profile.setParent(new WorkflowBundle());
		configuration.setParent(profile);

		PropertyResource propertyResource = configuration.getPropertyResource();
		propertyResource.setTypeURI(testBeanURI.resolve("#Configuration"));
		PropertyResource propertyResource2 = new PropertyResource();
		propertyResource2.setTypeURI(testBean2URI);
		propertyResource2.addProperty(testBean2URI.resolve("#stringType"), new PropertyLiteral("string value 2.1"));
		propertyResource2.addProperty(testBean2URI.resolve("#stringType2"), new PropertyLiteral("string value 2.2"));

		PropertyResource propertyResource3 = new PropertyResource();
		propertyResource3.setTypeURI(testBean2URI);
		propertyResource3.addProperty(testBean2URI.resolve("#stringType"), new PropertyLiteral("string value 3.1"));
		propertyResource3.addProperty(testBean2URI.resolve("#stringType2"), new PropertyLiteral("string value 3.2"));

		PropertyResource propertyResource4 = new PropertyResource();
		propertyResource4.setTypeURI(testBean2URI);
		propertyResource4.addProperty(testBean2URI.resolve("#stringType"), new PropertyLiteral("string value 4.1"));
		propertyResource4.addProperty(testBean2URI.resolve("#stringType2"), new PropertyLiteral("string value 4.2"));

		PropertyResource propertyResource5 = new PropertyResource();
		propertyResource5.setTypeURI(subclassTestBeanURI);
		propertyResource5.addProperty(testBean2URI.resolve("#stringType2"), new PropertyLiteral("string value 5.1"));
		propertyResource5.addProperty(subclassTestBeanURI.resolve("#stringType2"), new PropertyLiteral("string value 5.2"));
		propertyResource5.addProperty(subclassTestBeanURI.resolve("#overriding"), new PropertyLiteral("string value 5.3"));


		propertyResource.addProperty(testBeanURI.resolve("#stringType"), new PropertyLiteral("string value"));
		propertyResource.addProperty(testBeanURI.resolve("#optionalStringType"), new PropertyLiteral("optional string value"));
		propertyResource.addProperty(testBeanURI.resolve("#integerType"), new PropertyLiteral(5));
		propertyResource.addProperty(testBeanURI.resolve("#longType"), new PropertyLiteral(12l));
		propertyResource.addProperty(testBeanURI.resolve("#floatType"), new PropertyLiteral(1.2f));
		propertyResource.addProperty(testBeanURI.resolve("#doubleType"), new PropertyLiteral(36.2d));
		propertyResource.addProperty(testBeanURI.resolve("#booleanType"), new PropertyLiteral(false));
		propertyResource.addProperty(testBeanURI.resolve("#enumType"), new PropertyLiteral("A"));

		propertyResource.addProperty(testBeanURI.resolve("#beanType"), propertyResource2);
		propertyResource.addProperty(testBeanURI.resolve("#beanType2"), propertyResource4);
		propertyResource.addProperty(testBeanURI.resolve("#subclass"), propertyResource5);


		PropertyList propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("array element 1"));
		propertyList.add(new PropertyLiteral("array element 2"));
		propertyResource.addProperty(testBeanURI.resolve("#arrayType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("1"));
		propertyList.add(new PropertyLiteral("2"));
		propertyList.add(new PropertyLiteral("3"));
		propertyList.add(new PropertyLiteral("4"));
		propertyResource.addProperty(testBeanURI.resolve("#listType"), propertyList);
		propertyResource.addProperty(testBeanURI.resolve("#unorderedListType"), new PropertyLiteral("a"));
		propertyResource.addProperty(testBeanURI.resolve("#unorderedListType"), new PropertyLiteral("b"));
		propertyResource.addProperty(testBeanURI.resolve("#unorderedListType"), new PropertyLiteral("c"));
		propertyResource.addProperty(testBeanURI.resolve("#setType"), new PropertyLiteral("x"));
		propertyResource.addProperty(testBeanURI.resolve("#setType"), new PropertyLiteral("y"));
		propertyResource.addProperty(testBeanURI.resolve("#setType"), new PropertyLiteral("z"));

		propertyList = new PropertyList();
		propertyList.add(propertyResource2);
		propertyList.add(propertyResource3);
		propertyList.add(propertyResource4);
		propertyResource.addProperty(testBeanURI.resolve("#arrayOfBeanType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(propertyResource3);
		propertyList.add(propertyResource4);
		propertyResource.addProperty(testBeanURI.resolve("#listOfBeanType"), propertyList);
		propertyResource.addProperty(testBeanURI.resolve("#setOfBeanType"), propertyResource2);
		propertyResource.addProperty(testBeanURI.resolve("#setOfBeanType"), propertyResource3);

		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("A"));
		propertyList.add(new PropertyLiteral("B"));
		propertyResource.addProperty(testBeanURI.resolve("#arrayOfEnumType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("B"));
		propertyList.add(new PropertyLiteral("A"));
		propertyResource.addProperty(testBeanURI.resolve("#listOfEnumType"), propertyList);
		propertyResource.addProperty(testBeanURI.resolve("#setOfEnumType"), new PropertyLiteral("A"));
		propertyResource.addProperty(testBeanURI.resolve("#setOfEnumType"), new PropertyLiteral("B"));

		propertyResource.addPropertyReference(testBeanURI.resolve("#uriType"), URI.create("http://www.example.com/"));

		return configuration;
	}
}
