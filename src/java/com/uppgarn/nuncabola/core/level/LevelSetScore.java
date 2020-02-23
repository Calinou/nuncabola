/*
 * LevelSetScore.java
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

import java.util.*;

public final class LevelSetScore {
  private ScoreTables tables;
  
  private List<LevelScore> levelScores;
  private List<LevelScore> levelScoresR;
  
  public LevelSetScore(LevelSet set, List<Level> levels) {
    tables = new ScoreTables(set.getDefaultScoreTables());
    
    levelScores  = createLevelScores(set, levels);
    levelScoresR = Collections.unmodifiableList(levelScores);
  }
  
  private List<LevelScore> createLevelScores(LevelSet set, List<Level> levels) {
    int levelCount = set.getLevelCount();
    
    List<LevelScore> levelScores = new ArrayList<>(levelCount);
    
    for (int idx = 0; idx < levelCount; idx++) {
      Level level = (levels == null) ? null : levels.get(idx);
      
      levelScores.add(new LevelScore(level, idx == 0));
    }
    
    return levelScores;
  }
  
  public ScoreTables getTables() {
    return tables;
  }
  
  public LevelScore getLevelScore(int idx) {
    return levelScores.get(idx);
  }
  
  public List<LevelScore> getLevelScores() {
    return levelScoresR;
  }
}
