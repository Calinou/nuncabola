/*
 * Node.java
 *
 * Copyright (c) 2003-2020 Nuncabola authors
 * See authors.txt for details.
 *
 * Nuncabola is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package com.uppgarn.nuncabola.core.solid;

public final class Node {
  /**
   * Index of side.
   */
  public int sideIdx;
  
  /**
   * Index of first (in-front) child node.
   */
  public int node0Idx;
  
  /**
   * Index of second (behind) child node.
   */
  public int node1Idx;
  
  /**
   * Index of first lump.
   */
  public int lump0Idx;
  
  /**
   * Number of lumps.
   */
  public int lumpCount;
  
  public Node() {
    sideIdx = -1;
    
    node0Idx = -1;
    node1Idx = -1;
    
    lump0Idx  = -1;
    lumpCount = 0;
  }
}
