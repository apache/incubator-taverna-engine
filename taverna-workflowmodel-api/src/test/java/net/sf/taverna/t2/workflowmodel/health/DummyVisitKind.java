/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.health;

import net.sf.taverna.t2.visit.VisitKind;
import net.sf.taverna.t2.visit.Visitor;

/**
 * @author alanrw
 *
 */
public class DummyVisitKind extends VisitKind {
	@Override
	public Class<? extends Visitor<?>> getVisitorClass() {
		return null;
	}

	private static class Singleton {
		private static DummyVisitKind instance = new DummyVisitKind();
	}
	
	public static DummyVisitKind getInstance() {
		return Singleton.instance;
	}
}
