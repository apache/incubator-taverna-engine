/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.lang.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.text.BreakIterator;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A JTextArea whose text can be line wrapped either
 * based on the size of the scroll pane that holds the text area or by a given
 * line width in characters.
 * 
 * @author Alex Nenadic
 *
 */
@SuppressWarnings("serial")
public class LineWrappingTextArea extends JTextArea{
		
	String originalText;
	String[] wrappedLines;
	int lineWidth;
	
	final FontMetrics fontMetrics;
	
	public LineWrappingTextArea(String text){
		super(text);
		setFont(new Font("Monospaced", Font.PLAIN,12));
		fontMetrics  = this.getFontMetrics(this.getFont());
		setCaretPosition(0);
		originalText = text; 
	}
	
	/**
	 * Based on:
	 * @author Robert Hanson
	 * http://progcookbook.blogspot.com/2006/02/text-wrapping-function-for-java.html
	 * 
	 * This function takes a string value and a line length, and returns an array of 
	 * lines. Lines are cut on word boundaries, where the word boundary is a space 
	 * character. Spaces are included as the last character of a word, so most lines 
	 * will actually end with a space. This isn't too problematic, but will cause a 
	 * word to wrap if that space pushes it past the max line length.
	 * 
	 * This is a modified version - added various word boundaries based on Java's word's 
	 * BreakIterator in addition to simply space character as in the original function. 
	 *
	 * 
	 */
	private String [] wrapTextIntoLines (String text, int len)
	{		
		// BreakIterator will take care of word boundary characters for us
		BreakIterator wordIterator = BreakIterator.getWordInstance();
		wordIterator.setText(text);
        
		// return empty array for null text
		if (text == null)
			return new String[] {};

		// return text if len is zero or less
		if (len <= 0)
			return new String[] { text };

		// return text if less than length
		if (text.length() <= len)
			return new String[] { text };

		//char[] chars = text.toCharArray(); // no need to copy the text once again
		ArrayList<String> lines = new ArrayList<String>();
		StringBuffer line = new StringBuffer();
		StringBuffer word = new StringBuffer();

		for (int i = 0; i < text.length(); i++) {
		//for (int i = 0; i < chars.length; i++) {
			word.append(text.charAt(i));
			//word.append(chars[i]);

			if (wordIterator.isBoundary(i)){ // is this character a word boundary?
			//if (chars[i] == ' ') {
				if ((line.length() + word.length()) > len) {
					lines.add(line.toString());
					line.delete(0, line.length());
				}

				line.append(word);
				word.delete(0, word.length());
			}
		}

		// handle any extra chars in current word
		if (word.length() > 0) {
			if ((line.length() + word.length()) > len) {
				lines.add(line.toString());
				line.delete(0, line.length());
			}
			line.append(word);
		}

		// handle extra line
		if (line.length() > 0) {
			lines.add(line.toString());
		}

		String[] ret = new String[lines.size()];
		int c = 0; // counter
		for (String line2 : lines) {
			ret[c++] = line2;
		}

		return ret;
	}
	
	public void wrapText() {
		// Figure out how many characters to leave in one line
		// Based on the size of JTextArea on the screen and font
		// size - modified from
		// http://www.coderanch.com/t/517006/GUI/java/Visible-column-row-count-JTextArea

		// Get width of any char (font is monospaced)
		final int charWidth = fontMetrics.charWidth('M');
		// Get the width of the visible viewport of the scroll pane
		// that contains the lot - loop until such a scroll pane is found
		JScrollPane scrollPane = null;
		Component currentComponent = getParent();
		while (true) {
			if (currentComponent == null) {
				break;
			}
			if (currentComponent instanceof JScrollPane) {
				scrollPane = (JScrollPane) currentComponent;
				break;
			} else {
				currentComponent = currentComponent.getParent();
			}
		}
		int prefWidth;
		int maxChars;
		if (scrollPane == null) { // We did not find the parent scroll pane
			maxChars = 80; // wrap into lines of 80 characters
		} else {
			prefWidth = scrollPane.getVisibleRect().width;
//			if (scrollPane.getVerticalScrollBar().isVisible()){
//				prefWidth = prefWidth -	scrollPane.getVerticalScrollBar().getWidth();
//			}
			maxChars = prefWidth / charWidth - 3; // -3 because there is some
													// space between the text
													// area and the edge of
													// scroll pane so just to be sure
		}

		// If we have not wrapped lines before or the
		// width of the textarea has changed
		if (wrappedLines == null || lineWidth != maxChars) {
			lineWidth = maxChars;
			wrappedLines = wrapTextIntoLines(getText(), lineWidth);
		}

		if (wrappedLines.length >= 1) {
			setText(""); // clear the text area
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < wrappedLines.length; i++) {
				buff.append(wrappedLines[i] + "\n");
			}
			// Remove the last \n
			setText(buff.substring(0, buff.length()-1));
			repaint();
		}
	}
	
	public void wrapText(int numCharactersPerLine){
		// If we have not wrapped lines before or the
		// number of characters per line has changed since last 
		// we wrapped
		if (wrappedLines == null || lineWidth != numCharactersPerLine) {
			lineWidth = numCharactersPerLine;
			wrappedLines = wrapTextIntoLines(getText(), lineWidth);
		}

		if (wrappedLines.length >= 1) {
			setText(""); // clear the text area
			for (int i = 0; i < wrappedLines.length; i++) {
				append(wrappedLines[i] + "\n");
			}
			repaint();
		}

	}

	public void unwrapText(){
        setText(originalText);	
		repaint();
	}

}
