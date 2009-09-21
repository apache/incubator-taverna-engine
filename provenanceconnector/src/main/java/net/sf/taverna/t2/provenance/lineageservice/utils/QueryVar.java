/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice.utils;

/**
 * @author paolo
 *
 */	
public class QueryVar {
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

 }