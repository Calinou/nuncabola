/*
 * LevelSetReadTool.java
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

public final class LevelSetReadTool {
  private static final ScoreType[] SCORE_TYPES = {
    ScoreType.BEST_TIME,
    ScoreType.MOST_COINS
  };
  
  private static void readDefaultScoreTables(LevelSet set, BufferedReader in)
      throws IOException {
    String   str     = ReaderTool.readLine(in);
    String[] parts   = str.trim().split("\\s+");
    int      partIdx = 0;
    
    for (ScoreType type: SCORE_TYPES) {
      for (int rank = 0; rank < ScoreTable.SIZE; rank++) {
        int time  = LevelSet.MAX_TIME;
        int coins = 0;
        
        if (partIdx < parts.length) {
          try {
            int value = Integer.parseInt(parts[partIdx]);
            
            if (type.isTimeBased()) {
              time  = Math.min(Math.max(value, 0), time);
            } else {
              coins = Math.max(value, coins);
            }
          } catch (NumberFormatException ex) {
          }
        }
        
        ScoreEntry entry = new ScoreEntry(
          new Score(time, coins),
          ScoreTable.getDefaultPlayer(rank));
        
        set.getDefaultScoreTables().getTable(type).setEntry(rank, entry);
        
        partIdx++;
      }
    }
  }
  
  private static void readLevelPaths(LevelSet set, BufferedReader in)
      throws IOException {
    List<String> paths = new ArrayList<>(LevelSet.MAX_LEVEL_COUNT);
    
    for (String str;
        (paths.size() < LevelSet.MAX_LEVEL_COUNT)
          && ((str = in.readLine()) != null);) {
      if (!str.isEmpty()) {
        paths.add(str.trim());
      }
    }
    
    set.setLevelPaths(paths);
  }
  
  private static LevelSet readLevelSet(BufferedReader in) throws IOException {
    LevelSet set = new LevelSet();
    
    set.setName       (ReaderTool.readLine(in));
    set.setDescription(ReaderTool.readLine(in).replace('\\', '\n'));
    set.setID         (ReaderTool.readLine(in).trim());
    set.setShotPath   (ReaderTool.readLine(in).trim());
    
    readDefaultScoreTables(set, in);
    readLevelPaths        (set, in);
    
    return set;
  }
  
  public static LevelSet readLevelSet(InputStream inStream) throws IOException {
    BufferedReader in = new BufferedReader(
      new InputStreamReader(inStream, StandardCharsets.UTF_8));
    
    return readLevelSet(in);
  }
  
  public static LevelSet readLevelSet(Path file) throws IOException {
    try (InputStream in = Files.newInputStream(file)) {
      return readLevelSet(in);
    }
  }
  
  public static LevelSet readLevelSet(Source src) throws IOException {
    try (InputStream in = src.newInputStream()) {
      return readLevelSet(in);
    }
  }
  
  private LevelSetReadTool() {
  }
}
