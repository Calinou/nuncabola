/*
 * BodyBase.java
 *
 * Copyright (c) 2003-2019 Nuncabola authors
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

public final class BodyBase {
  /**
   * Index of node.
   */
  public int nodeIdx;
  
  /**
   * Index of first lump.
   */
  public int lump0Idx;
  
  /**
   * Number of lumps.
   */
  public int lumpCount;
  
  /**
   * Indirect index of first geom.
   */
  public int geom0Idx;
  
  /**
   * Number of geoms.
   */
  public int geomCount;
  
  /**
   * Index of first (linear) path.
   */
  public int path0Idx;
  
  /**
   * Index of second (rotational) path.
   */
  public int path1Idx;
  
  public BodyBase() {
    nodeIdx = -1;
    
    lump0Idx  = -1;
    lumpCount = 0;
    
    geom0Idx  = -1;
    geomCount = 0;
    
    path0Idx = -1;
    path1Idx = -1;
  }
}
