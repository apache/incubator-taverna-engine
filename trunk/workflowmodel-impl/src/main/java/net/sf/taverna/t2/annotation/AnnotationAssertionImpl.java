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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	public AnnotationBeanSPI getDetail() {
		return annotationBean;
	}

	public AnnotationRole getRole() {
		return annotationRole;
	}

	public Date getCreationDate() {
		return date;
	}

	public List<? extends Person> getCreators() {
		return creators;
	}
	
	public void addCreator(Person person) {
		creators.add(person);
	}
	
	public void removeCreator(Person person) {
		creators.remove(person);
	}

	public List<CurationEvent<?>> getCurationAssertions() {
		return curationEventList;
	}

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
	
	@SuppressWarnings("unchecked")
	public void addCurationEvent(CurationEvent curationEvent) {
		curationEventList.add(curationEvent);
	}

	@SuppressWarnings("unchecked")
	public void removeCurationEvent(CurationEvent curationEvent) {
		curationEventList.remove(curationEvent);
	}

}
