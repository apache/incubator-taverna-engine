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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.misc.NetscapeCertType;

import net.sf.taverna.raven.appconfig.ApplicationRuntime;

/**
 * Utility methods for Credential Manager and security-related stuff.
 * 
 * @author Alex Nenadic
 */
public class CMUtils {

	
    /** PKCS #7 encoding name */
    public static final String PKCS7_ENCODING = "PKCS7";

    /** PkiPath encoding name */
    public static final String PKIPATH_ENCODING = "PkiPath";

    /** OpenSSL PEM encoding name */
    public static final String OPENSSL_PEM_ENCODING = "OpenSSL_PEM";

	private static Logger logger = Logger.getLogger(CMUtils.class);
    
	/**
	 * Get the configuration directory where the security stuff will be/is saved to.
	 */
	public static File getCredentialManagerDefaultDirectory() {
		
		File home = ApplicationRuntime.getInstance().getApplicationHomeDir();
//		File configDirectory = new File(home,"conf");
//		if (!configDirectory.exists()) {
//			configDirectory.mkdir();
//		}
		File secConfigDirectory = new File(home,"security");
		if (!secConfigDirectory.exists()) {
			secConfigDirectory.mkdir();
		}
		return secConfigDirectory;
	}

	
	public static URI resolveUriFragment(URI uri, String realm) throws URISyntaxException {
		URI fragment;
		/*
		 * Little hack to encode the fragment correctly - why does
		 * not java.net.URI expose this quoting or have setFragment()?
		 */
		fragment = new URI("http", "localhost", "/", realm);
		fragment = (fragment.resolve(fragment
				.getPath())).relativize(fragment);
		uri = uri.resolve(fragment);
		return uri;
	}
  
    /**
     * Convert the supplied certificate object into an X509Certificate object.
     */
    public static X509Certificate convertCertificate(Certificate cert)
        throws CMException
    {
        try {
        	// Get the factory for X509 certificates
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // Get the encoded (binary) form of the certificate. 
            // For an X509 certificate the encoding will be DER.
            ByteArrayInputStream bais = new ByteArrayInputStream(
                cert.getEncoded());
            // Create the X509 certificate object from the stream
            return (X509Certificate) cf.generateCertificate(bais);
        }
        catch (CertificateException ex) {
            throw new CMException("Failed to convert certificate", ex);
        }
    }
    
    /**
     * Convert the given array of certificate objects into
     * X509Certificate objects.
     */
    public static X509Certificate[] convertCertificates(Certificate[] certsIn)
        throws CMException
    {
        X509Certificate[] certsOut = new X509Certificate[certsIn.length];

        for (int i = 0; i < certsIn.length; i++) {
            certsOut[i] = convertCertificate(certsIn[i]);
        }
        return certsOut;
    }   
    
    /**
     * Get the message digest as a string using the given digest algorithm.
     */
	public static String getMessageDigest(byte[] messageInBytes, String digestType) {
		// Create message digest object using the supplied algorithm
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance(digestType);
		} catch (NoSuchAlgorithmException ex) {
			logger.error("Failed to create message digest.", ex);
			return "";
		}

