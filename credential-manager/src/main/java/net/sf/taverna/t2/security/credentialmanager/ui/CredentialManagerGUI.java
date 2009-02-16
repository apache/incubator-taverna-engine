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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CMNotInitialisedException;
import net.sf.taverna.t2.security.credentialmanager.CMX509Util;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;

import org.apache.log4j.Logger;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

/**
 * Provides a GUI to the Credential Manager. It enables users to access the
 * functionality of the Credential manager, i.e. to manage their credentials -
 * passwords, key pairs and trusted certificates of CA's and services.
 * Credentials are stored in two Bouncy Castle "UBER"-type keystores called the
 * Keystore (containing passwords and key pairs) and the Truststore (containing
 * trusted certificates).
 * 
 * @author Alexandra Nenadic
 */

public class CredentialManagerGUI extends JFrame {

	private static final long serialVersionUID = 6190812355459557296L;

	/** Logger */
	private static Logger logger = Logger.getLogger(CredentialManagerGUI.class);

	/** Default tabbed pane width - dictates width of this frame */
	private static final int DEFAULT_FRAME_WIDTH = 600;

	/** Default tabbed pane height - dictates height of this frame */
	private static final int DEFAULT_FRAME_HEIGHT = 400;

	/** Credential Manager icon (when frame minimised) */
	private final Image credManagerIconImage = Toolkit.getDefaultToolkit()
			.createImage(CredentialManagerGUI.class.getResource("/images/cred_manager.png"));

	/**
	 * Credential Manager - manages all operations on the Keystore and
	 * Truststore.
	 */
	public static CredentialManager credManager;

	/**	Is the GUI initialised? */
	private boolean initialised = false;
	
	/** Minimum required BC version */
	/* private static final Double REQ_BC_VERSION = new Double(1.38);*/
	
	// //////////////////////////////////////////////////////////
	// Keystore tab and table controls
	// //////////////////////////////////////////////////////////
	/** Tabbed pane to hold keystore entries tables */
	private JTabbedPane keyStoreTabbedPane;

	/** Tab 1: holds passwords table */
	private JPanel passwordsTab;

	/** Tab 1: name */
	public static final String PASSWORDS_TAB_NAME = "Passwords";

	/** Tab 2: holds key pairs (user certificates) table */
	private JPanel keyPairsTab;

	/** Tab 2: name */
	public static final String KEYPAIRS_TAB_NAME = "Key Pairs";

	/** Tab 3: holds trusted certificates table */
	private JPanel trustedCertificatesTab;

	/** Tab 3: name */
	public static final String TRUSTED_CERTIFICATES_TAB_NAME = "Trusted Certificates";

	/** Password entries table */
	private JTable passwordsTable;

	/** Key Pair entries table */
	private JTable keyPairsTable;

	/** Trusted Certificate entries table */
	private JTable trustCertsTable;

	/** Value to place in the Type column for a password entry */
	public static final String PASSWORD_ENTRY_TYPE = "Password";

	/** Value to place in the Type column for a key pair entry */
	public static final String KEY_PAIR_ENTRY_TYPE = "Key Pair";

	/** Value to place in the Type column for a trusted certificate entry */
	public static final String TRUST_CERT_ENTRY_TYPE = "Trusted Certificate";

	/** Buttons for Passwords tab */
	private JButton newPasswordButton;
	private JButton viewPasswordButton;
	private JButton editPasswordButton;
	private JButton deletePasswordButton;

	/** Buttons for Key Pairs tab */
	private JButton viewKeyPairButton;
	private JButton importKeyPairButton;
	private JButton exportKeyPairButton;
	private JButton editServiceURLKeyPairButton;
	private JButton deleteKeyPairButton;

	/** Buttons for Trusted Certificates tab */
	private JButton viewTrustedCertificateButton;
	private JButton importTrustedCertificateButton;
	private JButton exportTrustedCertificateButton;
	private JButton deleteTrustedCertificateButton;

	// ///////////////////////////////////////////////////////////
	// Actions for buttons
	// ///////////////////////////////////////////////////////////

	/** New password entry action */
	private final NewPasswordAction newPasswordAction = new NewPasswordAction();

	/** View password entry action */
	private final ViewPasswordAction viewPasswordAction = new ViewPasswordAction();

	/** Edit password entry action */
	private final EditPasswordAction editPasswordAction = new EditPasswordAction();

	/** Delete password entry action */
	private final DeletePasswordAction deletePasswordAction = new DeletePasswordAction();

	/** View (public key or trusted) certificate action */
	private final ViewCertAction viewCertAction = new ViewCertAction();

	/** Import key pair entry action */
	private final ImportKeyPairAction importKeyPairAction = new ImportKeyPairAction();

	/** Export key pair entry action */
	private final ExportKeyPairAction exportKeyPairAction = new ExportKeyPairAction();

	/** Delete key pair entry action */
	private final DeleteKeyPairAction deleteKeyPairAction = new DeleteKeyPairAction();

	/** Edit Service URL for a key pair */
	private final EditServiceURLKeyPairAction editServiceURLKeyPairAction = new EditServiceURLKeyPairAction();

	/** Import trusted certificate entry action */
	private final ImportTrustCertAction importTrustCertAction = new ImportTrustCertAction();

	/** Export trusted certificate entry action */
	private final ExportTrustCertAction exportTrustCertAction = new ExportTrustCertAction();

	/** Delete trusted certificate entry action */
	private final DeleteTrustCertAction deleteTrustCertAction = new DeleteTrustCertAction();

