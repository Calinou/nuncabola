/*
 * TextImagizer.java
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

import com.uppgarn.codelibf.util.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.nio.*;

public final class TextImagizer {
  private final Font font;
  
  private FontRenderContext renderContext;
  
  TextImagizer(Font font) {
    this.font = font;
    
    renderContext = createRenderContext();
  }
  
  private FontRenderContext createRenderContext() {
    BufferedImage bufImg = new BufferedImage(
      1,
      1,
      BufferedImage.TYPE_4BYTE_ABGR);
    
    Graphics2D g = bufImg.createGraphics();
    
    g.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setFont(font);
    
    FontRenderContext renderContext = g.getFontRenderContext();
    
    g.dispose();
    
    return renderContext;
  }
  
  private Rectangle2D getBounds(String str) {
    return font.getStringBounds(str, renderContext);
  }
  
  public Dimension getSize(String str) {
    int width;
    int height;
    
    if (str.isEmpty()) {
      // Empty string.
      
      Rectangle2D bounds = getBounds(" ");
      
      width  = 0;
      height = (int) Math.round(bounds.getHeight());
    } else {
      // Non-empty string.
      
      Rectangle2D bounds = getBounds(str);
      
      width  = (int) Math.round(bounds.getWidth ());
      height = (int) Math.round(bounds.getHeight());
    }
    
    return new Dimension(width, height);
  }
  
  private BufferedImage getBufferedImage(
      String            str,
      Holder<Dimension> origSize) {
    // Special case for an empty string.
    
    if (str.isEmpty()) {
      origSize.set(getSize(str));
      
      return null;
    }
    
    // Determine bounds.
    
    Rectangle2D bounds = getBounds(str);
    
    // Original size.
    
    int origWidth  = (int) Math.round(bounds.getWidth ());
    int origHeight = (int) Math.round(bounds.getHeight());
    
    origSize.set(new Dimension(origWidth, origHeight));
    
    if ((origWidth == 0) || (origHeight == 0)) {
      return null;
    }
    
    // Size rounded up to power-of-two.
    
    int width  = Util.ceilPowerOf2(origWidth);
    int height = Util.ceilPowerOf2(origHeight);
    
    // Create buffered image.
    
    BufferedImage bufImg = new BufferedImage(
      width,
      height,
      BufferedImage.TYPE_4BYTE_ABGR);
    
    // Draw text.
    
    Graphics2D g = bufImg.createGraphics();
    
    g.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setFont (font);
    g.setColor(Color.WHITE);
    
    float x = (float) ((width  - bounds.getWidth ()) / 2 + bounds.getX());
    float y = (float) ((height - bounds.getHeight()) / 2 - bounds.getY());
    
    g.drawString(str, x, y);
    g.dispose();
    
    return bufImg;
  }
  
  private Image getImage(BufferedImage bufImg) {
    DataBuffer dataBuf = bufImg.getRaster().getDataBuffer();
    byte[]     data    = ((DataBufferByte) dataBuf).getData();
    int        width   = bufImg.getWidth ();
    int        height  = bufImg.getHeight();
    
    // Create byte buffer.
    
    ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
    
    // Fill byte buffer.
    
    for (int p = width * height, idx = 0; p != 0; p--, idx += 4) {
      // Whiten RGB channels.
      
      buf.put((byte) 255);
      buf.put((byte) 255);
      buf.put((byte) 255);
      
      // Use alpha channel from source image.
      
      buf.put(data[idx]);
    }
    
    return new Image(buf, width, height, true);
  }
  
  public Image getImage(String str, Holder<Dimension> origSize) {
    BufferedImage bufImg = getBufferedImage(str, origSize);
    
    if (bufImg == null) {
      return null;
    }
    
    return getImage(bufImg);
  }
  
  public boolean canDisplay(char ch) {
    return font.canDisplay(ch);
  }
}
