/*
 * TextLabel.java
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

import java.util.*;

public class TextLabel extends Label {
  private final Font     font;
  private final String[] layoutTexts;
  
  private Truncation truncation;
  private String     text;
  private Color      color0;
  private Color      color1;
  
  private boolean   widgetSizeSet;
  private boolean   textureCreated;
  private int       texture;
  private Dimension textSize;
  
  TextLabel(
      GUI      gui,
      Branch   parent,
      Font     font,
      String[] layoutTexts,
      Corners  corners,
      boolean  fill) {
    super(gui, parent, corners, fill);
    
    this.font        = font;
    this.layoutTexts = (layoutTexts == null) ? null : layoutTexts.clone();
    
    truncation = Truncation.NONE;
    text       = "";
    color0     = null;
    color1     = null;
    
    widgetSizeSet  = false;
    textureCreated = false;
    texture        = 0;
  }
  
  @Override
  final void doBottomUpPass() {
    Dimension size;
    
    if (layoutTexts != null) {
      // Report desired size based on the given layout texts.
      
      size = getLayoutTextsSize();
    } else {
      // Report desired size based on the text size measured
      // while creating the texture for the untruncated text.
      
      updateTexture();
      updateLookTextData();
      
      size = textSize;
    }
    
    int width  = size.getWidth ();
    int height = size.getHeight();
    
    if (width > 0) {
      // Add some space for the text shadow.
      
      width += height / 16;
      
      // Enforce minimum width.
      
      width = Math.max(width, height);
    }
    
    setLayoutSize(width, height);
    
    super.doBottomUpPass();
  }
  
  @Override
  final void doTopDownPass(int x, int y, int width, int height) {
    super.doTopDownPass(x, y, width, height);
    
    widgetSizeSet = true;
    
    if (!textureCreated) {
      updateTexture();
      updateLookTextData();
    }
  }
  
  @Override
  final void drawContents() {
    if ((textSize.getWidth() == 0) || (textSize.getHeight() == 0)) {
      return;
    }
    
    glPushMatrix();
    {
      float k = getScale();
      
      glTranslatef(getCenterX(), getCenterY(), 0.0f);
      glScalef(k, k, k);
      
      glBindTexture(GL_TEXTURE_2D, texture);
      getLook().drawText();
    }
    glPopMatrix();
  }
  
  public final Truncation getTruncation() {
    return truncation;
  }
  
  public final void setTruncation(Truncation truncation) {
    if (this.truncation == truncation) {
      return;
    }
    
    this.truncation = truncation;
    
    if (textureCreated) {
      updateTexture();
      updateLookTextData();
    }
  }
  
  public final String getText() {
    return text;
  }
  
  public final void setText(String text) {
    if (this.text.equals(text)) {
      return;
    }
    
    this.text = text;
    
    if (textureCreated) {
      updateTexture();
      updateLookTextData();
    }
  }
  
  public final Color getColor0() {
    return color0;
  }
  
  public final Color getColor1() {
    return color1;
  }
  
  public final void setColor(Color color) {
    setColors(color, color);
  }
  
  public final void setColors(Color color0, Color color1) {
    if    (Objects.equals(this.color0, color0)
        && Objects.equals(this.color1, color1)) {
      return;
    }
    
    this.color0 = color0;
    this.color1 = color1;
    
    if (textureCreated) {
      updateLookTextData();
    }
  }
  
  Color getDefaultColor0() {
    return Color.RED;
  }
  
  Color getDefaultColor1() {
    return Color.YELLOW;
  }
  
  private Dimension getLayoutTextsSize() {
    int width  = 0;
    int height = 0;
    
    for (String layoutText: layoutTexts) {
      Dimension size = GUIHome.getTextImagizer(font).getSize(layoutText);
      
      width  = Math.max(width,  size.getWidth ());
      height = Math.max(height, size.getHeight());
    }
    
    return new Dimension(width, height);
  }
  
  private void updateTexture() {
    // Delete existing texture, if any.
    
    deleteTexture();
    
    // Create new texture.
    
    TextImagizer imagizer = GUIHome.getTextImagizer(font);
    
    String str;
    
    if (!widgetSizeSet) {
      str = text;
    } else {
      int availWidth = getWidth() - GUIHome.getPadding();
      
      str = truncation.getText(text, availWidth, imagizer);
    }
    
    Holder<Dimension> origSize = new Holder<>();
    
    texture        = Gfx.createTexture(imagizer.getImage(str, origSize), false);
    textSize       = origSize.get();
    textureCreated = true;
  }
  
  private void updateLookTextData() {
    getLook().setTextData(
      -textSize.getWidth () / 2,
      -textSize.getHeight() / 2,
      textSize.getWidth (),
      textSize.getHeight(),
      (color0 != null) ? color0 : getDefaultColor0(),
      (color1 != null) ? color1 : getDefaultColor1());
  }
  
  private void deleteTexture() {
    glDeleteTextures(texture);
  }
  
  @Override
  final void deinitialize() {
    deleteTexture();
    
    super.deinitialize();
  }
}
