/*
 * Score.java
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

public final class Score {
  private final int time;
  private final int coins;
  
  public Score(int time, int coins) {
    this.time  = time;
    this.coins = coins;
  }
  
  public int getTime() {
    return time;
  }
  
  public int getCoins() {
    return coins;
  }
  
  public boolean beatsTime(Score score) {
    if (time == score.time) {
      return coins > score.coins;
    } else {
      return time  < score.time;
    }
  }
  
  public boolean beatsCoins(Score score) {
    if (coins == score.coins) {
      return time  < score.time;
    } else {
      return coins > score.coins;
    }
  }
}
