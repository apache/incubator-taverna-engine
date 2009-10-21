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
import java.awt.Color;
import java.awt.Container; 
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.lang.ui.DialogTextArea;
import net.sf.taverna.t2.security.profiles.NoSuchSecurityPropertyException;
import net.sf.taverna.t2.security.profiles.SecurityProperties;
import net.sf.taverna.t2.security.profiles.TransportProperties;
import net.sf.taverna.t2.security.profiles.WSSecurityMessageProperties;
import net.sf.taverna.t2.security.profiles.WSSecurityProfile;
import net.sf.taverna.t2.security.profiles.WSSecurityProfileManager;
import net.sf.taverna.t2.security.profiles.WSSecurityProfileManagerException;

/**
 * Lets the user select a pre-defined or define a new WS Security Profile.
 * 
 * @author Alexandra Nenadic
 *
 */

public class WSSecurityProfileChooser extends JDialog
{		
	private static final long serialVersionUID = -2754223284048320958L;

	private static Logger logger = Logger.getLogger(WSSecurityProfileChooser.class);
    
	/**	Is the WS Security Profile Chooser initialised? */
	private boolean initialised = false;
	
    /** 
     * Manager of system and user-defined WS-Security profiles. 
     */
    protected WSSecurityProfileManager wsSecurityProfileManager;
    
    /**
     * Stores the WS Security Profile selected or created from this dialog.
     */
    private WSSecurityProfile wsSecurityProfile;
	
	/**
	 * List of WS Security profiles read from two files: with system and user-defined profiles.
	 */
	private Vector<WSSecurityProfile> wsSecurityProfiles = new Vector<WSSecurityProfile>();
	 
	/**
	 * List of pre-defined WS Security profiles' names.
	 */
	private Vector<String> wsSecurityProfileNames = new Vector<String>();	
	 
	/**
	 * List of pre-defined WS Security profiles' descriptions.
	 */
	private Vector<String> wsSecurityProfileDescriptions = new Vector<String>();
	
	/**
	 * List with True/False constants.
	 */
	private String[] true_false = {
			SecurityProperties.FALSE, 
			SecurityProperties.TRUE
			};

    ////////////////////////////////////////////////////
    //			GUI Components
    ////////////////////////////////////////////////////
	
    // Components from the 'Select profile' panel
	/**
	 * Check box to indicate that the user is selecting a security profile
	 */
	private JCheckBox jchbSelectProfile;
	
	/**
	 * List of components from the 'Select' panel that have to be enabled/disabled based on the
	 * value of the checkbox.
	 */
	private Vector<Component> selectProfileComponentList = new Vector<Component>();
	
	 /**
	  * Combobox containing a list of possible WS security profiles to choose from. 
	  */
	 private JComboBox jcmbSecurityProfiles;

	 
	 // Components from the 'Create profile' panel
	 // Transport-level properties fields
	/**
	 * Check box to indicate that the user is creating a new security profile
	 */
	private JCheckBox jchbCreateProfile;
	
	/**
	 * List of components from the 'Create' panel that have to be enabled/disabled based on the
	 * value of a checkbox.
	 */
	private Vector<Component> createProfileComponentList = new Vector<Component>();
	
	/**
	 * Panel to hold fields that the user has to populate in order to create a new security profile
	 */ 
	private JTabbedPane jtpCreateProfile;
	
	/**
	 * Combobox for selecting the protocol.
	 */
	private JComboBox jcmbProtocol; 
	
	/**
	 * Field for entering the service port number.
	 */
	private JTextField jtfPort;
	
	/**
	 * Field for selecting the HTTP authentication type.
	 */
	private JComboBox jcmbAuthNType; 
	
	/**
	 * Field for selecting if service requires client authentication (HTTPS only).
	 */
	private JComboBox jcmbRequiresClientCert; 
	
	/**
	 * Field for selecting the proxy certificate depth (HTTPS only).
	 */
	private JComboBox jcmbProxyCertDepth; 
	
	 // Message-level properties fields
	/**
	 * Field for specifying the WS Security policy URL (if service has one).
	 */
	private JTextField jtfWSSecPolicyURL;
	
	/**
	 * Field for specifying WS-Security ACTIONS for the request message.
	 */
	private JComboBox[] jcmbWSSecActionsOutbound;
	
	/**
	 * Field for specifying WS-Security password type for the request message.
	 */
	private JComboBox jcmbPasswordTypeOutbound; 
	
	/**
	 * Field for specifying WS-Security signature parts for the request message.
	 */
	private JTextField jtfSignaturePartsOutbound;
	
	/**
	 * Field for specifying WS-Security signature key id for the request message.
	 */
	private JComboBox jcmbSignatureKeyIdentifierOutbound; 
	
	/**
	 * Field for specifying WS-Security signature algorithm for the request message.
	 */
	private JComboBox jcmbSignatureAlgorithmOutbound; 
	
	/**
	 * Field for specifying WS-Security encryption parts for the request message.
	 */
	private JTextField jtfEncryptionPartsOutbound;
	
	/**
	 * Field for specifying WS-Security encryption key id for the request message.
	 */
	private JComboBox jcmbEncryptionKeyIdentifierOutbound; 
	
	/**
	 * Field for specifying WS-Security encryption algorithm for the request message.
	 */
	private JComboBox jcmbEncryptionAlgorithmOutbound; 
	
	/**
	 * Field for specifying WS-Security encryption key transport algorithm for the request message.
	 */
	private JComboBox jcmbEncryptionKeyTransportAlgorithmOutbound; 
	
	/**
	 * Field for specifying WS-Security ACTIONS for the response message.
	 */
	private JComboBox[] jcmbWSSecActionsInbound;
	
	/**
	 * Field for specifying WS-Security signature parts for the response message.
	 */
	private JTextField jtfSignaturePartsInbound;
	
	/**
	 * Field for specifying WS-Security signature key id for the response message.
	 */
	private JComboBox jcmbSignatureKeyIdentifierInbound; 
	
	/**
	 * Field for specifying WS-Security signature algorithm for the response message.
	 */
	private JComboBox jcmbSignatureAlgorithmInbound; 
	
	/**
	 * Field for specifying WS-Security encryption parts for the response message.
	 */
	private JTextField jtfEncryptionPartsInbound;
	
	/**
	 * Field for specifying WS-Security encryption key id for the response message.
	 */
	private JComboBox jcmbEncryptionKeyIdentifierInbound; 
	
	/**
	 * Field for specifying WS-Security encryption algorithm for the response message.
	 */
	private JComboBox jcmbEncryptionAlgorithmInbound; 
	
	/**
	 * Field for specifying WS-Security encryption key transport algorithm for the response message.
	 */
	private JComboBox jcmbEncryptionKeyTransportAlgorithmInbound; 
	

