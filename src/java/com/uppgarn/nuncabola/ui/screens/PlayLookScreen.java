/*
 * PlayLookScreen.java
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

package com.uppgarn.nuncabola.ui.screens;

import com.uppgarn.nuncabola.core.audio.*;
import com.uppgarn.nuncabola.core.controls.*;
import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.graphics.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public final class PlayLookScreen extends Screen {
  public static final PlayLookScreen INSTANCE = new PlayLookScreen();
  
  private Game       oldGame;
  private GameLooker gameLooker;
  
  private AxisState xState;
  private AxisState yState;
  
  private PlayLookScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    oldGame    = new Game      (GameFuncs.getGame());
    gameLooker = new GameLooker(GameFuncs.getGame());
    
    xState = new AxisState();
    yState = new AxisState();
    
    Audio.setSoundsPaused(true);
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(t);
  }
  
  @Override
  public void timer(float dt) {
    float k = 5 * dt;
    
    gameLooker.move(xState.get() * k, -yState.get() * k);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if        (isKey(code, ch, Pref.KEY_LEFT)) {
      xState.set(0, true);
    } else if (isKey(code, ch, Pref.KEY_RIGHT)) {
      xState.set(1, true);
    } else if (isKey(code, ch, Pref.KEY_FORWARD)) {
      yState.set(0, true);
    } else if (isKey(code, ch, Pref.KEY_BACKWARD)) {
      yState.set(1, true);
    } else if (code == Keyboard.KEY_F5) {
      exitRequested();
    }
  }
  
  @Override
  public void keyUp(int code, char ch, char origCh) {
    if        (isKey(code, origCh, Pref.KEY_LEFT)) {
      xState.set(0, false);
    } else if (isKey(code, origCh, Pref.KEY_RIGHT)) {
      xState.set(1, false);
    } else if (isKey(code, origCh, Pref.KEY_FORWARD)) {
      yState.set(0, false);
    } else if (isKey(code, origCh, Pref.KEY_BACKWARD)) {
      yState.set(1, false);
    }
  }
  
  @Override
  public void mouseMove(int x, int y, int dx, int dy) {
    gameLooker.turn((float) dx / Gfx.getWidth(), (float) dy / Gfx.getHeight());
  }
  
  @Override
  public void mouseWheel(int wheel) {
    gameLooker.move(0.0f, wheel);
  }
  
  @Override
  public void controllerMove(int axis, float value, boolean recentered) {
    if        (axis == getIntPref(Pref.CONTROLLER_AXIS_X)) {
      xState.set(value);
    } else if (axis == getIntPref(Pref.CONTROLLER_AXIS_Y)) {
      yState.set(value);
    }
  }
  
  @Override
  public void windowDeactivated() {
    PlayPauseScreen.INSTANCE.setNextScreen(PlayMainScreen.INSTANCE);
    
    UI.gotoScreen(PlayPauseScreen.INSTANCE);
  }
  
  @Override
  public void exitRequested() {
    Audio.setSoundsPaused(false);
    
    UI.gotoScreen(PlayMainScreen.INSTANCE);
  }
  
  @Override
  public void leave(Screen to) {
    GameFuncs.getGame().copyFrom(oldGame);
    
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    oldGame    = null;
    gameLooker = null;
    xState     = null;
    yState     = null;
  }
}
