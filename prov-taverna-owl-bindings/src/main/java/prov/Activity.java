package prov;

import java.util.Set;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@category({"starting-point"})
@label({"Activity"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-Activity"})
@component({"entities-activities"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Activity"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
@subClassOf({"http://www.w3.org/ns/prov#ActivityOrAgentOrEntity", "http://www.w3.org/ns/prov#ActivityOrAgentOrEntityOrInstantaneousEvent"})
@definition({"An activity is something that occurs over a period of time and acts upon or with entities; it may include consuming, processing, transforming, modifying, relocating, using, or generating entities."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Activity")
public interface Activity extends ActivityOrAgentOrEntity, ActivityOrAgentOrEntityOrInstantaneousEvent {
	/** The time at which an activity ended. See also prov:startedAtTime. */
	@category({"starting-point"})
	@label({"endedAtTime"})
	@component({"entities-activities"})
	@comment({"The time at which an activity ended. See also prov:startedAtTime."})
	@editorialNote({"It is the intent that the property chain holds: (prov:qualifiedEnd o prov:atTime) rdfs:subPropertyOf prov:endedAtTime."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#End", "http://www.w3.org/ns/prov#atTime"})
	@Iri("http://www.w3.org/ns/prov#endedAtTime")
	Set<XMLGregorianCalendar> getProvEndedAtTime();
	/** The time at which an activity ended. See also prov:startedAtTime. */
	@category({"starting-point"})
	@label({"endedAtTime"})
	@component({"entities-activities"})
	@comment({"The time at which an activity ended. See also prov:startedAtTime."})
	@editorialNote({"It is the intent that the property chain holds: (prov:qualifiedEnd o prov:atTime) rdfs:subPropertyOf prov:endedAtTime."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#End", "http://www.w3.org/ns/prov#atTime"})
	@Iri("http://www.w3.org/ns/prov#endedAtTime")
	void setProvEndedAtTime(Set<? extends XMLGregorianCalendar> provEndedAtTime);

	@category({"expanded"})
	@label({"generated"})
	@component({"entities-activities"})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Generation"})
	@inverse({"wasGeneratedBy"})
	@editorialNote({"prov:generated is one of few inverse property defined, to allow Activity-oriented assertions in addition to Entity-oriented assertions."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influenced"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#generated")
	Set<Entity> getProvGenerated();
	@category({"expanded"})
	@label({"generated"})
	@component({"entities-activities"})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Generation"})
	@inverse({"wasGeneratedBy"})
	@editorialNote({"prov:generated is one of few inverse property defined, to allow Activity-oriented assertions in addition to Entity-oriented assertions."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influenced"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#generated")
	void setProvGenerated(Set<? extends Entity> provGenerated);

	@category({"expanded"})
	@label({"invalidated"})
	@component({"entities-activities"})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Invalidation"})
	@inverse({"wasInvalidatedBy"})
	@editorialNote({"prov:invalidated is one of few inverse property defined, to allow Activity-oriented assertions in addition to Entity-oriented assertions."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influenced"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#invalidated")
	Set<Entity> getProvInvalidated();
	@category({"expanded"})
	@label({"invalidated"})
	@component({"entities-activities"})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Invalidation"})
	@inverse({"wasInvalidatedBy"})
	@editorialNote({"prov:invalidated is one of few inverse property defined, to allow Activity-oriented assertions in addition to Entity-oriented assertions."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influenced"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#invalidated")
	void setProvInvalidated(Set<? extends Entity> provInvalidated);

	/** If this Activity prov:wasAssociatedWith Agent :ag, then it can qualify the Association using prov:qualifiedAssociation [ a prov:Association;  prov:agent :ag; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedAssociation"})
	@component({"agents-responsibility"})
	@comment({"If this Activity prov:wasAssociatedWith Agent :ag, then it can qualify the Association using prov:qualifiedAssociation [ a prov:Association;  prov:agent :ag; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Association"})
	@inverse({"qualifiedAssociationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasAssociatedWith"})
	@Iri("http://www.w3.org/ns/prov#qualifiedAssociation")
	Set<Association> getProvQualifiedAssociations();
	/** If this Activity prov:wasAssociatedWith Agent :ag, then it can qualify the Association using prov:qualifiedAssociation [ a prov:Association;  prov:agent :ag; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedAssociation"})
	@component({"agents-responsibility"})
	@comment({"If this Activity prov:wasAssociatedWith Agent :ag, then it can qualify the Association using prov:qualifiedAssociation [ a prov:Association;  prov:agent :ag; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Association"})
	@inverse({"qualifiedAssociationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasAssociatedWith"})
	@Iri("http://www.w3.org/ns/prov#qualifiedAssociation")
	void setProvQualifiedAssociations(Set<? extends Association> provQualifiedAssociations);

	/** If this Activity prov:wasInformedBy Activity :a, then it can qualify how it was Inform[ed] using prov:qualifiedCommunication [ a prov:Communication;  prov:activity :a; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedCommunication"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:wasInformedBy Activity :a, then it can qualify how it was Inform[ed] using prov:qualifiedCommunication [ a prov:Communication;  prov:activity :a; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Communication"})
	@inverse({"qualifiedCommunicationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Communication"})
	@Iri("http://www.w3.org/ns/prov#qualifiedCommunication")
	Set<Communication> getProvQualifiedCommunications();
	/** If this Activity prov:wasInformedBy Activity :a, then it can qualify how it was Inform[ed] using prov:qualifiedCommunication [ a prov:Communication;  prov:activity :a; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedCommunication"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:wasInformedBy Activity :a, then it can qualify how it was Inform[ed] using prov:qualifiedCommunication [ a prov:Communication;  prov:activity :a; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Communication"})
	@inverse({"qualifiedCommunicationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Communication"})
	@Iri("http://www.w3.org/ns/prov#qualifiedCommunication")
	void setProvQualifiedCommunications(Set<? extends Communication> provQualifiedCommunications);

	/** If this Activity prov:wasEndedBy Entity :e1, then it can qualify how it was ended using prov:qualifiedEnd [ a prov:End;  prov:entity :e1; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedEnd"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:wasEndedBy Entity :e1, then it can qualify how it was ended using prov:qualifiedEnd [ a prov:End;  prov:entity :e1; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#End"})
	@inverse({"qualifiedEndOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasEndedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedEnd")
	Set<End> getProvQualifiedEnds();
	/** If this Activity prov:wasEndedBy Entity :e1, then it can qualify how it was ended using prov:qualifiedEnd [ a prov:End;  prov:entity :e1; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedEnd"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:wasEndedBy Entity :e1, then it can qualify how it was ended using prov:qualifiedEnd [ a prov:End;  prov:entity :e1; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#End"})
	@inverse({"qualifiedEndOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasEndedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedEnd")
	void setProvQualifiedEnds(Set<? extends End> provQualifiedEnds);

	/** If this Activity prov:wasStartedBy Entity :e1, then it can qualify how it was started using prov:qualifiedStart [ a prov:Start;  prov:entity :e1; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedStart"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:wasStartedBy Entity :e1, then it can qualify how it was started using prov:qualifiedStart [ a prov:Start;  prov:entity :e1; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Start"})
	@inverse({"qualifiedStartOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasStartedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedStart")
	Set<Start> getProvQualifiedStarts();
	/** If this Activity prov:wasStartedBy Entity :e1, then it can qualify how it was started using prov:qualifiedStart [ a prov:Start;  prov:entity :e1; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedStart"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:wasStartedBy Entity :e1, then it can qualify how it was started using prov:qualifiedStart [ a prov:Start;  prov:entity :e1; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Start"})
	@inverse({"qualifiedStartOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasStartedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedStart")
	void setProvQualifiedStarts(Set<? extends Start> provQualifiedStarts);

	/** If this Activity prov:used Entity :e, then it can qualify how it used it using prov:qualifiedUsage [ a prov:Usage; prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedUsage"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:used Entity :e, then it can qualify how it used it using prov:qualifiedUsage [ a prov:Usage; prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Usage"})
	@inverse({"qualifiedUsingActivity"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#used"})
	@Iri("http://www.w3.org/ns/prov#qualifiedUsage")
	Set<Usage> getProvQualifiedUsages();
	/** If this Activity prov:used Entity :e, then it can qualify how it used it using prov:qualifiedUsage [ a prov:Usage; prov:entity :e; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedUsage"})
	@component({"entities-activities"})
	@comment({"If this Activity prov:used Entity :e, then it can qualify how it used it using prov:qualifiedUsage [ a prov:Usage; prov:entity :e; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Usage"})
	@inverse({"qualifiedUsingActivity"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#used"})
	@Iri("http://www.w3.org/ns/prov#qualifiedUsage")
	void setProvQualifiedUsages(Set<? extends Usage> provQualifiedUsages);

	/** The time at which an activity started. See also prov:endedAtTime. */
	@category({"starting-point"})
	@label({"startedAtTime"})
	@component({"entities-activities"})
	@comment({"The time at which an activity started. See also prov:endedAtTime."})
	@editorialNote({"It is the intent that the property chain holds: (prov:qualifiedStart o prov:atTime) rdfs:subPropertyOf prov:startedAtTime."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Start", "http://www.w3.org/ns/prov#atTime"})
	@Iri("http://www.w3.org/ns/prov#startedAtTime")
	Set<XMLGregorianCalendar> getProvStartedAtTime();
	/** The time at which an activity started. See also prov:endedAtTime. */
	@category({"starting-point"})
	@label({"startedAtTime"})
	@component({"entities-activities"})
	@comment({"The time at which an activity started. See also prov:endedAtTime."})
	@editorialNote({"It is the intent that the property chain holds: (prov:qualifiedStart o prov:atTime) rdfs:subPropertyOf prov:startedAtTime."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Start", "http://www.w3.org/ns/prov#atTime"})
	@Iri("http://www.w3.org/ns/prov#startedAtTime")
	void setProvStartedAtTime(Set<? extends XMLGregorianCalendar> provStartedAtTime);

	/** A prov:Entity that was used by this prov:Activity. For example, :baking prov:used :spoon, :egg, :oven . */
	@category({"starting-point"})
	@label({"used"})
	@component({"entities-activities"})
	@comment({"A prov:Entity that was used by this prov:Activity. For example, :baking prov:used :spoon, :egg, :oven ."})
	@inverse({"wasUsedBy"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Usage", "http://www.w3.org/ns/prov#qualifiedUsage"})
	@Iri("http://www.w3.org/ns/prov#used")
	Set<Entity> getProvUsed();
	/** A prov:Entity that was used by this prov:Activity. For example, :baking prov:used :spoon, :egg, :oven . */
	@category({"starting-point"})
	@label({"used"})
	@component({"entities-activities"})
	@comment({"A prov:Entity that was used by this prov:Activity. For example, :baking prov:used :spoon, :egg, :oven ."})
	@inverse({"wasUsedBy"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Usage", "http://www.w3.org/ns/prov#qualifiedUsage"})
	@Iri("http://www.w3.org/ns/prov#used")
	void setProvUsed(Set<? extends Entity> provUsed);

	/** An prov:Agent that had some (unspecified) responsibility for the occurrence of this prov:Activity. */
	@category({"starting-point"})
	@label({"wasAssociatedWith"})
	@component({"agents-responsibility"})
	@comment({"An prov:Agent that had some (unspecified) responsibility for the occurrence of this prov:Activity."})
	@inverse({"wasAssociateFor"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Association", "http://www.w3.org/ns/prov#qualifiedAssociation"})
	@Iri("http://www.w3.org/ns/prov#wasAssociatedWith")
	Set<Agent> getProvWasAssociatedWith();
	/** An prov:Agent that had some (unspecified) responsibility for the occurrence of this prov:Activity. */
	@category({"starting-point"})
	@label({"wasAssociatedWith"})
	@component({"agents-responsibility"})
	@comment({"An prov:Agent that had some (unspecified) responsibility for the occurrence of this prov:Activity."})
	@inverse({"wasAssociateFor"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Association", "http://www.w3.org/ns/prov#qualifiedAssociation"})
	@Iri("http://www.w3.org/ns/prov#wasAssociatedWith")
	void setProvWasAssociatedWith(Set<? extends Agent> provWasAssociatedWith);

	/** End is when an activity is deemed to have ended. An end may refer to an entity, known as trigger, that terminated the activity. */
	@category({"expanded"})
	@label({"wasEndedBy"})
	@component({"entities-activities"})
	@comment({"End is when an activity is deemed to have ended. An end may refer to an entity, known as trigger, that terminated the activity."})
	@inverse({"ended"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#End", "http://www.w3.org/ns/prov#qualifiedEnd"})
	@Iri("http://www.w3.org/ns/prov#wasEndedBy")
	Set<Entity> getProvWasEndedBy();
	/** End is when an activity is deemed to have ended. An end may refer to an entity, known as trigger, that terminated the activity. */
	@category({"expanded"})
	@label({"wasEndedBy"})
	@component({"entities-activities"})
	@comment({"End is when an activity is deemed to have ended. An end may refer to an entity, known as trigger, that terminated the activity."})
	@inverse({"ended"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#End", "http://www.w3.org/ns/prov#qualifiedEnd"})
	@Iri("http://www.w3.org/ns/prov#wasEndedBy")
	void setProvWasEndedBy(Set<? extends Entity> provWasEndedBy);

	/** An activity a2 is dependent on or informed by another activity a1, by way of some unspecified entity that is generated by a1 and used by a2. */
	@category({"starting-point"})
	@label({"wasInformedBy"})
	@component({"entities-activities"})
	@comment({"An activity a2 is dependent on or informed by another activity a1, by way of some unspecified entity that is generated by a1 and used by a2."})
	@inverse({"informed"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Communication", "http://www.w3.org/ns/prov#qualifiedCommunication"})
	@Iri("http://www.w3.org/ns/prov#wasInformedBy")
	Set<Activity> getProvWasInformedBy();
	/** An activity a2 is dependent on or informed by another activity a1, by way of some unspecified entity that is generated by a1 and used by a2. */
	@category({"starting-point"})
	@label({"wasInformedBy"})
	@component({"entities-activities"})
	@comment({"An activity a2 is dependent on or informed by another activity a1, by way of some unspecified entity that is generated by a1 and used by a2."})
	@inverse({"informed"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Communication", "http://www.w3.org/ns/prov#qualifiedCommunication"})
	@Iri("http://www.w3.org/ns/prov#wasInformedBy")
	void setProvWasInformedBy(Set<? extends Activity> provWasInformedBy);

	/** Start is when an activity is deemed to have started. A start may refer to an entity, known as trigger, that initiated the activity. */
	@category({"expanded"})
	@label({"wasStartedBy"})
	@component({"entities-activities"})
	@comment({"Start is when an activity is deemed to have started. A start may refer to an entity, known as trigger, that initiated the activity."})
	@inverse({"started"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Start", "http://www.w3.org/ns/prov#qualifiedStart"})
	@Iri("http://www.w3.org/ns/prov#wasStartedBy")
	Set<Entity> getProvWasStartedBy();
	/** Start is when an activity is deemed to have started. A start may refer to an entity, known as trigger, that initiated the activity. */
	@category({"expanded"})
	@label({"wasStartedBy"})
	@component({"entities-activities"})
	@comment({"Start is when an activity is deemed to have started. A start may refer to an entity, known as trigger, that initiated the activity."})
	@inverse({"started"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Start", "http://www.w3.org/ns/prov#qualifiedStart"})
	@Iri("http://www.w3.org/ns/prov#wasStartedBy")
	void setProvWasStartedBy(Set<? extends Entity> provWasStartedBy);

}
