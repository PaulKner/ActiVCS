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
 * Utility class, that represents a creator for an <code>Iterable</code> over the given iterator.
 *
 */
public class IterableCreator<K> implements Iterable<K>
{
	private Iterator<K> fIterator;

	/**
	 * Constructor. Instantiates the class with a given iterator.
	 * @param aIterator   iterator used to create Iterable
	 */
	public IterableCreator( Iterator<K> aIterator )
	{
		fIterator = aIterator;
	}
	
	/**
	 * Iterator method that is required by Iterable interface.
	 * @returns iterator
	 */
	public Iterator<K> iterator() 
	{
		return fIterator;
	}	
}
