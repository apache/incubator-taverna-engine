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
package net.sf.taverna.t2.reference.impl;

import static net.sf.taverna.t2.reference.T2ReferenceType.ErrorDocument;
import static net.sf.taverna.t2.reference.T2ReferenceType.IdentifiedList;
import static net.sf.taverna.t2.reference.T2ReferenceType.ReferenceSet;

import java.util.List;

import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceGenerator;
import net.sf.taverna.t2.reference.WorkflowRunIdEntity;

/**
 * An abstract class for implementing simple {@link T2ReferenceGenerator}s.
 * 
 * @author Stian Soiland-Reyes
 */
public abstract class AbstractT2ReferenceGenerator implements
		T2ReferenceGenerator {
	public AbstractT2ReferenceGenerator() {
		super();
	}

	private void initReferenceNamespace(T2ReferenceImpl r, ReferenceContext context) {
		if (context == null) {
			// this is not good, just use the default namespace
			r.setNamespacePart(getNamespace());
			return;
		}

		List<WorkflowRunIdEntity> workflowRunIdEntities = context
				.getEntities(WorkflowRunIdEntity.class);
		if (workflowRunIdEntities == null || workflowRunIdEntities.isEmpty()) {
			// this is not good, just use the default namespace
			r.setNamespacePart(getNamespace());
			return;
		}

		// there should be only one wf run id entity
		String workflowRunId = ((WorkflowRunIdEntity) workflowRunIdEntities
				.get(0)).getWorkflowRunId();
		r.setNamespacePart(workflowRunId);
	}

	@Override
	public synchronized T2Reference nextReferenceSetReference(
			ReferenceContext context) {
		T2ReferenceImpl r = new T2ReferenceImpl();
		initReferenceNamespace(r, context);
		r.setLocalPart(getNextLocalPart());
		r.setReferenceType(ReferenceSet);
		r.setDepth(0);
		r.setContainsErrors(false);
		return r;
	}

	/**
	 * Generate a new local part for a new {@link T2Reference reference}. The
	 * local part should be unique within this
	 * {@link T2ReferenceGenerator#getNamespace() namespace}.
	 * 
	 * @return A new, unique local part to identify a new reference.
	 */
	protected abstract String getNextLocalPart();

	@Override
	public T2Reference nextListReference(boolean containsErrors, int listDepth,
			ReferenceContext context) {
		T2ReferenceImpl r = new T2ReferenceImpl();
		initReferenceNamespace(r, context);
		r.setLocalPart(getNextLocalPart());
		r.setReferenceType(IdentifiedList);
		r.setDepth(listDepth);
		r.setContainsErrors(containsErrors);
		return r;
	}

	@Override
	public T2Reference nextErrorDocumentReference(int depth,
			ReferenceContext context) {
		T2ReferenceImpl r = new T2ReferenceImpl();
		initReferenceNamespace(r, context);
		r.setLocalPart(getNextLocalPart());
		r.setReferenceType(ErrorDocument);
		r.setDepth(depth);
		// This is an error document, it contains errors by definition
		r.setContainsErrors(true);
		return r;
	}
}
