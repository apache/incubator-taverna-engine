/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.lang.results;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ErrorDocumentService;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ListService;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.StackTraceElementBean;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;
import net.sf.taverna.t2.reference.ValueCarryingExternalReference;

import org.apache.log4j.Logger;
import org.clapper.util.misc.MIMETypeUtil;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;

/**
 * Convenience methods for displaying and storing workflow run results. For
 * example, converting result error documents into various representations (e.g.
 * StringS or JTreeS), getting MIME type of result objects, etc.
 * 
 * @author Alex Nenadic
 * 
 */
public class ResultsUtils {

	private static Logger logger = Logger.getLogger(ResultsUtils.class);
	
	/**
	 * Converts a T2Reference pointing to results to 
	 * a list of (lists of ...) dereferenced result object.
	 * @param context 
	 * @param referenceService 
	 */
	public static Object convertReferenceToObject(T2Reference reference, ReferenceService referenceService, InvocationContext context) {				
	
			if (reference.getReferenceType() == T2ReferenceType.ReferenceSet){
				
				ReferenceSet rs = referenceService.getReferenceSetService().getReferenceSet(reference);
				if (rs == null) {
					throw new ReferenceServiceException("Could not find ReferenceSet " + reference);
				}
				// Check that there are references in the set
				if (rs.getExternalReferences().isEmpty()) {
					throw new ReferenceServiceException(
							"Can't render an empty reference set to a POJO");
				}

				ReferencedDataNature dataNature = ReferencedDataNature.UNKNOWN;
				for (ExternalReferenceSPI ers : rs.getExternalReferences()) {
					if (dataNature.equals(ReferencedDataNature.UNKNOWN)) {
						dataNature = ers.getDataNature();
						break;
					}
				}

				// Dereference the object
				Object dataValue;
				try{
					if (dataNature.equals(ReferencedDataNature.TEXT)) {
						dataValue = referenceService.renderIdentifier(reference, String.class, context);
					} else {
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
					list.add(convertReferenceToObject(ref,referenceService,context));
				}
				return list;
			}	
	}	

	/**
	 * Creates a string representation of the ErrorDocument.
	 */
	public static String buildErrorDocumentString(ErrorDocument errDocument,
			InvocationContext context) {

		String errDocumentString = "";

		String exceptionMessage = errDocument.getExceptionMessage();
		if (exceptionMessage != null && !exceptionMessage.equals("")) {
			DefaultMutableTreeNode exceptionMessageNode = new DefaultMutableTreeNode(
					exceptionMessage);
			errDocumentString += exceptionMessageNode + "\n";
			List<StackTraceElementBean> stackTrace = errDocument
					.getStackTraceStrings();
			if (stackTrace.size() > 0) {
				for (StackTraceElementBean stackTraceElement : stackTrace) {
					errDocumentString += getStackTraceElementString(stackTraceElement)
							+ "\n";
				}
			}

		}

		Set<T2Reference> errorReferences = errDocument.getErrorReferences();
		if (!errorReferences.isEmpty()) {
			errDocumentString += "Set of ErrorDocumentS to follow." + "\n";
		}
		int errorCounter = 1;
		int listCounter = 0;
		for (T2Reference reference : errorReferences) {
			if (reference.getReferenceType().equals(
					T2ReferenceType.ErrorDocument)) {
				ErrorDocumentService errorDocumentService = context
						.getReferenceService().getErrorDocumentService();
				ErrorDocument causeErrorDocument = errorDocumentService
						.getError(reference);
				if (listCounter == 0) {
					errDocumentString += "ErrorDocument " + (errorCounter++)
							+ "\n";
				} else {
					errDocumentString += "ErrorDocument " + listCounter + "."
							+ (errorCounter++) + "\n";
				}
				errDocumentString += buildErrorDocumentString(
						causeErrorDocument, context)
						+ "\n";
			} else if (reference.getReferenceType().equals(
					T2ReferenceType.IdentifiedList)) {
				List<ErrorDocument> errorDocuments = getErrorDocuments(
						reference, context.getReferenceService());
				errDocumentString += "ErrorDocument list " + (++listCounter)
						+ "\n";
				for (ErrorDocument causeErrorDocument : errorDocuments) {
					errDocumentString += buildErrorDocumentString(
							causeErrorDocument, context)
							+ "\n";
				}
			}
		}

		return errDocumentString;
	}

	public static void buildErrorDocumentTree(DefaultMutableTreeNode node,
			ErrorDocument errorDocument, ReferenceService referenceService) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(errorDocument);
		String exceptionMessage = errorDocument.getExceptionMessage();
		if (exceptionMessage != null && !exceptionMessage.equals("")) {
			DefaultMutableTreeNode exceptionMessageNode = new DefaultMutableTreeNode(
					exceptionMessage);
			child.add(exceptionMessageNode);
			List<StackTraceElementBean> stackTrace = errorDocument
					.getStackTraceStrings();
			if (stackTrace.size() > 0) {
				for (StackTraceElementBean stackTraceElement : stackTrace) {
					exceptionMessageNode.add(new DefaultMutableTreeNode(
							getStackTraceElementString(stackTraceElement)));
				}
			}

		}
		node.add(child);

		Set<T2Reference> errorReferences = errorDocument.getErrorReferences();
		for (T2Reference reference : errorReferences) {
			if (reference.getReferenceType().equals(
					T2ReferenceType.ErrorDocument)) {
				ErrorDocumentService errorDocumentService = referenceService
						.getErrorDocumentService();
				ErrorDocument causeErrorDocument = errorDocumentService
						.getError(reference);
				if (errorReferences.size() == 1) {
					buildErrorDocumentTree(node, causeErrorDocument, referenceService);
				} else {
					buildErrorDocumentTree(child, causeErrorDocument, referenceService);
				}
			} else if (reference.getReferenceType().equals(
					T2ReferenceType.IdentifiedList)) {
				List<ErrorDocument> errorDocuments = getErrorDocuments(
						reference, referenceService);
				if (errorDocuments.size() == 1) {
					buildErrorDocumentTree(node, errorDocuments.get(0), referenceService);
				} else {
					for (ErrorDocument errorDocument2 : errorDocuments) {
						buildErrorDocumentTree(child, errorDocument2, referenceService);
					}
				}
			}
		}
	}

