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
package net.sf.taverna.t2.workflowmodel.processor.dispatch.description;

/**
 * Describes the effect of a message on the state of a dispatch layer, used by
 * DispatchLayerReaction. If no message causes any of these actions the layer
 * can be described as state free.
 * 
 * @author Tom Oinn
 * 
 */
public enum DispatchLayerStateEffect {

	/**
	 * The message causes a state object within the dispatch layer to be created
	 * keyed on the process identifier and index
	 */
	CREATE_LOCAL_STATE,

	/**
	 * The message causes the removal of a state object within the dispatch
	 * layer, the layer to be removed is keyed on process identifier and index
	 * of the message
	 */
	REMOVE_LOCAL_STATE,

	/**
	 * The message causes the modification of a previously stored state object
	 * within the dispatch layer, the state object modified is keyed on process
	 * identifier and index of the message.
	 */
	UPDATE_LOCAL_STATE,

	/**
	 * The message causes a state object within the dispatch layer to be created
	 * keyed only on the process identifier and not on the index of the message.
	 */
	CREATE_PROCESS_STATE,

	/**
	 * The message causes a state object to be removed from the dispatch layer,
	 * the state object is identified only by the process identifier
	 */
	REMOVE_PROCESS_STATE,

	/**
	 * The message causes a state object to be modified, the state object is
	 * identified by process identifier only
	 */
	UPDATE_PROCESS_STATE,

	/**
	 * The message causes global level state to be modified within the dispatch
	 * layer
	 */
	UPDATE_GLOBAL_STATE,

	/**
	 * The message has no effect on state. This value is used when specifying
	 * that a message might cause effect or it might not, the interpretation of
	 * the various reaction annotations is that exactly one of the state effects
	 * will take place, so if the state side effect array isn't empty you have
	 * to insert this one to specify that it's possible that no state change
	 * will occur
	 */
	NO_EFFECT;

}
