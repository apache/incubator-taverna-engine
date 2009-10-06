package net.sf.taverna.t2.provenance.lineageservice.utils;

public class Workflow {

	private String wfname;
	private String parentWFname;
	private String externalName;
	
	public void setWfName(String identifier) {
		this.wfname = identifier;
	}

	public String getWfname() {
		return wfname;
	}

	public void setParentWFname(String parentIdentifier) {
		this.parentWFname = parentIdentifier;
	}

	public String getParentWFname() {
		return parentWFname;
	}

	/**
	 * @return the externalName
	 */
	public String getExternalName() {
		return externalName;
	}

	/**
	 * @param externalName the externalName to set
	 */
	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}
	
}
