/*******************************************************************************
 * Copyright (C) 2009 Ingo Wassink of University of Twente, Netherlands and
 * The University of Manchester   
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

/**
 * @author Ingo Wassink
 * @author Ian Dunlop
 * @author Alan R Williams
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;

public class KeywordDocument extends DefaultStyledDocument {

	private static Logger logger = Logger
	.getLogger(KeywordDocument.class);
	
	private DefaultStyledDocument doc;
	private Element rootElement;

	private boolean multiLineComment;
	private MutableAttributeSet normal;
	private MutableAttributeSet keyword;
	private MutableAttributeSet comment;
	private MutableAttributeSet quote;
	private MutableAttributeSet port;
	
	private Set<String> keywords;
	
	private Set<String> ports;
	
	

	public KeywordDocument(Set<String> keywords) {
		doc = this;
		rootElement = doc.getDefaultRootElement();
		putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

		normal = new SimpleAttributeSet();
		StyleConstants.setForeground(normal, Color.black);

		comment = new SimpleAttributeSet();
		StyleConstants.setForeground(comment, new Color(0, 139, 69, 255));
		StyleConstants.setItalic(comment, true);

		keyword = new SimpleAttributeSet();
		StyleConstants.setForeground(keyword, Color.blue);
		StyleConstants.setBold(keyword, true);
		
				
		port = new SimpleAttributeSet();
		StyleConstants.setForeground(port, Color.magenta);

		quote = new SimpleAttributeSet();
		StyleConstants.setForeground(quote, Color.red);
		
		this.keywords = keywords;
		
		
		ports = new HashSet<String>();		
	}
	
	/**
	* Method for adding an port
	* @param name the name of the  port
	*/
	public void addPort(String name){
	  ports.add(name);
	  updateText();
	}
	
	/**
	 * Method for removing an  port
	 * @param name the name of the  port
	 */
	public void removePort(String name){
	  ports.remove(name);
	  updateText();
	} 
	
	/**
	 * Method for checking whether the name represents an input port
	 * @param name the name of the  port
	 * @return true if true
	 */
	private boolean isPort(String name){
	  return ports.contains(name);
	}
	
	
	/**
	 * Method for updating the whole text
	 */
	private void updateText(){
	  try{
	    processChangedLines(0, getLength() );
	  } catch(Exception e){
		  logger.error("Unable to update text", e);
	  }
	}

	/*
	 * Override to apply syntax highlighting after the document has been updated
	 */
	public void insertString(int offset, String str, AttributeSet a)
			throws BadLocationException {
		if (str.equals("{"))
			str = addMatchingBrace(offset);

		super.insertString(offset, str, a);
		processChangedLines(offset, str.length());
	}
	
	/*
	 * Override to apply syntax highlighting after the document has been updated
	 */
	public void remove(int offset, int length) throws BadLocationException {
		super.remove(offset, length);
		processChangedLines(offset, 0);
	}

	/*
	 * Determine how many lines have been changed, then apply highlighting to
	 * each line
	 */
	public void processChangedLines(int offset, int length)
			throws BadLocationException {
		String content = doc.getText(0, doc.getLength());

		// The lines affected by the latest document update

		int startLine = rootElement.getElementIndex(offset);
		int endLine = rootElement.getElementIndex(offset + length);

		// Make sure all comment lines prior to the start line are commented
		// and determine if the start line is still in a multi line comment

		setMultiLineComment(commentLinesBefore(content, startLine));

		// Do the actual highlighting

		for (int i = startLine; i <= endLine; i++) {
			applyHighlighting(content, i);
		}

		// Resolve highlighting to the next end multi line delimiter

		if (isMultiLineComment())
			commentLinesAfter(content, endLine);
		else
			highlightLinesAfter(content, endLine);
	}

	/*
	 * Highlight lines when a multi line comment is still 'open' (ie. matching
	 * end delimiter has not yet been encountered)
	 */
	private boolean commentLinesBefore(String content, int line) {
		int offset = rootElement.getElement(line).getStartOffset();

		// Start of comment not found, nothing to do

		int startDelimiter = lastIndexOf(content, getStartDelimiter(),
				offset - 2);

		if (startDelimiter < 0)
			return false;

		// Matching start/end of comment found, nothing to do

		int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);

		if (endDelimiter < offset & endDelimiter != -1)
			return false;

		// End of comment not found, highlight the lines

		doc.setCharacterAttributes(startDelimiter, offset - startDelimiter + 1,
				comment, false);
		return true;
	}

	/*
	 * Highlight comment lines to matching end delimiter
	 */
	private void commentLinesAfter(String content, int line) {
		int offset = rootElement.getElement(line).getEndOffset();

		// End of comment not found, nothing to do

		int endDelimiter = indexOf(content, getEndDelimiter(), offset);

		if (endDelimiter < 0)
			return;

		// Matching start/end of comment found, comment the lines

		int startDelimiter = lastIndexOf(content, getStartDelimiter(),
				endDelimiter);

		if (startDelimiter < 0 || startDelimiter <= offset) {
			doc.setCharacterAttributes(offset, endDelimiter - offset + 1,
					comment, false);
		}
	}

	/*
	 * Highlight lines to start or end delimiter
	 */
	private void highlightLinesAfter(String content, int line)
			throws BadLocationException {
		int offset = rootElement.getElement(line).getEndOffset();

		// Start/End delimiter not found, nothing to do

		int startDelimiter = indexOf(content, getStartDelimiter(), offset);
		int endDelimiter = indexOf(content, getEndDelimiter(), offset);

		if (startDelimiter < 0)
			startDelimiter = content.length();

		if (endDelimiter < 0)
			endDelimiter = content.length();

		int delimiter = Math.min(startDelimiter, endDelimiter);

		if (delimiter < offset)
			return;

		// Start/End delimiter found, reapply highlighting

		int endLine = rootElement.getElementIndex(delimiter);

		for (int i = line + 1; i < endLine; i++) {
			Element branch = rootElement.getElement(i);
			Element leaf = doc.getCharacterElement(branch.getStartOffset());
			AttributeSet as = leaf.getAttributes();

			if (as.isEqual(comment))
				applyHighlighting(content, i);
		}
	}

	/*
	 * Parse the line to determine the appropriate highlighting
	 */
	private void applyHighlighting(String content, int line)
			throws BadLocationException {
		int startOffset = rootElement.getElement(line).getStartOffset();
		int endOffset = rootElement.getElement(line).getEndOffset() - 1;

		int lineLength = endOffset - startOffset;
		int contentLength = content.length();

		if (endOffset >= contentLength)
			endOffset = contentLength - 1;

		// check for multi line comments
		// (always set the comment attribute for the entire line)

		if (endingMultiLineComment(content, startOffset, endOffset)
				|| isMultiLineComment()
				|| startingMultiLineComment(content, startOffset, endOffset)) {
			doc.setCharacterAttributes(startOffset,
					endOffset - startOffset + 1, comment, false);
			return;
		}

		// set normal attributes for the line

		doc.setCharacterAttributes(startOffset, lineLength, normal, true);

		// check for single line comment

		int index = content.indexOf(getSingleLineDelimiter(), startOffset);

		if ((index > -1) && (index < endOffset)) {
			doc.setCharacterAttributes(index, endOffset - index + 1, comment,
					false);
			endOffset = index - 1;
		}

		// check for tokens

		checkForTokens(content, startOffset, endOffset);
	}

	/*
	 * Does this line contain the start delimiter
	 */
	private boolean startingMultiLineComment(String content, int startOffset,
			int endOffset) throws BadLocationException {
		int index = indexOf(content, getStartDelimiter(), startOffset);

		if ((index < 0) || (index > endOffset))
			return false;
		else {
			setMultiLineComment(true);
			return true;
		}
	}

	/*
	 * Does this line contain the end delimiter
	 */
	private boolean endingMultiLineComment(String content, int startOffset,
			int endOffset) throws BadLocationException {
		int index = indexOf(content, getEndDelimiter(), startOffset);

		if ((index < 0) || (index > endOffset))
			return false;
		else {
			setMultiLineComment(false);
			return true;
		}
	}

	/*
	 * We have found a start delimiter and are still searching for the end
	 * delimiter
	 */
	private boolean isMultiLineComment() {
		return false;//multiLineComment;
	}

	private void setMultiLineComment(boolean value) {
		multiLineComment = value;
	}

	/*
	 * Parse the line for tokens to highlight
	 */
	private void checkForTokens(String content, int startOffset, int endOffset) {
		while (startOffset <= endOffset) {
			// skip the delimiters to find the start of a new token

			while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
				if (startOffset < endOffset)
					startOffset++;
				else
					return;
			}

			// Extract and process the entire token

			if (isQuoteDelimiter(content
					.substring(startOffset, startOffset + 1)))
				startOffset = getQuoteToken(content, startOffset, endOffset);
			else
				startOffset = getOtherToken(content, startOffset, endOffset);
		}
	}

	/*
	 * 
	 */
	private int getQuoteToken(String content, int startOffset, int endOffset) {
		String quoteDelimiter = content.substring(startOffset, startOffset + 1);
		String escapeString = getEscapeString(quoteDelimiter);

		int index;
		int endOfQuote = startOffset;

		// skip over the escape quotes in this quote

		index = content.indexOf(escapeString, endOfQuote + 1);

		while ((index > -1) && (index < endOffset)) {
			endOfQuote = index + 1;
			index = content.indexOf(escapeString, endOfQuote);
		}

		// now find the matching delimiter

		index = content.indexOf(quoteDelimiter, endOfQuote + 1);

		if ((index < 0) || (index > endOffset))
			endOfQuote = endOffset;
		else
			endOfQuote = index;

		doc.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1,
				quote, false);

		return endOfQuote + 1;
	}

	/*
	 * 
	 */
	private int getOtherToken(String content, int startOffset, int endOffset) {
		int endOfToken = startOffset + 1;

		while (endOfToken <= endOffset) {
			if (isDelimiter(content.substring(endOfToken, endOfToken + 1)))
				break;

			endOfToken++;
		}

		String token = content.substring(startOffset, endOfToken);

		if (isKeyword(token)) {
			doc.setCharacterAttributes(startOffset, endOfToken - startOffset,
					keyword, false);
		} else if(isPort(token)){
			doc.setCharacterAttributes(startOffset, endOfToken - startOffset,
			        port, false);
		}

		return endOfToken + 1;
	}

	/*
	 * Assume the needle will the found at the start/end of the line
	 */
	private int indexOf(String content, String needle, int offset) {
		int index;

		while ((index = content.indexOf(needle, offset)) != -1) {
			String text = getLine(content, index).trim();

			if (text.startsWith(needle) || text.endsWith(needle))
				break;
			else
				offset = index + 1;
		}

		return index;
	}

	/*
	 * Assume the needle will the found at the start/end of the line
	 */
	private int lastIndexOf(String content, String needle, int offset) {
		int index;

		while ((index = content.lastIndexOf(needle, offset)) != -1) {
			String text = getLine(content, index).trim();

			if (text.startsWith(needle) || text.endsWith(needle))
				break;
			else
				offset = index - 1;
		}

		return index;
	}

	private String getLine(String content, int offset) {
		int line = rootElement.getElementIndex(offset);
		Element lineElement = rootElement.getElement(line);
		int start = lineElement.getStartOffset();
		int end = lineElement.getEndOffset();
		return content.substring(start, end - 1);
	}

	/*
	 * Override for other languages
	 */
	protected boolean isDelimiter(String character) {
		String operands = ";:{}()[]+-/%<=>!&|^~*,.";

		if (Character.isWhitespace(character.charAt(0))
				|| operands.indexOf(character) != -1)
			return true;
		else
			return false;
	}

	/*
	 * Override for other languages
	 */
	protected boolean isQuoteDelimiter(String character) {
		String quoteDelimiters = "\"'";

		if (quoteDelimiters.indexOf(character) < 0)
			return false;
		else
			return true;
	}

	/*
	 * Override for other languages
	 */
	protected boolean isKeyword(String token) {
		return keywords.contains(token);
	}

	/*
	 * Override for other languages
	 */
	protected String getStartDelimiter() {
		return "/*";
	}

	/*
	 * Override for other languages
	 */
	protected String getEndDelimiter() {
		return "*/";
	}

	/*
	 * Override for other languages
	 */
	protected String getSingleLineDelimiter() {
		return "#";
	}

	/*
	 * Override for other languages
	 */
	protected String getEscapeString(String quoteDelimiter) {
		return "\\" + quoteDelimiter;
	}

	/*
	 * 
	 */
	protected String addMatchingBrace(int offset) throws BadLocationException {
		StringBuffer whiteSpace = new StringBuffer();
		int line = rootElement.getElementIndex(offset);
		int i = rootElement.getElement(line).getStartOffset();

		while (true) {
			String temp = doc.getText(i, 1);

			if (temp.equals(" ") || temp.equals("\t")) {
				whiteSpace.append(temp);
				i++;
			} else
				break;
		}

		return "{\n" + whiteSpace.toString() + "\t\n" + whiteSpace.toString()
				+ "}";
	}
}
