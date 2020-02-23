/*
 * ScoreResults.java
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

public final class ScoreResults {
  private final Map<ScoreType, ScoreResult> results;
  
  public ScoreResults(Map<ScoreType, ScoreResult> results) {
    this.results = results.isEmpty()
                   ? new EnumMap<>(ScoreType.class) : new EnumMap<>(results);
  }
  
  public ScoreResult getResult(ScoreType type) {
    return results.get(type);
  }
  
  public boolean isRecord() {
    for (ScoreResult result: results.values()) {
      if (result.isRecord()) {
        return true;
      }
    }
    
    return false;
  }
  
  public void setPlayer(String player) {
    for (ScoreResult result: results.values()) {
      result.setPlayer(player);
    }
  }
}
