/*
 * LevelOverride.java
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

package com.uppgarn.nuncabola.core.level;

public final class LevelOverride {
  private final Level level;
  
  private int    majorVersion;
  private int    minorVersion;
  private String shotPath;
  private int    time;
  private int    goal;
  
  public LevelOverride(Level level) {
    this.level = level;
    
    majorVersion = level.getMajorVersion();
    minorVersion = level.getMinorVersion();
    shotPath     = level.getShotPath();
    time         = level.getTime();
    goal         = level.getGoal();
  }
  
  public LevelOverride(LevelOverride src) {
    level = src.level;
    
    majorVersion = src.majorVersion;
    minorVersion = src.minorVersion;
    shotPath     = src.shotPath;
    time         = src.time;
    goal         = src.goal;
  }
  
  public Level getLevel() {
    return level;
  }
  
  public int getMajorVersion() {
    return majorVersion;
  }
  
  public void setMajorVersion(int version) {
    majorVersion = version;
  }
  
  public int getMinorVersion() {
    return minorVersion;
  }
  
  public void setMinorVersion(int version) {
    minorVersion = version;
  }
  
  public String getShotPath() {
    return shotPath;
  }
  
  public void setShotPath(String path) {
    shotPath = path;
  }
  
  public int getTime() {
    return time;
  }
  
  public void setTime(int time) {
    this.time = time;
  }
  
  public int getGoal() {
    return goal;
  }
  
  public void setGoal(int goal) {
    this.goal = goal;
  }
  
  public void copyFrom(LevelOverride src) {
    assert src.level == level;
    
    majorVersion = src.majorVersion;
    minorVersion = src.minorVersion;
    shotPath     = src.shotPath;
    time         = src.time;
    goal         = src.goal;
  }
}
