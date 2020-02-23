/*
 * BranchFolder.java
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

abstract class BranchFolder extends Folder {
  abstract List<? extends Folder> getChildren();
  
  @Override
  final void collectPaths(
      Set<String> paths,
      String      parentPath,
      boolean     directory) {
    for (Folder child: getChildren()) {
      child.collectPaths(paths, parentPath, directory);
    }
  }
  
  @Override
  public final boolean exists(String path, boolean directory) {
    for (Folder child: getChildren()) {
      if (child.exists(path, directory)) {
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  final Source getRawSource(String path) {
    for (Folder child: getChildren()) {
      Source src = child.getRawSource(path);
      
      if (src != null) {
        return src;
      }
    }
    
    return null;
  }
  
  @Override
  public void close() {
    for (Folder child: getChildren()) {
      child.close();
    }
  }
}
