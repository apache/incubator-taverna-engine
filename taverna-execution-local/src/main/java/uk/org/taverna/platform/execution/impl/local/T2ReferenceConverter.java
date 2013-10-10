/**
 *
 */
package uk.org.taverna.platform.execution.impl.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.StackTraceElementBean;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;
import net.sf.taverna.t2.reference.impl.external.file.FileReference;
import net.sf.taverna.t2.reference.impl.external.http.HttpReference;
import net.sf.taverna.t2.results.ResultsUtils;
import uk.org.taverna.databundle.DataBundles;

/**
 * @author alanrw
 *
 */
public class T2ReferenceConverter {

	public static Object convertPathToObject(Path path) throws IOException {
		Object object = null;
		if (DataBundles.isValue(path)) {
			object = DataBundles.getStringValue(path);
		} else if (DataBundles.isReference(path)) {
			URI reference = DataBundles.getReference(path);
			String scheme = reference.getScheme();
			if ("file".equals(scheme)) {
				object = new File(reference);
			} else {
				object = reference.toURL();
			}
		} else if (DataBundles.isList(path)) {
			List<Path> list = DataBundles.getList(path);
			List<Object> objectList = new ArrayList<Object>(list.size());
			for (Path pathElement : list) {
				objectList.add(convertPathToObject(pathElement));
			}
			object = objectList;
		}
		return object;
	}

	public static void convertReferenceToPath(Path path, T2Reference reference,
			ReferenceService referenceService, InvocationContext context) throws IOException, URISyntaxException {

		if (reference.getReferenceType() == T2ReferenceType.ReferenceSet) {
			ReferenceSet rs = referenceService.getReferenceSetService().getReferenceSet(reference);
			if (rs == null) {
				throw new ReferenceServiceException("Could not find ReferenceSet " + reference);
			}
			// Check that there are references in the set
			if (rs.getExternalReferences().isEmpty()) {
				throw new ReferenceServiceException("ReferenceSet " + reference + " is empty");
			}

			for (ExternalReferenceSPI ers : rs.getExternalReferences()) {
				if (ers instanceof FileReference) {
					URI uri = ((FileReference) ers).getFile().toURI();
					DataBundles.setReference(path, uri);
				} else if (ers instanceof HttpReference) {
					URI uri = ((HttpReference) ers).getHttpUrl().toURI();
					DataBundles.setReference(path, uri);
				} else {
					try (InputStream in = ers.openStream(context)) {
						Files.copy(in, path);
					}
				}
			}
		} else if (reference.getReferenceType() == T2ReferenceType.ErrorDocument) {
			ErrorDocument errorDocument = referenceService.getErrorDocumentService().getError(reference);
			String message = errorDocument.getMessage();
			StringBuilder trace = new StringBuilder();
			if (errorDocument.getExceptionMessage() != null
					&& !errorDocument.getExceptionMessage().isEmpty()) {
				trace.append(errorDocument.getExceptionMessage());
				trace.append("\n");
			}
			List<StackTraceElementBean> stackTraceStrings = errorDocument.getStackTraceStrings();
			for (StackTraceElementBean stackTraceElement : stackTraceStrings) {
				trace.append(ResultsUtils.getStackTraceElementString(stackTraceElement));
				trace.append("\n");
			}
			List<Path> causes = new ArrayList<>();
			// TODO intermediate values
//			for (T2Reference errorReference : errorDocument.getErrorReferences()) {
//				causes.add(convertReferenceToPath(errorReference, referenceService, context));
//			}
			DataBundles.setError(path, message, trace.toString(), causes.toArray(new Path[causes.size()]));
		} else { // it is an IdentifiedList<T2Reference>
			IdentifiedList<T2Reference> identifiedList = referenceService.getListService().getList(
					reference);
			DataBundles.createList(path);
			for (T2Reference ref : identifiedList) {
				convertReferenceToPath(DataBundles.newListItem(path), ref, referenceService, context);
			}
		}
	}

}
