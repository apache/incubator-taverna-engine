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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

/**
 * Dialog used for entering a password.
 * 
 * @author Alexandra Nenadic
 */
public class GetPasswordDialog
    extends JDialog
{

	private static final long serialVersionUID = 2359997256455514203L;

	/** Instructions for user explaining the purpose of the password */
	private String instructions = null;
	
    /** Password entry password field */
    private JPasswordField jpfPassword;

    /** Stores the password entered */
    private String password = null;

    /**
     * Creates new GetPasswordDialog dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param instr Instructions for user
     */
    public GetPasswordDialog(JFrame parent, String sTitle, boolean bModal, String instr)
    {
        super(parent, sTitle, bModal);
        instructions = instr;
        initComponents();
    }

    /**
     * Creates new GetPasswordDialog dialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?     
     * @param instr Instructions for user
     */
    public GetPasswordDialog(JDialog parent, String sTitle, boolean bModal, String instr)
    {
        super(parent, sTitle, bModal);
        instructions = instr;
        initComponents();
    }

    /**
     * Get the password set in the dialog.
     *
     * @return The password or null if none was set
     */
    public String getPassword()
    {
    	return password;
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());
               	
        JLabel jlPassword = new JLabel("Password");
        jpfPassword = new JPasswordField(15);

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

             
        JLabel jlInstructions; // Instructions
        if (instructions != null){
        	jlInstructions = new JLabel (instructions);
        	jlInstructions.setFont(new Font(null, Font.PLAIN, 11));
        	jlInstructions.setBorder(new EmptyBorder(5,5,5,5));
        	getContentPane().add(jlInstructions, BorderLayout.NORTH);
        }
        
        JPanel jpPassword = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpPassword.add(jlPassword);
        jpPassword.add(jpfPassword);
        jpPassword.setBorder(new EmptyBorder(5, 5, 5, 5));

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
     * Checks that the password entered is not empty and 
     * store the entered password in this object.
     *
     * @return true - if the user's dialog entry matches the above criteria, false otherwise
     */
    private boolean checkPassword()
    {
        password = new String(jpfPassword.getPassword());

        
        if (password.length() == 0) { //password is empty          
            JOptionPane.showMessageDialog(this,
                    "The password cannot be empty", 
                    "Credential Manager Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else { //password is not empty
        	return true;
        }

    }
    
    /**
     * OK button pressed or otherwise activated.
     */
    private void okPressed()
    {
        if (checkPassword()) {
            closeDialog();
        }
    }

    /**
     * Cancel button pressed or otherwise activated.
     */
    private void cancelPressed()
    {
    	password = null;
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

