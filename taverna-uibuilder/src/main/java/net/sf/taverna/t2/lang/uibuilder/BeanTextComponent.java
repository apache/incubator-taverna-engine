package net.sf.taverna.t2.lang.uibuilder;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Abstract superclass for all bean property editors based on text components
 * 
 * @author Tom Oinn
 */
public abstract class BeanTextComponent extends BeanComponent {

	private static final long serialVersionUID = -9133735782847457090L;
	private String regex = null;
	private JTextComponent text;
	private String originalValue = null;
	private boolean edited = false;

	protected BeanTextComponent(Object target, String propertyName,
			Properties props) throws NoSuchMethodException {
		this(target, propertyName, true, props);
	}

	protected BeanTextComponent(Object target, String propertyName,
			boolean useLabel, Properties props) throws NoSuchMethodException {
		super(target, propertyName, useLabel, props);
		setLayout(new BorderLayout());
		if (propertyType.equals(Boolean.class)) {
			setRegex("\\A((true)|(false))\\Z");
		} else if (propertyType.equals(Character.class)) {
			setRegex("\\A.\\Z");
		}
		text = getTextComponent();
		originalValue = getPropertyAsString();
		text.setText(originalValue);
		text.setEditable(editable);
		add(text, BorderLayout.CENTER);
		if (editable) {
			text.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					valueChangedInEditor();
				}

				public void insertUpdate(DocumentEvent e) {
					valueChangedInEditor();
				}

				public void removeUpdate(DocumentEvent e) {
					valueChangedInEditor();
				}

				private void valueChangedInEditor() {
					BeanTextComponent.this.valueChangedInEditor();
				}
			});
			text.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					//
				}

				public void focusLost(FocusEvent e) {
					// System.out.println("Focus lost : valid = "
					// + currentValueValid);
					if (currentValueValid) {
						if (isEdited()) {
							BeanTextComponent.this.setProperty();
							originalValue = text.getText();
						}
					} else {
						originalValue = getPropertyAsString();
						text.setText(originalValue);
					}
					setEdited(false);
				}

			});
		}
		addLabel();
	}

	private boolean isEdited() {
		return this.edited;
	}

	private void setEdited(boolean edited) {
		if (edited == this.edited) {
			return;
		}
		this.edited = edited;
		text.setBackground(edited ? editedColour : uneditedColour);
		if (!edited) {
			setValid(true);
		}
	}

	public void updateComponent() {
		originalValue = getPropertyAsString();
		text.setText(originalValue);
		setEdited(false);
	}

	private void valueChangedInEditor() {
		if (text.getText().equals(originalValue)) {
			setEdited(false);
			return;
		}
		setEdited(true);
		// Check for regex
		if (regex != null) {
			if (text.getText().matches(regex) == false) {
				// System.out.println(text.getText() + ".matches(" + regex
				// + ")==false");
				setValid(false);
				return;
			}
		}
		// Delegate to constructor for non-string classes
		if (!propertyType.equals(String.class)) {
			try {
				Constructor<?> cons = null;
				if (propertyType.equals(Character.class)
						&& text.getText().length() > 0) {
					currentObjectValue = text.getText().toCharArray()[0];
				} else {
					cons = propertyType.getConstructor(String.class);
					currentObjectValue = cons.newInstance(text.getText());
				}
			} catch (Throwable t) {
				setValid(false);
				return;
			}
		} else {
			currentObjectValue = text.getText();
		}
		setValid(true);
	}

	private void setValid(final boolean valid) {
		if (valid == currentValueValid) {
			return;
		}
		currentValueValid = valid;
		text.setForeground(valid ? validColour : invalidColour);
		setLabelColour(valid ? validColour : invalidColour);
		text.repaint();
	}

	/**
	 * Implement this to provide the actual UI component used to show the
	 * content of the field. Done this way so you can choose what component to
	 * use.
	 * 
	 * @return
	 */
	protected abstract JTextComponent getTextComponent();

	/**
	 * Set the regular expression used to validate the contents of the text
	 * field
	 * 
	 * @param regex
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

}
