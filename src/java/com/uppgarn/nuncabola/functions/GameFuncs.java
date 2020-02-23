/*
 * GameFuncs.java
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

import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.renderers.*;
import com.uppgarn.nuncabola.core.solid.*;
import com.uppgarn.nuncabola.preferences.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import java.io.*;

public final class GameFuncs {
  private static SolidBase    solBase;
  private static Level        level;
  private static GameBase     gameBase;
  private static Game         game;
  private static GameRenderer gameRend;
  
  private static float fade;
  
  public static void load(String path) throws FuncsException {
    // Solid base.
    
    solBase = loadSolidBase(path);
    
    // Level.
    
    level = LevelParser.parse(solBase.meta, path);
    
    // Game base.
    
    gameBase = new GameBase(level, solBase);
    
    // Game.
    
    game = new Game(gameBase);
    
    // Game renderer.
    
    if (gameRend != null) {
      gameRend.deinitialize();
    }
    
    gameRend = new GameRenderer(game);
    
    // Fade.
    
    fade = 0.0f;
    
    // Hack: Warm up OpenGL drawing.
    
    draw(0.0f);
  }
  
  private static SolidBase loadSolidBase(String path) throws FuncsException {
    try {
      SolidBase solBase = SolidReadTool.readSolidBase(
        DataFuncs.getSource(path));
      
      if (solBase.ballBases.length == 0) {
        throw new FuncsException();
      }
      
      return solBase;
    } catch (IOException ex) {
      throw new FuncsException();
    }
  }
  
  public static Level getLevel() {
    return level;
  }
  
  public static SolidBase getSolidBase() {
    return solBase;
  }
  
  public static GameBase getGameBase() {
    return gameBase;
  }
  
  public static Game getGame() {
    return game;
  }
  
  public static void setFade(float fade) {
    GameFuncs.fade = fade;
  }
  
  public static void draw(float t) {
    draw(t, Pose.NONE);
  }
  
  public static void draw(float t, Pose pose) {
    if (gameRend != null) {
      gameRend.draw(t, pose, fade);
    }
  }
  
  public static ViewDistance getViewDistance() {
    return new ViewDistance(
      getIntPref(Pref.VIEW_DISTANCE_PY) / 100.0f,
      getIntPref(Pref.VIEW_DISTANCE_PZ) / 100.0f,
      getIntPref(Pref.VIEW_DISTANCE_CY) / 100.0f);
  }
  
  public static void deinitialize() {
    if (gameRend != null) {
      gameRend.deinitialize();
    }
    
    solBase  = null;
    level    = null;
    gameBase = null;
    game     = null;
    gameRend = null;
  }
  
  private GameFuncs() {
  }
}
