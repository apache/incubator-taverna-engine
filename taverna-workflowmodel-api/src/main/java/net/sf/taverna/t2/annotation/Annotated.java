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

import java.util.Set;

import net.sf.taverna.t2.workflowmodel.Edit;

/**
 * Denotes that the object carries workflow object level annotation. Rather than
 * defining specific annotation types for each workflow entity we work on the
 * basis that multiple annotations of different types may apply, so free text
 * description is one example, semantic annotation of the internal function of a
 * processor might be another.
 * <p>
 * Where annotations are conceptually editable such as free text descriptions
 * the editing framework should internally remove the original annotation and
 * add the replacement rather than modifying the previous annotation in place.
 * 
 * @author Tom Oinn
 * 
 */
public interface Annotated<TargetType> {

	/**
	 * Each annotated object contains a bag of metadata object instances
	 * 
	 * @return set of metadata objects that apply to the annotated object
	 */
	Set<? extends AnnotationChain> getAnnotations();
	
	void setAnnotations(Set<AnnotationChain> annotations);

	/**
	 * Add new workflow object metadata to this annotated entity
	 * 
	 * @param <TargetType>
	 *            the type of the object being annotated
	 * @param newAnnotation
	 *            metadata object to add to the annotated object
	 * @return edit object to perform and undo the metadata assignment
	 */
	public Edit<? extends TargetType> getAddAnnotationEdit(
			AnnotationChain newAnnotation);

	/**
	 * Remove an annotation object from the this annotated entity
	 * 
	 * @param <TargetType>
	 *            type of the workflow object from which the annotation is
	 *            removed
	 * @param annotationToRemove
	 *            metadata object to remove
	 * @param objectToAnnotate
	 *            object from which the metadata is removed
	 * @return edit object to perform and undo the metadata removal
	 */
	public Edit<? extends TargetType> getRemoveAnnotationEdit(
			AnnotationChain annotationToRemove);

}
