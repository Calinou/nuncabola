/*
 * BufferedImageTool.java
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

import java.awt.color.*;
import java.awt.image.*;
import java.nio.*;

final class BufferedImageTool {
  public static Image getImage(BufferedImage bufImg, boolean topDown) {
    Raster     raster  = bufImg.getRaster();
    DataBuffer dataBuf = raster.getDataBuffer();
    ColorModel model   = bufImg.getColorModel();
    ColorSpace space   = model .getColorSpace();
    int        width   = bufImg.getWidth ();
    int        height  = bufImg.getHeight();
    boolean    alpha   = model .hasAlpha();
    
    // Create byte buffer.
    
    ByteBuffer buf = ByteBuffer.allocateDirect(
      width * height * (alpha ? 4 : 3));
    
    // Fill byte buffer.
    
    int y0;
    int y1;
    int yStep;
    
    if (topDown) {
      y0    = 0;
      y1    = height;
      yStep = +1;
    } else {
      y0    = height - 1;
      y1    = -1;
      yStep = -1;
    }
    
    if (bufImg.getType() == BufferedImage.TYPE_3BYTE_BGR) {
      // TYPE_3BYTE_BGR.
      
      byte[] data = ((DataBufferByte) dataBuf).getData();
      
      for (int y = y0; y != y1; y += yStep) {
        for (int x = width, idx = y * width * 3; x != 0; x--, idx += 3) {
          buf.put(data[idx + 2]);
          buf.put(data[idx + 1]);
          buf.put(data[idx]);
        }
      }
    } else if (bufImg.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
      // TYPE_4BYTE_ABGR.
      
      byte[] data = ((DataBufferByte) dataBuf).getData();
      
      for (int y = y0; y != y1; y += yStep) {
        for (int x = width, idx = y * width * 4; x != 0; x--, idx += 4) {
          buf.put(data[idx + 3]);
          buf.put(data[idx + 2]);
          buf.put(data[idx + 1]);
          buf.put(data[idx]);
        }
      }
    } else if (bufImg.getType() == BufferedImage.TYPE_BYTE_GRAY) {
      // TYPE_BYTE_GRAY.
      
      byte[] data = ((DataBufferByte) dataBuf).getData();
      
      for (int y = y0; y != y1; y += yStep) {
        for (int x = width, idx = y * width; x != 0; x--, idx++) {
          byte b = data[idx];
          
          buf.put(b);
          buf.put(b);
          buf.put(b);
        }
      }
    } else if ((raster.getNumBands() == 3)
        && (model.getPixelSize() == 24)
        && !alpha) {
      // Standard RGB.
      
      for (int y = y0; y != y1; y += yStep) {
        for (int x = 0; x < width; x++) {
          buf.put((byte) raster.getSample(x, y, 0));
          buf.put((byte) raster.getSample(x, y, 1));
          buf.put((byte) raster.getSample(x, y, 2));
        }
      }
    } else if ((raster.getNumBands() == 4)
        && (model.getPixelSize() == 32)
        && alpha
        && !model.isAlphaPremultiplied()) {
      // Standard RGBA.
      
      for (int y = y0; y != y1; y += yStep) {
        for (int x = 0; x < width; x++) {
          buf.put((byte) raster.getSample(x, y, 0));
          buf.put((byte) raster.getSample(x, y, 1));
          buf.put((byte) raster.getSample(x, y, 2));
          buf.put((byte) raster.getSample(x, y, 3));
        }
      }
    } else if ((space.getType() == ColorSpace.TYPE_GRAY)
        && (raster.getNumBands() == 1)
        && (model.getPixelSize() == 8)
        && !alpha) {
      // Standard grayscale.
      
      for (int y = y0; y != y1; y += yStep) {
        for (int x = 0; x < width; x++) {
          byte b = (byte) raster.getSample(x, y, 0);
          
          buf.put(b);
          buf.put(b);
          buf.put(b);
        }
      }
    } else if ((space.getType() == ColorSpace.TYPE_GRAY)
        && (raster.getNumBands() == 2)
        && (model.getPixelSize() == 16)
        && alpha
        && !model.isAlphaPremultiplied()) {
      // Standard grayscale with alpha.
      
      for (int y = y0; y != y1; y += yStep) {
        for (int x = 0; x < width; x++) {
          byte b0 = (byte) raster.getSample(x, y, 0);
          byte b1 = (byte) raster.getSample(x, y, 1);
          
          buf.put(b0);
          buf.put(b0);
          buf.put(b0);
          buf.put(b1);
        }
      }
    } else {
      // Other image type. Retrieve the pixel data using
      // format-independent code (which is slower).
      
      Object data = null;
      
      if (!alpha) {
        // No alpha.
        
        for (int y = y0; y != y1; y += yStep) {
          for (int x = 0; x < width; x++) {
            data = raster.getDataElements(x, y, data);
            
            buf.put((byte) model.getRed  (data));
            buf.put((byte) model.getGreen(data));
            buf.put((byte) model.getBlue (data));
          }
        }
      } else {
        // Alpha.
        
        for (int y = y0; y != y1; y += yStep) {
          for (int x = 0; x < width; x++) {
            data = raster.getDataElements(x, y, data);
            
            buf.put((byte) model.getRed  (data));
            buf.put((byte) model.getGreen(data));
            buf.put((byte) model.getBlue (data));
            buf.put((byte) model.getAlpha(data));
          }
        }
      }
    }
    
    return new Image(buf, width, height, alpha);
  }
  
  /**
   * Currently, this method supports bottom-up RGBA images only.
   */
  public static BufferedImage getBufferedImage(Image img) {
    assert img.hasAlpha();
    
    ByteBuffer buf    = img.getBuffer();
    int        width  = img.getWidth ();
    int        height = img.getHeight();
    
    // Create buffered image.
    
    BufferedImage bufImg = new BufferedImage(
      width,
      height,
      BufferedImage.TYPE_3BYTE_BGR);
    
    // Fill buffered image.
    
    DataBuffer dataBuf = bufImg.getRaster().getDataBuffer();
    byte[]     data    = ((DataBufferByte) dataBuf).getData();
    
    for (int y = height - 1; y >= 0; y--) {
      for (int x = width, idx = y * width * 3; x != 0; x--, idx += 3) {
        data[idx + 2] = buf.get();
        data[idx + 1] = buf.get();
        data[idx]     = buf.get();
        
        buf.get();
      }
    }
    
    return bufImg;
  }
  
  private BufferedImageTool() {
  }
}
