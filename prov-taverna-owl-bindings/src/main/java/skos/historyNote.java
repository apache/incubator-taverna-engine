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
public @interface historyNote {
	@label({"history note"})
	@definition({"A note about the past state/use/meaning of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#historyNote")
	String[] value();

}
