/*
 * PathBase.java
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

import com.uppgarn.nuncabola.core.math.*;

public final class PathBase {
  public static final int ORIENTED = 1 << 0;
  
  /**
   * Flags.
   */
  public int flags;
  
  /**
   * Starting position.
   */
  public Vector3 p;
  
  /**
   * Orientation.
   */
  public Quaternion e;
  
  /**
   * Index of target path.
   */
  public int pathIdx;
  
  /**
   * Travel time (seconds).
   */
  public float t;
  
  /**
   * Travel time (milliseconds).
   */
  public int tm;
  
  /**
   * Smooth state.
   */
  public boolean smooth;
  
  /**
   * Enabled state.
   */
  public boolean enabled;
  
  public PathBase() {
    flags = 0;
    
    p = new Vector3();
    e = new Quaternion();
    
    pathIdx = -1;
    
    t  = 1.0f;
    tm = 1000;
    
    smooth = true;
    
    enabled = true;
  }
}
