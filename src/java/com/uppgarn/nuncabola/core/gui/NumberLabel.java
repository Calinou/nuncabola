/*
 * NumberLabel.java
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

public final class NumberLabel extends Label {
  private final Font font;
  private final int  layoutNumber;
  
  private int number;
  
  NumberLabel(
      GUI     gui,
      Branch  parent,
      Font    font,
      int     layoutNumber,
      Corners corners,
      boolean fill) {
    super(gui, parent, corners, fill);
    
    this.font         = font;
    this.layoutNumber = layoutNumber;
    
    number = 0;
  }
  
  @Override
  void doBottomUpPass() {
    Digit digit = GUIHome.getDigit(font, 0);
    
    int width = 0;
    int n     = (layoutNumber != -1) ? layoutNumber : number;
    
    do {
      width += digit.getWidth();
    } while ((n /= 10) > 0);
    
    setLayoutSize(width, digit.getHeight());
    
    super.doBottomUpPass();
  }
  
  @Override
  void drawContents() {
    if (number < 0) {
      return;
    }
    
    glPushMatrix();
    {
      float k = getScale();
      
      glTranslatef(getCenterX(), getCenterY(), 0.0f);
      glScalef(k, k, k);
      
      // Translate right by half the total width of the rendered value.
      
      {
        int width = 0;
        int n     = number;
        
        do {
          width += GUIHome.getDigit(font, n % 10).getWidth();
        } while ((n /= 10) > 0);
        
        glTranslatef(width * 0.5f, 0.0f, 0.0f);
      }
      
      // Render each digit, moving left before each.
      
      {
        float width = 0.0f;
        int   n     = number;
        
        do {
          Digit digit = GUIHome.getDigit(font, n % 10);
          float half  = digit.getWidth() * 0.5f;
          
          width += half;
          glTranslatef(-width, 0.0f, 0.0f);
          width  = half;
          
          digit.draw();
        } while ((n /= 10) > 0);
      }
    }
    glPopMatrix();
  }
  
  public int getNumber() {
    return number;
  }
  
  public void setNumber(int number) {
    this.number = number;
  }
}
