/*
 * LevelSet.java
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

public final class LevelSet {
  public static final int MAX_TIME        = 359999;
  public static final int MAX_LEVEL_COUNT = 25;
  
  public static final List<ScoreType> SCORE_TYPES =
    Collections.unmodifiableList(
      Arrays.asList(ScoreType.MOST_COINS, ScoreType.BEST_TIME));
  
  private String id;
  private String name;
  private String desc;
  private String shotPath;
  
  private ScoreTables defaultScoreTables;
  
  private List<String> levelPaths;
  
  public LevelSet() {
    id       = "";
    name     = "";
    desc     = "";
    shotPath = "";
    
    defaultScoreTables = new ScoreTables(SCORE_TYPES, MAX_TIME);
    
    levelPaths = Collections.emptyList();
  }
  
  public String getID() {
    return id;
  }
  
  public void setID(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return desc;
  }
  
  public void setDescription(String desc) {
    this.desc = desc;
  }
  
  public String getShotPath() {
    return shotPath;
  }
  
  public void setShotPath(String path) {
    shotPath = path;
  }
  
  public ScoreTables getDefaultScoreTables() {
    return defaultScoreTables;
  }
  
  public int getLevelCount() {
    return levelPaths.size();
  }
  
  public String getLevelPath(int idx) {
    return levelPaths.get(idx);
  }
  
  public List<String> getLevelPaths() {
    return levelPaths;
  }
  
  public void setLevelPaths(List<String> paths) {
    levelPaths = Collections.unmodifiableList(new ArrayList<>(paths));
  }
}
