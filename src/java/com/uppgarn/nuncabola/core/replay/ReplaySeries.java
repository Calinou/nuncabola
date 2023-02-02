/*
 * ReplaySeries.java
 *
 * Copyright (c) 2003-2022 Nuncabola authors
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

import com.uppgarn.nuncabola.core.series.*;

public final class ReplaySeries extends Series {
  private final ReplayInfo info;
  
  public ReplaySeries(ReplayInfo info) {
    super(
      info.getMode(),
      info.getPlayer(),
      info.getBalls(),
      info.getTotalTime(),
      info.getTotalCoins());
    
    this.info = info;
    
    playLevel();
  }
  
  private void playLevel() {
    play(false, info.getDate(), info.getLevelPath());
  }
  
  public void repeat() {
    playLevel();
  }
}
