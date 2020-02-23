/*
 * AxisState.java
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

package com.uppgarn.nuncabola.core.controls;

public final class AxisState {
  private float value0;
  private float value1;
  
  private boolean use0;
  
  public AxisState() {
    value0 = 0.0f;
    value1 = 0.0f;
    
    use0 = true;
  }
  
  public float get() {
    return use0 ? value0 : value1;
  }
  
  public void set(float value) {
    value0 = (value < 0.0f) ? value : 0.0f;
    value1 = (value > 0.0f) ? value : 0.0f;
    
    use0 = value1 == 0.0f;
  }
  
  public void set(int side, boolean on) {
    if (side == 0) {
      value0 = on ? -1.0f : 0.0f;
      
      use0 = on;
    } else {
      value1 = on ? +1.0f : 0.0f;
      
      use0 = !on;
    }
  }
}
