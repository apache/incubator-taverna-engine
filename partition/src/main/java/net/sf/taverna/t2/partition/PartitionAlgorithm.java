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

/**
 * Interface for classes which can partition a set of objects into subsets according
 * to some embedded partitioning rule
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 * 
 * @param ValueType
 *            the java type of values used to represent the distinct partitions
 *            created by this algorithm, in many cases these will be primitive
 *            java types such as String but they could represent ranges of
 *            values in the case of binning of continuous quantities etc.
 */
public interface PartitionAlgorithm<ValueType> {

	/**
	 * Given an object to classify return the value of the partition into which
	 * the object falls.
	 * 
	 * @param newItem
	 * @return
	 */
	ValueType allocate(Object newItem, PropertyExtractorRegistry reg);

}
