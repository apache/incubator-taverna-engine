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
package \${package}.ui.menu;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.services.ServiceRegistry;

import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.ui.menu.ContextualMenuComponent;
import org.apache.taverna.ui.menu.MenuComponent;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;

import \${package}.ui.config.${classPrefix}ConfigureAction;

public class \${classPrefix}ConfigureMenuAction extends AbstractConfigureActivityMenuAction implements
		MenuComponent, ContextualMenuComponent {

	private static final URI ACTIVITY_TYPE = URI
			.create("http://example.com/2013/activity/\${rootArtifactId}");

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	public \${classPrefix}ConfigureMenuAction() {
		super(ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		Action result = new \${classPrefix}ConfigureAction(findActivity(), editManager, fileManager,
				activityIconManager, serviceDescriptionRegistry, serviceRegistry);
		result.putValue(Action.NAME, "Configure example service");
		addMenuDots(result);
		return result;
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setActivityIconManager(ActivityIconManager activityIconManager) {
		this.activityIconManager = activityIconManager;
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
