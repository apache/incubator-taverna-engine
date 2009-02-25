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
package net.sf.taverna.t2.annotation;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.taverna.t2.annotation.annotationbeans.FreeTextDescription;
import net.sf.taverna.t2.annotation.annotationbeans.MimeType;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;
import net.sf.taverna.t2.workflowmodel.impl.DummyDataflow;

import org.junit.Test;

public class TestAnnotations {

	@SuppressWarnings("unchecked")
	@Test
	public void getAnnotationsForADataFlow() {

		Edits edits = EditsRegistry.getEdits();

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
		Edit<AnnotationAssertion> addAnnotationBean = edits
				.getAddAnnotationBean(annotationAssertionImpl,
						mimeType);

		
		
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
