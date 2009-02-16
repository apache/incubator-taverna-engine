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
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.Set;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CMX509Util;

/**
 * Dialog that displays the details of all key pairs from a PKCS #12
 * keystore allowing the user to pick one for import.
 * 
 * @author Alexandra Nenadic
 */
class NewKeyPairEntryDialog
    extends JDialog
{

	private static final long serialVersionUID = -8069511901485714565L;

	/** List of key pairs available for import */
    private JList jltKeyPairs;

    /** Service URL text field for user to enter */
    //private JTextField jtfServiceURL;
    private JList jltServiceURLs;
    
    /** Service URL (associated with the key pair) */
    private Vector<String> serviceURLs;

    /** PKCS #12 keystore */
    private KeyStore pkcs12KeyStore;

    /** Private key part of key pair chosen by the user for import */
    private Key privateKey;

    /** Certificate chain part of key pair chosen by the user for import */
    private Certificate[] certificateChain;

    /** Key pair alias to be used for this entry in the Keystore */
    private String alias;
    
    /**
     * Creates new form NewKeyPairEntryDialog where the parent is a frame.
     *
     * @param parent The parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param pkcs12 The PKCS #12 keystore to list key pairs from
     * @throws CMException A problem was encountered importing a key pair.
     */
    public NewKeyPairEntryDialog(JFrame parent, String sTitle, boolean bModal, KeyStore pkcs12KS)
        throws CMException
    {
        super(parent, sTitle, bModal);
        pkcs12KeyStore = pkcs12KS;
        initComponents();
    }

    /**
     * Creates new form NewKeyPairEntryDialog where the parent is a dialog.
     *
     * @param parent The parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param pkcs12 The PKCS #12 keystore to list key pairs from
     * @throws CMException A problem was encountered importing a key pair.
     */
    public NewKeyPairEntryDialog(JDialog parent, String sTitle, boolean bModal, KeyStore pkcs12KS)
        throws CMException
    {
        super(parent, sTitle, bModal);
        pkcs12KeyStore = pkcs12KS;
        initComponents();
    }

    /**
     * Get the private part of the key pair chosen by the user for import.
     *
     * @return The private key or null if the user has not chosen a key pair
     */
    public Key getPrivateKey()
    {
        return privateKey;
    }

    /**
     * Get the certificate chain part of the key pair chosen by the
     * user for import.
     *
     * @return The certificate chain or null if the user has not
     * chosen a key pair
     */
    public Certificate[] getCertificateChain()
    {
        return certificateChain;
    }

    /**
     * Get the alias of the key pair chosen by the user for import.
     * 
     * @return the alias
     */
    public String getAlias()
    {
        return alias;
    }
    
    /**
     * Get the service URLs entered by the user.
     * 
     * @return list of service URLs
     */
    public Vector<String> getServiceURLs()
    {
        return serviceURLs;
    }
    
    /**
     * Initialise the dialog's GUI components.
     *
     * @throws CMException A problem was encountered importing a key pair
     */
    private void initComponents()
        throws CMException
    {
        // Instructions
        JLabel jlInstructions = new JLabel("Select a key pair for import:");
        jlInstructions.setFont(new Font(null, Font.PLAIN, 11));
        jlInstructions.setBorder(new EmptyBorder(5,5,5,5));
        JPanel jpInstructions = new JPanel(new BorderLayout());
        jpInstructions.add(jlInstructions, BorderLayout.WEST);

        // Import button
        final JButton jbImport = new JButton("Import");
        jbImport.setEnabled(false);
        jbImport.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                importPressed();
            }
        });

        // Certificate details button
        final JButton jbCertificateDetails = new JButton("Certificate Details");
        jbCertificateDetails.setEnabled(false);
        jbCertificateDetails.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                certificateDetailsPressed();
            }
        });

        // List to hold keystore's key pairs
        jltKeyPairs = new JList();
        jltKeyPairs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jltKeyPairs.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent evt)
            {	
                if (jltKeyPairs.getSelectedIndex() == -1) {
                    jbImport.setEnabled(false);
                    jbCertificateDetails.setEnabled(false);
                }
                else {
                    jbImport.setEnabled(true);
                    jbCertificateDetails.setEnabled(true);
                }
            }
        });

        // Put the key list into a scroll pane
        JScrollPane jspKeyPairs = new JScrollPane(jltKeyPairs,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jspKeyPairs.getViewport().setBackground(jltKeyPairs.getBackground());
        
        // Service URLs list
        // Label
        JLabel jlServiceURL = new JLabel ("Enter service URLs the key pair will be used for:");
        jlServiceURL.setFont(new Font(null, Font.PLAIN, 11));
        jlServiceURL.setBorder(new EmptyBorder(5,5,5,5));           
        // New empty service URLs list
        DefaultListModel jltModel = new DefaultListModel();
        jltServiceURLs = new JList(jltModel); 
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
        jpServiceURLs.add(jlServiceURL, BorderLayout.NORTH);
        jpServiceURLs.add(jspServiceURLs, BorderLayout.CENTER);
        jpServiceURLs.add(jpServiceURLsButtons, BorderLayout.SOUTH);
        
        // Put all the key pair components together
        JPanel jpKeyPairs = new JPanel(); // BoxLayout
        jpKeyPairs.setLayout(new BoxLayout(jpKeyPairs, BoxLayout.Y_AXIS));
        //jpKeyPairs.setPreferredSize(new Dimension(400, 200));
        jpKeyPairs.setBorder(new CompoundBorder(new CompoundBorder(
            new EmptyBorder(5, 5, 5, 5), new EtchedBorder()), new EmptyBorder(
            5, 5, 5, 5)));
   
        jpInstructions.setAlignmentY(JPanel.LEFT_ALIGNMENT);
        jpKeyPairs.add(jpInstructions);
        jspKeyPairs.setAlignmentY(JPanel.LEFT_ALIGNMENT);
        jpKeyPairs.add(jspKeyPairs);
        jbCertificateDetails.setAlignmentY(JPanel.RIGHT_ALIGNMENT);
        jpKeyPairs.add(jbCertificateDetails);
        jpServiceURLs.setAlignmentY(JPanel.LEFT_ALIGNMENT);
        jpKeyPairs.add(jpServiceURLs);

        // Cancel button
        final JButton jbCancel = new JButton("Cancel");
        jbCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                cancelPressed();
            }
        });

        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbImport);
        jpButtons.add(jbCancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jpKeyPairs, BorderLayout.CENTER);
        getContentPane().add(jpButtons, BorderLayout.SOUTH);

        // Populate the list
        populateList();

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent evt)
            {
                closeDialog();
            }
        });

        setResizable(false);

        getRootPane().setDefaultButton(jbImport);

        pack();

    }

    /**
     * Populate the key pair list with the PKCS #12 keystore's key
     * pair aliases.
     *
     * @throws CMException Problem accessing the keystore's entries
     */
    private void populateList()
        throws CMException
    {
        try {
            Vector<String> vKeyPairAliases = new Vector<String>();

            // For each entry in the keystore...
            for (Enumeration<String> aliases = pkcs12KeyStore.aliases(); aliases.hasMoreElements();)
            {
                // Get alias
                String sAlias = (String) aliases.nextElement();

                // Add the alias to the list if the entry has a key
                // and certificates
                if (pkcs12KeyStore.isKeyEntry(sAlias)) {
                	pkcs12KeyStore.getKey(sAlias, new char[] {});
                    Certificate[] certs = pkcs12KeyStore.getCertificateChain(sAlias);

                    if (certs != null && certs.length != 0) {
                        vKeyPairAliases.add(sAlias);
                    }
                }
            }

            if (vKeyPairAliases.size() > 0) {
                jltKeyPairs.setListData(vKeyPairAliases);
                jltKeyPairs.setSelectedIndex(0);
            }
            else {
                // No key pairs available...
                jltKeyPairs.setListData(new String[] { "-- No key pairs present in the Credential Store --" });
                jltKeyPairs.setEnabled(false);
            }
        }
        catch (GeneralSecurityException ex) {
            throw new CMException("Problem occured while accessing PKCS #12 keystore's entries.",
                ex);
        }
    }

    /**
     * 'Certificate Details' button pressed. Display the selected key
     * pair's certificate.
     */
    private void certificateDetailsPressed()
    {
        try {        	
            
        	String sAlias = (String) jltKeyPairs.getSelectedValue();

            assert sAlias != null;

            //Convert the certificate object into an X509Certificate object.
             X509Certificate cert = CMX509Util.convertCertificate(pkcs12KeyStore.getCertificate(sAlias));

            // Supply the certificate to the view certificate dialog
            ViewCertDetailsDialog viewCertificateDialog = new ViewCertDetailsDialog(this,
            		"Certificate details", 
            		true, 
            		(X509Certificate) cert,
            		null);
            viewCertificateDialog.setLocationRelativeTo(this);
            viewCertificateDialog.setVisible(true);
            
        }
        catch (Exception ex) {
        	
            JOptionPane.showMessageDialog(this,
                    "Failed to obtain certificate details to show.", 
                    "Credential Manager Alert",
                    JOptionPane.WARNING_MESSAGE);
            closeDialog();
        }
    }


    /**
     * Import button pressed by user. Store the selected key pair's
     * private and public parts and service URLs and close the dialog.
     */
    public void importPressed()
    {
    	// Get Service URLs
    	serviceURLs = new Vector<String>();
    	Enumeration<?> URLs = (((DefaultListModel) jltServiceURLs.getModel()).elements());
    	 for( ; URLs.hasMoreElements(); ){
    		 serviceURLs.add((String) URLs.nextElement());
    	 }
        	
        String sAlias = (String) jltKeyPairs.getSelectedValue();

        assert sAlias != null;

        try {
            privateKey = pkcs12KeyStore.getKey(sAlias, new char[] {});
            certificateChain = pkcs12KeyStore.getCertificateChain(sAlias);
            alias = sAlias;
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load the private key and certificate chain from PKCS #12 file.", 
                    "Credential Manager Error",
                    JOptionPane.ERROR_MESSAGE);
            closeDialog();
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
        
        if (sURL.length() == 0){ // user entered an empty URL
       		// Warn the user
        	JOptionPane.showMessageDialog(
            		this, 
            		"Service URL cannot be empty",
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
     * Cancel button pressed - close the dialog.
     */
    public void cancelPressed()
    {
    	// set everything to null, just in case some of the values have been set previously and
    	// the user pressed 'cancel' after that
    	privateKey = null;
    	certificateChain = null;
    	serviceURLs = null;
        closeDialog();
    }

    /**
     * Closes the dialog.
     */
    private void closeDialog()
    {
        setVisible(false);
        dispose();
    }
    
}

