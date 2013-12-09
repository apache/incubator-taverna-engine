#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.serviceprovider;

import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.workbench.activityicons.ActivityIconSPI;

public class ${classPrefix}ServiceIcon implements ActivityIconSPI {

	private static final URI ACTIVITY_TYPE = URI
			.create("http://example.com/2013/activity/${rootArtifactId}");

	private static Icon icon;

	@Override
	public int canProvideIconScore(URI activityType) {
		if (ACTIVITY_TYPE.equals(activityType)) {
			return DEFAULT_ICON + 1;
		}
		return NO_ICON;
	}

	@Override
	public Icon getIcon(URI activityType) {
		return getIcon();
	}

	public static Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(${classPrefix}ServiceIcon.class.getResource("/exampleIcon.png"));
		}
		return icon;
	}

}
