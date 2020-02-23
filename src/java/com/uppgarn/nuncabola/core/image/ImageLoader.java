/*
 * ImageLoader.java
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

import javax.imageio.*;

import java.awt.image.*;
import java.io.*;
import java.nio.file.*;

public final class ImageLoader {
  public static Image load(Path file, boolean topDown) throws IOException {
    BufferedImage bufImg = ImageIO.read(file.toFile());
    
    if (bufImg == null) {
      return null;
    }
    
    return BufferedImageTool.getImage(bufImg, topDown);
  }
  
  public static Image load(InputStream in, boolean topDown) throws IOException {
    BufferedImage bufImg = ImageIO.read(in);
    
    if (bufImg == null) {
      return null;
    }
    
    return BufferedImageTool.getImage(bufImg, topDown);
  }
  
  public static Image load(Source src, boolean topDown) throws IOException {
    Path file = src.getFile();
    
    if (file != null) {
      // File-based image loading (may be more efficient).
      
      return load(file, topDown);
    } else {
      // Stream-based image loading.
      
      try (InputStream in = new BufferedInputStream(src.newInputStream())) {
        return load(in, topDown);
      }
    }
  }
  
  private ImageLoader() {
  }
}
