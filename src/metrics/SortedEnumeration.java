/**
 *
 * Copyright (C) 2008-2011 Swinburne University of Technology
 *
 * This file is part of jCT - Java Computer Tomograph, developed at
 * the Faculty of Information and Communication Technologies 
 *
 * jCT is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * jCT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GLoo; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package metrics;

import java.util.Iterator;

/**
 * 
 * Returns a sorted collection of generic values, each of which extends Comparable interface.
 *
 */
public class SortedEnumeration< K extends Comparable<K> >
{
	/**
	 * 
	 * Represents a counter in Sorted Enumeration. Responsible for counting the number of key that are 
	 * duplicated. Hence, counts a frequency of occurrences of values.
	 *
	 */
	private class Counter
	{
		private int fValue;
		
		/**
		 * Constructor. Sets the starting a frequency count to 1. 
		 */
		public Counter()
		{
			fValue = 1;
		}
		
		/**
		 * Returns a a frequency count value.
		 * @return  a frequency count value
		 */
		public int getValue()
		{
			return fValue;
		}
		
		/**
		 * Increments a a frequency count value by one.
		 */
		public void increment()
		{
			fValue++;
		}
		
		/**
		 * Decrements a a frequency count value by one.
		 */
		public void decrement()
		{
			fValue--;
		}
		
		/**
		 * Checks whether the value is zero. 
		 * @return <code>true</code> if the current a frequency count value is 0, otherwise <code>false</code>
		 */
		public boolean isZero()
		{
			return fValue == 0;
		}
	}
	
	private RedBlackTreeMap<K,Counter> fTree;
	private int fSize;

	/**
	 * 
	 * Represents an iterator over SortedEnumeration values.
	 *
	 */
	private class SortedEnumerationIterator implements Iterator<K>
	{
		private Iterator<K> fKeys;
		private K fCurrentKey;
		private int fCurrentCount;
	
		/**
		 * Constructor. Instantiates SortedEnumerationIterator with an iterator over the keys, and
		 * sets current key to null.
		 */
		private SortedEnumerationIterator()
		{
			fKeys = fTree.keys().iterator();
			fCurrentKey = null;
		}
		
		/**
		 * Checks whether there is a next element available in the iterator provided. 
		 * @returns <code>true</code> if there is at least one more element in a collection, otherwise returns <code>false</code>
		 */
		public boolean hasNext()
		{
			if ( fCurrentKey == null )
				return fKeys.hasNext();
			else
				return true;
		}
		
		/**
		 * Returns the next available element in a collection. 
		 * @return  next element in a collection
		 */
		public K next()
		{
			K Result = null;
			
			// fetch next value
			if ( fCurrentKey == null )
			{
				if ( fKeys.hasNext() )
				{
					fCurrentKey = fKeys.next();
					fCurrentCount = fTree.getValue( fCurrentKey ).getValue();
				}
				else
					return null;
			}

			Result = fCurrentKey;
			fCurrentCount--;
			if ( fCurrentCount == 0 )
				fCurrentKey = null; // current key exhausted
			
			return Result;
		}
		
		/**
		 * Meant to remove an element from the collection. But as this functionality if not required for this system,
		 * this method has been left intentionally empty.
		 */
		public void remove()
		{
			// intentionally empty
		}
	}

	/**
	 * Constructor. Instantiates SortedEnumeration with a new RedBlackTree over the values and the number of its occurrences.
	 * @see jct.util.RedBlackTreeMap
	 */
	public SortedEnumeration()
	{
		fTree = new RedBlackTreeMap<K,Counter>();
	}
	
	/**
	 * Returns size of the current values collection(RedBlackTree). 
	 * @return  size of the collection
	 */
	public int size()
	{
		return fSize;
	}
	
	/**
	 * Returns a number of elements in a tree.
	 * @return number of elements in a tree
	 */
	public int count()
	{
		return fTree.size();
	}
	
	/**
	 * Checks whether the current collection is empty.
	 * @return <code>true</code> if the are no elements in the collection, otherwise <code>false</code>
	 */
	public boolean isEmpty()
	{
		return fSize == 0;
	}
	
	/**
	 * Checks whether a collection contains a value with a given key.
	 * @param aKey  key to the value to look for
	 * @return <code>true</code> if the value with a given key exists in a collection, otherwise <code>false</code> 
	 */
	public boolean contains( K aKey )
	{
		return fTree.contains( aKey );
	}
	
	/**
	 * Returns a a frequency count value for a given key.
	 * @param aKey  key to retrieve a a frequency count value for
	 * @return  the number of occurrences of this key during the analysis
	 */
	public int getCount( K aKey )
	{
		Counter lValue = fTree.getValue( aKey );
		
		if ( lValue != null )
			return lValue.getValue();
		else
			return 0;
	}

	/**
	 * Inserts a value to the SortedEnumeration with a given key. Checks whether this key is already exists.
	 * If so, the a frequency count for this key increments by one, otherwise a new value is added to the SortedEnumeration with a 
	 * a frequency count value 1.
	 * @param aKey  key to insert
	 * @return <code>true</code> if insertion was successful, otherwise <code>false</code>
	 */
	public boolean insert( K aKey )
	{
		Counter lValue = fTree.getValue( aKey );
		
		if ( lValue != null )
		{
			lValue.increment();
			fSize++;
			return true;
		}
		
		if ( fTree.insert( aKey, new Counter() ) )
		{
			fSize++;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a value from the SortedEnumeration with a given key. Checks whether this key is exists.
	 * If so, the a frequency count for this key increments by one, and method returns <code>true</code> otherwise 
	 * the method returns <code>false</code>
	 * @param aKey  key to remove
	 * @return <code>true</code> if removal was successful, otherwise <code>false</code>
	 */
	public boolean remove( K aKey )
	{
		Counter lValue = fTree.getValue( aKey );
		
		if ( lValue == null )
		{
			return false;
		}

		lValue.decrement();
		fSize--;
		
		if ( lValue.isZero() )
			fTree.remove( aKey );

		return true;
	}
	
	/**
	 * Removes member from a sorted tree with a given key value.
	 * @param aKey key to the RedBlacTree node
	 * @return "true" if a member was successfully removed, otherwise "false"
	 */
	public boolean removeMember( K aKey )
	{
		if( fTree.contains( aKey ) )
		{
			fTree.remove( aKey );
			return true;
		}
		return false;
	}
	
	/**
	 * Returns an Iterable over the keys in SortedEnumeration.
	 * @return  Iterable over SortedEnumeration keys
	 */
	public Iterable<K> keys()
	{
		return fTree.keys();
	}
	
	/**
	 * Returns an Iterable over the values in SortedEnumeration - a frequency counts.
	 * @return  Iterable over SortedEnumeration values and frequency counts
	 * @see jct.util.IterableCreator
	 */
	public Iterable<K> values()
	{
		return new IterableCreator<K> ( new SortedEnumerationIterator() );
	}
}
