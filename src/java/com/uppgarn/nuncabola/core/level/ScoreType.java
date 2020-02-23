/*
 * ScoreType.java
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

public enum ScoreType {
  MOST_COINS (false, false),
  BEST_TIME  (false, true),
  FAST_UNLOCK(true,  true);
  
  private final boolean goalUnlockingRequired;
  private final boolean timeBased;
  
  ScoreType(boolean goalUnlockingRequired, boolean timeBased) {
    this.goalUnlockingRequired = goalUnlockingRequired;
    this.timeBased             = timeBased;
  }
  
  public boolean isGoalUnlockingRequired() {
    return goalUnlockingRequired;
  }
  
  public boolean isTimeBased() {
    return timeBased;
  }
  
  public boolean accepts(Score score, int goal) {
    return !goalUnlockingRequired || (score.getCoins() >= goal);
  }
  
  public boolean beats(Score newScore, Score oldScore) {
    if (timeBased) {
      return newScore.beatsTime (oldScore);
    } else {
      return newScore.beatsCoins(oldScore);
    }
  }
}
