package skos;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface note {
	@label({"note"})
	@definition({"A general note, for any purpose."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"This property may be used directly, or as a super-property for more specific note types."})
	@Iri("http://www.w3.org/2004/02/skos/core#note")
	String[] value();

}
