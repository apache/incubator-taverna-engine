package net.sf.taverna.t2.provenance.lineageservice.utils;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Workflow {

	@Id
	private String wfname;
	
	private String parentWFname;

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
	
}
