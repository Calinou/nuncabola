/*
 * SwitchBase.java
 *
 * Copyright (c) 2003-2022 Nuncabola authors
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

public final class SwitchBase {
  public static final float HEIGHT = 2.0f;
  
  /**
   * Position.
   */
  public Vector3 p;
  
  /**
   * Radius.
   */
  public float r;
  
  /**
   * Invisible state.
   */
  public boolean invisible;
  
  /**
   * Index of linked path.
   */
  public int pathIdx;
  
  /**
   * Timer (seconds).
   */
  public float t;
  
  /**
   * Timer (milliseconds).
   */
  public int tm;
  
  /**
   * Enabled state.
   */
  public boolean enabled;
  
  public SwitchBase() {
    p = new Vector3();
    r = 0.5f;
    
    invisible = false;
    
    pathIdx = -1;
    
    t  = 0.0f;
    tm = 0;
    
    enabled = false;
  }
}
