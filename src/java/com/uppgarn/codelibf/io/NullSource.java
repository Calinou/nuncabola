/*
 * NullSource.java
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
import java.nio.file.*;

public final class NullSource implements Source {
  private static final NullSource INSTANCE = new NullSource();
  
  public static NullSource getInstance() {
    return INSTANCE;
  }
  
  private NullSource() {
  }
  
  @Override
  public InputStream newInputStream() throws SourceException {
    throw new SourceException();
  }
  
  @Override
  public Path getFile() {
    return null;
  }
}
