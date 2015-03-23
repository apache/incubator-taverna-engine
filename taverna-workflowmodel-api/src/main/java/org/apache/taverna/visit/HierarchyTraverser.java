/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.visit;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.synchronizedMap;
import static org.apache.taverna.visit.VisitReport.findAncestor;
import static org.apache.taverna.visit.VisitReport.getWorstStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.taverna.annotation.HierarchyRole;
import org.apache.taverna.annotation.HierarchyTraversal;
import org.apache.taverna.visit.VisitReport.Status;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.NestedDataflow;

import org.apache.log4j.Logger;

/**
 * A HierarchyTraverser allows the traversal of the parent -> child hierarchy
 * (as indicated by annotations) and performs visits conforming to the set of
 * VisitKinds.
 * 
 * @author alanrw
 */
public class HierarchyTraverser {
	private static Logger logger = Logger.getLogger(HierarchyTraverser.class);

	/**
	 * A mapping from the class of an object to the set of names of methods that
	 * will return children of instances of the object. Note that this has to be
	 * done by String because of problems with annotations on overridden
	 * methods.
	 */
	private static Map<Class<?>, Set<String>> childrenMethods = synchronizedMap(new WeakHashMap<Class<?>, Set<String>>());

	/**
	 * The set of visitors that can perform visits of one or more of a set of
	 * VisitKind.
	 */
	protected Set<Visitor<?>> visitors;

	/**
	 * Create a HierarchyTraverser that can perform visits of the specified set
	 * of VisitKind.
	 * 
	 * @param descriptions
	 */
	public HierarchyTraverser(Set<Visitor<?>> visitors) {
		this.visitors = visitors;
	}

	/**
	 * Add a new VisitReport to a set of VisitReport. If the VisitReport has
	 * sub-reports then, unless the report is about a Dataflow, then the
	 * VisitReport itself is ignored and the sub-reports added instead. If the
	 * VisiReport has no sub-reports, or it is a report about a Dataflow, then
	 * the VisitReport is added to the set.
	 * 
	 * @param reports
	 *            The set of reports to which to add the useful VisitReports
	 *            corresponding to the new VisitReport.
	 * @param newReport
	 *            The VisitReport to be added (or whose sub-reports are to be
	 *            added) to the set of reports.
	 */
	private void addReport(Set<VisitReport> reports, VisitReport newReport) {
		if (newReport == null)
			return;
		Collection<VisitReport> subReports = newReport.getSubReports();
		if ((subReports == null) || subReports.size() == 0)
			reports.add(newReport);
		else if (!(newReport.getSubject() instanceof Dataflow))
			for (VisitReport r : subReports)
				addReport(reports, r);
	}

	/**
	 * Change the subject of a VisitReport. This is currently done to change a
	 * VisitReport about an Activity to be about its containing Processor. If
	 * the VisitReport has sub-reports then their subject is also patched. It is
	 * not obvious that this should be done here.
	 * 
	 * @param vr
	 *            The VisitReport for which to change the subject
	 * @param newSubject
	 *            The new subject of the VisitReport and its sub-reports
	 */
	private void patchSubject(VisitReport vr, Object newSubject) {
		vr.setSubject(newSubject);
		Collection<VisitReport> subReports = vr.getSubReports();
		if (subReports != null)
			for (VisitReport child : subReports)
				patchSubject(child, newSubject);
	}

	private void patchCheckTime(VisitReport vr, long time) {
		vr.setCheckTime(time);
		Collection<VisitReport> subReports = vr.getSubReports();
		if (subReports != null)
			for (VisitReport child : subReports)
				patchCheckTime(child, time);
	}

	/**
	 * Change a VisitReport and its sub-reports (if any) to indicate that the
	 * visit was time-consuming. This is done to ensure that the time-consuming
	 * indication of the Visitor is used on the VisitReport.
	 * 
	 * @param vr
	 *            The VisitReport for which to set the time-consuming flag.
	 */
	private void patchTimeConsuming(VisitReport vr) {
		vr.setWasTimeConsuming(true);
		Collection<VisitReport> subReports = vr.getSubReports();
		if (subReports != null)
			for (VisitReport child : subReports)
				patchTimeConsuming(child);
	}

	/**
	 * Carry out the appropriate visits on an object and then traverse down the
	 * hierarchy of its children.
	 * 
	 * @param o
	 *            The object to visit
	 * @param ancestry
	 *            The, possibly empty, list of the ancestors (ordered parents)
	 *            of the object with the most recent ancestor being the first in
	 *            the list.
	 * @param reports
	 *            The set to which to add reports generated about the object and
	 *            its descendents
	 * @param includeTimeConsuming
	 *            Whether to include visits that are time-consuming.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void traverse(Object o, List ancestry, Set<VisitReport> reports,
			boolean includeTimeConsuming) {
		/*
		 * For each visitor that is able to do visits for the set of VisitKind
		 * specified for the HierarchyTraverser
		 */
		for (Visitor v : visitors)
			/*
			 * If time consuming visits are allowed or the visitor is not time
			 * consuming, and the visitor can visit the specified object
			 */

