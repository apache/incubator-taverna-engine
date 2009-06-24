package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers;

import net.sf.taverna.t2.reference.T2ReferenceType;

class SimplerT2Reference {
	
	private String localPart;
	private String namespacePart;
//	private boolean containsErrors = false;
	private T2ReferenceType referenceType = T2ReferenceType.ReferenceSet;
	private int depth = 0;
	/**
	 * @return the localPart
	 */
	public String getLocalPart() {
		return localPart;
	}
	/**
	 * @param localPart the localPart to set
	 */
	public void setLocalPart(String localPart) {
		this.localPart = localPart;
	}
	/**
	 * @return the namespacePart
	 */
	public String getNamespacePart() {
		return namespacePart;
	}
	/**
	 * @param namespacePart the namespacePart to set
	 */
	public void setNamespacePart(String namespacePart) {
		this.namespacePart = namespacePart;
	}
	/**
	 * @return the containsErrors
	 */
//	public boolean isContainsErrors() {
//		return containsErrors;
//	}
//	/**
//	 * @param containsErrors the containsErrors to set
//	 */
//	public void setContainsErrors(boolean containsErrors) {
//		this.containsErrors = containsErrors;
//	}
	/**
	 * @return the referenceType
	 */
	public T2ReferenceType getReferenceType() {
		return referenceType;
	}
	/**
	 * @param referenceType the referenceType to set
	 */
	public void setReferenceType(T2ReferenceType referenceType) {
		this.referenceType = referenceType;
	}
	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}
	/**
	 * @param depth the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

}