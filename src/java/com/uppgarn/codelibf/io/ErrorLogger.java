/*
 * ErrorLogger.java
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
import java.time.*;

public final class ErrorLogger {
  private final Path file;
  private final int  limit;
  
  private int errorCount;
  
  public ErrorLogger(Path file) {
    this(file, -1);
  }
  
  public ErrorLogger(Path file, int limit) {
    if ((file == null) || (limit < -1)) {
      throw new IllegalArgumentException();
    }
    
    this.file  = file;
    this.limit = limit;
  }
  
  public Path getFile() {
    return file;
  }
  
  public int getLimit() {
    return limit;
  }
  
  public synchronized boolean hasErrorOccurred() {
    return errorCount > 0;
  }
  
  public synchronized int getErrorCount() {
    return errorCount;
  }
  
  public synchronized void log(Throwable t) {
    if (t == null) {
      throw new IllegalArgumentException();
    }
    
    errorCount++;
    
    if ((limit != -1) && (errorCount > limit)) {
      return;
    }
    
    try (PrintWriter out = new PrintWriter(
        Files.newBufferedWriter(
          file,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND))) {
      out.println(Instant.now());
      out.println("===");
      
      t.printStackTrace(out);
      
      out.println();
      
      out.flush();
    } catch (IOException | SecurityException ex) {
    }
  }
}
