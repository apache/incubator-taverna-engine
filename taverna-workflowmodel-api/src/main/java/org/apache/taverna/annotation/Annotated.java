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

import java.util.Set;

import org.apache.taverna.workflowmodel.Edit;

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
