/*
 * ScoreEntry.java
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

public final class ScoreEntry {
  private final Score  score;
  private final String player;
  
  public ScoreEntry(Score score, String player) {
    this.score  = score;
    this.player = player;
  }
  
  public Score getScore() {
    return score;
  }
  
  public String getPlayer() {
    return player;
  }
}
