package rdf;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import owl.oneOf;

@oneOf({"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil", "http://www.w3.org/1999/02/22-rdf-syntax-ns#nil", "http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"})
public interface IsNil {
	public static final URI NIL = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");
	public static final URI[] ONEOF = new URI[]{NIL};
}
