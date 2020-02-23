/*
 * Switch.java
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

public final class Switch {
  public final SwitchBase base;
  
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
  
  /**
   * Ball inside state.
   */
  public boolean ballInside;
  
  public Switch(SwitchBase base) {
    this.base = base;
    
    t  = base.t;
    tm = base.tm;
    
    enabled = base.enabled;
    
    ballInside = false;
  }
  
  public Switch(Switch src) {
    base = src.base;
    
    t  = src.t;
    tm = src.tm;
    
    enabled = src.enabled;
    
    ballInside = src.ballInside;
  }
  
  public void copyFrom(Switch src) {
    assert src.base == base;
    
    t  = src.t;
    tm = src.tm;
    
    enabled = src.enabled;
    
    ballInside = src.ballInside;
  }
}
