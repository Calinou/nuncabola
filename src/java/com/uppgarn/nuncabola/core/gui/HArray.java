/*
 * HArray.java
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

public final class HArray extends Container {
  HArray(GUI gui, Branch parent, Corners corners, boolean fill) {
    super(gui, parent, corners, fill);
  }
  
  @Override
  void doBottomUpPass() {
    int width  = 0;
    int height = 0;
    
    // Find the widest child width and the highest child height.
    
    for (Widget child: getChildren()) {
      child.doBottomUpPass();
      
      width  = Math.max(width,  child.getLayoutWidth ());
      height = Math.max(height, child.getLayoutHeight());
    }
    
    // Total width is the widest child width times the child count.
    
    setLayoutSize(width * getChildCount(), height);
  }
  
  @Override
  void doTopDownPass(int x, int y, int width, int height) {
    super.doTopDownPass(x, y, width, height);
    
    // Distribute horizontal space evenly to all children.
    
    for (int idx = 0; idx < getChildCount(); idx++) {
      Widget child = getChild(idx);
      
      int x0 = x +  idx      * width / getChildCount();
      int x1 = x + (idx + 1) * width / getChildCount();
      
      child.doTopDownPass(x0, y, x1 - x0, height);
    }
  }
}
