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
package net.sf.taverna.t2.workflowmodel.processor.iteration;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.tree.TreeNode;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link AbstractIterationStrategyNode} implementations for
 * {@link TreeNode} behaviour.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class TestIterationStrategyNodes {

	TerminalNode root;
	private NamedInputPortNode input1;
	private NamedInputPortNode input2;
	private CrossProduct crossProduct1;
	private CrossProduct crossProduct2;
	private DotProduct dotProduct1;
	private DotProduct dotProduct2;

	@Test
	public void addSingleChildToTerminal() throws Exception {
		assertNull(input1.getParent());
		assertEquals(0, root.getChildCount());
		root.insert(input1);
		assertEquals(root, input1.getParent());
		assertEquals(1, root.getChildCount());
		assertEquals(input1, root.getChildAt(0));
		assertEquals(Arrays.asList(input1), root.getChildren());

		root.insert(input1);
		assertEquals(1, root.getChildCount());

		root.insert(input1, 0);
		assertEquals(1, root.getChildCount());
	}

	@Test(expected = IllegalStateException.class)
	public void cantAddSeveralChildrenToTerminal() throws Exception {
		root.insert(input1);
		root.insert(input2);
	}

	@Test
	public void addCrossProduct() throws Exception {
		assertNull(crossProduct1.getParent());
		crossProduct1.setParent(root);
		assertEquals(root, crossProduct1.getParent());
		assertEquals(1, root.getChildCount());
		assertEquals(crossProduct1, root.getChildAt(0));
		assertEquals(Arrays.asList(crossProduct1), root.getChildren());
		assertEquals(0, crossProduct1.getChildCount());

		crossProduct1.insert(input1);
		assertEquals(input1, crossProduct1.getChildAt(0));
		crossProduct1.insert(input2, 0);
		assertEquals(input2, crossProduct1.getChildAt(0));
		assertEquals(input1, crossProduct1.getChildAt(1));
		assertEquals(2, crossProduct1.getChildCount());
		assertEquals(Arrays.asList(input2, input1), crossProduct1.getChildren());

		// A re-insert should move it
		crossProduct1.insert(input2, 2);
		assertEquals(2, crossProduct1.getChildCount());
		assertEquals(Arrays.asList(input1, input2), crossProduct1.getChildren());

		crossProduct1.insert(input2, 0);
		assertEquals(Arrays.asList(input2, input1), crossProduct1.getChildren());

		crossProduct1.insert(input1, 1);
		assertEquals(Arrays.asList(input2, input1), crossProduct1.getChildren());
	}

	@Test
	public void addCrossProductMany() {
		crossProduct1.insert(dotProduct1);
		crossProduct1.insert(dotProduct2);
		crossProduct1.insert(input1);
		crossProduct1.insert(input2);
		crossProduct1.insert(crossProduct2);
		assertEquals(5, crossProduct1.getChildCount());
		assertEquals(Arrays.asList(dotProduct1, dotProduct2, input1, input2,
				crossProduct2), crossProduct1.getChildren());
		Enumeration<IterationStrategyNode> enumeration = crossProduct1
				.children();
		assertTrue(enumeration.hasMoreElements());
		assertEquals(dotProduct1, enumeration.nextElement());
		assertEquals(dotProduct2, enumeration.nextElement());
		assertEquals(input1, enumeration.nextElement());
		assertEquals(input2, enumeration.nextElement());
		assertEquals(crossProduct2, enumeration.nextElement());
		assertFalse(enumeration.hasMoreElements());
	}

	@Test
	public void moveNodeToDifferentParent() {
		crossProduct1.setParent(root);
		crossProduct1.insert(input1);
		crossProduct1.insert(dotProduct1);
		dotProduct1.insert(input2);
		dotProduct1.insert(crossProduct2);

		// Check tree
		assertEquals(crossProduct2, root.getChildAt(0).getChildAt(1)
				.getChildAt(1));
		assertEquals(Arrays.asList(input2, crossProduct2), dotProduct1
				.getChildren());

		crossProduct1.insert(crossProduct2, 1);
		assertEquals(Arrays.asList(input1, crossProduct2, dotProduct1),
				crossProduct1.getChildren());
		assertEquals(crossProduct1, crossProduct2.getParent());
		// Should no longer be in dotProduct1
		assertEquals(Arrays.asList(input2), dotProduct1.getChildren());
	}

	@Test(expected = IllegalStateException.class)
	public void cantAddToNamedInput() throws Exception {
		input1.insert(dotProduct1);
	}

	@Test
	public void cantAddSelf() throws Exception {
		dotProduct1.setParent(crossProduct1);
		try {
			dotProduct1.insert(dotProduct1);
			fail("Didn't throw IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			// Make sure we didn't loose our old parent and
			// ended up in a funny state
			assertEquals(crossProduct1, dotProduct1.getParent());
			assertEquals(dotProduct1, crossProduct1.getChildAt(0));
		}
	}

	@Test
	public void cantSetSelfParent() throws Exception {
		crossProduct1.insert(dotProduct1);
		try {
			dotProduct1.setParent(dotProduct1);
			fail("Didn't throw IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			// Make sure we didn't loose our old parent and
			// ended up in a funny state
			assertEquals(crossProduct1, dotProduct1.getParent());
			assertEquals(dotProduct1, crossProduct1.getChildAt(0));
		}
	}

	@Before
	public void makeNodes() throws Exception {
		root = new DummyTerminalNode();
		input1 = new NamedInputPortNode("input1", 1);
		input2 = new NamedInputPortNode("input2", 2);
		crossProduct1 = new CrossProduct();
		crossProduct2 = new CrossProduct();
		dotProduct1 = new DotProduct();
		dotProduct2 = new DotProduct();
	}

	protected final class DummyTerminalNode extends TerminalNode {

		public int getIterationDepth(Map<String, Integer> inputDepths)
				throws IterationTypeMismatchException {
			return 0;
		}

		public void receiveCompletion(int inputIndex, Completion completion) {
		}

		public void receiveJob(int inputIndex, Job newJob) {
		}
	}

}
