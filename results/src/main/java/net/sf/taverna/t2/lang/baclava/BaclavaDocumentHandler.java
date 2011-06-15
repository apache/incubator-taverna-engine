package net.sf.taverna.t2.lang.baclava;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.lang.results.ResultsUtils;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.apache.log4j.Logger;
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
	
	private static Logger logger = Logger.getLogger(BaclavaDocumentHandler.class);

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
		Map<String, Object> resultMap = new HashMap<String, Object>();
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
	
	
	/**
	 * Converts a T2Reference pointing to results to 
	 * a list of (lists of ...) dereferenced result object.
	 */
	private Object convertReferenceToObject(T2Reference reference) {				
	
			if (reference.getReferenceType() == T2ReferenceType.ReferenceSet){
				// Dereference the object
				Object dataValue;
				try{
					try {
						dataValue = referenceService.renderIdentifier(reference, String.class, context);
					}
					catch (ReferenceServiceException e) {
						dataValue = referenceService.renderIdentifier(reference, byte[].class, context);
					}
				}
				catch(ReferenceServiceException rse){
					String message = "Problem rendering T2Reference in convertReferencesToObjects().";
					logger.error("BaclavaDocumentHandler Error: "+ message, rse);
					throw rse;
				}
				return dataValue;
			}
			else if (reference.getReferenceType() == T2ReferenceType.ErrorDocument){
				// Dereference the ErrorDocument and convert it to some string representation
				ErrorDocument errorDocument = (ErrorDocument)referenceService.resolveIdentifier(reference, null, context);
				String errorString = ResultsUtils.buildErrorDocumentString(errorDocument, context);
				return errorString;
			}
			else { // it is an IdentifiedList<T2Reference> - go recursively
				IdentifiedList<T2Reference> identifiedList = referenceService.getListService().getList(reference);
				List<Object> list = new ArrayList<Object>();
				
				for (int j=0; j<identifiedList.size(); j++){
					T2Reference ref = identifiedList.get(j);
					list.add(convertReferenceToObject(ref));
				}
				return list;
			}	
	}	
	
	protected Object getObjectForName(String name) {
		Object result = null;
		if (getChosenReferences().containsKey(name)) {
			result = convertReferenceToObject(getChosenReferences().get(name));
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
