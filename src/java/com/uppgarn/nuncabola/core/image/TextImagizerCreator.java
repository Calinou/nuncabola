/*
 * TextImagizerCreator.java
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

import com.uppgarn.codelibf.io.*;

import java.awt.*;
import java.io.*;
import java.nio.file.*;

public final class TextImagizerCreator {
  private Font font;
  
  public TextImagizerCreator(Source src) {
    font = createFont(src);
  }
  
  private Font loadFont(Source src) {
    try {
      Path file = src.getFile();
      
      if (file != null) {
        // File-based font loading (may be more efficient).
        
        return Font.createFont(Font.TRUETYPE_FONT, file.toFile());
      } else {
        // Stream-based font loading.
        
        try (InputStream in = src.newInputStream()) {
          return Font.createFont(Font.TRUETYPE_FONT, in);
        }
      }
    } catch (IOException | FontFormatException ex) {
      return null;
    }
  }
  
  private Font createFont(Source src) {
    Font loadedFont = loadFont(src);
    
    if (loadedFont != null) {
      return loadedFont;
    }
    
    return new Font(Font.SANS_SERIF, Font.PLAIN, 0);
  }
  
  public TextImagizer create(boolean bold, float size) {
    int style = bold ? Font.BOLD : Font.PLAIN;
    
    return new TextImagizer(font.deriveFont(style, size));
  }
}
