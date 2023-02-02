/*
 * Series.java
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

package com.uppgarn.nuncabola.core.series;

import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.level.*;

import java.time.*;

public abstract class Series {
  public static final int EXTRA_BALL_MARK = 100;
  
  private final SeriesMode mode;
  
  private boolean stopped;
  
  private String player;
  private int    balls;
  private int    totalTime;
  private int    totalCoins;
  
  private Instant date;
  private String  levelPath;
  
  private LevelOverride levelOverride;
  private Status        status;
  private int           time;
  private int           gainedTime;
  private int           coins;
  
  private int newBalls;
  private int newTotalTime;
  private int newTotalCoins;
  
  protected Series(
      SeriesMode mode,
      String     player,
      int        balls,
      int        totalTime,
      int        totalCoins) {
    this.mode = mode;
    
    stopped = false;
    
    this.player     = player;
    this.balls      = balls;
    this.totalTime  = totalTime;
    this.totalCoins = totalCoins;
    
    date      = Instant.EPOCH;
    levelPath = "";
    
    levelOverride = new LevelOverride(new Level());
    status        = Status.NONE;
    time          = 0;
    gainedTime    = 0;
    coins         = 0;
    
    newBalls      = balls;
    newTotalTime  = totalTime;
    newTotalCoins = totalCoins;
  }
  
  public final SeriesMode getMode() {
    return mode;
  }
  
  public final boolean isStopped() {
    return stopped;
  }
  
  public final String getPlayer() {
    return player;
  }
  
  protected void setPlayer(String player) {
    this.player = player;
  }
  
  public final int getBalls() {
    return balls;
  }
  
  public final int getTotalTime() {
    return totalTime;
  }
  
  public final int getTotalCoins() {
    return totalCoins;
  }
  
  public final Instant getDate() {
    assert !stopped;
    
    return date;
  }
  
  public final String getLevelPath() {
    assert !stopped;
    
    return levelPath;
  }
  
  public final Level getLevel() {
    assert !stopped;
    
    return levelOverride.getLevel();
  }
  
  public final int getLevelMajorVersion() {
    assert !stopped;
    
    return levelOverride.getMajorVersion();
  }
  
  public final int getLevelMinorVersion() {
    assert !stopped;
    
    return levelOverride.getMinorVersion();
  }
  
  public final String getLevelShotPath() {
    assert !stopped;
    
    return levelOverride.getShotPath();
  }
  
  public final int getLevelTime() {
    assert !stopped;
    
    return levelOverride.getTime();
  }
  
  public final int getLevelGoal() {
    assert !stopped;
    
    return levelOverride.getGoal();
  }
  
  public final Status getStatus() {
    assert !stopped;
    
    return status;
  }
  
  public final int getTime() {
    assert !stopped;
    
    return time;
  }
  
  public final int getGainedTime() {
    assert !stopped;
    
    return gainedTime;
  }
  
  public final int getTimer() {
    assert !stopped;
    
    return (levelOverride.getTime() == 0)
           ? time : levelOverride.getTime() + gainedTime - time;
  }
  
  public final int getCoins() {
    assert !stopped;
    
    return coins;
  }
  
  public final int getGoal() {
    assert !stopped;
    
    return Math.max(levelOverride.getGoal() - coins, 0);
  }
  
  public final int getNewBalls() {
    assert !stopped;
    
    return newBalls;
  }
  
  public final int getNewTotalTime() {
    assert !stopped;
    
    return newTotalTime;
  }
  
  public final int getNewTotalCoins() {
    assert !stopped;
    
    return newTotalCoins;
  }
  
  protected void handleSuccess() {
  }
  
  protected void handleFailure() {
  }
  
  public final void update(Game game) {
    assert !stopped;
    
    if (status.isOver()) {
      return;
    }
    
    levelOverride = new LevelOverride(game.levelOverride);
    status        = game.status;
    time          = game.time;
    gainedTime    = game.gainedTime;
    coins         = game.coins;
    
    switch (status) {
      case NONE: {
        break;
      }
      case GOAL: {
        if (mode == SeriesMode.CHALLENGE) {
          newBalls      += (newTotalCoins % EXTRA_BALL_MARK + coins)
                             / EXTRA_BALL_MARK;
          newTotalTime  += time;
          newTotalCoins += coins;
        }
        
        handleSuccess();
        
        break;
      }
      case FALL_OUT:
      case TIME_OUT: {
        if (mode == SeriesMode.CHALLENGE) {
          newBalls     -= 1;
          newTotalTime += time;
        }
        
        handleFailure();
        
        break;
      }
    }
  }
  
  private void applyResult() {
    balls      = newBalls;
    totalTime  = newTotalTime;
    totalCoins = newTotalCoins;
  }
  
  protected final void play(
      boolean applyResult,
      Instant date,
      String  levelPath) {
    assert !stopped;
    
    if (applyResult) {
      applyResult();
    }
    
    this.date      = date;
    this.levelPath = levelPath;
    
    levelOverride = new LevelOverride(new Level());
    status        = Status.NONE;
    time          = 0;
    gainedTime    = 0;
    coins         = 0;
    
    newBalls      = balls;
    newTotalTime  = totalTime;
    newTotalCoins = totalCoins;
  }
  
  protected final void stop(boolean applyResult) {
    assert !stopped;
    
    if (applyResult) {
      applyResult();
    }
    
    stopped = true;
  }
}
