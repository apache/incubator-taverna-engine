package cnt;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import rdfs.subClassOf;
import tavernaprov.Content;

/** The base64 encoded content (can be used for binary content). */
@label({"Base64 content"})
@subClassOf({"http://ns.taverna.org.uk/2012/tavernaprov/Content", "http://www.w3.org/2011/content#Content"})
@comment({"The base64 encoded content (can be used for binary content)."})
@Iri("http://www.w3.org/2011/content#ContentAsBase64")
public interface ContentAsBase64 extends Content, cnt.Content {
	/** The Base64 encoded byte sequence of the content. */
	@label({"Base64 encoded byte sequence"})
	@comment({"The Base64 encoded byte sequence of the content."})
	@Iri("http://www.w3.org/2011/content#bytes")
	Set<byte[]> getCntBytes();
	/** The Base64 encoded byte sequence of the content. */
	@label({"Base64 encoded byte sequence"})
	@comment({"The Base64 encoded byte sequence of the content."})
	@Iri("http://www.w3.org/2011/content#bytes")
	void setCntBytes(Set<? extends byte[]> cntBytes);

}
