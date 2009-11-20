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
package net.sf.taverna.t2.workflowmodel.processor.activity;

import java.awt.datatransfer.Transferable;

/**
 * Used when dragging activities from the palette onto "something". Place it
 * inside a {@link Transferable} when doing a drag operation. Contains an
 * {@link Activity} and its configuration bean.
 * 
 * @author Ian Dunlop
 * 
 */
public class ActivityAndBeanWrapper {
	/** The Activity being dragged */
	private Activity<?> activity;
	/** The bean used to configure the activity */
	private Object bean;
	private String name;

	public Activity<?> getActivity() {
		return activity;
	}

	public void setActivity(Activity<?> activity) {
		this.activity = activity;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
