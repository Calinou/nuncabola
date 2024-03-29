/*
 * ReplayFuncs.java
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
import com.uppgarn.nuncabola.core.replay.*;

import com.uppgarn.codelibf.io.*;

import java.io.*;
import java.nio.file.*;

public final class ReplayFuncs {
  private static Source  src;
  private static Path    file;
  private static boolean soundEnabled;
  
  private static ReplayInfo   info;
  private static ReplaySeries series;
  
  private static Game             currGame;
  private static ReplayGameServer gameServer;
  private static GameClient       gameClient;
  private static Game             prevGame;
  private static float            accum;
  private static Speed            speed;
  
  public static void initialize(Source src) throws FuncsException {
    initialize(src, true);
  }
  
  public static void initialize(Source src, boolean soundEnabled)
      throws FuncsException {
    initialize(src, null, soundEnabled);
  }
  
  public static void initialize(Path file) throws FuncsException {
    initialize(file, true);
  }
  
  public static void initialize(Path file, boolean soundEnabled)
      throws FuncsException {
    initialize(new FileSource(file), file, soundEnabled);
  }
  
  public static void initialize(Source src, Path file, boolean soundEnabled)
      throws FuncsException {
    ReplayFuncs.src          = src;
    ReplayFuncs.file         = file;
    ReplayFuncs.soundEnabled = soundEnabled;
    
    // Load replay info.
    
    try {
      info = ReplayInfoReadTool.readReplayInfo(src);
    } catch (IOException ex) {
      ReplayFuncs.src  = null;
      ReplayFuncs.file = null;
      
      throw new FuncsException();
    }
    
    // Series.
    
    series = new ReplaySeries(info);
    
    // Initialize game functions.
    
    try {
      initializeGameFuncs();
    } catch (FuncsException ex) {
      ReplayFuncs.src  = null;
      ReplayFuncs.file = null;
      info             = null;
      series           = null;
      
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
    
    ReplayLevelOverrider.override(currGame.levelOverride, info);
    
    GameFuncs.getGame().copyFrom(currGame);
    
    series.update(GameFuncs.getGame());
    
    // Game server.
    
    gameServer = new ReplayGameServer(src);
    
    // Game client.
    
    gameClient = new GameClient(
      currGame,
      new GameClientListener() {
        @Override
        public void soundRequested(String path, float amp) {
          if (soundEnabled) {
            Audio.playSound(path, amp);
          }
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
    
    // Speed.
    
    speed = Speed.NORMAL;
  }
  
  private static void processCommands() {
    for (Command cmd: gameServer.getCommands()) {
      gameClient.execute(cmd);
    }
  }
  
  public static Path getFile() {
    return file;
  }
  
  public static ReplaySeries getSeries() {
    return series;
  }
  
  public static boolean isOver() {
    return gameServer.getCommands().isEmpty();
  }
  
  public static Speed getSpeed() {
    return speed;
  }
  
  public static void setSpeed(Speed speed) {
    ReplayFuncs.speed = speed;
  }
  
  public static void step(float dt) {
    accum += dt * speed.getFactor();
    
    for (float time; accum >= (time = gameServer.getRate().getTime());) {
      accum -= time;
      
      prevGame.copyFrom(currGame);
      
      gameServer.step();
      
      processCommands();
    }
    
    float alpha = accum / gameServer.getRate().getTime();
    
    GameFuncs.getGame().copyFrom(prevGame, currGame, alpha);
    
    series.update(GameFuncs.getGame());
  }
  
  public static void repeat() {
    gameServer.close();
    
    series.repeat();
    
    reset();
  }
  
  public static void deinitialize() {
    gameServer.close();
    
    src        = null;
    file       = null;
    info       = null;
    series     = null;
    currGame   = null;
    gameServer = null;
    gameClient = null;
    prevGame   = null;
  }
  
  private ReplayFuncs() {
  }
}
