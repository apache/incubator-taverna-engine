package prov;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.subPropertyOf;

/** 
 * This annotation property links a subproperty of prov:wasInfluencedBy with the subclass of prov:Influence and the qualifying property that are used to qualify it. 
 * 
 * Example annotation:
 * 
 *     prov:wasGeneratedBy prov:qualifiedForm prov:qualifiedGeneration, prov:Generation .
 * 
 * Then this unqualified assertion:
 * 
 *     :entity1 prov:wasGeneratedBy :activity1 .
 * 
 * can be qualified by adding:
 * 
 *    :entity1 prov:qualifiedGeneration :entity1Gen .
 *    :entity1Gen 
 *        a prov:Generation, prov:Influence;
 *        prov:activity :activity1;
 *        :customValue 1337 .
 * 
 * Note how the value of the unqualified influence (prov:wasGeneratedBy :activity1) is mirrored as the value of the prov:activity (or prov:entity, or prov:agent) property on the influence class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface qualifiedForm {
	@comment({"This annotation property links a subproperty of prov:wasInfluencedBy with the subclass of prov:Influence and the qualifying property that are used to qualify it. \n" + 
		"\n" + 
		"Example annotation:\n" + 
		"\n" + 
		"    prov:wasGeneratedBy prov:qualifiedForm prov:qualifiedGeneration, prov:Generation .\n" + 
		"\n" + 
		"Then this unqualified assertion:\n" + 
		"\n" + 
		"    :entity1 prov:wasGeneratedBy :activity1 .\n" + 
		"\n" + 
		"can be qualified by adding:\n" + 
		"\n" + 
		"   :entity1 prov:qualifiedGeneration :entity1Gen .\n" + 
		"   :entity1Gen \n" + 
		"       a prov:Generation, prov:Influence;\n" + 
		"       prov:activity :activity1;\n" + 
		"       :customValue 1337 .\n" + 
		"\n" + 
		"Note how the value of the unqualified influence (prov:wasGeneratedBy :activity1) is mirrored as the value of the prov:activity (or prov:entity, or prov:agent) property on the influence class."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#qualifiedForm")
	String[] value();

}
