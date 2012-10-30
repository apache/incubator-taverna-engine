/**
 * 
 */
package uk.org.taverna.platform.execution.impl.local;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;
import net.sf.taverna.t2.reference.ValueCarryingExternalReference;
import net.sf.taverna.t2.reference.impl.external.file.FileReference;
import net.sf.taverna.t2.reference.impl.external.http.HttpReference;
import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.DataLocation;
import uk.org.taverna.platform.data.api.DataNature;
import uk.org.taverna.platform.data.api.DataService;
import uk.org.taverna.platform.data.api.DataTools;

/**
 * @author alanrw
 *
 */
public class T2ReferenceConverter {
	
	public static Data convertReferenceToData(T2Reference reference,
			ReferenceService referenceService, InvocationContext context, DataService service) throws IOException, URISyntaxException {
		Data resultData = service.create(DataNature.NOT_YET_ARRIVED, reference.getLocalPart());
		
		// This does not cope with an already existing Data
		
		if (reference.getReferenceType() == T2ReferenceType.ReferenceSet) {

			ReferenceSet rs = referenceService.getReferenceSetService().getReferenceSet(reference);
			if (rs == null) {
				throw new ReferenceServiceException("Could not find ReferenceSet " + reference);
			}
			// Check that there are references in the set
			if (rs.getExternalReferences().isEmpty()) {
				throw new ReferenceServiceException("ReferenceSet " + reference + " is empty");
			}

			ReferencedDataNature dataNature = ReferencedDataNature.UNKNOWN;
			for (ExternalReferenceSPI ers : rs.getExternalReferences()) {
				ReferencedDataNature erDataNature = ers.getDataNature();
				
				// Set the data nature if it has not been set
				if (resultData.hasDataNature(DataNature.NOT_YET_ARRIVED) && !erDataNature.equals(ReferencedDataNature.UNKNOWN)) {
					if (dataNature == ReferencedDataNature.TEXT) {
						resultData = service.create(DataNature.TEXT_VALUE);
					} else if (dataNature == ReferencedDataNature.BINARY) {
						resultData = service.create(DataNature.BINARY_VALUE);
					}
				}
				
				if (ers instanceof ValueCarryingExternalReference) {
					if (!resultData.hasExplicitValue()) {
						resultData.setExplicitValue(((ValueCarryingExternalReference) ers).getValue());
					}
				} else if (ers instanceof FileReference) {
							URI uri = ((FileReference) ers).getFile().toURI();
							Charset charset = Charset.forName(ers.getCharset());
							DataTools.addDataReference(service, resultData, uri, charset);
				} else if (ers instanceof HttpReference) {
					URI uri = ((HttpReference) ers).getHttpUrl().toURI();
					Charset charset = Charset.forName(ers.getCharset());
					DataTools.addDataReference(service, resultData, uri, charset);
				} else {
					if (!resultData.hasExplicitValue()) {
					if (dataNature.equals(ReferencedDataNature.TEXT)) {
						resultData.setExplicitValue(referenceService.renderIdentifier(reference, String.class, context));
					} else {
						resultData.setExplicitValue(referenceService.renderIdentifier(reference, byte[].class, context));
					}
					}
				}
			}

		} else if (reference.getReferenceType() == T2ReferenceType.ErrorDocument) {
			resultData.setDataNature(DataNature.WILL_NOT_COME);
		} else { // it is an IdentifiedList<T2Reference>
			resultData.setDataNature(DataNature.LIST);
			IdentifiedList<T2Reference> identifiedList = referenceService.getListService().getList(
					reference);
			List<Data> list = new ArrayList<Data>();

			for (int j = 0; j < identifiedList.size(); j++) {
				T2Reference ref = identifiedList.get(j);
				list.add(convertReferenceToData(ref, referenceService, context, service));
			}
			resultData.setElements(list);
		}		
		return resultData;
	}

	public static DataLocation convertReferenceToDataLocation(T2Reference reference,
			ReferenceService referenceService, InvocationContext context, DataService service) throws IOException, URISyntaxException {
		Data d = convertReferenceToData(reference, referenceService, context, service);
		return service.getDataLocation(d);
	}
}
