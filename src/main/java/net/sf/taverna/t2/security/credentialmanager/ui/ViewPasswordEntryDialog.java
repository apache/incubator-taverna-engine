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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * Dialog used for viewing service URL, username and password.
 * 
 * @author Alexandra Nenadic
 */
public class ViewPasswordEntryDialog
    extends JDialog
{
	private static final long serialVersionUID = -7224904997349644853L;

	/** Service URL field */
    private JTextField jtfServiceURL;
    
    /** Username field */
    private JTextField jtfUsername;
    
    /** Password field */
    private JPasswordField jpfPassword;

    /** Service URL value*/
    private String sURL;    
    
    /** Service username value*/
    private String sUsername;
    
    /** Service password value*/
    private String sPassword;

   /**
     * Creates new ViewPasswordEntryDialog dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param bModal Is dialog modal?
     * @param cURL Service URL value
     * @param cUsername Service username value
     * @param cPassword Service password value
     */
    public ViewPasswordEntryDialog(JFrame parent, boolean bModal, String cURL, String cUsername, String cPassword)
    {
        this(parent, "View password entry for a service", bModal, cURL, cUsername, cPassword);

    }

    /**
     * Creates new ViewPasswordEntryDialog dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param cURL Service URL value
     * @param cUsername Service username value
     * @param cPassword Service password value
     */
    public ViewPasswordEntryDialog(JFrame parent, String sTitle, boolean bModal, String cURL, String cUsername, String cPassword)
    {
        super(parent, sTitle, bModal);  
        sURL = cURL;
        sUsername = cUsername;
        sPassword = cPassword;
        initComponents();
    }

    /**
     * Creates new ViewPasswordDialog dialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param bModal Is dialog modal?
     * @param cURL Service URL value
     * @param cUsername Service username value
     * @param cPassword Service password value
     */
    public ViewPasswordEntryDialog(JDialog parent, boolean bModal, String cURL, String cUsername, String cPassword)
    {
        this(parent, "View password entry", bModal, cURL, cUsername, cPassword);
    }

    /**
     * Creates new ViewPasswordDialog dialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param cURL Service URL value
     * @param cUsername Service username value
     * @param cPassword Service password value
     */
    public ViewPasswordEntryDialog(JDialog parent, String sTitle, boolean bModal, String cURL, String cUsername, String cPassword)
    {
        super(parent, sTitle, bModal);
        sURL = cURL;
        sUsername = cUsername;
        sPassword = cPassword;
        initComponents();
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());

        JLabel jlServiceURL = new JLabel("Service URL");
        JLabel jlUsername = new JLabel("Username");
        JLabel jlPassword = new JLabel("Password");

        //Populate the fields with values and disable user input
        jtfServiceURL = new JTextField(15);
        jtfServiceURL.setText(sURL);
        jtfServiceURL.setEditable(false);
        jtfUsername = new JTextField(15);
        jtfUsername.setText(sUsername);
        jtfUsername.setEditable(false);
        jpfPassword = new JPasswordField(15);
        jpfPassword.setText(sPassword);
        jpfPassword.setEditable(false);
        
        JButton jbOK = new JButton("OK");
        jbOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
            	closeDialog();
            }
        });
        
        JButton jbViewPassword = new JButton("View password");
        jbViewPassword.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
            	showPassword();
            }
        });
        
        JPanel jpPassword = new JPanel(new GridLayout(4, 2, 5, 5));
        jpPassword.add(jlServiceURL);
        jpPassword.add(jtfServiceURL);
        jpPassword.add(jlUsername);
        jpPassword.add(jtfUsername);
        jpPassword.add(jlPassword);
        jpPassword.add(jpfPassword);
        //jpPassword.setBorder(new EmptyBorder(5, 5, 5, 5));
        jpPassword.setBorder(new CompoundBorder(
                new EmptyBorder(5, 5, 5, 5), new EtchedBorder()));
        
        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbOK);
        jpButtons.add(jbViewPassword);
        
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
     * Show the password which was hidden in the JPasswordField.
     */
    private void showPassword()
    {
        // Show the password
        ViewPasswordDialog dShowPassword = new ViewPasswordDialog(this,
        		"View password",
        		true, 
       		 	sPassword);
        dShowPassword.setLocationRelativeTo(this);
        dShowPassword.setVisible(true);
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


