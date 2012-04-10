/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
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
package uk.org.taverna.platform.data.impl;

import java.util.List;
import java.util.Set;

import uk.org.taverna.platform.data.api.ErrorValue;

/**
 * Implementation of an <code>ErrorValue</code>.
 *
 * @author David Withers
 */
public class ErrorValueImpl implements ErrorValue {

	private final String message;
	private final String exceptionMessage;
	private final List<StackTraceElement> stackTrace;
	private final Set<ErrorValue> causes;

	public ErrorValueImpl(String message, String exceptionMessage,
			List<StackTraceElement> stackTrace, Set<ErrorValue> causes) {
		this.message = message;
		this.exceptionMessage = exceptionMessage;
		this.stackTrace = stackTrace;
		this.causes = causes;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	@Override
	public List<StackTraceElement> getStackTrace() {
		return stackTrace;
	}

	@Override
	public Set<ErrorValue> getCauses() {
		return causes;
	}

}
