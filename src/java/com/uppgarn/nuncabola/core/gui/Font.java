/*
 * Font.java
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

public enum Font {
  SMALL (26.0f),
  MEDIUM(13.0f),
  LARGE ( 7.0f);
  
  private final float div;
  
  Font(float div) {
    this.div = div;
  }
  
  float getPointSize(int refHeight) {
    return refHeight / div;
  }
}
