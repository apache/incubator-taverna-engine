/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.provenance.lineageservice.utils.QueryVar;

/**
 * @author paolo
 *
 */
public class QueryAnswer {

	NativeAnswer nativeAnswer;
	
	String _OPMAnswer_AsXML, _OPMAnswer_AsRDF;
	
	/**
	 * @return the pathsToDependencies
	 */
	public NativeAnswer getNativeAnswer() { return nativeAnswer; }

	/**
	 * @param pathsToDependencies the pathsToDependencies to set
	 */
	public void setNativeAnswer(NativeAnswer a) { this.nativeAnswer= a; }

	/**
	 * @return the oPM_AsXML
	 */
	public String getOPMAnswer_AsXML() {
		return _OPMAnswer_AsXML;
	}

	/**
	 * @param asXML the oPM_AsXML to set
	 */
	public void setOPMAnswer_AsXML(String asXML) {
		_OPMAnswer_AsXML = asXML;
	}

	/**
	 * @return the oPM_AsRDF
	 */
	public String getOPMAnswer_AsRDF() {
		return _OPMAnswer_AsRDF;
	}

	/**
	 * @param asRDF the oPM_AsRDF to set
	 */
	public void setOPMAnswer_AsRDF(String asRDF) {
		_OPMAnswer_AsRDF = asRDF;
	}

}
