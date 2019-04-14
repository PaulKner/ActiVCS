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
 * Represents an object that maps keys to values. Each key an map to at most one value.
 * Have similar functionality to HashMap. 
 * Takes two parameters that are represented as generic data types. 
 * The first parameter extends <code>Comparable</code> data type and represents a Key value.
 * Contains two iterators (over Keys an over values) implemented as inner classes.
 *
 * @param <K>  key 
 * @param <V>  value
 */
public class RedBlackTreeMap<K extends Comparable<K>,V>
{
	private RedBlackTree<ComparablePair<K,V>> fTree;
	
	private ComparablePair<K,V> fCachedPair;
	
	private ComparablePair<K,V> fCachedCheckPair;
		
	/**
	 * 
	 * Private inner class, represents an iterator over the keys in RedBlackTreeMap.
	 * Implements <code>Iterator</code> interface from <code>java.util</code>.
	 *
	 */
	private class RedBlackTreeMapKeyIterator implements Iterator<K>
	{
		private Iterator<ComparablePair<K,V>> fIterator;
		
		/**
		 * Constructor. Instantiates an Iterator over keys from <code>RedBlackTree<ComparablePair<K,V>></code>.
		 * @see jct.util.RedBlackTree
		 */
		private RedBlackTreeMapKeyIterator()
		{
			fIterator = fTree.keys().iterator();
		}
		
		/**
		 * Checks whether there is a next element available in a tree. 
		 * @returns <code>true</code> if there is at least one more element in a TreeMap, otherwise returns <code>false</code>
		 */
		public boolean hasNext()
		{
			return fIterator.hasNext();
		}
		
		/**
		 * Returns the key of next available element in a TreeMap. 
		 * @return  key of a next element in a TreeMap
		 */
		public K next()
		{
			fCachedPair = fIterator.next();
			return fCachedPair.first();
		}
		
		/**
		 * Meant to remove an element from the TreeMap. But as this functionality if not required for this system,
		 * this method has been left intentionally empty.
		 */
		public void remove()
		{
			// intentionally empty
		}
	}

	/**
	 * Private inner class, represents an iterator over the values in RedBlackTreeMap.
	 * Implements <code>Iterator</code> interface from <code>java.util</code>.
	 */
	private class RedBlackTreeMapValueIterator implements Iterator<V>
	{
		private Iterator<ComparablePair<K,V>> fIterator;
		
		/**
		 * Constructor. Instantiates an Iterator over keys from <code>RedBlackTree<ComparablePair<K,V>></code>.
		 * @see jct.util.RedBlackTree
		 */
		private RedBlackTreeMapValueIterator()
		{
			fIterator = fTree.keys().iterator();
		}
		
		/**
		 * Checks whether there is a next element available in a tree.
		 * @returns <code>true</code> if there is at least one more element in a TreeMap, otherwise returns <code>false</code>
		 */
		public boolean hasNext()
		{
			return fIterator.hasNext();
		}
		
		/**
		 * Returns the value of next available element in a TreeMap. 
		 * @return  value of a next element in a TreeMap
		 */
		public V next()
		{
			fCachedPair = fIterator.next();
			return fCachedPair.second();
		}
		
		/**
		 * Meant to remove an element from the TreeMap. But as this functionality if not required for this system,
		 * this method has been left intentionally empty.
		 */
		public void remove()
		{
			// intentionally empty
		}
	}
	
	/**
	 * Constructor. Initiates new <code>RedBlackTree<ComparablePair<K,V>></code>.
	 * Initiates CasedPair as a null.
	 * @see jct.util.RedBlackTree
	 * @see jct.util.ComparablePair
	 */
	public RedBlackTreeMap()
	{
		fTree = new RedBlackTree<ComparablePair<K,V>>();
		fCachedPair = null;
		// We create at most N+1 ComparablePairs
		fCachedCheckPair = new ComparablePair<K,V>( null, null );
	}
	
	/**
	 * Returns size of the RedBlackTreeMap as an Integer.
	 * @return  size of RedBlackTreeMap
	 */
	public int size()
	{
		return fTree.size();
	}
	
