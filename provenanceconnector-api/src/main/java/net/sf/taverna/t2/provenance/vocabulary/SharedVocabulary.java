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
package net.sf.taverna.t2.provenance.vocabulary;

import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.lineageservice.types.ProvenanceEventType;

/**
 * Static strings which identify all the {@link ProvenanceItem}s and
 * {@link ProvenanceEventType}s
 * 
 * @author Paolo Missier
 * 
 */
public interface SharedVocabulary {

	/**
	 * basic types
	 */
	public static String DATAFLOW_EVENT_TYPE = "workflow";
	public static String PROCESS_EVENT_TYPE = "process";

	/**
	 * correspond to each type in net.sf.taverna.t2.provenance
	 */
	public static String PROVENANCE_EVENT_TYPE = "provenanceEvent";
	public static String ACTIVITY_EVENT_TYPE = "activity";
	public static String DATA_EVENT_TYPE = "data";
	public static String ERROR_EVENT_TYPE = "error";
	public static String INMEMORY_EVENT_TYPE = "inmemory";
	public static String INPUTDATA_EVENT_TYPE = "inputdata";
	public static String ITERATION_EVENT_TYPE = "iteration";
	public static String OUTPUTDATA_EVENT_TYPE = "outputdata";
	public static String PROCESSOR_EVENT_TYPE = "processor";
	public static String WEBSERVICE_EVENT_TYPE = "webservice";
	public static String WORKFLOW_DATA_EVENT_TYPE = "workflowdata";
	public static String WORKFLOW_EVENT_TYPE = "workflow";
	public static String END_WORKFLOW_EVENT_TYPE = "EOW";

}
