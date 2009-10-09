/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author paolo
 *
 */
public class ProvenanceQueryParser {

	private static Logger logger = Logger.getLogger(ProvenanceQueryParser.class);

	private static final String RUNS_TAG = "runs";
	private static final String RUN_ID = "id";
	private static final Object RUN_TAG = "run";
	private static final Object RANGE_TAG = "range";
	private static final String RANGE_FROM = "from";
	private static final String RANGE_TO = "to";

	/**
	 * 
	 * @param XMLQuerySpec A string representation of the XML provenace query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Query parseProvenanceQuery(String XMLQuerySpecFilename) {

		List<String> runsScope = new ArrayList<String>();  // list of run IDs

		Document d=null; 

		// parse the XML using JDOM
		// new code to read off an XML spec of the query
		SAXBuilder  b = new SAXBuilder();
		try {
			d = b.build (new FileReader((XMLQuerySpecFilename)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Element root = d.getRootElement();
		
		/////
		// process runs scope
		/////
		Element runs = root.getChild(RUNS_TAG);
		if (runs != null) {
			logger.debug("setting runs scope");

			// expect a sequence of run elements and/ or a single <range> element
			List<Element> runElList = runs.getChildren();
			for (Element runEl:runElList) {
				
				if (runEl.getName().equals(RUN_TAG)) {
					String runID = runEl.getAttributeValue(RUN_ID);
					if (runID!=null) {
						logger.debug("adding runID "+runID+" to runs scope");
						runsScope.add(runID);
					} else {
						logger.warn("<run> element with no ID");
					}					
				} else if (runEl.getName().equals(RANGE_TAG)) {
					String from = runEl.getAttributeValue(RANGE_FROM);
					String to   = runEl.getAttributeValue(RANGE_TO);
					
					logger.debug("processing runs range from "+from+" to "+to);
				}
			}
		} else {
			logger.debug("null runs scope: using latest run");
		}

//		String processID = root.getAttributeValue("processID"); // this is

//		Element outputDataEl = root.getChild("outputdata");

		return null;  // TODO
	}
}
