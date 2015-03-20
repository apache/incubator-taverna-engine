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


/**
 * A parsed Distinguished Name with getters for parts.
 * 
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * @author Christian Brenninkmeijer
 */
public interface ParsedDistinguishedName {
    
        /**
         * 
         * @return The common name
         */
	public String getCN();

        /**
         * 
         * @return The Email address
         */
	public String getEmailAddress();

        /**
         * 
         * @return The organizational unit name
         */
	public String getOU();
        
        /**
         * 
         * @return The organization name
         */
	public String getO();

        /**
         * 
         * @return The locality name 
         */
	public String getL();

        /**
         * 
         * @return The state or province name
         */
	public String getST();

        /**
         * 
         * @return The country name 
         */
	public String getC();
}
