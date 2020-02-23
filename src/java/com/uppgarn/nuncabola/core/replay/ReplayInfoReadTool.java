/*
 * ReplayInfoReadTool.java
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
import com.uppgarn.nuncabola.core.util.*;

import static com.uppgarn.nuncabola.core.binary.BinaryReadTool.*;
import static com.uppgarn.nuncabola.core.replay.ReplayInfoIOTool.*;

import com.uppgarn.codelibf.io.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;

public final class ReplayInfoReadTool {
  private static SeriesMode getSeriesMode(int value) {
    if (value == 1) {
      return SeriesMode.CHALLENGE;
    } else {
      return SeriesMode.NORMAL;
    }
  }
  
  private static Status getStatus(int value) {
    switch (value) {
      case 0: {
        return Status.NONE;
      }
      case 1: {
        return Status.TIME_OUT;
      }
      case 2: {
        return Status.GOAL;
      }
      case 3: {
        return Status.FALL_OUT;
      }
      
      default: {
        return Status.NONE;
      }
    }
  }
  
  public static ReplayInfo readReplayInfo(InputStream in) throws IOException {
    int magic   = readInt(in);
    int version = readInt(in);
    
    if ((magic != MAGIC) || (version != VERSION)) {
      throw new InvalidDataException();
    }
    
    ReplayInfo info = new ReplayInfo();
    
    info.setTime  (readInt(in));
    info.setCoins (readInt(in));
    info.setStatus(getStatus(readInt(in)));
    
    info.setMode(getSeriesMode(readInt(in)));
    
    info.setPlayer(readString(in));
    
    try {
      info.setDate(Instant.from(DATE_FORMATTER.parse(readString(in))));
    } catch (DateTimeException ex) {
    }
    
    info.setShotPath (readString(in));
    info.setLevelPath(readString(in));
    
    info.setLevelTime(readInt(in));
    info.setLevelGoal(readInt(in));
    
    readInt(in);
    
    info.setTotalCoins(readInt(in));
    info.setBalls     (readInt(in));
    info.setTotalTime (readInt(in));
    
    return info;
  }
  
  public static ReplayInfo readReplayInfo(Path file) throws IOException {
    try (InputStream in = new BufferedInputStream(
        Files.newInputStream(file),
        192)) {
      return readReplayInfo(in);
    }
  }
  
  public static ReplayInfo readReplayInfo(Source src) throws IOException {
    try (InputStream in = new BufferedInputStream(src.newInputStream(), 192)) {
      return readReplayInfo(in);
    }
  }
  
  private ReplayInfoReadTool() {
  }
}
