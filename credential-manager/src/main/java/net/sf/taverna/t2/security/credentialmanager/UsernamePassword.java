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
package net.sf.taverna.t2.security.credentialmanager;

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
