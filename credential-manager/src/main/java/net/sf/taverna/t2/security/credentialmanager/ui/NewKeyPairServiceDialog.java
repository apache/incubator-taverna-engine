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
 * Dialog used for entering service url.
 * 
 * @author Alexandra Nenadic
 */
public class NewKeyPairServiceDialog
    extends JDialog
{
	private static final long serialVersionUID = -6601251225261121921L;

	/** Service URL entry field */
    private JTextField jtfServiceURL;

    /** Stores service URL entered */
    private String sURL = null;

    /**
     * Creates new NewKeyPairServiceDialog dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     */
    public NewKeyPairServiceDialog(JFrame parent, String sTitle, boolean bModal)
    {
        super(parent, sTitle, bModal);
        initComponents();
    }

    /**
     * Creates new NewKeyPairServiceDialog dialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     */
    public NewKeyPairServiceDialog(JDialog parent, String sTitle, boolean bModal)
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
          return sURL;
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());
        
        JLabel jlDescription = new JLabel("Enter the service URL the key pair will be associated to");
    	jlDescription.setFont(new Font(null, Font.PLAIN, 11));
    	jlDescription.setBorder(new EmptyBorder(5,5,5,5));
    	
    	JLabel jlServiceURL= new JLabel("Service URL");
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

        JPanel jpServiceURL = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpServiceURL.add(jlServiceURL);
        jpServiceURL.add(jtfServiceURL);
        jpServiceURL.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbOK);
        jpButtons.add(jbCancel);

    	getContentPane().add(jlDescription, BorderLayout.NORTH);
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
        sURL = jtfServiceURL.getText();
        closeDialog();
    }

    /**
     * Cancel button pressed or otherwise activated.
     */
    private void cancelPressed()
    {
       	// Set sURL field to null as it might have changed in the meantime
    	// if user entered some value than pressed cancel later on
    	sURL = null;       
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

