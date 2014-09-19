package net.sf.taverna.t2.visit;

/**
 * A type of visit that can be made e.g. a health check.
 * 
 * @author alanrw
 */
public abstract class VisitKind {
	/**
	 * The class that all visitors that extend/implement if they make this type
	 * of visit.
	 * 
	 * @return
	 */
	public abstract Class<? extends Visitor<?>> getVisitorClass();
}
