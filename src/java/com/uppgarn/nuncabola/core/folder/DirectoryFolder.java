/*
 * DirectoryFolder.java
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

import java.io.*;
import java.nio.file.*;
import java.util.*;

final class DirectoryFolder extends BranchFolder {
  private final Path dir;
  
  private List<Folder> folders;
  
  public DirectoryFolder(Path dir) {
    this.dir = dir;
    
    folders = createFolders();
  }
  
  private FileFolder createFileFolder() {
    return new FileFolder(dir);
  }
  
  private List<Path> getZipFiles() {
    List<Path> files = new ArrayList<>();
    
    DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
      @Override
      public boolean accept(Path entry) throws IOException {
        if (!Files.isRegularFile(entry)) {
          return false;
        }
        
        String name = entry.getFileName().toString();
        
        return name.endsWith(".zip") || name.endsWith(".pk3");
      }
    };
    
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
      for (Path entry: stream) {
        files.add(entry);
      }
    } catch (IOException ex) {
    }
    
    files.sort(null);
    
    return files;
  }
  
  private List<ZipFolder> createZipFolders() {
    List<Path> files = getZipFiles();
    
    List<ZipFolder> folders = new ArrayList<>(files.size());
    
    // Use reverse order so that when iterating over the folders later,
    // those with alphabetically "higher" file names take precedence.
    
    for (ListIterator<Path> it = files.listIterator(files.size());
        it.hasPrevious();) {
      try {
        folders.add(new ZipFolder(it.previous()));
      } catch (IOException ex) {
      }
    }
    
    return folders;
  }
  
  private List<Folder> createFolders() {
    FileFolder      fileFolder = createFileFolder();
    List<ZipFolder> zipFolders = createZipFolders();
    
    List<Folder> folders = new ArrayList<>(zipFolders.size() + 1);
    
    folders.add   (fileFolder);
    folders.addAll(zipFolders);
    
    return folders;
  }
  
  @Override
  List<? extends Folder> getChildren() {
    return folders;
  }
}
