/*
 * PlayMainScreen.java
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
import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.core.series.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.hud.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public final class PlayMainScreen extends PlayActionScreen {
  public static final PlayMainScreen INSTANCE = new PlayMainScreen();
  
  private Screen     from;
  private PlaySeries series;
  private Input      input;
  private HUD        hud;
  
  private AxisState xState;
  private AxisState yState;
  private AxisState rotationState;
  private boolean   rotateFast;
  
  private PlayMainScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    this.from = from;
    series    = PlayFuncs.getSeries();
    input     = PlayFuncs.getInput();
    hud       = UI.getHUD();
    
    xState        = new AxisState();
    yState        = new AxisState();
    rotationState = new AxisState();
    rotateFast    = false;
    
    input.setRotation(0.0f);
    
    hud.setContents(series, false);
    
    super.enter(from);
    
    if (from != PlayPauseScreen.INSTANCE) {
      Audio.playSound("snd/go.ogg");
    }
  }
  
  private void setPosition(int x, int y) {
    final float range = Input.MAX_TILT * 2;
    
    int mouseSense = getIntPref(Pref.MOUSE_SENSE);
    
    input.setX(input.getX() + range * y / mouseSense);
    input.setZ(input.getZ() + range * x / mouseSense);
  }
  
  private void updateInputZ() {
    input.setZ(+Input.MAX_TILT * xState.get());
  }
  
  private void setX(float value) {
    xState.set(value);
    
    updateInputZ();
  }
  
  private void setX(int side, boolean on) {
    xState.set(side, on);
    
    updateInputZ();
  }
  
  private void updateInputX() {
    input.setX(-Input.MAX_TILT * yState.get());
  }
  
  private void setY(float value) {
    yState.set(value);
    
    updateInputX();
  }
  
  private void setY(int side, boolean on) {
    yState.set(side, on);
    
    updateInputX();
  }
  
  private void setResponse(Pref pref) {
    input.setResponse(getIntPref(pref) * 0.001f);
  }
  
  private void updateInputRotation() {
    int rate = getIntPref(rotateFast ? Pref.ROTATE_FAST : Pref.ROTATE_SLOW);
    
    input.setRotation(rotationState.get() * (rate / 100.0f));
  }
  
  private void setRotation(float value) {
    rotationState.set(value);
    
    updateInputRotation();
  }
  
  private void setRotation(int side, boolean on) {
    rotationState.set(side, on);
    
    updateInputRotation();
  }
  
  private void setRotateFast(boolean fast) {
    rotateFast = fast;
    
    updateInputRotation();
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    if (from != PlayPauseScreen.INSTANCE) {
      TextLabel lbl = gui.textLabel(null, Font.LARGE);
      lbl.setText  ("GO!");
      lbl.setColors(Color.GREEN, Color.BLUE);
      lbl.setScale (1.2f);
    }
  }
  
  @Override
  public void timer(float dt) {
    PlayFuncs.step(dt);
    
    if (series.getStatus().isOver()) {
      Screen screen;
      
      switch (series.getStatus()) {
        case GOAL: {
          screen = PlayGoalScreen   .INSTANCE;
          
          break;
        }
        case FALL_OUT: {
          screen = PlayFallOutScreen.INSTANCE;
          
          break;
        }
        case TIME_OUT: {
          screen = PlayTimeOutScreen.INSTANCE;
          
          break;
        }
        
        default: {
          throw new AssertionError();
        }
      }
      
      UI.setMouseGrabbed(false);
      UI.gotoScreen     (screen, true);
      
      return;
    }
    
    hud.setContents(series, true);
    
    super.timer(dt);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if        (isKey(code, ch, Pref.KEY_LEFT)) {
      setX(0, true);
      
      setResponse(Pref.RESPONSE_KEYBOARD);
    } else if (isKey(code, ch, Pref.KEY_RIGHT)) {
      setX(1, true);
      
      setResponse(Pref.RESPONSE_KEYBOARD);
    } else if (isKey(code, ch, Pref.KEY_FORWARD)) {
      setY(0, true);
      
      setResponse(Pref.RESPONSE_KEYBOARD);
    } else if (isKey(code, ch, Pref.KEY_BACKWARD)) {
      setY(1, true);
      
      setResponse(Pref.RESPONSE_KEYBOARD);
    } else if (isKey(code, ch, Pref.KEY_ROTATE_L)) {
      setRotation(0, true);
    } else if (isKey(code, ch, Pref.KEY_ROTATE_R)) {
      setRotation(1, true);
    } else if (isKey(code, ch, Pref.KEY_ROTATE_FAST)) {
      setRotateFast(true);
    } else if (isKey(code, ch, Pref.KEY_RESTART)) {
      if (PlayFuncs.canRestartLevel()) {
        Audio.stopSounds();
        
        PlayFuncs.restartLevel();
        
        UI.gotoScreen(PlayReadyScreen.INSTANCE);
      }
    } else if (code == Keyboard.KEY_F5) {
      if (getBooleanPref(Pref.CHEAT)) {
        UI.gotoScreen(PlayLookScreen.INSTANCE);
      }
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public void keyUp(int code, char ch, char origCh) {
    if        (isKey(code, origCh, Pref.KEY_LEFT)) {
      setX(0, false);
      
      setResponse(Pref.RESPONSE_KEYBOARD);
    } else if (isKey(code, origCh, Pref.KEY_RIGHT)) {
      setX(1, false);
      
      setResponse(Pref.RESPONSE_KEYBOARD);
    } else if (isKey(code, origCh, Pref.KEY_FORWARD)) {
      setY(0, false);
      
      setResponse(Pref.RESPONSE_KEYBOARD);
    } else if (isKey(code, origCh, Pref.KEY_BACKWARD)) {
      setY(1, false);
      
      setResponse(Pref.RESPONSE_KEYBOARD);
    } else if (isKey(code, origCh, Pref.KEY_ROTATE_L)) {
      setRotation(0, false);
    } else if (isKey(code, origCh, Pref.KEY_ROTATE_R)) {
      setRotation(1, false);
    } else if (isKey(code, origCh, Pref.KEY_ROTATE_FAST)) {
      setRotateFast(false);
    }
  }
  
  @Override
  public void mouseMove(int x, int y, int dx, int dy) {
    setPosition(dx, dy);
    
    setResponse(Pref.RESPONSE_MOUSE);
  }
  
  @Override
  public void mouseDown(int button) {
    if        (isMouseButton(button, Pref.MOUSE_ROTATE_L)) {
      setRotation(0, true);
    } else if (isMouseButton(button, Pref.MOUSE_ROTATE_R)) {
      setRotation(1, true);
    } else {
      super.mouseDown(button);
    }
  }
  
  @Override
  public void mouseUp(int button) {
    if        (isMouseButton(button, Pref.MOUSE_ROTATE_L)) {
      setRotation(0, false);
    } else if (isMouseButton(button, Pref.MOUSE_ROTATE_R)) {
      setRotation(1, false);
    }
  }
  
  @Override
  public void controllerMove(int axis, float value, boolean recentered) {
    if        (axis == getIntPref(Pref.CONTROLLER_AXIS_X)) {
      setX(value);
      
      setResponse(Pref.RESPONSE_CONTROLLER);
    } else if (axis == getIntPref(Pref.CONTROLLER_AXIS_Y)) {
      setY(value);
      
      setResponse(Pref.RESPONSE_CONTROLLER);
    } else if (axis == getIntPref(Pref.CONTROLLER_AXIS_Z)) {
      setRotation(value);
    }
  }
  
  @Override
  public void controllerDown(int button) {
    if        (button == getIntPref(Pref.CONTROLLER_ROTATE_L)) {
      setRotation(0, true);
    } else if (button == getIntPref(Pref.CONTROLLER_ROTATE_R)) {
      setRotation(1, true);
    } else if (button == getIntPref(Pref.CONTROLLER_ROTATE_FAST)) {
      setRotateFast(true);
    } else {
      super.controllerDown(button);
    }
  }
  
  @Override
  public void controllerUp(int button) {
    if        (button == getIntPref(Pref.CONTROLLER_ROTATE_L)) {
      setRotation(0, false);
    } else if (button == getIntPref(Pref.CONTROLLER_ROTATE_R)) {
      setRotation(1, false);
    } else if (button == getIntPref(Pref.CONTROLLER_ROTATE_FAST)) {
      setRotateFast(false);
    }
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    series        = null;
    input         = null;
    hud           = null;
    xState        = null;
    yState        = null;
    rotationState = null;
  }
}
