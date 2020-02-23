/*
 * ReplayInfoWriteTool.java
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

import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.series.*;

import static com.uppgarn.nuncabola.core.binary.BinaryWriteTool.*;
import static com.uppgarn.nuncabola.core.replay.ReplayInfoIOTool.*;

import java.io.*;

public final class ReplayInfoWriteTool {
  private static int getSeriesModeInt(SeriesMode mode) {
    switch (mode) {
      case NORMAL: {
        return 2;
      }
      case CHALLENGE: {
        return 1;
      }
      
      default: {
        throw new AssertionError();
      }
    }
  }
  
  private static int getStatusInt(Status status) {
    switch (status) {
      case NONE: {
        return 0;
      }
      case GOAL: {
        return 2;
      }
      case FALL_OUT: {
        return 3;
      }
      case TIME_OUT: {
        return 1;
      }
      
      default: {
        throw new AssertionError();
      }
    }
  }
  
  public static void writeReplayInfo(OutputStream out, ReplayInfo info)
      throws IOException {
    writeInt(out, MAGIC);
    writeInt(out, VERSION);
    
    writeInt(out, info.getTime());
    writeInt(out, info.getCoins());
    writeInt(out, getStatusInt(info.getStatus()));
    
    writeInt(out, getSeriesModeInt(info.getMode()));
    
    writeString(out, info.getPlayer());
    
    writeString(out, DATE_FORMATTER.format(info.getDate()));
    
    writeString(out, info.getShotPath());
    writeString(out, info.getLevelPath());
    
    writeInt(out, info.getLevelTime());
    writeInt(out, info.getLevelGoal());
    
    writeInt(out, 0);
    
    writeInt(out, info.getTotalCoins());
    writeInt(out, info.getBalls());
    writeInt(out, info.getTotalTime());
  }
  
  private ReplayInfoWriteTool() {
  }
}
