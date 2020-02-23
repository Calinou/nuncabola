/*
 * GUI.java
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

public final class GUI {
  private Widget root;
  
  private int xPos;
  private int yPos;
  
  private Widget focusWidget;
  
  public GUI() {
    root = null;
    
    xPos = 0;
    yPos = 0;
    
    focusWidget = null;
  }
  
  void setRoot(Widget widget) {
    root = widget;
  }
  
  public void setXPosition(int pos) {
    xPos = pos;
  }
  
  public void setYPosition(int pos) {
    yPos = pos;
  }
  
  public HStack hStack(Container parent) {
    return hStack(parent, Corners.NONE);
  }
  
  public HStack hStack(Container parent, Corners corners) {
    return hStack(parent, corners, false);
  }
  
  public HStack hStack(Container parent, Corners corners, boolean fill) {
    return new HStack(this, parent, corners, fill);
  }
  
  public VStack vStack(Container parent) {
    return vStack(parent, Corners.NONE);
  }
  
  public VStack vStack(Container parent, Corners corners) {
    return vStack(parent, corners, false);
  }
  
  public VStack vStack(Container parent, Corners corners, boolean fill) {
    return new VStack(this, parent, corners, fill);
  }
  
  public HArray hArray(Container parent) {
    return hArray(parent, Corners.NONE);
  }
  
  public HArray hArray(Container parent, Corners corners) {
    return hArray(parent, corners, false);
  }
  
  public HArray hArray(Container parent, Corners corners, boolean fill) {
    return new HArray(this, parent, corners, fill);
  }
  
  public VArray vArray(Container parent) {
    return vArray(parent, Corners.NONE);
  }
  
  public VArray vArray(Container parent, Corners corners) {
    return vArray(parent, corners, false);
  }
  
  public VArray vArray(Container parent, Corners corners, boolean fill) {
    return new VArray(this, parent, corners, fill);
  }
  
  public TextLabel textLabel(Container parent) {
    return textLabel(parent, Font.SMALL);
  }
  
  public TextLabel textLabel(Container parent, Font font) {
    return textLabel(parent, font, null);
  }
  
  public TextLabel textLabel(
      Container parent,
      Font      font,
      String[]  layoutTexts) {
    return textLabel(parent, font, layoutTexts, Corners.ALL);
  }
  
  public TextLabel textLabel(
      Container parent,
      Font      font,
      String[]  layoutTexts,
      Corners   corners) {
    return textLabel(parent, font, layoutTexts, corners, false);
  }
  
  public TextLabel textLabel(
      Container parent,
      Font      font,
      String[]  layoutTexts,
      Corners   corners,
      boolean   fill) {
    return new TextLabel(this, parent, font, layoutTexts, corners, fill);
  }
  
  public MultiTextLabel multiTextLabel(Container parent) {
    return multiTextLabel(parent, Font.SMALL);
  }
  
  public MultiTextLabel multiTextLabel(Container parent, Font font) {
    return multiTextLabel(parent, font, null);
  }
  
  public MultiTextLabel multiTextLabel(
      Container parent,
      Font      font,
      String    layoutText) {
    return multiTextLabel(parent, font, layoutText, Corners.ALL);
  }
  
  public MultiTextLabel multiTextLabel(
      Container parent,
      Font      font,
      String    layoutText,
      Corners   corners) {
    return multiTextLabel(parent, font, layoutText, corners, false);
  }
  
  public MultiTextLabel multiTextLabel(
      Container parent,
      Font      font,
      String    layoutText,
      Corners   corners,
      boolean   fill) {
    return new MultiTextLabel(this, parent, font, layoutText, corners, fill);
  }
  
  public NumberLabel numberLabel(Container parent) {
    return numberLabel(parent, Font.SMALL);
  }
  
  public NumberLabel numberLabel(Container parent, Font font) {
    return numberLabel(parent, font, -1);
  }
  
  public NumberLabel numberLabel(
      Container parent,
      Font      font,
      int       layoutNumber) {
    return numberLabel(parent, font, layoutNumber, Corners.ALL);
  }
  
  public NumberLabel numberLabel(
      Container parent,
      Font      font,
      int       layoutNumber,
      Corners   corners) {
    return numberLabel(parent, font, layoutNumber, corners, false);
  }
  
  public NumberLabel numberLabel(
      Container parent,
      Font      font,
      int       layoutNumber,
      Corners   corners,
      boolean   fill) {
    return new NumberLabel(this, parent, font, layoutNumber, corners, fill);
  }
  
  public TimeLabel timeLabel(Container parent) {
    return timeLabel(parent, Font.SMALL);
  }
  
  public TimeLabel timeLabel(Container parent, Font font) {
    return timeLabel(parent, font, Corners.ALL);
  }
  
  public TimeLabel timeLabel(Container parent, Font font, Corners corners) {
    return timeLabel(parent, font, corners, false);
  }
  
  public TimeLabel timeLabel(
      Container parent,
      Font      font,
      Corners   corners,
      boolean   fill) {
    return new TimeLabel(this, parent, font, corners, fill);
  }
  
  public ImageLabel imageLabel(
      Container parent,
      float     relWidth,
      float     relHeight) {
    return imageLabel(parent, relWidth, relHeight, Corners.ALL);
  }
  
  public ImageLabel imageLabel(
      Container parent,
      float     relWidth,
      float     relHeight,
      Corners   corners) {
    return imageLabel(parent, relWidth, relHeight, corners, false);
  }
  
  public ImageLabel imageLabel(
      Container parent,
      float     relWidth,
      float     relHeight,
      Corners   corners,
      boolean   fill) {
    return new ImageLabel(this, parent, relWidth, relHeight, corners, fill);
  }
  
  public Button button(Container parent) {
    return button(parent, Font.SMALL);
  }
  
  public Button button(Container parent, Font font) {
    return button(parent, font, null);
  }
  
  public Button button(Container parent, Font font, String[] layoutTexts) {
    return button(parent, font, layoutTexts, false);
  }
  
  public Button button(
      Container parent,
      Font      font,
      String[]  layoutTexts,
      boolean   fill) {
    return new Button(this, parent, font, layoutTexts, fill);
  }
  
  public Space space(Container parent) {
    return space(parent, false);
  }
  
  public Space space(Container parent, boolean fill) {
    return new Space(this, parent, fill);
  }
  
  public Filler filler(Container parent) {
    return new Filler(this, parent);
  }
  
  public void build() {
    if (root != null) {
      root.layout(xPos, yPos);
    }
  }
  
  public Widget getFocusWidget() {
    return focusWidget;
  }
  
  public void setFocusWidget(Widget widget) {
    focusWidget = widget;
  }
  
  public void draw() {
    if (root != null) {
      root.draw();
    }
  }
  
  public void step(float dt) {
    if (root != null) {
      root.step(dt);
    }
  }
  
  public Widget move(Direction dir) {
    if (root == null) {
      return null;
    }
    
    Widget widget;
    
    // Find a new focus widget in the given direction.
    
    if (focusWidget == null) {
      widget = null;
    } else {
      widget = root.getNext(focusWidget, dir);
    }
    
    // If the focus widget has changed, return the new focus widget.
    
    if ((widget == null) || (widget == focusWidget)) {
      return null;
    }
    
    focusWidget = widget;
    
    return focusWidget;
  }
  
  public Widget point(int x, int y) {
    if (root == null) {
      return null;
    }
    
    Widget widget;
    
    // Short-circuit check the current focus widget.
    
    if (focusWidget == null) {
      widget = null;
    } else {
      widget = focusWidget.getWidgetAt(x, y);
    }
    
    // If not still focused, search the hierarchy for a new focus widget.
    
    if (widget == null) {
      widget = root.getWidgetAt(x, y);
    }
    
    // If the focus widget has changed, return the new focus widget.
    
    if ((widget == null) || (widget == focusWidget)) {
      return null;
    }
    
    focusWidget = widget;
    
    return focusWidget;
  }
  
  public void deinitialize() {
    if (root != null) {
      root.deinitialize();
    }
  }
}
