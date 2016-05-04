/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.security.credentialmanager;

import java.util.Arrays;

/**
 * 
 * @author Stian Soiland-Reyes
 */
public class UsernamePassword {
	private char[] password;
	private boolean shouldSave = false;
	private String username;

	@Override
	public UsernamePassword clone() {
		UsernamePassword up = new UsernamePassword();
		up.setUsername(getUsername());
		up.setPassword(getPassword().clone());
		up.setShouldSave(isShouldSave());
		return up;
	}

	public UsernamePassword() {
	}

	public UsernamePassword(String username, String password) {
		this.username = username;
		this.password = password.toCharArray();
	}

	public char[] getPassword() {
		return password;
	}

	public String getPasswordAsString() {
		return String.valueOf(password);
	}

	public String getUsername() {
		return username;
	}

	public boolean isShouldSave() {
		return shouldSave;
	}

	public void resetPassword() {
		if (this.password == null)
			return;
		Arrays.fill(this.password, '\u0000');
	}

	public void setPassword(char[] password) {
		resetPassword();
		this.password = password;
	}

	public void setShouldSave(boolean shouldSave) {
		this.shouldSave = shouldSave;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	protected void finalize() throws Throwable {
		resetPassword();
	}
}