		// Create raw message digest
		byte[] bFingerPrint = messageDigest.digest(messageInBytes);

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
	 * extension (2.16.840.1.113730.1.1) as a string.
	 */
	public static String getIntendedUses(byte[] value) {

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
	
//    /**
//     * Load one or more certificates from the specified file.
//     *
//     * @param certFile The file to load certificates from
//     * @param encoding The certification path encoding. If null, treat as a
//     * normal certificate, not certification path.  Use one of the
//     * <code>*_ENCODING</code> constants here.
//     * @return The array of certificates
//     * @throws CMException Problem encountered while loading the
//     * certificate(s)
//     */
//	public static X509Certificate[] loadCertificates(File certFile,
//			String encoding) throws CMException
//        {
//        ArrayList<X509Certificate> certsList = new ArrayList<X509Certificate>();
//
//        FileInputStream fis = null;
//
//        try {
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            fis = new FileInputStream(certFile);
//            Collection<? extends Certificate> coll = null;
//
//            if (OPENSSL_PEM_ENCODING.equals(encoding)) {
//                // Special case; this is not a real JCE supported encoding.
//                PEMReader pr = new PEMReader(new InputStreamReader(fis), null,
//                    cf.getProvider().getName());
//                /* These can contain just about anything, and
//                 unfortunately the PEMReader API (as of BC 1.25 to 1.31)
//                 won't allow us to really skip things we're not interested
//                 in; stuff happens already in readObject().  This may cause
//                 some weird exception messages for non-certificate objects in
//                 the "stream", for example passphrase related ones for
//                 protected private keys. */
//                Object cert;
//                while ((cert = pr.readObject()) != null) {
//                    if (cert instanceof X509Certificate) {
//                        // "Short-circuit" into vCerts, not using coll.
//                        certsList.add((X509Certificate) cert);
//                    }
//                    // Skip other stuff, at least for now.
//                }
//            }
//            else if (encoding != null) {
//                // Try it as a certification path of the specified type
//                coll = cf.generateCertPath(fis, encoding).getCertificates();
//            }
//            else {
//                // "Normal" certificate(s)
//                coll = cf.generateCertificates(fis);
//            }
//
//            if (coll != null) {
//                for (Iterator<? extends Certificate> iter = (Iterator<? extends Certificate>) coll.iterator(); iter.hasNext();) {
//                    X509Certificate cert = (X509Certificate) iter.next();
//                    if (cert != null) {
//                        certsList.add(cert);
//                    }
//                }
//            }
//        }
//        // Some RuntimeExceptions which really ought to be
//        // CertificateExceptions may be thrown from cf.generateCert* above,
//        // for example Sun's PKCS #7 parser tends to throw them.
//        catch (Exception ex) {
//            // TODO: don't throw if vCerts non-empty (eg. OpenSSL PEM above)?
//            throw new CMException("Failed to load certificate", ex);
//        }
//        finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                }
//                catch (IOException ex) {
//                    // Ignore
//                }
//            }
//        }
//
//        return (X509Certificate[]) certsList.toArray(new X509Certificate[certsList.size()]);
//    }
    
    
    // FROM RFC 2253:	
    //                    CN      commonName
    //                    L       localityName
    //                    ST      stateOrProvinceName
    //                    O       organizationName
    //                    OU      organizationalUnitName
    //                    C       countryName
    //                    STREET  streetAddress
    //                    DC      domainComponent
    //                    UID     userid

    private String emailAddress; // not from RFC 2253, yet some certificates contain this field
    
    private String CN;
    private String L;
    private String ST;
    private String C;
    private String O;
    private String OU;
    /**
     * Parses a DN string and fills in fields with DN parts.
     * Heavily based on uk.ac.omii.security.utils.DNParser class from omii-security-utils library.
     * 
     * http://maven.omii.ac.uk/maven2/repository/omii/omii-security-utils/
     */

    public void parseDN(String DNstr)
    {
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //		Parse the DN String and put into variables. First, tokenise using a "," character as a delimiter
        //		UNLESS escaped with a "\" character. Put the tokens into an ArrayList. These should be name value pairs
        //		separated by "=". Tokenise these using a StringTokenizer class, test for the name, and if one of the
        //		recognised names, copy into the correct variable. The reason StringTokenizer is not used for the major
        //		token list is that the StringTokenizer class does not handle escaped delimiters so an escaped delimiter
        //		in the code would be treated as a valid one.

        int i = 0;

        char majorListDelimiter = ',';
        char majorListEscapeChar = '\\';

        //String minorListDelimiter = "=";

        String DNchars = DNstr;

        int startIndex = 0;
        int endIndex = 0;
        boolean ignoreThisChar = false;

        boolean inQuotes = false;

        ArrayList<String> majorTokenList = new ArrayList<String>();

        for (i = 0; i < DNchars.length(); i++)
        {
            if (ignoreThisChar == true)
            {
                ignoreThisChar = false;
            }
            else if ((inQuotes == false) && (DNchars.charAt(i) == '\"'))
            {
                inQuotes = true;
            }
            else if ((inQuotes == true) && (DNchars.charAt(i) == '\"'))
            {
                inQuotes = false;
            }
            else if (inQuotes == true)
            {
                continue;
            }
            else if (DNchars.charAt(i) == majorListEscapeChar)
            {
                ignoreThisChar = true;
            }
            else if ((DNchars.charAt(i) == majorListDelimiter)
                && (ignoreThisChar == false))
            {
                endIndex = i;
                majorTokenList.add(DNchars.substring(startIndex, endIndex));
                startIndex = i + 1;
            }
        }

        // Add last token - after the last delimiter
        endIndex = DNchars.length();
        majorTokenList.add(DNchars.substring(startIndex, endIndex));

        for (i = 0; i < majorTokenList.size(); i++)
        {
            String currentToken = String.valueOf(majorTokenList.get(i));

            currentToken = currentToken.trim();
	   
            String[] minorTokenList = currentToken.split("=", 2); // split on first equals only, as value can contain an equals char

            if(minorTokenList.length == 2) // there had better be a key and a value only
            {
	    	String DNTokenName = minorTokenList[0];
	    	String DNTokenValue = minorTokenList[1];

                if (DNTokenName.toUpperCase().equals("CN") ||
                		DNTokenName.toUpperCase().equals("COMMONNAME"))
                {
                    CN = DNTokenValue;
                }

                if (DNTokenName.toUpperCase().equals("EMAIL") ||
                    DNTokenName.toUpperCase().equals("EMAILADDRESS"))
                {
                    emailAddress = DNTokenValue;
                }

                if (DNTokenName.toUpperCase().equals("OU") ||
                    DNTokenName.toUpperCase().equals("ORGANIZATIONALUNITNAME"))
                {
                    OU = DNTokenValue;
                }

                if (DNTokenName.toUpperCase().equals("O") ||
                     DNTokenName.toUpperCase().equals("ORGANIZATIONNAME"))
                {
                    O = DNTokenValue;
                }

                if (DNTokenName.toUpperCase().equals("L") ||
                    DNTokenName.toUpperCase().equals("LOCALITYNAME"))
                {
                    L = DNTokenValue;
                }

                if (DNTokenName.toUpperCase().equals("ST") ||
                    DNTokenName.toUpperCase().equals("STATEORPROVINCENAME"))
                {
                    ST = DNTokenValue;
                }

                if (DNTokenName.toUpperCase().equals("C") ||
                    DNTokenName.toUpperCase().equals("COUNTRYNAME"))
                {
                    C = DNTokenValue;
                }
            }
	    // else we have a key with no value, so skip processing the key
        }

        if (CN == null)
        {
            CN = "none";
        }

        if (emailAddress == null)
        {
            emailAddress = "none";
        }

        if (OU == null)
        {
            OU = "none";
        }

        if (O == null)
        {
            O = "none";
        }

        if (L == null)
        {
            L = "none";
        }

        if (ST == null)
        {
            ST = "none";
        }

        if (C == null)
        {
            C = "none";
        }
    }

    public String getCN()
    {
        return CN;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public String getOU()
    {
        return OU;
    }

    public String getO()
    {
        return O;
    }

    public String getL()
    {
        return L;
    }

    public String getST()
    {
        return ST;
    }

    public String getC()
    {
        return C;
    }
}
