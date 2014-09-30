/*******************************************************************************
 * Copyright (C) 2014 The University of Manchester
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
package net.sf.taverna.t2.security.credentialmanager.impl;

import java.util.ArrayList;
import net.sf.taverna.t2.security.credentialmanager.ParsedDistinguishedName;
import org.apache.log4j.Logger;


/**
 * Utility methods for Credential Manager and security-related stuff.
 * 
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 */
public class ParsedDistinguishedNameImpl implements ParsedDistinguishedName{
	private static final Logger logger = Logger.getLogger(ParsedDistinguishedNameImpl.class);

	private String emailAddress; // not from RFC 2253, yet some certificates
									// contain this field
	private String CN;
	private String L;
	private String ST;
	private String C;
	private String O;
	private String OU;

	/**
	 * Parses a DN string and fills in fields with DN parts. Heavily based on
	 * uk.ac.omii.security.utils.DNParser class from omii-security-utils
	 * library.
	 * 
	 * http://maven.omii.ac.uk/maven2/repository/omii/omii-security-utils/
	 */
	public ParsedDistinguishedNameImpl(String DNstr) {
		// ///////////////////////////////////////////////////////////////////////////////////////////////////
		// Parse the DN String and put into variables. First, tokenise using a
		// "," character as a delimiter
		// UNLESS escaped with a "\" character. Put the tokens into an
		// ArrayList. These should be name value pairs
		// separated by "=". Tokenise these using a StringTokenizer class, test
		// for the name, and if one of the
		// recognised names, copy into the correct variable. The reason
		// StringTokenizer is not used for the major
		// token list is that the StringTokenizer class does not handle escaped
		// delimiters so an escaped delimiter
		// in the code would be treated as a valid one.

		int i = 0;

		char majorListDelimiter = ',';
		char majorListEscapeChar = '\\';

		// String minorListDelimiter = "=";

		String DNchars = DNstr;

		int startIndex = 0;
		int endIndex = 0;
		boolean ignoreThisChar = false;

		boolean inQuotes = false;

		ArrayList<String> majorTokenList = new ArrayList<String>();

		for (i = 0; i < DNchars.length(); i++) {
			if (ignoreThisChar == true) {
				ignoreThisChar = false;
			} else if ((inQuotes == false) && (DNchars.charAt(i) == '\"')) {
				inQuotes = true;
			} else if ((inQuotes == true) && (DNchars.charAt(i) == '\"')) {
				inQuotes = false;
			} else if (inQuotes == true) {
				continue;
			} else if (DNchars.charAt(i) == majorListEscapeChar) {
				ignoreThisChar = true;
			} else if ((DNchars.charAt(i) == majorListDelimiter)
					&& (ignoreThisChar == false)) {
				endIndex = i;
				majorTokenList.add(DNchars.substring(startIndex, endIndex));
				startIndex = i + 1;
			}
		}

		// Add last token - after the last delimiter
		endIndex = DNchars.length();
		majorTokenList.add(DNchars.substring(startIndex, endIndex));

		for (String currentToken : majorTokenList) {
			currentToken = currentToken.trim();

			// split on first equals only, as value can contain an equals char
			String[] minorTokenList = currentToken.split("=", 2);

			if (minorTokenList.length == 2) {
				// there had better be a key and a value only
				String DNTokenName = minorTokenList[0].toUpperCase();
				String DNTokenValue = minorTokenList[1];

				if (DNTokenName.equals("CN")
						|| DNTokenName.equals("COMMONNAME")) {
					CN = DNTokenValue;
				} else if (DNTokenName.equals("EMAIL")
						|| DNTokenName.equals("EMAILADDRESS")) {
					emailAddress = DNTokenValue;
				} else if (DNTokenName.equals("OU")
						|| DNTokenName.equals("ORGANIZATIONALUNITNAME")) {
					OU = DNTokenValue;
				} else if (DNTokenName.equals("O")
						|| DNTokenName.equals("ORGANIZATIONNAME")) {
					O = DNTokenValue;
				} else if (DNTokenName.equals("L")
						|| DNTokenName.equals("LOCALITYNAME")) {
					L = DNTokenValue;
				} else if (DNTokenName.equals("ST")
						|| DNTokenName.equals("STATEORPROVINCENAME")) {
					ST = DNTokenValue;
				} else if (DNTokenName.equals("C")
						|| DNTokenName.equals("COUNTRYNAME")) {
					C = DNTokenValue;
				}
			}
			// else we have a key with no value, so skip processing the key
		}

		if (CN == null)
			CN = "none";

		if (emailAddress == null)
			emailAddress = "none";

		if (OU == null)
			OU = "none";

		if (O == null)
			O = "none";

		if (L == null)
			L = "none";

		if (ST == null)
			ST = "none";

		if (C == null)
			C = "none";
	}

        @Override
	public String getCN() {
		return CN;
	}

        @Override
	public String getEmailAddress() {
		return emailAddress;
	}

        @Override
	public String getOU() {
		return OU;
	}

        @Override
	public String getO() {
		return O;
	}

        @Override
	public String getL() {
		return L;
	}

        @Override
	public String getST() {
		return ST;
	}

        @Override
	public String getC() {
		return C;
	}
}
