/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

/**
 * @author Paolo Missier
 *<p/>Encapsulates a native Java data structure as a well as a String that holds the OPM graph that represents the query answer 
 *
 */
public class QueryAnswer {

	NativeAnswer nativeAnswer;
	
	String _OPMAnswer_AsRDF;
	
	/**
	 * @return the native Java part of the query answer
	 */
	public NativeAnswer getNativeAnswer() { return nativeAnswer; }

	/**
	 * @param sets the query answer
	 */
	public void setNativeAnswer(NativeAnswer a) { this.nativeAnswer= a; }

	/**
	 * @return the OPM graph as RDF/XML string, or null if OPM was inhibited {@see OPM.computeGraph in APIClient.properties}  
	 */
	public String getOPMAnswer_AsRDF() {
		return _OPMAnswer_AsRDF;
	}

	/**
	 * @param set the OPM graph as RDF/XML string
	 */
	public void setOPMAnswer_AsRDF(String asRDF) {
		_OPMAnswer_AsRDF = asRDF;
	}

}
