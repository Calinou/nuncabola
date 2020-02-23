/*
 * SimpleImageLoader.java
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

import java.io.*;
import java.net.*;
import java.nio.*;

public final class SimpleImageLoader {
  public static Image load(URL url, boolean topDown) throws IOException {
    try (DataInputStream in = new DataInputStream(url.openStream())) {
      int     width  = in.readInt();
      int     height = in.readInt();
      boolean alpha  = in.readBoolean();
      
      int lineLen = width  * (alpha ? 4 : 3);
      int len     = height * lineLen;
      
      byte[] array = InputStreamTool.readBytes(in, len);
      
      ByteBuffer buf = ByteBuffer.allocateDirect(len);
      
      if (topDown) {
        buf.put(array);
      } else {
        for (int y = height - 1; y >= 0; y--) {
          buf.put(array, y * lineLen, lineLen);
        }
      }
      
      return new Image(buf, width, height, alpha);
    }
  }
  
  private SimpleImageLoader() {
  }
}
