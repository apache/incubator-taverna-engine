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
package net.sf.taverna.t2.security.profiles.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


/**
 * Dialog used for entering a name and description for a profile.
 * 
 * @author Alexandra Nenadic
 */
public class GetProfileNameDescriptionDialog
    extends JDialog
{
	private static final long serialVersionUID = -5683871610547660782L;

	/** Profile name entry field */
    private JTextField jtfName;

    /** Stores the entered name */
    private String name;

    /** Profile description entry field */
    private JTextField jtfDesc;

    /** Stores the entered description */
    private String desc = null;
    
    /**
     * Creates new GetProfileNameDescriptionDialog dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     */
    public GetProfileNameDescriptionDialog(JFrame parent, String sTitle, boolean bModal)
    {
        super(parent, sTitle, bModal);
        initComponents();
    }

    /**
     * Creates new GetProfileNameDescriptionDialog dialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?     
     */
    public GetProfileNameDescriptionDialog(JDialog parent, String sTitle, boolean bModal)
    {
        super(parent, sTitle, bModal);
        initComponents();
    }

    /**
     * Get the name set in the dialog.
     *
     * @return The name or null if none was set
     */
    public String getName()
    {
    	return name;
    }

    /**
     * Get the descritpion set in the dialog.
     *
     * @return The description or null if none was set
     */
    public String getDescritpion()
    {
    	return desc;
    }
    
    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());
               	
        JLabel jlName = new JLabel("Name");
        jtfName = new JTextField(25);
        
        JLabel jlDesc = new JLabel("Description");
        jtfDesc = new JTextField(25);

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

           
        // Instructions
        JLabel jlInstructions;
        jlInstructions = new JLabel ("Give name and decription for the new WS security profile:");
        jlInstructions.setFont(new Font(null, Font.PLAIN, 11));
        jlInstructions.setBorder(new EmptyBorder(15, 5, 15, 15));
        
        // Controls panel
        JPanel jpControls = new JPanel(new GridBagLayout());
        jpControls.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        GridBagConstraints gbcCol0 = new GridBagConstraints();
        gbcCol0.gridx = 0;
        gbcCol0.gridwidth = 1;
        gbcCol0.gridheight = 1;
        gbcCol0.insets = new Insets(2, 5, 2, 5);
        gbcCol0.anchor = GridBagConstraints.LINE_START;
        
        GridBagConstraints gbcCol1 = new GridBagConstraints();
        gbcCol1.gridx = 1;
        gbcCol1.gridwidth = 1;
        gbcCol1.gridheight = 1;
        gbcCol1.insets = new Insets(2, 5, 2, 5);
        gbcCol1.anchor = GridBagConstraints.LINE_START;
        
        GridBagConstraints gbc_col0 = (GridBagConstraints) gbcCol0.clone();
        gbc_col0.gridy = 0;
        GridBagConstraints gbc_col1 = (GridBagConstraints) gbcCol1.clone();
        gbc_col1.gridy = 0;
        jpControls.add(jlName, gbc_col0);
        jpControls.add(jtfName, gbc_col1);
        
        gbc_col0.gridy = 1;
        gbc_col1.gridy = 1;
        jpControls.add(jlDesc, gbc_col0);
        jpControls.add(jtfDesc, gbc_col1);

        // Button panel
        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbOK);
        jpButtons.add(jbCancel);

    	getContentPane().add(jlInstructions, BorderLayout.NORTH);
        getContentPane().add(jpControls, BorderLayout.CENTER);
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
     * Checks that the name and description are not empty and that the
     * list of current profile names does not already contain the name entered and 
     * then stores the entered values in this object.
     *
     * @return true - if the user's dialog entry matches the above criteria, false otherwise
     */
    private boolean checkControls()
    {
        name = new String(jtfName.getText());
        desc = new String(jtfDesc.getText());
        
        if (name.length() == 0) { //name is empty          
            JOptionPane.showMessageDialog(this,
                    "The profile name cannot be empty", 
                    "Credential Manager Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else if (desc.length() == 0) { //desc is empty          
            JOptionPane.showMessageDialog(this,
                    "The profile description cannot be empty", 
                    "Credential Manager Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // Check if the profile name already exists  
        else if(((WSSecurityProfileChooser) getParent()).wsSecurityProfileManager.getWSSecurityProfileNames().contains(name)) {
            JOptionPane.showMessageDialog(this,
                    "The profile with that name already exist", 
                    "Credential Manager Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else { //not empty
        	return true;
        }
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
    	name = null;
    	desc = null;
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

