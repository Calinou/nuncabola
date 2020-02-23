/*
 * PlayFuncs.java
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
  private static Game           prevGame;
  private static ReplayRecorder replayRecorder;
  private static Input          input;
  private static PlayGameServer gameServer;
  private static GameClient     gameClient;
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
    GameFuncs.load(series.getLevel().getSolidPath());
    
    reset();
  }
  
  private static void reset() {
    // Games.
    
    currGame = new Game(GameFuncs.getGameBase());
    prevGame = new Game(GameFuncs.getGameBase());
    
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
      GameFuncs.getViewDistance(),
      new GameClientListener() {
        @Override
        public void soundRequested(String path, float amp) {
          Audio.playSound(path, amp);
        }
      });
    
    // Accumulator.
    
    accum = 0.0f;
    
    // Process first update.
    
    processCommands();
    
    prevGame.copyFrom(currGame);
    
    updateGame();
  }
  
  private static void processCommands() {
    for (Command cmd: gameServer.getCommands()) {
      gameClient    .execute(cmd);
      replayRecorder.record (cmd);
    }
  }
  
  private static void updateGame() {
    float alpha = accum / gameServer.getRate().getTime();
    
    GameFuncs.getGame().copyFrom(prevGame, currGame, alpha);
    
    series.update(GameFuncs.getGame());
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
    
    updateGame();
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
    prevGame       = null;
    replayRecorder = null;
    input          = null;
    gameServer     = null;
    gameClient     = null;
  }
  
  private PlayFuncs() {
  }
}
