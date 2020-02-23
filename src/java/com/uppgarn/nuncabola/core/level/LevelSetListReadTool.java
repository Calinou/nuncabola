/*
 * LevelSetListReadTool.java
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

package com.uppgarn.nuncabola.core.level;

import com.uppgarn.codelibf.io.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class LevelSetListReadTool {
  private static void readLevelSetPaths(LevelSetList list, BufferedReader in)
      throws IOException {
    List<String> paths = new ArrayList<>();
    
    for (String str; (str = in.readLine()) != null;) {
      if (!str.isEmpty()) {
        paths.add(str.trim());
      }
    }
    
    list.setSetPaths(paths);
  }
  
  private static LevelSetList readLevelSetList(BufferedReader in)
      throws IOException {
    LevelSetList list = new LevelSetList();
    
    readLevelSetPaths(list, in);
    
    return list;
  }
  
  public static LevelSetList readLevelSetList(InputStream inStream)
      throws IOException {
    BufferedReader in = new BufferedReader(
      new InputStreamReader(inStream, StandardCharsets.UTF_8));
    
    return readLevelSetList(in);
  }
  
  public static LevelSetList readLevelSetList(Path file) throws IOException {
    try (InputStream in = Files.newInputStream(file)) {
      return readLevelSetList(in);
    }
  }
  
  public static LevelSetList readLevelSetList(Source src) throws IOException {
    try (InputStream in = src.newInputStream()) {
      return readLevelSetList(in);
    }
  }
  
  private LevelSetListReadTool() {
  }
}
