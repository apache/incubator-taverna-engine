#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.menu;

import javax.swing.Action;

import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import ${package}.${classPrefix}Activity;
import ${package}.ui.config.${classPrefix}ConfigureAction;

public class ${classPrefix}ConfigureMenuAction extends
		AbstractConfigureActivityMenuAction<${classPrefix}Activity> {

	public ${classPrefix}ConfigureMenuAction() {
		super(${classPrefix}Activity.class);
	}

	@Override
	protected Action createAction() {
		${classPrefix}Activity a = findActivity();
		Action result = null;
		result = new ${classPrefix}ConfigureAction(findActivity(),
				getParentFrame());
		result.putValue(Action.NAME, "Configure example service");
		addMenuDots(result);
		return result;
	}

}
