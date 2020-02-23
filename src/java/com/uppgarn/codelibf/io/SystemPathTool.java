/*
 * SystemPathTool.java
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
import java.util.*;

public final class SystemPathTool {
  private static String getApplicationDataRootDirectoryName() {
    if (System.getProperty("os.name").startsWith("Windows")) {
      // Windows.
      
      return System.getenv("APPDATA");
    } else {
      // Other platforms.
      
      return System.getProperty("user.home");
    }
  }
  
  public static Path getApplicationDataRootDirectory() {
    String name = getApplicationDataRootDirectoryName();
    
    if (name == null) {
      return null;
    }
    
    try {
      return Paths.get(name);
    } catch (InvalidPathException ex) {
      return null;
    }
  }
  
  private static String getApplicationDataDirectoryName(String appName) {
    if (System.getProperty("os.name").startsWith("Windows")) {
      // Windows.
      
      return appName;
    } else {
      // Other platforms.
      
      return ".".concat(appName.toLowerCase(Locale.ENGLISH));
    }
  }
  
  public static Path getApplicationDataDirectory(String appName) {
    if (appName == null) {
      throw new IllegalArgumentException();
    }
    
    Path rootDir = getApplicationDataRootDirectory();
    
    if (rootDir == null) {
      rootDir = Paths.get(""); // last resort
    }
    
    Path dir;
    
    try {
      dir = rootDir.resolve(getApplicationDataDirectoryName(appName));
    } catch (InvalidPathException ex) {
      return null;
    }
    
    if (!Files.exists(dir)) {
      try {
        Files.createDirectory(dir);
      } catch (IOException ex) {
      }
    }
    
    return dir;
  }
  
  private SystemPathTool() {
  }
}
