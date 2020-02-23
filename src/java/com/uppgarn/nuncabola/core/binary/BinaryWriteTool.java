/*
 * BinaryWriteTool.java
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

public final class BinaryWriteTool {
  public static void writeShort(OutputStream out, int value)
      throws IOException {
    out.write( value        & 0xFF);
    out.write((value >>> 8) & 0xFF);
  }
  
  public static void writeInt(OutputStream out, int value) throws IOException {
    out.write( value         & 0xFF);
    out.write((value >>> 8)  & 0xFF);
    out.write((value >>> 16) & 0xFF);
    out.write((value >>> 24) & 0xFF);
  }
  
  public static void writeFloat(OutputStream out, float value)
      throws IOException {
    writeInt(out, Float.floatToIntBits(value));
  }
  
  public static void writeString(OutputStream out, String str)
      throws IOException {
    out.write(str.getBytes(StandardCharsets.UTF_8));
    out.write(0);
  }
  
  public static void writeString(OutputStream out, String str, int padLen)
      throws IOException {
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    
    out.write(bytes, 0, Math.min(bytes.length, padLen));
    
    for (int idx = bytes.length; idx < padLen; idx++) {
      out.write(0);
    }
  }
  
  private BinaryWriteTool() {
  }
}
