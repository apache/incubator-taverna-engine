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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * Dialog used for getting a master password for Credential Manager
 * from the users.
 * 
 * @author Alex Nenadic
 */
@SuppressWarnings("serial")
public class GetMasterPasswordDialog extends JDialog {
	
    // Password entry field 
    private JPasswordField jpfPassword;

    // Stores the password entered 
    private String password = null;
    
    // Text giving user the instructions what to do in the dialog
    private String instructions;

    /**
     * Creates new GetNewPasswordDialog where the parent is a frame.
     */
    public GetMasterPasswordDialog(String instructions)
    {
        super((Frame)null, "Enter master password", true);
        this.instructions = instructions;
        initComponents();
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());

        JLabel jlInstructions = new JLabel (instructions);
    	jlInstructions.setFont(new Font(null, Font.PLAIN, 11));
    	
    	JPanel jpInstructions = new JPanel();
    	jpInstructions.setLayout(new BoxLayout(jpInstructions, BoxLayout.Y_AXIS));
    	jpInstructions.add(jlInstructions);
    	jpInstructions.setBorder(new EmptyBorder(10,5,10,0));
        
        JLabel jlPassword = new JLabel("Password");
        jlPassword.setBorder(new EmptyBorder(0,5,0,0));

        jpfPassword = new JPasswordField(15);
        JPanel jpPassword = new JPanel(new GridLayout(1, 1, 5, 5));
        jpPassword.add(jlPassword);
        jpPassword.add(jpfPassword);
        
        JPanel jpMain = new JPanel(new BorderLayout());
        jpMain.setBorder(new CompoundBorder(
                new EmptyBorder(10, 10, 10, 10), new EtchedBorder()));
        jpMain.add(jpInstructions, BorderLayout.NORTH);
        jpMain.add(jpPassword, BorderLayout.CENTER);

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
        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbOK);
        jpButtons.add(jbCancel);
        
        getContentPane().add(jpMain, BorderLayout.CENTER);
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
     * Get the password set in the dialog or null if none was set
     */
    public String getPassword()
    {
    	return password;
    }
    
    /**
     * Check that the password entered is not empty and 
     * store the entered password.
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
    	// Set the password to null as it might have changed in the meantime 
    	// if user entered something previously
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



