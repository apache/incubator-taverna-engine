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

package org.apache.taverna.workflowmodel.processor.iteration.impl;

import org.apache.taverna.workflowmodel.processor.iteration.CrossProduct;
import org.apache.taverna.workflowmodel.processor.iteration.DotProduct;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;
import org.apache.taverna.workflowmodel.processor.iteration.MissingIterationInputException;
import org.apache.taverna.workflowmodel.processor.iteration.NamedInputPortNode;
import junit.framework.TestCase;
import java.util.Map;
import java.util.HashMap;

/**
 * Test the type check functionality now built into the iteration system,
 * exercises the iteration strategy and the strategy stack with a combination of
 * simple and staged iteration to check whether the type checker is getting
 * sensible results out.
 * 
 * @author Tom Oinn
 * 
 */
public class IterationTypeCheckerTest extends TestCase {

	private IterationStrategyImpl getISDot(int depthA, int depthB) {
		IterationStrategyImpl is1 = new IterationStrategyImpl();
		NamedInputPortNode nipn1 = new NamedInputPortNode("a", depthA);
		NamedInputPortNode nipn2 = new NamedInputPortNode("b", depthB);
		is1.addInput(nipn1);
		is1.addInput(nipn2);
		DotProduct dp = new DotProduct();
		nipn1.setParent(dp);
		nipn2.setParent(dp);
		dp.setParent(is1.getTerminalNode());
		return is1;
	}

	private IterationStrategyImpl getISCross(int depthA, int depthB) {
		IterationStrategyImpl is1 = new IterationStrategyImpl();
		NamedInputPortNode nipn1 = new NamedInputPortNode("a", depthA);
		NamedInputPortNode nipn2 = new NamedInputPortNode("b", depthB);
		is1.addInput(nipn1);
		is1.addInput(nipn2);
		CrossProduct cp = new CrossProduct();
		nipn1.setParent(cp);
		nipn2.setParent(cp);
		cp.setParent(is1.getTerminalNode());
		return is1;
	}

	private Map<String, Integer> getDepths(String[] names, int[] depths) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		for (int i = 0; i < names.length; i++) {
			result.put(names[i], depths[i]);
		}
		return result;
	}

	/**
	 * Test simple iteration type check based on dot products
	 * 
	 * @throws IterationTypeMismatchException
	 */
	public void testDotUnstagedIteration()
			throws IterationTypeMismatchException {
		IterationStrategyImpl isi = getISDot(0, 0);

		assertTrue(isi.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 1, 1 })) == 1);

		assertTrue(isi.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 2, 2 })) == 2);

		isi = getISDot(0, 1);

		assertTrue(isi.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 1, 2 })) == 1);
	}

	/**
	 * Test a simple iteration based on a cross product
	 * 
	 * @throws IterationTypeMismatchException
	 */
	public void testCrossUnstagedIteration()
			throws IterationTypeMismatchException {
		IterationStrategyImpl isi = getISCross(0, 0);

		assertTrue(isi.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 1, 1 })) == 2);

		assertTrue(isi.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 2, 2 })) == 4);

		assertTrue(isi.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 1, 2 })) == 3);

		isi = getISCross(0, 1);

		assertTrue(isi.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 1, 2 })) == 2);
	}

	/**
	 * Test that attempting to typecheck a mismatched dot product produces a
	 * type mismatch exception
	 * 
	 */
	public void testValidationFailureWithDot() {
		try {
			IterationStrategyImpl isi = getISDot(0, 0);
			isi.getIterationDepth(getDepths(new String[] { "a", "b" },
					new int[] { 1, 2 }));
			fail("should have failed due to mismatch");
		} catch (IterationTypeMismatchException itme) {
			// Correct behaviour, this should cause a mismatch
		}
	}

	/**
	 * Fundamentally pointless, combining two cross product iteration strategies
	 * is the same as using the last one directly but we can check this
	 * 
	 * @throws IterationTypeMismatchException
	 * @throws MissingIterationInputException
	 */
	public void testStagedCombinationOfCross()
			throws IterationTypeMismatchException,
			MissingIterationInputException {
		IterationStrategyStackImpl iss;

		iss = new IterationStrategyStackImpl();
		iss.addStrategy(getISCross(1, 1));
		iss.addStrategy(getISCross(0, 0));

		assertTrue(iss.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 2, 2 })) == 4);

	}

	/**
	 * Fundamentally pointless, combining two dot product iteration strategies
	 * is the same as using the last one directly but we can check this
	 * 
	 * @throws IterationTypeMismatchException
	 * @throws MissingIterationInputException
	 */
	public void testStagedCombinationOfDot()
			throws IterationTypeMismatchException,
			MissingIterationInputException {
		IterationStrategyStackImpl iss;

		iss = new IterationStrategyStackImpl();
		iss.addStrategy(getISDot(1, 1));
		iss.addStrategy(getISDot(0, 0));

		assertTrue(iss.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 2, 2 })) == 2);

		// Should pass because the single items (depth 0) are promoted to single
		// item lists before being passed into the iteration system so are
		// effectively both depth 1 going into the second stage which then
		// iterates to produce an index array length of 1
		assertTrue(iss.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 0, 0 })) == 1);

		// Slightly strange superficially that this should work, but in fact
		// what happens is that the first single item is lifted to a list before
		// being passed to the iteration strategy. The result is that it's fine
		// to match with the dot product against the other list as neither at
		// this point have index arrays, then in the second stage both are lists
		// to be iterated over and both have single length index arrays.
		assertTrue(iss.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 1, 0 })) == 1);
	}

	/**
	 * Test whether Paul's example of iterating with dot product then cross
	 * product can typecheck in a single staged iteration. This was an example
	 * where the user had two lists of folders (a1, a2, b1, b2, c1, c2) and
	 * wanted to compare all the contents of each 'a' folder with the other 'a'
	 * folder and so on, doing a dot match to only compare a1 and a2 then a
	 * cross product join within each pair to compare all contents of a1 with
	 * all contents of a2. This appears to work!
	 */
	public void testStagedCombinationOfDotAndCross()
			throws IterationTypeMismatchException,
			MissingIterationInputException {
		IterationStrategyStackImpl iss;

		iss = new IterationStrategyStackImpl();
		iss.addStrategy(getISDot(1, 1));
		iss.addStrategy(getISCross(0, 0));

		assertTrue(iss.getIterationDepth(getDepths(new String[] { "a", "b" },
				new int[] { 2, 2 })) == 3);
	}

}
