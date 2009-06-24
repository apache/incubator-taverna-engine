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
public class ProcessType  implements ProvenanceEventType {
    private ProcessorType[] processor;

    private java.lang.String dataflowID;  // attribute

    private java.lang.String facadeID;  // attribute

    public ProcessType() {
    }

    public ProcessType(
           ProcessorType[] processor,
           java.lang.String dataflowID,
           java.lang.String facadeID) {
           this.processor = processor;
           this.dataflowID = dataflowID;
           this.facadeID = facadeID;
    }


    /**
     * Gets the processor value for this ProcessType.
     * 
     * @return processor
     */
    public ProcessorType[] getProcessor() {
        return processor;
    }


    /**
     * Sets the processor value for this ProcessType.
     * 
     * @param processor
     */
    public void setProcessor(ProcessorType[] processor) {
        this.processor = processor;
    }

    public ProcessorType getProcessor(int i) {
        return this.processor[i];
    }

    public void setProcessor(int i, ProcessorType _value) {
        this.processor[i] = _value;
    }


    /**
     * Gets the dataflowID value for this ProcessType.
     * 
     * @return dataflowID
     */
    public java.lang.String getDataflowID() {
        return dataflowID;
    }


    /**
     * Sets the dataflowID value for this ProcessType.
     * 
     * @param dataflowID
     */
    public void setDataflowID(java.lang.String dataflowID) {
        this.dataflowID = dataflowID;
    }


    /**
     * Gets the facadeID value for this ProcessType.
     * 
     * @return facadeID
     */
    public java.lang.String getFacadeID() {
        return facadeID;
    }


    /**
     * Sets the facadeID value for this ProcessType.
     * 
     * @param facadeID
     */
    public void setFacadeID(java.lang.String facadeID) {
        this.facadeID = facadeID;
    }

}
