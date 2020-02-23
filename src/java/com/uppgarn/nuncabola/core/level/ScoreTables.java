/*
 * ScoreTables.java
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

public final class ScoreTables {
  private Map<ScoreType, ScoreTable> tables;
  
  public ScoreTables(Collection<ScoreType> types, int defaultTime) {
    tables = createTables(types, defaultTime);
  }
  
  public ScoreTables(ScoreTables src) {
    tables = createTables(src);
  }
  
  private Map<ScoreType, ScoreTable> createTables(
      Collection<ScoreType> types,
      int                   defaultTime) {
    Map<ScoreType, ScoreTable> tables = new EnumMap<>(ScoreType.class);
    
    for (ScoreType type: types) {
      tables.put(type, new ScoreTable(defaultTime));
    }
    
    return tables;
  }
  
  private Map<ScoreType, ScoreTable> createTables(ScoreTables src) {
    Map<ScoreType, ScoreTable> tables = new EnumMap<>(ScoreType.class);
    
    for (Map.Entry<ScoreType, ScoreTable> entry: src.tables.entrySet()) {
      ScoreType  type  = entry.getKey();
      ScoreTable table = entry.getValue();
      
      tables.put(type, new ScoreTable(table));
    }
    
    return tables;
  }
  
  public ScoreTable getTable(ScoreType type) {
    return tables.get(type);
  }
  
  public ScoreResults enter(Score score, String player, int goal) {
    Map<ScoreType, ScoreResult> results = new EnumMap<>(ScoreType.class);
    
    for (Map.Entry<ScoreType, ScoreTable> entry: tables.entrySet()) {
      ScoreType  type  = entry.getKey();
      ScoreTable table = entry.getValue();
      
      results.put(type, table.enter(score, player, goal, type));
    }
    
    return new ScoreResults(results);
  }
  
  public void copyFrom(ScoreTables src) {
    for (Map.Entry<ScoreType, ScoreTable> entry: src.tables.entrySet()) {
      ScoreType  srcType  = entry.getKey();
      ScoreTable srcTable = entry.getValue();
      ScoreTable table    = tables.get(srcType);
      
      if (table == null) {
        tables.put(srcType, new ScoreTable(srcTable));
      } else {
        table.copyFrom(srcTable);
      }
    }
    
    tables.keySet().retainAll(src.tables.keySet());
  }
}
