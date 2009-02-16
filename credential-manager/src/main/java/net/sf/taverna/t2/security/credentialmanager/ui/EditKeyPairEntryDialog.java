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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.awt.Dimension;

/**
 * Dialog used for editing service urls associated with a key pair entry.
 * 
 * @author Alexandra Nenadic
 */
public class EditKeyPairEntryDialog
    extends JDialog
{

	private static final long serialVersionUID = 2882368665291275466L;

	/** Service URLs entry list */
    private JList jltServiceURLs;

    /** Stores service URLs entered */
    private Vector<String> serviceURLs;

    /**
     * Creates new EditKeyPairEntryDialog dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param sURLs Current list of service URLs associated with the key entry
     */
    public EditKeyPairEntryDialog(JFrame parent, String sTitle, boolean bModal, Vector<String> sURLs)
    {
        super(parent, sTitle, bModal);
        serviceURLs = sURLs;
        initComponents();
    }

    /**
     * Creates new EditKeyPairEntryDialog dialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param sURLs Current list of service URLs associated with the key entry
     */
    public EditKeyPairEntryDialog(JDialog parent, String sTitle, boolean bModal, Vector<String> sURLs)
    {
        super(parent, sTitle, bModal);
        serviceURLs = sURLs;
        initComponents();
    }

    /**
     * Get the service URLs set in the dialog.
     *
     * @return The service URLs
     */
    public Vector<String> getServiceURLs()
    {
          return serviceURLs;
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());
        
        // Label
        JLabel jlServiceURLs = new JLabel("Service URLs this key will be used for:");             
        jlServiceURLs.setFont(new Font(null, Font.PLAIN, 11));
        jlServiceURLs.setBorder(new EmptyBorder(5,5,5,5));   
        
        // Service URLs list
        DefaultListModel jltModel = new DefaultListModel();
        jltServiceURLs = new JList(jltModel); 
        // Populate the list with current values
        for (Enumeration<String> e = serviceURLs.elements(); e.hasMoreElements();){
        	jltModel.addElement(e.nextElement());
        }
        jltServiceURLs.setVisibleRowCount(5); //don't show more than 5, otherwise the window is too big
        
        // 'Add' service URL button
        JButton addButton = new JButton("+");
        addButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt)
            {
            	addServiceURLPressed();
            }       	
        });
        addButton.setEnabled(true);
        // 'Remove' service URL button
        final JButton removeButton = new JButton("-");
        removeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt)
            {
            	// get selected indices
            	int[] selected = jltServiceURLs.getSelectedIndices();
            	for (int i = selected.length -1; i>=0 ; i--){
            		 ((DefaultListModel) jltServiceURLs.getModel()).remove(selected[i]);
            	}
            }       	
        });
        removeButton.setEnabled(false);
        jltServiceURLs.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent evt)
            {
                if (jltServiceURLs.getSelectedIndex() == -1) {
                	removeButton.setEnabled(false);
                }
                else {
                	removeButton.setEnabled(true);
                }
            }
        });
        
        // Scroll pane for service URLs list
        JScrollPane jspServiceURLs = new JScrollPane(jltServiceURLs,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jspServiceURLs.getViewport().setBackground(jltServiceURLs.getBackground());
        
        // Panel for Add and Remove buttons
        JPanel jpServiceURLsButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpServiceURLsButtons.add(addButton);
        jpServiceURLsButtons.add(removeButton);
        
        // Panel to hold the list scroll pane and Add/Remove buttons panel
        JPanel jpServiceURLs = new JPanel(new BorderLayout());
        jpServiceURLs.add(jlServiceURLs, BorderLayout.NORTH);
        jpServiceURLs.add(jspServiceURLs, BorderLayout.CENTER);
        jpServiceURLs.add(jpServiceURLsButtons, BorderLayout.SOUTH);
        jpServiceURLs.setBorder(new CompoundBorder(
                new EmptyBorder(15, 15, 15, 15), new EtchedBorder()));
        jpServiceURLs.setPreferredSize(new Dimension(300, 250));
        
        // OK button
        JButton jbOK = new JButton("OK");
        jbOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                okPressed();
            }
        });

        // Cancel buton
        JButton jbCancel = new JButton("Cancel");
        jbCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                cancelPressed();
            }
        });

        // Panel for OK and Cancel buttons
        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbOK);
        jpButtons.add(jbCancel);

        getContentPane().add(jpServiceURLs, BorderLayout.CENTER);
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
    	// Get Service URLs
    	serviceURLs = new Vector<String>();
    	Enumeration<?> URLs = (((DefaultListModel) jltServiceURLs.getModel()).elements());
    	 for( ; URLs.hasMoreElements(); ){
    		 serviceURLs.add((String) URLs.nextElement());
    	 }
        closeDialog();
    }
    
    
    /**
     * Add Service URL button pressed.
     */
    public void addServiceURLPressed(){
    	
    	// Display the dialog for entering service URL
    	GetServiceURLDialog dGetServiceURL = new GetServiceURLDialog(this, 
    			"Enter Service URL", 
    			true);
        
    	dGetServiceURL.setLocationRelativeTo(this);
    	dGetServiceURL.setVisible(true);
    	
        String sURL = dGetServiceURL.getServiceURL();
        
        if (sURL == null){ // user cancelled
        	return;
        }
        
        if (sURL.length() == 0){ // user entered empty URL
       		// Warn the user
        	JOptionPane.showMessageDialog(
            		this, 
            		"The URL cannot be empty",
        			"Credential Manager Alert",
        			JOptionPane.INFORMATION_MESSAGE);
        	return;
        }
        
    	// Check if the entered URL already exist in the URL list for this key entry
    	if (((DefaultListModel) jltServiceURLs.getModel()).contains(sURL)){

    		// Warn the user
        	JOptionPane.showMessageDialog(
            		this, 
            		"The entered URL already exists in the list of URLs for this entry",
        			"Credential Manager Alert",
        			JOptionPane.INFORMATION_MESSAGE);
        	return;
    	}
		
		// Check if the entered URL is already associated with another key pair entry in the Keystore
    	// This check should exclude the current entry from the map of URLs being searched
    	// really (we've already checked for it in the jltServiceURLs) - but does not really matter anyway
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
                	return;
        		}    		
        	 }
       	}
    	
		// Check if the entered URL is already associated with a password entry in the Keystore
    	Vector<String> urlList = (Vector<String>) ((CredentialManagerGUI) this.getParent()).getURLsForPasswords();
		// Check if this url list contains the newly entered url
		if (urlList.contains(sURL)){
    		// Warn the user and exit
        	JOptionPane.showMessageDialog(
            		this, 
            		"The entered URL is already associated with another password entry",
        			"Credential Manager Alert",
        			JOptionPane.INFORMATION_MESSAGE);
        	return;
		}    	
    	
    	// Otherwise - the entered URL is not already associated with a different entry in the Keystore, 
		// so add this URL to the list of URLs for this key pair entry
        ((DefaultListModel) jltServiceURLs.getModel()).addElement(sURL);
        int index = ((DefaultListModel) jltServiceURLs.getModel()).getSize() - 1;
        // Element is appended to the list - get its index
        jltServiceURLs.setSelectedIndex(index);
        // Insure the newly added URL is visible
        jltServiceURLs.ensureIndexIsVisible(index);

    }
    

    /**
     * Cancel button pressed or otherwise activated.
     */
    private void cancelPressed()
    {
       	// Set serviceURLs to null as it might have changed in the meantime
    	// if user entered some value than pressed cancel later on
    	serviceURLs = null;       
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


