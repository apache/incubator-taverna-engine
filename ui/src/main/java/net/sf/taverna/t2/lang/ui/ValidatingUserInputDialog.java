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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.sf.taverna.t2.lang.ui.icons.Icons;

/**
 * A user input dialog that validates the input as the user is entering the
 * input and gives feedback on why the input is invalid.
 * 
 * @author David Withers
 */
public class ValidatingUserInputDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private String inputTitle;

	private JButton okButton;

	private JButton cancelButton;

	private JTextArea inputText;

	private JPanel inputPanel;

	private JLabel iconLabel;

	private boolean valid = true;

	private boolean result = false;

	/**
	 * Constructs a new instance of ValidatingUserInputDialog.
	 * 
	 * @param inputTitle
	 *            the title for the dialog.
	 * @param inputMessage
	 *            the message describing what the user should input.
	 */
	public ValidatingUserInputDialog(String inputTitle, JPanel inputPanel) {
		this.inputTitle = inputTitle;
		this.inputPanel = inputPanel;
		initialize();
	}

	/**
	 * Adds a text component and the rules for a valid user entry.
	 * 
	 * @param textComponent
	 *            the text component to validate
	 * @param invalidInputs
	 *            a set of inputs that are not valid. This is typically a set of
	 *            already used identifiers to avoid name clashes. Can be an
	 *            empty set or null.
	 * @param invalidInputsMessage
	 *            the message to display if the user enters a value that is in
	 *            invalidInputs.
	 * @param inputRegularExpression
	 *            a regular expression that specifies a valid user input. Can be
	 *            null.
	 * @param inputRegularExpressionMessage
	 *            the message to display if the user enters a value that doesn't
	 *            match the inputRegularExpression.
	 */
	public void addTextComponentValidation(final JTextComponent textComponent,
			final String inputMessage, final Set<String> invalidInputs,
			final String invalidInputsMessage,
			final String inputRegularExpression,
			final String inputRegularExpressionMessage) {
		textComponent.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				verify(textComponent.getText(), inputMessage, invalidInputs,
						invalidInputsMessage, inputRegularExpression,
						inputRegularExpressionMessage);
			}

			public void removeUpdate(DocumentEvent e) {
				verify(textComponent.getText(), inputMessage, invalidInputs,
						invalidInputsMessage, inputRegularExpression,
						inputRegularExpressionMessage);
			}
		});
		textComponent.addKeyListener(new KeyAdapter() {
			boolean okDown = false;
			
			public void keyPressed(KeyEvent e) {
				if (okButton.isEnabled() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					okDown = true;
				}
			}
			public void keyReleased(KeyEvent e) {
				if (okDown && okButton.isEnabled() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButton.doClick();
				}
			}
		});
		textComponent.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (valid) {
					setMessage(inputMessage);
				}
			}
		});
	}

	/**
	 * Adds a component and a message to display when the component is in focus.
	 * 
	 * @param component
	 *            the component to add
	 * @param message
	 *            the message to display when the component is in focus
	 */
	public void addMessageComponent(Component component, final String message) {
		component.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (valid) {
					setMessage(message);
				}
			}
		});
	}

	private void initialize() {
		setLayout(new BorderLayout());

		JPanel messagePanel = new JPanel(new BorderLayout());
		messagePanel.setBorder(new EmptyBorder(5, 5, 0, 0));
		messagePanel.setBackground(Color.WHITE);

		add(messagePanel, BorderLayout.NORTH);

		JLabel inputLabel = new JLabel(inputTitle);
		inputLabel.setBackground(Color.WHITE);
		inputLabel.setFont(inputLabel.getFont().deriveFont(Font.BOLD));
		messagePanel.add(inputLabel, BorderLayout.NORTH);

		inputText = new JTextArea();
		inputText.setMargin(new Insets(5, 10, 10, 10));
		inputText.setMinimumSize(new Dimension(0, 30));
		inputText.setFont(inputText.getFont().deriveFont(11f));
		inputText.setEditable(false);
		inputText.setFocusable(false);
		messagePanel.add(inputText, BorderLayout.CENTER);

		iconLabel = new JLabel();
		messagePanel.add(iconLabel, BorderLayout.WEST);

		add(inputPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		add(buttonPanel, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result = valid;
				setVisible(false);
			}
		});
		okButton.setEnabled(false);
		buttonPanel.add(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		buttonPanel.add(cancelButton);

		setModal(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	public void setMessage(String message) {
		iconLabel.setIcon(null);
		inputText.setText(message);
	}

	public void setWarningMessage(String message) {
		iconLabel.setIcon(Icons.warningIcon);
		inputText.setText(message);
	}

	public void setErrorMessage(String message) {
		iconLabel.setIcon(Icons.severeIcon);
		inputText.setText(message);
	}

	private void verify(String text, String inputMessage,
			Set<String> invalidInputs, String invalidInputsMessage,
			String inputRegularExpression, String inputRegularExpressionMessage) {
		if (invalidInputs != null && invalidInputs.contains(text)) {
			setErrorMessage(invalidInputsMessage);
			valid = false;
		} else if (inputRegularExpression != null
				&& !text.matches(inputRegularExpression)) {
			setErrorMessage(inputRegularExpressionMessage);
			valid = false;
		} else {
			setMessage(inputMessage);
			valid = true;
		}
		okButton.setEnabled(valid);
//		okButton.setSelected(valid);
	}

	/**
	 * Show the dialog relative to the component. If the component is null then
	 * the dialog is shown in the centre of the screen.
	 * 
	 * Returns true if the user input is valid.
	 * 
	 * @param component
	 *            the component that the dialog is shown relative to
	 * @return true if the user input is valid
	 */
	public boolean show(Component component) {
		setLocationRelativeTo(component);
		setVisible(true);
		dispose();
		return result;
	}

}
