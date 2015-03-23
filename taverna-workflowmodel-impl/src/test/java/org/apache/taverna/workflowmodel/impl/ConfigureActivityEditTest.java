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

package org.apache.taverna.workflowmodel.impl;

import static org.junit.Assert.*;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.processor.activity.AbstractActivity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigureActivityEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	DummyActivity activity;

	@Before
	public void setup() throws Exception {
		activity = new DummyActivity();
	}

	@Test
	public void testDoEdit() throws Exception {
		String bean = "bob";
		Edit<?> edit = edits.getConfigureActivityEdit(activity, bean);
		edit.doEdit();
		assertTrue(activity.isConfigured);
		assertEquals("bob", activity.getConfiguration());
	}

	@Test
	public void testIsApplied() throws Exception {
		String bean = "bob";
		Edit<?> edit = edits.getConfigureActivityEdit(activity, bean);
		assertFalse(edit.isApplied());
		edit.doEdit();
		assertTrue(edit.isApplied());
	}

	@Test
	public void testSubject() {
		String bean = "bob";
		Edit<?> edit = edits.getConfigureActivityEdit(activity, bean);
		assertEquals(activity, edit.getSubject());
	}

	class DummyActivity extends AbstractActivity<Object> {
		public boolean isConfigured = false;
		public Object configBean = null;

		@Override
		public void configure(Object conf)
				throws ActivityConfigurationException {
			isConfigured = true;
			configBean = conf;
		}

		@Override
		public Object getConfiguration() {
			return configBean;
		}

	}
}
