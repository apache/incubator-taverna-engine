#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.menu;

import java.net.URI;

import javax.swing.Action;

import uk.org.taverna.commons.services.ServiceRegistry;

import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.ui.menu.ContextualMenuComponent;
import net.sf.taverna.t2.ui.menu.MenuComponent;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

import ${package}.ui.config.${classPrefix}ConfigureAction;

public class ${classPrefix}ConfigureMenuAction extends AbstractConfigureActivityMenuAction implements
		MenuComponent, ContextualMenuComponent {

	private static final URI ACTIVITY_TYPE = URI
			.create("http://example.com/2013/activity/${rootArtifactId}");

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	public ${classPrefix}ConfigureMenuAction() {
		super(ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		Action result = new ${classPrefix}ConfigureAction(findActivity(), editManager, fileManager,
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
