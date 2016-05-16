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

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;

import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

public class \${classPrefix}ActivityContextViewFactory implements ContextualViewFactory<Activity> {

	private static final URI ACTIVITY_TYPE = URI
			.create("http://example.com/2013/activity/\${rootArtifactId}");

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	@Override
	public boolean canHandle(Object object) {
		return object instanceof Activity && ((Activity) object).getType().equals(ACTIVITY_TYPE);
	}

	@Override
	public List<ContextualView> getViews(Activity selection) {
		return Arrays.<ContextualView>asList(new \${classPrefix}ContextualView(selection, editManager,
				fileManager, activityIconManager, serviceDescriptionRegistry, serviceRegistry));
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
