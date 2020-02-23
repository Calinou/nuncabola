/*
 * Folder.java
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

package com.uppgarn.nuncabola.core.folder;

import com.uppgarn.codelibf.io.*;

import java.util.*;

public abstract class Folder {
  Folder() {
  }
  
  abstract void collectPaths(
      Set<String> paths,
      String      parentPath,
      boolean     directory);
  
  public final Set<String> getPaths(
      String     parentPath,
      boolean    directory,
      PathFilter filter) {
    Set<String> paths = new HashSet<>();
    
    collectPaths(paths, parentPath, directory);
    
    if (filter != null) {
      for (Iterator<String> it = paths.iterator(); it.hasNext();) {
        if (!filter.accepts(it.next())) {
          it.remove();
        }
      }
    }
    
    return paths;
  }
  
  public abstract boolean exists(String path, boolean directory);
  
  abstract Source getRawSource(String path);
  
  public final Source getSource(String path) {
    Source src = getRawSource(path);
    
    if (src != null) {
      return src;
    } else {
      return NullSource.getInstance();
    }
  }
  
  public void close() {
  }
}
