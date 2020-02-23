/*
 * ZipFolder.java
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
import java.util.zip.*;

final class ZipFolder extends Folder {
  private ZipFile zip;
  
  private Set<String>           dirPaths;
  private Map<String, ZipEntry> fileEntries;
  
  public ZipFolder(Path file) throws IOException {
    zip = new ZipFile(file.toFile());
    
    List<ZipEntry> entries = getEntries();
    
    dirPaths    = createDirectoryPaths(entries);
    fileEntries = createFileEntries   (entries);
  }
  
  private List<ZipEntry> getEntries() {
    List<ZipEntry> entries = new ArrayList<>(zip.size());
    
    for (Enumeration<? extends ZipEntry> en = zip.entries();
        en.hasMoreElements();) {
      entries.add(en.nextElement());
    }
    
    return entries;
  }
  
  private Set<String> createDirectoryPaths(List<ZipEntry> entries) {
    Set<String> dirPaths = new HashSet<>();
    
    for (ZipEntry entry: entries) {
      String name = entry.getName();
      
      for (int sepIdx; (sepIdx = name.lastIndexOf('/')) != -1;) {
        name = name.substring(0, sepIdx);
        
        dirPaths.add(name);
      }
    }
    
    return dirPaths;
  }
  
  private Map<String, ZipEntry> createFileEntries(List<ZipEntry> entries) {
    Map<String, ZipEntry> fileEntries = new HashMap<>();
    
    for (ZipEntry entry: entries) {
      if (!entry.isDirectory()) {
        fileEntries.put(entry.getName(), entry);
      }
    }
    
    return fileEntries;
  }
  
  @Override
  void collectPaths(Set<String> paths, String parentPath, boolean directory) {
    String prefix = parentPath.isEmpty() ? "" : parentPath.concat("/");
    
    for (String path: (directory ? dirPaths : fileEntries.keySet())) {
      if (path.startsWith(prefix)) {
        path = path.substring(prefix.length());
        
        if (path.indexOf('/') == -1) {
          paths.add(path);
        }
      }
    }
  }
  
  @Override
  public boolean exists(String path, boolean directory) {
    if (directory) {
      return dirPaths   .contains   (path);
    } else {
      return fileEntries.containsKey(path);
    }
  }
  
  @Override
  Source getRawSource(String path) {
    ZipEntry entry = fileEntries.get(path);
    
    if (entry != null) {
      return new ZipSource(zip, entry);
    } else {
      return null;
    }
  }
  
  @Override
  public void close() {
    try {
      zip.close();
    } catch (IOException ex) {
    }
  }
}
