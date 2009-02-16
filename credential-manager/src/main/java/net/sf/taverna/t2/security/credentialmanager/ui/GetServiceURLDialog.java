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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Dialog used for entering a service URL.
 * 
 * @author Alexandra Nenadic
 */

public class GetServiceURLDialog extends JDialog
{
	
	private static final long serialVersionUID = -2023348569964325951L;

	/** Password entry password field */
    private JTextField jtfServiceURL;

    /** Stores the service URL entered */
    private String serviceURL = null;

    /**
     * Creates new GetServiceURLDialog dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     */
    public GetServiceURLDialog(JFrame parent, String sTitle, boolean bModal)
    {
        super(parent, sTitle, bModal);
        initComponents();
    }

    /**
     * Creates new GetServiceURLDialog dialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?     
     */
    public GetServiceURLDialog(JDialog parent, String sTitle, boolean bModal)
    {
        super(parent, sTitle, bModal);
        initComponents();
    }

    /**
     * Get the service URL set in the dialog.
     *
     * @return The service URL or null if none was set
     */
    public String getServiceURL()
    {
    	return serviceURL;
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());
               	
        JLabel jlServiceURL = new JLabel("Service URL");
        jtfServiceURL = new JTextField(15);

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
    	jlInstructions = new JLabel ("Enter the Service URL");
    	jlInstructions.setFont(new Font(null, Font.PLAIN, 11));
    	jlInstructions.setBorder(new EmptyBorder(5,5,5,5));
    	getContentPane().add(jlInstructions, BorderLayout.NORTH);
        
        JPanel jpServiceURL = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpServiceURL.add(jlServiceURL);
        jpServiceURL.add(jtfServiceURL);
        jpServiceURL.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbOK);
        jpButtons.add(jbCancel);

        getContentPane().add(jpServiceURL, BorderLayout.CENTER);
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
     * OK button pressed or otherwise activated.
     */
    private void okPressed()
    {
    	serviceURL = new String(jtfServiceURL.getText());
    	closeDialog();
    }

    /**
     * Cancel button pressed or otherwise activated.
     */
    private void cancelPressed()
    {
    	serviceURL = null;
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

