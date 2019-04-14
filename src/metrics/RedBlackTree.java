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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

/**
 * 
 * Represents a RedBlackTree binary search tree. 
 * Takes a generic data type which extends comparable interface over this data type.
 * Has an inner class - RedBlackTreeNode representing a node of a tree.
 * Enables to iterate through the nodes, insert and remove nodes, checks whether the node exists in collection or not.
 * The implementation idea is based on "Corman, Leiserson, Rivest, and Stein, Introduction to Algorithms, 2nd Edition" p.273.
 * 
 */
public class RedBlackTree<K extends Comparable<K>>
{
	/*
	 * Red-Black Tree: 
	 * 
	 *   Corman, Leiserson, Rivest, and Stein, Introduction to Algorithms, 2nd Edition
	 * 
	 *   page 273ff
	 * 
	 */

	private static final boolean COLOR_BLACK = true;
	private static final boolean COLOR_RED   = false;

	/**
	 * Private inner class, represents the RedBlackTree Node. Contains information about each node such as:
	 * <ul>
	 * <li> Color of the node
	 * <li> Key - generic type, can be any type. The generic type is taken from the super class - RedBlackTree.
	 * <li> Left, Right, and Parent node
	 * </ul>
	 */
	private class RedBlackNode
	{
		private boolean fColor;
		private K fKey;
		private RedBlackNode fLeft;
		private RedBlackNode fRight;
		private RedBlackNode fParent;
		
		/**
		 * Constructor. Invokes the information about the node.
		 * @param aColor    boolean value representing the color of the node
		 * @param aKey      the key to the node
		 */
		public RedBlackNode( boolean aColor, K aKey )
		{
			fColor = aColor;
			fKey = aKey;
			
			// sentinel Black Hole rule required for RB-DELETE-FIXUP
			// Sentinel can become w and, therefore, need to provide
			// proper links for getRight() and getLeft()!
			// The sentinel is the fixed-point of RedBlackNodes.
			if ( aKey == null )
			{
				fLeft = this;
				fRight = this;
				fParent = this;
			}
		}
		
		/**
		 * Returns aColor of the node in boolean representation.
		 * If color is Red then returns <code>false</code>.
		 * If color is Black, returns <code>true</code>. 
		 * @return  the color of the node in a boolean form
		 */
		public boolean getColor()
		{
			return fColor;
		}
		
		/**
		 * Sets the color of the node. Pass <code>true</code> for Black and <code>false</code> for Red.
		 * @param aColor   
		 */
		public void setColor( boolean aColor )
		{
			fColor = aColor;
		}
		
		/**
		 * Checks whether the node color is Black.
		 * @return    boolean value indicating whether color of the node is Black.
		 */
		public boolean isBlack()
		{
			return fColor && COLOR_BLACK;
		}

		/**
		 * Checks whether the node color is Red. 
		 * @return    boolean value indicating whether color of the node is Red.
		 */
		public boolean isRed()
		{
			return !isBlack();
		}
		
		/**
		 * Returns a key of the node.
		 * @return   the key of the node
		 */
		public K getKey()
		{
			return fKey;
		}
		
		/**
		 * Copies key information from the other node to the current one.
		 * @param aOtherNode   node to copy information from
		 */
		public void copyData( RedBlackNode aOtherNode )
		{
			fKey = aOtherNode.fKey;
		}
		
		/**
		 * Finds a successor node without looking at keys.
		 * @return successor node
		 */
		public RedBlackNode getTreeSuccessor()
		{
			RedBlackNode x = this;

			if ( x.getRight() != NIL )
			{
				x = x.getRight();

				while ( x.getLeft() != NIL )
					x = x.getLeft();

				return x;
			}
			
			RedBlackNode y = x.getParent();

			while ( y != NIL && x == y.getRight() )
			{
				x = y;
				y = y.getParent();
			}
			
			return y;
		}
		
		/**
		 * Returns a left node of the current node.
		 * @return  the node that is a left node for the current one.
		 */
		public RedBlackNode getLeft()
		{
			return fLeft;
		}
		
		/**
		 * Sets a left node of the current node.
		 * @param aNode  new left node
		 */
		public void setLeft( RedBlackNode aNode )
		{
			fLeft = aNode;
		}
		
