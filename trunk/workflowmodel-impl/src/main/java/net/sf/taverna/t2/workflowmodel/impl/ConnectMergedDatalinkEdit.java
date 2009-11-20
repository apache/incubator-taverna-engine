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
package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.utils.Tools;

/**
 * <p>
 * An edit that connects an EventForwardingOutputPort sourcePort and EventHandlingInputPort sinkPort together
 * via an intermediary {@link Merge} instance, which is provided to the constructor. 
 * The connections are made using {@link Datalink}. Using a Merge facilitates multiple incoming Datalinks connect to a single
 * input port.
 * </p>
 * <p>
 * If an connection already exists between a sinkPort and a sourcePort, then then an new datalink is provided
 * for the incoming link but the outgoing link remains as is (since there can only be 1). In this case, if the
 * sink port differs from the existing one then an EditException is thrown.
 * </p>
 * 
 * @author Stuart Owen
 *
 */
public class ConnectMergedDatalinkEdit extends AbstractMergeEdit {

	private EventHandlingInputPort sinkPort;
	private EventForwardingOutputPort sourcePort;
	private Datalink inLink;
	private Datalink outLink;
	private Edit<Datalink> connectInLinkEdit;
	private Edit<Datalink> connectOutLinkEdit;
	private MergeInputPortImpl mergeInputPort;
	
	/**
	 * Constructs the ConnectMergedDatalinkEdit with an existing Merge instance, and the source and sink ports that are to
	 * be connected.
	 * 
	 * @param merge
	 * @param sourcePort
	 * @param sinkPort
	 */
	public ConnectMergedDatalinkEdit(Merge merge, EventForwardingOutputPort sourcePort, EventHandlingInputPort sinkPort) {
		super(merge);
		if (sinkPort==null) throw new RuntimeException("The sinkport cannot be null");
		this.sinkPort=sinkPort;
		if (sourcePort==null) throw new RuntimeException("The sourceport cannot be null");
		this.sourcePort=sourcePort;
	}
	
	@Override
	protected void doEditAction(MergeImpl mergeImpl) throws EditException {
		Edits edits = new EditsImpl();
		String name = Tools.getUniqueMergeInputPortName(mergeImpl, sourcePort.getName()+"To" + merge.getLocalName() + "_input", 0);
		mergeInputPort = new MergeInputPortImpl(mergeImpl,name,sinkPort.getDepth());
		inLink = edits.createDatalink(sourcePort, mergeInputPort);
		connectInLinkEdit=edits.getConnectDatalinkEdit(inLink);
		if (mergeImpl.getOutputPort().getOutgoingLinks().size()==0) {
			outLink = edits.createDatalink(mergeImpl.getOutputPort(), sinkPort);
			connectOutLinkEdit=edits.getConnectDatalinkEdit(outLink);
		}
		else if (mergeImpl.getOutputPort().getOutgoingLinks().size()==1){
			if (mergeImpl.getOutputPort().getOutgoingLinks().toArray(new Datalink[]{})[0].getSink() != sinkPort) {
				throw new EditException("Cannot add a different sinkPort to a Merge that already has one defined");
			}
		}
		else {
			throw new EditException("The merge instance cannot have more that 1 outgoing Datalink");
		}
		
		mergeImpl.addInputPort(mergeInputPort);
		connectInLinkEdit.doEdit();
		if (connectOutLinkEdit!=null) connectOutLinkEdit.doEdit();
	}

	@Override
	protected void undoEditAction(MergeImpl mergeImpl) {
		if (connectOutLinkEdit!=null) connectOutLinkEdit.undo();
		connectInLinkEdit.undo();
		mergeImpl.removeInputPort(mergeInputPort);
	}
	
	
}
