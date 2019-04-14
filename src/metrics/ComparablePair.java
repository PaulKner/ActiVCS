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

/**
 * 
 * Represents a pair that implements <code>Comparable</code> interface over itself. This class extends <code>Pair</code> class.
 * Takes two generic types. The first one extends <code>Comparable</code> interface; this functionality if used 
 * for comparing two pairs against each other in <code>Comparable</code> interface implementation over the class. 
 *
 * @param <K>  first value
 * @param <V>  second value
 */
public class ComparablePair<K extends Comparable<K>,V> extends Pair<K, V> 
	                                                   implements Comparable< ComparablePair<K,V> > 
{
	/**
	 * Constructor. Instantiates a super class (Pair) with a give First and Second values.
	 * @param aFirst    first value of the pair
	 * @param aSecond   second value of the pair
	 * @see jct.util.Pair
	 */
	public ComparablePair( K aFirst, V aSecond )
	{
		super( aFirst, aSecond );
	}

	/**
	 * Method required for implementation of <code>Comparable</code> Interface. 
	 * Takes other pair as a parameter and compares first values of the given pair and one we are working with. 
	 * Returns negative integer if this pair is less than given one. 
	 * Returns 0 if this pair is equal to the given one. 
	 * Returns positive integer if this pair is greater than the given one.
	 * @param aPair  takes a pair to which this pair is being compared to
	 */
	public int compareTo( ComparablePair<K,V> aPair ) 
	{
		return first().compareTo( aPair.first() );
	}
}
