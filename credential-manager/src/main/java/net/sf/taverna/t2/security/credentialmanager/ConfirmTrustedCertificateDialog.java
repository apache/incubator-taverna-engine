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
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JSeparator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;

import net.sf.taverna.t2.lang.ui.DialogTextArea;
import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CMX509Util;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.misc.NetscapeCertType;

/**
 * Displays the details of a X.509 certificate and asks user if they want to
 * trust it.
 * 
 * @author Alex Nenadic
 */
@SuppressWarnings("serial")
public class ConfirmTrustedCertificateDialog extends JDialog {
	
	private static Logger logger = Logger.getLogger(ConfirmTrustedCertificateDialog.class);

	// Stores certificate to display
	private X509Certificate cert;

	// Stores user's decision as whether to trust this service's certificaet or not.
	private boolean shouldTrust;

	/**
	 * Creates new ConfirmTrustedCertificateDialog where parent is a Frame.
	 */
	public ConfirmTrustedCertificateDialog(Frame parent, String title,
			boolean modal, X509Certificate crt){
		super(parent, title, modal);
		this.cert = crt;
		initComponents();
	}
	
	/**
	 * Creates new ConfirmTrustedCertificateDialog where parent is a Dialog.
	 */
	public ConfirmTrustedCertificateDialog(Dialog parent, String title,
			boolean modal, X509Certificate crt)
			throws CMException {
		super(parent, title, modal);
		this.cert = crt;
		initComponents();
	}

