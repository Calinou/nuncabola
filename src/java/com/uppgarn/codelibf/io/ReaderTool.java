/*
 * ReaderTool.java
 *
 * Copyright (c) 1998-2019 Florian Priester
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

public final class ReaderTool {
  public static String readLine(BufferedReader in) throws IOException {
    if (in == null) {
      throw new IllegalArgumentException();
    }
    
    String str = in.readLine();
    
    if (str == null) {
      throw new EOFException();
    }
    
    return str;
  }
  
  private ReaderTool() {
  }
}
