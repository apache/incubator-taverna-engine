/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.provenance.lineageservice.Dependencies;
import net.sf.taverna.t2.provenance.lineageservice.utils.QueryVar;

/**
 * @author paolo
 *
 */
public class NativeAnswer {
	
	Map<QueryVar, Map<String, List<Dependencies>>>  answer;

	/**
	 * @return the answer
	 */
	public Map<QueryVar, Map<String, List<Dependencies>>> getAnswer() {
		return answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(Map<QueryVar, Map<String, List<Dependencies>>> answer) {
		this.answer = answer;
	}

}
