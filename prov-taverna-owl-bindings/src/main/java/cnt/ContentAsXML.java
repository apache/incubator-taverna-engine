package cnt;

import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.XMLLiteral;
import rdfs.comment;
import rdfs.label;
import rdfs.subClassOf;

/** The XML content (can only be used for XML-wellformed content). */
@label({"XML content"})
@subClassOf({"http://www.w3.org/2011/content#Content"})
@comment({"The XML content (can only be used for XML-wellformed content)."})
@Iri("http://www.w3.org/2011/content#ContentAsXML")
public interface ContentAsXML extends Content {
	/** The character encoding declared in the XML declaration. */
	@label({"XML character encoding"})
	@comment({"The character encoding declared in the XML declaration."})
	@Iri("http://www.w3.org/2011/content#declaredEncoding")
	Set<Object> getCntDeclaredEncoding();
	/** The character encoding declared in the XML declaration. */
	@label({"XML character encoding"})
	@comment({"The character encoding declared in the XML declaration."})
	@Iri("http://www.w3.org/2011/content#declaredEncoding")
	void setCntDeclaredEncoding(Set<?> cntDeclaredEncoding);

	/** The document type declaration. */
	@label({"Document type declaration"})
	@comment({"The document type declaration."})
	@Iri("http://www.w3.org/2011/content#dtDecl")
	Set<DoctypeDecl> getCntDtDecl();
	/** The document type declaration. */
	@label({"Document type declaration"})
	@comment({"The document type declaration."})
	@Iri("http://www.w3.org/2011/content#dtDecl")
	void setCntDtDecl(Set<? extends DoctypeDecl> cntDtDecl);

	/** The XML content preceding the document type declaration. */
	@label({"XML leading misc"})
	@comment({"The XML content preceding the document type declaration."})
	@Iri("http://www.w3.org/2011/content#leadingMisc")
	Set<XMLLiteral> getCntLeadingMisc();
	/** The XML content preceding the document type declaration. */
	@label({"XML leading misc"})
	@comment({"The XML content preceding the document type declaration."})
	@Iri("http://www.w3.org/2011/content#leadingMisc")
	void setCntLeadingMisc(Set<? extends XMLLiteral> cntLeadingMisc);

	/** The XML content following the document type declaration. */
	@label({"XML rest"})
	@comment({"The XML content following the document type declaration."})
	@Iri("http://www.w3.org/2011/content#rest")
	Set<XMLLiteral> getCntRests();
	/** The XML content following the document type declaration. */
	@label({"XML rest"})
	@comment({"The XML content following the document type declaration."})
	@Iri("http://www.w3.org/2011/content#rest")
	void setCntRests(Set<? extends XMLLiteral> cntRests);

	/** The standalone declaration in the XML declaration. */
	@label({"XML standalone document declaration"})
	@comment({"The standalone declaration in the XML declaration."})
	@Iri("http://www.w3.org/2011/content#standalone")
	Set<Object> getCntStandalones();
	/** The standalone declaration in the XML declaration. */
	@label({"XML standalone document declaration"})
	@comment({"The standalone declaration in the XML declaration."})
	@Iri("http://www.w3.org/2011/content#standalone")
	void setCntStandalones(Set<?> cntStandalones);

	/** The XML version declared in the XML declaration. */
	@label({"XML version"})
	@comment({"The XML version declared in the XML declaration."})
	@Iri("http://www.w3.org/2011/content#version")
	Set<Object> getCntVersions();
	/** The XML version declared in the XML declaration. */
	@label({"XML version"})
	@comment({"The XML version declared in the XML declaration."})
	@Iri("http://www.w3.org/2011/content#version")
	void setCntVersions(Set<?> cntVersions);

}
