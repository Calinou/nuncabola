/*
 * Corners.java
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

public enum Corners {
  NONE  (false, false, false, false),
  NW    (true,  false, true,  false),
  SW    (false, true,  true,  false),
  NE    (true,  false, false, true),
  SE    (false, true,  false, true),
  LEFT  (true,  true,  true,  false),
  RIGHT (true,  true,  false, true),
  TOP   (true,  false, true,  true),
  BOTTOM(false, true,  true,  true),
  ALL   (true,  true,  true,  true);
  
  private final boolean n;
  private final boolean s;
  private final boolean w;
  private final boolean e;
  
  Corners(boolean n, boolean s, boolean w, boolean e) {
    this.n = n;
    this.s = s;
    this.w = w;
    this.e = e;
  }
  
  boolean getN() {
    return n;
  }
  
  boolean getS() {
    return s;
  }
  
  boolean getW() {
    return w;
  }
  
  boolean getE() {
    return e;
  }
}
