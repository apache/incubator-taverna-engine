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

package org.apache.taverna.annotation;

import java.lang.annotation.*;

/**
 * Annotation to be used on metadata objects to denote which workflow objects
 * they apply to
 * 
 * @author Tom Oinn
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface AppliesTo {

	/**
	 * The class of the metadata object allowed by this annotation
	 */
	Class<?>[] targetObjectType();

	/**
	 * Can you have more than one of these metadata objects in the resolved set?
	 */
	boolean many() default true;
	
   /**
     * Should the annotation be pruned, i.e. only most recent kept, when saving?
     */
    boolean pruned() default true;


}
