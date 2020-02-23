/*
 * NullOutputStream.java
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

public final class NullOutputStream extends OutputStream {
  private static final NullOutputStream INSTANCE = new NullOutputStream();
  
  public static NullOutputStream getInstance() {
    return INSTANCE;
  }
  
  private NullOutputStream() {
  }
  
  @Override
  public void write(int b) throws IOException {
  }
  
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (b == null) {
      throw new NullPointerException();
    }
    if ((off < 0) || (len < 0) || (len > b.length - off)) {
      throw new IndexOutOfBoundsException();
    }
  }
}
