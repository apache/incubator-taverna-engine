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

/**
 * Represents a single act of curation, parameterized on a bean encapsulating
 * the necessary and sufficient information to describe the specifics of the
 * curation event.
 * 
 * @author Tom Oinn
 * 
 * @param <T>
 */
public interface CurationEvent<CurationType extends CurationEventBeanSPI> {

	public CurationType getDetail();

	/**
	 * The curation event type specifies whether this curation event is a
	 * validation, repudiation or neither of its target.
	 * 
	 * @return
	 */
	public CurationEventType getType();

	/**
	 * The curation event applies to a specific other event, either another
	 * curation event or an annotation assertion.
	 * 
	 * @return the event which this event is curating
	 */
	public Curateable getTarget();

}
