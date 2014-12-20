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

import java.util.Date;
import java.util.List;

/**
 * Implemented by objects which can have curation assertions attached to them.
 * In our model this includes the AnnotationAssertion but also includes the
 * CurationEvent itself, in this way we allow curation of curation assertions
 * and thence a conversational model of annotation.
 * 
 * @author Tom Oinn
 * 
 */
public interface Curateable {

	/**
	 * Curateable instances have a list of curation events which are used to
	 * determine whether the implementing object is valid given a particular
	 * interpretive context. If this list is empty the event is unchallenged.
	 * 
	 * @return
	 */
	public List<CurationEvent<?>> getCurationAssertions();

	/**
	 * All curation events are marked with their creation date, this is the date
	 * at which the curation event was associated with its target.
	 * 
	 * @return
	 */
	public Date getCreationDate();

	/**
	 * Each curateable has a list of people associated with it, frequently one
	 * person and in some cases none, although this should be avoided if
	 * possible.
	 * 
	 * @return
	 */
	public List<? extends Person> getCreators();

	/**
	 * Each annotation or curation has a resource from which the event is
	 * inherently derived, for example if the annotation was created manually
	 * after reading a paper the source would unambiguously specify the
	 * publication.
	 * 
	 * @return
	 */
	public AnnotationSourceSPI getSource();
}