    /**
     * Creates a new Security Profile Chooser GUI's frame.
     */
    public WSSecurityProfileChooser(Frame owner)
    {
    	super(owner, true); //create a modal dialog
    	
    	// Get the WSSecurityProfileManager instance - it will load 
    	// the pre-defined system and saved user-defined security profiles
    	try {
    		wsSecurityProfileManager = WSSecurityProfileManager.getInstance();
    		
    		wsSecurityProfiles = wsSecurityProfileManager.getWSSecurityProfiles();
    		wsSecurityProfileNames = wsSecurityProfileManager.getWSSecurityProfileNames();	
    		wsSecurityProfileDescriptions = wsSecurityProfileManager.getWSSecurityProfileDescriptions();
        	
        	// Initialise GUI components
        	initComponents();
        	
        	setLocationRelativeTo(owner);

        	// Everything is ok
        	initialised = true;        	
    	}
    	catch(WSSecurityProfileManagerException wsspme){
    		logger.error("WSSecurityProfileChooser: " + wsspme.getMessage());
            JOptionPane.showMessageDialog(this,
            		wsspme.getMessage(),
            		"WS Security Profile Chooser", 
            		JOptionPane.ERROR_MESSAGE);
    	}

    }
	
	
	/**
	 * Is the WS Security Profile Chooser initialised properly?
	 */
	public boolean isInitialised(){
		return initialised;
	}
	
    
    /**
     * Initialises the frame's GUI components.
     */
    public void initComponents()
    {
        //////////////////////////////////////////////////////////////////////////////////////
    	// 'Select' panel to hold pre-defined security profiles from which user can choose one
        //////////////////////////////////////////////////////////////////////////////////////
    	JPanel jpSelectProfile = new JPanel(new BorderLayout());
    	jpSelectProfile.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(5,5,5,5)));
    	// Check box for the Select panel's title border (to be added later)
        jchbSelectProfile = new JCheckBox("Select a pre-defined security profile", true); 

        // Initialise the Select panel's components
        // Panel to hold the combobox and its description
        JPanel jpSelectProfileComponents = new JPanel(new BorderLayout(0,5)); 
        jpSelectProfileComponents.setBorder(new EmptyBorder(5,5,5,5));
        // Combobox with a list of pre-defined profiles
        jcmbSecurityProfiles = new JComboBox(wsSecurityProfileNames);
        jcmbSecurityProfiles.setFont(new Font(null, Font.PLAIN, 11));
        jcmbSecurityProfiles.setMaximumRowCount(15);
        JLabel jlSecurityProfileDescription = new JLabel("Description");
        jlSecurityProfileDescription.setFont(new Font(null, Font.BOLD, 11));
        final DialogTextArea jtaSecurityProfileDescription = new DialogTextArea(
        		wsSecurityProfileDescriptions.elementAt(jcmbSecurityProfiles.getSelectedIndex())); //description text area
        jtaSecurityProfileDescription.setEditable(false);
        jtaSecurityProfileDescription.setOpaque(false);
        jtaSecurityProfileDescription.setBorder(null);
        jtaSecurityProfileDescription.setLineWrap(true);
        jtaSecurityProfileDescription.setWrapStyleWord(true);
        jtaSecurityProfileDescription.setFont(new Font(null, Font.PLAIN, 11));
        // Add components to a panel
        jpSelectProfileComponents.add(jcmbSecurityProfiles, BorderLayout.NORTH);
        jpSelectProfileComponents.add(jlSecurityProfileDescription, BorderLayout.CENTER);
        jpSelectProfileComponents.add(jtaSecurityProfileDescription, BorderLayout.SOUTH);
        // Add to the list of components to be enabled/disabled
        selectProfileComponentList.add(jcmbSecurityProfiles);
        selectProfileComponentList.add(jtaSecurityProfileDescription);
        selectProfileComponentList.add(jlSecurityProfileDescription);
        // Panel to hold buttons
        JPanel jpSelectButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
       // jpSelectButtons.setBorder(new EmptyBorder(5,5,5,5));
        JButton jbView = new JButton("View");
        jbView.setEnabled(false);
        jbView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// TODO viewPressed();				
			}        	
        });

        final JButton jbDelete = new JButton("Delete");
    	jbDelete.setEnabled(false); // disabled by default
        jbDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				deletePressed();			
			}        	
        });
        jpSelectButtons.add(jbView);
        jpSelectButtons.add(jbDelete);

        //selectProfileComponentList.add(jbView);
        selectProfileComponentList.add(jbDelete);
        //// Add all to the Select panel
        jpSelectProfile.add(jchbSelectProfile, BorderLayout.NORTH);
        jpSelectProfile.add(jpSelectProfileComponents, BorderLayout.CENTER);
        jpSelectProfile.add(jpSelectButtons, BorderLayout.SOUTH);
        
        /////////////////////////////////////////////////////////////////////////
    	// 'Create' panel to hold fields required to create a new security profile
        /////////////////////////////////////////////////////////////////////////
        JPanel jpCreateProfile = new JPanel(new BorderLayout(0,5));
    	jpCreateProfile.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(5,5,5,5)));
    	// Check box for the Create panel's title border (to be added later)
        jchbCreateProfile = new JCheckBox("Create a new security profile", false); 
        
        // Initialise the Create panel's components
        // Tabbed panel to hold Transport-level and Message-level tabs 
        jtpCreateProfile = new  JTabbedPane();
        //jtpCreateProfile.setBorder(new EmptyBorder(5,5,5,5));
        // Scroll pane to hold tabbed pane - on small screens the frame is too big so we need scroll bars
        JScrollPane jspCreateProfile = new JScrollPane(jtpCreateProfile);
        jspCreateProfile.setOpaque(false);
        jspCreateProfile.getViewport().setOpaque(false);
        jspCreateProfile.setBorder(null);
                
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
        
        GridBagConstraints gbcCol2 = new GridBagConstraints();
        gbcCol2.gridx = 2;
        gbcCol2.gridwidth = 1;
        gbcCol2.gridheight = 1;
        gbcCol2.insets = new Insets(2, 5, 2, 5);
        gbcCol2.anchor = GridBagConstraints.LINE_START;
        
        GridBagConstraints gbcCol3 = new GridBagConstraints();
        gbcCol3.gridx = 3;
        gbcCol3.gridwidth = 1;
        gbcCol3.gridheight = 1;
        gbcCol3.insets = new Insets(2, 5, 2, 5);
        gbcCol3.anchor = GridBagConstraints.LINE_START;
        
        // Transport-level tab
        JPanel jpTransport = new JPanel(new GridBagLayout());
        
        // Protocol field
        JLabel jlProtocol = new JLabel("Protocol");
        jlProtocol.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlProtocol = (GridBagConstraints) gbcCol0.clone();
        gbc_jlProtocol.gridy = 0;
        jcmbProtocol = new JComboBox(TransportProperties.PROTOCOLS);
        jcmbProtocol.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbProtocol = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbProtocol.gridy = 0;
        final JLabel jlProtocolDescription = new JLabel("<html>Transfer protocol to use.</html>");
        jlProtocolDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlProtocolDescription.isEnabled())
					jlProtocolDescription.setForeground(Color.gray);
				else 
					jlProtocolDescription.setForeground(Color.black);
			}	
        	
        });
        jlProtocolDescription.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlProtocolDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlProtocolDescription.gridy = 0;
        createProfileComponentList.add(jlProtocol);
        createProfileComponentList.add(jcmbProtocol);
        createProfileComponentList.add(jlProtocolDescription);
        jpTransport.add(jlProtocol, gbc_jlProtocol);
        jpTransport.add(jcmbProtocol, gbc_jcmbProtocol);
        jpTransport.add(jlProtocolDescription, gbc_jlProtocolDescription);
        
        // Port field
        JLabel jlPort = new JLabel("Port");
        jlPort.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlPort = (GridBagConstraints) gbcCol0.clone();
        gbc_jlPort.gridy = 1;
        jtfPort = new JTextField(8);
        jtfPort.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbPort = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbPort.gridy = 1;
        final JLabel jlPortDescription = new JLabel("<html>Port on which the protocol operates. If not specified, <br>defaults to 80 for HTTP and 443 for HTTPS.</html>");
        jlPortDescription.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlPortDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlPortDescription.gridy = 1;
        jlPortDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlPortDescription.isEnabled())
					jlPortDescription.setForeground(Color.gray);
				else 
					jlPortDescription.setForeground(Color.black);
			}	
        	
        });
        createProfileComponentList.add(jlPort);
        createProfileComponentList.add(jtfPort);
        createProfileComponentList.add(jlPortDescription);
        jpTransport.add(jlPort, gbc_jlPort);
        jpTransport.add(jtfPort, gbc_jcmbPort);
        jpTransport.add(jlPortDescription, gbc_jlPortDescription);
        
        // AuthNType field
        JLabel jlAuthNType = new JLabel("HTTP authentication type");
        jlAuthNType.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlAuthNType = (GridBagConstraints) gbcCol0.clone();
        gbc_jlAuthNType.gridy = 2;
        jcmbAuthNType = new JComboBox(TransportProperties.HTTP_AUTHENTICATION_TYPES);
        jcmbAuthNType.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbAuthNType = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbAuthNType.gridy = 2;
        final JLabel jlAuthNTypeDescription = new JLabel("<html>Type of HTTP authentication information <br> to be embedded in HTTP headers.</html>");
        jlAuthNTypeDescription.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlAuthNTypeDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlAuthNTypeDescription.gridy = 2;
        jlAuthNTypeDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlAuthNTypeDescription.isEnabled())
					jlAuthNTypeDescription.setForeground(Color.gray);
				else 
					jlAuthNTypeDescription.setForeground(Color.black);
			}	
        	
        });
        createProfileComponentList.add(jlAuthNType);
        createProfileComponentList.add(jcmbAuthNType);
        createProfileComponentList.add(jlAuthNTypeDescription);
        jpTransport.add(jlAuthNType, gbc_jlAuthNType);
        jpTransport.add(jcmbAuthNType, gbc_jcmbAuthNType);
        jpTransport.add(jlAuthNTypeDescription, gbc_jlAuthNTypeDescription);
        
        // RequireClientCert field
        final JLabel jlRequireClientCert = new JLabel("Requires client certificate");
        jlRequireClientCert.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlRequireClientCert = (GridBagConstraints) gbcCol0.clone();
        gbc_jlRequireClientCert.gridy = 3;
        jcmbRequiresClientCert = new JComboBox(true_false);
        jcmbRequiresClientCert.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbRequireClientCert = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbRequireClientCert.gridy = 3;
        final JLabel jlRequireClientCertDescription = new JLabel("<html>If protocol is HTTPS, indicates whether the client is to be <br>authenticated via its certificate (in addition to the server).</html>");
        jlRequireClientCertDescription.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlRequireClientCertDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlRequireClientCertDescription.gridy = 3;
        jlRequireClientCertDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlRequireClientCertDescription.isEnabled())
					jlRequireClientCertDescription.setForeground(Color.gray);
				else 
					jlRequireClientCertDescription.setForeground(Color.black);
			}	
        	
        });
        createProfileComponentList.add(jlRequireClientCert);
        createProfileComponentList.add(jcmbRequiresClientCert);
        createProfileComponentList.add(jlRequireClientCertDescription);
        jpTransport.add(jlRequireClientCert, gbc_jlRequireClientCert);
        jpTransport.add(jcmbRequiresClientCert, gbc_jcmbRequireClientCert);
        jpTransport.add(jlRequireClientCertDescription,gbc_jlRequireClientCertDescription);
        
        // ProxyCertDepth field
        final JLabel jlProxyCertDepth = new JLabel("Proxy certificates depth");
        jlProxyCertDepth.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlproxyCertDepth = (GridBagConstraints) gbcCol0.clone();
        gbc_jlproxyCertDepth.gridy = 4;
        jcmbProxyCertDepth = new JComboBox(TransportProperties.PROXY_CERT_DEPTHS);
        jcmbProxyCertDepth.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbProxyCertDepth = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbProxyCertDepth.gridy = 4;
        final JLabel jlProxyCertDepthDescription = new JLabel("<html>If protocol is HTTPS, indicates whether client proxy <br>" +
        		"certificates are accepted and to which depth.<br>" +
        		"Value 0 indicates proxies are not accepted.</html>");
        jlProxyCertDepthDescription.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlProxyCertDepthDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlProxyCertDepthDescription.gridy = 4;
        jlProxyCertDepthDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlProxyCertDepthDescription.isEnabled())
					jlProxyCertDepthDescription.setForeground(Color.gray);
				else 
					jlProxyCertDepthDescription.setForeground(Color.black);
			}	
        	
        });
        createProfileComponentList.add(jlProxyCertDepth);
        createProfileComponentList.add(jcmbProxyCertDepth);
        createProfileComponentList.add(jlProxyCertDepthDescription);
        jpTransport.add(jlProxyCertDepth, gbc_jlproxyCertDepth);
        jpTransport.add(jcmbProxyCertDepth, gbc_jcmbProxyCertDepth); 
        jpTransport.add(jlProxyCertDepthDescription,gbc_jlProxyCertDepthDescription);

        // Add some action listeners now that all fields are defined
        jchbSelectProfile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
					boolean selected =  jchbSelectProfile.isSelected();
					// Enable/Disable Select panel components
					toggleComponents(selectProfileComponentList,selected);
					// Uncheck/check Create panel checkbox
					jchbCreateProfile.setSelected(!selected);
					// Disable/enable Create panel components
					toggleComponents(createProfileComponentList, !selected);
					
					//Fire the ACTIONS on the select profile cobmo box - 
					//that will make delete button to become enabled/disabled properly
					if (selected){
						// This will cause the select profile combo box to fire up
						// without changing the value of the field
						jcmbSecurityProfiles.setSelectedIndex(jcmbSecurityProfiles.getSelectedIndex());
					}
			}
        });
        jchbCreateProfile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				boolean selected =  jchbCreateProfile.isSelected();
				// Enable/disable Create panel components
				toggleComponents(createProfileComponentList,selected);
				// Uncheck/check Select panel checkbox
				jchbSelectProfile.setSelected(!selected);
				// Disable/enable Select panel components
				toggleComponents(selectProfileComponentList, !selected);
		        
				//Fire the ACTIONS on the Protocol field - that will make other fields become enabled/disabled properly
				if (selected){
					// This will cause the actionlistener on the Protocol filed to fire up
					// without changing the value of the field
					jcmbProtocol.setSelectedIndex(jcmbProtocol.getSelectedIndex());
				}
			}				
        });
        jcmbSecurityProfiles.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// Set the description label
		        int securityProfileIndex = jcmbSecurityProfiles.getSelectedIndex();
		        jtaSecurityProfileDescription.setText(wsSecurityProfileDescriptions.elementAt(securityProfileIndex));
		        
		        // If selected profile is user-defined, then enable the delete button
		        if (wsSecurityProfileManager.isUserDefinedProfile((String)jcmbSecurityProfiles.getSelectedItem())){
		        	jbDelete.setEnabled(true);
		        }
		        else{
		        	jbDelete.setEnabled(false);
		        }
		        	
			}
        });
        jcmbProtocol.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

					if (!((String) jcmbProtocol.getSelectedItem()).contains("HTTPS")){
						jcmbRequiresClientCert.setEnabled(false);
						jlRequireClientCert.setEnabled(false);
						jlRequireClientCertDescription.setEnabled(false);
						jcmbProxyCertDepth.setEnabled(false);
						jlProxyCertDepth.setEnabled(false);
						jlProxyCertDepthDescription.setEnabled(false);
					}
					else{
						jcmbRequiresClientCert.setEnabled(true);
						jlRequireClientCert.setEnabled(true);
						jlRequireClientCertDescription.setEnabled(true);
						jlRequireClientCertDescription.setEnabled(true);
		    			if (((String) jcmbRequiresClientCert.getSelectedItem()).equals(SecurityProperties.FALSE)){
		    				jcmbProxyCertDepth.setEnabled(false);
		    				jlProxyCertDepth.setEnabled(false);
		    				jlProxyCertDepthDescription.setEnabled(false);
		    			}
		    			else{
							jcmbProxyCertDepth.setEnabled(true);
							jlProxyCertDepth.setEnabled(true);
							jlProxyCertDepthDescription.setEnabled(true);
		    			}
					}				
			}
        });
        jcmbRequiresClientCert.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e) {

	    			if (((String) jcmbRequiresClientCert.getSelectedItem()).equals(SecurityProperties.FALSE)){
	    				jcmbProxyCertDepth.setEnabled(false);
	    				jlProxyCertDepth.setEnabled(false);
	    				jlProxyCertDepthDescription.setEnabled(false);
	    			}
	    			else{
	    				jcmbProxyCertDepth.setEnabled(true);
	    				jlProxyCertDepth.setEnabled(true);
	    				jlProxyCertDepthDescription.setEnabled(true);
	    			}

    		}
        });
        
        
        //Fire some ACTIONS on the fields to make them enbled/disabled properly
        jcmbProtocol.setSelectedIndex(0);
        jcmbRequiresClientCert.setSelectedIndex(0);
        
        // Add Transport-level tab to the Create panel's tabbed pane
        jtpCreateProfile.addTab("Transport-level",jpTransport);
        
        
        // Message-level tab
        JPanel jpMessage = new JPanel(new BorderLayout(0,10));
        
        // WS-Security policy URL field
        JPanel jpWSSecPolicyURL = new JPanel();
        final JLabel jlWSSecPolicyURL = new JLabel("<html>WS-Security policy URL<br>" +
        		"(If defined, the properties <br>below are not required)</html>");
        jlWSSecPolicyURL.setFont(new Font(null, Font.PLAIN, 11));
        jlWSSecPolicyURL.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlWSSecPolicyURL.isEnabled())
					jlWSSecPolicyURL.setForeground(Color.gray);
				else 
					jlWSSecPolicyURL.setForeground(Color.black);
			}	
        });
        jtfWSSecPolicyURL = new JTextField(40);
        jtfWSSecPolicyURL.setFont(new Font(null, Font.PLAIN, 11));
        createProfileComponentList.add(jlWSSecPolicyURL);
        createProfileComponentList.add(jtfWSSecPolicyURL);
        jpWSSecPolicyURL.add(jlWSSecPolicyURL);
        jpWSSecPolicyURL.add(jtfWSSecPolicyURL);      
        jpMessage.add(jpWSSecPolicyURL, BorderLayout.NORTH); 
     
        // Outbound/Inbound tabbed pane on the Message-level tab
        final JTabbedPane jtpMessage = new JTabbedPane();
        
         // Outbound (i.e REQUEST properties) tab
        JPanel jpMessageOutbound = new JPanel(new GridBagLayout());
        jpMessageOutbound.setBorder(new EmptyBorder(0,5,0,5));
        
        // Outbound Actions fields
        JLabel jlWSSecActionsOutbound = new JLabel("Actions (a list of WS-Security ACTIONS to be performed on a REQUEST message):");
        jlWSSecActionsOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlWSSecActionsOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlWSSecActionsOutbound.gridy = 2;
        gbc_jlWSSecActionsOutbound.gridwidth=4;
        createProfileComponentList.add(jlWSSecActionsOutbound);
        jpMessageOutbound.add(jlWSSecActionsOutbound, gbc_jlWSSecActionsOutbound);
        
        // Only 8 possible ACTIONS exist - define 8 different fields for them in two columns
        jcmbWSSecActionsOutbound = new JComboBox[8]; 
        for (int i = 0; i< jcmbWSSecActionsOutbound.length/2 ; i++){
            JLabel jlWSSecAction = new JLabel("Action " + (i+1));
            jlWSSecAction.setFont(new Font(null, Font.PLAIN, 11));
            GridBagConstraints gbc_jlWSSecAction = (GridBagConstraints) gbcCol0.clone();
            gbc_jlWSSecAction.gridy = i+3;
            jcmbWSSecActionsOutbound[i] = new JComboBox(WSSecurityMessageProperties.ACTIONS);
            jcmbWSSecActionsOutbound[i].setFont(new Font(null, Font.PLAIN, 11));
            GridBagConstraints gbc_jcmbWSSecAction = (GridBagConstraints) gbcCol1.clone();
            gbc_jcmbWSSecAction.gridy = i+3;
            createProfileComponentList.add(jlWSSecAction);
            createProfileComponentList.add(jcmbWSSecActionsOutbound[i]);
            jpMessageOutbound.add(jlWSSecAction, gbc_jlWSSecAction);
            jpMessageOutbound.add(jcmbWSSecActionsOutbound[i], gbc_jcmbWSSecAction);
        }
        
        for (int i = 4; i< 8 ; i++){
            JLabel jlWSSecAction = new JLabel("Action " + (i+1));
            jlWSSecAction.setFont(new Font(null, Font.PLAIN, 11));
            GridBagConstraints gbc_jlWSSecAction = (GridBagConstraints) gbcCol2.clone();
            gbc_jlWSSecAction.gridy = i%4+3;
            jcmbWSSecActionsOutbound[i] = new JComboBox(WSSecurityMessageProperties.ACTIONS);
            jcmbWSSecActionsOutbound[i].setFont(new Font(null, Font.PLAIN, 11));
            GridBagConstraints gbc_jcmbWSSecAction = (GridBagConstraints) gbcCol3.clone();
            gbc_jcmbWSSecAction.gridy = i%4+3;
            createProfileComponentList.add(jlWSSecAction);
            createProfileComponentList.add(jcmbWSSecActionsOutbound[i]);
            jpMessageOutbound.add(jlWSSecAction, gbc_jlWSSecAction);
            jpMessageOutbound.add(jcmbWSSecActionsOutbound[i], gbc_jcmbWSSecAction);
        }
        
        // Password Type field (can be defined for outbound messsages only)
        JLabel jlPasswordTypeOutbound = new JLabel("Password type");
        jlPasswordTypeOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlPasswordTypeOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlPasswordTypeOutbound.gridy = 7;
        jcmbPasswordTypeOutbound = new JComboBox(WSSecurityMessageProperties.PASSWORD_TYPES);
        jcmbPasswordTypeOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbPasswordTypeOutbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbPasswordTypeOutbound.gridy = 7;
        final JLabel jlPasswordTypeOutboundDescription = new JLabel("<html>Specifies whether password will be <br>" +
        		"sent in plaintext or digest form.</html>");
        jlPasswordTypeOutboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlPasswordTypeOutboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlPasswordTypeOutboundDescription.isEnabled())
					jlPasswordTypeOutboundDescription.setForeground(Color.gray);
				else 
					jlPasswordTypeOutboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlPasswordTypeOutboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlPasswordTypeOutboundDescription.gridy = 7;
        gbc_jlPasswordTypeOutboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlPasswordTypeOutbound);
        createProfileComponentList.add(jcmbPasswordTypeOutbound);
        createProfileComponentList.add(jlPasswordTypeOutboundDescription);
        jpMessageOutbound.add(jlPasswordTypeOutbound, gbc_jlPasswordTypeOutbound);
        jpMessageOutbound.add(jcmbPasswordTypeOutbound, gbc_jcmbPasswordTypeOutbound);
        jpMessageOutbound.add(jlPasswordTypeOutboundDescription, gbc_jlPasswordTypeOutboundDescription);
        
        // Outbound Signature Parts field
        JLabel jlSignaturePartsOutbound = new JLabel("Message parts to sign");
        jlSignaturePartsOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlSignaturePartsOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlSignaturePartsOutbound.gridy = 8;
        jtfSignaturePartsOutbound = new JTextField(40);
        jtfSignaturePartsOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jtfSignaturePartsOutbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jtfSignaturePartsOutbound.gridy = 8;
        gbc_jtfSignaturePartsOutbound.gridwidth = 3;
        createProfileComponentList.add(jlSignaturePartsOutbound);
        createProfileComponentList.add(jtfSignaturePartsOutbound);
        jpMessageOutbound.add(jlSignaturePartsOutbound, gbc_jlSignaturePartsOutbound);
        jpMessageOutbound.add(jtfSignaturePartsOutbound, gbc_jtfSignaturePartsOutbound);
        
        // Outbound Signature Key Identifier field
        JLabel jlSignatureKeyIdentifierOutbound = new JLabel("Signature key identifier");
        jlSignatureKeyIdentifierOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlSignatureKeyIdentifierOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlSignatureKeyIdentifierOutbound.gridy = 9;
        jcmbSignatureKeyIdentifierOutbound = new JComboBox(WSSecurityMessageProperties.SIGNATURE_KEY_IDENTIFIERS);
        jcmbSignatureKeyIdentifierOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbSignatureKeyIdentifierOutbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbSignatureKeyIdentifierOutbound.gridy = 9;
        final JLabel jlSignatureKeyIdentifierOutboundDescription = new JLabel("<html>Specifies how the key to be used to sign<br>" +
        		"the request message will be referred to.</html>");
        jlSignatureKeyIdentifierOutboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlSignatureKeyIdentifierOutboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlSignatureKeyIdentifierOutboundDescription.isEnabled())
					jlSignatureKeyIdentifierOutboundDescription.setForeground(Color.gray);
				else 
					jlSignatureKeyIdentifierOutboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlSignatureKeyIdentifierOutboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlSignatureKeyIdentifierOutboundDescription.gridy = 9;
        gbc_jlSignatureKeyIdentifierOutboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlSignatureKeyIdentifierOutbound);
        createProfileComponentList.add(jcmbSignatureKeyIdentifierOutbound);
        createProfileComponentList.add(jlSignatureKeyIdentifierOutboundDescription);
        jpMessageOutbound.add(jlSignatureKeyIdentifierOutbound, gbc_jlSignatureKeyIdentifierOutbound);
        jpMessageOutbound.add(jcmbSignatureKeyIdentifierOutbound, gbc_jcmbSignatureKeyIdentifierOutbound);
        jpMessageOutbound.add(jlSignatureKeyIdentifierOutboundDescription, gbc_jlSignatureKeyIdentifierOutboundDescription);
        
        // Outbound Signature Algorithm field
        JLabel jlSignatureAlgorithmOutbound = new JLabel("Signature alg.");
        jlSignatureAlgorithmOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlSignatureAlgorithmOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlSignatureAlgorithmOutbound.gridy = 10;
        jcmbSignatureAlgorithmOutbound = new JComboBox(WSSecurityMessageProperties.SIGNATURE_ALGORITHMS_SHORT);
        jcmbSignatureAlgorithmOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbSignatureAlgorithmOutbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbSignatureAlgorithmOutbound.gridy = 10;
        final JLabel jlSignatureAlgorithmOutboundDescription = new JLabel("<html>Specifies algorithm to be used <br>" +
        		"for signing the request message.</html>");
        jlSignatureAlgorithmOutboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlSignatureAlgorithmOutboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlSignatureAlgorithmOutboundDescription.isEnabled())
					jlSignatureAlgorithmOutboundDescription.setForeground(Color.gray);
				else 
					jlSignatureAlgorithmOutboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlSignatureAlgorithmOutboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlSignatureAlgorithmOutboundDescription.gridy = 10;
        gbc_jlSignatureAlgorithmOutboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlSignatureAlgorithmOutbound);
        createProfileComponentList.add(jcmbSignatureAlgorithmOutbound);
        createProfileComponentList.add(jlSignatureAlgorithmOutboundDescription);
        jpMessageOutbound.add(jlSignatureAlgorithmOutbound, gbc_jlSignatureAlgorithmOutbound);
        jpMessageOutbound.add(jcmbSignatureAlgorithmOutbound, gbc_jcmbSignatureAlgorithmOutbound);
        jpMessageOutbound.add(jlSignatureAlgorithmOutboundDescription, gbc_jlSignatureAlgorithmOutboundDescription);
        
        // Outbound Encryption Parts field
        JLabel jlEncryptionPartsOutbound = new JLabel("Message parts to encrypt");
        jlEncryptionPartsOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlEncryptionPartsOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlEncryptionPartsOutbound.gridy = 11;
        jtfEncryptionPartsOutbound = new JTextField(40);
        jtfEncryptionPartsOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jtfEncryptionPartsOutbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jtfEncryptionPartsOutbound.gridy = 11;
        gbc_jtfEncryptionPartsOutbound.gridwidth = 3;
        createProfileComponentList.add(jlEncryptionPartsOutbound);
        createProfileComponentList.add(jtfEncryptionPartsOutbound);
        jpMessageOutbound.add(jlEncryptionPartsOutbound, gbc_jlEncryptionPartsOutbound);
        jpMessageOutbound.add(jtfEncryptionPartsOutbound, gbc_jtfEncryptionPartsOutbound);
        
        // Outbound Encryption Key Identifier field
        JLabel jlEncryptionKeyIdentifierOutbound = new JLabel("Encryption key identifier");
        jlEncryptionKeyIdentifierOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlEncryptionKeyIdentifierOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlEncryptionKeyIdentifierOutbound.gridy =12;
        jcmbEncryptionKeyIdentifierOutbound = new JComboBox(WSSecurityMessageProperties.ENCRYPTION_KEY_IDENTIFIERS);
        jcmbEncryptionKeyIdentifierOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbEncryptionKeyIdentifierOutbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbEncryptionKeyIdentifierOutbound.gridy = 12;
        final JLabel jlEncryptionKeyIdentifierOutboundDescription = new JLabel("<html>Specifies how the key to be used to encrypt<br>" +
			"the request message will be referred to.</html>");
        jlEncryptionKeyIdentifierOutboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlEncryptionKeyIdentifierOutboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlEncryptionKeyIdentifierOutboundDescription.isEnabled())
					jlEncryptionKeyIdentifierOutboundDescription.setForeground(Color.gray);
				else 
					jlEncryptionKeyIdentifierOutboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlEncryptionKeyIdentifierOutboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlEncryptionKeyIdentifierOutboundDescription.gridy = 12;
        gbc_jlEncryptionKeyIdentifierOutboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlEncryptionKeyIdentifierOutbound);
        createProfileComponentList.add(jcmbEncryptionKeyIdentifierOutbound);
        createProfileComponentList.add(jlEncryptionKeyIdentifierOutboundDescription);
        jpMessageOutbound.add(jlEncryptionKeyIdentifierOutbound, gbc_jlEncryptionKeyIdentifierOutbound);
        jpMessageOutbound.add(jcmbEncryptionKeyIdentifierOutbound, gbc_jcmbEncryptionKeyIdentifierOutbound);
        jpMessageOutbound.add(jlEncryptionKeyIdentifierOutboundDescription, gbc_jlEncryptionKeyIdentifierOutboundDescription);
        
        // Outbound Encryption Algorithm field
        JLabel jlEncryptionAlgorithmOutbound = new JLabel("Encryption alg.");
        jlEncryptionAlgorithmOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlEncryptionAlgorithmOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlEncryptionAlgorithmOutbound.gridy = 13;
        jcmbEncryptionAlgorithmOutbound = new JComboBox(WSSecurityMessageProperties.ENCRYPTION_ALGORITHMS_SHORT);
        jcmbEncryptionAlgorithmOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbEncryptionAlgorithmOutbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbEncryptionAlgorithmOutbound.gridy =13;
        final JLabel jlEncrytpionAlgorithmOutboundDescription = new JLabel("<html>Specifies algorithm to be used <br>" +
        		"for encrypting the message.</html>");
        jlEncrytpionAlgorithmOutboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlEncrytpionAlgorithmOutboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlEncrytpionAlgorithmOutboundDescription.isEnabled())
					jlEncrytpionAlgorithmOutboundDescription.setForeground(Color.gray);
				else 
					jlEncrytpionAlgorithmOutboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlEncrytpionAlgorithmOutboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlEncrytpionAlgorithmOutboundDescription.gridy = 13;
        gbc_jlEncrytpionAlgorithmOutboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlEncryptionAlgorithmOutbound);
        createProfileComponentList.add(jcmbEncryptionAlgorithmOutbound);
        createProfileComponentList.add(jlEncrytpionAlgorithmOutboundDescription);
        jpMessageOutbound.add(jlEncryptionAlgorithmOutbound, gbc_jlEncryptionAlgorithmOutbound);
        jpMessageOutbound.add(jcmbEncryptionAlgorithmOutbound, gbc_jcmbEncryptionAlgorithmOutbound);
        jpMessageOutbound.add(jlEncrytpionAlgorithmOutboundDescription, gbc_jlEncrytpionAlgorithmOutboundDescription);
        
        // Outbound Encryption Key Transport Algorithm field
        JLabel jlEncryptionKeyTransportAlgorithmOutbound = new JLabel("Encryption key transport alg.");
        jlEncryptionKeyTransportAlgorithmOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlEncryptionKeyTransportAlgorithmOutbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlEncryptionKeyTransportAlgorithmOutbound.gridy = 14;
        jcmbEncryptionKeyTransportAlgorithmOutbound = new JComboBox(WSSecurityMessageProperties.ENCRYPTION_KEY_TRANSPORT_ALGORITHMS_SHORT);
        jcmbEncryptionKeyTransportAlgorithmOutbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbEncryptionKeyTransportAlgorithmOutbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbEncryptionKeyTransportAlgorithmOutbound.gridy =14;
        final JLabel jlEncryptionKeyTransportAlgorithmOutboundDescription = new JLabel("<html>Specifies algorithm to be used to encrypt the <br>" +
        		"symmetric key used for encryption of the message.</html>");
        jlEncryptionKeyTransportAlgorithmOutboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlEncryptionKeyTransportAlgorithmOutboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlEncryptionKeyTransportAlgorithmOutboundDescription.isEnabled())
					jlEncryptionKeyTransportAlgorithmOutboundDescription.setForeground(Color.gray);
				else 
					jlEncryptionKeyTransportAlgorithmOutboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlEncryptionKeyTransportAlgorithmOutboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlEncryptionKeyTransportAlgorithmOutboundDescription.gridy = 14;
        gbc_jlEncryptionKeyTransportAlgorithmOutboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlEncryptionKeyTransportAlgorithmOutbound);
        createProfileComponentList.add(jcmbEncryptionKeyTransportAlgorithmOutbound);
        createProfileComponentList.add(jlEncryptionKeyTransportAlgorithmOutboundDescription);
        jpMessageOutbound.add(jlEncryptionKeyTransportAlgorithmOutbound, gbc_jlEncryptionKeyTransportAlgorithmOutbound);
        jpMessageOutbound.add(jcmbEncryptionKeyTransportAlgorithmOutbound, gbc_jcmbEncryptionKeyTransportAlgorithmOutbound);
        jpMessageOutbound.add(jlEncryptionKeyTransportAlgorithmOutboundDescription, gbc_jlEncryptionKeyTransportAlgorithmOutboundDescription);
        
        // Add Outbound tab to the Message-level tabbed pane
        jtpMessage.addTab("REQUEST properties",jpMessageOutbound);       
        
        // Inbound (i.e. RESPONSE properties) tab
        JPanel jpMessageInbound = new JPanel(new GridBagLayout());
        jpMessageInbound.setBorder(new EmptyBorder(0,5,0,5));

        // Inbound Actions fields
        JLabel jlWSSecActionsInbound = new JLabel("Actions (a list of WS-Security ACTIONS to be performed on a RESPONSE message):");
        jlWSSecActionsInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlWSSecActionsInbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlWSSecActionsInbound.gridy = 2;
        gbc_jlWSSecActionsInbound.gridwidth=4;
        createProfileComponentList.add(jlWSSecActionsInbound);
        jpMessageInbound.add(jlWSSecActionsInbound, gbc_jlWSSecActionsInbound);
        
        // Only 8 possible ACTIONS exist - define 8 different fields for them
        jcmbWSSecActionsInbound = new JComboBox[8]; 
        for (int i = 0; i< jcmbWSSecActionsInbound.length/2 ; i++){
            JLabel jlWSSecAction = new JLabel("Action " + (i+1));
            jlWSSecAction.setFont(new Font(null, Font.PLAIN, 11));
            GridBagConstraints gbc_jlWSSecAction = (GridBagConstraints) gbcCol0.clone();
            gbc_jlWSSecAction.gridy = i+3;
            jcmbWSSecActionsInbound[i] = new JComboBox(WSSecurityMessageProperties.ACTIONS);
            jcmbWSSecActionsInbound[i].setFont(new Font(null, Font.PLAIN, 11));
            GridBagConstraints gbc_jcmbWSSecAction = (GridBagConstraints) gbcCol1.clone();
            gbc_jcmbWSSecAction.gridy = i+3;
            createProfileComponentList.add(jlWSSecAction);
            createProfileComponentList.add(jcmbWSSecActionsInbound[i]);
            jpMessageInbound.add(jlWSSecAction, gbc_jlWSSecAction);
            jpMessageInbound.add(jcmbWSSecActionsInbound[i], gbc_jcmbWSSecAction);
        }
        
        for (int i = 4; i< 8 ; i++){
            JLabel jlWSSecAction = new JLabel("Action " + (i+1));
            jlWSSecAction.setFont(new Font(null, Font.PLAIN, 11));
            GridBagConstraints gbc_jlWSSecAction = (GridBagConstraints) gbcCol2.clone();
            gbc_jlWSSecAction.gridy = i%4+3;
            jcmbWSSecActionsInbound[i] = new JComboBox(WSSecurityMessageProperties.ACTIONS);
            jcmbWSSecActionsInbound[i].setFont(new Font(null, Font.PLAIN, 11));
            GridBagConstraints gbc_jcmbWSSecAction = (GridBagConstraints) gbcCol3.clone();
            gbc_jcmbWSSecAction.gridy = i%4+3;
            createProfileComponentList.add(jlWSSecAction);
            createProfileComponentList.add(jcmbWSSecActionsInbound[i]);
            jpMessageInbound.add(jlWSSecAction, gbc_jlWSSecAction);
            jpMessageInbound.add(jcmbWSSecActionsInbound[i], gbc_jcmbWSSecAction);
        }    
        
        // Inbound Signature Parts field
        JLabel jlSignaturePartsInbound = new JLabel("Message parts to sign");
        jlSignaturePartsInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlSignaturePartsInbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlSignaturePartsInbound.gridy = 8;
        jtfSignaturePartsInbound = new JTextField(40);
        jtfSignaturePartsInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jtfSignaturePartsInbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jtfSignaturePartsInbound.gridy = 8;
        gbc_jtfSignaturePartsInbound.gridwidth = 3;
        createProfileComponentList.add(jlSignaturePartsInbound);
        createProfileComponentList.add(jtfSignaturePartsInbound);
        jpMessageInbound.add(jlSignaturePartsInbound, gbc_jlSignaturePartsInbound);
        jpMessageInbound.add(jtfSignaturePartsInbound, gbc_jtfSignaturePartsInbound);
        
        // Inbound Signature Key Identifier field
        JLabel jlSignatureKeyIdentifierInbound = new JLabel("Signature key identifier");
        jlSignatureKeyIdentifierInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlSignatureKeyIdentifierInbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlSignatureKeyIdentifierInbound.gridy = 9;
        jcmbSignatureKeyIdentifierInbound = new JComboBox(WSSecurityMessageProperties.SIGNATURE_KEY_IDENTIFIERS);
        jcmbSignatureKeyIdentifierInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbSignatureKeyIdentifierInbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbSignatureKeyIdentifierInbound.gridy = 9;
        final JLabel jlSignatureKeyIdentifierInboundDescription = new JLabel("<html>Specifies how the key to be used to verify<br>" +
        		"the response message will be referred to.</html>");
        jlSignatureKeyIdentifierInboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlSignatureKeyIdentifierInboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			if (!jlSignatureKeyIdentifierInboundDescription.isEnabled())
				jlSignatureKeyIdentifierInboundDescription.setForeground(Color.gray);
			else 
				jlSignatureKeyIdentifierInboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlSignatureKeyIdentifierInboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlSignatureKeyIdentifierInboundDescription.gridy = 9;
        gbc_jlSignatureKeyIdentifierInboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlSignatureKeyIdentifierInbound);
        createProfileComponentList.add(jcmbSignatureKeyIdentifierInbound);
        createProfileComponentList.add(jlSignatureKeyIdentifierInboundDescription);
        jpMessageInbound.add(jlSignatureKeyIdentifierInbound, gbc_jlSignatureKeyIdentifierInbound);
        jpMessageInbound.add(jcmbSignatureKeyIdentifierInbound, gbc_jcmbSignatureKeyIdentifierInbound);
        jpMessageInbound.add(jlSignatureKeyIdentifierInboundDescription, gbc_jlSignatureKeyIdentifierInboundDescription);
        
        // Inbound Signature Algorithm field
        JLabel jlSignatureAlgorithmInbound = new JLabel("Signature alg.");
        jlSignatureAlgorithmInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlSignatureAlgorithmInbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlSignatureAlgorithmInbound.gridy = 10;
        jcmbSignatureAlgorithmInbound = new JComboBox(WSSecurityMessageProperties.SIGNATURE_ALGORITHMS_SHORT);
        jcmbSignatureAlgorithmInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbSignatureAlgorithmInbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbSignatureAlgorithmInbound.gridy = 10;
        final JLabel jlSignatureAlgorithmInboundDescription = new JLabel("<html>Specifies algorithm to be used<br>" +
		"for verifying the signed response message.</html>");
        jlSignatureAlgorithmInboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlSignatureAlgorithmInboundDescription.addPropertyChangeListener(new PropertyChangeListener(){
        	public void propertyChange(PropertyChangeEvent evt) {
        		if (!jlSignatureAlgorithmInboundDescription.isEnabled())
        			jlSignatureAlgorithmInboundDescription.setForeground(Color.gray);
        		else 
        			jlSignatureAlgorithmInboundDescription.setForeground(Color.black);
        		}	
        	});
        GridBagConstraints gbc_jlSignatureAlgorithmInboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlSignatureAlgorithmInboundDescription.gridy = 10;
        gbc_jlSignatureAlgorithmInboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlSignatureAlgorithmInbound);
        createProfileComponentList.add(jcmbSignatureAlgorithmInbound);
        createProfileComponentList.add(jlSignatureAlgorithmInboundDescription);
        jpMessageInbound.add(jlSignatureAlgorithmInbound, gbc_jlSignatureAlgorithmInbound);
        jpMessageInbound.add(jcmbSignatureAlgorithmInbound, gbc_jcmbSignatureAlgorithmInbound);
        jpMessageInbound.add(jlSignatureAlgorithmInboundDescription, gbc_jlSignatureAlgorithmInboundDescription);
        
        // Inbound Encryption Parts field
        JLabel jlEncryptionPartsInbound = new JLabel("Message parts to encrypt");
        jlEncryptionPartsInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlEncryptionPartsInbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlEncryptionPartsInbound.gridy = 11;
        jtfEncryptionPartsInbound = new JTextField(40);
        jtfEncryptionPartsInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jtfEncryptionPartsInbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jtfEncryptionPartsInbound.gridy = 11;
        gbc_jtfEncryptionPartsInbound.gridwidth = 3;
        createProfileComponentList.add(jlEncryptionPartsInbound);
        createProfileComponentList.add(jtfEncryptionPartsInbound);
        jpMessageInbound.add(jlEncryptionPartsInbound, gbc_jlEncryptionPartsInbound);
        jpMessageInbound.add(jtfEncryptionPartsInbound, gbc_jtfEncryptionPartsInbound);
        
        // Inbound Encryption Key Identifier field
        JLabel jlEncryptionKeyIdentifierInbound = new JLabel("Encryption key identifier");
        jlEncryptionKeyIdentifierInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlEncryptionKeyIdentifierInbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlEncryptionKeyIdentifierInbound.gridy =12;
        jcmbEncryptionKeyIdentifierInbound = new JComboBox(WSSecurityMessageProperties.ENCRYPTION_KEY_IDENTIFIERS);
        jcmbEncryptionKeyIdentifierInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbEncryptionKeyIdentifierInbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbEncryptionKeyIdentifierInbound.gridy = 12;
        final JLabel jlEncryptionKeyIdentifierInboundDescription = new JLabel("<html>Specifies how the key to be used to decrypt<br>" +
		"the response message will be refered to.</html>");
        jlEncryptionKeyIdentifierInboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlEncryptionKeyIdentifierInboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			if (!jlEncryptionKeyIdentifierInboundDescription.isEnabled())
				jlEncryptionKeyIdentifierInboundDescription.setForeground(Color.gray);
			else 
				jlEncryptionKeyIdentifierInboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlEncryptionKeyIdentifierInboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlEncryptionKeyIdentifierInboundDescription.gridy = 12;
        gbc_jlEncryptionKeyIdentifierInboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlEncryptionKeyIdentifierInbound);
        createProfileComponentList.add(jcmbEncryptionKeyIdentifierInbound);
        createProfileComponentList.add(jlEncryptionKeyIdentifierInboundDescription);
        jpMessageInbound.add(jlEncryptionKeyIdentifierInbound, gbc_jlEncryptionKeyIdentifierInbound);
        jpMessageInbound.add(jcmbEncryptionKeyIdentifierInbound, gbc_jcmbEncryptionKeyIdentifierInbound);
        jpMessageInbound.add(jlEncryptionKeyIdentifierInboundDescription, gbc_jlEncryptionKeyIdentifierInboundDescription);
        
        // Inbound Encryption Algorithm field
        JLabel jlEncryptionAlgorithmInbound = new JLabel("Encryption alg.");
        jlEncryptionAlgorithmInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlEncryptionAlgorithmInbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlEncryptionAlgorithmInbound.gridy = 13;
        jcmbEncryptionAlgorithmInbound = new JComboBox(WSSecurityMessageProperties.ENCRYPTION_ALGORITHMS_SHORT);
        jcmbEncryptionAlgorithmInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbEncryptionAlgorithmInbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbEncryptionAlgorithmInbound.gridy =13;
        final JLabel jlEncrytpionAlgorithmInboundDescription = new JLabel("<html>Specifies algorithm to be used <br>" +
        		"for decrypting the message.</html>");
        jlEncrytpionAlgorithmInboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlEncrytpionAlgorithmInboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				if (!jlEncrytpionAlgorithmInboundDescription.isEnabled())
					jlEncrytpionAlgorithmInboundDescription.setForeground(Color.gray);
				else 
					jlEncrytpionAlgorithmInboundDescription.setForeground(Color.black);
			}	
        });
        GridBagConstraints gbc_jlEncrytpionAlgorithmInboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlEncrytpionAlgorithmInboundDescription.gridy = 13;
        gbc_jlEncrytpionAlgorithmInboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlEncryptionAlgorithmInbound);
        createProfileComponentList.add(jcmbEncryptionAlgorithmInbound);
        createProfileComponentList.add(jlEncrytpionAlgorithmInboundDescription);
        jpMessageInbound.add(jlEncryptionAlgorithmInbound, gbc_jlEncryptionAlgorithmInbound);
        jpMessageInbound.add(jcmbEncryptionAlgorithmInbound, gbc_jcmbEncryptionAlgorithmInbound);
        jpMessageInbound.add(jlEncrytpionAlgorithmInboundDescription, gbc_jlEncrytpionAlgorithmInboundDescription);
        
        // Inbound Encryption Key Transport Algorithm field
        JLabel jlEncryptionKeyTransportAlgorithmInbound = new JLabel("Encryption key transport alg.");
        jlEncryptionKeyTransportAlgorithmInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jlEncryptionKeyTransportAlgorithmInbound = (GridBagConstraints) gbcCol0.clone();
        gbc_jlEncryptionKeyTransportAlgorithmInbound.gridy = 14;
        jcmbEncryptionKeyTransportAlgorithmInbound = new JComboBox(WSSecurityMessageProperties.ENCRYPTION_KEY_TRANSPORT_ALGORITHMS_SHORT);
        jcmbEncryptionKeyTransportAlgorithmInbound.setFont(new Font(null, Font.PLAIN, 11));
        GridBagConstraints gbc_jcmbEncryptionKeyTransportAlgorithmInbound = (GridBagConstraints) gbcCol1.clone();
        gbc_jcmbEncryptionKeyTransportAlgorithmInbound.gridy =14;
        final JLabel jlEncryptionKeyTransportAlgorithmInboundDescription = new JLabel("<html>Specifies algorithm to be used to decrypt the <br>" +
		"symmetric key used for encryption of the message.</html>");
        jlEncryptionKeyTransportAlgorithmInboundDescription.setFont(new Font(null, Font.PLAIN, 11));
        jlEncryptionKeyTransportAlgorithmInboundDescription.addPropertyChangeListener(new PropertyChangeListener(){

        	public void propertyChange(PropertyChangeEvent evt) {
        		if (!jlEncryptionKeyTransportAlgorithmInboundDescription.isEnabled())
        			jlEncryptionKeyTransportAlgorithmInboundDescription.setForeground(Color.gray);
        		else 
        			jlEncryptionKeyTransportAlgorithmInboundDescription.setForeground(Color.black);
        	}	
        });
                GridBagConstraints gbc_jlEncryptionKeyTransportAlgorithmInboundDescription = (GridBagConstraints) gbcCol2.clone();
        gbc_jlEncryptionKeyTransportAlgorithmInboundDescription.gridy = 14;
        gbc_jlEncryptionKeyTransportAlgorithmInboundDescription.gridwidth = 2;
        createProfileComponentList.add(jlEncryptionKeyTransportAlgorithmInbound);
        createProfileComponentList.add(jcmbEncryptionKeyTransportAlgorithmInbound);
        createProfileComponentList.add(jlEncryptionKeyTransportAlgorithmInboundDescription);
        jpMessageInbound.add(jlEncryptionKeyTransportAlgorithmInbound, gbc_jlEncryptionKeyTransportAlgorithmInbound);
        jpMessageInbound.add(jcmbEncryptionKeyTransportAlgorithmInbound, gbc_jcmbEncryptionKeyTransportAlgorithmInbound);
        jpMessageInbound.add(jlEncryptionKeyTransportAlgorithmInboundDescription, gbc_jlEncryptionKeyTransportAlgorithmInboundDescription);
        
        // Add Inbound tab to the Message-level tabbed pane
        jtpMessage.addTab("RESPONSE properties",jpMessageInbound);
        
        // Add the Message-level tabbed pane to the list of components to be enabled/disabled
        createProfileComponentList.add(jtpMessage);
        // Add the Message-level tabbed pane to the Message-level tab
        jpMessage.add(jtpMessage, BorderLayout.CENTER);
        
        // Add the Message-level tab to the Create panel's tabbed pane
        jtpCreateProfile.addTab("Message-level", jpMessage);
        
        // Add the Create tabbed pane to the list of components to be enabled/disabled
        createProfileComponentList.add(jtpCreateProfile);
        
        // Add all to the Create panel
       jpCreateProfile.add(jchbCreateProfile, BorderLayout.NORTH);
       jpCreateProfile.add(jspCreateProfile, BorderLayout.CENTER);
        
        // Main panel to hold the Select and Create panels
        JPanel jpMain = new JPanel(new BorderLayout(0,10));
        jpMain.setBorder(new EmptyBorder(10,10,10,10));     
        
        jpMain.add(jpSelectProfile,BorderLayout.NORTH);
        jpMain.add(jpCreateProfile,BorderLayout.CENTER);
               
        // This will cause all fields on the Create panel to initially be disabled
        toggleComponents(createProfileComponentList, false);

        
        // Handle application close
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent evt)
            {
                closeDialog();
            }
        });
        
        
        // Put everything on one big panel
        JPanel jpBig = new JPanel(new BorderLayout());
        // OK/Cancel button panel
		JPanel jpButtons = new JPanel();
		jpButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okPressed();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancelPressed();
			}
		});
		
		jpButtons.add(cancelButton);
		jpButtons.add(okButton);
		jpBig.add(jpMain, BorderLayout.CENTER);
		jpBig.add(jpButtons,BorderLayout.SOUTH);
        
        getContentPane().add(jpBig);

        pack();
              
    }
    
	/**
     * Toggles the enabled/disabled state of all components in a vector
     * @param vect vector of components
     * @param toogle the state the components will be set to
     */
    private void toggleComponents(Vector<Component> vect, boolean toggle){
    	
    	for (int i=0 ; i< vect.size(); i++){
    		((Container)vect.elementAt(i)).setEnabled(toggle);
    	}
    }
	
	/**
     * Runnable to create and show the GUI.
     */
