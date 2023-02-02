/*
 * PlayFuncs.java
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

package com.uppgarn.nuncabola.functions;

import com.uppgarn.nuncabola.core.audio.*;
import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.replay.*;
import com.uppgarn.nuncabola.core.series.*;
import com.uppgarn.nuncabola.preferences.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public final class PlayFuncs {
  private static PlaySeries series;
  
  private static Game           currGame;
  private static ReplayRecorder replayRecorder;
  private static Input          input;
  private static PlayGameServer gameServer;
  private static GameClient     gameClient;
  private static Game           prevGame;
  private static float          accum;
  
  public static void initialize(
      SeriesMode mode,
      boolean    normalUnlockGoals,
      int        levelIdx) throws FuncsException {
    // Series.
    
    series = new PlaySeries(
      mode,
      getStringPref(Pref.PLAYER),
      LevelSetFuncs.getSet(),
      LevelSetFuncs.getLevels(),
      LevelSetFuncs.getSetScore(),
      normalUnlockGoals,
      levelIdx,
      new PlaySeriesListener() {
        @Override
        public void levelSetScoreChanged(LevelSetScore setScore) {
          LevelSetFuncs.saveSetScore();
        }
      });
    
    // Initialize game functions.
    
    try {
      initializeGameFuncs();
    } catch (FuncsException ex) {
      series = null;
      
      throw ex;
    }
  }
  
  private static void initializeGameFuncs() throws FuncsException {
    GameFuncs.load(series.getLevelPath());
    
    reset();
  }
  
  private static void reset() {
    // Current game.
    
    currGame = new Game(GameFuncs.getGameBase());
    
    GameFuncs.getGame().copyFrom(currGame);
    
    series.update(GameFuncs.getGame());
    
    // Replay recorder.
    
    replayRecorder = new ReplayRecorder(
      ReplayFileFuncs.getGameReplayFile(),
      series);
    
    // Input.
    
    input = new Input();
    input.setCamera     (getCameraPref(Pref.CAMERA));
    input.setCameraSpeed(getIntPref   (Pref.CAMERA_SPEED) / 1000.0f);
    
    // Game server.
    
    gameServer = new PlayGameServer(
      GameFuncs.getLevel(),
      GameFuncs.getSolidBase(),
      series.getUnlockGoals(),
      GameFuncs.getViewDistance(),
      Rate.DEFAULT);
    
    // Game client.
    
    gameClient = new GameClient(
      currGame,
      new GameClientListener() {
        @Override
        public void soundRequested(String path, float amp) {
          Audio.playSound(path, amp);
        }
      });
    
    // Process first update.
    
    processCommands();
    
    GameFuncs.getGame().copyFrom(currGame);
    
    series.update(GameFuncs.getGame());
    
    // Previous game.
    
    prevGame = new Game(currGame);
    
    // Accumulator.
    
    accum = 0.0f;
  }
  
  private static void processCommands() {
    for (Command cmd: gameServer.getCommands()) {
      gameClient    .execute(cmd);
      replayRecorder.record (cmd);
    }
  }
  
  public static PlaySeries getSeries() {
    return series;
  }
  
  public static Input getInput() {
    return input;
  }
  
  public static void step(float dt) {
    accum += dt;
    
    for (float time; accum >= (time = gameServer.getRate().getTime());) {
      accum -= time;
      
      prevGame.copyFrom(currGame);
      
      gameServer.step(input);
      
      processCommands();
    }
    
    float alpha = accum / gameServer.getRate().getTime();
    
    GameFuncs.getGame().copyFrom(prevGame, currGame, alpha);
    
    series.update(GameFuncs.getGame());
  }
  
  public static void stopRecording() {
    replayRecorder.close();
  }
  
  public static String getDefaultReplayName() {
    String name = ReplayNameTool.getName(
      getStringPref(Pref.REPLAY_NAME_PATTERN),
      LevelSetFuncs.getSet(),
      LevelSetFuncs.getLevelNumber(series.getLevelIndex()));
    
    if (name.isEmpty()) {
      return name;
    } else {
      return ReplayFileFuncs.getDisambiguatedName(name);
    }
  }
  
  public static boolean canRestartLevel() {
    return series.canRestartLevel();
  }
  
  public static void restartLevel() {
    replayRecorder.close();
    
    series.restartLevel();
    
    reset();
  }
  
  public static boolean canRetryLevel() {
    return series.canRetryLevel();
  }
  
  public static void retryLevel() {
    replayRecorder.close();
    
    series.retryLevel();
    
    reset();
  }
  
  public static boolean canPlayNextLevel() {
    return series.canPlayNextLevel();
  }
  
  public static void playNextLevel() throws FuncsException {
    replayRecorder.close();
    
    series.playNextLevel();
    
    initializeGameFuncs();
  }
  
  public static boolean canFinish() {
    return series.canFinish();
  }
  
  public static void finish() {
    replayRecorder.close();
    
    series.finish();
  }
  
  public static void deinitialize() {
    replayRecorder.close();
    
    series         = null;
    currGame       = null;
    replayRecorder = null;
    input          = null;
    gameServer     = null;
    gameClient     = null;
    prevGame       = null;
  }
  
  private PlayFuncs() {
  }
}
