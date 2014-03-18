/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * @author alanrw
 *
 */
public class ReadOnlyTextArea extends JTextArea {
	
	public ReadOnlyTextArea () {
		super();
		setFields();
	}
	
	public ReadOnlyTextArea(Document doc) {
		super(doc);
		setFields();
	}
	
	public ReadOnlyTextArea (Document doc, String text, int rows, int columns) {
		super(doc,text,rows,columns);
		setFields();
	}
	
	public ReadOnlyTextArea(int rows, int columns) {
		super(rows, columns);
		setFields();
	}
	
	public ReadOnlyTextArea(String text) {
		super(text);
		setFields();
	}
	
	public ReadOnlyTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		setFields();
	}

	private void setFields() {
		super.setEditable(false);
		super.setLineWrap(true);
		super.setWrapStyleWord(true);
	}
}
