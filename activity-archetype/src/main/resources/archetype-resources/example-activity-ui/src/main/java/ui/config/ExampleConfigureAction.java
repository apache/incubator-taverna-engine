#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

import ${package}.ExampleActivity;
import ${package}.ExampleActivityConfigurationBean;

@SuppressWarnings("serial")
public class ExampleConfigureAction
		extends
		ActivityConfigurationAction<ExampleActivity, ExampleActivityConfigurationBean> {

	public ExampleConfigureAction(ExampleActivity activity, Frame owner) {
		super(activity);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog<ExampleActivity, ExampleActivityConfigurationBean> currentDialog = ActivityConfigurationAction
				.getDialog(getActivity());
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		ExampleConfigurationPanel panel = new ExampleConfigurationPanel(
				getActivity());
		ActivityConfigurationDialog<ExampleActivity, ExampleActivityConfigurationBean> dialog = new ActivityConfigurationDialog<ExampleActivity, ExampleActivityConfigurationBean>(
				getActivity(), panel);

		ActivityConfigurationAction.setDialog(getActivity(), dialog);

	}

}
