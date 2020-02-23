/*
 * Color.java
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

package com.uppgarn.nuncabola.core.gui;

public final class Color {
  public static final Color BLACK  = new Color(  0,   0,   0, 255);
  public static final Color BLUE   = new Color(  0,   0, 255, 255);
  public static final Color GRAY   = new Color( 85,  85,  85, 255);
  public static final Color GREEN  = new Color(  0, 255,   0, 255);
  public static final Color RED    = new Color(255,   0,   0, 255);
  public static final Color WHITE  = new Color(255, 255, 255, 255);
  public static final Color YELLOW = new Color(255, 255,   0, 255);
  public static final Color SHADOW = new Color(  0,   0,   0, 128);
  
  private final int r;
  private final int g;
  private final int b;
  private final int a;
  
  public Color(int r, int g, int b, int a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }
  
  public int getR() {
    return r;
  }
  
  public int getG() {
    return g;
  }
  
  public int getB() {
    return b;
  }
  
  public int getA() {
    return a;
  }
  
  @Override
  public int hashCode() {
    return r + (g << 8) + (b << 16) + (a << 24);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Color)) {
      return false;
    }
    
    Color other = (Color) obj;
    
    return (r == other.r) && (g == other.g) && (b == other.b) && (a == other.a);
  }
}
