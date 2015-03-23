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

package org.apache.taverna.annotation.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.taverna.annotation.AnnotationAssertion;
import org.apache.taverna.annotation.AnnotationBeanSPI;
import org.apache.taverna.annotation.AnnotationRole;
import org.apache.taverna.annotation.AnnotationSourceSPI;
import org.apache.taverna.annotation.CurationEvent;
import org.apache.taverna.annotation.Person;

public class AnnotationAssertionImpl implements AnnotationAssertion<AnnotationBeanSPI> {
	
	private AnnotationBeanSPI annotationBean;
	private AnnotationRole annotationRole;
	private Date date;
	private List<Person> creators;
	private AnnotationSourceSPI annotationSource;
	private List<CurationEvent<?>> curationEventList;

	public AnnotationAssertionImpl(){
		date = new Date();
		curationEventList = new ArrayList<CurationEvent<?>>();
		creators = new ArrayList<Person>();
		
	}
	
	public AnnotationAssertionImpl(AnnotationBeanSPI freeTextDescription, AnnotationRole annotationRole, List<Person> creators, AnnotationSourceSPI annotationSource) {
		this.annotationBean = freeTextDescription;
		this.annotationRole = annotationRole;
		this.creators = creators;
		this.annotationSource = annotationSource;
	}

	@Override
	public AnnotationBeanSPI getDetail() {
		return annotationBean;
	}

	@Override
	public AnnotationRole getRole() {
		return annotationRole;
	}

	@Override
	public Date getCreationDate() {
		return date;
	}

	@Override
	public List<? extends Person> getCreators() {
		return creators;
	}
	
	public void addCreator(Person person) {
		creators.add(person);
	}
	
	public void removeCreator(Person person) {
		creators.remove(person);
	}

	@Override
	public List<CurationEvent<?>> getCurationAssertions() {
		return curationEventList;
	}

	@Override
	public AnnotationSourceSPI getSource() {
		return annotationSource;
	}

	public void setAnnotationBean(AnnotationBeanSPI annotationBean) {
		this.annotationBean = annotationBean;
	}

	public void setAnnotationRole(AnnotationRole annotationRole) {
		this.annotationRole = annotationRole;
	}
	
	public void removeAnnotationRole() {
		this.annotationRole = null;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setCreators(List<Person> creators) {
		this.creators = creators;
	}

	public void setAnnotationSource(AnnotationSourceSPI annotationSource) {
		this.annotationSource = annotationSource;
	}
	
	public void removeAnnotationSource() {
		this.annotationSource = null;
	}

	public void removeAnnotationBean() {
		annotationBean = null;
	}
	
	public void addCurationEvent(CurationEvent<?> curationEvent) {
		curationEventList.add(curationEvent);
	}

	public void removeCurationEvent(CurationEvent<?> curationEvent) {
		curationEventList.remove(curationEvent);
	}

}
