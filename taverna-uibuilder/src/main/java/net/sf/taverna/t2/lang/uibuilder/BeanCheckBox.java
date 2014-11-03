package net.sf.taverna.t2.lang.uibuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JCheckBox;

/**
 * Bean field editor using a JCheckBox to handle boolean and Boolean field types
 * 
 * @author Tom Oinn
 */
public class BeanCheckBox extends BeanComponent implements AlignableComponent {

	private static final long serialVersionUID = -2842617445268734650L;
	private JCheckBox value;

	public BeanCheckBox(Object target, String propertyName, Properties props)
			throws NoSuchMethodException {
		this(target, propertyName, true, props);
	}

	public BeanCheckBox(Object target, String propertyName, boolean useLabel,
			Properties props) throws NoSuchMethodException {
		super(target, propertyName, useLabel, props);
		setLayout(new BorderLayout());
		value = new JCheckBox();
		value.setSelected(getBooleanProperty());
		value.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				boolean isSelected = value.isSelected();
				currentObjectValue = isSelected;
				setProperty();
			}
		});
		addLabel();
		value.setOpaque(false);
		add(Box.createHorizontalGlue(), BorderLayout.CENTER);
		add(value, BorderLayout.EAST);
	}

	@Override
	protected void updateComponent() {
		value.setSelected(getBooleanProperty());
	}

	private boolean getBooleanProperty() {
		return (Boolean) getProperty();
	}

}
