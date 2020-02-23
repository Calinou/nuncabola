/*
 * ImageLabel.java
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

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

public final class ImageLabel extends Label {
  private final float relWidth;
  private final float relHeight;
  
  private String imgPath;
  
  private boolean textureCreated;
  private int     texture;
  
  ImageLabel(
      GUI     gui,
      Branch  parent,
      float   relWidth,
      float   relHeight,
      Corners corners,
      boolean fill) {
    super(gui, parent, corners, fill);
    
    this.relWidth  = relWidth;
    this.relHeight = relHeight;
    
    imgPath = null;
    
    textureCreated = false;
    texture        = 0;
  }
  
  @Override
  int getPaddingFactor() {
    return 2;
  }
  
  @Override
  void doBottomUpPass() {
    updateTexture();
    
    setLayoutSize(
      Math.round(Gfx    .getWidth          () * relWidth),
      Math.round(GUIHome.getReferenceHeight() * relHeight));
    
    super.doBottomUpPass();
  }
  
  @Override
  void doTopDownPass(int x, int y, int width, int height) {
    super.doTopDownPass(x, y, width, height);
    
    updateLookImageData();
  }
  
  @Override
  void drawContents() {
    if (imgPath == null) {
      return;
    }
    
    glPushMatrix();
    {
      float k = getScale();
      
      glTranslatef(getCenterX(), getCenterY(), 0.0f);
      glScalef(k, k, k);
      
      glBindTexture(GL_TEXTURE_2D, texture);
      glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
      getLook().drawImage();
    }
    glPopMatrix();
  }
  
  public String getImagePath() {
    return imgPath;
  }
  
  public void setImagePath(String path) {
    if (Objects.equals(imgPath, path)) {
      return;
    }
    
    imgPath = path;
    
    if (textureCreated) {
      updateTexture();
    }
  }
  
  private void updateTexture() {
    // Delete existing texture, if any.
    
    deleteTexture();
    
    // Create new texture.
    
    Image img = (imgPath == null) ? null : GUIHome.loadImage(imgPath);
    
    texture        = Gfx.createTexture(img, true);
    textureCreated = true;
  }
  
  private void updateLookImageData() {
    getLook().setImageData(
      -getWidth () / 2,
      -getHeight() / 2,
      getWidth (),
      getHeight(),
      getCorners());
  }
  
  private void deleteTexture() {
    glDeleteTextures(texture);
  }
  
  @Override
  void deinitialize() {
    deleteTexture();
    
    super.deinitialize();
  }
}
