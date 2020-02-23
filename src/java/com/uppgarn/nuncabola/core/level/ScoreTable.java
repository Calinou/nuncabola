/*
 * ScoreTable.java
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

public final class ScoreTable {
  public static final int SIZE = 3;
  
  public static String getDefaultPlayer(int rank) {
    switch (rank) {
      case 0: {
        return "Hard";
      }
      case 1: {
        return "Medium";
      }
      case 2: {
        return "Easy";
      }
      
      default: {
        throw new AssertionError();
      }
    }
  }
  
  private ScoreEntry[] entries;
  
  public ScoreTable(int defaultTime) {
    entries = createEntries(defaultTime);
  }
  
  public ScoreTable(ScoreTable src) {
    entries = src.entries.clone();
  }
  
  private ScoreEntry[] createEntries(int defaultTime) {
    ScoreEntry[] entries = new ScoreEntry[SIZE];
    
    Score score = new Score(defaultTime, 0);
    
    for (int idx = 0; idx < SIZE; idx++) {
      entries[idx] = new ScoreEntry(score, getDefaultPlayer(idx));
    }
    
    return entries;
  }
  
  public ScoreEntry getEntry(int rank) {
    return entries[rank];
  }
  
  public void setEntry(int rank, ScoreEntry entry) {
    entries[rank] = entry;
  }
  
  public ScoreResult enter(
      Score     score,
      String    player,
      int       goal,
      ScoreType type) {
    ScoreEntry entry = new ScoreEntry(score, player);
    
    int        rank         = -1;
    ScoreEntry removedEntry = null;
    
    if (type.accepts(score, goal)) {
      for (int idx = 0; idx < SIZE; idx++) {
        if (type.beats(score, entries[idx].getScore())) {
          rank         = idx;
          removedEntry = entries[SIZE - 1];
          
          System.arraycopy(entries, idx, entries, idx + 1, SIZE - idx - 1);
          
          entries[idx] = entry;
          
          break;
        }
      }
    }
    
    return new ScoreResult(this, rank, removedEntry, entry);
  }
  
  public void copyFrom(ScoreTable src) {
    System.arraycopy(src.entries, 0, entries, 0, SIZE);
  }
}