		/**
		 * Returns a right node of the current node.
		 * @return  the node that is a right node for the current one.
		 */
		public RedBlackNode getRight()
		{
			return fRight;
		}
		
		/**
		 * Sets a right node of the current node.
		 * @param aNode  new right node
		 */
		public void setRight( RedBlackNode aNode )
		{
			fRight = aNode;
		}
		
		/**
		 * Returns a parent node of the current node.
		 * @return  node that is a parent node for the current one.
		 */
		public RedBlackNode getParent()
		{
			return fParent;
		}

		/**
		 * Sets a parent node of the current node.
		 * @param aNode  new parent node
		 */
		public void setParent( RedBlackNode aNode )
		{
			fParent = aNode;
		}
		
		/**
		 * String representation of a RedBlackTreeNode instance.
		 */
		public String toString()
		{
			if ( fKey == null )
				return "NIL";
			else
				return fKey.toString() + "[" + (fColor ? "B" : "R") + "]";
		}
	}
		
	// Node iterators
	
	/**
	 * Private inner class that implements iterator over the nodes of a generic type of the RedBlackTree.
	 * Implements <code>Iterator</code> interface from java.util.
	 */
	private class RedBlackNodeIterator implements Iterator<K>
	{
		/*
		 * The traversal stack will contain max(h) elements in worst case. 
		 */
		
		private Stack<RedBlackNode> fTraversalStack;
		
		/**
		 * Constructor. Always uses a root node as a start node for iteration.
		 * @see Core.util.RedBlackTree.RedBlackNode
		 * @see java.util.Stack
		 */
		private RedBlackNodeIterator()
		{
			fTraversalStack = new Stack<RedBlackNode>();
			if ( fRoot != NIL )
			{
				fTraversalStack.push( fRoot );

				// decent on left path first
				while ( fTraversalStack.peek().getLeft() != NIL )
				{
					fTraversalStack.push( fTraversalStack.peek().getLeft() );
				}
			}
		}
		
		/**
		 * Checks whether there is next element in the iterated tree. 
		 * @returns   <code>true</code> if there is an element in a tree after the current one, otherwise returns <code>false</code>
		 */
		public boolean hasNext()
		{
			return !fTraversalStack.empty();
		}
		
		/**
		 * Returns a next key in the iterated tree.
		 * @returns   null if the tree is empty, otherwise returns a next available node on a traversal branch.
		 * @see Core.util.RedBlackTree.RedBlackNode
		 */
		public K next()
		{
			// next implements binary tree in-order traversal
			
			if ( fTraversalStack.empty() )
				return null;
			else
			{
				// capture element to return
				RedBlackNode Result = fTraversalStack.pop();
			
				// add right path root, if present
				if ( Result.getRight() != NIL )
				{
					fTraversalStack.push( Result.getRight() );

					// decent on left path first
					while ( fTraversalStack.peek().getLeft() != NIL )
					{
						fTraversalStack.push( fTraversalStack.peek().getLeft() );
					}
				}
			
				return Result.getKey();
			}
		}
		
		/**
		 * Meant to remove a node from the tree. Required as a part of Iterator interface.
		 * Left intentionally empty as this functionality is not required.
		 */
		public void remove()
		{
			// intentionally empty
		}
	}

	private RedBlackNode fRoot;		// tree's root (always black)
	private int fSize; 				// number of elements
	
	// sentinel
	private final RedBlackNode NIL = new RedBlackNode( COLOR_BLACK, null );

	/**
	 * Constructor. Instantiates the the root of the tree to NIL(color black, no other information attached).
	 */
	public RedBlackTree()
	{
		fRoot = NIL;
	}
	
	/**
	 * Returns the size of the tree. 
	 * @return   integer representation of the tree size.
	 */
	public int size()
	{
		return fSize;
	}
	
	/**
	 * Checks whether the tree is empty.
	 * @return  <code>true</code> if the tree does not contain anything but the root node = NIL, otherwise returns false
	 */
	public boolean isEmpty()
	{
		return fRoot == NIL;
	}
	