	/**
	 * Initialise the dialog's GUI components.
	 */
	private void initComponents(){
		
		// title panel
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(Color.WHITE);
		JLabel titleLabel = new JLabel("View service's certificate");
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13.5f));
		titleLabel.setBorder(new EmptyBorder(10, 10, 0, 10));
		DialogTextArea titleMessage = new DialogTextArea();
		titleMessage.setMargin(new Insets(5, 20, 10, 10));
		titleMessage.setFont(titleMessage.getFont().deriveFont(11f));
		titleMessage.setEditable(false);
		titleMessage.setFocusable(false);
		titlePanel.setBorder( new EmptyBorder(10, 10, 0, 10));
		titlePanel.add(titleLabel, BorderLayout.NORTH);
		titlePanel.add(titleMessage, BorderLayout.CENTER);
		
		// Certificate details:

		// Grid Bag Constraints templates for labels (column 1) and
		// values (column 2) of certificate details
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.gridx = 0;
		gbcLabel.ipadx = 20;
		gbcLabel.gridwidth = 1;
		gbcLabel.gridheight = 1;
		gbcLabel.insets = new Insets(2, 15, 2, 2);
		gbcLabel.anchor = GridBagConstraints.LINE_START;

		GridBagConstraints gbcValue = new GridBagConstraints();
		gbcValue.gridx = 1;
		gbcValue.gridwidth = 1;
		gbcValue.gridheight = 1;
		gbcValue.insets = new Insets(2, 5, 2, 2);
		gbcValue.anchor = GridBagConstraints.LINE_START;

		// Netscape Certificate Type non-critical extension (if any)
		// defines the intended uses of the certificate - to make it look like
		// firefox's view certificate dialog
		byte[] intendedUses = cert.getExtensionValue("2.16.840.1.113730.1.1"); // Netscape Certificate Type OID
		JLabel jlIntendedUses = null;
		JTextField jtfIntendedUsesValue = null;
		JPanel jpUses = null;
		GridBagConstraints gbc_jpUses = null;
		if (intendedUses != null) {
			jlIntendedUses = new JLabel(
					"This certificate has been approved for the following uses:");
			jlIntendedUses.setFont(new Font(null, Font.BOLD, 11));
			jlIntendedUses.setBorder(new EmptyBorder(5, 5, 5, 5));

			jtfIntendedUsesValue = new JTextField(45);
			jtfIntendedUsesValue.setText(getIntendedUses(intendedUses));
			jtfIntendedUsesValue.setEditable(false);
			jtfIntendedUsesValue.setFont(new Font(null, Font.PLAIN, 11));

			jpUses = new JPanel(new BorderLayout());
			jpUses.add(jlIntendedUses, BorderLayout.NORTH);
			jpUses.add(jtfIntendedUsesValue, BorderLayout.CENTER);
			JSeparator jsp = new JSeparator(JSeparator.HORIZONTAL);
			jpUses.add(jsp, BorderLayout.SOUTH);

			gbc_jpUses = (GridBagConstraints) gbcLabel.clone();
			gbc_jpUses.gridy = 0;
			gbc_jpUses.gridwidth = 2; // takes two columns
			gbc_jpUses.insets = new Insets(5, 5, 5, 5);// has slightly bigger insets
		}

		// Issued To
		JLabel jlIssuedTo = new JLabel("Issued To");
		jlIssuedTo.setFont(new Font(null, Font.BOLD, 11));
		GridBagConstraints gbc_jlIssuedTo = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlIssuedTo.gridy = 1;
		gbc_jlIssuedTo.gridwidth = 2; // takes two columns
		gbc_jlIssuedTo.insets = new Insets(5, 5, 5, 5);// has slightly bigger insets
		// Distinguished Name (DN)
		String sDN = cert.getSubjectX500Principal().getName(
				X500Principal.RFC2253);
		CMX509Util util = new CMX509Util();
		util.parseDN(sDN);
		// Extract the CN, O, OU and EMAILADDRESS fields
		String sCN = util.getCN();
		String sOrg = util.getO();
		String sOU = util.getOU();
		titleMessage.setText("The service host " + sCN + " requires HTTPS connection and has identified itself with the certificate below.\n" +
				"Do you want to trust this service? (Refusing to trust means you will not be able to invoke services on this host from a workflow.)");
		// String sEMAILADDRESS = CMX509Util.getEmilAddress();
		// Common Name (CN)
		JLabel jlCN = new JLabel("Common Name (CN)");
		jlCN.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlCN = (GridBagConstraints) gbcLabel.clone();
		gbc_jlCN.gridy = 2;
		JLabel jlCNValue = new JLabel(sCN);
		jlCNValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlCNValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlCNValue.gridy = 2;
		// Organisation (O)
		JLabel jlOrg = new JLabel("Organisation (O)");
		jlOrg.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlOrg = (GridBagConstraints) gbcLabel.clone();
		gbc_jlOrg.gridy = 3;
		JLabel jlOrgValue = new JLabel(sOrg);
		jlOrgValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlOrgValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlOrgValue.gridy = 3;
		// Organisation Unit (OU)
		JLabel jlOU = new JLabel("Organisation Unit (OU)");
		jlOU.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlOU = (GridBagConstraints) gbcLabel.clone();
		gbc_jlOU.gridy = 4;
		JLabel jlOUValue = new JLabel(sOU);
		jlOUValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlOUValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlOUValue.gridy = 4;
		// E-mail Address
		// JLabel jlEmail = new JLabel("E-mail Address");
		// jlEmail.setFont(new Font(null, Font.PLAIN, 11));
		// GridBagConstraints gbc_jlEmail = (GridBagConstraints)
		// gbcLabel.clone();
		// gbc_jlEmail.gridy = 5;
		// JLabel jlEmailValue = new JLabel(sEMAILADDRESS);
		// jlEmailValue.setFont(new Font(null, Font.PLAIN, 11));
		// GridBagConstraints gbc_jlEmailValue = (GridBagConstraints)
		// gbcValue.clone();
		// gbc_jlEmailValue.gridy = 5;
		// Serial Number
		JLabel jlSN = new JLabel("Serial Number");
		jlSN.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlSN = (GridBagConstraints) gbcLabel.clone();
		gbc_jlSN.gridy = 6;
		JLabel jlSNValue = new JLabel();
		// Get the hexadecimal serial number
		StringBuffer strBuff = new StringBuffer(new BigInteger(1, cert
				.getSerialNumber().toByteArray()).toString(16).toUpperCase());
		// Place colons at every two hexadecimal characters
		if (strBuff.length() > 2) {
			for (int iCnt = 2; iCnt < strBuff.length(); iCnt += 3) {
				strBuff.insert(iCnt, ':');
			}
		}
		jlSNValue.setText(strBuff.toString());
		jlSNValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlSNValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlSNValue.gridy = 6;
		// Version
		JLabel jlVersion = new JLabel("Version");
		jlVersion.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlVersion = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlVersion.gridy = 7;
		JLabel jlVersionValue = new JLabel(Integer.toString(cert.getVersion()));
		jlVersionValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlVersionValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlVersionValue.gridy = 7;

		// Issued By
		JLabel jlIssuedBy = new JLabel("Issued By");
		jlIssuedBy.setFont(new Font(null, Font.BOLD, 11));
		GridBagConstraints gbc_jlIssuedBy = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlIssuedBy.gridy = 8;
		gbc_jlIssuedBy.gridwidth = 2; // takes two columns
		gbc_jlIssuedBy.insets = new Insets(5, 5, 5, 5);// has slightly bigger
														// insets
		// Distinguished Name (DN)
		String iDN = cert.getIssuerX500Principal().getName(
				X500Principal.RFC2253);
		util.parseDN(iDN);
		// Extract the CN, O and OU fields
		String iCN = util.getCN();
		String iOrg = util.getO();
		String iOU = util.getOU();
		// Common Name (CN)
		JLabel jlICN = new JLabel("Common Name (CN)");
		jlICN.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlICN = (GridBagConstraints) gbcLabel.clone();
		gbc_jlICN.gridy = 9;
		JLabel jlICNValue = new JLabel(iCN);
		jlICNValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlICNValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlICNValue.gridy = 9;
		// Organisation (O)
		JLabel jlIOrg = new JLabel("Organisation (O)");
		jlIOrg.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlIOrg = (GridBagConstraints) gbcLabel.clone();
		gbc_jlIOrg.gridy = 10;
		JLabel jlIOrgValue = new JLabel(iOrg);
		jlIOrgValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlIOrgValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlIOrgValue.gridy = 10;
		// Organisation Unit (OU)
		JLabel jlIOU = new JLabel("Organisation Unit (OU)");
		jlIOU.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlIOU = (GridBagConstraints) gbcLabel.clone();
		gbc_jlIOU.gridy = 11;
		JLabel jlIOUValue = new JLabel(iOU);
		jlIOUValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlIOUValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlIOUValue.gridy = 11;
		// Validity
		JLabel jlValidity = new JLabel("Validity");
		jlValidity.setFont(new Font(null, Font.BOLD, 11));
		GridBagConstraints gbc_jlValidity = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlValidity.gridy = 12;
		gbc_jlValidity.gridwidth = 2; // takes two columns
		gbc_jlValidity.insets = new Insets(5, 5, 5, 5);// has slightly bigger
														// insets
		// Issued On
		JLabel jlIssuedOn = new JLabel("Issued On");
		jlIssuedOn.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlIssuedOn = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlIssuedOn.gridy = 13;
		JLabel jlIssuedOnValue = new JLabel(cert.getNotBefore().toString());
		jlIssuedOnValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlIssuedOnValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlIssuedOnValue.gridy = 13;
		// Expires On
		JLabel jlExpiresOn = new JLabel("Expires On");
		jlExpiresOn.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlExpiresOn = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlExpiresOn.gridy = 14;
		JLabel jlExpiresOnValue = new JLabel(cert.getNotAfter().toString());
		jlExpiresOnValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlExpiresOnValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlExpiresOnValue.gridy = 14;

		// Fingerprints
		byte[] bCert = new byte[0];
		try {
			bCert = cert.getEncoded();
		} catch (CertificateEncodingException ex) {
			logger.error("Could not get the encoded form of the certificate.", ex);
		}
		JLabel jlFingerprints = new JLabel("Fingerprints");
		jlFingerprints.setFont(new Font(null, Font.BOLD, 11));
		GridBagConstraints gbc_jlFingerprints = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlFingerprints.gridy = 15;
		gbc_jlFingerprints.gridwidth = 2; // takes two columns
		gbc_jlFingerprints.insets = new Insets(5, 5, 5, 5);// has slightly
															// bigger insets
		// SHA-1 Fingerprint
		JLabel jlSHA1Fingerprint = new JLabel("SHA1 Fingerprint");
		jlSHA1Fingerprint.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlSHA1Fingerprint = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlSHA1Fingerprint.gridy = 16;
		JLabel jlSHA1FingerprintValue = new JLabel(getMessageDigest(bCert,
				"SHA1"));
		jlSHA1FingerprintValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlSHA1FingerprintValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlSHA1FingerprintValue.gridy = 16;
		// MD5 Fingerprint
		JLabel jlMD5Fingerprint = new JLabel("MD5 Fingerprint");
		jlMD5Fingerprint.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlMD5Fingerprint = (GridBagConstraints) gbcLabel
				.clone();
		gbc_jlMD5Fingerprint.gridy = 17;
		JLabel jlMD5FingerprintValue = new JLabel(
				getMessageDigest(bCert, "MD5"));
		jlMD5FingerprintValue.setFont(new Font(null, Font.PLAIN, 11));
		GridBagConstraints gbc_jlMD5FingerprintValue = (GridBagConstraints) gbcValue
				.clone();
		gbc_jlMD5FingerprintValue.gridy = 17;

		// Empty label to add a bit space at the bottom of the panel
		// to make it look like firefox's view certificate dialog
		JLabel jlEmpty = new JLabel("");
		GridBagConstraints gbc_jlEmpty = (GridBagConstraints) gbcLabel.clone();
		gbc_jlEmpty.gridy = 18;
		gbc_jlEmpty.gridwidth = 2; // takes two columns
		gbc_jlEmpty.ipady = 40;

		JPanel jpCertificate = new JPanel(new GridBagLayout());
		jpCertificate.setBorder(new CompoundBorder(new EmptyBorder(15, 15, 15,
				15), new EtchedBorder()));

		if (intendedUses != null) {
			jpCertificate.add(jpUses, gbc_jpUses);
		}
		jpCertificate.add(jlIssuedTo, gbc_jlIssuedTo); // Issued To
		jpCertificate.add(jlCN, gbc_jlCN);
		jpCertificate.add(jlCNValue, gbc_jlCNValue);
		jpCertificate.add(jlOrg, gbc_jlOrg);
		jpCertificate.add(jlOrgValue, gbc_jlOrgValue);
		jpCertificate.add(jlOU, gbc_jlOU);
		jpCertificate.add(jlOUValue, gbc_jlOUValue);
		// jpCertificate.add(jlEmail, gbc_jlEmail);
		// jpCertificate.add(jlEmailValue, gbc_jlEmailValue);
		jpCertificate.add(jlSN, gbc_jlSN);
		jpCertificate.add(jlSNValue, gbc_jlSNValue);
		jpCertificate.add(jlVersion, gbc_jlVersion);
		jpCertificate.add(jlVersionValue, gbc_jlVersionValue);
		jpCertificate.add(jlIssuedBy, gbc_jlIssuedBy); // Issued By
		jpCertificate.add(jlICN, gbc_jlICN);
		jpCertificate.add(jlICNValue, gbc_jlICNValue);
		jpCertificate.add(jlIOrg, gbc_jlIOrg);
		jpCertificate.add(jlIOrgValue, gbc_jlIOrgValue);
		jpCertificate.add(jlIOU, gbc_jlIOU);
		jpCertificate.add(jlIOUValue, gbc_jlIOUValue);
		jpCertificate.add(jlValidity, gbc_jlValidity); // Validity
		jpCertificate.add(jlIssuedOn, gbc_jlIssuedOn);
		jpCertificate.add(jlIssuedOnValue, gbc_jlIssuedOnValue);
		jpCertificate.add(jlExpiresOn, gbc_jlExpiresOn);
		jpCertificate.add(jlExpiresOnValue, gbc_jlExpiresOnValue);
		jpCertificate.add(jlFingerprints, gbc_jlFingerprints); // Fingerprints
		jpCertificate.add(jlSHA1Fingerprint, gbc_jlSHA1Fingerprint);
		jpCertificate.add(jlSHA1FingerprintValue, gbc_jlSHA1FingerprintValue);
		jpCertificate.add(jlMD5Fingerprint, gbc_jlMD5Fingerprint);
		jpCertificate.add(jlMD5FingerprintValue, gbc_jlMD5FingerprintValue);
		jpCertificate.add(jlEmpty, gbc_jlEmpty); // Empty label to get some vertical space on the frame

		// OK button
		JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));

		final JButton jbTrust = new JButton("Trust");
		jbTrust.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				trustPressed();
			}
		});
		final JButton jbDontTrust = new JButton("Do not trust");
		jbDontTrust.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dontTrustPressed();
			}
		});

		jpButtons.add(jbTrust);
		jpButtons.add(jbDontTrust);

		// Put it all together
		getContentPane().add(titlePanel, BorderLayout.NORTH);
		getContentPane().add(jpCertificate, BorderLayout.CENTER);
		getContentPane().add(jpButtons, BorderLayout.SOUTH);

		// Resizing wreaks havoc
		setResizable(false);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				closeDialog();
			}
		});

		getRootPane().setDefaultButton(jbTrust);

		pack();
	}

	/**
	 * Get the digest of a message as a formatted String.
	 * 
	 * @param bMessage
	 *            The message to digest
	 * @param digestType
	 *            The message digest algorithm
	 * @return The message digest
	 */
	public static String getMessageDigest(byte[] bMessage, String digestType) {
		// Create message digest object using the supplied algorithm
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance(digestType);
		} catch (NoSuchAlgorithmException ex) {
			logger.error("Failed to create message digest.", ex);
			return "";
		}

		// Create raw message digest
		byte[] bFingerPrint = messageDigest.digest(bMessage);

		// Place the raw message digest into a StringBuffer as a Hex number
		StringBuffer strBuff = new StringBuffer(new BigInteger(1, bFingerPrint)
				.toString(16).toUpperCase());

		// Odd number of characters so add in a padding "0"
		if ((strBuff.length() % 2) != 0) {
			strBuff.insert(0, '0');
		}

		// Place colons at every two hex characters
		if (strBuff.length() > 2) {
			for (int iCnt = 2; iCnt < strBuff.length(); iCnt += 3) {
				strBuff.insert(iCnt, ':');
			}
		}

		// Return the formatted message digest
		return strBuff.toString();
	}

	/**
	 * Gets the intended certificate uses, i.e. Netscape Certificate Type
	 * extension (2.16.840.1.113730.1.1) value as a string
	 * 
	 * @param value
	 *            Extension value as a DER-encoded OCTET string
	 * @return Extension value as a string
	 */
	private String getIntendedUses(byte[] value) {

		// Netscape Certificate Types (2.16.840.1.113730.1.1)
		int[] INTENDED_USES = new int[] { NetscapeCertType.sslClient,
				NetscapeCertType.sslServer, NetscapeCertType.smime,
				NetscapeCertType.objectSigning, NetscapeCertType.reserved,
				NetscapeCertType.sslCA, NetscapeCertType.smimeCA,
				NetscapeCertType.objectSigningCA, };

		// Netscape Certificate Type strings (2.16.840.1.113730.1.1)
		HashMap<String, String> INTENDED_USES_STRINGS = new HashMap<String, String>();
		INTENDED_USES_STRINGS.put("128", "SSL Client");
		INTENDED_USES_STRINGS.put("64", "SSL Server");
		INTENDED_USES_STRINGS.put("32", "S/MIME");
		INTENDED_USES_STRINGS.put("16", "Object Signing");
		INTENDED_USES_STRINGS.put("8", "Reserved");
		INTENDED_USES_STRINGS.put("4", "SSL CA");
		INTENDED_USES_STRINGS.put("2", "S/MIME CA");
		INTENDED_USES_STRINGS.put("1", "Object Signing CA");

		// Get octet string from extension value
		ASN1OctetString fromByteArray = new DEROctetString(value);
		byte[] octets = fromByteArray.getOctets();
		DERBitString fromByteArray2 = new DERBitString(octets);
		int val = new NetscapeCertType(fromByteArray2).intValue();
		StringBuffer strBuff = new StringBuffer();
		for (int i = 0, len = INTENDED_USES.length; i < len; i++) {
			int use = INTENDED_USES[i];
			if ((val & use) == use) {
				strBuff.append(INTENDED_USES_STRINGS.get(String.valueOf(use))
						+ ", \n");
			}
		}
		// remove the last ", \n" from the end of the buffer
		String str = strBuff.toString();
		str = str.substring(0, str.length() - 3);
		return str;
	}

	/**
	 * 'Trust' button pressed.
	 */
	private void trustPressed() {
		shouldTrust = true;
		closeDialog();
	}

	/**
	 * 'Do not trust' button pressed.
	 */
	private void dontTrustPressed() {
		shouldTrust = false;
		closeDialog();
	}	
	
	/**
	 * Closes the dialog.
	 */
	public void closeDialog() {
		setVisible(false);
		dispose();
	}

	public boolean shouldTrust() {
		return shouldTrust;
	}
}
