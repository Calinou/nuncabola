/*
 * Image.java
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

package com.uppgarn.nuncabola.core.image;

import com.uppgarn.nuncabola.core.util.*;

import java.nio.*;

public final class Image {
  private final ByteBuffer buf;
  private final int        width;
  private final int        height;
  private final boolean    alpha;
  
  public Image(ByteBuffer buf, int width, int height, boolean alpha) {
    this.buf    = (ByteBuffer) buf.duplicate().rewind();
    this.width  = width;
    this.height = height;
    this.alpha  = alpha;
  }
  
  public ByteBuffer getBuffer() {
    return buf.duplicate();
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public boolean hasAlpha() {
    return alpha;
  }
  
  public Image scaledDown(int factor) {
    assert factor >= 1;
    
    int newWidth  = width  / factor;
    int newHeight = height / factor;
    int bytes     = alpha ? 4 : 3;
    
    // Create byte buffer.
    
    ByteBuffer newBuf = ByteBuffer.allocateDirect(newWidth * newHeight * bytes);
    
    // Iterate each component of each destination pixel.
    
    int y0Inc    = (width - newWidth) * factor * bytes;
    int x0Inc    = (factor - 1)                * bytes;
    int y1Inc    = (width - factor)            * bytes;
    int xyFactor = factor * factor;
    
    for (int y0 = newHeight, off = 0; y0 != 0; y0--, off += y0Inc) {
      for (int x0 = newWidth; x0 != 0; x0--, off += x0Inc) {
        for (int b = bytes; b != 0; b--, off++) {
          // Average the source pixel block for each.
          
          int value = 0;
          
          for (int y1 = factor, idx = off; y1 != 0; y1--, idx += y1Inc) {
            for (int x1 = factor; x1 != 0; x1--, idx += bytes) {
              value += buf.get(idx) & 0xFF;
            }
          }
          
          newBuf.put((byte) (value / xyFactor));
        }
      }
    }
    
    return new Image(newBuf, newWidth, newHeight, alpha);
  }
  
  public Image scaledDown(int newWidth, int newHeight) {
    assert newWidth  <= width;
    assert newHeight <= height;
    
    int bytes = alpha ? 4 : 3;
    
    // Create byte buffer.
    
    ByteBuffer newBuf = ByteBuffer.allocateDirect(newWidth * newHeight * bytes);
    
    // Calculate indices, weights and factors for both axes.
    
    int  [] xIndices0 = null, yIndices0 = null;
    int  [] xIndices1 = null, yIndices1 = null;
    float[] xWeights0 = null, yWeights0 = null;
    float[] xWeights1 = null, yWeights1 = null;
    float[] xFactors  = null, yFactors  = null;
    
    for (int axis = 0; axis < 2; axis++) {
      int size;
      int newSize;
      
      if (axis == 0) {
        size    = width;
        newSize = newWidth;
      } else {
        size    = height;
        newSize = newHeight;
      }
      
      int  [] indices0 = new int  [newSize];
      int  [] indices1 = new int  [newSize];
      float[] weights0 = new float[newSize];
      float[] weights1 = new float[newSize];
      float[] factors  = new float[newSize];
      
      float factor = (float) size / newSize;
      float fIdx0  = 0.0f;
      
      for (int idx = 0; idx < newSize; idx++) {
        float fIdx1 = Util.snapToInt((idx + 1) * factor, 0.01f);
        int   iIdx0 = (int) fIdx0;
        int   iIdx1 = (int) fIdx1;
        
        indices0[idx] = iIdx0;
        indices1[idx] = (iIdx1 < fIdx1) ? iIdx1 : iIdx1 - 1;
        weights0[idx] = 1 - (fIdx0 - indices0[idx]);
        weights1[idx] = fIdx1 - indices1[idx];
        factors [idx] = weights0[idx]
                      + ((indices0[idx] != indices1[idx]) ? weights1[idx] : 0)
                      + Math.max(indices1[idx] - indices0[idx] - 1, 0);
        
        fIdx0 = fIdx1;
      }
      
      if (axis == 0) {
        xIndices0 = indices0;
        xIndices1 = indices1;
        xWeights0 = weights0;
        xWeights1 = weights1;
        xFactors  = factors;
      } else {
        yIndices0 = indices0;
        yIndices1 = indices1;
        yWeights0 = weights0;
        yWeights1 = weights1;
        yFactors  = factors;
      }
    }
    
    // Iterate each component of each destination pixel.
    
    for (int y0 = 0; y0 < newHeight; y0++) {
      for (int x0 = 0; x0 < newWidth; x0++) {
        for (int b = 0; b < bytes; b++) {
          // Average the source pixel block for each.
          
          float yValue = 0.0f;
          
          for (int y1 = yIndices0[y0]; y1 <= yIndices1[y0]; y1++) {
            float xValue = 0.0f;
            
            for (int x1 = xIndices0[x0], idx = (y1 * width + x1) * bytes + b;
                x1 <= xIndices1[x0];
                x1++, idx += bytes) {
              int value = buf.get(idx) & 0xFF;
              
              if (x1 == xIndices0[x0]) {
                xValue += value * xWeights0[x0];
              } else if (x1 == xIndices1[x0]) {
                xValue += value * xWeights1[x0];
              } else {
                xValue += value;
              }
            }
            
            xValue /= xFactors[x0];
            
            if (y1 == yIndices0[y0]) {
              yValue += xValue * yWeights0[y0];
            } else if (y1 == yIndices1[y0]) {
              yValue += xValue * yWeights1[y0];
            } else {
              yValue += xValue;
            }
          }
          
          yValue /= yFactors[y0];
          
          newBuf.put((byte) (yValue + 0.5f));
        }
      }
    }
    
    return new Image(newBuf, newWidth, newHeight, alpha);
  }
  
  public Image gridEnlarged(int newWidth, int newHeight) {
    assert newWidth  >= width;
    assert newHeight >= height;
    
    int bytes = alpha ? 4 : 3;
    
    // Create byte buffer.
    
    ByteBuffer newBuf = ByteBuffer.allocateDirect(newWidth * newHeight * bytes);
    
    // Copy source pixels to the larger destination grid.
    
    int lineLen    = width    * bytes;
    int newLineLen = newWidth * bytes;
    int xOff       = (newWidth  - width ) / 2;
    int yOff       = (newHeight - height) / 2;
    int xyOff      = (yOff * newWidth + xOff) * bytes;
    
    for (int y = height, off = xyOff, idx = 0; y != 0; y--, off += newLineLen) {
      newBuf.position(off);
      
      for (int p = lineLen; p != 0; p--, idx++) {
        newBuf.put(buf.get(idx));
      }
    }
    
    return new Image(newBuf, newWidth, newHeight, alpha);
  }
}