	/**
	 * Populates RedBlackTree recursively from the start index to the end index using a specified 
	 * node colour as a root node colour.
	 * @param aSortedArrayOfValues values to be added to the tree
	 * @param aStart start index
	 * @param aEnd   end index
	 * @param aNodeColor root node colour
	 * @return populated RedBlackTree
	 */
	private RedBlackNode expandToTree( K[] aSortedArrayOfValues, int aStart, int aEnd, boolean aNodeColor )
	{
		RedBlackNode Result = NIL;
		
		if ( aStart <= aEnd )
		{			
			int lMidIdx = aStart + (aEnd - aStart)/2;

			Result = new RedBlackNode( aNodeColor, aSortedArrayOfValues[lMidIdx] );
			Result.setLeft( expandToTree( aSortedArrayOfValues, aStart, lMidIdx - 1, !aNodeColor ) );
			Result.setRight( expandToTree( aSortedArrayOfValues, lMidIdx + 1, aEnd, !aNodeColor ) );
		}

		return Result;
	}
	
	/**
	 * Populate a Red-Black-Tree with a set of values (array).
	 * 
	 * @param aArrayOfValues - array of values to populate tree 
	 */
	public void assign( K[] aArrayOfValues )
	{
		Arrays.sort( aArrayOfValues );
		
		fRoot = expandToTree( aArrayOfValues, 0, aArrayOfValues.length - 1, COLOR_BLACK );
	}
	
	/**
	 * Insets a new node to the tree with a given key. Checks whether the key is already exists in a tree.
	 * If so, the insertion is unsuccessful. This method introduces node color violations (in a particular problems with color Red). 
	 * The problems is being solved by calling <code>insertFixup()</code> at the end of the method.
	 * @param aKey  key value to insert to the tree 
	 * @return  <code>true</code> if insertion is successful, otherwise <code>false</code>
	 * @see jct.util.RedBlackTree.RedBlackNode
	 */
	public boolean insert( K aKey )
	{
		if ( aKey == null )
			return false;		// null key 
		
		// build new node z (temporarily marked red)
		RedBlackNode z = new RedBlackNode( COLOR_RED, aKey );

		RedBlackNode y = NIL;
		RedBlackNode x = fRoot;
		
		while ( x != NIL )
		{
			y = x;
			int lCompare = z.getKey().compareTo( x.getKey() );
			
			if ( lCompare == 0 )				
				return false; // duplicate key - error
			
			if ( lCompare < 0 )
				x = x.getLeft();
			else
				x = x.getRight();
		}
		
		// here y contains either NIL (tree is empty) or the node that has to become
		// the parent of z
		z.setParent( y );
		fSize++;	 // new node added

		if ( y == NIL )
			fRoot = z; // z is new root
		else
			if ( z.getKey().compareTo( y.getKey()) < 0 )
				y.setLeft( z );
			else
				y.setRight( z );

		z.setLeft( NIL );
		z.setRight( NIL );
	
		insertFixup( z ); // page 281
		
		return true; // insert succeeded
	}
	
	
	/**
	 * Inserts a given node to the tree.
	 * Solves the color violation problems introduced in <code>insert()</code> method. 
	 * @param z  node to insert into the tree 
	 * @see jct.util.RedBlackTree.RedBlackNode
	 */
	private void insertFixup( RedBlackNode z )
	{
		while ( z.getParent().isRed() )
		{
			RedBlackNode y;
			
			if ( z.getParent() == z.getParent().getParent().getLeft() )
			{
				y = z.getParent().getParent().getRight();
				if ( y.isRed() )
				{
					z.getParent().setColor( COLOR_BLACK );
					y.setColor( COLOR_BLACK );
					z.getParent().getParent().setColor( COLOR_RED );
					z = z.getParent().getParent();
				}
				else
				{
					if ( z == z.getParent().getRight() )
					{
						z = z.getParent();
						RotateLeft( z );
					}
					z.getParent().setColor( COLOR_BLACK );
					z.getParent().getParent().setColor( COLOR_RED );
					RotateRight( z.getParent().getParent() );
				}
			}
			else
			{
				y = z.getParent().getParent().getLeft();
				if ( y.isRed() )
				{
					z.getParent().setColor( COLOR_BLACK );
					y.setColor( COLOR_BLACK );
					z.getParent().getParent().setColor( COLOR_RED );
					z = z.getParent().getParent();
				}
				else
				{
					if ( z == z.getParent().getLeft() )
					{
						z = z.getParent();
						RotateRight( z );
					}
					z.getParent().setColor( COLOR_BLACK );
					z.getParent().getParent().setColor( COLOR_RED );
					RotateLeft( z.getParent().getParent() );
				}				
			}
		}
		
		fRoot.setColor( COLOR_BLACK );
	}

