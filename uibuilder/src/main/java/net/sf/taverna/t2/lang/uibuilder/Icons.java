package net.sf.taverna.t2.lang.uibuilder;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * Manage icons for the UIBuilder
 * 
 * @author Tom Oinn
 * 
 */
public abstract class Icons {

	private static Map<String, ImageIcon> icons;

	static {
		icons = new HashMap<String, ImageIcon>();
	}

	static synchronized ImageIcon getIcon(String iconName) {
		if (!icons.containsKey(iconName.toLowerCase())) {
			try {
				URL iconURL = Icons.class.getClassLoader().getResource(
						"net/sf/taverna/t2/platform/ui/" + iconName
								+ ".png");
				icons.put(iconName.toLowerCase(), new ImageIcon(iconURL));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return icons.get(iconName.toLowerCase());
	}

}
