/*
 * Progress.java
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

package com.uppgarn.nuncabola.core.progress;

import com.uppgarn.nuncabola.core.graphics.*;
import com.uppgarn.nuncabola.core.image.*;
import com.uppgarn.nuncabola.core.util.Util;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

public final class Progress {
  private static final int VBO_ENTRY = 12;
  private static final int VBO_OFF0  = 0;
  private static final int VBO_OFF1  = 8;
  private static final int VBO_BLOCK = 4;
  private static final int VBO_TOTAL = VBO_BLOCK * VBO_ENTRY;
  
  private int rectWidth;
  private int rectHeight;
  
  private int texture;
  private int vbo;
  
  private boolean firstUpdate;
  private long    lastTime;
  
  public Progress(Image img) {
    Image textureImg;
    
    int   refSize  = Math.min(Gfx.getWidth(), Gfx.getHeight());
    float imgRatio = (float) img.getWidth() / img.getHeight();
    
    int targetWidth  = Math.round(refSize     / 5.0f);
    int targetHeight = Math.round(targetWidth / imgRatio);
    
    if ((targetWidth >= img.getWidth()) || (targetHeight >= img.getHeight())) {
      // Let OpenGL handle the scaling.
      
      rectWidth  = targetWidth;
      rectHeight = targetHeight;
      
      textureImg = img;
    } else {
      // Manual scaling.
      
      rectWidth  = Util.ceilPowerOf2(targetWidth);
      rectHeight = Util.ceilPowerOf2(targetHeight);
      
      Image scaledImg = img.scaledDown(targetWidth, targetHeight);
      
      if ((rectWidth == targetWidth) && (rectHeight == targetHeight)) {
        textureImg = scaledImg;
      } else {
        textureImg = scaledImg.gridEnlarged(rectWidth, rectHeight);
      }
    }
    
    texture = createTexture(textureImg);
    vbo     = createVBO();
    
    firstUpdate = true;
    lastTime    = 0;
  }
  
  private int createTexture(Image img) {
    return Gfx.createTexture(img, false);
  }
  
  private void setVertex(ByteBuffer buf, int x, int y, float s, float t) {
    buf.putFloat(s);
    buf.putFloat(t);
    buf.putShort((short) x);
    buf.putShort((short) y);
  }
  
  private ByteBuffer getVBOBuffer() {
    ByteBuffer buf = BufferUtils.createByteBuffer(VBO_TOTAL);
    
    int x = rectWidth  / 2;
    int y = rectHeight / 2;
    
    setVertex(buf, -x, -y, 0.0f, 0.0f);
    setVertex(buf, +x, -y, 1.0f, 0.0f);
    setVertex(buf, -x, +y, 0.0f, 1.0f);
    setVertex(buf, +x, +y, 1.0f, 1.0f);
    
    buf.flip();
    
    return buf;
  }
  
  private int createVBO() {
    int vbo = glGenBuffersARB();
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, vbo);
    glBufferDataARB(GL_ARRAY_BUFFER_ARB, getVBOBuffer(), GL_STATIC_DRAW_ARB);
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
    
    return vbo;
  }
  
  private void draw(float alpha) {
    Gfx.setOrtho();
    
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    glEnableClientState(GL_VERTEX_ARRAY);
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, vbo);
    
    glTexCoordPointer(2, GL_FLOAT, VBO_ENTRY, VBO_OFF0);
    glVertexPointer  (2, GL_SHORT, VBO_ENTRY, VBO_OFF1);
    
    glDisable(GL_DEPTH_TEST);
    
    glPushMatrix();
    {
      glTranslatef(Gfx.getWidth() / 2, Gfx.getHeight() / 2, 0.0f);
      
      glBindTexture(GL_TEXTURE_2D, texture);
      glColor4f(1.0f, 1.0f, 1.0f, alpha);
      
      glDrawArrays(GL_TRIANGLE_STRIP, 0, VBO_BLOCK);
      
      glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
      glBindTexture(GL_TEXTURE_2D, 0);
    }
    glPopMatrix();
    
    glEnable(GL_DEPTH_TEST);
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
    
    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
  }
  
  public void show() {
    long  time = System.nanoTime();
    float dt   = (float) ((time - lastTime) / 1000000000.0);
    
    if (firstUpdate || (dt >= 0.05f)) {
      firstUpdate = false;
      lastTime    = time;
      
      Gfx.clear();
      
      draw(1.0f);
      
      Display.update();
    }
  }
  
  public void fadeOut() {
    long t0 = System.nanoTime();
    long t1 = t0 + 100 * 1000000L; // 100ms
    
    long t;
    
    do {
      try {
        Thread.sleep(1);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
      
      t = System.nanoTime();
      
      Gfx.clear();
      
      draw(1.0f - (float) (Math.min(t, t1) - t0) / (t1 - t0));
      
      Display.update();
    } while (t < t1);
  }
  
  public void deinitialize() {
    glDeleteTextures(texture);
    
    glDeleteBuffersARB(vbo);
  }
}
