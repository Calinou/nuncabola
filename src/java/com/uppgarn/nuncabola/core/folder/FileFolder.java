/*
 * FileFolder.java
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

import java.io.*;
import java.nio.file.*;
import java.util.*;

final class FileFolder extends Folder {
  private final Path dir;
  
  public FileFolder(Path dir) {
    this.dir = dir;
  }
  
  private Path getFile(String path, boolean rootValid) {
    Path file;
    
    try {
      String fileName = (File.separatorChar == '\\')
                        ? path.replace('/', '\\') : path;
      
      file = dir.resolve(fileName).normalize();
    } catch (InvalidPathException ex) {
      return null;
    }
    
    // Security check: File must be inside designated directory.
    
    if (!file.startsWith(dir) || (!rootValid && file.equals(dir))) {
      return null;
    }
    
    return file;
  }
  
  @Override
  void collectPaths(Set<String> paths, String parentPath, boolean directory) {
    Path parent = getFile(parentPath, true);
    
    if (parent == null) {
      return;
    }
    
    DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
      @Override
      public boolean accept(Path entry) throws IOException {
        if (directory) {
          return Files.isDirectory  (entry);
        } else {
          return Files.isRegularFile(entry);
        }
      }
    };
    
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(
        parent,
        filter)) {
      for (Path entry: stream) {
        paths.add(entry.getFileName().toString());
      }
    } catch (IOException ex) {
    }
  }
  
  @Override
  public boolean exists(String path, boolean directory) {
    Path file = getFile(path, false);
    
    if (file == null) {
      return false;
    }
    
    if (directory) {
      return Files.isDirectory  (file);
    } else {
      return Files.isRegularFile(file);
    }
  }
  
  @Override
  Source getRawSource(String path) {
    Path file = getFile(path, false);
    
    if (file == null) {
      return null;
    }
    
    if (Files.isRegularFile(file)) {
      return new FileSource(file);
    } else {
      return null;
    }
  }
}
