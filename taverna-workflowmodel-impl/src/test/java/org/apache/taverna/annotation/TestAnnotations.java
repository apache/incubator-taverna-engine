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

package org.apache.taverna.annotation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.taverna.annotation.annotationbeans.FreeTextDescription;
import org.apache.taverna.annotation.annotationbeans.MimeType;
import org.apache.taverna.annotation.impl.AnnotationAssertionImpl;
import org.apache.taverna.annotation.impl.AnnotationChainImpl;
import org.apache.taverna.annotation.impl.PersonImpl;
import org.apache.taverna.annotation.impl.URISource;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.impl.DummyDataflow;
import org.apache.taverna.workflowmodel.impl.EditsImpl;

import org.junit.Ignore;
import org.junit.Test;

public class TestAnnotations {

	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	@Test
	@Ignore("utterly broken")
	public void getAnnotationsForADataFlow() {

		Edits edits = new EditsImpl();

		FreeTextDescription freeTextDescription = new FreeTextDescription();
		freeTextDescription.setText("i am the mime type for some object");
		MimeType mimeType = new MimeType();
		mimeType.setText("text/plain");
		Person person1 = new PersonImpl("A person");
		Person person2 = new PersonImpl("Another person");
		List<Person> personList = new ArrayList<Person>();
		personList.add(person1);
		personList.add(person2);

		@SuppressWarnings("unused")
		AnnotationSourceSPI annotationSource = null;
		try {
			annotationSource = new URISource(new URI("http://google.com"));
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		AnnotationAssertion annotationAssertionImpl = new AnnotationAssertionImpl();
		Edit<?> addAnnotationBean = edits.getAddAnnotationBean(
				annotationAssertionImpl, mimeType);

		try {
			addAnnotationBean.doEdit();
		} catch (EditException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// AnnotationAssertion<FreeTextDescription> annotationAssertion = new
		// AnnotationAssertionImpl(
		// freeTextDescription, AnnotationRole.INITIAL_ASSERTION,
		// personList, annotationSource);

		AnnotationChain annotationChain = new AnnotationChainImpl();
		// not 100% convinced that the edits should be in the EditsImpl but it
		// doesn't seem to fit in with AbstractAnnotatedThing either

		Edit<AnnotationChain> addAnnotationAssertionEdit = edits
				.getAddAnnotationAssertionEdit(annotationChain,
						annotationAssertionImpl);
		try {
			addAnnotationAssertionEdit.doEdit();
		} catch (EditException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		assertFalse("There were no assertions", annotationChain.getAssertions().isEmpty());

		addAnnotationAssertionEdit.undo();

		assertTrue("There were assertions", annotationChain.getAssertions().isEmpty());

		try {
			addAnnotationAssertionEdit.doEdit();
		} catch (EditException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		assertFalse("There were no assertions", annotationChain.getAssertions().isEmpty());

		Dataflow dataflow = new DummyDataflow();

		Edit<? extends Dataflow> addAnnotationEdit = dataflow
				.getAddAnnotationEdit(annotationChain);

		try {
			addAnnotationEdit.doEdit();
		} catch (EditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (AnnotationChain chain : dataflow.getAnnotations()) {
			for (AnnotationAssertion assertion : chain.getAssertions()) {
				// assume we do some sort of SPI lookup to figure out the
				// classes!!
				AnnotationBeanSPI detail = assertion.getDetail();
				System.out.println(((MimeType) detail).getText());
			}
		}

		for (Annotation annotation : freeTextDescription.getClass()
				.getAnnotations()) {
			if (annotation.annotationType() == AppliesTo.class) {
				System.out.println("It's an applies to");
				Class<?>[] targetObjectType = ((AppliesTo) annotation)
						.targetObjectType();
				for (Class clazz : targetObjectType) {
					System.out.println(clazz.getCanonicalName());
				}
			}
		}

		for (AnnotationAssertion assertion : annotationChain.getAssertions()) {
			System.out.println(assertion.getDetail().getClass());

		}

		Set<? extends AnnotationChain> annotations = dataflow.getAnnotations();

		for (AnnotationChain chain : annotations) {
			for (AnnotationAssertion assertion : chain.getAssertions()) {
				System.out.println("class: " + assertion.getClass().getName());
				// Do we need some sort of SPI look up thing to do this because
				// we don't know what type of AnnotationBean we will be getting
				// System.out.println("Detail: "
				// + ((AnnotationAssertionImpl) assertion).getDetail()
				// .getText());
				
				
				
				System.out.println("Creation date: "
						+ assertion.getCreationDate());

				// int x = 1;
				// for (Person person : assertion.getCreators()) {
				// System.out.println("Person " + x);
				// x++;
				// }
				// for (CurationEvent event : assertion.getCurationAssertions())
				// {
				// System.out
				// .println("CurationBeanSPI - what do you do with it?");
				// }

				System.out.println("Role: " + assertion.getRole());

			}
		}
	}

}
