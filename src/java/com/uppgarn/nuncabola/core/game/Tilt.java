/*
 * Tilt.java
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

package com.uppgarn.nuncabola.core.game;

import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.util.*;

public final class Tilt {
  /**
   * X-axis.
   */
  public Vector3 x;
  
  /**
   * Rotation about x-axis.
   */
  public float rx;
  
  /**
   * Z-axis.
   */
  public Vector3 z;
  
  /**
   * Rotation about z-axis.
   */
  public float rz;
  
  public Tilt() {
    x  = new Vector3(1.0f, 0.0f, 0.0f);
    rx = 0.0f;
    
    z  = new Vector3(0.0f, 0.0f, 1.0f);
    rz = 0.0f;
  }
  
  public Tilt(Tilt src) {
    x  = new Vector3(src.x);
    rx = src.rx;
    
    z  = new Vector3(src.z);
    rz = src.rz;
  }
  
  public void copyFrom(Tilt src) {
    x.copyFrom(src.x);
    
    rx = src.rx;
    
    z.copyFrom(src.z);
    
    rz = src.rz;
  }
  
  public void copyFrom(Tilt src0, Tilt src1, float alpha) {
    x.copyFrom(src0.x, src1.x, alpha);
    
    rx = Util.lerp(src0.rx, src1.rx, alpha);
    
    z.copyFrom(src0.z, src1.z, alpha);
    
    rz = Util.lerp(src0.rz, src1.rz, alpha);
  }
}
