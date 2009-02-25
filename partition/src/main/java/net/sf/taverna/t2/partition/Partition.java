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
package net.sf.taverna.t2.partition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

/**
 * A partition represents a set of items which can be exclusively classified
 * into one or more distinct subsets along with the algorithm to perform this
 * subset operation.
 * 
 * @author Tom Oinn
 * 
 * @param <ItemType>
 *            all items in the underlying set of which this is a subset are
 *            instances of this type or can be cast to it according to
 *            conventional java language rules.
 * @param <PartitionValueType>
 *            the partition value type used by the parent partition algorithm to
 *            create this partition object. As an example, if this partition
 *            object represented all those entries with a particular host
 *            institution this would be a String or possibly URL.
 * @param <ChildPartitionValueType>
 *            the partition value type used by this partition's partition
 *            algorithm to create sub-partitions, it's used in the signature
 *            here because ordering of children is done based on these values.
 *            Any child partition will have a getPartitionValue return type
 *            cast-able to this type.
 */
public class Partition<ItemType extends Comparable, PartitionValueType, ChildPartitionValueType> {

	// A comparator operating over the value type of the child partitions and
	// used to order them as created or to re-order on a change of this property
	private Comparator<ChildPartitionValueType> childPartitionOrder = null;

	// Back reference to the root partition of which this is a direct or
	// indirect sub-partition. If this *is* the root partition this points to
	// self
	protected RootPartition<ItemType> root;

	// A subset of the parent's member set containing items which have been
	// allocated to this partition by the parent's partition algorithm. The
	// partition is specified by the partitionValue field.
	private List<ItemType> members;

	// The parent partition of which this is a subset
	private Partition<ItemType, ?, PartitionValueType> parent;

	// Specification of the partition in terms of the parent's partitioning
	// algorithm
	private PartitionValueType partitionValue;

	// List of partitioning algorithms to be applied to this subset to create
	// further partitions, the algorithm at index 0 is the one used for this
	// partition, all others are passed in to the constructors for
	// sub-partitions
	protected List<PartitionAlgorithm<?>> partitionAlgorithms;

	// An initially empty list of sub-partitions created by the head element of
	// the partition algorithm list
	protected List<Partition<ItemType, ChildPartitionValueType, ?>> children;

	// Path from this node back to the root, initialised on first access and
	// cached
	private List<Partition<ItemType, ?, ?>> partitionPath = null;

	// For leaf partitions this is equal to the number of items in the member
	// set, for all other partitions it is the sum of the item count of all
	// child partitions
	protected int itemCount = 0;

	/**
	 * Construct a new Partition, this is used by the RootPartition and by this
	 * class to construct the recursively sub-divided partition structure based
	 * on the rules encapsulated by the partition algorithm list.
	 * 
	 * @param parent
	 *            parent partition of which this is a subset
	 * @param pa
	 *            partition algorithm list, with the algorithm used to create
	 *            child partitions of this one at position 0, if this list is
	 *            empty this is a leaf partition.
	 * @param root
	 *            reference to the RootPartition acting as the externally
	 *            visible front-end to this structure
	 * @param pv
	 *            the value which the parent's partition algorithm has assigned
	 *            to this partition. This must be interpreted in the context of
	 *            the parent's first partition algorithm for display and other
	 *            purposes
	 */
	protected Partition(Partition<ItemType, ?, PartitionValueType> parent,
			List<PartitionAlgorithm<?>> pa, RootPartition<ItemType> root,
			PartitionValueType pv) {
		this.root = root;
		this.members = new ArrayList<ItemType>();
		this.parent = parent;
		this.partitionValue = pv;
		this.partitionAlgorithms = pa;
		this.children = new ArrayList<Partition<ItemType, ChildPartitionValueType, ?>>();
	}

	/**
	 * Return the number of items below this node in the partition tree; in the
	 * case of leaf partitions this is the number of items in the member set,
	 * for non-leaf partitions it is the sum of the item count for all immediate
	 * child partitions.
	 */
	public int getItemCount() {
		return this.itemCount;
	}

	/**
	 * Sub-partitions of this partition are ordered based on a comparator over
	 * the child partition value type.
	 * 
	 * @return a comparator over child partition value types, or null if no
	 *         comparator has been specified (by default this returns null)
	 */
	public Comparator<ChildPartitionValueType> getChildPartitionOrder() {
		return this.childPartitionOrder;
	}

	/**
	 * Set a comparator for child partition ordering - if the supplied
	 * comparator is different to the current one this will also trigger a
	 * re-order of all child partitions and corresponding events in the root
	 * partition's tree view. In the current implementation this is the very
	 * broad 'tree structure changed' event for this node in the tree view.
	 * 
	 * @param order
	 *            a new comparator to order child partitions
	 */
	public void setChildPartitionOrder(Comparator<ChildPartitionValueType> order) {
		if (!order.equals(childPartitionOrder)) {
			childPartitionOrder = order;
			sortChildPartitions();
		}
	}

