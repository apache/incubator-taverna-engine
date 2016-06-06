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
package org.apache.taverna.provenance.lineageservice.utils;

public class DataBinding {
	private String dataBindingId;
	private Port port;
	private String t2Reference;
	private String workflowRunId;

	public String getDataBindingId() {
		return dataBindingId;
	}

	public void setDataBindingId(String dataBindingId) {
		this.dataBindingId = dataBindingId;
	}

	public Port getPort() {
		return port;
	}

	public void setPort(Port port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		int result = 31 + ((dataBindingId == null) ? 0 : dataBindingId
				.hashCode());
		return 31 * result + ((port == null) ? 0 : port.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataBinding [dataBindingId=");
		builder.append(dataBindingId);
		builder.append(", port=");
		builder.append(port);
		builder.append(", t2Reference=");
		builder.append(t2Reference);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataBinding other = (DataBinding) obj;
		if (dataBindingId == null) {
			if (other.dataBindingId != null)
				return false;
		} else if (!dataBindingId.equals(other.dataBindingId))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		return true;
	}

	public String getT2Reference() {
		return t2Reference;
	}

	public void setT2Reference(String t2Reference) {
		this.t2Reference = t2Reference;
	}

	public String getWorkflowRunId() {
		return workflowRunId;
	}

	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}
}
