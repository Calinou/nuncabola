/*
 * Look.java
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

import com.uppgarn.nuncabola.core.util.*;

import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

final class Look {
  public static final int VBO_ENTRY     = 16;
  public static final int VBO_OFF0      = 0;
  public static final int VBO_OFF1      = 4;
  public static final int VBO_OFF2      = 12;
  public static final int VBO_RECTANGLE = 16;
  public static final int VBO_TEXT      = 8;
  public static final int VBO_IMAGE     = 4;
  public static final int VBO_CONTENT   = VBO_TEXT;
  public static final int VBO_BLOCK     = VBO_RECTANGLE + VBO_CONTENT;
  public static final int VBO_TOTAL     = VBO_BLOCK * VBO_ENTRY;
  public static final int EBO_ENTRY     = 2;
  public static final int EBO_BLOCK     = 28;
  public static final int EBO_TOTAL     = EBO_BLOCK * EBO_ENTRY;
  
  private static final int[] RECTANGLE_BASE_ELEMS = {
    0, 1, 4, 5, 8, 9, 12, 13, 13, 1,  // top
    1, 2, 5, 6, 9, 10, 13, 14, 14, 2, // middle
    2, 3, 6, 7, 10, 11, 14, 15        // bottom
  };
  
  private final ByteBuffer vboBuf;
  private final ByteBuffer eboBuf;
  private final int        vbo;
  private final int        ebo;
  private final boolean[]  used;
  private final int        baseIdx;
  
  private final Theme theme;
  
  public Look(
      ByteBuffer vboBuf,
      ByteBuffer eboBuf,
      int        vbo,
      int        ebo,
      boolean[]  used,
      int        baseIdx) {
    this.vboBuf  = vboBuf;
    this.eboBuf  = eboBuf;
    this.vbo     = vbo;
    this.ebo     = ebo;
    this.used    = used;
    this.baseIdx = baseIdx;
    
    theme = GUIHome.getTheme();
    
    used[baseIdx] = true;
  }
  
  private void positionForRectangle() {
    int pos   = baseIdx * VBO_TOTAL;
    int limit = pos + VBO_RECTANGLE * VBO_ENTRY;
    
    vboBuf.limit(limit).position(pos);
    eboBuf.clear();
  }
  
  private void positionForText() {
    int pos   = baseIdx * VBO_TOTAL + VBO_RECTANGLE * VBO_ENTRY;
    int limit = pos + VBO_TEXT * VBO_ENTRY;
    
    vboBuf.limit(limit).position(pos);
  }
  
  private void positionForImage() {
    int pos   = baseIdx * VBO_TOTAL + VBO_RECTANGLE * VBO_ENTRY;
    int limit = pos + VBO_IMAGE * VBO_ENTRY;
    
    vboBuf.limit(limit).position(pos);
  }
  
  private void setVertex(int x, int y, float s, float t, Color color) {
    vboBuf.put     ((byte) color.getR());
    vboBuf.put     ((byte) color.getG());
    vboBuf.put     ((byte) color.getB());
    vboBuf.put     ((byte) color.getA());
    vboBuf.putFloat(s);
    vboBuf.putFloat(t);
    vboBuf.putShort((short) x);
    vboBuf.putShort((short) y);
  }
  
  private void setElement(int elem) {
    eboBuf.putShort((short) elem);
  }
  
  private void copyVertexData() {
    int off = vboBuf.position();
    
    glBindBufferARB   (GL_ARRAY_BUFFER_ARB, vbo);
    glBufferSubDataARB(GL_ARRAY_BUFFER_ARB, off, vboBuf);
    glBindBufferARB   (GL_ARRAY_BUFFER_ARB, 0);
  }
  
  private void copyElementData() {
    int off = baseIdx * EBO_TOTAL;
    
    glBindBufferARB   (GL_ELEMENT_ARRAY_BUFFER_ARB, ebo);
    glBufferSubDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, off, eboBuf);
    glBindBufferARB   (GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
  }
  
  public void setRectangleData(
      int     x,
      int     y,
      int     width,
      int     height,
      Corners corners) {
    positionForRectangle();
    
    int x0 = x;
    int x1 = x +          (corners.getW() ? GUIHome.getPadding() : 0);
    int x2 = x + width  - (corners.getE() ? GUIHome.getPadding() : 0);
    int x3 = x + width;
    
    int y0 = y + height;
    int y1 = y + height - (corners.getN() ? GUIHome.getPadding() : 0);
    int y2 = y +          (corners.getS() ? GUIHome.getPadding() : 0);
    int y3 = y;
    
    float s0 = theme.getS(0);
    float s1 = theme.getS(1);
    float s2 = theme.getS(2);
    float s3 = theme.getS(3);
    
    float t0 = theme.getT(0);
    float t1 = theme.getT(1);
    float t2 = theme.getT(2);
    float t3 = theme.getT(3);
    
    setVertex(x0, y0, s0, t0, Color.WHITE);
    setVertex(x0, y1, s0, t1, Color.WHITE);
    setVertex(x0, y2, s0, t2, Color.WHITE);
    setVertex(x0, y3, s0, t3, Color.WHITE);
    
    setVertex(x1, y0, s1, t0, Color.WHITE);
    setVertex(x1, y1, s1, t1, Color.WHITE);
    setVertex(x1, y2, s1, t2, Color.WHITE);
    setVertex(x1, y3, s1, t3, Color.WHITE);
    
    setVertex(x2, y0, s2, t0, Color.WHITE);
    setVertex(x2, y1, s2, t1, Color.WHITE);
    setVertex(x2, y2, s2, t2, Color.WHITE);
    setVertex(x2, y3, s2, t3, Color.WHITE);
    
    setVertex(x3, y0, s3, t0, Color.WHITE);
    setVertex(x3, y1, s3, t1, Color.WHITE);
    setVertex(x3, y2, s3, t2, Color.WHITE);
    setVertex(x3, y3, s3, t3, Color.WHITE);
    
    for (int idx = 0; idx < EBO_BLOCK; idx++) {
      setElement(baseIdx * VBO_BLOCK + RECTANGLE_BASE_ELEMS[idx]);
    }
    
    positionForRectangle();
    copyVertexData();
    copyElementData();
  }
  
  public void setTextData(
      int   x,
      int   y,
      int   width,
      int   height,
      Color color0,
      Color color1) {
    positionForText();
    
    if ((width > 0) && (height > 0)) {
      int width2  = Util.ceilPowerOf2(width);
      int height2 = Util.ceilPowerOf2(height);
      
      int myWidth  = width  + (((width2  - width ) % 2 != 0) ? 1 : 0);
      int myHeight = height + (((height2 - height) % 2 != 0) ? 1 : 0);
      int myX      = x - (myWidth - width);
      
      int x0 = myX;
      int y0 = y;
      int x1 = myX + myWidth;
      int y1 = y   + myHeight;
      int d  = height / 16;
      
      float s0 = 0.5f * (width2  - myWidth ) / width2;
      float t0 = 0.5f * (height2 - myHeight) / height2;
      float s1 = 1.0f - s0;
      float t1 = 1.0f - t0;
      
      setVertex(x0 + d, y1 - d, s0, t0, Color.SHADOW);
      setVertex(x0 + d, y0 - d, s0, t1, Color.SHADOW);
      setVertex(x1 + d, y1 - d, s1, t0, Color.SHADOW);
      setVertex(x1 + d, y0 - d, s1, t1, Color.SHADOW);
      
      setVertex(x0, y1, s0, t0, color0);
      setVertex(x0, y0, s0, t1, color1);
      setVertex(x1, y1, s1, t0, color0);
      setVertex(x1, y0, s1, t1, color1);
    } else {
      while (vboBuf.hasRemaining()) {
        vboBuf.put((byte) 0);
      }
    }
    
    positionForText();
    copyVertexData();
  }
  
  public void setImageData(
      int     x,
      int     y,
      int     width,
      int     height,
      Corners corners) {
    positionForImage();
    
    int x0 = x +          (corners.getW() ? GUIHome.getPadding() : 0);
    int x1 = x + width  - (corners.getE() ? GUIHome.getPadding() : 0);
    
    int y0 = y + height - (corners.getN() ? GUIHome.getPadding() : 0);
    int y1 = y +          (corners.getS() ? GUIHome.getPadding() : 0);
    
    setVertex(x0, y0, 0.0f, 1.0f, Color.WHITE);
    setVertex(x0, y1, 0.0f, 0.0f, Color.WHITE);
    setVertex(x1, y0, 1.0f, 1.0f, Color.WHITE);
    setVertex(x1, y1, 1.0f, 0.0f, Color.WHITE);
    
    positionForImage();
    copyVertexData();
  }
  
  public void drawRectangle() {
    int first = baseIdx * EBO_TOTAL;
    int count = EBO_BLOCK;
    
    glDrawElements(GL_TRIANGLE_STRIP, count, GL_UNSIGNED_SHORT, first);
  }
  
  public void drawText() {
    int first = baseIdx * VBO_BLOCK + VBO_RECTANGLE;
    int count = VBO_TEXT;
    
    glDrawArrays(GL_TRIANGLE_STRIP, first, count);
  }
  
  public void drawImage() {
    int first = baseIdx * VBO_BLOCK + VBO_RECTANGLE;
    int count = VBO_IMAGE;
    
    glDrawArrays(GL_TRIANGLE_STRIP, first, count);
  }
  
  public void deinitialize() {
    used[baseIdx] = false;
  }
}
