/*
 * ReplaySeries.java
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

package com.uppgarn.nuncabola.core.replay;

import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.series.*;

public final class ReplaySeries extends Series {
  private final ReplayInfo info;
  private final Level      level;
  
  public ReplaySeries(ReplayInfo info, Level level) {
    super(
      info.getMode(),
      info.getPlayer(),
      info.getBalls(),
      info.getTotalTime(),
      info.getTotalCoins());
    
    this.info  = info;
    this.level = level;
    
    playLevel();
  }
  
  private void playLevel() {
    play(
      false,
      info.getDate(),
      level,
      info.getLevelTime(),
      info.getLevelGoal());
  }
  
  public void repeat() {
    playLevel();
  }
}
