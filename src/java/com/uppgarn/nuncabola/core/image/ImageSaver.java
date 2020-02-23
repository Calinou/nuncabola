/*
 * ImageSaver.java
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

import javax.imageio.*;

import java.awt.image.*;
import java.io.*;
import java.nio.file.*;

public final class ImageSaver {
  /**
   * Currently, this method supports bottom-up RGBA images only.
   */
  public static void save(Path file, Image img) throws IOException {
    BufferedImage bufImg = BufferedImageTool.getBufferedImage(img);
    
    ImageIO.write(bufImg, "png", file.toFile());
  }
  
  private ImageSaver() {
  }
}
