package net.sf.taverna.t2.visit;

/**
 * This visit kind is a dummy for collecting together the information associated
 * with a nested workflow.
 * 
 * @author alanrw
 */
public class DataflowCollation extends VisitKind {
	public static final int NESTED_ISSUES = 1;

	/*
	 * (non-Javadoc)
	 * 
	 * There are no visitors that can perform a DataflowCollation visit. This
	 * is, instead done within the HierarchyTraverser code iteself.
	 * 
	 * @see net.sf.taverna.t2.visit.VisitKind#getVisitorClass()
	 */
	@Override
	public Class<? extends Visitor<?>> getVisitorClass() {
		return null;
	}

	private static class Singleton {
		private static DataflowCollation instance = new DataflowCollation();
	}

	public static DataflowCollation getInstance() {
		return Singleton.instance;
	}
}
