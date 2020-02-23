/*
 * LevelSetScoreWriteTool.java
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

import com.uppgarn.codelibf.util.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

public final class LevelSetScoreWriteTool {
  private static final ScoreType[] LEVEL_SET_SCORE_TYPES = {
    ScoreType.BEST_TIME,
    ScoreType.MOST_COINS
  };
  private static final ScoreType[] LEVEL_SCORE_TYPES     = {
    ScoreType.BEST_TIME,
    ScoreType.FAST_UNLOCK,
    ScoreType.MOST_COINS
  };
  
  private static int getLevelStatusInt(LevelStatus status) {
    switch (status) {
      case LOCKED: {
        return 1;
      }
      case OPEN: {
        return 0;
      }
      case COMPLETED: {
        return 2;
      }
      
      default: {
        throw new AssertionError();
      }
    }
  }
  
  private static void writeScoreEntry(Writer out, ScoreEntry entry)
      throws IOException {
    out.write(Integer.toString(entry.getScore().getTime()));
    out.write(' ');
    out.write(Integer.toString(entry.getScore().getCoins()));
    out.write(' ');
    out.write(entry.getPlayer());
    out.write(StringTool.LS);
  }
  
  private static void writeScoreTable(Writer out, ScoreTable table)
      throws IOException {
    for (int rank = 0; rank < ScoreTable.SIZE; rank++) {
      writeScoreEntry(out, table.getEntry(rank));
    }
  }
  
  private static void writeScoreTables(
      Writer      out,
      ScoreTables tables,
      ScoreType[] types) throws IOException {
    for (ScoreType type: types) {
      writeScoreTable(out, tables.getTable(type));
    }
  }
  
  private static void writeLevelSetBlock(
      Writer        out,
      LevelSet      set,
      LevelSetScore setScore) throws IOException {
    out.write("set ");
    out.write(set.getID());
    out.write(StringTool.LS);
    
    writeScoreTables(out, setScore.getTables(), LEVEL_SET_SCORE_TYPES);
  }
  
  private static void writeLevelBlock(
      Writer     out,
      String     levelPath,
      LevelScore levelScore) throws IOException {
    out.write("level ");
    out.write(Integer.toString(getLevelStatusInt(levelScore.getStatus())));
    out.write(' ');
    out.write(Integer.toString(levelScore.getVersion()));
    out.write(' ');
    out.write(levelPath);
    out.write(StringTool.LS);
    
    writeScoreTables(out, levelScore.getTables(), LEVEL_SCORE_TYPES);
  }
  
  private static void writeLevelSetScore(
      Writer        out,
      LevelSetScore setScore,
      LevelSet      set) throws IOException {
    out.write("version 2");
    out.write(StringTool.LS);
    
    writeLevelSetBlock(out, set, setScore);
    
    for (int idx = 0; idx < set.getLevelCount(); idx++) {
      writeLevelBlock(out, set.getLevelPath(idx), setScore.getLevelScore(idx));
    }
  }
  
  public static void writeLevelSetScore(
      OutputStream  outStream,
      LevelSetScore setScore,
      LevelSet      set) throws IOException {
    Writer out = new BufferedWriter(
      new OutputStreamWriter(outStream, StandardCharsets.UTF_8));
    
    writeLevelSetScore(out, setScore, set);
    
    out.flush();
  }
  
  public static void writeLevelSetScore(
      Path          file,
      LevelSetScore setScore,
      LevelSet      set) throws IOException {
    try (OutputStream out = Files.newOutputStream(file)) {
      writeLevelSetScore(out, setScore, set);
    }
  }
  
  private LevelSetScoreWriteTool() {
  }
}
