#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.view;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

import ${package}.${classPrefix}Activity;

public class ${classPrefix}ActivityContextViewFactory implements
		ContextualViewFactory<${classPrefix}Activity> {

	public boolean canHandle(Object selection) {
		return selection instanceof ${classPrefix}Activity;
	}

	public List<ContextualView> getViews(${classPrefix}Activity selection) {
		return Arrays.<ContextualView>asList(new ${classPrefix}ContextualView(selection));
	}
	
}
