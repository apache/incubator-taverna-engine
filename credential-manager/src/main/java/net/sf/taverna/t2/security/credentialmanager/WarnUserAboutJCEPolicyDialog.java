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
package net.sf.taverna.t2.security.credentialmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import edu.stanford.ejalbert.BrowserLauncher;

/**
 * Dialog that lets user know that there are updates available.
 * 
 * @author Alex Nenadic
 *
 */
@SuppressWarnings("serial")
public class WarnUserAboutJCEPolicyDialog extends JDialog{
	
	private Logger logger = Logger.getLogger(WarnUserAboutJCEPolicyDialog.class);
	
	private JCheckBox doNotWarnMeAgainCheckBox;

	public WarnUserAboutJCEPolicyDialog(){
		super((Frame)null, "Java Unlimited Strength Cryptography Policy Warning", true);
		initComponents();
	}

	// For testing
	public static void main (String[] args){
		WarnUserAboutJCEPolicyDialog dialog = new WarnUserAboutJCEPolicyDialog();
		dialog.setVisible(true);
	}
	
	private void initComponents() {
		// Base font for all components on the form
		Font baseFont = new JLabel("base font").getFont().deriveFont(11f);
		
		// Message saying that updates are available
		JPanel messagePanel = new JPanel(new BorderLayout());
		messagePanel.setBorder(new CompoundBorder(new EmptyBorder(10,10,10,10), new EtchedBorder(EtchedBorder.LOWERED)));
		
		JEditorPane message = new JEditorPane();
		message.setEditable(false);
		message.setBackground(this.getBackground());
		message.setFocusable(false);
		HTMLEditorKit kit = new HTMLEditorKit();
		message.setEditorKit(kit);
		StyleSheet styleSheet = kit.getStyleSheet();
		//styleSheet.addRule("body {font-family:"+baseFont.getFamily()+"; font-size:"+baseFont.getSize()+";}"); // base font looks bigger when rendered as HTML
		styleSheet.addRule("body {font-family:"+baseFont.getFamily()+"; font-size:10px;}");
		Document doc = kit.createDefaultDocument();
		message.setDocument(doc);
		message.setText("<html><body>In order for Credential Manager to function properly - you need to install 'Java Cryptography Extension (JCE)<br>" +
				"Unlimited Strength Jurisdiction Policy'. If you already do not have it, you can get it from:<br><ul>" +
				"<li><a href=\"http://java.sun.com/javase/downloads/index_jdk5.jsp\">http://java.sun.com/javase/downloads/index_jdk5.jsp</a><b> for Java 5</b></li>" +
				"<li><a href=\"http://java.sun.com/javase/downloads/index.jsp\">http://java.sun.com/javase/downloads/index.jsp</a><b> for Java 6</b></li></ul>" +	
				"Installation instructions are contained in the download."+
			"</body><html>");
		message.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent he) {
				HyperlinkEvent.EventType type = he.getEventType();
			    if (type == HyperlinkEvent.EventType.ACTIVATED) {
					// Open a Web browser
					try{
						BrowserLauncher launcher = new BrowserLauncher();
						launcher.openURLinBrowser(he.getURL().toString());
					}catch(Exception ex){
						logger.error("User registration: Failed to launch browser to show terms and conditions at " + he.getURL().toString());
					}
			    }				
			}
		});		
		message.setBorder(new EmptyBorder(5,5,5,5));
		messagePanel.add(message, BorderLayout.CENTER);
		
		doNotWarnMeAgainCheckBox = new JCheckBox("Do not warn me again");
		doNotWarnMeAgainCheckBox.setFont(baseFont.deriveFont(12f));
		messagePanel.add(doNotWarnMeAgainCheckBox, BorderLayout.SOUTH);

		// Buttons
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton okButton = new JButton("OK");
		okButton.setFont(baseFont);
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				okPressed();
			}
		});
		
		buttonsPanel.add(okButton);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(messagePanel, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
		// Center the dialog on the screen (we do not have the parent)
		Dimension dimension = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dimension.width - abounds.width) / 2,
				(dimension.height - abounds.height) / 2);
		setSize(getPreferredSize());
	}

	protected void okPressed(){
		if (doNotWarnMeAgainCheckBox.isSelected()){
			try {
				FileUtils.touch(CMUtil.doNotWarnUserAboutJCEPolicyFile);
			} catch (IOException ioex) {
				logger.error("Failed to touch the 'Do not want me about JCE unilimited security policy file.",
								ioex);
			}
		}
		closeDialog();

	}
	
	private void closeDialog() {
		setVisible(false);
		dispose();
	}

}