	/**
	 * Removes a node from the tree with a given key. Returns true if removal is successful.
	 * Introduces color violation problem in the tree, that is being solved by calling <code>deleteFixup()</code>
	 * method inside this one.
	 * @param aKey  key of the node to be removed
	 * @return  <code>true</code> if removal is successful, otherwise <code>false</code>
	 * @see jct.util.RedBlackTree.RedBlackNode
	 */
	public boolean remove( K aKey )
	{
		RedBlackNode z = lookup( aKey );
		
		if ( z != null )
		{
			// p. 288
			RedBlackNode y; // node to splice out
			RedBlackNode x; // NIL or non-NIL child of y
			
			if ( (z.getLeft() == NIL) || (z.getRight() == NIL) )
			{
				// zero or one child
				y = z;
			}
			else
			{
				y = z.getTreeSuccessor();
			}

			if ( y.getLeft() != NIL )
				x = y.getLeft();
			else
				x = y.getRight();
			
			// the parent of the sentinel NIL may get set in the process making NIL an auxiliary node
			x.setParent( y.getParent() ); 			// re-wire the parent link

			if ( y.getParent() == NIL )
				fRoot = x;							// we remove to root node
			else
			{
				if ( y == y.getParent().getLeft() )
					y.getParent().setLeft( x );		// attach child x to left
				else
					y.getParent().setRight( x );	// attach child x to right
			}
				
			if ( y != z )
				z.copyData( y );					// successor of z was node spliced out, copy data

			if ( y.isBlack() )
				deleteFixup( x );	

			fSize--;								// decrement element count
			return true;
		}
		else
			return false; // nothing to remove (lookup failed)
	}
	
	/**
	 * Deletes a given node from the tree.
	 * Solves the color violation problems introduced in <code>remove()</code> method. 
	 * @param z  node to delete from the tree
	 * @see jct.util.RedBlackTree.RedBlackNode
	 */
	private void deleteFixup( RedBlackNode x )
	{
		while ( (x != fRoot) && x.isBlack() )
		{
			RedBlackNode w;
			
			if ( x == x.getParent().getLeft() )
			{
				w = x.getParent().getRight();
				if ( w.isRed() )
				{
					w.setColor( COLOR_BLACK );
					x.getParent().setColor( COLOR_RED );
					RotateLeft( x.getParent() );
					w = x.getParent().getRight();
				}
				if ( w.getLeft().isBlack() && w.getRight().isBlack() )
				{
					w.setColor( COLOR_RED );
					x = x.getParent();
				}
				else
				{
					if ( w.getRight().isBlack() )
					{
						w.getLeft().setColor( COLOR_BLACK );
						w.setColor( COLOR_RED );
						RotateRight( w );
						w = x.getParent().getRight();
					}
					w.setColor( x.getParent().getColor() );
					x.getParent().setColor( COLOR_BLACK	 );
					w.getRight().setColor( COLOR_BLACK );
					RotateLeft( x.getParent() );
					x = fRoot;
				}
			}
			else
			{
				w = x.getParent().getLeft();
				if ( w.isRed() )
				{
					w.setColor( COLOR_BLACK );
					x.getParent().setColor( COLOR_RED );
					RotateRight( x.getParent() );
					w = x.getParent().getLeft();
				}
				if ( w.getRight().isBlack() && w.getLeft().isBlack() )
				{
					w.setColor( COLOR_RED );
					x = x.getParent();
				}
				else
				{
					if ( w.getLeft().isBlack() )
					{
						w.getRight().setColor( COLOR_BLACK );
						w.setColor( COLOR_RED );
						RotateLeft( w );
						w = x.getParent().getLeft();
					}
					w.setColor( x.getParent().getColor() );
					x.getParent().setColor( COLOR_BLACK );
					w.getLeft().setColor( COLOR_BLACK );
					RotateRight( x.getParent() );
					x = fRoot;
				}
			}
		}
		
		x.setColor( COLOR_BLACK );
	}
	