	private static String getStackTraceElementString(
			StackTraceElementBean stackTraceElement) {
		StringBuilder sb = new StringBuilder();
		sb.append(stackTraceElement.getClassName());
		sb.append('.');
		sb.append(stackTraceElement.getMethodName());
		if (stackTraceElement.getFileName() == null) {
			sb.append("(unknown file)");
		} else {
			sb.append('(');
			sb.append(stackTraceElement.getFileName());
			sb.append(':');
			sb.append(stackTraceElement.getLineNumber());
			sb.append(')');
		}
		return sb.toString();
	}

	public static List<ErrorDocument> getErrorDocuments(T2Reference reference,
			ReferenceService referenceService) {
		List<ErrorDocument> errorDocuments = new ArrayList<ErrorDocument>();
		if (reference.getReferenceType().equals(T2ReferenceType.ErrorDocument)) {
			ErrorDocumentService errorDocumentService = referenceService
					.getErrorDocumentService();
			errorDocuments.add(errorDocumentService.getError(reference));
		} else if (reference.getReferenceType().equals(
				T2ReferenceType.IdentifiedList)) {
			ListService listService = referenceService.getListService();
			IdentifiedList<T2Reference> list = listService.getList(reference);
			for (T2Reference listReference : list) {
				errorDocuments
						.addAll(getErrorDocuments(listReference, referenceService));
			}
		}
		return errorDocuments;
	}

	@SuppressWarnings("unchecked")
	public static List<MimeType> getMimeTypes(
			ExternalReferenceSPI externalReference, InvocationContext context) {
		List<MimeType> mimeList = new ArrayList<MimeType>();
		MimeUtil2 mimeUtil = new MimeUtil2();
		mimeUtil
				.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
		mimeUtil
				.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		mimeUtil
				.registerMimeDetector("eu.medsea.mimeutil.detector.WindowsRegistryMimeDetector");
		mimeUtil
				.registerMimeDetector("eu.medsea.mimeutil.detector.ExtraMimeTypes");
		InputStream inputStream = externalReference.openStream(context);
		try {
			byte[] bytes = new byte[2048]; // need to read this much if we want to detect SVG using the hack below
			inputStream.read(bytes);
			Collection mimeTypes2 = mimeUtil.getMimeTypes(bytes);
			mimeList.addAll(mimeTypes2);
			
			// Hack for SVG that seems not to be recognised
			String bytesString = new String(bytes, "UTF-8");
			if (bytesString.contains("http://www.w3.org/2000/svg")){
				MimeType svgMimeType = new MimeType("image/svg+xml");
				if (!mimeList.contains(svgMimeType)){
					mimeList.add(svgMimeType);
				}
			}
			
		} catch (IOException e) {
			logger.error("Failed to read from stream to determine mimetype", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.error(
						"Failed to close stream after determining mimetype", e);
			}
		}

		return mimeList;
	}

	public static String getExtension(String mimeType) {

		String mimeTypeForFileExtension = MIMETypeUtil
				.fileExtensionForMIMEType(mimeType);
		return mimeTypeForFileExtension;
	}
}
