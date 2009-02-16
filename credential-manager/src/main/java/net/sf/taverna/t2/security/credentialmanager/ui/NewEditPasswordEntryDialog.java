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
package net.sf.taverna.t2.security.credentialmanager.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * Dialog used for editing or entering new service URL, username or password of a password entry.
 * 
 * @author Alexandra Nenadic
 */
public class NewEditPasswordEntryDialog
    extends JDialog
{

	private static final long serialVersionUID = -4102369542444832245L;

	/** 'Edit' mode constant */
	private static final String EDIT_MODE = "Edit";
	
	/** 'New' mode constant */
	private static final String NEW_MODE = "NEW";
	
	/** Mode of this dialog - NEW_MODE for entering new password entry and EDIT_MODE for editting an existing password entry */
	String mode;
  
    /** Service URL field */
    private JTextField jtfServiceURL;
    
    /** Username field */
    private JTextField jtfUsername;
    
    /** First password entry field */
    private JPasswordField jpfFirstPassword;

    /** Password confirmation entry field */
    private JPasswordField jpfConfirmPassword;

    /** Stores service URL entered */
    private String sURL;    
    
    /** Stores username entered */
    private String sUsername;
    
    /** Stores password entered */
    private String sPassword;

    /**
     * Creates new NewEditPasswordEntryDialog dialog where parent is a frame.
     *
     * @param parent Parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param cURL Current service URL value
     * @param cUsername Current service username value
     * @param cPassword Current service password value
     */
    public NewEditPasswordEntryDialog(JFrame parent, String sTitle, boolean bModal, String cURL, String cUsername, String cPassword)
    {
        super(parent, sTitle, bModal);        
        sURL = cURL;
        sUsername = cUsername;
        sPassword = cPassword;
        if (sURL == null && sUsername == null && sPassword == null) // if passed values are all null
        {
        	mode = NEW_MODE; // dialog is for entering a new password entry
        }
        else{
            mode = EDIT_MODE; // dialog is for editing an existing entry
        }
        initComponents();
    }

    /**
     * Creates new NewEditPasswordEntryDialog dialog where parent is a dialog.
     *
     * @param parent Parent dialog
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param cURL Current service URL value
     * @param cUsername Current service username value
     * @param cPassword Current service password value
     */
    public NewEditPasswordEntryDialog(JDialog parent, String sTitle, boolean bModal, String cURL, String cUsername, String cPassword)
    {
        super(parent, sTitle, bModal);
        sURL = cURL;
        sUsername = cUsername;
        sPassword = cPassword;
        if (sURL == null && sUsername == null && sPassword == null) // if passed values are all null
        {
        	mode = NEW_MODE; // dialog is for entering new password entry
        }
        else{
            mode = EDIT_MODE; // dialog is for editing existing entry
        }
        initComponents();
    }
    
    
    /**
     * Get the username set in the dialog.
     *
     * @return the username
     */
    public String getUsername()
    {
        return sUsername;
    }
    
    /**
     * Get the service URL set in the dialog.
     *
     * @return the service URL
     */
    public String getServiceURL()
    {
        return sURL;
    }
    
    /**
     * Get the password set in the dialog.
     *
     * @return the password
     */
    public String getPassword()
    {
    	return sPassword;
    }
    
    
    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());

        JLabel jlServiceURL = new JLabel("Service URL");
        JLabel jlUsername = new JLabel("Username");
        JLabel jlFirstPassword = new JLabel("Password");
        JLabel jlConfirmPassword = new JLabel("Confirm password");
               
        jtfServiceURL = new JTextField(15);
        jtfUsername = new JTextField(15);
        jpfFirstPassword = new JPasswordField(15);
        jpfConfirmPassword = new JPasswordField(15);
        
        //If in EDIT_MODE - populate the fields with current values
        if (mode.equals(EDIT_MODE)){
            jtfServiceURL.setText(sURL);
            jtfUsername.setText(sUsername);     
            jpfFirstPassword.setText(sPassword);
            jpfConfirmPassword.setText(sPassword);
        }
        
        JButton jbOK = new JButton("OK");
        jbOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                okPressed();
            }
        });

        JButton jbCancel = new JButton("Cancel");
        jbCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                cancelPressed();
            }
        });

        JPanel jpPassword = new JPanel(new GridLayout(4, 2, 5, 5));
        jpPassword.add(jlServiceURL);
        jpPassword.add(jtfServiceURL);
        jpPassword.add(jlUsername);
        jpPassword.add(jtfUsername);
        jpPassword.add(jlFirstPassword);
        jpPassword.add(jpfFirstPassword);
        jpPassword.add(jlConfirmPassword);
        jpPassword.add(jpfConfirmPassword);
        
        jpPassword.setBorder(new CompoundBorder(
                new EmptyBorder(5, 5, 5, 5), new EtchedBorder()));
        
        jpPassword.setMinimumSize(new Dimension(300,100));

        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbOK);
        jpButtons.add(jbCancel);

        getContentPane().add(jpPassword, BorderLayout.CENTER);
        getContentPane().add(jpButtons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent evt)
            {
                closeDialog();
            }
        });

        setResizable(false);

        getRootPane().setDefaultButton(jbOK);

        pack();
    }

    /**
     * Checks for the following:
     * <ul>
     *     <li>That the user has supplied a non empty service URL
     *     <li>That the user has supplied a non empty username
     *     <li>That the user has supplied and confirmed a non empty password
     *     <li>That the entry with the same URL already does not exist in the Keystore
     * </ul>
     * and stores the new password in this object.
     *
     * @return true - if the user's dialog entry matches the above criteria, false otherwise
     */
    private boolean checkControls()
    {
    	sURL = new String(jtfServiceURL.getText());
    	if (sURL.length() == 0) {
            JOptionPane.showMessageDialog(this,
                "Service URL cannot be empty", 
                "Credential Manager Warning",
                JOptionPane.WARNING_MESSAGE);
               
            return false;
    	}
    	
    	sUsername = new String(jtfUsername.getText());
    	if (sUsername.length() == 0){
            JOptionPane.showMessageDialog(this,
                "Username cannot be empty", 
                "Credential Manager Warning",
                JOptionPane.WARNING_MESSAGE);
               
            return false;
    	}
    	   	
    	String sFirstPassword = new String(jpfFirstPassword.getPassword());
        String sConfirmPassword = new String(jpfConfirmPassword.getPassword());

    	if ((sFirstPassword.length() > 0) && (sFirstPassword.equals(sConfirmPassword))) { // passwords the same and non-empty
    		sPassword = sFirstPassword;
        }
        else if ((sFirstPassword.length() == 0) && (sFirstPassword.equals(sConfirmPassword))){ // passwords match but are empty

            JOptionPane.showMessageDialog(this,
                "Password cannot be empty", 
                "Credential Manager Warning",
                JOptionPane.WARNING_MESSAGE);

            return false;        	
        }
        else{ // passwords do not match
            JOptionPane.showMessageDialog(this,
                "Passwords do not match", 
                "Credential Manager Warning",
                JOptionPane.WARNING_MESSAGE);

            return false;            	
        }
    	
		// Check if the entered URL is already associated with another key pair entry in the Keystore
    	HashMap<String, Vector<String>> urlMap = (HashMap<String, Vector<String>>) ((CredentialManagerGUI) this.getParent()).getURLsForKeyPairs();
       	if (urlMap != null){ // should not be null really (although can be empty). Check anyway.
        	Set<String> aliases = urlMap.keySet();
        	for (Iterator<String> i = aliases.iterator(); i.hasNext(); ){
        		String alias = (String) i.next();
        		// Check if url list for this alias contains the newly entered url
        		Vector<String> urls = (Vector<String>) urlMap.get(alias);
        		if (urls.contains(sURL)){
            		// Warn the user and exit
                	JOptionPane.showMessageDialog(
                    		this, 
                    		"The entered URL is already associated with another key pair entry",
                			"Credential Manager Alert",
                			JOptionPane.INFORMATION_MESSAGE);
                	return false;
        		}    		
        	 }
       	}
    	
    	return true;
    }

    /**
     * OK button pressed or otherwise activated.
     */
    private void okPressed()
    {
        if (checkControls()) {
            closeDialog();
        }
    }

    /**
     * Cancel button pressed or otherwise activated.
     */
    private void cancelPressed()
    {
    	// Set all fields to null to indicate that cancel button was pressed
    	sURL = null;
    	sUsername = null;
    	sPassword = null;
        closeDialog();
    }

    /**
     * Close the dialog.
     */
    private void closeDialog()
    {
        setVisible(false);
        dispose();
    }
}

