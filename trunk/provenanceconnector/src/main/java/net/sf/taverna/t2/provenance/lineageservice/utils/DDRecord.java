/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice.utils;

/**
 * a simple bean to hold a database record from the DD table
 * @author paolo
 *
 */
public class DDRecord {

	String  PFrom;
	String  PTo;
	String  vTo;
	String  valTo;
	String  vFrom;
	String  valFrom;
	String iteration;
	public boolean isInput;
	
	public String toString() {
		return new String("proc: "+PFrom+" vFrom: "+vFrom+" valFrom: "+valFrom+"PTo: "+PTo+" vTo: "+vTo+" valTo: "+valTo);
	}
	
	
	/**
	 * @return the vTo
	 */
	public String getVTo() {
		return vTo;
	}
	/**
	 * @param to the vTo to set
	 */
	public void setVTo(String to) {
		vTo = to;
	}
	/**
	 * @return the valTo
	 */
	public String getValTo() {
		return valTo;
	}
	/**
	 * @param valTo the valTo to set
	 */
	public void setValTo(String valTo) {
		this.valTo = valTo;
	}
	/**
	 * @return the vFrom
	 */
	public String getVFrom() {
		return vFrom;
	}
	/**
	 * @param from the vFrom to set
	 */
	public void setVFrom(String from) {
		vFrom = from;
	}
	/**
	 * @return the valFrom
	 */
	public String getValFrom() {
		return valFrom;
	}
	/**
	 * @param valFrom the valFrom to set
	 */
	public void setValFrom(String valFrom) {
		this.valFrom = valFrom;
	}


	/**
	 * @return the isInput
	 */
	public boolean isInput() {
		return isInput;
	}


	/**
	 * @param isInput the isInput to set
	 */
	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}


	/**
	 * @return the pFrom
	 */
	public String getPFrom() {
		return PFrom;
	}


	/**
	 * @param from the pFrom to set
	 */
	public void setPFrom(String from) {
		PFrom = from;
	}


	/**
	 * @return the pTo
	 */
	public String getPTo() {
		return PTo;
	}


	/**
	 * @param to the pTo to set
	 */
	public void setPTo(String to) {
		PTo = to;
	}


	/**
	 * @return the iteration
	 */
	public String getIteration() {
		return iteration;
	}


	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(String iteration) {
		this.iteration = iteration;
	}
	
}
