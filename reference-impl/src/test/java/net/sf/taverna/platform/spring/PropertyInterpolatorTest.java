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
package net.sf.taverna.platform.spring;

import static net.sf.taverna.platform.spring.PropertyInterpolator.interpolate;

import java.util.Properties;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test case for the property interpolator, mostly because java regex is evil
 * 
 * @author Tom Oinn
 * 
 */
public class PropertyInterpolatorTest {

	static Properties exampleProperties() {
		Properties props = new Properties();
		props.put("a", "x");
		props.put("a.b", "xy");
		return props;
	}

	@Test
	public void testInterpolation() {
		assertEquals(interpolate("${a}", exampleProperties()), "x");
		assertEquals(interpolate("foo ${a} bar", exampleProperties()),
				"foo x bar");
		assertEquals(interpolate("foo ${a}${a.b} bar", exampleProperties()),
				"foo xxy bar");
	}

}
