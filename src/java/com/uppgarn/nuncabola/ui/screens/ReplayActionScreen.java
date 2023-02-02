/*
 * ReplayActionScreen.java
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

package com.uppgarn.nuncabola.ui.screens;

import com.uppgarn.nuncabola.core.audio.*;
import com.uppgarn.nuncabola.core.replay.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.hud.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public abstract class ReplayActionScreen extends GUIScreen {
  private HUD hud;
  
  @Override
  public void enter(Screen from) {
    hud = UI.getHUD();
    
    super.enter(from);
  }
  
  private void changeSpeed(int dir) {
    Speed speed = ReplayFuncs.getSpeed().getNext(dir);
    
    ReplayFuncs.setSpeed(speed);
    hud        .setSpeed(speed);
  }
  
  private void goToReplayPauseScreen() {
    UI.gotoScreen(ReplayPauseScreen.INSTANCE);
  }
  
  @Override
  public final void paint(float t) {
    GameFuncs.draw(t);
    
    hud.draw();
    
    super.paint(t);
  }
  
  @Override
  public void timer(float dt) {
    hud.step(dt);
    
    super.timer(dt);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if        (isKey(code, ch, Pref.KEY_FORWARD)) {
      changeSpeed(+1);
    } else if (isKey(code, ch, Pref.KEY_BACKWARD)) {
      changeSpeed(-1);
    } else if (isKey(code, ch, Pref.KEY_PAUSE)
        || (code == Keyboard.KEY_RETURN)) {
      goToReplayPauseScreen();
    } else if (code == Keyboard.KEY_F5) {
      if (getBooleanPref(Pref.CHEAT)) {
        UI.gotoScreen(ReplayLookScreen.INSTANCE);
      }
    } else if (code == Keyboard.KEY_F10) {
      hud.toggleVisible();
    }
  }
  
  @Override
  public void mouseDown(int button) {
    if (button == 0) {
      goToReplayPauseScreen();
    }
  }
  
  @Override
  public final void mouseWheel(int wheel) {
    changeSpeed(wheel);
  }
  
  @Override
  public final void controllerMove(int axis, float value, boolean recentered) {
    if (!recentered) {
      return;
    }
    
    if (axis == getIntPref(Pref.CONTROLLER_AXIS_Y)) {
      if (value < -0.5f) {
        changeSpeed(+1);
      } else if (value > +0.5f) {
        changeSpeed(-1);
      }
    }
  }
  
  @Override
  public void controllerDown(int button) {
    if (button == getIntPref(Pref.CONTROLLER_BUTTON_A)) {
      goToReplayPauseScreen();
    }
  }
  
  @Override
  public final void exitRequested() {
    if (isKey(Keyboard.KEY_ESCAPE, (char) 0, Pref.KEY_PAUSE)) {
      goToReplayPauseScreen();
    } else {
      Audio.stopSounds();
      
      UI.gotoScreen(ReplayEndScreen.INSTANCE);
    }
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      ReplayFuncs.deinitialize();
    }
    
    hud = null;
  }
}
