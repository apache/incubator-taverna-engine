/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.provenance.lineageservice.Dependencies;
import net.sf.taverna.t2.provenance.lineageservice.utils.QueryVar;

/**
 * @author Paolo Missier<br/>
 * Java bean used to encapsulate the results of a provenance query
 *
 */
public class NativeAnswer {
	
	Map<QueryVar, Map<String, List<Dependencies>>>  answer;

	/**
	 * @return a Map of the form: {@link QueryVar}  --> ( &lt;path> --> [ {@link Dependencies} ]) where
	 * <ul>
	 * <li>  {@link QueryVar}  denotes one of the ports in the &lt;select> element of the input query
	 * <li>  &lt;path>  is the index within the value associated to the port, for instance [1,2,3] or []. 
	 * Multiple paths may appear if the query asked for the provenance of specific elements within a collection value, for example
	 * <li> [{@link Dependencies}] is a list of {@link Dependencies}, each associated with one target processor and port as requested
	 * in the input query.  
	 */
	public Map<QueryVar, Map<String, List<Dependencies>>> getAnswer() { return answer; }

	/**
	 * @param sets the query answer, in the format described in {@link #getAnswer()}
	 */
	public void setAnswer(Map<QueryVar, Map<String, List<Dependencies>>> answer) { this.answer = answer; }

}
