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
package org.apache.taverna.provenance.lineageservice.types;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


/**
 * 
 * @author Paolo Missier
 * 
 */
public class ProcessorType implements ProvenanceEventType {
	private ActivityType[] activity;
	private String id; // attribute

	public ProcessorType() {
	}

	public ProcessorType(ActivityType[] activity, String id) {
		this.activity = activity;
		this.id = id;
	}

	/**
	 * Gets the activity value for this ProcessorType.
	 * 
	 * @return activity
	 */
	public ActivityType[] getActivity() {
		return activity;
	}

	/**
	 * Sets the activity value for this ProcessorType.
	 * 
	 * @param activity
	 */
	public void setActivity(ActivityType[] activity) {
		this.activity = activity;
	}

	public ActivityType getActivity(int i) {
		return this.activity[i];
	}

	public void setActivity(int i, ActivityType _value) {
		this.activity[i] = _value;
	}

	/**
	 * Gets the id value for this ProcessorType.
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id value for this ProcessorType.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
}