	/**
	 * Creates a new Credential Manager GUI's frame.
	 */
	public CredentialManagerGUI() {

		/***********************************************
		 * Instantiate and initialise Credential Manager
		 * that will perform all operations on the 
		 * Keystore and Truststore
		 ***********************************************/
		try {
			credManager = CredentialManager.getInstance();
		} 
		catch (CMException cme) {

			// Failed to instantiate Credential Manager - warn the user and exit
			String sMessage = "Credential Manager GUI: Failed to instantiate Credential Manager. " + cme.getMessage();
			logger.error(sMessage);
			JOptionPane.showMessageDialog(new JFrame(), sMessage,
					"Credential Manager Error", JOptionPane.ERROR_MESSAGE);
		}

		if (credManager != null){

			// Ask user for the master password
			String mPassword = null;
			
			// If the Keystore file exists - then the master password has already been set
			if (credManager.exists(CredentialManager.KEYSTORE)) { 			
				// Get the user to enter the master password
				GetPasswordDialog dGetMasterPassword = new GetPasswordDialog(this,
						"Credential Manager", true, "Enter the master password");
				dGetMasterPassword.setLocationRelativeTo(this);
				dGetMasterPassword.setVisible(true);

				mPassword = dGetMasterPassword.getPassword();

				/*if (mPassword == null) { // user cancelled
					// Exit - CM will not be initialised
				}*/
			} 
			else { // Otherwise, the Keystore file does not exist yet - an empty one will be created
				
				// Get the user to set the new master password
				GetNewPasswordDialog dGetNewMasterPassword = new GetNewPasswordDialog(
						this, "Credential Manager", true, "Set the master password");
				dGetNewMasterPassword.setLocationRelativeTo(this);
				dGetNewMasterPassword.setVisible(true);

				mPassword = dGetNewMasterPassword.getPassword();

				/*if (mPassword == null) { // user cancelled
					// Exit - CM will not be initialised
				}*/
			}
			
			boolean masterPasswordOK = false; // indicator if master password is correct
			if (mPassword != null){ // if user did not cancel
				
				if (credManager.isInitialised()){ 
					// If credential Manager is already initialised in previous accesses to Credental Manager GUI
					// then just compare the passwords
					if (!credManager.compareMasterPassword(mPassword)){
						JOptionPane.showMessageDialog(this, "Incorrect master password", "Credential Manager",
								JOptionPane.ERROR_MESSAGE);
						// Exit
						masterPasswordOK = false;
					}
					else{
						masterPasswordOK = true;
					}
				}
				else{
					try {
						// Initialise the Credential Manager with the master password
						credManager.init(mPassword);
					} 
					catch (CMException cme) {
						// Failed to initialise Credential Manager - warn the user and exit
						String sMessage = "Credential Manager GUI: Failed to instantiate Credential Manager. " + cme.getMessage();
						logger.error(sMessage);
						JOptionPane.showMessageDialog(this, sMessage, "Credential Manager",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				
				/***************************
				 * Initialise GUI components
				 ***************************/
				if (credManager.isInitialised() && masterPasswordOK){

					// Initialise the tabbed pane that contains the tabs with tabular
					// representations of the Keystore's content.
					keyStoreTabbedPane = new JTabbedPane();
					// Initialise the tab containing the table for password entries from the
					// Keystore
					passwordsTable = initTable(PASSWORDS_TAB_NAME, passwordsTab);
					// Initialise the tab containing the table for key pair entries from the
					// Keystore
					keyPairsTable = initTable(KEYPAIRS_TAB_NAME, keyPairsTab);
					// Initialise the tab containing the table for trusted certificate
					// entries from the Truststore
					trustCertsTable = initTable(TRUSTED_CERTIFICATES_TAB_NAME,
							trustedCertificatesTab);
					// Set the size of the tabbed pane to the preferred size - the size of
					// the main app frame depends on it.
					keyStoreTabbedPane.setPreferredSize(new Dimension(DEFAULT_FRAME_WIDTH,
							DEFAULT_FRAME_HEIGHT));
					
					try{
						// Update the GUI's tables with data loaded from the
						// Keystore/Truststore (if any)
						updateTables();
					}
					catch(CMException cme){
						// Failed to load contents of the Keystore/Truststore - warn the user and exit
						String sMessage = cme.getMessage();
						JOptionPane.showMessageDialog(this, sMessage, "Credential Manager",
								JOptionPane.ERROR_MESSAGE);
					}
					
					// Add tabbed pane to the main app frame
					getContentPane().add(keyStoreTabbedPane, BorderLayout.CENTER);

					// Handle application close
					addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent evt) {
							closeFrame();
						}
					});
					setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

					pack();

					// Centre the frame in the centre of the desktop
					setLocationRelativeTo(null);

					// Set the frame's icon
					setIconImage(credManagerIconImage);

					// Set the frame's title
					setTitle("Credential Manager");

					//setVisible(true);
					
					initialised = true;
				}
			}
		}
	}
	
	
	/**
	 * Is the Credential Manager GUI initialised properly?
	 */
	public boolean isInitialised(){
		return initialised;
	}
	
	
	/**
	 * Initialise the tab on the tabed pane and its tabular content
	 * 
	 * @param tableType
	 *            Type of the table to be displayed on the tab (i.e. passwords,
	 *            key pairs or trusted certificates)
	 * @param tab
	 *            Tab to hold the table with data from the Keystore
	 */
	private JTable initTable(String tableType, JPanel tab) {
		// Create a tab
		tab = new JPanel(new BorderLayout(10, 10));

		JTable table = null;

		if (tableType.equals(PASSWORDS_TAB_NAME)) { // Passwords tab
			// The Passwords table's data model
			PasswordsTableModel passwordsTableModel = new PasswordsTableModel();
			// The table itself
			table = new JTable(passwordsTableModel);

			// Set the Password and Alias columns of the Passwords table to be
			// invisible by removing them from the column model (they will still present
			// in the table model)
			// Remove the last column first
			TableColumn aliasColumn = table.getColumnModel().getColumn(5);
			table.getColumnModel().removeColumn(aliasColumn);
			TableColumn passwordColumn = table.getColumnModel().getColumn(4);
			table.getColumnModel().removeColumn(passwordColumn);

			// Buttons
			newPasswordButton = new JButton(newPasswordAction);
			viewPasswordButton = new JButton(viewPasswordAction);
			editPasswordButton = new JButton(editPasswordAction);
			deletePasswordButton = new JButton(deletePasswordAction);

			// Panel to hold the buttons
			JPanel bp = new JPanel();
			bp.add(newPasswordButton);
			bp.add(viewPasswordButton);
			bp.add(editPasswordButton);
			bp.add(deletePasswordButton);

			// Add button panel to the tab
			tab.add(bp, BorderLayout.PAGE_END);

		} 
		else if (tableType.equals(KEYPAIRS_TAB_NAME)) { // Key Pairs tab
			// The Key Pairs table's data model
			KeyPairsTableModel keyPairsTableModel = new KeyPairsTableModel();
			// The table itself
			table = new JTable(keyPairsTableModel);

			// Set the URLs and Alias columns of the Key Pairs table to be
			// invisible by removing them from the column model (they will still present
			// in the table model)
			// Remove the last column first
			TableColumn aliasColumn = table.getColumnModel().getColumn(4); 
			table.getColumnModel().removeColumn(aliasColumn);
			TableColumn urlsColumn = table.getColumnModel().getColumn(3);
			table.getColumnModel().removeColumn(urlsColumn);

			// Buttons
			viewKeyPairButton = new JButton(viewCertAction);
			importKeyPairButton = new JButton(importKeyPairAction);
			exportKeyPairButton = new JButton(exportKeyPairAction);
			deleteKeyPairButton = new JButton(deleteKeyPairAction);
			editServiceURLKeyPairButton = new JButton(
					editServiceURLKeyPairAction);

			// Panel to hold the buttons
			JPanel bp = new JPanel();
			bp.add(viewKeyPairButton);
			bp.add(importKeyPairButton);
			bp.add(exportKeyPairButton);
			bp.add(editServiceURLKeyPairButton);
			bp.add(deleteKeyPairButton);

			// Add button panel to the tab
			tab.add(bp, BorderLayout.PAGE_END);

		} 
		else if (tableType.equals(TRUSTED_CERTIFICATES_TAB_NAME)) { // Trusted Certificates tab
			// The Trusted Certificate table's data model
			TrustCertsTableModel trustedCertificatesTableModel = new TrustCertsTableModel();
			// The table itself
			table = new JTable(trustedCertificatesTableModel);

			// Set the Alias column of the Trusted Certs table to be invisible
			// by removing it from the column model (it is still present in the
			// table model)
			TableColumn aliasColumn = table.getColumnModel().getColumn(3);
			table.getColumnModel().removeColumn(aliasColumn);

			// Buttons
			viewTrustedCertificateButton = new JButton(viewCertAction);
			importTrustedCertificateButton = new JButton(importTrustCertAction);
			exportTrustedCertificateButton = new JButton(exportTrustCertAction);
			deleteTrustedCertificateButton = new JButton(deleteTrustCertAction);

			// Panel to hold the buttons
			JPanel bp = new JPanel();
			bp.add(viewTrustedCertificateButton);
			bp.add(importTrustedCertificateButton);
			bp.add(exportTrustedCertificateButton);
			bp.add(deleteTrustedCertificateButton);

			// Add button panel to the tab
			tab.add(bp, BorderLayout.PAGE_END);
		}

		table.setShowGrid(false);
		table.setRowMargin(0);
		table.getColumnModel().setColumnMargin(0);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		// Top accomodates entry icons with 2 pixels spare space (images are 16x16 pixels)
		table.setRowHeight(18);

		// Add custom renderers for the table headers and cells
		for (int iCnt = 0; iCnt < table.getColumnCount(); iCnt++) {
			TableColumn column = table.getColumnModel().getColumn(iCnt);
			column.setHeaderRenderer(new TableHeaderRenderer());
			column.setCellRenderer(new TableCellRenderer());
		}

		// Make the first column small and not resizable (it holds icons to
		// represent different entry types)
		TableColumn typeCol = table.getColumnModel().getColumn(0);
		typeCol.setResizable(false);
		typeCol.setMinWidth(20);
		typeCol.setMaxWidth(20);
		typeCol.setPreferredWidth(20);

		// Set the size for the second column
		// (i.e. Service URL column of Passwords and Key Pairs tables, and
		// Certificate Name column of the Trusted Certificates table)
		TableColumn secondCol = table.getColumnModel().getColumn(1);
		secondCol.setMinWidth(20);
		secondCol.setMaxWidth(10000);
		secondCol.setPreferredWidth(300);

		// Don't care about the size of other columns

		// Put the table into a scroll pane
		JScrollPane jspTableScrollPane = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jspTableScrollPane.getViewport().setBackground(table.getBackground());

		// Put the scroll pane on the tab panel
		tab.add(jspTableScrollPane, BorderLayout.CENTER);
		jspTableScrollPane.setBorder(new EmptyBorder(3, 3, 3, 3));

		// Add mouse listeners to show an entry's details if it is
		// double-clicked
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				tableDoubleClick(evt);
			}
		});

		// Add the tab to the tabbed pane
		keyStoreTabbedPane.addTab(tableType, tab);

		return table;
	}

	
	/**
	 * Handle double click on the Keystore tables. If it has osccured, show the
	 * details of the entry clicked upon.
	 * 
	 * @param evt
	 *            The mouse event
	 */
	private void tableDoubleClick(MouseEvent evt) {
		if (evt.getClickCount() > 1) { // is it double click?

			// What row and column were clicked upon (if any)?
			Point point = new Point(evt.getX(), evt.getY());
			int iRow = ((JTable) evt.getSource()).rowAtPoint(point);
			if (iRow == -1) {
				return;
			}
			// Which table the click occured on?
			if (((JTable) evt.getSource()).getModel() instanceof PasswordsTableModel) { // Passwords table
				viewPasswordEntry();
			} 
			else if (((JTable) evt.getSource()).getModel() instanceof KeyPairsTableModel) { // Key pairs table
				viewCert();
			} 
			else { // Trusted certificates table
				viewCert();
			}
		}
	}

	
	/**
	 * Lets the user examine a password entry from the Keystore.
	 * 
	 * @return true - if the password details were viewed sucessfully, false
	 *         otherwise
	 */
	private boolean viewPasswordEntry() {
		// Which password entry has been selected, if any?
		int iRow = passwordsTable.getSelectedRow();
		if (iRow == -1) { // no row currently selected
			return false;
		}

		// Get current values for service URL, username and password
		String sURL = (String) passwordsTable.getValueAt(iRow, 1); // current entry's service url

		String sUsername = (String) passwordsTable.getValueAt(iRow, 2); // current entry's username

		// Because the Password and the Alias columns are not visible we call
		// the getValueAt method on the table model rather than at the JTable
		String sPassword = (String) passwordsTable.getModel().getValueAt(iRow,
				4); // current entry's password value
		/* String sAlias = (String) passwordsTable.getModel().getValueAt(iRow,5); //current entry's alias*/

		// Let the user view service URL, username and password
		ViewPasswordEntryDialog viewServicePassDialog = new ViewPasswordEntryDialog(
				this, "View password entry", true, sURL, sUsername, sPassword);

		viewServicePassDialog.setLocationRelativeTo(this);
		viewServicePassDialog.setVisible(true);

		return true;
	}

	
	/**
	 * Lets the user insert a new password entry in the Keystore.
	 * 
	 * @return true - if the creation was successful, false otherwise
	 */
	private boolean newPasswordEntry() {
		String sURL = null; // service URL
		String sUsername = null; // username
		String sPassword = null; // password

		// Loop until the user cancels or enters everything correctly
		while (true) { 
			
			// Let the user insert a new password entry (by specifying service
			// URL, username and password)
			NewEditPasswordEntryDialog newPasswordDialog = new NewEditPasswordEntryDialog(
					this, "New password entry", true, sURL, sUsername,
					sPassword);
			newPasswordDialog.setLocationRelativeTo(this);
			newPasswordDialog.setVisible(true);

			sURL = newPasswordDialog.getServiceURL(); // get service URL
			sUsername = newPasswordDialog.getUsername(); // get username
			sPassword = newPasswordDialog.getPassword(); // get password

			if (sPassword == null) { // user cancelled - any of the above
				// three fields is null 
				
				// do nothing
				return false;
			}

			try {
				String sAlias = "password#" + sURL; // construct the Keystore
				// alias for the new entry

				// Check if a password entry with the entered URL (i.e. alias)
				// already exists in the Keystore
				// We ask this here as the user may wish to overwrite the
				// existing password entry
				// Checking for key pair entries' URLs is done in the
				// NewEditPasswordEntry dialog
				
				// Get list of URLs for all the password entries in the Keystore
				Vector<String> vURLs = getURLsForPasswords(); 
				if (vURLs.contains(sURL)) { // if such a URL already exists 
					// Aks if the user wants to overwrite it
					int iSelected = JOptionPane.showConfirmDialog(this,
							"The Keystore already contains a password entry with the same service URL.\n"
									+ "Do you want to overwrite it?",
							"Credential Manager Alert",
							JOptionPane.YES_NO_OPTION);

					if (iSelected == JOptionPane.YES_OPTION) {
						if (deletePasswordEntry(sAlias)
								&& savePasswordEntry(sAlias, sPassword,
										sUsername)) {
							
							// Update the frame's tables
							updateTables();
							return true;
						} 
						else {
							String exMessage = "Failed to insert a new password entry in the Keystore";
							JOptionPane.showMessageDialog(this, exMessage,
									"Credential Manager Error",
									JOptionPane.ERROR_MESSAGE);
							return false;
						}
					}
					// Otherwise show the same window with the entered service
					// URL, username and password values
				} 
				else {
					// Add the new password entry in the Keystore
					if (savePasswordEntry(sAlias, sPassword, sUsername)) {

						// Update the frame's tables
						updateTables();
						return true;
					} 
					else {
						String exMessage = "Failed to insert a new password entry in the Keystore.";
						JOptionPane.showMessageDialog(this, exMessage,
								"Credential Manager Error",
								JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
			} 
			catch (CMException cme) {
				// An error occured while accessing the Keystore	
				String exMessage = "An error occured while reading the Keystore after inserting a new password entry.";
				logger.error("Credential Manager GUI: " + exMessage);
				JOptionPane
						.showMessageDialog(
								this,
								exMessage,
								"Credential Manager Error",
								JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
	}
	

	/**
	 * Lets the user edit a password entry from the Keystore.
	 * 
	 * @return true - if the update is successful, false otherwise
	 */
	private boolean editPasswordEntry() {

		// Which password entry has been selected?
		int iRow = passwordsTable.getSelectedRow();
		if (iRow == -1) { // no row currently selected
			return false;
		}

		// Get current values for service URL, username and password
		String sURL = (String) passwordsTable.getValueAt(iRow, 1); // current entry's service url

		String sUsername = (String) passwordsTable.getValueAt(iRow, 2); // current entry's username

		// Because the password and the alias columns are not visible we call
		// the getValueAt method on the table model rather than at the JTable
		String sPassword = (String) passwordsTable.getModel().getValueAt(iRow, 4); // current entry's password value
		String sAlias = (String) passwordsTable.getModel().getValueAt(iRow, 5); // current entry's keystore alias

		while (true) { // loop until user cancels or enters everyting correctly
			// Let the user edit service URL, username or password of a password entry
			NewEditPasswordEntryDialog editPasswordDialog = new NewEditPasswordEntryDialog(
					this, "Edit password entry", true, sURL, sUsername,
					sPassword);

			editPasswordDialog.setLocationRelativeTo(this);
			editPasswordDialog.setVisible(true);

			// New values
			String nURL = editPasswordDialog.getServiceURL(); // get new service URL
			String nUsername = editPasswordDialog.getUsername(); // get new username
			String nPassword = editPasswordDialog.getPassword(); // get new password

			if (nPassword == null) { // user cancelled - any of the above three fields is null
				// do nothing
				return false;
			}

			// Is anything actally modified?
			boolean isModified = (!sURL.equals(nURL)
					|| !sUsername.equals(nUsername) || !sPassword
					.equals(nPassword));

			if (isModified) {
				String nAlias = "password#" + nURL; // construct new alias

				try {
					// Check if a different password entry with the new URL
					// (i.e. alias) already exists in the Keystore
					// We ask this here as the user may wish to overwrite that
					// other password entry
					// Checking for key pair entries' URLs is done in the
					// NewEditPasswordEntry dialog
					
					// Get list of URLs for all passwords in the Keystore
					Vector<String> vURLs = getURLsForPasswords(); 
					
					// If such a URL exists and is not the currently selected one
					if (vURLs.contains(nURL) && (vURLs.indexOf(nURL) != iRow)) { 

						int iSelected = JOptionPane.showConfirmDialog(this,
								"The Keystore already contains a password entry with the same service URL.\n"
										+ "Do you want to overwrite it?",
								"Credential Manager Alert",
								JOptionPane.YES_NO_OPTION);

						if (iSelected == JOptionPane.YES_OPTION) {

							// Remove that other entry entry and save the new
							// one in its place
							// Also remove the current one that we are editing -
							// as it is replacing the other entry
							if (deletePasswordEntry(sAlias)
									&& deletePasswordEntry(nAlias)
									&& savePasswordEntry(nAlias, nPassword,
											nUsername)) {

								// Update the frame's tables
								updateTables();
								return true;
							} else {
								String exMessage = "Failed to update the password entry in the Keystore.";
								JOptionPane.showMessageDialog(this, exMessage,
										"Credential Manager Error",
										JOptionPane.ERROR_MESSAGE);
								return false;
							}
						}

						// Otherwise show the same window with the entered
						// service URL, username and password values
					} 
					else {
						// Remove the current entry and save the new one in its place
						if (deletePasswordEntry(sAlias)
								&& savePasswordEntry(nAlias, nPassword,
										nUsername)) {

							// Update the frame's tables
							updateTables();
							return true;
						} else {
							String exMessage = "Failed to update the password entry in the Keystore.";
							JOptionPane.showMessageDialog(this, exMessage,
									"Credential Manager Error",
									JOptionPane.ERROR_MESSAGE);
							return false;
						}
					}
				} catch (CMException cme) {
					// An error occured while accessing the Keystore
					String exMessage = "An error occured while reading the Keystore after updating a password entry.";
					logger.error("Credential Manager GUI: " + exMessage);
					JOptionPane
							.showMessageDialog(
									this,
									exMessage,
									"Credential Manager Error",
									JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
	}

	
	/**
	 * Saves the password entry in the Keystore.
	 * 
	 * @return true - if the insert is successful, false otherwise
	 */
	private boolean savePasswordEntry(String alias, String password,
			String username) {

		try {

			/*
			 * Password (together with its related username) is wrapped as a
			 * SecretKeySpec that implements SecretKey and constructs a secret
			 * key from the given password as a byte array. The reason for this
			 * is that we can only save instances of Key objects in the
			 * Keystore, and SecretKeySpec class is useful for raw secret keys
			 * (i.e. passwords) that can be represented as a byte array and have
			 * no key or algorithm parameters associated with them, e.g., DES or
			 * Triple DES. That is why we create it with the name "DUMMY" for
			 * algorithm name, as this is not checked for anyway. We actually do
			 * a little trick here and prepend the username to a password
			 * (separated by a blank character) so they are both saved in the
			 * Keystore.
			 */
			String keyToSave = username + " " + password; 

			SecretKeySpec passwordKey = new SecretKeySpec(keyToSave.getBytes(),
					"DUMMY");

			// Insert the password entry and save the changes to the Keystore
			credManager.insertPasswordEntry(alias, passwordKey);
			credManager.saveKeystore(CredentialManager.KEYSTORE);

			return true;
		} 
		catch (CMException cme) {
			logger.error("Credential Manager GUI: " + cme.getMessage());
			return false;
		} 
		catch (CMNotInitialisedException cmni) {
			// Should not really happen - we have initialised the Credential Manager
			return false;
		}
	}

	
	/**
	 * Lets the user delete a password entry from the Keystore.
	 * 
	 * @param alias -
	 *            alias of the password entry to delete
	 * @return true - if the deletion is successful, false otherwise
	 */
	private boolean deletePasswordEntry(String alias) {

		try {

			// Delete the password entry and save the changes to the Keystore
			credManager.deleteEntry(CredentialManager.KEYSTORE, alias);
			credManager.saveKeystore(CredentialManager.KEYSTORE);

			return true;
		} 
		catch (CMException cme) {
			logger.error("Credential Manager GUI: " + cme.getMessage());
			return false;
		} 
		catch (CMNotInitialisedException cmni) {
			// Should not realy happen - we have initialised the Credential Manager
			return false;
		}
	}

	
	/**
	 * Let the user examine the contents of a (user or trusted) certificate.
	 * 
	 * @return true - if the user was able to examine the certificate, false
	 *         otherwise
	 */
	private boolean viewCert() {

		// Which row from which table has been selected?
		int iRow = keyPairsTable.getSelectedRow();
		String sAlias;
		X509Certificate certToView;
		if (iRow != -1) { // row currently selected in Key Pairs table
			// Get the selected key pair entry's Keystore alias
			// Alias column is invisible so we get the value from the table model
			sAlias = (String) keyPairsTable.getModel().getValueAt(iRow, 4); 

			// Get the URL list for the key pair entry from the invisible column
			Vector<String> urlList = (Vector<String>) keyPairsTable.getModel()
					.getValueAt(iRow, 3);

			try {
				// Get the key pair entry's certificate chain
				X509Certificate[] certChain;
				certChain = CMX509Util.convertCertificates(credManager
						.getCertificateChain(sAlias));
				certToView = certChain[0]; // Show only the first certificate
				// (user's cert) for now. Later on
				// we may show the whole chain

				// Supply the certificate and list of URLs to the view
				// certificate dialog
				ViewCertDetailsDialog viewCertDetailsDialog = new ViewCertDetailsDialog(
						this, "Key pair entry's details", true, certToView,
						urlList);
				viewCertDetailsDialog.setLocationRelativeTo(this);
				viewCertDetailsDialog.setVisible(true);
				return true;
			} 
			catch (CMException cme) {
				logger.error("Credential Manager GUI: " + cme.getMessage());
				JOptionPane.showMessageDialog(this, cme.getMessage(),
						"Credential Manager Error", JOptionPane.ERROR_MESSAGE);
				return false;
			} 
			catch (CMNotInitialisedException cmni) {
				// Should not realy happen - we have initialised the Credential
				// Manager
				return false;
			}
		} 
		else {
			iRow = trustCertsTable.getSelectedRow();
			if (iRow != -1) { // row currently selected in Trusted
				// Certificates table
				sAlias = (String) trustCertsTable.getModel()
						.getValueAt(iRow, 3); // alias column is invisible so
				// we get the value from the
				// table model
				try {
					// Get the certificate entry
					certToView = CMX509Util.convertCertificate(credManager
							.getCertificate(CredentialManager.TRUSTSTORE,
									sAlias));

					// Supply the certificate to the view certificate dialog
					ViewCertDetailsDialog viewCertDetailsDialog = new ViewCertDetailsDialog(
							this, "Certificate details", true, certToView, null);
					viewCertDetailsDialog.setLocationRelativeTo(this);
					viewCertDetailsDialog.setVisible(true);
					return true;
				} 
				catch (CMException cme) {
					logger.error("Credential Manager GUI: "
							+ cme.getMessage());
					JOptionPane.showMessageDialog(this, cme.getMessage(),
							"Credential Manager Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				} 
				catch (CMNotInitialisedException cmni) {
					// Should not realy happen - we have initialised the
					// Credential Manager
					return false;
				}
			} 
			else {
				return false; // no row currently selected
			}
		}

	}

	
	/**
	 * Let the user import a key pair from a PKCS #12 keystore file.
	 * 
	 * @return True if the import is successful, false otherwise
	 */
	private boolean importKeyPairEntry() {

		// Let the user choose a PKCS #12 file (keystore) containing a public
		// and private key pair to import
		File importFile = selectImportExportFile(
				"PKCS #12 file to import from", // title
				new String[] { ".p12", ".pfx" }, // array of file extensions
				// for the file filter
				"PKCS#12 Files (*.p12, *.pfx)", // description of the filter
				"Import"); // text for the file chooser's approve button

		if (importFile == null) {
			return false;
		}

		// The PKCS #12 keystore is not a file
		if (!importFile.isFile()) {
			JOptionPane.showMessageDialog(this, "Your selection is not a file",
					"Credential Manager Alert", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		// Get the user to enter the password that was used to encrypt the
		// private key contained in the PKCS #12 file
		GetPasswordDialog dGetPassword = new GetPasswordDialog(this,
				"Import key pair entry", true,
				"Enter the password that was used to encrypt the PKCS #12 file");
		dGetPassword.setLocationRelativeTo(this);
		dGetPassword.setVisible(true);

		String pkcs12Password = dGetPassword.getPassword();

		if (pkcs12Password == null) { // user cancelled
			return false;
		} else if (pkcs12Password.length() == 0) { // empty password
			// Maybe user did not have the password set for the private key???
			return false;
		}

		try {
			// Load the PKCS #12 keystore from the file using Bouncy Castle
			// provider
			KeyStore pkcs12 = KeyStore.getInstance("PKCS12", "BC");
			pkcs12.load(new FileInputStream(importFile), pkcs12Password
					.toCharArray());

			// Display the import key pair dialog supplying all the private keys
			// stored in the PKCS #12 file
			// and a field for the user to enter service URL associated with the
			// selected key pair
			// Typically there will be only one private key inside, but could be
			// more
			NewKeyPairEntryDialog dImportKeyPair = new NewKeyPairEntryDialog(
					this, "Credential Manager", true, pkcs12);
			dImportKeyPair.setLocationRelativeTo(this);
			dImportKeyPair.setVisible(true);

			// Get the private key and certificate chain of the key pair
			Key privateKey = dImportKeyPair.getPrivateKey();
			Certificate[] certs = dImportKeyPair.getCertificateChain();

			// Get the service URLs
			Vector<String> sURLs = dImportKeyPair.getServiceURLs();

			if (privateKey == null || certs == null) {
				// User did not select a key pair for import or cancelled
				return false;
			}

			// Create an alias for the new key pair entry in the Keystore
			// as "keypair#<CERT_SERIAL_NUMBER>#<CERT_COMMON_NAME>"
			String DN = ((X509Certificate) certs[0]).getSubjectX500Principal()
					.getName(X500Principal.RFC2253);
			CMX509Util.parseDN(DN);

			String CN = CMX509Util.getCN(); // owner's common name

			// Get the hexadecimal representation of the certificate's serial
			// number
			String SN = new BigInteger(1, ((X509Certificate) certs[0])
					.getSerialNumber().toByteArray()).toString(16)
					.toUpperCase();

			String sAlias = "keypair#" + SN + "#" + CN;

			// Check if a key pair entry with the same alias already exists in
			// the Keystore
			if (credManager.containsAlias(CredentialManager.KEYSTORE, sAlias)) {

				int iSelected = JOptionPane
						.showConfirmDialog(
								this,
								"The keystore already contains the key pair entry with the same private key.\nDo you want to overwrite it?",
								"Credential Manager Alert",
								JOptionPane.YES_NO_OPTION);

				if (iSelected != JOptionPane.YES_OPTION) {
					return false;
				}
				// Otherwise, delete the old entry
				credManager.deleteEntry(CredentialManager.KEYSTORE, sAlias);
			}

			// Place the private key and certificate chain into the Keystore
			credManager.insertKeyPairEntry(sAlias, privateKey, certs);
			// Save the service URLs list associated to the private key alias to
			// the service URLs file
			credManager.addServiceURLs(sAlias, sURLs);
			// Save the changes to the Keystore
			credManager.saveKeystore(CredentialManager.KEYSTORE);

			// Update the frame's tables
			updateTables();

			// Display success message
			JOptionPane
					.showMessageDialog(this, "Key pair import successful",
							"Credential Manager Alert",
							JOptionPane.INFORMATION_MESSAGE);
			return true;
		} 
		catch (Exception ex) { // too many exceptions to catch separately
			String exMessage = "Failed to import the key pair entry to the Keystore. "
					+ ex.getMessage();

			logger.error("Credential Manager GUI: " + exMessage);
			JOptionPane.showMessageDialog(this, exMessage,
					"Credential Manager Error", JOptionPane.ERROR_MESSAGE);

			return false;
		}
	}

	
	/**
	 * Let the user edit service URLs for a given key pair entry.
	 * 
	 * @return true - if the update is successful, false otherwise
	 */
	private boolean editKeyPairEntry() {
		// Which key pair entry has been selected?
		int iRow = keyPairsTable.getSelectedRow();
		if (iRow == -1) { // no row currently selected
			return false;
		}

		// Get current URLs list for the key pair entry from the invisible
		// column
		// Because the the URLs and alias columns are not visible we call the
		// getValueAt method
		// on the table model rather than at the JTable
		Vector<String> sURLs = (Vector<String>) keyPairsTable.getModel()
				.getValueAt(iRow, 3);
		String sAlias = (String) keyPairsTable.getModel().getValueAt(iRow, 4); // current entry's Keystore alias

		// Let the user edit the list of service urls this key pair is
		// associated to
		EditKeyPairEntryDialog dEditKeyPair = new EditKeyPairEntryDialog(this,
				"Edit key pair's service URLs", true, sURLs);

		dEditKeyPair.setLocationRelativeTo(this);
		dEditKeyPair.setVisible(true);

		Vector<String> nURLs = dEditKeyPair.getServiceURLs(); // new service URLs list

		if (nURLs == null) { // user cancelled
			return false;
		}

		// Is anything actally modified?
		boolean isModified = (!sURLs.equals(nURLs));

		if (isModified) {
			try {

				// Add the new list of URLs for the alias
				credManager.addServiceURLs(sAlias, nURLs);

				// Update the frame's tables
				updateTables();

				return true;
			} 
			catch (CMException cme) {
				String exMessage = "Failed to update service URLs for the key pair entry";
				logger.error("Credential Manager GUI: " + exMessage);
				JOptionPane.showMessageDialog(this, exMessage,
						"Credential Manager Error", JOptionPane.ERROR_MESSAGE);
				return false;
			} 
			catch (CMNotInitialisedException cmni) {
				// Should not realy happen - we have initialised the Credential
				// Manager
				return false;
			}
		} 
		else {
			return true;
		}
	}

	
	/**
	 * Lets the user export user's private and public key pair to a PKCS #12
	 * keystore file.
	 * 
	 * @return True if the export is successful, false otherwise
	 */
	private boolean exportKeyPairEntry() {
		
		// Which key pair entry has been selected?
		int iRow = keyPairsTable.getSelectedRow();
		if (iRow == -1) { // no row currently selected
			return false;
		}

		// Get the key pair entry's Keystore alias
		String sAlias = (String) keyPairsTable.getModel().getValueAt(iRow, 4); 


		// Let the user choose a PKCS #12 file (keystore) to export public and
		// private key pair to
		File exportFile = selectImportExportFile("Select a file to export to", // title
				new String[] { ".p12", ".pfx" }, // array of file extensions
				// for the file filter
				"PKCS#12 Files (*.p12, *.pfx)", // description of the filter
				"Export"); // text for the file chooser's approve button

		if (exportFile == null) {
			return false;
		}

		// If file already exist - ask the user if he wants to overwrite it
		if (exportFile.isFile()) {
			int iSelected = JOptionPane
					.showConfirmDialog(
							this,
							"The file with the given name already exists.\nDo you want to overwrite it?",
							"Credential Manager Alert",
							JOptionPane.YES_NO_OPTION);

			if (iSelected == JOptionPane.NO_OPTION) {
				return false;
			}
		}

		// Get the user to enter the password for the PKCS #12 keystore file
		GetPasswordDialog dGetPassword = new GetPasswordDialog(this,
				"Credential Manager", true,
				"Enter the password for protecting the exported key pair");
		dGetPassword.setLocationRelativeTo(this);
		dGetPassword.setVisible(true);

		String pkcs12Password = dGetPassword.getPassword();

		if (pkcs12Password == null) { // user cancelled or empty password
			// Warn the user
			JOptionPane
					.showMessageDialog(
							this,
							"You must supply a password for protecting the exported key pair.",
							"Credential Manager Alert",
							JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		// Export the key pair
		try {

			credManager.exportKeyPairEntry(sAlias, exportFile, pkcs12Password);

			JOptionPane
					.showMessageDialog(this, "Key pair export successful",
							"Credential Manager Alert",
							JOptionPane.INFORMATION_MESSAGE);
			return true;
		} 
		catch (CMException cme) {
			logger.error("Credential Manager GUI: " + cme.getMessage());
			JOptionPane.showMessageDialog(this, cme.getMessage(),
					"Credential Manager Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} 
		catch (CMNotInitialisedException cmni) {
			// Should not realy happen - we have initialised the Credential
			// Manager
			return false;
		}
	}

	
	/**
	 * Lets the user delete a key pair entry from the Keystore.
	 * 
	 * @param alias - the Keystore alias of the key pair entry to delete
	 * @return True if the deletion was successful, false otherwise
	 */
	private boolean deleteKeyPairEntry(String alias) {

		try {

			// Delete the key pair entry from the Keystore and update the
			// Keystore wrapper
			credManager.deleteEntry(CredentialManager.KEYSTORE, alias);
			credManager.saveKeystore(CredentialManager.KEYSTORE);

			// Delete all URLs associated with this key pair entry
			credManager.deleteServiceURLs(alias);

			return true;
		} 
		catch (CMException cme) {
			logger.error("Credential Manager GUI: " + cme.getMessage());
			JOptionPane.showMessageDialog(this, cme.getMessage(),
					"Credential Manager Alert", JOptionPane.ERROR_MESSAGE);
			return false;
		} 
		catch (CMNotInitialisedException cmni) {
			// Should not realy happen - we have initialised the Credential
			// Manager
			return false;
		}
	}

	/**
	 * Lets the user import a trusted certificate from a PEM or DER encoded file
	 * into the Keystore.
	 * 
	 * @return True if the import is successful, false otherwise
	 */
	private boolean importTrustCertEntry() {
		// Let the user choose a file containing trusted certificate(s) to
		// import
		File certFile = selectImportExportFile(
				"Certificate file to import from", // title	
				new String[] { ".pem", "crt", ".cer", ".der", ".p7c" }, // file extensions filters
				"Certificate Files (*.pem, *.crt, , *.cer, *.der, *.p7c)", // filter description
				"Import"); // text for the file chooser's approve button

		if (certFile == null) {
			return false;
		}

		// Load the certificate(s) from the file
		ArrayList<X509Certificate> trustCertsList = new ArrayList<X509Certificate>();
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(certFile);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			// The following should be able to load PKCS #7 certificate chain files
			// as well as ASN.1 DER or PEM-encoded (sequences of) certificates
			Collection<? extends Certificate> c = cf.generateCertificates(fis);
			Iterator<? extends Certificate> i = c.iterator();
			while (i.hasNext()) {
				trustCertsList.add((X509Certificate) i.next());
			}
		} 
		catch (Exception cex) {
			// Do nothing
		} 
		finally {
			try {
				fis.close();
			} catch (Exception ex) {
				// ignore
			}
		}

		if (trustCertsList.size() == 0) { // Could not load certificates as
			// any of the above types
			try {
				// Try as openssl PEM format - which sligtly differs from the
				// one supported by JCE
				fis = new FileInputStream(certFile);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				PEMReader pr = new PEMReader(new InputStreamReader(fis), null,
						cf.getProvider().getName());
				Object cert;
				while ((cert = pr.readObject()) != null) {
					if (cert instanceof X509Certificate) {
						trustCertsList.add((X509Certificate) cert);
					}
				}
			} catch (Exception cex) {
				// do nothing
			} finally {
				try {
					fis.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}

		if (trustCertsList.size() == 0) { // Failed to load certifcate(s)
			// using any of the known encodings
			JOptionPane
					.showMessageDialog(
							this,
							"Failed to load certificate(s) using any of the known encodings -\nfile format not recognised.",
							"Credential Manager Error",
							JOptionPane.ERROR_MESSAGE);
			return false;
		}

		// Show the list of certificates contained in the file for the user to
		// select the ones to import
		NewTrustCertsDialog dImportTrustCerts = new NewTrustCertsDialog(this,
				"Credential Manager", true, trustCertsList);

		dImportTrustCerts.setLocationRelativeTo(this);
		dImportTrustCerts.setVisible(true);
		ArrayList<X509Certificate> selectedTrustCerts = dImportTrustCerts
				.getTrustedCertificates(); // user-selected trusted certs to import

		// If user cancelled or did not select any cert to import
		if ((selectedTrustCerts) == null || (selectedTrustCerts.size() == 0)) { 

			return false;
		}

		try {
			// Generate keystore aliases for the certificates to import
			ArrayList<String> aliases = new ArrayList<String>();

			for (int i = selectedTrustCerts.size() - 1; i >= 0; i--) {
				// alias is constructed as
				// "trustcert#<CERT_SERIAL_NUMBER>#<CERT_COMMON_NAME>"
				X509Certificate crt = selectedTrustCerts.get(i);
				String DN = crt.getSubjectX500Principal().getName(
						X500Principal.RFC2253);
				CMX509Util.parseDN(DN);
				String CN = CMX509Util.getCN(); // owner's common name
				
				String SN = (new BigInteger(1, crt.getSerialNumber()
						.toByteArray())).toString(16).toUpperCase(); // serial no. in hexadecimal

				String alias = "trustcert#" + SN + "#" + CN;

				// Certificate does not already exist in the Truststore
				if (!credManager.containsAlias(CredentialManager.TRUSTSTORE,
						alias)) {
					aliases.add(0, alias); // push the element to the beginning
					// of the list as we are iterating
					// from the end
				} else { // remove the certificate from the import list
					selectedTrustCerts.remove(i);
				}
			}
			// If there is anything to import - do the import
			if (selectedTrustCerts.size() > 0) {
				for (int i = 0; i < selectedTrustCerts.size(); i++) {
					// Import the selected trusted certificates
					credManager.insertTrustedCertificateEntry(aliases.get(i),
							selectedTrustCerts.get(i));
				}
			}

			// Save the changes
			credManager.saveKeystore(CredentialManager.TRUSTSTORE);

			// Update the frame's tables
			updateTables();

			// Display success message
			JOptionPane
					.showMessageDialog(this,
							"Trusted certificate import successful",
							"Credential Manager Alert",
							JOptionPane.INFORMATION_MESSAGE);

			return true;
		} 
		catch (CMException cme) {
			logger.error("Credential Manager GUI: " + cme.getMessage());
			JOptionPane.showMessageDialog(this, cme.getMessage(),
					"Credential Manager Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} 
		catch (CMNotInitialisedException cmni) {
			// Should not realy happen - we have initialised the Credential
			// Manager
			return false;
		}
	}

	
	/**
	 * Lets the user export one (at the moment) or more (in future) trusted
	 * certificate entries to a PEM-encoded file.
	 * 
	 * @return True if the export is successful, false otherwise
	 */
	private boolean exportTrustCertEntry() {

		// Which trusted certificate has been selected?
		int iRow = trustCertsTable.getSelectedRow();
		if (iRow == -1) { // no row currently selected
			return false;
		}

		// Get the trust certificate entry's Keystore alias
		String sAlias = (String) trustCertsTable.getModel().getValueAt(iRow, 3); 
		// the alias column is invisible so we get the value from the table model

		// Let the user choose a file to export public and private key pair to
		File exportFile = selectImportExportFile("Select a file to export to", // title
				new String[] { ".pem" }, // array of file extensions for the
				// file filter
				"Certificate Files (*.pem)", // description of the filter
				"Export"); // text for the file chooser's approve button

		if (exportFile == null) {
			return false;
		}

		// If file already exist - ask the user if he wants to overwrite it
		if (exportFile.isFile()) {
			int iSelected = JOptionPane
					.showConfirmDialog(
							this,
							"The file with the given name already exists.\nDo you want to overwrite it?",
							"Credential Manager Alert",
							JOptionPane.YES_NO_OPTION);

			if (iSelected == JOptionPane.NO_OPTION) {
				return false;
			}
		}

		// Export the trusted certificate
		PEMWriter pw = null;
		try {
			// Get the trusted certificate
			Certificate certToExport = credManager.getCertificate(
					CredentialManager.TRUSTSTORE, sAlias);
			pw = new PEMWriter(new FileWriter(exportFile));
			pw.writeObject(certToExport);

			JOptionPane
					.showMessageDialog(this,
							"Trusted certificate export successful",
							"Credential Manager Alert",
							JOptionPane.INFORMATION_MESSAGE);

			return true;
		} 
		catch (Exception ex) {
			String exMessage = "Failed to export the trusted certificate from the Truststore.";
			logger.error("Credential Manager GUI: " + exMessage);
			JOptionPane.showMessageDialog(
							this,
							exMessage,
							"Credential Manager Error",
							JOptionPane.ERROR_MESSAGE);
			return false;
		} 
		finally {
			if (pw != null) {
				try {
					pw.close();
				} catch (IOException ex) {
					// ignore
				}
			}
		}
	}
	

	/**
	 * Lets the user delete a trusted certificate entry from the Truststore.
	 * 
	 * @param alias - the Truststore alias of the certificate entry to delete
	 * @return True if the deletion is successful, false otherwise
	 */
	private boolean deleteTrustCertEntry(String alias) {

		try {

			// Delete the trusted certificate entry from the Truststore
			credManager.deleteEntry(CredentialManager.TRUSTSTORE, alias);
			credManager.saveKeystore(CredentialManager.TRUSTSTORE);

			return true;
		} 
		catch (CMException ex) {
			String exMessage = "Failed to delete the trusted certificate entry from the Truststore.";
			logger
					.error("Credential Manager GUI: " + exMessage);
			JOptionPane
					.showMessageDialog(
							this,
							exMessage,
							"Credential Manager Alert",
							JOptionPane.ERROR_MESSAGE);
			return false;
		}
		catch (CMNotInitialisedException cmni) {
			// Should not realy happen - we have initialised the Credential
			// Manager
			return false;
		}
	}

	
	/**
	 * Returns URLs associated with all password entries in the Keystore.
	 * 
	 * @return vURL - a list of URLs
	 */
	public Vector<String> getURLsForPasswords() {

		Vector<String> vURLs = new Vector<String>();
		for (int i = 0; i < passwordsTable.getRowCount(); i++) {
			vURLs.add((String) passwordsTable.getValueAt(i, 1));
		}
		return vURLs;
	}

	
	/**
	 * Returns URLs associated with all key pair entries in the Keystore.
	 * 
	 * @return vURL - a map of kay pair aliases and their associated URLs
	 */
	public HashMap<String, Vector<String>> getURLsForKeyPairs() {

		HashMap<String, Vector<String>> urlMap = ((KeyPairsTableModel) keyPairsTable
				.getModel()).getUrlMap();
		return urlMap;
	}

	/**
	 * Let the user select a file to export to or import from a key pair or a
	 * certificate. The file types are filtered according to their extensions:
	 * .p12 or .pfx are PKCS #12 keystore files containing private key and its
	 * public key (+cert chain) .crt are ASN.1 PEM-encoded files containing one
	 * (or more concatenated) public key certificate(s) .der are ASN.1
	 * DER-encoded files containing one public key certificate .cer are
	 * CER-encoded files containing one ore more DER-encoded certificates
	 * 
	 * @param title
	 *            File chooser's dialog title
	 * @param filter
	 *            Array of file extensions for the filter.
	 * @param description
	 *            The description of this filter
	 * @param approveButtonText
	 *            Text for the custom approve button
	 * @return The chosen file or null if none was chosen
	 */
	private File selectImportExportFile(String title, String[] filter,
			String description, String approveButtonText) {

		JFileChooser chooser = new JFileChooser();
		chooser
				.addChoosableFileFilter(new CryptoFileFilter(filter,
						description));
		chooser.setDialogTitle(title);
		chooser.setMultiSelectionEnabled(false);

		int rtnValue = chooser.showDialog(this, approveButtonText);
		if (rtnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			return selectedFile;
		}
		return null;
	}

	
	/**
	 * Update the application's tables with the Keystore/Trustore contents
	 */
	private void updateTables() throws CMException {

		// Update the Passwords, Key pairs, and Trusted Certificates tables with
		// the Keystore entries
		((PasswordsTableModel) passwordsTable.getModel()).load(credManager);
		((KeyPairsTableModel) keyPairsTable.getModel()).load(credManager);
		((TrustCertsTableModel) trustCertsTable.getModel()).load(credManager);
	}

	
	/**
	 * Exit the GUI's frame.
	 */
	private void closeFrame() {
		setVisible(false);
		dispose();
	}

	
	/**
	 * Set cursor to busy and disable application input. This can be reversed by
	 * a subsequent call to setCursorFree.
	 */
	private void setCursorBusy() {
		// Block all mouse events using glass pane
		Component glassPane = getRootPane().getGlassPane();
		glassPane.addMouseListener(new MouseAdapter() {
		});
		glassPane.setVisible(true);

		// Set cursor to busy
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	
	/**
	 * Set cursor to free and enable application input. Called after a call to
	 * setCursorBusy.
	 */
	private void setCursorFree() {
		// Accept mouse events
		Component glassPane = getRootPane().getGlassPane();
		glassPane.setVisible(false);

		// Revert cursor to default
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	
	/**
	 * Action to insert a new password entry.
	 */
	private class NewPasswordAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public NewPasswordAction() {
			putValue(NAME, "New");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			newPasswordEntry();
		}
	}

	
	/**
	 * Action to examine a password entry.
	 */
	private class ViewPasswordAction extends AbstractAction {

		private static final long serialVersionUID = -7607794320309476157L;

		/**
		 * Construct action.
		 */
		public ViewPasswordAction() {
			putValue(NAME, "View");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			viewPasswordEntry();
		}
	}

	
	/**
	 * Action to edit a password entry.
	 */
	private class EditPasswordAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public EditPasswordAction() {
			putValue(NAME, "Edit");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			editPasswordEntry();
		}
	}

	
	/**
	 * Action to delete a password entry.
	 */
	private class DeletePasswordAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public DeletePasswordAction() {
			putValue(NAME, "Delete");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			// Which entries have been selected?
			int[] iRows = passwordsTable.getSelectedRows();
			if (iRows.length == 0) { // no password entry selected
				return;
			}

			// Ask user to confirm the deletion
			int iSelected = JOptionPane
					.showConfirmDialog(
							null,
							"Are you sure you want to delete the selected password entry/entries?",
							"Credential Manager Alert",
							JOptionPane.YES_NO_OPTION);

			if (iSelected != JOptionPane.YES_OPTION) {
				return;
			}
						
			for (int i = iRows.length - 1; i >= 0; i--) { // delete from backwards

				// Get the alias for the password entry
				// Because the alias column is not visible we call the
				// getValueAt method on the table model
				// rather than at the JTable
				String alias = (String) passwordsTable.getModel().getValueAt(
						iRows[i], 5);
				deletePasswordEntry(alias);
			}
			
			try{
				// Update the frame's tables
				updateTables();
			}
			catch(CMException cme){
				logger.error("Credential Manager GUI: " + cme.getMessage());
				JOptionPane.showMessageDialog(null, cme.getMessage(),
						"Credential Manager Error", JOptionPane.ERROR_MESSAGE);
			}
			
		}
	}

	
	/**
	 * Action to examine a certificate entry.
	 */
	private class ViewCertAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public ViewCertAction() {

			putValue(NAME, "View");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			viewCert();
		}
	}

	
	/**
	 * Action to import a key pair entry.
	 */
	private class ImportKeyPairAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public ImportKeyPairAction() {
			putValue(NAME, "Import");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			importKeyPairEntry();
		}
	}

	
	/**
	 * Action to export a key pair entry.
	 */
	private class ExportKeyPairAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public ExportKeyPairAction() {
			putValue(NAME, "Export");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			exportKeyPairEntry();
		}
	}

	
	/**
	 * Action to delete a key pair entry.
	 */
	private class DeleteKeyPairAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public DeleteKeyPairAction() {
			putValue(NAME, "Delete");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			// Which entries have been selected?
			int[] iRows = keyPairsTable.getSelectedRows();
			if (iRows.length == 0) { // no key pair entries selected
				return;
			}

			// Ask user to confirm the deletion
			int iSelected = JOptionPane
					.showConfirmDialog(
							null,
							"Are you sure you want to delete the selected key pair entry/entries?",
							"Credential Manager Alert",
							JOptionPane.YES_NO_OPTION);

			if (iSelected != JOptionPane.YES_OPTION) {
				return;
			}

			for (int i = iRows.length - 1; i >= 0; i--) { // delete from
				// backwards
				// Get the alias for the key pair entry
				// Because the alias column is not visible we call the
				// getValueAt method on the table model
				// rather than at the JTable
				String alias = (String) keyPairsTable.getModel().getValueAt(
						iRows[i], 4);
				deleteKeyPairEntry(alias);
			}			

			try{
				// Update the frame's tables
				updateTables();
			}
			catch(CMException cme){
				logger.error("Credential Manager GUI: " + cme.getMessage());
				JOptionPane.showMessageDialog(null, cme.getMessage(),
						"Credential Manager Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	
	/**
	 * Action to edit service URL for a key pair entry.
	 */
	private class EditServiceURLKeyPairAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public EditServiceURLKeyPairAction() {
			putValue(NAME, "Edit");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			editKeyPairEntry();
		}
	}

	
	/**
	 * Action to import a trused certificate entry.
	 */
	private class ImportTrustCertAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public ImportTrustCertAction() {
			putValue(NAME, "Import");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			importTrustCertEntry();
		}
	}

	
	/**
	 * Action to export a trused certificate entry.
	 */
	private class ExportTrustCertAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public ExportTrustCertAction() {
			putValue(NAME, "Export");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			exportTrustCertEntry();
		}
	}

	
	/**
	 * Action to delete a trusted certificate entry.
	 */
	private class DeleteTrustCertAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Construct action.
		 */
		public DeleteTrustCertAction() {
			putValue(NAME, "Delete");
			setEnabled(true);
		}

		/**
		 * Perform action.
		 */
		public void act() {
			// Which entries have been selected?
			int[] iRows = trustCertsTable.getSelectedRows();
			if (iRows.length == 0) { // no trusted certificate entries
				// selected
				return;
			}

			// Ask user to confirm the deletion
			int iSelected = JOptionPane
					.showConfirmDialog(
							null,
							"Are you sure you want to delete the selected trusted certificate entry/entries?",
							"Credential Manager Alert",
							JOptionPane.YES_NO_OPTION);

			if (iSelected != JOptionPane.YES_OPTION) {
				return;
			}

			for (int i = iRows.length - 1; i >= 0; i--) { // delete from
				// backwards
				// Get the alias for the trust certificate entry
				// Because the alias column is not visible we call the
				// getValueAt method on the table model
				// rather than at the JTable
				String alias = (String) trustCertsTable.getModel().getValueAt(
						iRows[i], 3);
				deleteTrustCertEntry(alias);
			}
			
			try{
				// Update the frame's tables
				updateTables();
			}
			catch(CMException cme){
				logger.error("Credential Manager GUI: " + cme.getMessage());
				JOptionPane.showMessageDialog(null, cme.getMessage(),
						"Credential Manager Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	
	/**
	 * Action helper class.
	 */
	private abstract class AbstractAction extends javax.swing.AbstractAction {
		protected abstract void act();

		public void actionPerformed(ActionEvent evt) {
			setCursorBusy();
			repaint();
			new Thread(new Runnable() {
				public void run() {
					try {
						act();
					} finally {
						setCursorFree();
					}
				}
			}).start();
		}
	}

}
