/*
 * Widget.java
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

import com.uppgarn.nuncabola.core.graphics.*;

import static org.lwjgl.opengl.GL11.*;

public abstract class Widget {
  private final GUI     gui;
  private final Corners corners;
  private final boolean fill;
  
  private Object  token0;
  private Object  token1;
  private boolean selected;
  private float   scale;
  
  private Look look;
  
  private int layoutWidth;
  private int layoutHeight;
  
  private int x;
  private int y;
  private int width;
  private int height;
  
  Widget(GUI gui, Branch parent, Corners corners, boolean fill) {
    if (parent == null) {
      gui.setRoot(this);
    } else {
      parent.add(this);
    }
    
    this.gui     = gui;
    this.corners = corners;
    this.fill    = fill;
    
    token0   = null;
    token1   = null;
    selected = false;
    scale    = 1.0f;
    
    look = GUIHome.getLookManager().createLook();
    
    layoutWidth  = 0;
    layoutHeight = 0;
    
    x      = 0;
    y      = 0;
    width  = 0;
    height = 0;
  }
  
  final GUI getGUI() {
    return gui;
  }
  
  final Corners getCorners() {
    return corners;
  }
  
  final boolean getFill() {
    return fill;
  }
  
  final Look getLook() {
    return look;
  }
  
  final int getLayoutWidth() {
    return layoutWidth;
  }
  
  final int getLayoutHeight() {
    return layoutHeight;
  }
  
  final void setLayoutSize(int width, int height) {
    layoutWidth  = width;
    layoutHeight = height;
  }
  
  final int getX() {
    return x;
  }
  
  final int getY() {
    return y;
  }
  
  final int getWidth() {
    return width;
  }
  
  final int getHeight() {
    return height;
  }
  
  final int getCenterX() {
    return x + width  / 2;
  }
  
  final int getCenterY() {
    return y + height / 2;
  }
  
  final int getMaxX() {
    return x + width;
  }
  
  final int getMaxY() {
    return y + height;
  }
  
  final boolean contains(int x, int y) {
    return (x >= this.x) && (x < getMaxX()) && (y >= this.y) && (y < getMaxY());
  }
  
  final boolean intersectsHorizontally(Widget widget) {
    return (x < widget.getMaxX()) && (widget.x < getMaxX());
  }
  
  final int getHorizontalOffset(Widget widget, boolean left) {
    return left ? x - widget.x : widget.x - x;
  }
  
  final int getVerticalOffset(Widget widget, boolean up) {
    return up ? widget.getMaxY() - getMaxY() : getMaxY() - widget.getMaxY();
  }
  
  final int getHorizontalDistance(Widget widget) {
    int l0 = x;
    int r0 = (x <= widget.getCenterX())
             ? Math.min(widget.getMaxX(), getMaxX()) : getMaxX();
    int x0 = l0 + (r0 - l0) / 2;
    
    int l1 = Math.min(Math.max(x,         widget.x), widget.getMaxX());
    int r1 = Math.min(Math.max(getMaxX(), widget.x), widget.getMaxX());
    int x1 = l1 + (r1 - l1) / 2;
    
    return Math.abs(x1 - x0);
  }
  
  private void updateLookRectangleData() {
    look.setRectangleData(-width / 2, -height / 2, width, height, corners);
  }
  
  final void setBounds(int x, int y, int width, int height) {
    this.x      = x;
    this.y      = y;
    this.width  = width;
    this.height = height;
    
    updateLookRectangleData();
  }
  
  abstract void doBottomUpPass();
  
  abstract void doTopDownPass(int x, int y, int width, int height);
  
  private int getX(int pos) {
    int diff = Gfx.getWidth() - layoutWidth;
    
    if (pos < 0) {
      return 0;
    } else if (pos > 0) {
      return diff;
    } else {
      return diff / 2;
    }
  }
  
  private int getY(int pos) {
    int diff = Gfx.getHeight() - layoutHeight;
    
    if (pos < 0) {
      return 0;
    } else if (pos > 0) {
      return diff;
    } else {
      return diff / 2;
    }
  }
  
  /**
   * During GUI layout, we make a bottom-up pass to determine total area
   * requirements for the widget tree. We position this area to the
   * sides or center of the screen. Finally, we make a top-down pass
   * to distribute this area to each widget.
   */
  final void layout(int xPos, int yPos) {
    doBottomUpPass();
    doTopDownPass(getX(xPos), getY(yPos), layoutWidth, layoutHeight);
  }
  
  final boolean isHot() {
    return token0 != null;
  }
  
  /**
   * Searches the hierarchy for the highest-level "hot" widget
   * containing the given point.
   */
  abstract Widget getWidgetAt(int x, int y);
  
  abstract Widget getNextHorizontal(Widget widget, boolean left, boolean pass0);
  
  abstract Widget getNextVertical(Widget widget, boolean up, boolean pass0);
  
  /**
   * Finds the next "hot" widget in the specified direction
   * from the given widget (and inside the receiver).
   */
  final Widget getNext(Widget widget, Direction dir) {
    for (int pass = 0; pass < 2; pass++) {
      Widget next;
      
      if (dir.isHorizontal()) {
        next = getNextHorizontal(widget, dir == Direction.LEFT, pass == 0);
      } else {
        next = getNextVertical  (widget, dir == Direction.UP,   pass == 0);
      }
      
      if (next != null) {
        return next;
      }
    }
    
    return null;
  }
  
  final boolean drawRectangle(boolean drawSelected, boolean drawFocused) {
    if (corners == Corners.NONE) {
      return false;
    }
    
    glPushMatrix();
    {
      Theme theme   = GUIHome.getTheme();
      int   texture = theme.getTexture(drawSelected, drawFocused);
      
      glTranslatef(getCenterX(), getCenterY(), 0.0f);
      
      glBindTexture(GL_TEXTURE_2D, texture);
      look.drawRectangle();
    }
    glPopMatrix();
    
    return true;
  }
  
  abstract void drawBackground(boolean branchSelected, boolean branchFocused);
  
  abstract void drawContents();
  
  final void draw() {
    Gfx.setOrtho();
    
    glDisable(GL_DEPTH_TEST);
    {
      LookManager lookMgr = GUIHome.getLookManager();
      
      lookMgr.enableDrawing(false);
      drawBackground(false, false);
      
      lookMgr.enableDrawing(true);
      drawContents();
      
      lookMgr.disableDrawing();
      glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
    }
    glEnable(GL_DEPTH_TEST);
  }
  
  final void stepScale(float dt) {
    scale = Math.max(scale - dt, 1.0f);
  }
  
  abstract void step(float dt);
  
  public final Object getToken0() {
    return token0;
  }
  
  public final Object getToken1() {
    return token1;
  }
  
  public final void setToken(Object token) {
    setTokens(token, null);
  }
  
  public final void setTokens(Object token0, Object token1) {
    this.token0 = token0;
    this.token1 = token1;
  }
  
  public final boolean isSelected() {
    return selected;
  }
  
  public final void setSelected(boolean selected) {
    this.selected = selected;
  }
  
  public final boolean isFocused() {
    return this == gui.getFocusWidget();
  }
  
  public final float getScale() {
    return scale;
  }
  
  public final void setScale(float scale) {
    this.scale = scale;
  }
  
  void deinitialize() {
    look.deinitialize();
  }
}
