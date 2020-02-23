/*
 * Branch.java
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

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

public abstract class Branch extends Widget {
  private List<Widget> children;
  private List<Widget> childrenR;
  
  Branch(GUI gui, Branch parent, Corners corners, boolean fill) {
    super(gui, parent, corners, fill);
    
    children  = new ArrayList<>();
    childrenR = Collections.unmodifiableList(children);
  }
  
  boolean isChildOrderReversed() {
    return false;
  }
  
  final int getChildCount() {
    return children.size();
  }
  
  final Widget getChild(int idx) {
    return children.get(idx);
  }
  
  final List<Widget> getChildren() {
    return childrenR;
  }
  
  final void add(Widget widget) {
    if (!isChildOrderReversed()) {
      children.add(widget);
    } else {
      children.add(0, widget);
    }
  }
  
  @Override
  void doTopDownPass(int x, int y, int width, int height) {
    setBounds(x, y, width, height);
  }
  
  @Override
  final Widget getWidgetAt(int x, int y) {
    if (contains(x, y)) {
      if (isHot()) {
        return this;
      }
      
      for (Widget child: children) {
        Widget widget = child.getWidgetAt(x, y);
        
        if (widget != null) {
          return widget;
        }
      }
    }
    
    return null;
  }
  
  @Override
  final Widget getNextHorizontal(Widget widget, boolean left, boolean pass0) {
    if (this == widget) {
      return null;
    }
    if (isHot()) {
      return this;
    }
    
    Widget best = null;
    
    if (pass0) {
      int bestHOff = Integer.MAX_VALUE;
      
      for (Widget child: children) {
        Widget test = child.getNextHorizontal(widget, left, pass0);
        
        if ((test != null) && (test.getMaxY() == widget.getMaxY())) {
          int hOff = widget.getHorizontalOffset(test, left);
          
          if (hOff <= 0) {
            continue;
          }
          if (hOff < bestHOff) {
            best     = test;
            bestHOff = hOff;
          }
        }
      }
    } else {
      int bestVOff = Integer.MAX_VALUE;
      int bestX    = 0; // overwritten before use
      
      for (Widget child: children) {
        Widget test = child.getNextHorizontal(widget, left, pass0);
        
        if (test != null) {
          int vOff = widget.getVerticalOffset(test, left);
          
          if (vOff <= 0) {
            vOff += Short.MAX_VALUE;
          }
          if (vOff <= bestVOff) {
            int x = test.getX();
            
            if ((vOff < bestVOff) || (left ? x > bestX : x < bestX)) {
              best     = test;
              bestVOff = vOff;
              bestX    = x;
            }
          }
        }
      }
    }
    
    return best;
  }
  
  @Override
  final Widget getNextVertical(Widget widget, boolean up, boolean pass0) {
    if (this == widget) {
      return null;
    }
    if (isHot()) {
      return this;
    }
    
    Widget best      = null;
    int    bestVOff  = Integer.MAX_VALUE;
    int    bestHDist = 0; // overwritten before use
    
    for (Widget child: children) {
      Widget test = child.getNextVertical(widget, up, pass0);
      
      if ((test != null) && (!pass0 || test.intersectsHorizontally(widget))) {
        int vOff = widget.getVerticalOffset(test, up);
        
        if (pass0 ? vOff <= 0 : vOff == 0) {
          continue;
        }
        if (vOff < 0) {
          vOff += Short.MAX_VALUE;
        }
        if (vOff <= bestVOff) {
          int hDist = widget.getHorizontalDistance(test);
          
          if ((vOff < bestVOff) || (hDist < bestHDist)) {
            best      = test;
            bestVOff  = vOff;
            bestHDist = hDist;
          }
        }
      }
    }
    
    return best;
  }
  
  @Override
  final void drawBackground(boolean branchSelected, boolean branchFocused) {
    boolean selected = branchSelected || isSelected();
    boolean focused  = branchFocused  || isFocused();
    
    if (!drawRectangle(selected, focused)) {
      for (Widget child: children) {
        child.drawBackground(selected, focused);
      }
    }
  }
  
  @Override
  final void drawContents() {
    glPushMatrix();
    {
      float k = getScale();
      
      if (k != 1.0f) {
        float x = getX() + getWidth () / 2.0f; // float, cannot use getCenterX()
        float y = getY() + getHeight() / 2.0f; // float, cannot use getCenterY()
        
        glTranslatef(+x, +y, 0.0f);
        glScalef(k, k, k);
        glTranslatef(-x, -y, 0.0f);
      }
      
      for (Widget child: children) {
        child.drawContents();
      }
    }
    glPopMatrix();
  }
  
  @Override
  final void step(float dt) {
    stepScale(dt);
    
    for (Widget child: children) {
      child.step(dt);
    }
  }
  
  @Override
  final void deinitialize() {
    for (Widget child: children) {
      child.deinitialize();
    }
    
    super.deinitialize();
  }
}