	/**
	 * Return the parent partition of which this is a sub-partition, or null if
	 * this is the root partition.
	 */
	public Partition<ItemType, ?, PartitionValueType> getParent() {
		return this.parent;
	}

	/**
	 * The parent partition created this partition based on a particular value
	 * of a property of the members of the sub-partition. This returns that
	 * value, and is the result returned from the parent partition's first
	 * partition algorithm when run on all members of this partition or its
	 * direct or indirect sub-partitions.
	 */
	public PartitionValueType getPartitionValue() {
		return this.partitionValue;
	}

	@Override
	public String toString() {
		if (getParent() != null) {
			// query type
			String string = this.getParent().getPartitionAlgorithms().get(0)
					.toString();
			// result of query
			String string2 = this.partitionValue.toString();
			return string2 + " (" + getItemCount() + ")";
		} else {
			// This is for a root partition, loop through its children to return
			// the correct number when running a new query
			int items = 0;
			for (Partition child : children) {
				items = items + child.getItemCount();
			}
			String queryType = getPartitionAlgorithms().get(0).toString();
			// return "Activities which match query = " + getItemCount();
			return "Available activities (" + items + ")";
			//+ ", query by "
				//	+ queryType;
		}
	}

	/**
	 * Return a list of Partition objects from the root (at index 0) to this
	 * node at the final position in the list. Computes the first time then
	 * caches, as it should be impossible for this to be modified without
	 * recreation of the entire structure from scratch.
	 */
	public synchronized List<Partition<ItemType, ?, ?>> getPartitionPath() {
		if (partitionPath == null) {
			List<Partition<ItemType, ?, ?>> al = new ArrayList<Partition<ItemType, ?, ?>>();
			Partition<ItemType, ?, ?> activePartition = this;
			al.add(activePartition);
			while (activePartition.getParent() != null) {
				al.add(0, activePartition.getParent());
				activePartition = activePartition.getParent();
			}
			partitionPath = al;
		}
		return partitionPath;
	}

	/**
	 * If this is a leaf partition, defined as one with an empty list of
	 * partition algorithms, then this method returns the set of all items which
	 * have been classified as belonging to this leaf partition. For non-leaf
	 * partitions it will return an empty set.
	 */
	public final List<ItemType> getMembers() {
		return Collections.unmodifiableList(this.members);
	}

	/**
	 * The list of partition algorithms applicable to this node (at index 0) and
	 * subsequent downstream sub-partitions of it. If this is empty then the
	 * partition is a leaf partition.
	 */
	public final List<PartitionAlgorithm<?>> getPartitionAlgorithms() {
		return Collections.unmodifiableList(partitionAlgorithms);
	}

