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

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom cell renderer for the headers of the tables displaying Keystore/Truststore contents.
 * 
 * @author Alexandra Nenadic
 */
public class TableHeaderRenderer
    extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = -7691713691433621006L;
	
	private final ImageIcon entryTypeIcon = new ImageIcon(TableHeaderRenderer.class.getResource(
	"/images/table/entry_heading.png"));
    
    /**
     * Returns the rendered header cell for the supplied value and column.
     *
     * @param jtKeyStore The JTable
     * @param value The value to assign to the cell
     * @param bIsSelected True if cell is selected
     * @param iRow The row of the cell to render
     * @param iCol The column of the cell to render
     * @param bHasFocus If true, render cell appropriately
     ** @return The renderered cell
     */
    public Component getTableCellRendererComponent(JTable jtKeyStoreTable,
        Object value, boolean bIsSelected, boolean bHasFocus, int iRow,
        int iCol)
    {
        // Get header renderer
        JLabel header = (JLabel) jtKeyStoreTable.getColumnModel().getColumn(iCol).getHeaderRenderer();

        // The entry type header contains an icon
        if (iCol == 0) {
            header.setText("");
            header.setIcon(entryTypeIcon); // entry type icon (header for the first column of the table)
            header.setHorizontalAlignment(CENTER);
            header.setVerticalAlignment(CENTER);
            header.setToolTipText("Entry type");

        }
        // All other headers contain text
        else {
            header.setText((String) value);
            header.setHorizontalAlignment(LEFT);
            
            // Passwords table has 5 colums, Key pairs and Trusted Certificates tables have 3 each
            if (jtKeyStoreTable.getModel() instanceof PasswordsTableModel){
                if (iCol == 1) { //Service URL column
                    header.setToolTipText("Service URL for the password entry");
                }
                else if (iCol == 2 ){ //Username column 
                    header.setToolTipText("Username for the password entry");                	
                }
                else if (iCol == 3){ // Last modified column
                    header.setToolTipText("Password entry's last modification date and time");
                }            	
            }
            else if(jtKeyStoreTable.getModel() instanceof KeyPairsTableModel){
                if (iCol == 1) { //Owner:Serial Number column
                    header.setToolTipText("Certificate's owner and serial number");
                }
                else if(iCol == 2) { // Last modified column
                    header.setToolTipText("Key pair entry's last modification date and time");
                }         	
            }          
            else if(jtKeyStoreTable.getModel() instanceof TrustCertsTableModel){
                if (iCol == 1) { //Owner column
                    header.setToolTipText("Certificate's owner and serial number");
                }
                else if (iCol == 2){ // Last modified column
                    header.setToolTipText("Trusted certificate's last modification date and time");
                }         	
            }         
        }
        header.setBorder(new CompoundBorder(
            new BevelBorder(BevelBorder.RAISED), new EmptyBorder(0, 5, 0, 5)));

        return header;
    }
}


