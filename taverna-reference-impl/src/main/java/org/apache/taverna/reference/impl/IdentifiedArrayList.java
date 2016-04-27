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
package org.apache.taverna.reference.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.taverna.reference.IdentifiedList;

/**
 * Implementation of IdentifiedList which delegates to an ArrayList for its
 * storage functionality.
 * 
 * @param <T>
 */
public class IdentifiedArrayList<T> extends AbstractEntityImpl implements
		IdentifiedList<T> {
	protected List<T> listDelegate = null;

	// Constructors copied from ArrayList for convenience
	public IdentifiedArrayList() {
		super();
		this.listDelegate = new ArrayList<>();
	}

	public IdentifiedArrayList(Collection<T> c) {
		super();
		this.listDelegate = new ArrayList<>(c);
	}

	public IdentifiedArrayList(int initialCapacity) {
		super();
		this.listDelegate = new ArrayList<>(initialCapacity);
	}

	private void checkUndefinedId() {
		if (this.getId() != null)
			throw new IllegalStateException(
					"Attempt made to modify a list which has already been named");
	}

	@Override
	public boolean add(T e) {
		checkUndefinedId();
		return listDelegate.add(e);
	}

	@Override
	public void add(int index, T element) {
		checkUndefinedId();
		listDelegate.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		checkUndefinedId();
		return listDelegate.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		checkUndefinedId();
		return listDelegate.addAll(index, c);
	}

	@Override
	public void clear() {
		checkUndefinedId();
		listDelegate.clear();
	}

	@Override
	public boolean contains(Object o) {
		return listDelegate.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return listDelegate.containsAll(c);
	}

	@Override
	public T get(int index) {
		return listDelegate.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return listDelegate.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return listDelegate.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return listDelegate.iterator();
	}

	@Override
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
			@Override
			public void add(T e) {
				checkUndefinedId();
				iteratorDelegate.add(e);
			}

			@Override
			public boolean hasNext() {
				return iteratorDelegate.hasNext();
			}

			@Override
			public boolean hasPrevious() {
				return iteratorDelegate.hasPrevious();
			}

			@Override
			public T next() {
				return iteratorDelegate.next();
			}

			@Override
			public int nextIndex() {
				return iteratorDelegate.nextIndex();
			}

			@Override
			public T previous() {
				return iteratorDelegate.previous();
			}

			@Override
			public int previousIndex() {
				return iteratorDelegate.previousIndex();
			}

			@Override
			public void remove() {
				checkUndefinedId();
				iteratorDelegate.remove();
			}

			@Override
			public void set(T e) {
				checkUndefinedId();
				iteratorDelegate.set(e);
			}
		};
	}

	@Override
	public ListIterator<T> listIterator() {
		return getCheckedListIterator(listDelegate.listIterator());
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return getCheckedListIterator(listDelegate.listIterator(index));
	}

	@Override
	public boolean remove(Object o) {
		checkUndefinedId();
		return listDelegate.remove(o);
	}

	@Override
	public T remove(int index) {
		checkUndefinedId();
		return listDelegate.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		checkUndefinedId();
		return listDelegate.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		checkUndefinedId();
		return listDelegate.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		checkUndefinedId();
		return listDelegate.set(index, element);
	}

	@Override
	public int size() {
		return listDelegate.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return listDelegate.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return listDelegate.toArray();
	}

	@Override
	public <U> U[] toArray(U[] a) {
		return listDelegate.toArray(a);
	}
}
