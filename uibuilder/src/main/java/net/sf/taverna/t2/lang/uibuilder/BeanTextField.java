package net.sf.taverna.t2.lang.uibuilder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * Bean field editor using a JTextField for short values
 * 
 * @author Tom Oinn
 */
public class BeanTextField extends BeanTextComponent implements
		AlignableComponent {

	private static final long serialVersionUID = 5968203948656812060L;

	public BeanTextField(Object target, String propertyName, boolean useLabel,
			Properties props) throws NoSuchMethodException {
		super(target, propertyName, useLabel, props);
	}

	public BeanTextField(Object target, String propertyName, Properties props)
			throws NoSuchMethodException {
		this(target, propertyName, true, props);
	}

	@SuppressWarnings("serial")
	@Override
	protected JTextComponent getTextComponent() {
		final JTextField result = new JTextField() {

			@Override
			public Dimension getMinimumSize() {
				return new Dimension(0, height);
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(0, height);
			}

			@Override
			public Dimension getMaximumSize() {
				return new Dimension(super.getMaximumSize().width, height);
			}
		};
		result.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setProperty();
				result.transferFocus();
			}
		});
		return new JTextField();
	}

}
