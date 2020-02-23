/*
 * Material.java
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

public final class Material {
  public static final int CLAMP_T     = 1 <<  0;
  public static final int CLAMP_S     = 1 <<  1;
  public static final int ADDITIVE    = 1 <<  2;
  public static final int TWO_SIDED   = 1 <<  3;
  public static final int ENVIRONMENT = 1 <<  4;
  public static final int DECAL       = 1 <<  5;
  public static final int SHADOWED    = 1 <<  6;
  public static final int TRANSPARENT = 1 <<  7;
  public static final int REFLECTIVE  = 1 <<  8;
  public static final int ALPHA_TEST  = 1 <<  9;
  public static final int PARTICLE    = 1 << 10;
  public static final int LIT         = 1 << 11;
  
  /**
   * Flags.
   */
  public int flags;
  
  /**
   * Diffuse color.
   */
  public Color4 d;
  
  /**
   * Ambient color.
   */
  public Color4 a;
  
  /**
   * Specular color.
   */
  public Color4 s;
  
  /**
   * Emission color.
   */
  public Color4 e;
  
  /**
   * Specular exponent.
   */
  public float h;
  
  /**
   * Alpha test comparison function.
   */
  public int alphaFunc;
  
  /**
   * Alpha test reference value.
   */
  public float alphaRef;
  
  /**
   * Lump smoothing cutoff angle.
   */
  public float angle;
  
  /**
   * Image file path.
   */
  public String path;
  
  public Material() {
    flags = 0;
    
    d = new Color4(0.8f, 0.8f, 0.8f, 1.0f);
    a = new Color4(0.2f, 0.2f, 0.2f, 1.0f);
    s = new Color4(0.0f, 0.0f, 0.0f, 1.0f);
    e = new Color4(0.0f, 0.0f, 0.0f, 1.0f);
    h = 0.0f;
    
    alphaFunc = 0;
    alphaRef  = 0.0f;
    
    angle = 45.0f;
    
    path = "";
  }
}
