#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.view;

import java.awt.Frame;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.services.ServiceRegistry;

import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityContextualView;

import org.apache.taverna.scufl2.api.activity.Activity;

import ${package}.ui.config.${classPrefix}ConfigureAction;

@SuppressWarnings("serial")
public class ${classPrefix}ContextualView extends ActivityContextualView {

	private final EditManager editManager;
	private final FileManager fileManager;
	private final ActivityIconManager activityIconManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;
	private final ServiceRegistry serviceRegistry;

	private JLabel description = new JLabel("ads");

	public ${classPrefix}ContextualView(Activity activity, EditManager editManager,
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
		return "${classPrefix} service " + configuration.get("exampleString").asText();
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView() {
		JsonNode configuration = getConfigBean().getJson();
		description.setText("${classPrefix} service " + configuration.get("exampleUri").asText()
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
		return new ${classPrefix}ConfigureAction(getActivity(), editManager, fileManager,
				activityIconManager, serviceDescriptionRegistry, serviceRegistry);
	}

}
