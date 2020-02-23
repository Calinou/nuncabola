/*
 * MultiTextLabel.java
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

public final class MultiTextLabel extends Branch {
  private static final int MAX_LINE_COUNT = 16;
  
  private final Font   font;
  private final String layoutText;
  
  private Truncation truncation;
  private String     text;
  private Color      color0;
  private Color      color1;
  
  private boolean     labelsCreated;
  private VArray      array;
  private TextLabel[] labels;
  
  MultiTextLabel(
      GUI     gui,
      Branch  parent,
      Font    font,
      String  layoutText,
      Corners corners,
      boolean fill) {
    super(gui, parent, corners, fill);
    
    this.font       = font;
    this.layoutText = layoutText;
    
    truncation = Truncation.NONE;
    text       = "";
    color0     = null;
    color1     = null;
    
    labelsCreated = false;
  }
  
  @Override
  void doBottomUpPass() {
    // Create array.
    
    array = new VArray(getGUI(), this, Corners.NONE, getFill());
    
    // Create labels.
    
    String   testText  = (layoutText != null) ? layoutText : text;
    String[] strs      = testText.split("\n", MAX_LINE_COUNT + 1);
    int      lineCount = Math.min(strs.length, MAX_LINE_COUNT);
    
    labels = new TextLabel[lineCount];
    
    for (int idx = 0; idx < lineCount; idx++) {
      labels[idx] = new TextLabel(
        getGUI(),
        array,
        font,
        new String[] {strs[idx]},
        Corners.NONE,
        false);
    }
    
    updateLabelTruncations();
    updateLabelTexts();
    updateLabelColors();
    
    labelsCreated = true;
    
    // Refer to array for size determination.
    
    array.doBottomUpPass();
    
    setLayoutSize(array.getLayoutWidth(), array.getLayoutHeight());
  }
  
  @Override
  void doTopDownPass(int x, int y, int width, int height) {
    super.doTopDownPass(x, y, width, height);
    
    array.doTopDownPass(x, y, width, height);
  }
  
  public Truncation getTruncation() {
    return truncation;
  }
  
  public void setTruncation(Truncation truncation) {
    this.truncation = truncation;
    
    if (labelsCreated) {
      updateLabelTruncations();
    }
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
    
    if (labelsCreated) {
      updateLabelTexts();
    }
  }
  
  public Color getColor0() {
    return color0;
  }
  
  public Color getColor1() {
    return color1;
  }
  
  public void setColor(Color color) {
    setColors(color, color);
  }
  
  public void setColors(Color color0, Color color1) {
    this.color0 = color0;
    this.color1 = color1;
    
    if (labelsCreated) {
      updateLabelColors();
    }
  }
  
  private void updateLabelTruncations() {
    for (TextLabel lbl: labels) {
      lbl.setTruncation(truncation);
    }
  }
  
  private void updateLabelTexts() {
    String[] strs = text.split("\n");
    
    for (int idx = 0; idx < labels.length; idx++) {
      labels[idx].setText((idx < strs.length) ? strs[idx] : "");
    }
  }
  
  private void updateLabelColors() {
    for (TextLabel lbl: labels) {
      lbl.setColors(color0, color1);
    }
  }
}
