/*
 * VStack.java
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

public final class VStack extends Container {
  VStack(GUI gui, Branch parent, Corners corners, boolean fill) {
    super(gui, parent, corners, fill);
  }
  
  @Override
  boolean isChildOrderReversed() {
    return true;
  }
  
  @Override
  void doBottomUpPass() {
    int width  = 0;
    int height = 0;
    
    // Find the widest child width. Sum the child heights.
    
    for (Widget child: getChildren()) {
      child.doBottomUpPass();
      
      width   = Math.max(width, child.getLayoutWidth());
      height += child.getLayoutHeight();
    }
    
    setLayoutSize(width, height);
  }
  
  @Override
  void doTopDownPass(int x, int y, int width, int height) {
    super.doTopDownPass(x, y, width, height);
    
    // Measure the excess height.
    
    int excessHeight = height;
    int fillerCount  = 0;
    
    for (Widget child: getChildren()) {
      excessHeight -= child.getLayoutHeight();
      
      if (child.getFill()) {
        fillerCount++;
      }
    }
    
    excessHeight = Math.max(excessHeight, 0);
    
    // Give all children their requested space.
    // Distribute the rest evenly among filling children.
    
    int childY = y;
    
    for (Widget child: getChildren()) {
      int childHeight = child.getLayoutHeight();
      
      if (child.getFill()) {
        childHeight += excessHeight / fillerCount;
      }
      
      child.doTopDownPass(x, childY, width, childHeight);
      
      childY += childHeight;
    }
  }
}