	/**
	 * Checks whether the RedBlackTreeMap is empty. 
	 * @return <code>true</code> if RedBlackTreeMap is empty, otherwise <code>false</code>
	 */
	public boolean isEmpty()
	{
		return fTree.isEmpty();
	}
	
	/**
	 * Inserts a new Comparable Pair into RedBlackTree with a given key and value. 
	 * Returns <code>true</code> if insert was successful, otherwise returns <code>false</code>.
	 * @param aKey     key for CopmarablePair element (or first value)  
	 * @param aValue   value for ComparablePair element (or second value)
	 * @return    <code>true</code> if insert was successful, otherwise <code>false</code>
	 * @see jct.util.ComparablePair
	 */
	public boolean insert( K aKey, V aValue )
	{
		fCachedCheckPair.setFirst( aKey );
		fCachedCheckPair.setSecond( aValue );
		
		boolean Result = fTree.insert( fCachedCheckPair );
		
		if ( Result )
		{
			fCachedPair = fCachedCheckPair;
			fCachedCheckPair = new ComparablePair<K,V>( null, null );		
		}
		
		return Result;
	}

	/**
	 * Removes a node from the RedBlackTree with a given key.
	 * @param aKey   key of the node to remove
	 * @return <code>true</code> if removal was successful, otherwise <code>false</code>
	 * @see jct.util.ComparablePair
	 */
	public boolean remove( K aKey )
	{
		fCachedPair = null;
		fCachedCheckPair.setFirst( aKey );
		return fTree.remove( fCachedCheckPair );
	}
	
	/**
	 * Checks whether the tree contains a node with a given key. 
	 * If so, return <code> true</code>, otherwise return <code>false</code>. 
	 * @param aKey   key to the node to search for
	 * @return <code>true</code> if search is successful, otherwise <code>false</code>
	 * @see jct.util.ComparablePair
	 */
	public boolean contains( K aKey )
	{
		fCachedCheckPair.setFirst( aKey );

		ComparablePair<K,V> lPair = fTree.lookupKey( fCachedCheckPair );
		
		if ( lPair != null )
		{
			fCachedPair = lPair;
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Returns value of the node with a given key from the tree. 
	 * @param aKey  key to the node to search for
	 * @return  value of the tree node with a given key; if such node does not exist in a tree, returns <code>null</code>
	 */
	public V getValue( K aKey )
	{
		if ( (fCachedPair != null) && (fCachedPair.first() == aKey) )
		{
			return fCachedPair.second();
		}
		else
		{
			fCachedCheckPair.setFirst( aKey );			
			ComparablePair<K,V> lPair = fTree.lookupKey( fCachedCheckPair );
		
			if ( lPair != null )
			{
				fCachedPair = lPair;
				return fCachedPair.second();
			}
			else
				return null;
		}
	}

	/**
	 * Updates value in the tree node with a given key to the specified one.
	 * @param aKey    key of the tree node where the value should be updated.
	 * @param aValue  new value for the node
	 * @return  <code>true</code> if update is successful, otherwise <code>false</code>
	 * @see jct.util.ComparablePair
	 */
	public boolean updateValue( K aKey, V aValue )
	{
		if ( (fCachedPair != null) && (fCachedPair.first() == aKey) )
		{
			fCachedPair.setSecond( aValue );
			return true;
		}
		else
		{
			fCachedCheckPair.setFirst( aKey );			
			ComparablePair<K,V> lPair = fTree.lookupKey( fCachedCheckPair );
		
			if ( lPair != null )
			{
				fCachedPair = lPair;
				fCachedPair.setSecond( aValue );
				return true;
			}
			else
				return false;
		}
	}

	/**
	 * Returns an Iterable over the keys in RedBlackTreeMap.
	 * @return  Iterable over RedBlackTreeMap keys
	 */
	public Iterable<K> keys()
	{
		return new IterableCreator<K>( new RedBlackTreeMapKeyIterator() );
	}

	/**
	 * Returns an Iterable over the values in RedBlackTreeMap.
	 * @return  Iterable over RedBlackTreeMap values
	 */
	public Iterable<V> values()
	{
		return new IterableCreator<V>( new RedBlackTreeMapValueIterator() );
	}
}
