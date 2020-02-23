/*
 * Lump.java
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

public final class Lump {
  public static final int DETAIL = 1 << 0;
  
  /**
   * Flags.
   */
  public int flags;
  
  /**
   * Indirect index of first vertex.
   */
  public int vert0Idx;
  
  /**
   * Number of vertices.
   */
  public int vertCount;
  
  /**
   * Indirect index of first edge.
   */
  public int edge0Idx;
  
  /**
   * Number of edges.
   */
  public int edgeCount;
  
  /**
   * Indirect index of first side.
   */
  public int side0Idx;
  
  /**
   * Number of sides.
   */
  public int sideCount;
  
  /**
   * Indirect index of first geom.
   */
  public int geom0Idx;
  
  /**
   * Number of geoms.
   */
  public int geomCount;
  
  public Lump() {
    flags = 0;
    
    vert0Idx  = -1;
    vertCount = 0;
    
    edge0Idx  = -1;
    edgeCount = 0;
    
    side0Idx  = -1;
    sideCount = 0;
    
    geom0Idx  = -1;
    geomCount = 0;
  }
}
