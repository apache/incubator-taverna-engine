package net.sf.taverna.t2.visit;

import java.util.List;

/**
 * A Visitor can perform a visit of a VisitKind on a object. It can return a
 * VisitReport giving details of the result of the visit, A Visitor may be time
 * consuming, in which case it is only performed upon user request.
 * 
 * @author alanrw
 * 
 * @param <T> The type of the objects being visited.
 */
public interface Visitor<T> {
	/**
	 * Returns true if the visitor can visit the specified object.
	 * 
	 * @param o
	 *            The object that might be visited
	 * @return true is a visit is possible from this Visitor.
	 */
	boolean canVisit(Object o);

	/**
	 * Visit an object which has the specified ancestry (list of parents) and
	 * possibly return a VisitReport detailing the result of the visit.
	 * 
	 * @param o
	 *            The object to visit
	 * @param ancestry
	 *            A list of the ancestors of the object with the immediate
	 *            parent at the start of the list.
	 * @return A VisitReport detailing the result of the visit.
	 */
	VisitReport visit(T o, List<Object> ancestry);

	/**
	 * An indication if the visit would take sufficient time that it should only
	 * be run upon user request
	 * 
	 * @return
	 */
	boolean isTimeConsuming();
}
