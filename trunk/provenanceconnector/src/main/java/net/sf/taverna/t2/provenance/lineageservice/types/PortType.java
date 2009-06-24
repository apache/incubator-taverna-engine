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
package net.sf.taverna.t2.provenance.lineageservice.types;

/**
 *
 * @author Paolo Missier
 *
 */
public class PortType  implements ProvenanceEventType {
    private DataDocumentType dataDocument;

    private LiteralType literal;

    private java.lang.String name;  // attribute

    public PortType() {
    }

    public PortType(
           DataDocumentType dataDocument,
           LiteralType literal,
           java.lang.String name) {
           this.dataDocument = dataDocument;
           this.literal = literal;
           this.name = name;
    }


    /**
     * Gets the dataDocument value for this PortType.
     * 
     * @return dataDocument
     */
    public DataDocumentType getDataDocument() {
        return dataDocument;
    }


    /**
     * Sets the dataDocument value for this PortType.
     * 
     * @param dataDocument
     */
    public void setDataDocument(DataDocumentType dataDocument) {
        this.dataDocument = dataDocument;
    }


    /**
     * Gets the literal value for this PortType.
     * 
     * @return literal
     */
    public LiteralType getLiteral() {
        return literal;
    }


    /**
     * Sets the literal value for this PortType.
     * 
     * @param literal
     */
    public void setLiteral(LiteralType literal) {
        this.literal = literal;
    }


    /**
     * Gets the name value for this PortType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this PortType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

}
