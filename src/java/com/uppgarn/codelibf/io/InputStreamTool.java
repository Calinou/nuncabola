/*
 * InputStreamTool.java
 *
 * Copyright (c) 1998-2020 Florian Priester
 *
 * CodeLibF is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */

package com.uppgarn.codelibf.io;

import java.io.*;

public final class InputStreamTool {
  public static byte[] readBytes(InputStream in, int len) throws IOException {
    if ((in == null) || (len < 0)) {
      throw new IllegalArgumentException();
    }
    
    byte[] bytes = new byte[len];
    
    for (int count = 0; count < len;) {
      int read = in.read(bytes, count, len - count);
      
      if (read == -1) {
        throw new EOFException();
      }
      
      count += read;
    }
    
    return bytes;
  }
  
  public static void skip(InputStream in, int len) throws IOException {
    if ((in == null) || (len < 0)) {
      throw new IllegalArgumentException();
    }
    
    for (int idx = 0; idx < len; idx++) {
      if (in.read() == -1) {
        throw new EOFException();
      }
    }
  }
  
  private InputStreamTool() {
  }
}
