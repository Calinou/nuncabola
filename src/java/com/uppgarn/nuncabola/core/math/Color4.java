/*
 * Color4.java
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

package com.uppgarn.nuncabola.core.math;

import com.uppgarn.nuncabola.core.util.*;

public final class Color4 {
  public float r;
  public float g;
  public float b;
  public float a;
  
  public Color4() {
    r = 0.0f;
    g = 0.0f;
    b = 0.0f;
    a = 0.0f;
  }
  
  public Color4(float r, float g, float b, float a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }
  
  public Color4(Color4 src) {
    r = src.r;
    g = src.g;
    b = src.b;
    a = src.a;
  }
  
  public void set(float r, float g, float b, float a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }
  
  public void copyFrom(Color4 src) {
    r = src.r;
    g = src.g;
    b = src.b;
    a = src.a;
  }
  
  public void copyFrom(Color4 src0, Color4 src1, float alpha) {
    r = Util.lerp(src0.r, src1.r, alpha);
    g = Util.lerp(src0.g, src1.g, alpha);
    b = Util.lerp(src0.b, src1.b, alpha);
    a = Util.lerp(src0.a, src1.a, alpha);
  }
}
