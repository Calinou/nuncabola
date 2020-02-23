/*
 * ZipSource.java
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
import java.util.zip.*;

public final class ZipSource implements Source {
  private final ZipFile  zip;
  private final ZipEntry entry;
  
  public ZipSource(ZipFile zip, ZipEntry entry) {
    if ((zip == null) || (entry == null)) {
      throw new IllegalArgumentException();
    }
    
    this.zip   = zip;
    this.entry = entry;
  }
  
  @Override
  public InputStream newInputStream() throws SourceException {
    try {
      InputStream in = zip.getInputStream(entry);
      
      if (in == null) {
        throw new SourceException();
      }
      
      return in;
    } catch (IOException ex) {
      throw new SourceException(ex);
    }
  }
  
  @Override
  public Path getFile() {
    return null;
  }
}
