/*
 * DataFuncs.java
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

package com.uppgarn.nuncabola.functions;

import com.uppgarn.nuncabola.core.folder.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import com.uppgarn.codelibf.io.*;

import java.nio.file.*;
import java.util.*;

public final class DataFuncs {
  private static Folder dataFolder;
  
  public static void initialize() {
    dataFolder = new RootFolder(
      Arrays.asList(new Path[] {getUserDataDirectory(), getDataDirectory()}));
  }
  
  public static Folder getDataFolder() {
    return dataFolder;
  }
  
  public static Set<String> getPaths(
      String     parentPath,
      boolean    directory,
      PathFilter filter) {
    return dataFolder.getPaths(parentPath, directory, filter);
  }
  
  public static boolean exists(String path, boolean directory) {
    return dataFolder.exists(path, directory);
  }
  
  public static Source getSource(String path) {
    return dataFolder.getSource(path);
  }
  
  public static void deinitialize() {
    dataFolder.close();
    
    dataFolder = null;
  }
  
  private DataFuncs() {
  }
}
