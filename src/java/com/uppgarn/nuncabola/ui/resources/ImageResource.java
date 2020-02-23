/*
 * ImageResource.java
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

package com.uppgarn.nuncabola.ui.resources;

import com.uppgarn.nuncabola.core.image.*;

import java.io.*;
import java.net.*;

public final class ImageResource {
  public static Image getImage(String fileName, boolean topDown) {
    URL url = ImageResource.class.getResource("images/".concat(fileName));
    
    if (url == null) {
      throw new RuntimeException();
    }
    
    try {
      return SimpleImageLoader.load(url, topDown);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private ImageResource() {
  }
}
