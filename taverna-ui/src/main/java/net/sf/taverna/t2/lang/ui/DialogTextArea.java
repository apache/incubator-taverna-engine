/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.Font;

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * @author alanrw
 *
 */
public class DialogTextArea extends JTextArea {

	private static Font newFont = Font.decode("Dialog");
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2329063139827993252L;

	/**
	 * 
	 */
	public DialogTextArea() {
		updateFont();
	}

	/**
	 * @param text
	 */
	public DialogTextArea(String text) {
		super(text);
		updateFont();
	}

	/**
	 * @param doc
	 */
	public DialogTextArea(Document doc) {
		super(doc);
		updateFont();
	}

	/**
	 * @param rows
	 * @param columns
	 */
	public DialogTextArea(int rows, int columns) {
		super(rows, columns);
		updateFont();
	}

	/**
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public DialogTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		updateFont();
	}

	/**
	 * @param doc
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public DialogTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		updateFont();
	}
	
	private void updateFont() {
		if (newFont != null) {
			this.setFont(newFont);
		}
	}

}