/*    private static class CreateAndShowGui
        implements Runnable
    {

        // Create and show the GUI.
       	public void run()
        {
            new WSSecurityProfileChooser();
        }
    }*/
    
    /**
     * Start the application.
     */
/*    public static void main(String[] args)
    {
        // Create and show GUI on the event handler thread
        SwingUtilities.invokeLater(new CreateAndShowGui());
    }
    */

	/**
	 * @return the wsSecurityProfile
	 */
	public WSSecurityProfile getWSSecurityProfile() {
		return wsSecurityProfile;
	}

	
	/**
	 * 'Delete' button pressed - lets user delete a profile he has saved.
	 */
	private void deletePressed(){

		int indexToDelete = jcmbSecurityProfiles.getSelectedIndex();
		
		// Is this user-defined profile? (If not we cannot delete it.)
		if (wsSecurityProfileManager.isUserDefinedProfile((String) jcmbSecurityProfiles.getSelectedItem())){
			try{
				
				// Ask for confirmation
				int iSelected = JOptionPane
						.showConfirmDialog(
								null,
								"Are you sure you want to delete the selected profile?",
								"WS Security Profile Chooser",
								JOptionPane.YES_NO_OPTION);

				if (iSelected != JOptionPane.YES_OPTION) {
					return;
				}
				
				// Delete profile from the file
				wsSecurityProfileManager.deleteProfile((String) jcmbSecurityProfiles.getSelectedItem());
				// Remove the profile from the lists
				wsSecurityProfiles.remove(indexToDelete);
				wsSecurityProfileNames.remove(indexToDelete);
				jcmbSecurityProfiles.setSelectedIndex(0);
				wsSecurityProfileDescriptions.remove(indexToDelete);
	        	JOptionPane.showMessageDialog(
	               		this, 
	               		"Profile deleted.",
	           			"WS Security Profile Chooser",
	           			JOptionPane.INFORMATION_MESSAGE);
			}
			catch (WSSecurityProfileManagerException wsspme){
	     		logger.error("WSSecurityProfileChooser: " + wsspme.getMessage());

            	JOptionPane.showMessageDialog(
               		this, 
               		wsspme.getMessage(),
           			"WS Security Profile Chooser",
           			JOptionPane.ERROR_MESSAGE);
			}
		}
		else{
        	JOptionPane.showMessageDialog(
               		this, 
               		"You cannot delete a system-defined profile, only the one that you created.",
           			"WS Security Profile Chooser",
           			JOptionPane.INFORMATION_MESSAGE);
		}
	 }
	
	/**
	 * 'OK' button pressed.
	 */
	private void okPressed(){

		if (jchbSelectProfile.isSelected()){
			// Set the selected profile
			wsSecurityProfile = new WSSecurityProfile();
			wsSecurityProfile = wsSecurityProfiles.elementAt(jcmbSecurityProfiles.getSelectedIndex());
			logger.info("The selected WS-Security profile: " + wsSecurityProfile.getWSSecurityProfileString());
		}
		else{
			// Create a new profile
			WSSecurityProfile wsSecProf = new WSSecurityProfile();
				
			// Pick up all defined fields from the form
			
			// Transport-level properties
			TransportProperties tlp = new TransportProperties();
			tlp.setProperty(TransportProperties.Protocol,(String) jcmbProtocol.getSelectedItem());
			if (jtfPort.getText() == null || jtfPort.getText().length() == 0){
				if (((String) jcmbProtocol.getSelectedItem()).contains("HTTPS")) 
					tlp.setProperty(TransportProperties.Port,"443");
				else 
					tlp.setProperty(TransportProperties.Port,"80");
			}
			else{
				
				try{// if the entered value is number
					Integer.parseInt((String)jtfPort.getText());
					tlp.setProperty(TransportProperties.Port,(String) jtfPort.getText());
				}
				catch(NumberFormatException nfe){ // if not number
					// Port must be a number
		            JOptionPane.showMessageDialog(this,
		            		"Port value must be a number",
		            		"WS Security Profile Chooser", 
		            		JOptionPane.ERROR_MESSAGE);
		            return;
				}
			}
			tlp.setProperty(TransportProperties.AuthNType,(String) jcmbAuthNType.getSelectedItem());
			if (jcmbRequiresClientCert.isEnabled()){
					tlp.setProperty(TransportProperties.RequiresClientCert,(String) jcmbRequiresClientCert.getSelectedItem());
			}
			if (jcmbProxyCertDepth.isEnabled()){
				tlp.setProperty(TransportProperties.ProxyCertDepth,(String) jcmbProxyCertDepth.getSelectedItem());
			}
			wsSecProf.setTransportProperties(tlp);
			
			// Outbound message-level properties
			WSSecurityMessageProperties omlp = new WSSecurityMessageProperties();
			if (jtfWSSecPolicyURL.getText()!= null && jtfWSSecPolicyURL.getText().length()!=0)
				wsSecProf.setWSSecurityPolicyRef(jtfWSSecPolicyURL.getText());
			String outboundActions = new String();
			for (int i=0; i<8; i++){
				if (!jcmbWSSecActionsOutbound[i].getSelectedItem().equals("None")){
					outboundActions = outboundActions + (String) jcmbWSSecActionsOutbound[i].getSelectedItem() + " ";
				}		
			}
			if (outboundActions.length() != 0) {
				omlp.setProperty(WSSecurityMessageProperties.Actions, outboundActions.trim());
			}

			if (outboundActions.contains("UsernameToken")){
				omlp.setProperty(WSSecurityMessageProperties.PasswordType, (String)jcmbPasswordTypeOutbound.getSelectedItem());
			} // otherwise ignore password-related fields

			if (outboundActions.contains("Signature")){
				if (jtfSignaturePartsOutbound.getText()!= null && jtfSignaturePartsOutbound.getText().length()!=0)
					omlp.setProperty(WSSecurityMessageProperties.SignatureParts,jtfSignaturePartsOutbound.getText());
				omlp.setProperty(WSSecurityMessageProperties.SignatureKeyIdentifier, (String)jcmbSignatureKeyIdentifierOutbound.getSelectedItem());
				omlp.setProperty(WSSecurityMessageProperties.SignatureAlgorithm, WSSecurityMessageProperties.SIGNATURE_ALGORITHMS[jcmbSignatureAlgorithmOutbound.getSelectedIndex()]);
			}// otherwise ignore signature-related fields

			if (outboundActions.contains("Encrypt")){
				if (jtfEncryptionPartsOutbound.getText()!= null && jtfEncryptionPartsOutbound.getText().length()!=0)
					omlp.setProperty(WSSecurityMessageProperties.EncryptionParts,jtfEncryptionPartsOutbound.getText());
				omlp.setProperty(WSSecurityMessageProperties.EncryptionKeyIdentifier, (String)jcmbEncryptionKeyIdentifierOutbound.getSelectedItem());
				omlp.setProperty(WSSecurityMessageProperties.EncryptionAlgorithm, WSSecurityMessageProperties.ENCRYPTION_ALGORITHMS[jcmbEncryptionAlgorithmOutbound.getSelectedIndex()]);
				omlp.setProperty(WSSecurityMessageProperties.EncryptionKeyTransportAlgorithm, WSSecurityMessageProperties.ENCRYPTION_KEY_TRANSPORT_ALGORITHMS[jcmbEncryptionKeyTransportAlgorithmOutbound.getSelectedIndex()]);
			}//otherwise ignore encryption-related fields

			wsSecProf.setOutboundMessageProperties(omlp);
			
			// Inbound message-level properties
			WSSecurityMessageProperties imlp = new WSSecurityMessageProperties();
			
			String inboundActions = new String();
			for (int i=0; i<8; i++){
				if (!jcmbWSSecActionsInbound[i].getSelectedItem().equals("None")){
					inboundActions = inboundActions + (String) jcmbWSSecActionsInbound[i].getSelectedItem() + " ";
				}		
			}
			if (inboundActions.length() != 0) {
				imlp.setProperty(WSSecurityMessageProperties.Actions, inboundActions.trim());
			}

			if (inboundActions.contains("Signature")){
				if (jtfSignaturePartsInbound.getText()!= null && jtfSignaturePartsInbound.getText().length()!=0)
					imlp.setProperty(WSSecurityMessageProperties.SignatureParts,jtfSignaturePartsInbound.getText());
				imlp.setProperty(WSSecurityMessageProperties.SignatureKeyIdentifier, (String)jcmbSignatureKeyIdentifierInbound.getSelectedItem());
				imlp.setProperty(WSSecurityMessageProperties.SignatureAlgorithm, WSSecurityMessageProperties.SIGNATURE_ALGORITHMS[jcmbSignatureAlgorithmInbound.getSelectedIndex()]);
			}// otherwise ignore signature-related fields

			if (inboundActions.contains("Encrypt")){
				if (jtfEncryptionPartsInbound.getText()!= null && jtfEncryptionPartsInbound.getText().length()!=0)
					imlp.setProperty(WSSecurityMessageProperties.EncryptionParts,jtfEncryptionPartsInbound.getText());
				imlp.setProperty(WSSecurityMessageProperties.EncryptionKeyIdentifier, (String)jcmbEncryptionKeyIdentifierInbound.getSelectedItem());
				imlp.setProperty(WSSecurityMessageProperties.EncryptionAlgorithm, WSSecurityMessageProperties.ENCRYPTION_ALGORITHMS[jcmbEncryptionAlgorithmInbound.getSelectedIndex()]);
				imlp.setProperty(WSSecurityMessageProperties.EncryptionKeyTransportAlgorithm, WSSecurityMessageProperties.ENCRYPTION_KEY_TRANSPORT_ALGORITHMS[jcmbEncryptionKeyTransportAlgorithmInbound.getSelectedIndex()]);
			}//otherwise ignore encryption-related fields
			wsSecProf.setInboundMessageProperties(imlp);

			// Create the profile configuration string from the defined parameters as well
			String profile = "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "+
			"xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"> \n"+
			"<globalConfiguration> \n" +
			"<requestFlow>\n" +
			"<handler type=\"java:net.sf.taverna.security.T2WSDoAllSender\"> \n";
			try{
				if (omlp.getProperty(WSSecurityMessageProperties.Actions)!= null) {
					profile = profile + "<parameter name=\"action\" value=\""+omlp.getProperty(WSSecurityMessageProperties.Actions)+"\"/> \n";
					HashMap<String,String> parameters = omlp.getProperties();
					Set<String> set = parameters.keySet();
					for (Iterator<String> itr=set.iterator(); itr.hasNext(); ){
						String key = (String) itr.next();
						if (!key.equals(WSSecurityMessageProperties.Actions)){
							String value = omlp.getProperty(key);
							if (value!=null)
								profile = profile + "<parameter name=\""+ key +"\" value=\""+ value +"\"/> \n";
						}
					}
				}
			}
			catch(NoSuchSecurityPropertyException nspe){
				// should not happen as we are only accessing the properties that do exist
			}
			profile = profile + "</handler> \n"+
			"</requestFlow> \n"+
			"<responseFlow>\n" +
			"<handler type=\"java:net.sf.taverna.security.T2WSDoAllSender\"> \n";
			try{
				if (imlp.getProperty(WSSecurityMessageProperties.Actions)!= null) {
					profile = profile + "<parameter name=\"action\" value=\""+imlp.getProperty(WSSecurityMessageProperties.Actions)+"\"/> \n";
					HashMap<String,String> parameters = imlp.getProperties();
					Set<String> set = parameters.keySet();
					for (Iterator<String> itr=set.iterator(); itr.hasNext(); ){
						String key = (String) itr.next();
						if (!key.equals(WSSecurityMessageProperties.Actions)){
							String value = imlp.getProperty(key);
							if (value!=null)
								profile = profile + "<parameter name=\""+ key +"\" value=\""+ value +"\"/> \n";
						}
					}
				}
			}
			catch(NoSuchSecurityPropertyException nspe){
				// should not happen as we are only accessing the properties that do exist
			}
			profile = profile + "</handler> \n"+"</handler> \n"+
			"</responseFlow> \n"+
			"</globalConfiguration> \n"+
			// TODO: Transport level handler here will depend on the transport level protocol (i.e. whether it is HTTPS or not)
			"<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.HTTPSender\"/> \n"+
			"</deployment>\n";
			
			// Since all is correct - set the newly created profile
			wsSecurityProfile = new WSSecurityProfile();
			wsSecurityProfile = wsSecProf;
			// Set the profile string
			wsSecurityProfile.setWSSecurityProfileString(profile);
			// Ask the user if he also wants to save the newly created profile to the file with user-defined profiles
	        int iSelected = JOptionPane.showConfirmDialog(this, 
	        		"Do you also want to save the created profile so you can refer to it later?",
	        		"WS Security Profile Chooser",
	        		JOptionPane.YES_NO_OPTION);
	                
	    	if (iSelected == JOptionPane.YES_OPTION) {
	    		// Give name and description to the profile
	    		GetProfileNameDescriptionDialog dGetNameDesc = new GetProfileNameDescriptionDialog(this,
	        			"WS Security Profile Chooser", 
	        			true);         
	    		dGetNameDesc.setLocationRelativeTo(this);
	    		dGetNameDesc.setVisible(true);
	            String name = dGetNameDesc.getName();
	            String desc = dGetNameDesc.getDescritpion();
	            
	            if (!(name == null)) { //user did not cancel
	            	wsSecurityProfile.setWSSecurityProfileName(name);
	            	wsSecurityProfile.setWSSecurityProfileDescription(desc);
	            	try{
	            		wsSecurityProfileManager.saveProfile(wsSecurityProfile);
	            	}
	            	catch (WSSecurityProfileManagerException wsspme){
	            		logger.error("WSSecurityProfileChooser: " + wsspme.getMessage());

	                	JOptionPane.showMessageDialog(
	                   		this, 
	                   		wsspme.getMessage(),
	               			"WS Security Profile Chooser",
	               			JOptionPane.ERROR_MESSAGE);
	            	}
	            }
	    	}   

			logger.info("The created WS-Security profile:" + profile);
		}

		closeDialog();			
	 }
	
	
	/**
	 * 'Cancel' button pressed.
	 */
	private void cancelPressed(){

		wsSecurityProfile = null;
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
