package prov;

import java.lang.Object;
import java.util.Set;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.seeAlso;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@category({"starting-point"})
@label({"Entity"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-entity"})
@component({"entities-activities"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Entity"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
@subClassOf({"http://www.w3.org/ns/prov#ActivityOrAgentOrEntity", "http://www.w3.org/ns/prov#ActivityOrAgentOrEntityOrInstantaneousEvent"})
@definition({"An entity is a physical, digital, conceptual, or other kind of thing with some fixed aspects; entities may be real or imaginary. "})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Entity")
public interface Entity extends ActivityOrAgentOrEntity, ActivityOrAgentOrEntityOrInstantaneousEvent {
	/** 
	 * 
	 * @see prov.Entity#getProvSpecializationOf
	 */
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-alternate"})
	@seeAlso({"http://www.w3.org/ns/prov#specializationOf"})
	@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@definition({"Two alternate entities present aspects of the same thing. These aspects may be the same or different, and the alternate entities may or may not overlap in time."})
	@inverse({"alternateOf"})
	@category({"expanded"})
	@label({"alternateOf"})
	@component({"alternate"})
	@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-alternate"})
	@Iri("http://www.w3.org/ns/prov#alternateOf")
	Set<Entity> getProvAlternateOf();
	/** 
	 * 
	 * @see prov.Entity#getProvSpecializationOf_1
	 */
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-alternate"})
	@seeAlso({"http://www.w3.org/ns/prov#specializationOf"})
	@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@definition({"Two alternate entities present aspects of the same thing. These aspects may be the same or different, and the alternate entities may or may not overlap in time."})
	@inverse({"alternateOf"})
	@category({"expanded"})
	@label({"alternateOf"})
	@component({"alternate"})
	@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-alternate"})
	@Iri("http://www.w3.org/ns/prov#alternateOf")
	void setProvAlternateOf(Set<? extends Entity> provAlternateOf);

	/** 
	 * The prov:mentionOf and prov:asInBundle properties are "at risk" (http://www.w3.org/2005/10/Process-20051014/tr#cfi) and may be removed from this specification based on feedback. Please send feedback to public-prov-comments@w3.org.
	 * 
	 * These two properties are used to encode the PROV-DM's Mention construct (http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention), which might be removed from PROV if implementation experience reveals problems with supporting this construct. 
	 * When :x prov:mentionOf :y and :y is described in Bundle :b, the triple :x prov:asInBundle :b is also asserted to cite the Bundle in which :y was described.
	 */
	@category({"expanded"})
	@label({"asInBundle"})
	@comment({"The prov:mentionOf and prov:asInBundle properties are \"at risk\" (http://www.w3.org/2005/10/Process-20051014/tr#cfi) and may be removed from this specification based on feedback. Please send feedback to public-prov-comments@w3.org.\n" + 
		"\n" + 
		"These two properties are used to encode the PROV-DM's Mention construct (http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention), which might be removed from PROV if implementation experience reveals problems with supporting this construct. ", "When :x prov:mentionOf :y and :y is described in Bundle :b, the triple :x prov:asInBundle :b is also asserted to cite the Bundle in which :y was described."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#mentionOf"})
	@inverse({"contextOf"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#asInBundle")
	Set<Bundle> getProvAsInBundles();
	/** 
	 * The prov:mentionOf and prov:asInBundle properties are "at risk" (http://www.w3.org/2005/10/Process-20051014/tr#cfi) and may be removed from this specification based on feedback. Please send feedback to public-prov-comments@w3.org.
	 * 
	 * These two properties are used to encode the PROV-DM's Mention construct (http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention), which might be removed from PROV if implementation experience reveals problems with supporting this construct. 
	 * When :x prov:mentionOf :y and :y is described in Bundle :b, the triple :x prov:asInBundle :b is also asserted to cite the Bundle in which :y was described.
	 */
	@category({"expanded"})
	@label({"asInBundle"})
	@comment({"The prov:mentionOf and prov:asInBundle properties are \"at risk\" (http://www.w3.org/2005/10/Process-20051014/tr#cfi) and may be removed from this specification based on feedback. Please send feedback to public-prov-comments@w3.org.\n" + 
		"\n" + 
		"These two properties are used to encode the PROV-DM's Mention construct (http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention), which might be removed from PROV if implementation experience reveals problems with supporting this construct. ", "When :x prov:mentionOf :y and :y is described in Bundle :b, the triple :x prov:asInBundle :b is also asserted to cite the Bundle in which :y was described."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#mentionOf"})
	@inverse({"contextOf"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#asInBundle")
	void setProvAsInBundles(Set<? extends Bundle> provAsInBundles);

	/** The time at which an entity was completely created and is available for use. */
	@category({"expanded"})
	@label({"generatedAtTime"})
	@component({"entities-activities"})
	@comment({"The time at which an entity was completely created and is available for use."})
	@editorialNote({"It is the intent that the property chain holds: (prov:qualifiedGeneration o prov:atTime) rdfs:subPropertyOf prov:generatedAtTime."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Generation", "http://www.w3.org/ns/prov#atTime"})
	@Iri("http://www.w3.org/ns/prov#generatedAtTime")
	Set<XMLGregorianCalendar> getProvGeneratedAtTime();
	/** The time at which an entity was completely created and is available for use. */
	@category({"expanded"})
	@label({"generatedAtTime"})
	@component({"entities-activities"})
	@comment({"The time at which an entity was completely created and is available for use."})
	@editorialNote({"It is the intent that the property chain holds: (prov:qualifiedGeneration o prov:atTime) rdfs:subPropertyOf prov:generatedAtTime."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Generation", "http://www.w3.org/ns/prov#atTime"})
	@Iri("http://www.w3.org/ns/prov#generatedAtTime")
	void setProvGeneratedAtTime(Set<? extends XMLGregorianCalendar> provGeneratedAtTime);

	@category({"expanded"})
	@label({"hadPrimarySource"})
	@component({"derivations"})
	@inverse({"wasPrimarySourceOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasDerivedFrom"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Source", "http://www.w3.org/ns/prov#qualifiedSource"})
	@Iri("http://www.w3.org/ns/prov#hadPrimarySource")
	Set<Entity> getProvHadPrimarySources();
	@category({"expanded"})
	@label({"hadPrimarySource"})
	@component({"derivations"})
	@inverse({"wasPrimarySourceOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasDerivedFrom"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Source", "http://www.w3.org/ns/prov#qualifiedSource"})
	@Iri("http://www.w3.org/ns/prov#hadPrimarySource")
	void setProvHadPrimarySources(Set<? extends Entity> provHadPrimarySources);

	/** The time at which an entity was invalidated (i.e., no longer usable). */
	@category({"expanded"})
	@label({"invalidatedAtTime"})
	@component({"entities-activities"})
	@comment({"The time at which an entity was invalidated (i.e., no longer usable)."})
	@editorialNote({"It is the intent that the property chain holds: (prov:qualifiedInvalidation o prov:atTime) rdfs:subPropertyOf prov:invalidatedAtTime."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Invalidation", "http://www.w3.org/ns/prov#atTime"})
	@Iri("http://www.w3.org/ns/prov#invalidatedAtTime")
	Set<XMLGregorianCalendar> getProvInvalidatedAtTime();
	/** The time at which an entity was invalidated (i.e., no longer usable). */
	@category({"expanded"})
	@label({"invalidatedAtTime"})
	@component({"entities-activities"})
	@comment({"The time at which an entity was invalidated (i.e., no longer usable)."})
	@editorialNote({"It is the intent that the property chain holds: (prov:qualifiedInvalidation o prov:atTime) rdfs:subPropertyOf prov:invalidatedAtTime."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Invalidation", "http://www.w3.org/ns/prov#atTime"})
	@Iri("http://www.w3.org/ns/prov#invalidatedAtTime")
	void setProvInvalidatedAtTime(Set<? extends XMLGregorianCalendar> provInvalidatedAtTime);

	/** 
	 * When :x prov:mentionOf :y and :y is described in Bundle :b, the triple :x prov:asInBundle :b is also asserted to cite the Bundle in which :y was described.
	 * The prov:mentionOf and prov:asInBundle properties are "at risk" (http://www.w3.org/2005/10/Process-20051014/tr#cfi) and may be removed from this specification based on feedback. Please send feedback to public-prov-comments@w3.org.
	 * 
	 * These two properties are used to encode the PROV-DM's Mention construct (http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention), which might be removed from PROV if implementation experience reveals problems with supporting this construct. 
	 * prov:asInBundle is used to cite the Bundle in which the generalization was mentioned.
	 */
	@category({"expanded"})
	@label({"mentionOf"})
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention"})
	@comment({"When :x prov:mentionOf :y and :y is described in Bundle :b, the triple :x prov:asInBundle :b is also asserted to cite the Bundle in which :y was described.", "The prov:mentionOf and prov:asInBundle properties are \"at risk\" (http://www.w3.org/2005/10/Process-20051014/tr#cfi) and may be removed from this specification based on feedback. Please send feedback to public-prov-comments@w3.org.\n" + 
		"\n" + 
		"These two properties are used to encode the PROV-DM's Mention construct (http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention), which might be removed from PROV if implementation experience reveals problems with supporting this construct. ", "prov:asInBundle is used to cite the Bundle in which the generalization was mentioned."})
	@inverse({"hadMention"})
	@definition({"The mention of an Entity in a Bundle (containing a description of this Entity) is another Entity that is a specialization of the former and that presents the Bundle as a further additional aspect. "})
	@subPropertyOf({"http://www.w3.org/ns/prov#specializationOf"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#mentionOf")
	Set<Entity> getProvMentionOf();
	/** 
	 * When :x prov:mentionOf :y and :y is described in Bundle :b, the triple :x prov:asInBundle :b is also asserted to cite the Bundle in which :y was described.
	 * The prov:mentionOf and prov:asInBundle properties are "at risk" (http://www.w3.org/2005/10/Process-20051014/tr#cfi) and may be removed from this specification based on feedback. Please send feedback to public-prov-comments@w3.org.
	 * 
	 * These two properties are used to encode the PROV-DM's Mention construct (http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention), which might be removed from PROV if implementation experience reveals problems with supporting this construct. 
	 * prov:asInBundle is used to cite the Bundle in which the generalization was mentioned.
	 */
	@category({"expanded"})
	@label({"mentionOf"})
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention"})
	@comment({"When :x prov:mentionOf :y and :y is described in Bundle :b, the triple :x prov:asInBundle :b is also asserted to cite the Bundle in which :y was described.", "The prov:mentionOf and prov:asInBundle properties are \"at risk\" (http://www.w3.org/2005/10/Process-20051014/tr#cfi) and may be removed from this specification based on feedback. Please send feedback to public-prov-comments@w3.org.\n" + 
		"\n" + 
		"These two properties are used to encode the PROV-DM's Mention construct (http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-mention), which might be removed from PROV if implementation experience reveals problems with supporting this construct. ", "prov:asInBundle is used to cite the Bundle in which the generalization was mentioned."})
	@inverse({"hadMention"})
	@definition({"The mention of an Entity in a Bundle (containing a description of this Entity) is another Entity that is a specialization of the former and that presents the Bundle as a further additional aspect. "})
	@subPropertyOf({"http://www.w3.org/ns/prov#specializationOf"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#mentionOf")
	void setProvMentionOf(Set<? extends Entity> provMentionOf);

	/** If this Entity prov:wasAttributedTo Agent :ag, then it can qualify how it was  using prov:qualifiedAttribution [ a prov:Attribution;  prov:agent :ag; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedAttribution"})
	@component({"agents-responsibility"})
	@comment({"If this Entity prov:wasAttributedTo Agent :ag, then it can qualify how it was  using prov:qualifiedAttribution [ a prov:Attribution;  prov:agent :ag; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Attribution"})
	@inverse({"qualifiedAttributionOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasAttributedTo"})
	@Iri("http://www.w3.org/ns/prov#qualifiedAttribution")
	Set<Attribution> getProvQualifiedAttributions();
	/** If this Entity prov:wasAttributedTo Agent :ag, then it can qualify how it was  using prov:qualifiedAttribution [ a prov:Attribution;  prov:agent :ag; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedAttribution"})
	@component({"agents-responsibility"})
	@comment({"If this Entity prov:wasAttributedTo Agent :ag, then it can qualify how it was  using prov:qualifiedAttribution [ a prov:Attribution;  prov:agent :ag; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Attribution"})
	@inverse({"qualifiedAttributionOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasAttributedTo"})
	@Iri("http://www.w3.org/ns/prov#qualifiedAttribution")
	void setProvQualifiedAttributions(Set<? extends Attribution> provQualifiedAttributions);

	/** If this Entity prov:wasDerivedFrom Entity :e, then it can qualify how it was derived using prov:qualifiedDerivation [ a prov:Derivation;  prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedDerivation"})
	@component({"derivations"})
	@comment({"If this Entity prov:wasDerivedFrom Entity :e, then it can qualify how it was derived using prov:qualifiedDerivation [ a prov:Derivation;  prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Derivation"})
	@inverse({"qualifiedDerivationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasDerivedFrom"})
	@Iri("http://www.w3.org/ns/prov#qualifiedDerivation")
	Set<Derivation> getProvQualifiedDerivations();
	/** If this Entity prov:wasDerivedFrom Entity :e, then it can qualify how it was derived using prov:qualifiedDerivation [ a prov:Derivation;  prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedDerivation"})
	@component({"derivations"})
	@comment({"If this Entity prov:wasDerivedFrom Entity :e, then it can qualify how it was derived using prov:qualifiedDerivation [ a prov:Derivation;  prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Derivation"})
	@inverse({"qualifiedDerivationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasDerivedFrom"})
	@Iri("http://www.w3.org/ns/prov#qualifiedDerivation")
	void setProvQualifiedDerivations(Set<? extends Derivation> provQualifiedDerivations);

	/** If this Activity prov:generated Entity :e, then it can qualify how it did performed the Generation using prov:qualifiedGeneration [ a prov:Generation;  prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedGeneration"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:generated Entity :e, then it can qualify how it did performed the Generation using prov:qualifiedGeneration [ a prov:Generation;  prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Generation"})
	@inverse({"qualifiedGenerationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasGeneratedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedGeneration")
	Set<Generation> getProvQualifiedGenerations();
	/** If this Activity prov:generated Entity :e, then it can qualify how it did performed the Generation using prov:qualifiedGeneration [ a prov:Generation;  prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedGeneration"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:generated Entity :e, then it can qualify how it did performed the Generation using prov:qualifiedGeneration [ a prov:Generation;  prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Generation"})
	@inverse({"qualifiedGenerationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasGeneratedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedGeneration")
	void setProvQualifiedGenerations(Set<? extends Generation> provQualifiedGenerations);

	/** If this Entity prov:wasInvalidatedBy Activity :a, then it can qualify how it was invalidated using prov:qualifiedInvalidation [ a prov:Invalidation;  prov:activity :a; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedInvalidation"})
	@component({"entities-activities"})
	@comment({"If this Entity prov:wasInvalidatedBy Activity :a, then it can qualify how it was invalidated using prov:qualifiedInvalidation [ a prov:Invalidation;  prov:activity :a; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Invalidation"})
	@inverse({"qualifiedInvalidationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasInvalidatedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedInvalidation")
	Set<Invalidation> getProvQualifiedInvalidations();
	/** If this Entity prov:wasInvalidatedBy Activity :a, then it can qualify how it was invalidated using prov:qualifiedInvalidation [ a prov:Invalidation;  prov:activity :a; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedInvalidation"})
	@component({"entities-activities"})
	@comment({"If this Entity prov:wasInvalidatedBy Activity :a, then it can qualify how it was invalidated using prov:qualifiedInvalidation [ a prov:Invalidation;  prov:activity :a; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Invalidation"})
	@inverse({"qualifiedInvalidationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasInvalidatedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedInvalidation")
	void setProvQualifiedInvalidations(Set<? extends Invalidation> provQualifiedInvalidations);

	/** If this Entity prov:wasQuotedFrom Entity :e, then it can qualify how using prov:qualifiedQuotation [ a prov:Quotation;  prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedQuotation"})
	@component({"derivations"})
	@comment({"If this Entity prov:wasQuotedFrom Entity :e, then it can qualify how using prov:qualifiedQuotation [ a prov:Quotation;  prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Quotation"})
	@inverse({"qualifiedQuotationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasQuotedFrom"})
	@Iri("http://www.w3.org/ns/prov#qualifiedQuotation")
	Set<Quotation> getProvQualifiedQuotations();
	/** If this Entity prov:wasQuotedFrom Entity :e, then it can qualify how using prov:qualifiedQuotation [ a prov:Quotation;  prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedQuotation"})
	@component({"derivations"})
	@comment({"If this Entity prov:wasQuotedFrom Entity :e, then it can qualify how using prov:qualifiedQuotation [ a prov:Quotation;  prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Quotation"})
	@inverse({"qualifiedQuotationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasQuotedFrom"})
	@Iri("http://www.w3.org/ns/prov#qualifiedQuotation")
	void setProvQualifiedQuotations(Set<? extends Quotation> provQualifiedQuotations);

	/** If this Entity prov:wasRevisionOf Entity :e, then it can qualify how it was  revised using prov:qualifiedRevision [ a prov:Revision;  prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedRevision"})
	@component({"derivations"})
	@comment({"If this Entity prov:wasRevisionOf Entity :e, then it can qualify how it was  revised using prov:qualifiedRevision [ a prov:Revision;  prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Revision"})
	@inverse({"revisedEntity"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasRevisionOf"})
	@Iri("http://www.w3.org/ns/prov#qualifiedRevision")
	Set<Revision> getProvQualifiedRevisions();
	/** If this Entity prov:wasRevisionOf Entity :e, then it can qualify how it was  revised using prov:qualifiedRevision [ a prov:Revision;  prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedRevision"})
	@component({"derivations"})
	@comment({"If this Entity prov:wasRevisionOf Entity :e, then it can qualify how it was  revised using prov:qualifiedRevision [ a prov:Revision;  prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Revision"})
	@inverse({"revisedEntity"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasRevisionOf"})
	@Iri("http://www.w3.org/ns/prov#qualifiedRevision")
	void setProvQualifiedRevisions(Set<? extends Revision> provQualifiedRevisions);

	/** If this Entity prov:hadOriginalSource Entity :e, then it can qualify how using prov:qualifiedSource [ a prov:Source; prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedSource"})
	@component({"derivations"})
	@comment({"If this Entity prov:hadOriginalSource Entity :e, then it can qualify how using prov:qualifiedSource [ a prov:Source; prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Source"})
	@inverse({"qualifiedSourceOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#hadPrimarySource"})
	@Iri("http://www.w3.org/ns/prov#qualifiedSource")
	Set<Source> getProvQualifiedSources();
	/** If this Entity prov:hadOriginalSource Entity :e, then it can qualify how using prov:qualifiedSource [ a prov:Source; prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedSource"})
	@component({"derivations"})
	@comment({"If this Entity prov:hadOriginalSource Entity :e, then it can qualify how using prov:qualifiedSource [ a prov:Source; prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Source"})
	@inverse({"qualifiedSourceOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#hadPrimarySource"})
	@Iri("http://www.w3.org/ns/prov#qualifiedSource")
	void setProvQualifiedSources(Set<? extends Source> provQualifiedSources);

	/** 
	 * 
	 * @see prov.Entity#getProvAlternateOf_1
	 */
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-specialization"})
	@seeAlso({"http://www.w3.org/ns/prov#alternateOf"})
	@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@subPropertyOf({"http://www.w3.org/ns/prov#alternateOf"})
	@definition({"An entity that is a specialization of another shares all aspects of the latter, and additionally presents more specific aspects of the same thing as the latter. In particular, the lifetime of the entity being specialized contains that of any specialization. Examples of aspects include a time period, an abstraction, and a context associated with the entity."})
	@inverse({"generalizationOf"})
	@category({"expanded"})
	@label({"specializationOf"})
	@component({"alternate"})
	@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-specialization"})
	@Iri("http://www.w3.org/ns/prov#specializationOf")
	Set<Entity> getProvSpecializationOf_1();
	/** 
	 * 
	 * @see prov.Entity#getProvAlternateOf_1
	 */
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-specialization"})
	@seeAlso({"http://www.w3.org/ns/prov#alternateOf"})
	@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@subPropertyOf({"http://www.w3.org/ns/prov#alternateOf"})
	@definition({"An entity that is a specialization of another shares all aspects of the latter, and additionally presents more specific aspects of the same thing as the latter. In particular, the lifetime of the entity being specialized contains that of any specialization. Examples of aspects include a time period, an abstraction, and a context associated with the entity."})
	@inverse({"generalizationOf"})
	@category({"expanded"})
	@label({"specializationOf"})
	@component({"alternate"})
	@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-specialization"})
	@Iri("http://www.w3.org/ns/prov#specializationOf")
	void setProvSpecializationOf_1(Set<? extends Entity> provSpecializationOf_1);

	/** Provides a value for an Entity. */
	@editorsDefinition({"The main value (if there is one) of a structured value."})
	@category({"expanded"})
	@label({"value"})
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-attribute-value"})
	@component({"entities-activities"})
	@comment({"Provides a value for an Entity."})
	@editorialNote({"The editor's definition comes from http://www.w3.org/TR/rdf-primer/#rdfvalue", "This property serves the same purpose as rdf:value, but has been reintroduced to avoid some of the definitional ambiguity in the RDF specification (specifically, 'may be used in describing structured values')."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#value")
	Set<Object> getProvValues();
	/** Provides a value for an Entity. */
	@editorsDefinition({"The main value (if there is one) of a structured value."})
	@category({"expanded"})
	@label({"value"})
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-attribute-value"})
	@component({"entities-activities"})
	@comment({"Provides a value for an Entity."})
	@editorialNote({"The editor's definition comes from http://www.w3.org/TR/rdf-primer/#rdfvalue", "This property serves the same purpose as rdf:value, but has been reintroduced to avoid some of the definitional ambiguity in the RDF specification (specifically, 'may be used in describing structured values')."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#value")
	void setProvValues(Set<?> provValues);

	/** Attribution is the ascribing of an entity to an agent. */
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@definition({"Attribution is the ascribing of an entity to an agent."})
	@inverse({"contributed"})
	@category({"starting-point"})
	@label({"wasAttributedTo"})
	@component({"agents-responsibility"})
	@comment({"Attribution is the ascribing of an entity to an agent."})
	@qualifiedForm({"http://www.w3.org/ns/prov#Attribution", "http://www.w3.org/ns/prov#qualifiedAttribution"})
	@Iri("http://www.w3.org/ns/prov#wasAttributedTo")
	Set<Agent> getProvWasAttributedTo();
	/** Attribution is the ascribing of an entity to an agent. */
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@definition({"Attribution is the ascribing of an entity to an agent."})
	@inverse({"contributed"})
	@category({"starting-point"})
	@label({"wasAttributedTo"})
	@component({"agents-responsibility"})
	@comment({"Attribution is the ascribing of an entity to an agent."})
	@qualifiedForm({"http://www.w3.org/ns/prov#Attribution", "http://www.w3.org/ns/prov#qualifiedAttribution"})
	@Iri("http://www.w3.org/ns/prov#wasAttributedTo")
	void setProvWasAttributedTo(Set<? extends Agent> provWasAttributedTo);

	@category({"starting-point"})
	@label({"wasDerivedFrom"})
	@component({"derivations"})
	@inverse({"hadDerivation"})
	@definition({"A derivation is a transformation of an entity into another, a construction of an entity into another, or an update of an entity, resulting in a new one."})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Derivation", "http://www.w3.org/ns/prov#qualifiedDerivation"})
	@Iri("http://www.w3.org/ns/prov#wasDerivedFrom")
	Set<Entity> getProvWasDerivedFrom();
	@category({"starting-point"})
	@label({"wasDerivedFrom"})
	@component({"derivations"})
	@inverse({"hadDerivation"})
	@definition({"A derivation is a transformation of an entity into another, a construction of an entity into another, or an update of an entity, resulting in a new one."})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Derivation", "http://www.w3.org/ns/prov#qualifiedDerivation"})
	@Iri("http://www.w3.org/ns/prov#wasDerivedFrom")
	void setProvWasDerivedFrom(Set<? extends Entity> provWasDerivedFrom);

	@category({"starting-point"})
	@label({"wasGeneratedBy"})
	@component({"entities-activities"})
	@inverse({"generated"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Generation", "http://www.w3.org/ns/prov#qualifiedGeneration"})
	@Iri("http://www.w3.org/ns/prov#wasGeneratedBy")
	Set<Activity> getProvWasGeneratedBy();
	@category({"starting-point"})
	@label({"wasGeneratedBy"})
	@component({"entities-activities"})
	@inverse({"generated"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Generation", "http://www.w3.org/ns/prov#qualifiedGeneration"})
	@Iri("http://www.w3.org/ns/prov#wasGeneratedBy")
	void setProvWasGeneratedBy(Set<? extends Activity> provWasGeneratedBy);

	@category({"expanded"})
	@label({"wasInvalidatedBy"})
	@component({"entities-activities"})
	@inverse({"invalidated"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Invalidation", "http://www.w3.org/ns/prov#qualifiedInvalidation"})
	@Iri("http://www.w3.org/ns/prov#wasInvalidatedBy")
	Set<Activity> getProvWasInvalidatedBy();
	@category({"expanded"})
	@label({"wasInvalidatedBy"})
	@component({"entities-activities"})
	@inverse({"invalidated"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Invalidation", "http://www.w3.org/ns/prov#qualifiedInvalidation"})
	@Iri("http://www.w3.org/ns/prov#wasInvalidatedBy")
	void setProvWasInvalidatedBy(Set<? extends Activity> provWasInvalidatedBy);

	/** An entity is derived from an original entity by copying, or 'quoting', some or all of it. */
	@category({"expanded"})
	@label({"wasQuotedFrom"})
	@component({"derivations"})
	@comment({"An entity is derived from an original entity by copying, or 'quoting', some or all of it."})
	@inverse({"quotedAs"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasDerivedFrom"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Quotation", "http://www.w3.org/ns/prov#qualifiedQuotation"})
	@Iri("http://www.w3.org/ns/prov#wasQuotedFrom")
	Set<Entity> getProvWasQuotedFrom();
	/** An entity is derived from an original entity by copying, or 'quoting', some or all of it. */
	@category({"expanded"})
	@label({"wasQuotedFrom"})
	@component({"derivations"})
	@comment({"An entity is derived from an original entity by copying, or 'quoting', some or all of it."})
	@inverse({"quotedAs"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasDerivedFrom"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Quotation", "http://www.w3.org/ns/prov#qualifiedQuotation"})
	@Iri("http://www.w3.org/ns/prov#wasQuotedFrom")
	void setProvWasQuotedFrom(Set<? extends Entity> provWasQuotedFrom);

	/** A revision is a derivation that revises an entity into a revised version. */
	@category({"expanded"})
	@label({"wasRevisionOf"})
	@component({"derivations"})
	@comment({"A revision is a derivation that revises an entity into a revised version."})
	@inverse({"hadRevision"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasDerivedFrom"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Revision", "http://www.w3.org/ns/prov#qualifiedRevision"})
	@Iri("http://www.w3.org/ns/prov#wasRevisionOf")
	Set<Entity> getProvWasRevisionOf();
	/** A revision is a derivation that revises an entity into a revised version. */
	@category({"expanded"})
	@label({"wasRevisionOf"})
	@component({"derivations"})
	@comment({"A revision is a derivation that revises an entity into a revised version."})
	@inverse({"hadRevision"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasDerivedFrom"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Revision", "http://www.w3.org/ns/prov#qualifiedRevision"})
	@Iri("http://www.w3.org/ns/prov#wasRevisionOf")
	void setProvWasRevisionOf(Set<? extends Entity> provWasRevisionOf);

}
