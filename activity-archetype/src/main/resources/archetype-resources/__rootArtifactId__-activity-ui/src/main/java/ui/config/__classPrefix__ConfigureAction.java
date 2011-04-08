#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

import ${package}.${classPrefix}Activity;
import ${package}.${classPrefix}ActivityConfigurationBean;

@SuppressWarnings("serial")
public class ${classPrefix}ConfigureAction
		extends
		ActivityConfigurationAction<${classPrefix}Activity,
        ${classPrefix}ActivityConfigurationBean> {

	public ${classPrefix}ConfigureAction(${classPrefix}Activity activity, Frame owner) {
		super(activity);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog<${classPrefix}Activity, ${classPrefix}ActivityConfigurationBean> currentDialog = ActivityConfigurationAction
				.getDialog(getActivity());
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		${classPrefix}ConfigurationPanel panel = new ${classPrefix}ConfigurationPanel(
				getActivity());
		ActivityConfigurationDialog<${classPrefix}Activity,
        ${classPrefix}ActivityConfigurationBean> dialog = new ActivityConfigurationDialog<${classPrefix}Activity, ${classPrefix}ActivityConfigurationBean>(
				getActivity(), panel);

		ActivityConfigurationAction.setDialog(getActivity(), dialog);

	}

}
