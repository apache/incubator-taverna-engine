package tavernaprov;

import java.lang.Object;
import java.math.BigInteger;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@subClassOf({"http://www.w3.org/2011/content#Content"})
@Iri("http://ns.taverna.org.uk/2012/tavernaprov/Content")
public interface Content extends cnt.Content {
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/byteCount")
	Set<BigInteger> getTavernaprovByteCount();
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/byteCount")
	void setTavernaprovByteCount(Set<? extends BigInteger> tavernaprovByteCount);

	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/checksum")
	Set<Object> getTavernaprovChecksums();
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/checksum")
	void setTavernaprovChecksums(Set<?> tavernaprovChecksums);

	@subPropertyOf({"http://ns.taverna.org.uk/2012/tavernaprov/checksum"})
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/sha1")
	Set<byte[]> getTavernaprovSha1s();
	@subPropertyOf({"http://ns.taverna.org.uk/2012/tavernaprov/checksum"})
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/sha1")
	void setTavernaprovSha1s(Set<? extends byte[]> tavernaprovSha1s);

	@subPropertyOf({"http://ns.taverna.org.uk/2012/tavernaprov/checksum"})
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/sha512")
	Set<byte[]> getTavernaprovSha512s();
	@subPropertyOf({"http://ns.taverna.org.uk/2012/tavernaprov/checksum"})
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/sha512")
	void setTavernaprovSha512s(Set<? extends byte[]> tavernaprovSha512s);

}
