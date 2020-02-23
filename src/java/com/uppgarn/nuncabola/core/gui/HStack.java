/*
 * HStack.java
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

public final class HStack extends Container {
  HStack(GUI gui, Branch parent, Corners corners, boolean fill) {
    super(gui, parent, corners, fill);
  }
  
  @Override
  void doBottomUpPass() {
    int width  = 0;
    int height = 0;
    
    // Find the highest child height. Sum the child widths.
    
    for (Widget child: getChildren()) {
      child.doBottomUpPass();
      
      width += child.getLayoutWidth();
      height = Math.max(height, child.getLayoutHeight());
    }
    
    setLayoutSize(width, height);
  }
  
  @Override
  void doTopDownPass(int x, int y, int width, int height) {
    super.doTopDownPass(x, y, width, height);
    
    // Measure the excess width.
    
    int excessWidth = width;
    int fillerCount = 0;
    
    for (Widget child: getChildren()) {
      excessWidth -= child.getLayoutWidth();
      
      if (child.getFill()) {
        fillerCount++;
      }
    }
    
    excessWidth = Math.max(excessWidth, 0);
    
    // Give all children their requested space.
    // Distribute the rest evenly among filling children.
    
    int childX = x;
    
    for (Widget child: getChildren()) {
      int childWidth = child.getLayoutWidth();
      
      if (child.getFill()) {
        childWidth += excessWidth / fillerCount;
      }
      
      child.doTopDownPass(childX, y, childWidth, height);
      
      childX += childWidth;
    }
  }
}
