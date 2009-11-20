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
public class IterationType  implements ProvenanceEventType {
    private PortsSequenceType inputdata;

    private PortsSequenceType outputdata;

    private java.lang.String id;  // attribute

    public IterationType() {
    }

    public IterationType(
           PortsSequenceType inputdata,
           PortsSequenceType outputdata,
           java.lang.String id) {
           this.inputdata = inputdata;
           this.outputdata = outputdata;
           this.id = id;
    }


    /**
     * Gets the inputdata value for this IterationType.
     * 
     * @return inputdata
     */
    public PortsSequenceType getInputdata() {
        return inputdata;
    }


    /**
     * Sets the inputdata value for this IterationType.
     * 
     * @param inputdata
     */
    public void setInputdata(PortsSequenceType inputdata) {
        this.inputdata = inputdata;
    }


    /**
     * Gets the outputdata value for this IterationType.
     * 
     * @return outputdata
     */
    public PortsSequenceType getOutputdata() {
        return outputdata;
    }


    /**
     * Sets the outputdata value for this IterationType.
     * 
     * @param outputdata
     */
    public void setOutputdata(PortsSequenceType outputdata) {
        this.outputdata = outputdata;
    }


    /**
     * Gets the id value for this IterationType.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this IterationType.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }

}
