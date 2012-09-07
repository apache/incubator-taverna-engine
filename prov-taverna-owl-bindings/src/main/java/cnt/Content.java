package cnt;

import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;

/** The content. */
@label({"Content"})
@comment({"The content."})
@Iri("http://www.w3.org/2011/content#Content")
public interface Content {
	/** The character encoding used to create a character sequence from a byte sequence or vice versa. */
	@label({"Character encoding"})
	@comment({"The character encoding used to create a character sequence from a byte sequence or vice versa."})
	@Iri("http://www.w3.org/2011/content#characterEncoding")
	Set<Object> getCntCharacterEncoding();
	/** The character encoding used to create a character sequence from a byte sequence or vice versa. */
	@label({"Character encoding"})
	@comment({"The character encoding used to create a character sequence from a byte sequence or vice versa."})
	@Iri("http://www.w3.org/2011/content#characterEncoding")
	void setCntCharacterEncoding(Set<?> cntCharacterEncoding);

}
