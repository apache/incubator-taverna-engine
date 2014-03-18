/**
 * 
 */
package net.sf.taverna.t2.lang.ui;

import java.util.regex.Pattern;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/**
 * @author alanrw
 *
 */
public class SanitisingDocumentFilter extends DocumentFilter {
	
	private static SanitisingDocumentFilter INSTANCE = new SanitisingDocumentFilter();
	
	private SanitisingDocumentFilter () {
		super();
	}
	
	public static void addFilterToComponent(JTextComponent c) {
		Document d = c.getDocument();
		if (d instanceof AbstractDocument) {
			((AbstractDocument) d).setDocumentFilter(INSTANCE);
		} 		
	}
	
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		
		fb.insertString(offset, sanitiseString(string), attr);
	}
	
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
		      String text, javax.swing.text.AttributeSet attr)

		      throws BadLocationException {
		           fb.replace(offset, length, sanitiseString(text), attr);   
		 }
	
	private static String sanitiseString(String text) {
		String result = text;
		if (Pattern.matches("\\w++", text) == false) {
			result = "";
			for (char c : text.toCharArray()) {
				if (Character.isLetterOrDigit(c) || c == '_') {
					result += c;
				} else {
					result += "_";
				}
			}
		}
		return result;		
	}
}
