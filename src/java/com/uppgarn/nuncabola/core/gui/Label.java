/*
 * Label.java
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

public abstract class Label extends Leaf {
  Label(GUI gui, Branch parent, Corners corners, boolean fill) {
    super(gui, parent, corners, fill);
  }
  
  int getPaddingFactor() {
    return 1;
  }
  
  @Override
  void doBottomUpPass() {
    int width  = getLayoutWidth ();
    int height = getLayoutHeight();
    
    // Add padding.
    
    int padding = GUIHome.getPadding() * getPaddingFactor();
    
    width  += padding;
    height += padding;
    
    // The size needs to be sufficient to accommodate the borders.
    
    int borders = GUIHome.getPadding() * 2;
    
    width  = Math.max(width,  borders);
    height = Math.max(height, borders);
    
    setLayoutSize(width, height);
  }
}
