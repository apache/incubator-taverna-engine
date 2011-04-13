/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.provenance.lineageservice.Dependencies;
import net.sf.taverna.t2.provenance.lineageservice.utils.QueryPort;

/**
 * @author Paolo Missier<br/>
 * Java bean used to encapsulate the results of a provenance query. <br/>
 * This takes the form of a nested map, see {@link #getAnswer} for details on its structure.
 *
 */
public class NativeAnswer {
	
	Map<QueryPort, Map<String, List<Dependencies>>>  answer;

	/**
	 * @return a Map of the form: {@link QueryPort}  --> ( &lt;path> --> [ {@link Dependencies} ]) where
	 * <ul>
	 * <li>  {@link QueryVar}  denotes one of the ports in the &lt;select> element of the input query, for example: <em>converter:atlasSlice</em>
	 * <li>  &lt;path>  is the index within the value associated to the port, for instance [1,2,3] or []. 
	 * The inner Map structure accounts for multiple paths, so for example if the query asked for the provenance of elements [1,2] and [2,3] of 
	 * the value bound to <em>converter:atlasSlice</em>, then the inner Map structure contains two entries, one for each of the two paths.
	 * <li> for each such path, the corresponding [{@link Dependencies}] is a list of {@link Dependencies}, each associated with one <it>target processor and port</it>
	 *  mentioned in the input query. For example, for path [1,2] of value bound to  <em>converter:atlasSlice</em>, you may see the following list of Dependencies:
	 *  <ul>
	 *  <li> converter:atlasSlice:[2]
	 *  <li> slicer:atlasAverage[2]
	 *  <li>slicer:atlasAverage[0]
	 *  </ul>
	 *  etc. <br/>
	 *  Each of these elements is described by a Java bean, {@link net.sf.taverna.t2.provenance.lineageservice.LineageQueryResultRecord}, which represents a single provenance data record.  
	 *  This means that the particular value depends on each of these other values that are mentioned in the Dependencies list. 
	 */
	public Map<QueryPort, Map<String, List<Dependencies>>> getAnswer() { return answer; }

	/**
	 * @param sets the query answer, in the format described in {@link #getAnswer()}
	 */
	public void setAnswer(Map<QueryPort, Map<String, List<Dependencies>>> answer) { this.answer = answer; }

}
