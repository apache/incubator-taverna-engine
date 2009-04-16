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
		String iconNameLC = iconName.toLowerCase();
		if (!icons.containsKey(iconNameLC)) {
			try {
				URL iconURL = Icons.class.getResource(iconName + ".png");
				icons.put(iconNameLC, new ImageIcon(iconURL));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return icons.get(iconNameLC);
	}

}
