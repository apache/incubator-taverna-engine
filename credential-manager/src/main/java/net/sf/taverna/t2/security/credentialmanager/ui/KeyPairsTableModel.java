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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CMNotInitialisedException;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;

/**
 * The table model used to display the Keystore's key pair entries.
 * 
 * @author Alexandra Nenadic
 */
public class KeyPairsTableModel
    extends AbstractTableModel
{

	private static final long serialVersionUID = 4908900063321484451L;

	/** Holds the column names */
    private String[] columnNames;

    /** Holds the table data */
    private Object[][] data;
    
    /** Holds the map of URLs for each key pair entry (i.e. map's keys are key pair entries' aliases
     * and values are lists of URLs.   
     */
    private HashMap<String, Vector<String>> urlMap = null;
    
    /**
     * Construct a new KeyPairsTableModel.
     */
    public KeyPairsTableModel()
    {
       	data = new Object[0][0];
        columnNames = new String[] {
        	"Entry Type", // type of the Keystore entry
        	"Owner:Serial Number", // owner's common name and serial number of the user's public key certificate
            "Last Modified", // last modified date of the entry
            "URLs", // the invisible column holding the list of URLs associated with this entry
            "Alias" // the invisible column holding the actual alias in the Keystore
        	};
        
        
    }
    
    /**
     * Load the KeyPairsTableModel with the key pair entries from the Keystore. 
     *
     * @param keyStore The keystore
     * @throws CMException A problem is encountered during accessing
     * the keystore's entries
     */
    public void load(CredentialManager cm)
        throws CMException
    {
        // Place key pair entries' aliases in a tree map to sort them
        TreeMap<String, String> sortedAliases = new TreeMap<String, String>();
        
        // Also place aliases in a list 
        Vector<String> aliasList = new Vector<String>();
        
        try{
        	for (Enumeration<String> en = cm.getAliases(CredentialManager.KEYSTORE); en.hasMoreElements();) {

        		String sAlias = en.nextElement();
        		// Only save key pair entries starting with "keypair"
        		// Alias for a key pair entry is constructed as "keypair#<CERT_SERIAL_NUMBER>#<CERT_COMMON_NAME>"
        		/*if (keyStore.isKeyEntry(sAlias)
                    && keyStore.getCertificateChain(sAlias) != null
                    && keyStore.getCertificateChain(sAlias).length != 0){*/
        		if (sAlias.startsWith("keypair") && cm.isKeyEntry(sAlias)){ //user's public key cert entry gets the same alias in the Keystore as its private key entry, so we have to check which one it is
        			sortedAliases.put(sAlias, sAlias);
            		aliasList.add(sAlias);
        		}
        	}
 
        	// Get the lists of URLs for every alias
        	urlMap = null;
        	if (aliasList != null){
            	urlMap = cm.getServiceURLs();        		
        	}
        			
            // Create one table row for each key pair entry
            // Each row has 4 fields - type, service URL, last modified data and the invisible alias
            data = new Object[sortedAliases.size()][5];

            // Iterate through the sorted aliases (if any), retrieving the key pair
            // entries and populating the table model
            int iCnt = 0;
            for (Iterator<Entry<String, String>> itr = sortedAliases.entrySet().iterator(); itr.hasNext(); iCnt++)
            {
                String sAlias = (String) itr.next().getKey();

                // Populate the type column - it is set with an integer
                // but a custom cell renderer will cause a suitable icon
                // to be displayed
                data[iCnt][0] = CredentialManagerGUI.KEY_PAIR_ENTRY_TYPE;

                // Populate the 'Owner:Serial Number' column extracted from the alias
                data[iCnt][1] = sAlias.substring(sAlias.lastIndexOf('#')+1) + 
                	":" + 
                	sAlias.substring(sAlias.indexOf('#')+1,sAlias.lastIndexOf('#'));
                
                // Populate the modified date column ("UBER" keystore type supports creation date)
               	data[iCnt][2] = cm.getCreationDate(CredentialManager.KEYSTORE, sAlias);

                // Populate the invisible URLs list column
                data[iCnt][3] = (Vector<String>) urlMap.get(sAlias);   
                
                // Populate the invisible alias column
                data[iCnt][4] = sAlias;          
            }
        }
        catch (CMException cme){
            throw (new CMException(cme.getMessage()));
        }
        catch (CMNotInitialisedException cmni) {
        	// Should not realy happen - we have initialised the Credential Manager
        	throw (new CMException(cmni.getMessage()));
        }	

        fireTableDataChanged();
    }
    
    /**
     * Get the map of URL lists associated to key pair aliases.
     *
     * @return The map of aliases and URLs.
     */
    public HashMap<String, Vector<String>> getUrlMap()
    {
        return urlMap;
    }
    
    /**
     * Get the number of columns in the table.
     *
     * @return The number of columns
     */
    public int getColumnCount()
    {
        return columnNames.length;
    }

    /**
     * Get the number of rows in the table.
     *
     * @return The number of rows
     */
    public int getRowCount()
    {
        return data.length;
    }

    /**
     * Get the name of the column at the given position.
     *
     * @param iCol The column position
     * @return The column name
     */
    public String getColumnName(int iCol)
    {
        return columnNames[iCol];
    }

    /**
     * Get the cell value at the given row and column position.
     *
     * @param iRow The row position
     * @param iCol The column position
     * @return The cell value
     */
    public Object getValueAt(int iRow, int iCol)
    {
        return data[iRow][iCol];
    }

    /**
     * Get the class at of the cells at the given column position.
     *
     * @param iCol The column position
     * @return The column cells' class
     */
    public Class<? extends Object> getColumnClass(int iCol)
    {
        return getValueAt(0, iCol).getClass();
    }

    /**
     * Is the cell at the given row and column position editable?
     *
     * @param iRow The row position
     * @param iCol The column position
     * @return True if the cell is editable, false otherwise
     */
    public boolean isCellEditable(int iRow, int iCol)
    {
        // The table is always read-only
        return false;
    }    

}

