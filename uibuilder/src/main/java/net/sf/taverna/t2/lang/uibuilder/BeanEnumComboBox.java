package net.sf.taverna.t2.lang.uibuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JComboBox;

/**
 * Bean property editor for enumerated property types, rendering the enumeration
 * as a combo box
 * 
 * @author Tom Oinn
 * 
 */
public class BeanEnumComboBox extends BeanComponent implements
		AlignableComponent {

	private static final long serialVersionUID = -6892016525599793149L;

	private Object[] possibleValues;
	private static int height = 24;
	private JComboBox value;

	public BeanEnumComboBox(Object target, String propertyName, Properties props)
			throws NoSuchMethodException {
		this(target, propertyName, true, props);
	}

	public BeanEnumComboBox(Object target, String propertyName,
			boolean useLabel, Properties props) throws NoSuchMethodException {
		super(target, propertyName, useLabel, props);
		setLayout(new BorderLayout());
		// Check that this is actually an enumeration type
		if (!propertyType.isEnum()) {
			throw new IllegalArgumentException(
					"Can't use BeanEnumComboBox on a non Enumeration property");
		}
		possibleValues = propertyType.getEnumConstants();
		value = new JComboBox(possibleValues) {

			private static final long serialVersionUID = -7712225463703816146L;

			@Override
			public Dimension getMinimumSize() {
				return new Dimension(super.getMinimumSize().width, height);
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, height);
			}

			@Override
			public Dimension getMaximumSize() {
				return new Dimension(super.getMaximumSize().width, height);
			}
		};
		value.setSelectedIndex(currentValueIndex());
		value.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					currentObjectValue = value.getSelectedItem();
					setProperty();
				}
			}
		});
		addLabel();
		add(value, BorderLayout.CENTER);
	}

	private int currentValueIndex() {
		Object currentValue = getProperty();
		for (int i = 0; i < possibleValues.length; i++) {
			if (currentValue.equals(possibleValues[i])) {
				return i;
			}
		}
		return -1;
	}

	@Override
	protected void updateComponent() {
		value.setSelectedIndex(currentValueIndex());
	}

}
