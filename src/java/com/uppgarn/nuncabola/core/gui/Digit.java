/*
 * Digit.java
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
import com.uppgarn.nuncabola.core.image.*;

import com.uppgarn.codelibf.util.*;

import static org.lwjgl.opengl.GL11.*;

final class Digit {
  private int       texture;
  private Dimension size;
  private Look      look;
  
  public Digit(Font font, int d) {
    Holder<Dimension> origSize = new Holder<>();
    
    texture = createTexture(font, d, origSize);
    size    = origSize.get();
    look    = createLook();
  }
  
  private int createTexture(Font font, int d, Holder<Dimension> origSize) {
    TextImagizer imagizer = GUIHome.getTextImagizer(font);
    
    String str = (d <= 9) ? Integer.toString(d) : ":";
    
    return Gfx.createTexture(imagizer.getImage(str, origSize), false);
  }
  
  private Look createLook() {
    Look look = GUIHome.getLookManager().createLook();
    
    look.setTextData(
      -size.getWidth () / 2,
      -size.getHeight() / 2,
      size.getWidth (),
      size.getHeight(),
      Color.RED,
      Color.YELLOW);
    
    return look;
  }
  
  public int getWidth() {
    return size.getWidth();
  }
  
  public int getHeight() {
    return size.getHeight();
  }
  
  public void draw() {
    glBindTexture(GL_TEXTURE_2D, texture);
    look.drawText();
  }
  
  public void deinitialize() {
    look.deinitialize();
    
    glDeleteTextures(texture);
  }
}
