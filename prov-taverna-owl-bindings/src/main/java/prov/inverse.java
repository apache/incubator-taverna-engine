package prov;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.seeAlso;

/** 
 * PROV-O does not define all property inverses. The directionalities defined in PROV-O should be given preference over those not defined. However, if users wish to name the inverse of a PROV-O property, the local name given by prov:inverse should be used.
 * @see http://www.w3.org/TR/prov-o/#names-of-inverse-properties
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface inverse {
	@seeAlso({"http://www.w3.org/TR/prov-o/#names-of-inverse-properties"})
	@comment({"PROV-O does not define all property inverses. The directionalities defined in PROV-O should be given preference over those not defined. However, if users wish to name the inverse of a PROV-O property, the local name given by prov:inverse should be used."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#inverse")
	String[] value();

}
