/*
 * RootFolder.java
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

import java.nio.file.*;
import java.util.*;

public final class RootFolder extends BranchFolder {
  private List<DirectoryFolder> folders;
  
  public RootFolder(List<Path> dirs) {
    folders = createDirectoryFolders(dirs);
  }
  
  private List<DirectoryFolder> createDirectoryFolders(List<Path> dirs) {
    List<DirectoryFolder> folders = new ArrayList<>(dirs.size());
    
    for (Path dir: dirs) {
      folders.add(new DirectoryFolder(dir));
    }
    
    return folders;
  }
  
  @Override
  List<? extends Folder> getChildren() {
    return folders;
  }
}
