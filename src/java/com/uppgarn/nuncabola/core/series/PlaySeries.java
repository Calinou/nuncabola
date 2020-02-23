/*
 * PlaySeries.java
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

package com.uppgarn.nuncabola.core.series;

import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.level.*;

import java.time.*;
import java.util.*;

public final class PlaySeries extends Series {
  private final LevelSet           set;
  private final List<Level>        levels;
  private final LevelSetScore      setScore;
  private final boolean            normalUnlockGoals;
  private final PlaySeriesListener listener;
  
  private ScoreResults totalScoreResults;
  
  private int          levelIdx;
  private int          nextLevelIdx;
  private boolean      unlockGoals;
  private ScoreResults scoreResults;
  
  public PlaySeries(
      SeriesMode         mode,
      String             player,
      LevelSet           set,
      List<Level>        levels,
      LevelSetScore      setScore,
      boolean            normalUnlockGoals,
      int                startLevelIdx,
      PlaySeriesListener listener) {
    super(mode, player, (mode == SeriesMode.CHALLENGE) ? 2 : 0, 0, 0);
    
    this.set               = set;
    this.levels            = levels;
    this.setScore          = setScore;
    this.normalUnlockGoals = normalUnlockGoals;
    this.listener          = listener;
    
    totalScoreResults = null;
    
    playLevel(false, startLevelIdx);
  }
  
  private LevelScore getLevelScore(int idx) {
    return setScore.getLevelScore(idx);
  }
  
  private void playLevel(boolean applyResult, int idx) {
    levelIdx     = idx;
    nextLevelIdx = -1;
    unlockGoals  = (getMode() == SeriesMode.NORMAL)
                     && normalUnlockGoals
                     && (getLevelScore(levelIdx).getStatus()
                       == LevelStatus.COMPLETED);
    scoreResults = null;
    
    Instant date      = Instant.now();
    Level   level     = levels.get(levelIdx);
    int     levelTime = level.getTime();
    int     levelGoal = level.getGoal();
    
    play(applyResult, date, level, levelTime, levelGoal);
  }
  
  @Override
  public void setPlayer(String player) {
    if (getPlayer().equals(player)) {
      return;
    }
    
    super.setPlayer(player);
    
    boolean scoreDirty = false;
    
    // Update total score results.
    
    if (totalScoreResults != null) {
      totalScoreResults.setPlayer(player);
      
      if (totalScoreResults.isRecord()) {
        scoreDirty = true;
      }
    }
    
    // Update score results.
    
    if (!isStopped() && (scoreResults != null)) {
      scoreResults.setPlayer(player);
      
      if (scoreResults.isRecord()) {
        scoreDirty = true;
      }
    }
    
    // If the set score has been modified, notify the listener.
    
    if (scoreDirty) {
      listener.levelSetScoreChanged(setScore);
    }
  }
  
  public ScoreResults getTotalScoreResults() {
    return totalScoreResults;
  }
  
  public int getLevelIndex() {
    assert !isStopped();
    
    return levelIdx;
  }
  
  public boolean getUnlockGoals() {
    assert !isStopped();
    
    return unlockGoals;
  }
  
  public ScoreResults getScoreResults() {
    assert !isStopped();
    
    return scoreResults;
  }
  
  @Override
  protected void handleSuccess() {
    boolean scoreDirty = false;
    
    // Mark current level as completed if still uncompleted.
    
    if (getLevelScore(levelIdx).getStatus() != LevelStatus.COMPLETED) {
      getLevelScore(levelIdx).setStatus(LevelStatus.COMPLETED);
      
      scoreDirty = true;
    }
    
    // Find next playable level.
    
    switch (getMode()) {
      case NORMAL: {
        for (int idx = levelIdx + 1; idx < set.getLevelCount(); idx++) {
          if ((levels.get(idx) != null)
              && !(levels.get(idx).isBonus()
                && (getLevelScore(idx).getStatus() == LevelStatus.LOCKED))) {
            nextLevelIdx = idx;
            
            break;
          }
        }
        
        break;
      }
      case CHALLENGE: {
        for (int idx = levelIdx + 1; idx < set.getLevelCount(); idx++) {
          if (levels.get(idx) != null) {
            if (levels.get(idx).isBonus()) {
              // Unlock bonus level if still locked.
              
              if (getLevelScore(idx).getStatus() == LevelStatus.LOCKED) {
                getLevelScore(idx).setStatus(LevelStatus.OPEN);
                
                scoreDirty = true;
              }
            } else {
              nextLevelIdx = idx;
              
              break;
            }
          }
        }
        
        break;
      }
    }
    
    // Unlock next level if still locked.
    
    if ((nextLevelIdx != -1)
        && (getLevelScore(nextLevelIdx).getStatus() == LevelStatus.LOCKED)) {
      getLevelScore(nextLevelIdx).setStatus(LevelStatus.OPEN);
      
      scoreDirty = true;
    }
    
    // Pass score to level score tables.
    
    scoreResults = getLevelScore(levelIdx).getTables().enter(
      new Score(getTime(), getCoins()),
      getPlayer(),
      getLevelGoal());
    
    if (scoreResults.isRecord()) {
      scoreDirty = true;
    }
    
    // If the set score has been modified, notify the listener.
    
    if (scoreDirty) {
      listener.levelSetScoreChanged(setScore);
    }
  }
  
  @Override
  protected void handleFailure() {
    // Find next playable level.
    
    switch (getMode()) {
      case NORMAL: {
        for (int idx = levelIdx + 1; idx < set.getLevelCount(); idx++) {
          if ((levels.get(idx) != null)
              && getLevelScore(idx).getStatus().isAccessible()) {
            nextLevelIdx = idx;
            
            break;
          }
        }
        
        break;
      }
      case CHALLENGE: {
        break;
      }
    }
  }
  
  public boolean canRestartLevel() {
    return !isStopped()
      && !getStatus().isOver()
      && (getMode() != SeriesMode.CHALLENGE);
  }
  
  public void restartLevel() {
    assert canRestartLevel();
    
    playLevel(false, levelIdx);
  }
  
  public boolean canRetryLevel() {
    return !isStopped()
      && getStatus().isOver()
      && ((getMode() != SeriesMode.CHALLENGE) || (getNewBalls() >= 0));
  }
  
  public void retryLevel() {
    assert canRetryLevel();
    
    playLevel(getStatus() != Status.GOAL, levelIdx);
  }
  
  public boolean canPlayNextLevel() {
    return !isStopped() && getStatus().isOver() && (nextLevelIdx != -1);
  }
  
  public void playNextLevel() {
    assert canPlayNextLevel();
    
    playLevel(true, nextLevelIdx);
  }
  
  public boolean canFinish() {
    return !isStopped() && (getStatus() == Status.GOAL) && (nextLevelIdx == -1);
  }
  
  public void finish() {
    assert canFinish();
    
    stop(true);
    
    if (getMode() == SeriesMode.CHALLENGE) {
      // Pass total score to set score tables.
      
      totalScoreResults = setScore.getTables().enter(
        new Score(getTotalTime(), getTotalCoins()),
        getPlayer(),
        0);
      
      // If the set score has been modified, notify the listener.
      
      if (totalScoreResults.isRecord()) {
        listener.levelSetScoreChanged(setScore);
      }
    }
  }
}
