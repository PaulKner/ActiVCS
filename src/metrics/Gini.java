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
 * Represents a measure that calculates <a href="http://en.wikipedia.org/wiki/Gini_coefficient">Gini coefficient</a>.
 * Gini coefficient is commonly used as a measure of inequality of income or wealth. 
 * Gini coefficient an range from 0 to 1; it is sometimes multiplied by 100 to range between 0 and 100.
 * A low Gini coefficient indicates a more equal distribution, with 0 corresponding to complete equality, 
 * while higher Gini coefficients indicate more unequal distribution, with 1 corresponding to complete 
 * inequality. To be validly computed, no negative goods can be distributed.
 */
public class Gini
{
	// Gini is in [0.0,1.0]

	/**
	 * Computes Gini coefficient using a provided array of integers and a boolean value that indicates 
	 * whether to count zero values or not. This method is static.
	 * @param aArrayOfInt  array of integers to perform calculations on 
	 * @param aCountZeros  indicator whether to take into account zeros
	 * @return the value of Gini coefficient for the given array
	 * @see jct.util.SortedEnumeration
	 */
	public static <T extends Number & Comparable<T>> double compute( T[] aArrayOfVals, boolean aCountZeros )
	{
		SortedEnumeration<T> lEnum = new SortedEnumeration<T>();
		
		for ( T i : aArrayOfVals )
		{
			lEnum.insert( i );
		}
		
		return compute( lEnum, aCountZeros );
	}
	
	/**
	 * Computes Gini coefficient using a provided SortedEnumeration of integers and a boolean value 
	 * that indicates whether to count zero values or not. This method is static.
	 * @param aOrderedCollection  ordered collection of integers to perform calculation on
	 * @param aCountZeros  indicator whether to take into account zeros
	 * @return the value of Gini coefficient for the given  sorted enumeration
	 * @see jct.util.SortedEnumeration
	 */
	public static <T extends Number & Comparable<T>> double compute( SortedEnumeration<T> aOrderedCollection, boolean aCountZeros )
	{
		double ginisum = 0;
		double sum = 0;
		int count = aOrderedCollection.size();
		long i = 1;
		
		// auxiliaries
		long skip = 0;
		
		for ( T elem : aOrderedCollection.values() )
		{
			double lElem = elem.doubleValue();
			
			if ( aCountZeros || lElem != 0 )
			{
				long off = 2 * i - count - skip - 1;
				ginisum = ginisum + off * lElem;
				sum += lElem;
			}
			else
				skip++;

			i++;
		}
		
		if ( sum != 0 )
			return ginisum / (double)(count - skip) / sum;
		else
			return Double.NaN; // no occurrence recorded
	}
}
