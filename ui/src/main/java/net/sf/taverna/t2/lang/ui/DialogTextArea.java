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

	/**
	 * 
	 */
	public DialogTextArea() {
		setFont(Font.getFont("Dialog"));
	}

	/**
	 * @param text
	 */
	public DialogTextArea(String text) {
		super(text);
		setFont(Font.getFont("Dialog"));
	}

	/**
	 * @param doc
	 */
	public DialogTextArea(Document doc) {
		super(doc);
		setFont(Font.getFont("Dialog"));
	}

	/**
	 * @param rows
	 * @param columns
	 */
	public DialogTextArea(int rows, int columns) {
		super(rows, columns);
		setFont(Font.getFont("Dialog"));
	}

	/**
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public DialogTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		setFont(Font.getFont("Dialog"));
	}

	/**
	 * @param doc
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public DialogTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		setFont(Font.getFont("Dialog"));
	}

}
