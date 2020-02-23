/*
 * ReplayInfo.java
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

import java.time.*;

public final class ReplayInfo {
  private SeriesMode mode;
  
  private String player;
  private int    balls;
  private int    totalTime;
  private int    totalCoins;
  
  private Instant date;
  private String  levelPath;
  private String  shotPath;
  private int     levelTime;
  private int     levelGoal;
  private Status  status;
  private int     time;
  private int     coins;
  
  public ReplayInfo() {
    mode = SeriesMode.NORMAL;
    
    player     = "";
    balls      = 0;
    totalTime  = 0;
    totalCoins = 0;
    
    date      = Instant.EPOCH;
    levelPath = "";
    shotPath  = "";
    levelTime = 0;
    levelGoal = 0;
    status    = Status.NONE;
    time      = 0;
    coins     = 0;
  }
  
  public ReplayInfo(Series series) {
    mode = series.getMode();
    
    player     = series.getPlayer();
    balls      = series.getBalls();
    totalTime  = series.getTotalTime();
    totalCoins = series.getTotalCoins();
    
    date      = series.getDate();
    levelPath = series.getLevel().getSolidPath();
    shotPath  = series.getLevel().getShotPath();
    levelTime = series.getLevelTime();
    levelGoal = series.getLevelGoal();
    status    = series.getStatus();
    time      = series.getTime();
    coins     = series.getCoins();
  }
  
  public SeriesMode getMode() {
    return mode;
  }
  
  public void setMode(SeriesMode mode) {
    this.mode = mode;
  }
  
  public String getPlayer() {
    return player;
  }
  
  public void setPlayer(String player) {
    this.player = player;
  }
  
  public int getBalls() {
    return balls;
  }
  
  public void setBalls(int balls) {
    this.balls = balls;
  }
  
  public int getTotalTime() {
    return totalTime;
  }
  
  public void setTotalTime(int time) {
    totalTime = time;
  }
  
  public int getTotalCoins() {
    return totalCoins;
  }
  
  public void setTotalCoins(int coins) {
    totalCoins = coins;
  }
  
  public Instant getDate() {
    return date;
  }
  
  public void setDate(Instant date) {
    this.date = date;
  }
  
  public String getLevelPath() {
    return levelPath;
  }
  
  public void setLevelPath(String path) {
    levelPath = path;
  }
  
  public String getShotPath() {
    return shotPath;
  }
  
  public void setShotPath(String path) {
    shotPath = path;
  }
  
  public int getLevelTime() {
    return levelTime;
  }
  
  public void setLevelTime(int time) {
    levelTime = time;
  }
  
  public int getLevelGoal() {
    return levelGoal;
  }
  
  public void setLevelGoal(int goal) {
    levelGoal = goal;
  }
  
  public Status getStatus() {
    return status;
  }
  
  public void setStatus(Status status) {
    this.status = status;
  }
  
  public int getTime() {
    return time;
  }
  
  public void setTime(int time) {
    this.time = time;
  }
  
  public int getCoins() {
    return coins;
  }
  
  public void setCoins(int coins) {
    this.coins = coins;
  }
}
