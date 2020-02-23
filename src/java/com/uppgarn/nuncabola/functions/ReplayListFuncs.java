/*
 * ReplayListFuncs.java
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

import com.uppgarn.nuncabola.core.replay.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import com.uppgarn.codelibf.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ReplayListFuncs {
  private static final Random RANDOM = new Random();
  
  private static List<Path> files;
  
  private static ReplayInfo[] infos;
  private static boolean   [] checked;
  
  public static void initialize() {
    files = createFiles();
    
    infos   = new ReplayInfo[files.size()];
    checked = new boolean   [files.size()];
  }
  
  private static List<Path> createFiles() {
    List<Path> files = new ArrayList<>();
    
    DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
      @Override
      public boolean accept(Path entry) throws IOException {
        return Files.isRegularFile(entry)
          && entry.getFileName().toString().endsWith(ReplayFileFuncs.EXTENSION);
      }
    };
    
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(
        getReplayDirectory(),
        filter)) {
      for (Path entry: stream) {
        files.add(entry);
      }
    } catch (IOException ex) {
    }
    
    files.sort(null);
    
    return files;
  }
  
  public static int getReplayCount() {
    return files.size();
  }
  
  public static Path getFile(int idx) {
    return files.get(idx);
  }
  
  public static String getName(int idx) {
    return files.get(idx).getFileName().toString();
  }
  
  public static String getShortName(int idx) {
    return StringTool.removeSuffix(getName(idx), ReplayFileFuncs.EXTENSION);
  }
  
  public static int getIndex(int startIdx, String pattern) {
    for (int idx = 0; idx < files.size(); idx++) {
      int myIdx = (startIdx + idx) % files.size();
      
      if (getName(myIdx).regionMatches(true, 0, pattern, 0, pattern.length())) {
        return myIdx;
      }
    }
    
    return -1;
  }
  
  public static ReplayInfo getInfo(int idx) {
    if (!checked[idx]) {
      try {
        infos[idx] = ReplayInfoReadTool.readReplayInfo(files.get(idx));
      } catch (IOException ex) {
        infos[idx] = null;
      }
      
      checked[idx] = true;
    }
    
    return infos[idx];
  }
  
  public static boolean isValidReplay(int idx) {
    return getInfo(idx) != null;
  }
  
  public static Path getRandomFile() {
    int[] indices = new int[files.size()];
    
    for (int idx = 0; idx < indices.length; idx++) {
      indices[idx] = idx;
    }
    for (int len = indices.length; len > 0; len--) {
      int idx       = RANDOM.nextInt(len);
      int replayIdx = indices[idx];
      
      if (isValidReplay(replayIdx)) {
        return files.get(replayIdx);
      }
      
      indices[idx] = indices[len - 1];
    }
    
    return null;
  }
  
  public static void deinitialize() {
    files   = null;
    infos   = null;
    checked = null;
  }
  
  private ReplayListFuncs() {
  }
}
