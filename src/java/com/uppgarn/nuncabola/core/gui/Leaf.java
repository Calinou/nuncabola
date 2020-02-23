/*
 * Leaf.java
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

public abstract class Leaf extends Widget {
  Leaf(GUI gui, Branch parent, Corners corners, boolean fill) {
    super(gui, parent, corners, fill);
  }
  
  @Override
  void doTopDownPass(int x, int y, int width, int height) {
    setBounds(x, y, width, height);
  }
  
  @Override
  final Widget getWidgetAt(int x, int y) {
    if (contains(x, y) && isHot()) {
      return this;
    }
    
    return null;
  }
  
  @Override
  final Widget getNextHorizontal(Widget widget, boolean left, boolean pass0) {
    if ((this != widget) && isHot()) {
      return this;
    }
    
    return null;
  }
  
  @Override
  final Widget getNextVertical(Widget widget, boolean up, boolean pass0) {
    if ((this != widget) && isHot()) {
      return this;
    }
    
    return null;
  }
  
  @Override
  final void drawBackground(boolean branchSelected, boolean branchFocused) {
    boolean selected = branchSelected || isSelected();
    boolean focused  = branchFocused  || isFocused();
    
    drawRectangle(selected, focused);
  }
  
  @Override
  final void step(float dt) {
    stepScale(dt);
  }
}
