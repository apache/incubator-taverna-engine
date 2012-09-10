package tavernaprov;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;
import rdfs.subPropertyOf;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface errorMessage {
	@subPropertyOf({"http://purl.org/dc/terms/description"})
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/errorMessage")
	String[] value();

}
