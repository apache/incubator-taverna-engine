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

import javax.swing.table.AbstractTableModel;

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CMNotInitialisedException;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;

import javax.crypto.spec.SecretKeySpec;

/**
 * The table model used to display the Keystore's password entries.
 * 
 * @author Alexandra Nenadic
 */
public class PasswordsTableModel
    extends AbstractTableModel
{

	private static final long serialVersionUID = -8220506200070509137L;

	/** Holds the column names */
    private String[] columnNames;

    /** Holds the table data */
    private Object[][] data;

    /**
     * Construct a new PasswordsTableModel.
     */
    public PasswordsTableModel()
    {
    	data = new Object[0][0];
        columnNames = new String[] {
        	"Entry Type", // type of the Keystore entry
        	"Service URL", // the service url, part of the actual alias in the Keystore
        	"Username", // username for the service, part of the password entry in the Keystore
            "Last Modified", // last modified date of the entry
            "Password", // the invisible column holding the password value of the password entry in the Keystore
            "Alias" // the invisible column holding the actual alias in the Keystore
            };
    }
    
    /**
     * Load the PasswordsTableModel with the password entries from the Keystore. 
     *
     * @param cm Credential Manager used as an interface to the Keystore
     * @throws CMException A problem is encountered during accessing the Keystore
     */
    public void load(CredentialManager cm) throws CMException
    {
        // Place password entries' aliases in a tree map to sort them
        TreeMap<String, String> sortedAliases = new TreeMap<String, String>();
        
        try{
        	
        	for (Enumeration<String> en = cm.getAliases(CredentialManager.KEYSTORE); en.hasMoreElements();) {
        		// We are only interested in password entries here
        		// Alias for a password entry is constructed as "password#<SERVICE_URL>"
        		String sAlias = en.nextElement();
             
        		if (sAlias.startsWith("password")){
        			sortedAliases.put(sAlias, sAlias);
        		}
        	}

            // Create one table row for each password entry:
            // each row has 6 fields - type, service url, username, last modified date and 
        	// two invisible field holding the password and the Keystore alias of this entry
            data = new Object[sortedAliases.size()][6];

            // Iterate through the sorted aliases, retrieving the password
            // entries and populating the table model
            int iCnt = 0;
            for (Iterator<Entry<String, String>> itr = sortedAliases.entrySet().iterator(); itr.hasNext(); iCnt++)
            {
                String sAlias = (String) itr.next().getKey();

                // Populate the type column - it is set with an integer
                // but a custom cell renderer will cause a suitable icon
                // to be displayed
                data[iCnt][0] = CredentialManagerGUI.PASSWORD_ENTRY_TYPE;

                // Populate the service url column as a substring of alias 
                // from the first occurence of '#' till the end of the string
                data[iCnt][1] = sAlias.substring(sAlias.indexOf('#')+1);
                
                // Populate the username column - it is contained in the SecretKeySpec 
                // object's key information, prepended to a password and separated by a blank character,
                // i.e. key is constructed as raw bytes from the following string "<USERNAME> <PASSWORD>"
            	SecretKeySpec skspecPassword = cm.getPasswordEntry(sAlias);
            	String key = new String(skspecPassword.getEncoded());
                data[iCnt][2] = key.substring(0,key.indexOf(' '));
               
                // Populate the modified date column ("UBER" keystore type supports creation date)
                data[iCnt][3] = cm.getCreationDate(CredentialManager.KEYSTORE, sAlias);
                
                // Populate the invisible password column
                // Password is contained in the SecretKeySpec's key apended to the 
                // username and separated by a ' '
                data[iCnt][4] = key.substring(key.indexOf(' ')+1); 
                
                data[iCnt][5] = sAlias;
            }
        }
        catch (CMException cme){
            throw (cme);
        }
        catch (CMNotInitialisedException cmni) {
        	// Should not realy happen - we have initialised the Credential Manager
        	throw (new CMException(cmni.getMessage()));
        }

        fireTableDataChanged();
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

