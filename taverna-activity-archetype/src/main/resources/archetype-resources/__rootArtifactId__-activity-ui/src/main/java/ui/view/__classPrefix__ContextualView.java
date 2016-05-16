##    Licensed to the Apache Software Foundation (ASF) under one or more
##    contributor license agreements.  See the NOTICE file distributed with
##    this work for additional information regarding copyright ownership.
##    The ASF licenses this file to You under the Apache License, Version 2.0
##    (the "License"); you may not use this file except in compliance with
##    the License.  You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
##    Unless required by applicable law or agreed to in writing, software
##    distributed under the License is distributed on an "AS IS" BASIS,
##    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
##    See the License for the specific language governing permissions and
##    limitations under the License.
##
## Note: Above Velocity comment should NOT be included in generated
## code from the archetype
package \${package}.ui.view;

import java.awt.Frame;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.services.ServiceRegistry;

import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.ActivityContextualView;

import org.apache.taverna.scufl2.api.activity.Activity;

import \${package}.ui.config.${classPrefix}ConfigureAction;

@SuppressWarnings("serial")
public class \${classPrefix}ContextualView extends ActivityContextualView {

	private final EditManager editManager;
	private final FileManager fileManager;
	private final ActivityIconManager activityIconManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;
	private final ServiceRegistry serviceRegistry;

	private JLabel description = new JLabel("ads");

	public \${classPrefix}ContextualView(Activity activity, EditManager editManager,
			FileManager fileManager, ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry, ServiceRegistry serviceRegistry) {
		super(activity);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
		this.serviceRegistry = serviceRegistry;
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		JPanel jPanel = new JPanel();
		jPanel.add(description);
		refreshView();
		return jPanel;
	}

	@Override
	public String getViewTitle() {
		JsonNode configuration = getConfigBean().getJson();
		return "\${classPrefix} service " + configuration.get("exampleString").asText();
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView() {
		JsonNode configuration = getConfigBean().getJson();
		description.setText("\${classPrefix} service " + configuration.get("exampleUri").asText()
				+ " - " + configuration.get("exampleString").asText());
		// TODO: Might also show extra service information looked
		// up dynamically from endpoint/registry
	}

	/**
	 * View position hint
	 */
	@Override
	public int getPreferredPosition() {
		// We want to be on top
		return 100;
	}

	@Override
	public Action getConfigureAction(final Frame owner) {
		return new \${classPrefix}ConfigureAction(getActivity(), editManager, fileManager,
				activityIconManager, serviceDescriptionRegistry, serviceRegistry);
	}

}