	/**
	 * Transforms the configuration of the two nodes on the left into the configuration on the right.
	 * @param x  node to rotate left 
	 * @see jct.util.RedBlackTree.RedBlackNode
	 */
	private void RotateLeft( RedBlackNode x )
	{
		// Assumption: x's right child y is not NIL (see p. 278)
		
		RedBlackNode y = x.getRight();				// set y
		x.setRight( y.getLeft() );					// turn y's left subtree into x's right subtree
		y.getLeft().setParent( x );
		y.setParent( x.getParent() );				// link x's parent to y
		if ( x.getParent() == NIL )
		{
			fRoot = y;
		}
		else
		{
			if ( x == x.getParent().getLeft() )
			{
				x.getParent().setLeft( y );
			}
			else
			{
				x.getParent().setRight( y );
			}
		}
		y.setLeft( x );								// put x on y's left
		x.setParent( y );
	}
	
	/**
	 * Transforms the configuration of the two nodes on the right into the configuration on the left.
	 * @param x  node to rotate right
	 * @see jct.util.RedBlackTree.RedBlackNode
	 */
	private void RotateRight( RedBlackNode x )
	{
		// Assumption: x's left child y is not NIL		

		RedBlackNode y = x.getLeft();				// set y
		x.setLeft( y.getRight() );					// turn y's right subtree into x's left subtree
		y.getRight().setParent( x );
		y.setParent( x.getParent() );				// link x's parent to y
		if ( x.getParent() == NIL )
		{
			fRoot = y;
		}
		else
		{
			if ( x == x.getParent().getRight() )	//if ( x == x.getParent().getLeft() )
			{
				x.getParent().setRight( y );		//x.getParent().setLeft( y )
			}
			else
			{
				x.getParent().setLeft( y );			//x.getParent().setRight( y );
			}
		}
		y.setRight( x );							// put x on y's right
		x.setParent( y );
	}
	
	/**
	 * Looks up a node of a given key in the tree. Returns <code>null</code> if a node with a given key was not found. 
	 * Otherwise returns a <code>RedBlackNode</code> which contains given key.
	 * @param aKey   generic type representing a key to search in the tree 
	 * @return    <code>RedBlackNode</code> which contains a given key from the tree. 
	 *            If such node was not found, returns <code>null</code>.
	 * @see jct.util.RedBlackTree.RedBlackNode
	 */
	private RedBlackNode lookup( K aKey )
	{
		if ( aKey == null )
			return null;		// null key 

		RedBlackNode x = fRoot;
		
		while( x != NIL )
		{
			int lCompare = aKey.compareTo( x.getKey() );
			
			if ( lCompare == 0 )
				break;
			
			if ( lCompare < 0 )
				x = x.getLeft();
			else
				x = x.getRight();
		}
		
		if ( x == NIL )
			return null;			// lookup failed
		else
			return x;
	}
	
	/**
	 * Checks whether the tree contains a node with a given key. Returns <code>true</code> if there is such node in the tree.
	 * @param aKey  key of the node to search for
	 * @return   <code>true</code> if a node with the given key was found, otherwise <code>false</code>
	 */
	public boolean contains( K aKey )
	{
		return lookup( aKey ) != null;
	}

	/**
	 * Checks whether a given key is present in a tree. Returns <code>null</code> if a a given key was not found. 
	 * Otherwise returns a found key value.
	 * @param aKey generic type representing a key to search in the tree
	 * @return a found key value or null
	 */
	public K lookupKey( K aKey )
	{
		RedBlackNode lNode = lookup( aKey );
		
		if ( lNode != null )
			return lNode.getKey();
		else
			return null;
	}

	/**
	 * Returns <code>Iterable</code> over the keys in the RedBlackTree. 
	 * Starting iteration from the Root node.
	 * @return  Iterable over the keys in the tree starting from the Root node
	 */
	public Iterable<K> keys()
	{
		return new IterableCreator<K>( new RedBlackNodeIterator() );
	}
}
