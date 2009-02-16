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

import javax.swing.table.AbstractTableModel;

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * The table model used to display the Keystore's trusted certificate entries.
 * 
 * @author Alexandra Nenadic
 */
public class TrustCertsTableModel
    extends AbstractTableModel
{
	private static final long serialVersionUID = 8293656530274606048L;

	/** Holds the column names */
    private String[] columnNames;

    /** Holds the table data */
    private Object[][] data;
	
    /**
     * Construct a new TrustCertsTableModel.
     */
    public TrustCertsTableModel()
    {
       	data = new Object[0][0];
        columnNames = new String[] {
        	"Entry Type", // type of the Keystore entry
        	"Owner:Serial Number", // owner's common name and serial number of the public key certificate
            "Last Modified", // last modified date of the entry
            "Alias" // the invisible column holding the actual alias in the Keystore
            };
    }
    	   
    /**
     * Load the TrustCertsTableModel with trusted certificate entries from the Keystore. 
     *
     * @param keyStore The keystore
     * @throws CMException A problem is encountered accessing
     * the keystore's entries
     */
    public void load(CredentialManager cm)
        throws CMException
    {
        // Place trusted certificate entries' aliases in a tree map to sort them
        TreeMap<String, String> sortedAliases = new TreeMap<String, String>();

        try{
        	for (Enumeration<String> en = cm.getAliases(CredentialManager.TRUSTSTORE); en.hasMoreElements();) {
        		String sAlias = en.nextElement();
        		// Alias for a trusted cert. entry is constructed as "trustcert#<CERT_SERIAL_NUMBER>#<CERT_COMMON_NAME>"
        		// Only save trusted certificate entries
        		//if (cm.isCertificateEntry(CredentialManager.TRUSTSTORE, sAlias)){
        		if (sAlias.startsWith("trustcert")){
        			sortedAliases.put(sAlias, sAlias); //Truststore only contains trusted cert entries - no need to check if alias starts with "trustcert" but anyway
        		}
        	}
        	
            // Create one table row for each trusted certificate entry
            // Each row has 4 fields - type, owner name, last modified data and the invisible alias
            data = new Object[sortedAliases.size()][4];

            // Iterate through the sorted aliases, retrieving the trusted certificate 
            // entries and populating the table model
            int iCnt = 0;
            for (Iterator<Entry<String, String>> itr = sortedAliases.entrySet().iterator(); itr.hasNext(); iCnt++)
            {
                String sAlias = (String) itr.next().getKey();

                // Populate the type column - it is set with an integer
                // but a custom cell renderer will cause a suitable icon
                // to be displayed
                data[iCnt][0] = CredentialManagerGUI.TRUST_CERT_ENTRY_TYPE;
                
                // Populate the 'Owner:Serial Number' column extracted from the alias
                data[iCnt][1] = sAlias.substring(sAlias.lastIndexOf('#')+1) + 
                	":" + 
                	sAlias.substring(sAlias.indexOf('#')+1,sAlias.lastIndexOf('#'));
            	
                // Populate the modified date column
                data[iCnt][2] = cm.getCreationDate(CredentialManager.TRUSTSTORE, sAlias);

                // Populate the invisible alias column
                data[iCnt][3] = sAlias;
            }
        }
        catch (Exception e){
            throw (new CMException("Failed to load trusted certificates entries from the Truststore."));
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

