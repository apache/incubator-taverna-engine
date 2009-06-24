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
package net.sf.taverna.t2.annotation.annotationbeans;

import java.net.URI;

import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AppliesTo;

/**
 * A metadata bean containing a predicate and object part of a triple - it is
 * assumed that the entity to which this is bound is the subject part of the
 * triple.
 * <p>
 * This then forms a triple where sense is <em>bound object</em> 'predicate'
 * 'object' for example 'Activity hasRole globalAlignment'.
 * <p>
 * This is a relatively simplistic mapping into an ontology as it only allows
 * for a single predicate and object, but in reality that captures the vast
 * majority of our current usage in myGrid. As this is entirely non-specific in
 * its default form it can be applied to anything, although obviously not all
 * combinations of ontologies and target subjects will make sense! We expect
 * this to be subclassed for specific cases to make annotators etc more user
 * friendly (i.e. have a specific 'hasRole' annotation which delegates to this
 * as its superclass)
 * 
 * @author Tom Oinn
 * 
 */
@AppliesTo(targetObjectType = { Object.class }, many = true)
public abstract class AbstractTripleAssertion implements AnnotationBeanSPI {

	private URI ontologyURI;

	private String predicateLocalName;

	private String objectLocalName;

	/**
	 * Default public constructor as required by java bean specification
	 */
	protected AbstractTripleAssertion() {
		// Default constructor, not much to do here.
	}

	/**
	 * @return URI of the ontology which contains both object and predicate
	 *         local names.
	 */
	public URI getOntologyURI() {
		return ontologyURI;
	}

	/**
	 * @param ontologyURI
	 *            URI of the ontology which contains both object and predicate
	 *            local names.
	 */
	public void setOntologyURI(URI ontologyURI) {
		this.ontologyURI = ontologyURI;
	}

	/**
	 * @return Local name of the predicate used in this assignment, the fully
	 *         qualified name is the URI of the ontology plus this local name,
	 *         i.e. uri:full.ontology.name#localName
	 */
	public String getPredicateLocalName() {
		return predicateLocalName;
	}

	/**
	 * @param predicateLocalName
	 *            local name of the predicate
	 */
	public void setPredicateLocalName(String predicateLocalName) {
		this.predicateLocalName = predicateLocalName;
	}

	/**
	 * @return Local name of the object used in this assignment, the fully
	 *         qualified name is the URI of the ontology plus this local name,
	 *         i.e. uri:full.ontology.name#localName
	 */
	public String getObjectLocalName() {
		return objectLocalName;
	}

	/**
	 * @param objectLocalName
	 *            local name within the ontology of the object for this triple
	 */
	public void setObjectLocalName(String objectLocalName) {
		this.objectLocalName = objectLocalName;
	}

}
