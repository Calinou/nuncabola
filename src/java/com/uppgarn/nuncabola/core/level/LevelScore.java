/*
 * LevelScore.java
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

public final class LevelScore {
  private int version;
  
  private LevelStatus status;
  private ScoreTables tables;
  
  public LevelScore(Level level, boolean first) {
    if (level == null) {
      version = 0;
      tables  = new ScoreTables(Level.SCORE_TYPES, Level.MAX_TIME);
    } else {
      version = level.getMajorVersion();
      tables  = new ScoreTables(level.getDefaultScoreTables());
    }
    
    status = first ? LevelStatus.OPEN : LevelStatus.LOCKED;
  }
  
  public int getVersion() {
    return version;
  }
  
  public void setVersion(int version) {
    this.version = version;
  }
  
  public LevelStatus getStatus() {
    return status;
  }
  
  public void setStatus(LevelStatus status) {
    this.status = status;
  }
  
  public ScoreTables getTables() {
    return tables;
  }
}
