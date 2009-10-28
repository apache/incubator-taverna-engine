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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * Dialog used for user to set a master password for Credential Manager.
 * 
 * @author Alex Nenadic
 */
@SuppressWarnings("serial")
public class SetMasterPasswordDialog extends JDialog {
	
    // First password entry field 
    private JPasswordField jpfFirst;

    // Password confirmation entry field 
    private JPasswordField jpfConfirm;

    // Stores the password entered 
    private String password = null;
    
    private String instructions;

    /**
     * Creates new SetNewPasswordDialog where the parent is a frame.
     */
    public SetMasterPasswordDialog(JFrame parent, String title, boolean modal, String instructions)
    {
        super(parent, title, modal);
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

        JLabel jlFirst = new JLabel("Master password");
        jlFirst.setBorder(new EmptyBorder(0,5,0,0));

        JLabel jlConfirm = new JLabel("Confirm master password");
        jlConfirm.setBorder(new EmptyBorder(0,5,0,0));

        jpfFirst = new JPasswordField(15);
        jpfConfirm = new JPasswordField(15);
        
        JPanel jpPassword = new JPanel(new GridLayout(2, 2, 5, 5));
        jpPassword.add(jlFirst);
        jpPassword.add(jpfFirst);
        jpPassword.add(jlConfirm);
        jpPassword.add(jpfConfirm);
        
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
     * Check the following:
     * <ul>
     *     <li>that the user has supplied and confirmed a password
     *     <li>that the password's match
     *     <li>that the passwords are not empty
     * </ul>
     * and stores the new password in this object.
     *
     * @return true, if the user's dialog entry matches the above criteria,
     *         false otherwise
     */
    private boolean checkPassword()
    {
        String sFirstPassword = new String(jpfFirst.getPassword());
        String sConfirmPassword = new String(jpfConfirm.getPassword());

        if ((sFirstPassword.equals(sConfirmPassword)) && (sFirstPassword.length()!= 0)) { //passwords match and not empty
            password = sFirstPassword;
            return true;
        }
        else if ((sFirstPassword.equals(sConfirmPassword)) && (sFirstPassword.length() == 0)) { //passwords match but are empty
            JOptionPane.showMessageDialog(this,
                    "The password cannot be empty", 
                    "Credential Manager Warning",
                    JOptionPane.WARNING_MESSAGE);

                return false;
        }
        else{ // passwords do not match

       	JOptionPane.showMessageDialog(this,
       			"The passwords do not match", 
       			"Credential Manager Warning",
       			JOptionPane.WARNING_MESSAGE);

       	return false;
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


