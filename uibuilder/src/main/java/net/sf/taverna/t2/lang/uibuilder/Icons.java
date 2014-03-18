package net.sf.taverna.t2.lang.uibuilder;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * Manage icons for the UIBuilder
 * 
 * @author Tom Oinn
 * 
 */
public abstract class Icons {

	private static Logger logger = Logger
	.getLogger(Icons.class);

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
				logger.error("Unable to get icon resource", ex);
			}
		}
		return icons.get(iconNameLC);
	}

}
