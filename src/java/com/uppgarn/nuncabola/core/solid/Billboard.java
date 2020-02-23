/*
 * Billboard.java
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

public final class Billboard {
  public static final int EDGE    = 1 << 0;
  public static final int FLAT    = 1 << 1;
  public static final int NO_FACE = 1 << 2;
  
  /**
   * Flags.
   */
  public int flags;
  
  /**
   * Index of material.
   */
  public int mtrlIdx;
  
  /**
   * Position.
   */
  public Vector3 p;
  
  /**
   * Distance.
   */
  public float d;
  
  /**
   * Width coefficient 0.
   */
  public float w0;
  
  /**
   * Width coefficient 1.
   */
  public float w1;
  
  /**
   * Width coefficient 2.
   */
  public float w2;
  
  /**
   * Height coefficient 0.
   */
  public float h0;
  
  /**
   * Height coefficient 1.
   */
  public float h1;
  
  /**
   * Height coefficient 2.
   */
  public float h2;
  
  /**
   * X-rotation coefficient 0.
   */
  public float rx0;
  
  /**
   * X-rotation coefficient 1.
   */
  public float rx1;
  
  /**
   * X-rotation coefficient 2.
   */
  public float rx2;
  
  /**
   * Y-rotation coefficient 0.
   */
  public float ry0;
  
  /**
   * Y-rotation coefficient 1.
   */
  public float ry1;
  
  /**
   * Y-rotation coefficient 2.
   */
  public float ry2;
  
  /**
   * Z-rotation coefficient 0.
   */
  public float rz0;
  
  /**
   * Z-rotation coefficient 1.
   */
  public float rz1;
  
  /**
   * Z-rotation coefficient 2.
   */
  public float rz2;
  
  /**
   * Repeat time interval.
   */
  public float t;
  
  public Billboard() {
    flags = 0;
    
    mtrlIdx = -1;
    
    p = new Vector3();
    d = 0.0f;
    
    w0 = 0.0f;
    w1 = 0.0f;
    w2 = 0.0f;
    h0 = 0.0f;
    h1 = 0.0f;
    h2 = 0.0f;
    
    rx0 = 0.0f;
    rx1 = 0.0f;
    rx2 = 0.0f;
    ry0 = 0.0f;
    ry1 = 0.0f;
    ry2 = 0.0f;
    rz0 = 0.0f;
    rz1 = 0.0f;
    rz2 = 0.0f;
    
    t = 1.0f;
  }
}