			if ((includeTimeConsuming || !v.isTimeConsuming()) && v.canVisit(o)) {
				// Make the visitor visit the object
				VisitReport report = null;
				try {
					report = v.visit(o, ancestry);
				} catch (NullPointerException|ClassCastException e) {
					logger.error("Visit threw exception", e);
				}

				if (report == null)
					continue;

				patchCheckTime(report, currentTimeMillis());

				/*
				 * If the current object is an Activity then change the report
				 * so that its subject is the Processor containing the Activity
				 */
				if (o instanceof Activity) {
					Processor p = (Processor) findAncestor(ancestry,
							Processor.class);
					if (p != null)
						patchSubject(report, p);
				}
				/*
				 * Note in the VisitReport if it was caused by a time-consuming
				 * visitor
				 */
				if (v.isTimeConsuming() && (report != null))
					patchTimeConsuming(report);
				/*
				 * Add the VisitReport and its sub-reports, if any, to the set
				 * of VisitReports
				 */
				addReport(reports, report);
			}

		/*
		 * If the object is a nested dataflow activity then traverse the
		 * dataflow that is nested. Take the reports about the sub-dataflow and,
		 * if there are problems with it, create a DataflowCollation report
		 * about the nested dataflow activity (or to be more precise the
		 * Procesor containing it.)
		 */
		if (o instanceof NestedDataflow) {
			NestedDataflow nestedDataflow = (NestedDataflow) o;
			Dataflow subFlow = nestedDataflow.getNestedDataflow();
			Set<VisitReport> subReports = new HashSet<>();
			traverse(subFlow, new ArrayList<Object>(), subReports,
					includeTimeConsuming);
			Processor p = (Processor) findAncestor(ancestry, Processor.class);
			if (p != null) {
				Status worstStatus = getWorstStatus(subReports);
				if (!worstStatus.equals(Status.OK)) {
					VisitReport report = new VisitReport(
							DataflowCollation.getInstance(),
							p,
							(worstStatus.equals(Status.WARNING) ? "Warnings in nested workflow"
									: "Errors in nested workflow"),
							DataflowCollation.NESTED_ISSUES, worstStatus,
							subReports);
					report.setProperty("dataflowIdentifier",
							subFlow.getIdentifier());
					report.setWasTimeConsuming(includeTimeConsuming);
					reports.add(report);
				}
			}
		}

		// Now move on to traversing the descendents

		/*
		 * For every child-getting method for this object, try to get the
		 * children and add them into a set.
		 */
		Set<String> methodNames = getMethods(o);
		Set<Object> children = new HashSet<>();
		for (Method m : o.getClass().getMethods())
			if (methodNames.contains(m.getName())) {
				Object methodResult = null;
				try {
					methodResult = m.invoke(o);
				} catch (IllegalArgumentException | IllegalAccessException
						| InvocationTargetException e) {
					logger.error(e);
				}
				/*
				 * If the method did not produce a singleton but instead a List
				 * or similar then add the members of the list.
				 */
				children.addAll(getLeafs(methodResult));
			}

		/*
		 * For every child of the current object, traverse that object and get
		 * reports about it and its descendents.
		 */
		ArrayList<Object> newAncestry = new ArrayList<>();
		newAncestry.add(o);
		newAncestry.addAll(ancestry);
		for (Object c : children)
			traverse(c, newAncestry, reports, includeTimeConsuming);
	}

	/**
	 * Determine the set of singletons corresponding to an object. If the object
	 * is a singleton then a set containing just the object is returned. If the
	 * object is iterable then the singletons of the elements of the iteration
	 * are returned.
	 * 
	 * @param o
	 *            The object.
	 * @return The set of singletons
	 */
	@SuppressWarnings("unchecked")
	private static Set<Object> getLeafs(Object o) {
		Set<Object> result = new HashSet<>();
		if (o instanceof Iterable)
			for (Object element : (Iterable<Object>) o)
				result.addAll(getLeafs(element));
		else
			result.add(o);
		return result;
	}

	/**
	 * Determine the set of names of child-getting methods for a given object
	 * 
	 * @param o
	 *            The object to consider.
	 * @return The set of names of child-getting methods
	 */
	private static Set<String> getMethods(Object o) {
		Class<?> c = o.getClass();
		return getMethodsForClass(c);
	}

	/**
	 * Determine the set of names of child-getting methods for a given Class.
	 * This includes the names of methods from interfaces and super-classes.
	 * 
	 * @param c
	 *            The class to consider
	 * @return The set of names of child-getting methods for the class
	 */
	private static synchronized Set<String> getMethodsForClass(Class<?> c) {
		if (!childrenMethods.containsKey(c)) {
			Set<String> result = new HashSet<>();
			result.addAll(getExplicitMethodsForClass(c));
			for (Class<?> i : c.getInterfaces())
				result.addAll(getMethodsForClass(i));
			Class<?> s = c.getSuperclass();
			if (s != null)
				result.addAll(getMethodsForClass(s));
			childrenMethods.put(c, result);
		}
		return childrenMethods.get(c);
	}

	/**
	 * Determine the set of names of child-getting methods explicitly identified
	 * for an Interface or a Class.
	 * 
	 * @param c
	 *            The Interface or Class to consider
	 * @return The set of names of child-getting methods.
	 */
	private static Collection<? extends String> getExplicitMethodsForClass(
			Class<?> c) {
		Method[] methods = c.getDeclaredMethods();
		Set<String> result = new HashSet<>();

		for (Method m : methods)
			if (m.getParameterTypes().length == 0) {
				HierarchyTraversal ht = m
						.getAnnotation(HierarchyTraversal.class);
				if (ht != null
						&& Arrays.asList(ht.role()).contains(
								HierarchyRole.CHILD))
					result.add(m.getName());
			}
		return result;
	}
}
