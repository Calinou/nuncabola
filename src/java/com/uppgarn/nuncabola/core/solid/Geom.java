/*
 * Geom.java
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

public final class Geom {
  /**
   * Index of material.
   */
  public int mtrlIdx;
  
  /**
   * Index of first offset.
   */
  public int offset0Idx;
  
  /**
   * Index of second offset.
   */
  public int offset1Idx;
  
  /**
   * Index of third offset.
   */
  public int offset2Idx;
  
  public Geom() {
    mtrlIdx = -1;
    
    offset0Idx = -1;
    offset1Idx = -1;
    offset2Idx = -1;
  }
}
