package net.sf.taverna.t2.lang.uibuilder;

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

/**
 * Bean editor based on a JTextArea for use with longer strings such as
 * descriptions. Supports the 'nofilter' property, if this is not specified then
 * the text inserted initially (but not on subsequent events such as property
 * change messages) will be filtered to remove multiple whitespace elements,
 * replacing them with spaces, and to trim leading and trailing whitespace.
 * 
 * @author Tom Oinn
 * 
 */
public class BeanTextArea extends BeanTextComponent implements
		AlignableComponent {

	private static final long serialVersionUID = 6418526320837944375L;
	boolean initialized = false;

	public BeanTextArea(Object target, String propertyName, Properties props)
			throws NoSuchMethodException {
		super(target, propertyName, props);
		initialized = true;
	}

	@SuppressWarnings( { "serial", "unchecked" })
	@Override
	protected JTextComponent getTextComponent() {
		JTextArea result = new JTextArea() {
			@Override
			public void setText(String text) {
				if (!initialized && !getProperties().containsKey("nofilter")) {
					super.setText(text.replaceAll("[ \\t\\n\\x0B\\f\\r]+", " ")
							.trim());
				} else {
					super.setText(text);
				}
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(0, super.getPreferredSize().height);
			}

			@Override
			public Dimension getMinimumSize() {
				return new Dimension(0, super.getPreferredSize().height);
			}
		};
		// Fix to add borders to JTextArea on old look and feel implementations,
		// the new one (Nimbus) already has this
		if (!UIManager.getLookAndFeel().getName().equals("Nimbus")) {
			result.setBorder(UIManager.getBorder("TextField.border"));
			result.setFont(UIManager.getFont("TextField.font"));
		}
		// Change tab behaviour to allow tab to move to the next field - this
		// effectively prevents a tab being placed in the text area but hey, we
		// don't really want people doing that anyway in these cases.
		Set set = new HashSet(
				result
						.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		set.add(KeyStroke.getKeyStroke("TAB"));
		result.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);

		set = new HashSet(
				result
						.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		set.add(KeyStroke.getKeyStroke("shift TAB"));
		result.setFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set);

		result.setLineWrap(true);
		result.setWrapStyleWord(true);
		return result;
	}

}
