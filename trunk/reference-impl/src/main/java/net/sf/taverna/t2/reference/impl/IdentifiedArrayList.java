/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.reference.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sf.taverna.t2.reference.IdentifiedList;

/**
 * Implementation of IdentifiedList which delegates to an ArrayList for its
 * storage functionality.
 * 
 * @author Tom Oinn
 * 
 * @param <T>
 */
public class IdentifiedArrayList<T> extends AbstractEntityImpl implements
		IdentifiedList<T> {

	protected List<T> listDelegate = null;

	// Constructors copied from ArrayList for convenience
	public IdentifiedArrayList() {
		super();
		this.listDelegate = new ArrayList<T>();
	}

	public IdentifiedArrayList(Collection<T> c) {
		super();
		this.listDelegate = new ArrayList<T>(c);
	}

	public IdentifiedArrayList(int initialCapacity) {
		super();
		this.listDelegate = new ArrayList<T>(initialCapacity);
	}

	private void checkUndefinedId() {
		if (this.getId() != null) {
			throw new IllegalStateException(
					"Attempt made to modify a list which has already been named");
		}
	}

	public boolean add(T e) {
		checkUndefinedId();
		return listDelegate.add(e);
	}

	public void add(int index, T element) {
		checkUndefinedId();
		listDelegate.add(index, element);
	}

	public boolean addAll(Collection<? extends T> c) {
		checkUndefinedId();
		return listDelegate.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		checkUndefinedId();
		return listDelegate.addAll(index, c);
	}

	public void clear() {
		checkUndefinedId();
		listDelegate.clear();
	}

	public boolean contains(Object o) {
		return listDelegate.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return listDelegate.containsAll(c);
	}

	public T get(int index) {
		return listDelegate.get(index);
	}

	public int indexOf(Object o) {
		return listDelegate.indexOf(o);
	}

	public boolean isEmpty() {
		return listDelegate.isEmpty();
	}

	public Iterator<T> iterator() {
		return listDelegate.iterator();
	}

	public int lastIndexOf(Object o) {
		return listDelegate.lastIndexOf(o);
	}

	/**
	 * The ListIterator can modify the list contents, so wrap the delegate's
	 * list iterator and use as a delegate itself, checking for null ID on
	 * operations which set list properties.
	 * 
	 * @param iteratorDelegate
	 *            ListIterator to wrap.
	 * @return wrapped ListIterator which throws IllegalStateException on calls
	 *         which modify the list if the ID has been set to a non-null value
	 */
	private ListIterator<T> getCheckedListIterator(
			final ListIterator<T> iteratorDelegate) {
		return new ListIterator<T>() {
			public void add(T e) {
				checkUndefinedId();
				iteratorDelegate.add(e);
			}

			public boolean hasNext() {
				return iteratorDelegate.hasNext();
			}

			public boolean hasPrevious() {
				return iteratorDelegate.hasPrevious();
			}

			public T next() {
				return iteratorDelegate.next();
			}

			public int nextIndex() {
				return iteratorDelegate.nextIndex();
			}

			public T previous() {
				return iteratorDelegate.previous();
			}

			public int previousIndex() {
				return iteratorDelegate.previousIndex();
			}

			public void remove() {
				checkUndefinedId();
				iteratorDelegate.remove();
			}

			public void set(T e) {
				checkUndefinedId();
				iteratorDelegate.set(e);
			}
		};
	}

	public ListIterator<T> listIterator() {
		return getCheckedListIterator(listDelegate.listIterator());
	}

	public ListIterator<T> listIterator(int index) {
		return getCheckedListIterator(listDelegate.listIterator(index));
	}

	public boolean remove(Object o) {
		checkUndefinedId();
		return listDelegate.remove(o);
	}

	public T remove(int index) {
		checkUndefinedId();
		return listDelegate.remove(index);
	}

	public boolean removeAll(Collection<?> c) {
		checkUndefinedId();
		return listDelegate.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		checkUndefinedId();
		return listDelegate.retainAll(c);
	}

	public T set(int index, T element) {
		checkUndefinedId();
		return listDelegate.set(index, element);
	}

	public int size() {
		return listDelegate.size();
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return listDelegate.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return listDelegate.toArray();
	}

	public <U> U[] toArray(U[] a) {
		return listDelegate.toArray(a);
	}

}