	/**
	 * Sub-partitions of this partition defined by the partition algorithm at
	 * index 0 of the list. If this is a leaf partition this will always be
	 * empty.
	 */
	public final List<Partition<ItemType, ChildPartitionValueType, ?>> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Inject an item into this partition, if there are partition algorithms in
	 * the partition algorithm list (i.e. this is not a leaf) this will
	 * recursively call the same method on child partitions or create new
	 * partitions where there are none that match the value from the partition
	 * algorithm. If this is a leaf partition the item is added to the member
	 * set. The list is sorted when adding an item and it is inserted in the
	 * appropriate place
	 * 
	 * @param item
	 *            the item to add to the partition structure.
	 */
	@SuppressWarnings("unchecked")
	protected synchronized void addItem(ItemType item) {
		if (partitionAlgorithms.isEmpty()) {
			// itemCount = 0;
			// Allocate directly to member set, no further partitioning
			members.add(item);
			Collections.sort(members);
			int indexOf = members.indexOf(item);
			// root.treeNodesInserted(new TreeModelEvent(this, getTreePath(),
			// new int[] { members.size() - 1 }, new Object[] { item }));
			root.treeNodesInserted(new TreeModelEvent(this, getTreePath(),
					new int[] { indexOf }, new Object[] { item }));
			// Increment item count for all partitions in the partition path
			for (Partition<ItemType, ?, ?> p : getPartitionPath()) {
				synchronized (p) {
					p.itemCount++;
					root.treeNodesChanged(new TreeModelEvent(this, p
							.getTreePath()));
				}
			}

			// Cache the storage of this item to this partition in the root
			// partition for more efficient removal if required (saves having to
			// search the entire partition tree, although that wouldn't be too
			// painful if it was required this is faster at the cost of a few
			// bytes of memory)
			root.itemStoredAt(item, this);
			// TODO - when the tree model covers items on the leaf nodes we'll
			// want to message it here as well.
		} else {
			PartitionAlgorithm<ChildPartitionValueType> pa;
			pa = (PartitionAlgorithm<ChildPartitionValueType>) partitionAlgorithms
					.get(0);
			ChildPartitionValueType pvalue = pa.allocate(item, root
					.getPropertyExtractorRegistry());
			// FIXME not sure how to do this since you seem to have to add the
			// items to the partition if you want to then search them again,
			// maybe you need a non-showing partition or something?
			// //if it is a failed search then don't bother adding to the
			// partition
			// if (pvalue.toString().equalsIgnoreCase("No match")) {
			// return;
			// }
			// See if there's a partition with this value already in the child
			// partition list
			for (Partition<ItemType, ChildPartitionValueType, ?> potentialChild : children) {
				if (potentialChild.getPartitionValue().equals(pvalue)) {
					potentialChild.addItem(item);
					return;
				}
			}
			// If not we have to create a new sub-partition
			List<PartitionAlgorithm<?>> tail = new ArrayList<PartitionAlgorithm<?>>();
			for (int i = 1; i < partitionAlgorithms.size(); i++) {
				tail.add(partitionAlgorithms.get(i));
			}
			Partition<ItemType, ChildPartitionValueType, ?> newPartition = new Partition(
					this, tail, this.root, pvalue);
			// Insert the new partition in the correct place according to the
			// comparator currently installed, or at the end if none exists or
			// the list is empty
			if (childPartitionOrder == null || children.isEmpty()) {
				children.add(newPartition);
				root.treeNodesInserted(new TreeModelEvent(this, getTreePath(),
						new int[] { children.indexOf(newPartition) },
						new Object[] { newPartition }));
			} else {
				boolean foundIndex = false;
				for (int i = 0; i < children.size(); i++) {
					ChildPartitionValueType existingPartitionValue = children
							.get(i).getPartitionValue();
					if (childPartitionOrder.compare(pvalue,
							existingPartitionValue) < 0) {
						children.add(i, newPartition);
						root.treeNodesInserted(new TreeModelEvent(this,
								getTreePath(), new int[] { i },
								new Object[] { newPartition }));
						if (i != 0) {
							root.treeStructureChanged(new TreeModelEvent(this,
									getTreePath()));
						}
						foundIndex = true;
						break;
					}
				}
				if (!foundIndex) {
					// Fallen off the end of the array without finding something
					// with greater index than the new partition so we add it at
					// the
					// end (by definition this is the largest value according to
					// the
					// comparator)
					children.add(newPartition);
					root.treeNodesInserted(new TreeModelEvent(this,
							getTreePath(), new int[] { children
									.indexOf(newPartition) },
							new Object[] { newPartition }));
				}
			}
			// Add the item to the new partition to trigger creation of any
			// sub-partitions required
			newPartition.addItem(item);
		}
	}

	/**
	 * Remove an item from the member set
	 * 
	 * @param item
	 *            the item to remove
	 */
	protected void removeMember(ItemType item) {
		this.members.remove(item);
	}

	/**
	 * Re-order the child partitions based on the comparator, if no comparator
	 * has been defined this method does nothing. Tree structure changed
	 * messages are fired from this node in the tree view if the comparator is
	 * defined even if no nodes have been changed (lazy but not too much of an
	 * issue I suspect)
	 */
	protected synchronized final void sortChildPartitions() {
		if (this.childPartitionOrder == null) {
			// Can't do anything unless the comparator is set appropriately
			return;
		}
		Comparator<Partition<ItemType, ChildPartitionValueType, ?>> comparator = new Comparator<Partition<ItemType, ChildPartitionValueType, ?>>() {
			public int compare(
					Partition<ItemType, ChildPartitionValueType, ?> o1,
					Partition<ItemType, ChildPartitionValueType, ?> o2) {
				// FIXME is this really safe to do? It's fairly specific to our
				// case. Doesn't seem very generic
				if (o1.getPartitionValue().toString().equalsIgnoreCase(
						"no value")) {
					// No value so put it to the end
					return 1;
				}
				return childPartitionOrder.compare(o1.getPartitionValue(), o2
						.getPartitionValue());
			}
		};
		Collections.<Partition<ItemType, ChildPartitionValueType, ?>> sort(
				children, comparator);
		// Message the root that the node structure under this node has changed
		// (this is a bit lazy and we could almost certainly be more clever here
		// as the nodes have been removed and added to re-order them)
		root.treeStructureChanged(new TreeModelEvent(this, getTreePath()));
	}

	/**
	 * Return a TreePath object with this node as the final entry in the path
	 */
	protected final synchronized TreePath getTreePath() {
		// System.out.println("Getting path..."+this.toString());
		// for (Partition<?,?,?> p : getPartitionPath()) {
		// System.out.println(p.toString());
		// }
		return new TreePath(getPartitionPath().toArray());
	}

	// public void sortItems() {
	// System.out.println("sorting the items");
	// synchronized (members) {
	// List<ItemType> oldOrder = new ArrayList<ItemType>(members);
	// Collections.sort(oldOrder);
	//
	// for (ItemType item : oldOrder) {
	// removeMember(item);
	// }
	// for (ItemType item : oldOrder) {
	// addItem(item);
	// }
	// }
	//
	// }

}
