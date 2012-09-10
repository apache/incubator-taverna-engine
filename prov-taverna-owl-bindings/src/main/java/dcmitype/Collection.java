package dcmitype;

import dct.Frequency;
import dct.MethodOfAccrual;
import dct.Policy;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import skos.definition;

/** A collection is described as a group; its parts may also be separately described. */
@label({"Collection"})
@definition({"An aggregation of resources."})
@comment({"A collection is described as a group; its parts may also be separately described."})
@Iri("http://purl.org/dc/dcmitype/Collection")
public interface Collection {
	/** The method by which items are added to a collection. */
	@label({"Accrual Method", "Accrual Method"})
	@definition({"The method by which items are added to a collection."})
	@comment({"The method by which items are added to a collection."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/accrualMethod")
	Set<MethodOfAccrual> getDctermsAccrualMethod();
	/** The method by which items are added to a collection. */
	@label({"Accrual Method", "Accrual Method"})
	@definition({"The method by which items are added to a collection."})
	@comment({"The method by which items are added to a collection."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/accrualMethod")
	void setDctermsAccrualMethod(Set<? extends MethodOfAccrual> dctermsAccrualMethod);

	/** The frequency with which items are added to a collection. */
	@label({"Accrual Periodicity", "Accrual Periodicity"})
	@definition({"The frequency with which items are added to a collection."})
	@comment({"The frequency with which items are added to a collection."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/accrualPeriodicity")
	Set<Frequency> getDctermsAccrualPeriodicity();
	/** The frequency with which items are added to a collection. */
	@label({"Accrual Periodicity", "Accrual Periodicity"})
	@definition({"The frequency with which items are added to a collection."})
	@comment({"The frequency with which items are added to a collection."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/accrualPeriodicity")
	void setDctermsAccrualPeriodicity(Set<? extends Frequency> dctermsAccrualPeriodicity);

	/** The policy governing the addition of items to a collection. */
	@label({"Accrual Policy", "Accrual Policy"})
	@definition({"The policy governing the addition of items to a collection."})
	@comment({"The policy governing the addition of items to a collection."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/accrualPolicy")
	Set<Policy> getDctermsAccrualPolicies();
	/** The policy governing the addition of items to a collection. */
	@label({"Accrual Policy", "Accrual Policy"})
	@definition({"The policy governing the addition of items to a collection."})
	@comment({"The policy governing the addition of items to a collection."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/accrualPolicy")
	void setDctermsAccrualPolicies(Set<? extends Policy> dctermsAccrualPolicies);

}
