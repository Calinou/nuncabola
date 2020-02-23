/*
 * TimeLabel.java
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

public final class TimeLabel extends Label {
  private final Font font;
  
  private int time;
  
  TimeLabel(GUI gui, Branch parent, Font font, Corners corners, boolean fill) {
    super(gui, parent, corners, fill);
    
    this.font = font;
    
    time = 0;
  }
  
  @Override
  void doBottomUpPass() {
    Digit digit = GUIHome.getDigit(font, 0);
    
    setLayoutSize(6 * digit.getWidth(), digit.getHeight());
    
    super.doBottomUpPass();
  }
  
  @Override
  void drawContents() {
    if (time < 0) {
      return;
    }
    
    int myTime = Math.min(time, 599999);
    
    int min10 =  (myTime / 6000) / 10;
    int min00 =  (myTime / 6000) % 10;
    int sec10 = ((myTime % 6000) / 100) / 10;
    int sec00 = ((myTime % 6000) / 100) % 10;
    int hun10 = ((myTime % 6000) % 100) / 10;
    int hun00 = ((myTime % 6000) % 100) % 10;
    
    float dxLarge = GUIHome.getDigit(font, 0).getWidth();
    float dxSmall = dxLarge * 0.75f;
    
    glPushMatrix();
    {
      float k = getScale();
      
      glTranslatef(getCenterX(), getCenterY(), 0.0f);
      glScalef(k, k, k);
      
      // Translate left by half the total width of the rendered value.
      
      glTranslatef(((min10 > 0) ? -2.25f : -1.75f) * dxLarge, 0.0f, 0.0f);
      
      // Render the minutes counter.
      
      if (min10 > 0) {
        GUIHome.getDigit(font, min10).draw();
        glTranslatef(dxLarge, 0.0f, 0.0f);
      }
      
      GUIHome.getDigit(font, min00).draw();
      glTranslatef(dxSmall, 0.0f, 0.0f);
      
      // Render the colon.
      
      GUIHome.getDigit(font, 10).draw();
      glTranslatef(dxSmall, 0.0f, 0.0f);
      
      // Render the seconds counter.
      
      GUIHome.getDigit(font, sec10).draw();
      glTranslatef(dxLarge, 0.0f, 0.0f);
      
      GUIHome.getDigit(font, sec00).draw();
      glTranslatef(dxSmall, 0.0f, 0.0f);
      
      // Render hundredths counter half size.
      
      glScalef(0.5f, 0.5f, 1.0f);
      
      GUIHome.getDigit(font, hun10).draw();
      glTranslatef(dxLarge, 0.0f, 0.0f);
      
      GUIHome.getDigit(font, hun00).draw();
    }
    glPopMatrix();
  }
  
  public int getTime() {
    return time;
  }
  
  public void setTime(int time) {
    this.time = time;
  }
}
