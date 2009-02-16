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
import java.text.DateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom cell renderer for the cells of the tables displaying Keystore/Truststore contents.
 * 
 * @author Alexandra Nenadic
 */
public class TableCellRenderer
    extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = -3983986682794010259L;

	private final ImageIcon passwordEntryIcon = new ImageIcon(TableCellRenderer.class.getResource(
	"/images/table/key_entry.png"));
	
	private final ImageIcon keypairEntryIcon = new ImageIcon(TableCellRenderer.class.getResource(
	"/images/table/keypair_entry.png"));
	
	private final ImageIcon trustcertEntryIcon = new ImageIcon(TableCellRenderer.class.getResource(
	"/images/table/trustcert_entry.png"));

    /**
     * Returns the rendered cell for the supplied value and column.
     *
     * @param jtKeyStore The JTable
     * @param value The value to assign to the cell
     * @param bIsSelected True if cell is selected
     * @param iRow The row of the cell to render
     * @param iCol The column of the cell to render
     * @param bHasFocus If true, render cell appropriately
     * @return The renderered cell
     */
    public Component getTableCellRendererComponent(JTable jtKeyStoreTable,
        Object value, boolean bIsSelected, boolean bHasFocus, int iRow,
        int iCol)
    {
        JLabel cell = (JLabel) super.getTableCellRendererComponent(jtKeyStoreTable,
            value, bIsSelected, bHasFocus, iRow, iCol);

        if (value != null){
            // Type column - display an icon representing the type
            if (iCol == 0) {
                ImageIcon icon = null;
                //The cell is in the first column of Passwords table
                if (CredentialManagerGUI.PASSWORD_ENTRY_TYPE.equals(value)) { 
                    icon = passwordEntryIcon; //key (i.e. password) entry image
                }
                // The cell is in the first column of Key Pairs table
                else if (CredentialManagerGUI.KEY_PAIR_ENTRY_TYPE.equals(value)) { 
                    icon = keypairEntryIcon; //key pair entry image
                }
                //The cell is in the first column of Trusted Certificates table
                else if (CredentialManagerGUI.TRUST_CERT_ENTRY_TYPE.equals(value)) { 
                    icon = trustcertEntryIcon; //trust. certificate entry image
                }

                cell.setIcon(icon);
                cell.setText("");
                cell.setVerticalAlignment(CENTER);
                cell.setHorizontalAlignment(CENTER);
                
            }
            // Last Modified column - format date (if date supplied)        
            else if (((jtKeyStoreTable.getModel() instanceof PasswordsTableModel) && (iCol == 3)) || 
            	((jtKeyStoreTable.getModel() instanceof KeyPairsTableModel) && (iCol == 2))||
            	((jtKeyStoreTable.getModel() instanceof TrustCertsTableModel) && (iCol == 2))){
            	if (value instanceof Date) {
            		// Include timezone
            		cell.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
            			DateFormat.LONG).format((Date) value));
            	}
            	else {
            		cell.setText(value.toString());
            	}
            }
            // Other columns - just use their text
            else { 
            	cell.setText(value.toString());     	
            }
        }

        cell.setBorder(new EmptyBorder(0, 5, 0, 5));

        return cell;
    }
}

