/*
 * Level.java
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

public final class Level {
  public static final int MAX_TIME = 59999;
  
  public static final List<ScoreType> SCORE_TYPES =
    Collections.unmodifiableList(Arrays.asList(ScoreType.values()));
  
  private int     majorVersion;
  private int     minorVersion;
  private String  shotPath;
  private String  bgSolidPath;
  private String  bgGradientPath;
  private String  musicPath;
  private int     time;
  private int     goal;
  private String  msg;
  private boolean bonus;
  
  private ScoreTables defaultScoreTables;
  
  private String solidPath;
  
  public Level() {
    majorVersion   = 0;
    minorVersion   = 0;
    shotPath       = "";
    bgSolidPath    = "";
    bgGradientPath = "";
    musicPath      = "";
    time           = 0;
    goal           = 0;
    msg            = "";
    bonus          = false;
    
    defaultScoreTables = new ScoreTables(SCORE_TYPES, MAX_TIME);
    
    solidPath = "";
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
  
  public String getBackgroundSolidPath() {
    return bgSolidPath;
  }
  
  public void setBackgroundSolidPath(String path) {
    bgSolidPath = path;
  }
  
  public String getBackgroundGradientPath() {
    return bgGradientPath;
  }
  
  public void setBackgroundGradientPath(String path) {
    bgGradientPath = path;
  }
  
  public String getMusicPath() {
    return musicPath;
  }
  
  public void setMusicPath(String path) {
    musicPath = path;
  }
  
  public String getMessage() {
    return msg;
  }
  
  public void setMessage(String msg) {
    this.msg = msg;
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
  
  public boolean isBonus() {
    return bonus;
  }
  
  public void setBonus(boolean bonus) {
    this.bonus = bonus;
  }
  
  public ScoreTables getDefaultScoreTables() {
    return defaultScoreTables;
  }
  
  public String getSolidPath() {
    return solidPath;
  }
  
  public void setSolidPath(String path) {
    solidPath = path;
  }
}
