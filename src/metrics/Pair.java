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
 * Represents a data type that takes two values of a generic type and treats this two values as one 
 * object - a pair. 
 * 
 */
public class Pair<A,B>
{
	private A fFirst;
	private B fSecond;
	
	/**
	 * Constructor. Instantiates values of the pair.
	 * @param aFirst    first value
	 * @param aSecond   second value
	 */
	public Pair( A aFirst, B aSecond )
	{
		fFirst = aFirst;
		fSecond = aSecond;
	}
	
	/**
	 * Returns first value. 
	 * @return  a value of the first value
	 */
	public A first()
	{
		return fFirst;
	}
	
	/**
	 * Sets a new value for the first value. 
	 * @param aFirst  new value
	 */
	public void setFirst( A aFirst )
	{
		fFirst = aFirst;
	}
	
	/**
	 * Returns second value.
	 * @return  a value of the second value
	 */
	public B second()
	{
		return fSecond;
	}
	
	/**
	 * Sets a new value for the second value.
	 * @param aSecond  new value
	 */
	public void setSecond( B aSecond )
	{
		fSecond = aSecond;
	}
	
	/**
	 * Returns a string representation of the pair.
	 * @returns string representation of the pair
	 */
	public String toString()
	{
		return "[" + fFirst + "," + fSecond + "]"; 
	}
}
