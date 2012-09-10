package cnt;

import java.lang.Object;
import java.net.URI;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;

/** The document type declaration. */
@label({"Document type declaration"})
@comment({"The document type declaration."})
@Iri("http://www.w3.org/2011/content#DoctypeDecl")
public interface DoctypeDecl {
	/** The document type name. */
	@label({"Document type name"})
	@comment({"The document type name."})
	@Iri("http://www.w3.org/2011/content#doctypeName")
	Set<Object> getCntDoctypeNames();
	/** The document type name. */
	@label({"Document type name"})
	@comment({"The document type name."})
	@Iri("http://www.w3.org/2011/content#doctypeName")
	void setCntDoctypeNames(Set<?> cntDoctypeNames);

	/** The internal document type definition subset within the document type declarations. */
	@label({"Internal DTD subset"})
	@comment({"The internal document type definition subset within the document type declarations."})
	@Iri("http://www.w3.org/2011/content#internalSubset")
	Set<Object> getCntInternalSubset();
	/** The internal document type definition subset within the document type declarations. */
	@label({"Internal DTD subset"})
	@comment({"The internal document type definition subset within the document type declarations."})
	@Iri("http://www.w3.org/2011/content#internalSubset")
	void setCntInternalSubset(Set<?> cntInternalSubset);

	/** The document type declarations's public identifier. */
	@label({"Public ID"})
	@comment({"The document type declarations's public identifier."})
	@Iri("http://www.w3.org/2011/content#publicId")
	Set<Object> getCntPublicId();
	/** The document type declarations's public identifier. */
	@label({"Public ID"})
	@comment({"The document type declarations's public identifier."})
	@Iri("http://www.w3.org/2011/content#publicId")
	void setCntPublicId(Set<?> cntPublicId);

	/** The document type declarations's system identifier (typed: xsd:anyURI) */
	@label({"System ID"})
	@comment({"The document type declarations's system identifier (typed: xsd:anyURI)"})
	@Iri("http://www.w3.org/2011/content#systemId")
	Set<URI> getCntSystemId();
	/** The document type declarations's system identifier (typed: xsd:anyURI) */
	@label({"System ID"})
	@comment({"The document type declarations's system identifier (typed: xsd:anyURI)"})
	@Iri("http://www.w3.org/2011/content#systemId")
	void setCntSystemId(Set<? extends URI> cntSystemId);

}
