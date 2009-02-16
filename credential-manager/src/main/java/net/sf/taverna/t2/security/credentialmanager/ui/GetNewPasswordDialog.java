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
import java.awt.GridLayout;
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
 * Dialog used for entering and confirming a password.
 * 
 * @author Alexandra Nenadic
 */
public class GetNewPasswordDialog
    extends JDialog
{

	private static final long serialVersionUID = 5317657512148769008L;

	/** Instructions for user explaining the purpose of the password */
	private String instructions = null;
	
    /** First password entry password field */
    private JPasswordField jpfFirst;

    /** Password confirmation entry password field */
    private JPasswordField jpfConfirm;

    /** Stores new password entered */
    private String password = null;

    /**
     * Creates new GetNewPasswordDialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param bModal Is dialog modal?
     * @param instr Instructions for user
     */
    public GetNewPasswordDialog(JFrame parent, String sTitle, boolean bModal, String instr)
    {
        super(parent, sTitle, bModal);
        instructions = instr;
        initComponents();
    }

    /**
     * Creates new GetNewPasswordDialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param bModal Is dialog modal?
     * @param instr Instructions for user
     */
    public GetNewPasswordDialog(JDialog parent, String sTitle, boolean bModal, String instr)
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

        JLabel jlFirst = new JLabel("Enter New Password:");
        JLabel jlConfirm = new JLabel("Confirm New Password:");
        jpfFirst = new JPasswordField(15);
        jpfConfirm = new JPasswordField(15);

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

        JLabel jlInstructions;
        if (instructions != null){
        	jlInstructions = new JLabel (instructions);
        	jlInstructions.setFont(new Font(null, Font.PLAIN, 11));
        	jlInstructions.setBorder(new EmptyBorder(5,5,5,5));
        	getContentPane().add(jlInstructions, BorderLayout.NORTH);
        }
        
        JPanel jpPassword = new JPanel(new GridLayout(2, 2, 5, 5));
        jpPassword.add(jlFirst);
        jpPassword.add(jpfFirst);
        jpPassword.add(jlConfirm);
        jpPassword.add(jpfConfirm);
        jpPassword.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbCancel);
        jpButtons.add(jbOK);

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
     * Checks the following:
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


