/*******************************************************************************
 * Copyright (C) 2007-2008 The University of Manchester   
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

import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * An abstract {@link Edit} implementation that checks if an edit has been
 * applied or not, and that can also check if the subject implements the given
 * implementation subclass.
 * 
 * @author Stian Soiland-Reyes
 * 
 * @param <SubjectInterface>
 *            Official interface of the Subject of this edit
 * @param <SubjectType>
 *            Expected implementation type of the Subject of this edit
 */
public abstract class AbstractEdit<SubjectInterface, SubjectType extends SubjectInterface>
		extends EditSupport<SubjectInterface> {
	protected SubjectInterface subject;
	private final Class<? extends SubjectInterface> subjectType;

	/**
	 * Construct an AbstractEdit.
	 * 
	 * @param subjectType
	 *            The expected implementation type of the subject. The edit will
	 *            not go through unless the subject is an instance of this type.
	 *            If the edit don't care about the implementation type, provide
	 *            the official SubjectInterface instead.
	 * @param subject
	 *            The subject of this edit
	 */
	@SuppressWarnings("unchecked")
	public AbstractEdit(Class<?> subjectType, SubjectInterface subject) {
		if (subject == null && !isNullSubjectAllowed())
			throw new RuntimeException(
					"Cannot construct an edit with null subject");
		this.subjectType = (Class<? extends SubjectInterface>) subjectType;
		this.subject = subject;
	}

	protected boolean isNullSubjectAllowed() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final SubjectInterface applyEdit() throws EditException {
		if (!subjectType.isInstance(subject))
			throw new EditException(
					"Edit cannot be applied to a object which isn't an instance of "
							+ subjectType);
		SubjectType subjectImpl = (SubjectType) subject;
		synchronized (subjectImpl) {
			doEditAction(subjectImpl);
			return this.subject;
		}
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param subjectImpl
	 *            The implementation instance to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(SubjectType subjectImpl)
			throws EditException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final SubjectType getSubject() {
		return (SubjectType) subject;
	}
}