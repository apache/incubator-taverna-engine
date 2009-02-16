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
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.security.auth.x500.X500Principal;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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

import net.sf.taverna.t2.security.credentialmanager.CMX509Util;

/**
 * Dialog that displays the details of all trusted certificates
 * keystore allowing the user to pick one or more for import.
 * 
 * @author Alexandra Nenadic
 */
class NewTrustCertsDialog
    extends JDialog
{

	private static final long serialVersionUID = 8702957635188643993L;

	/** List of trusted certs available for import */
    private JList jltTrustCerts;

    /** List of trusted certs available for import */
    private ArrayList<X509Certificate> availableTrustCerts = new ArrayList<X509Certificate>();
    
    /** List of trusted certs selected for import */
    private ArrayList<X509Certificate> selectedTrustCerts;

    /**
     * Creates new form NewTrustCertsDialog where the parent is a frame.
     *
     * @param parent The parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param lCerts List of certificates to choose from
     */
    public NewTrustCertsDialog(JFrame parent, String sTitle, boolean bModal, ArrayList<X509Certificate> lCerts)
    {
        super(parent, sTitle, bModal);
        //System.arraycopy(lCerts, 0, trustCerts, 0, lCerts.length);
        availableTrustCerts = lCerts;
        initComponents();
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        // Instructions
        JLabel jlInstructions = new JLabel("Select one or more certificates for import:");
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

        // List to hold trusted certs' aliases
        jltTrustCerts = new JList();
        jltTrustCerts.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        jltTrustCerts.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent evt)
            {
            	
                if (jltTrustCerts.getSelectedIndex() == -1) {
                    jbImport.setEnabled(false);
                    jbCertificateDetails.setEnabled(false);
                }
                else {
                    jbImport.setEnabled(true);
                    jbCertificateDetails.setEnabled(true);
                }
            }
        });
        // Populate the list
        // Get the certificate subjects' CNs
        ArrayList<String> cns = new ArrayList<String>();
        for (int i = 0; i < availableTrustCerts.size(); i++){
        	
    		String DN = ((X509Certificate) availableTrustCerts.get(i)).getSubjectX500Principal().getName(X500Principal.RFC2253);
    		CMX509Util.parseDN(DN);
    		
        	String CN = CMX509Util.getCN();
        	cns.add(i, CN);
        }
        jltTrustCerts.setListData(cns.toArray());
        jltTrustCerts.setSelectedIndex(0);

        // Put the list into a scroll pane
        JScrollPane jspTrustCerts = new JScrollPane(jltTrustCerts,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jspTrustCerts.getViewport().setBackground(jltTrustCerts.getBackground());
        
        // Put all the trusted cert components together
        JPanel jpTrustCerts = new JPanel(); // BoxLayout
        jpTrustCerts.setLayout(new BoxLayout(jpTrustCerts, BoxLayout.Y_AXIS));
        //jpKeyPairs.setPreferredSize(new Dimension(400, 200));
        jpTrustCerts.setBorder(new CompoundBorder(new CompoundBorder(
            new EmptyBorder(5, 5, 5, 5), new EtchedBorder()), new EmptyBorder(
            5, 5, 5, 5)));
   
        jpInstructions.setAlignmentY(JPanel.LEFT_ALIGNMENT);
        jpTrustCerts.add(jpInstructions);
        jspTrustCerts.setAlignmentY(JPanel.LEFT_ALIGNMENT);
        jpTrustCerts.add(jspTrustCerts);
        jbCertificateDetails.setAlignmentY(JPanel.RIGHT_ALIGNMENT);
        jpTrustCerts.add(jbCertificateDetails);

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
        getContentPane().add(jpTrustCerts, BorderLayout.CENTER);
        getContentPane().add(jpButtons, BorderLayout.SOUTH);

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
     * Certificate Details button pressed.  Display the selected key
     * pair's certificate.
     */
    private void certificateDetailsPressed()
    {
        try {        	
        	
        	int i = jltTrustCerts.getSelectedIndex();
            
        	X509Certificate cert = (X509Certificate) availableTrustCerts.get(i);

            // Supply the certificate to the view certificate dialog
            ViewCertDetailsDialog viewCertificateDialog = new ViewCertDetailsDialog(this,
            		"Certificate details", 
            		true, 
            		cert,
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
     * Get the trusted certificates selected for import.
     *
     * @return The array of trusted certificates selected for import
     */
    public ArrayList<X509Certificate> getTrustedCertificates()
    {
    	return selectedTrustCerts;
    }

   
    /**
     * Import button pressed by user. Store the selected trusted certs
     * and close the dialog.
     */
    public void importPressed()
    {
    	int[] selectedValues = jltTrustCerts.getSelectedIndices();
    	selectedTrustCerts = new ArrayList<X509Certificate>();
    	for (int i= 0; i < selectedValues.length; i++){
    		selectedTrustCerts.add(availableTrustCerts.get(selectedValues[i]));
    	}

        closeDialog();
    }

    
    /**
     * Cancel button pressed - close the dialog.
     */
    public void cancelPressed()
    {
    	// Set selectedTrustCerts to null to indicate that user cancelled
    	selectedTrustCerts = null;
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

