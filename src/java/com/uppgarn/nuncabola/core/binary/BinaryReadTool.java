/*
 * BinaryReadTool.java
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

package com.uppgarn.nuncabola.core.binary;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public final class BinaryReadTool {
  public static int readUnsignedByte(InputStream in) throws IOException {
    int b = in.read();
    
    if (b == -1) {
      throw new EOFException();
    }
    
    return b;
  }
  
  public static int readUnsignedShort(InputStream in) throws IOException {
    int b0 = in.read();
    int b1 = in.read();
    
    if ((b0 | b1) < 0) {
      throw new EOFException();
    }
    
    return b0 | (b1 << 8);
  }
  
  public static int readInt(InputStream in) throws IOException {
    int b0 = in.read();
    int b1 = in.read();
    int b2 = in.read();
    int b3 = in.read();
    
    if ((b0 | b1 | b2 | b3) < 0) {
      throw new EOFException();
    }
    
    return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
  }
  
  public static float readFloat(InputStream in) throws IOException {
    return Float.intBitsToFloat(readInt(in));
  }
  
  public static String readString(InputStream in) throws IOException {
    byte[] bytes = new byte[64];
    int    len   = 0;
    
    for (int b; (b = readUnsignedByte(in)) != 0;) {
      if (len == bytes.length) {
        bytes = Arrays.copyOf(bytes, bytes.length * 2);
      }
      
      bytes[len++] = (byte) b;
    }
    
    return new String(bytes, 0, len, StandardCharsets.UTF_8);
  }
  
  public static String readString(InputStream in, int padLen)
      throws IOException {
    byte[] bytes = new byte[padLen];
    int    len   = 0;
    
    boolean nulFound = false;
    
    for (int idx = 0; idx < padLen; idx++) {
      int b = readUnsignedByte(in);
      
      if (!nulFound) {
        if (b != 0) {
          bytes[len++] = (byte) b;
        } else {
          nulFound = true;
        }
      }
    }
    
    return new String(bytes, 0, len, StandardCharsets.UTF_8);
  }
  
  private BinaryReadTool() {
  }
}
