/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice.utils;

/**
 * @author paolo
 *
 */	
public class QueryVar {
	
	 String wfInstanceId;
	 String wfName;
	 String pname;
	 String vname;
	 String path;
	 String value;

	 /**
	  * @return the pname
	  */
	 public String getPname() {
		 return pname;
	 }
	 /**
	  * @param pname the pname to set
	  */
	 public void setPname(String pname) {
		 this.pname = pname;
	 }
	 /**
	  * @return the vname
	  */
	 public String getVname() {
		 return vname;
	 }
	 /**
	  * @param vname the vname to set
	  */
	 public void setVname(String vname) {
		 this.vname = vname;
	 }
	 /**
	  * @return the path
	  */
	 public String getPath() {
		 return path;
	 }
	 /**
	  * @param path the path to set
	  */
	 public void setPath(String path) {
		 this.path = path;
	 }
	 /**
	  * @return the value
	  */
	 public String getValue() {
		 return value;
	 }
	 /**
	  * @param value the value to set
	  */
	 public void setValue(String value) {
		 this.value = value;
	 }
	/**
	 * @return the wfInstanceId
	 */
	public String getWfInstanceId() {
		return wfInstanceId;
	}
	/**
	 * @param wfInstanceId the wfInstanceId to set
	 */
	public void setWfInstanceId(String wfInstanceId) {
		this.wfInstanceId = wfInstanceId;
	}
	/**
	 * @return the wfName
	 */
	public String getWfName() {
		return wfName;
	}
	/**
	 * @param wfName the wfName to set
	 */
	public void setWfName(String wfName) {
		this.wfName = wfName;
	}

 }