/*
 * PlayActionScreen.java
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
import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.series.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.hud.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public abstract class PlayActionScreen extends GUIScreen {
  private PlaySeries series;
  private Input      input;
  private HUD        hud;
  
  @Override
  public void enter(Screen from) {
    series = PlayFuncs.getSeries();
    input  = PlayFuncs.getInput();
    hud    = UI.getHUD();
    
    super.enter(from);
    
    UI.setMouseGrabbed(true);
  }
  
  private void setCamera(Camera camera) {
    setPref(Pref.CAMERA, camera);
    
    input.setCamera(camera);
    hud  .setCamera(camera);
  }
  
  private void toggleCamera() {
    setCamera(
      (getCameraPref(Pref.CAMERA) == Camera.MANUAL)
      ? Camera.CHASE : Camera.MANUAL);
  }
  
  private void goToPlayPauseScreen() {
    PlayPauseScreen.INSTANCE.setNextScreen(this);
    
    UI.gotoScreen(PlayPauseScreen.INSTANCE);
  }
  
  @Override
  public final void paint(float t) {
    GameFuncs.draw(t);
    
    hud.draw();
    
    if (UI.getScreenTime() < 1.0f) {
      super.paint(t);
    }
  }
  
  @Override
  public void timer(float dt) {
    hud.step(dt);
    
    super.timer(dt);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if        (isKey(code, ch, Pref.KEY_CAMERA_1)) {
      setCamera(Camera.CHASE);
    } else if (isKey(code, ch, Pref.KEY_CAMERA_2)) {
      setCamera(Camera.LAZY);
    } else if (isKey(code, ch, Pref.KEY_CAMERA_3)) {
      setCamera(Camera.MANUAL);
    } else if (isKey(code, ch, Pref.KEY_CAMERA_TOGGLE)) {
      toggleCamera();
    } else if (isKey(code, ch, Pref.KEY_PAUSE)) {
      goToPlayPauseScreen();
    } else if (code == Keyboard.KEY_F10) {
      hud.toggleVisible();
    }
  }
  
  @Override
  public void mouseDown(int button) {
    if        (isMouseButton(button, Pref.MOUSE_CAMERA_1)) {
      setCamera(Camera.CHASE);
    } else if (isMouseButton(button, Pref.MOUSE_CAMERA_2)) {
      setCamera(Camera.LAZY);
    } else if (isMouseButton(button, Pref.MOUSE_CAMERA_3)) {
      setCamera(Camera.MANUAL);
    } else if (isMouseButton(button, Pref.MOUSE_CAMERA_TOGGLE)) {
      toggleCamera();
    }
  }
  
  @Override
  public void controllerDown(int button) {
    if        (button == getIntPref(Pref.CONTROLLER_CAMERA_1)) {
      setCamera(Camera.CHASE);
    } else if (button == getIntPref(Pref.CONTROLLER_CAMERA_2)) {
      setCamera(Camera.LAZY);
    } else if (button == getIntPref(Pref.CONTROLLER_CAMERA_3)) {
      setCamera(Camera.MANUAL);
    } else if (button == getIntPref(Pref.CONTROLLER_CAMERA_TOGGLE)) {
      toggleCamera();
    }
  }
  
  @Override
  public final void windowDeactivated() {
    goToPlayPauseScreen();
  }
  
  @Override
  public final void exitRequested() {
    if (isKey(Keyboard.KEY_ESCAPE, (char) 0, Pref.KEY_PAUSE)) {
      goToPlayPauseScreen();
    } else {
      Audio.stopSounds();
      
      UI.setMouseGrabbed(false);
      
      if (series.getMode() == SeriesMode.CHALLENGE) {
        UI.gotoScreen(PlayGameOverScreen.INSTANCE);
      } else {
        PlayFuncs.deinitialize();
        
        UI.gotoScreen(LevelSetScreen.INSTANCE);
      }
    }
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    series = null;
    input  = null;
    hud    = null;
  }
}
