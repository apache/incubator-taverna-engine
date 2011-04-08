#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.view;

import java.awt.Frame;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;

import ${package}.${classPrefix}Activity;
import ${package}.${classPrefix}ActivityConfigurationBean;
import ${package}.ui.config.${classPrefix}ConfigureAction;

@SuppressWarnings("serial")
public class ${classPrefix}ContextualView extends ContextualView {
	private final ${classPrefix}Activity activity;
	private JLabel description = new JLabel("ads");

	public ${classPrefix}ContextualView(${classPrefix}Activity activity) {
		this.activity = activity;
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
		${classPrefix}ActivityConfigurationBean configuration = activity
				.getConfiguration();
		return "${classPrefix} service " + configuration.getExampleString();
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView() {
		${classPrefix}ActivityConfigurationBean configuration = activity
				.getConfiguration();
		description.setText("${classPrefix} service " + configuration.getExampleUri()
				+ " - " + configuration.getExampleString());
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
		return new ${classPrefix}ConfigureAction(activity, owner);
	}

}
