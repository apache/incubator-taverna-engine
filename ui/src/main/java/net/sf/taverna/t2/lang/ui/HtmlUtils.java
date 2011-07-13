/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

/**
 * @author alanrw
 *
 */
public class HtmlUtils {
	
	public static JEditorPane createEditorPane(String html) {
		JEditorPane result = new JEditorPane("text/html", html);
		result.setEditable(false);
		return result;
	}
	
	public static JPanel panelForHtml(JEditorPane editorPane) {
		JPanel result = new JPanel();

		result.setLayout(new BorderLayout());

		result.add(editorPane, BorderLayout.CENTER);
		return result;
	}

	public static String getStyle(String backgroundColour) {
		String style = "<style type='text/css'>";
		style += "table {align:center; border:solid black 1px; background-color:"
				+ backgroundColour
				+ ";width:100%; height:100%; overflow:auto;}";
		style += "</style>";
		return style;
	}
	
	public static String buildTableOpeningTag() {
		String result = "<table ";
		Map<String, String> props = getTableProperties();
		for (String key : props.keySet()) {
			result += key + "=\"" + props.get(key) + "\" ";
		}
		result += ">";
		return result;
	}

	public static Map<String, String> getTableProperties() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("border", "1");
		return result;
	}

	public static String getHtmlHead(String backgroundColour) {
		return "<html><head>" + getStyle(backgroundColour) + "</head><body>";
	}
}
