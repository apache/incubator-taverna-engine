package net.sf.taverna.t2.lang.baclava;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.lang.results.ResultsUtils;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;

import org.embl.ebi.escience.baclava.DataThing;
import org.embl.ebi.escience.baclava.factory.DataThingFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Handles the loading and saving of T2Reference data as Baclava documents
 * 
 * @author Stuart Owen
 *
 */

public class BaclavaDocumentHandler {
	
	private Map<String, T2Reference> chosenReferences;
	
	private static Namespace namespace = Namespace.getNamespace("b","http://org.embl.ebi.escience/baclava/0.1alpha");
	
	protected ReferenceService referenceService;
	
	protected InvocationContext context;
	
	//private static Logger logger = Logger.getLogger(BaclavaDocumentHandler.class);

	/**
	 * Saves the result data to an XML Baclava file. 
	 * @throws IOException 
	 */
	public void saveData(File file) throws IOException {	
		// Build the string containing the XML document from the result map
		Document doc = getDataDocument();
	    XMLOutputter xo = new XMLOutputter(Format.getPrettyFormat());
	    String xmlString = xo.outputString(doc);
	    PrintWriter out = new PrintWriter(new FileWriter(file));
	    out.print(xmlString);
	    out.flush();
	    out.close();
	}
	
	/**
	 * Returns a org.jdom.Document from a map of port named to DataThingS containing
	 * the port's results.
	 */
	public Document getDataDocument() {
		Element rootElement = new Element("dataThingMap", namespace);
		Document theDocument = new Document(rootElement);
		// Build the DataThing map from the chosenReferences
		// First convert map of references to objects into a map of real result objects		
		for (String portName : getChosenReferences().keySet()) {
			DataThing thing = DataThingFactory.bake(getObjectForName(portName));
 			Element dataThingElement = new Element("dataThing", namespace);
			dataThingElement.setAttribute("key", portName);
			dataThingElement.addContent(thing.getElement());
			rootElement.addContent(dataThingElement);
		}
		return theDocument;
	}
	
	/**
	 * @param referenceService the referenceService to set
	 */
	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}
	
	/**
	 * Sets the InvocationContext to be used to get the Reference Service to be
	 * used dereference the reference.
	 */
	public void setInvocationContext(InvocationContext context) {
		this.context = context;
	}
	
	protected Object getObjectForName(String name) {
		Object result = null;
		if (getChosenReferences().containsKey(name)) {
			result = ResultsUtils.convertReferenceToObject(getChosenReferences().get(name),referenceService,context);
		}
		if (result == null) {
			result = "null";
		}
		return result;	
	}
			
	private Map<String,T2Reference> getChosenReferences() {
		return chosenReferences;
	}

	public void setChosenReferences(Map<String, T2Reference> chosenReferences) {
		this.chosenReferences = chosenReferences;
	}
}
