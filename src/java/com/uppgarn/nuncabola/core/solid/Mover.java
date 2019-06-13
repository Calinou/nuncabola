/*
 * Mover.java
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

import com.uppgarn.nuncabola.core.util.*;

public final class Mover {
  /**
   * Index of path.
   */
  public int pathIdx;
  
  /**
   * Time on current path (seconds).
   */
  public float t;
  
  /**
   * Time on current path (milliseconds).
   */
  public int tm;
  
  public Mover() {
    pathIdx = -1;
    
    t  = 0.0f;
    tm = 0;
  }
  
  public Mover(Mover src) {
    pathIdx = src.pathIdx;
    
    t  = src.t;
    tm = src.tm;
  }
  
  public void copyFrom(Mover src) {
    pathIdx = src.pathIdx;
    
    t  = src.t;
    tm = src.tm;
  }
  
  public void copyFrom(Mover src0, Mover src1, float alpha) {
    if (src0.pathIdx == src1.pathIdx) {
      t = Util.lerp(src0.t, src1.t, alpha);
    } else {
      t = src1.t * alpha;
    }
    
    tm = Util.secondsToMilliseconds(t);
    
    pathIdx = src1.pathIdx;
  }
}
