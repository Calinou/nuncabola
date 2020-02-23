/*
 * ScoreResult.java
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

public final class ScoreResult {
  private final ScoreTable table;
  private final int        rank;
  private final ScoreEntry removedEntry;
  
  private ScoreEntry entry;
  
  public ScoreResult(
      ScoreTable table,
      int        rank,
      ScoreEntry removedEntry,
      ScoreEntry entry) {
    this.table        = table;
    this.rank         = rank;
    this.removedEntry = removedEntry;
    
    this.entry = entry;
  }
  
  public int getRank() {
    return rank;
  }
  
  public boolean isRecord() {
    return rank != -1;
  }
  
  public ScoreEntry getRemovedEntry() {
    return removedEntry;
  }
  
  public ScoreEntry getEntry() {
    return entry;
  }
  
  public void setPlayer(String player) {
    entry = new ScoreEntry(entry.getScore(), player);
    
    if (isRecord()) {
      table.setEntry(rank, entry);
    }
  }
}
