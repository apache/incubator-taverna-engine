package cnt;

import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import rdfs.subClassOf;
import tavernaprov.Content;

/** The text content (can be used for text content). */
@label({"Text content"})
@subClassOf({"http://ns.taverna.org.uk/2012/tavernaprov/Content", "http://www.w3.org/2011/content#Content"})
@comment({"The text content (can be used for text content)."})
@Iri("http://www.w3.org/2011/content#ContentAsText")
public interface ContentAsText extends Content, cnt.Content {
	/** The character sequence of the text content. */
	@label({"Character sequence"})
	@comment({"The character sequence of the text content."})
	@Iri("http://www.w3.org/2011/content#chars")
	Set<Object> getCntChars();
	/** The character sequence of the text content. */
	@label({"Character sequence"})
	@comment({"The character sequence of the text content."})
	@Iri("http://www.w3.org/2011/content#chars")
	void setCntChars(Set<?> cntChars);

}
