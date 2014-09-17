package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;

abstract class EditSupport<T> implements Edit<T> {
	protected boolean applied;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final T doEdit() throws EditException {
		if (applied)
			throw new EditException("Edit has already been applied!");
		T result = applyEdit();
		applied = true;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isApplied() {
		return applied;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void undo() {
		if (!applied)
			throw new RuntimeException(
					"Attempt to undo edit that was never applied");
		applyUndo();
	}

	protected abstract T applyEdit() throws EditException;

	protected void applyUndo() {
		throw new UnsupportedOperationException(
				"undo not supported by this interface in Taverna 3");
	}
}
