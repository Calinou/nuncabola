/*
 * LevelSetScoreReadTool.java
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

import com.uppgarn.nuncabola.core.util.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class LevelSetScoreReadTool {
  private static final ScoreType[] LEVEL_SET_SCORE_TYPES = {
    ScoreType.BEST_TIME,
    ScoreType.MOST_COINS
  };
  private static final ScoreType[] LEVEL_SCORE_TYPES     = {
    ScoreType.BEST_TIME,
    ScoreType.FAST_UNLOCK,
    ScoreType.MOST_COINS
  };
  
  private static LevelStatus getLevelStatusV1(char value) {
    switch (value) {
      case 'L': {
        return LevelStatus.LOCKED;
      }
      case 'O': {
        return LevelStatus.OPEN;
      }
      case 'C': {
        return LevelStatus.COMPLETED;
      }
      
      default: {
        return null;
      }
    }
  }
  
  private static LevelStatus getLevelStatusV2(int value) {
    if ((value & 1) != 0) {
      return LevelStatus.LOCKED;
    } else if ((value & 2) != 0) {
      return LevelStatus.COMPLETED;
    } else {
      return LevelStatus.OPEN;
    }
  }
  
  private static ScoreEntry readScoreEntry(BufferedReader in, int maxTime)
      throws IOException {
    String str = in.readLine();
    
    if (str == null) {
      throw new InvalidDataException();
    }
    
    String[] parts = str.trim().split("\\s+", 3);
    
    if (parts.length != 3) {
      throw new InvalidDataException();
    }
    
    try {
      int parsedTime  = Integer.parseInt(parts[0]);
      int parsedCoins = Integer.parseInt(parts[1]);
      
      int    time   = Math.min(Math.max(parsedTime, 0), maxTime);
      int    coins  = Math.max(parsedCoins, 0);
      String player = parts[2];
      
      return new ScoreEntry(new Score(time, coins), player);
    } catch (NumberFormatException ex) {
      throw new InvalidDataException();
    }
  }
  
  private static void readScoreTable(
      ScoreTable     table,
      BufferedReader in,
      int            maxTime) throws IOException {
    for (int rank = 0; rank < ScoreTable.SIZE; rank++) {
      table.setEntry(rank, readScoreEntry(in, maxTime));
    }
  }
  
  private static void readScoreTables(
      ScoreTables    tables,
      BufferedReader in,
      int            maxTime,
      ScoreType[]    types) throws IOException {
    for (ScoreType type: types) {
      readScoreTable(tables.getTable(type), in, maxTime);
    }
  }
  
  private static void readLevelStatusesBlockV1(
      LevelSetScore setScore,
      String        str,
      LevelSet      set) throws IOException {
    int levelCount = Math.min(str.length(), set.getLevelCount());
    
    for (int idx = 0; idx < levelCount; idx++) {
      LevelStatus status = getLevelStatusV1(str.charAt(idx));
      
      if (status == null) {
        throw new InvalidDataException();
      }
      
      setScore.getLevelScore(idx).setStatus(status);
    }
  }
  
  private static void readLevelSetBlockV1(
      LevelSetScore  setScore,
      BufferedReader in) throws IOException {
    readScoreTables(
      setScore.getTables(),
      in,
      LevelSet.MAX_TIME,
      LEVEL_SET_SCORE_TYPES);
  }
  
  private static void readLevelBlockV1(LevelScore levelScore, BufferedReader in)
      throws IOException {
    readScoreTables(
      levelScore.getTables(),
      in,
      Level.MAX_TIME,
      LEVEL_SCORE_TYPES);
  }
  
  private static void readLevelSetScoreV1(
      LevelSetScore  setScore,
      String         str,
      BufferedReader in,
      LevelSet       set) throws IOException {
    try {
      readLevelStatusesBlockV1(setScore, str, set);
      readLevelSetBlockV1     (setScore, in);
      
      for (LevelScore levelScore: setScore.getLevelScores()) {
        readLevelBlockV1(levelScore, in);
      }
    } catch (InvalidDataException ex) {
      // The set score maintains a valid configuration at all times.
      // For this reason, invalid or missing data may be ignored.
    }
  }
  
  private static void readLevelSetBlockV2(
      ScoreTables    setScoreTables,
      BufferedReader in) throws IOException {
    readScoreTables(
      setScoreTables,
      in,
      LevelSet.MAX_TIME,
      LEVEL_SET_SCORE_TYPES);
  }
  
  private static boolean readLevelBlockV2(
      LevelSetScore  setScore,
      String         str,
      BufferedReader in,
      LevelSet       set,
      List<Level>    levels) throws IOException {
    String[] parts = str.substring(6).trim().split("\\s+", 3);
    
    if (parts.length != 3) {
      throw new InvalidDataException();
    }
    
    try {
      int parsedStatusInt = Integer.parseInt(parts[0]);
      int parsedVersion   = Integer.parseInt(parts[1]);
      
      String      path    = parts[2];
      int         version = Math.max(parsedVersion, 0);
      LevelStatus status  = getLevelStatusV2(parsedStatusInt);
      
      int idx = set.getLevelPaths().indexOf(path);
      
      if (idx == -1) {
        return false;
      }
      
      Level      level      = (levels == null) ? null : levels.get(idx);
      LevelScore levelScore = setScore.getLevelScore(idx);
      
      if ((level != null) && (version != level.getMajorVersion())) {
        if (status.isAccessible()) {
          levelScore.setStatus(LevelStatus.OPEN);
        }
        
        return false;
      }
      
      levelScore.setVersion(version);
      
      if ((idx == 0) && !status.isAccessible()) {
        levelScore.setStatus(LevelStatus.OPEN);
      } else {
        levelScore.setStatus(status);
      }
      
      readScoreTables(
        levelScore.getTables(),
        in,
        Level.MAX_TIME,
        LEVEL_SCORE_TYPES);
      
      return true;
    } catch (NumberFormatException ex) {
      throw new InvalidDataException();
    }
  }
  
  private static void readLevelSetScoreV2(
      LevelSetScore  setScore,
      BufferedReader in,
      LevelSet       set,
      List<Level>    levels) throws IOException {
    ScoreTables setScoreTables      = null;
    boolean     setScoreTablesValid = true;
    
    for (String str; (str = in.readLine()) != null;) {
      try {
        if (str.startsWith("set ")) {
          // Level set block.
          
          setScoreTables = new ScoreTables(setScore.getTables());
          
          readLevelSetBlockV2(setScoreTables, in);
        } else if (str.startsWith("level ")) {
          // Level block.
          
          if (!readLevelBlockV2(setScore, str, in, set, levels)) {
            setScoreTablesValid = false;
          }
        }
      } catch (InvalidDataException ex) {
        // The set score maintains a valid configuration at all times.
        // For this reason, invalid or missing data may be ignored.
      }
    }
    
    if ((setScoreTables != null) && setScoreTablesValid) {
      setScore.getTables().copyFrom(setScoreTables);
    }
  }
  
  private static LevelSetScore readLevelSetScore(
      BufferedReader in,
      LevelSet       set,
      List<Level>    levels) throws IOException {
    LevelSetScore setScore = new LevelSetScore(set, levels);
    
    String str = in.readLine();
    
    if (str != null) {
      if (str.startsWith("version ")) {
        try {
          int version = Integer.parseInt(str.substring(8).trim());
          
          switch (version) {
            case 2: {
              readLevelSetScoreV2(setScore, in, set, levels);
              
              break;
            }
          }
        } catch (NumberFormatException ex) {
        }
      } else {
        readLevelSetScoreV1(setScore, str, in, set);
      }
    }
    
    return setScore;
  }
  
  public static LevelSetScore readLevelSetScore(
      InputStream inStream,
      LevelSet    set,
      List<Level> levels) throws IOException {
    BufferedReader in = new BufferedReader(
      new InputStreamReader(inStream, StandardCharsets.UTF_8));
    
    return readLevelSetScore(in, set, levels);
  }
  
  public static LevelSetScore readLevelSetScore(
      Path        file,
      LevelSet    set,
      List<Level> levels) throws IOException {
    try (InputStream in = Files.newInputStream(file)) {
      return readLevelSetScore(in, set, levels);
    }
  }
  
  private LevelSetScoreReadTool() {
  }
}
