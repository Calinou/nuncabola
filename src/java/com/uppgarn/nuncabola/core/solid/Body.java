/*
 * Body.java
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

public final class Body {
  public final BodyBase base;
  
  /**
   * Index of first (linear) mover.
   */
  public int mover0Idx;
  
  /**
   * Index of second (rotational) mover.
   */
  public int mover1Idx;
  
  public Body(BodyBase base, MoverCreator moverCreator) {
    this.base = base;
    
    if (base.path0Idx >= 0) {
      mover0Idx = moverCreator.add(base.path0Idx);
    } else {
      mover0Idx = -1;
    }
    
    if (base.path1Idx == base.path0Idx) {
      mover1Idx = mover0Idx;
    } else if (base.path1Idx >= 0) {
      mover1Idx = moverCreator.add(base.path1Idx);
    } else {
      mover1Idx = -1;
    }
  }
  
  public Body(Body src) {
    base = src.base;
    
    mover0Idx = src.mover0Idx;
    mover1Idx = src.mover1Idx;
  }
  
  public void copyFrom(Body src) {
    assert src.base == base;
    
    mover0Idx = src.mover0Idx;
    mover1Idx = src.mover1Idx;
  }
}
