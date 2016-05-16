##    Licensed to the Apache Software Foundation (ASF) under one or more
##    contributor license agreements.  See the NOTICE file distributed with
##    this work for additional information regarding copyright ownership.
##    The ASF licenses this file to You under the Apache License, Version 2.0
##    (the "License"); you may not use this file except in compliance with
##    the License.  You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
##    Unless required by applicable law or agreed to in writing, software
##    distributed under the License is distributed on an "AS IS" BASIS,
##    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
##    See the License for the specific language governing permissions and
##    limitations under the License.
##
## Note: Above Velocity comment should NOT be included in generated
## code from the archetype
package \${package}.ui.config;

import java.awt.GridLayout;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;

@SuppressWarnings("serial")
public class \${classPrefix}ConfigurationPanel extends ActivityConfigurationPanel {

	private final ServiceRegistry serviceRegistry;

	private JTextField fieldString;
	private JTextField fieldURI;

	public \${classPrefix}ConfigurationPanel(Activity activity, ServiceRegistry serviceRegistry) {
		super(activity);
		this.serviceRegistry = serviceRegistry;
		initialise();
	}

	protected void initialise() {
		// call super.initialise() to initialise the configuration
		super.initialise();

		removeAll();
		setLayout(new GridLayout(0, 2));

		// FIXME: Create GUI depending on activity configuration bean
		JLabel labelString = new JLabel("Example string:");
		add(labelString);
		fieldString = new JTextField(20);
		add(fieldString);
		labelString.setLabelFor(fieldString);

		JLabel labelURI = new JLabel("Example URI:");
		add(labelURI);
		fieldURI = new JTextField(25);
		add(fieldURI);
		labelURI.setLabelFor(fieldURI);

		// Populate fields from activity configuration
		fieldString.setText(getProperty("exampleString"));
		fieldURI.setText(getProperty("exampleUri"));
	}

	/**
	 * Check that user values in UI are valid
	 */
	@Override
	public boolean checkValues() {
		try {
			URI.create(fieldURI.getText());
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, ex.getCause().getMessage(),
					"Invalid URI", JOptionPane.ERROR_MESSAGE);
			// Not valid, return false
			return false;
		}
		// All valid, return true
		return true;
	}

	/**
	 * Set the configuration properties from the UI
	 */
	@Override
	public void noteConfiguration() {
		// FIXME: Update bean fields from your UI elements
		setProperty("exampleString", fieldString.getText());
		setProperty("exampleUri", fieldURI.getText());

		configureInputPorts(serviceRegistry);
		configureOutputPorts(serviceRegistry);
	}

}
