/**
 * 
 */
package net.sf.taverna.t2.annotation.annotationbeans;

import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AppliesTo;
import net.sf.taverna.t2.workflowmodel.Dataflow;

/**
 * An IdentificationAssertion is used to hold previous identifications of an object.
 * 
 * @author alanrw
 *
 */
@AppliesTo(targetObjectType = { Dataflow.class }, many = false)
public class IdentificationAssertion implements AnnotationBeanSPI {
	
	private String identification;

	/**
	 * @return The identification.  This will be a previous identifier of the annotated object.
	 */
	public String getIdentification() {
		return identification;
	}

	/**
	 * @param identification A previous identified of the annotated object.
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}

}
