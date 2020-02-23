/*
 * PlayPauseScreen.java
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
import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.core.series.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.hud.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public final class PlayPauseScreen extends MenuScreen {
  public static final PlayPauseScreen INSTANCE = new PlayPauseScreen();
  
  private Screen nextScreen;
  
  private PlaySeries series;
  private HUD        hud;
  
  private PlayPauseScreen() {
  }
  
  public void setNextScreen(Screen screen) {
    nextScreen = screen;
  }
  
  @Override
  public void enter(Screen from) {
    series = PlayFuncs.getSeries();
    hud    = UI.getHUD();
    
    super.enter(from);
    
    Audio.setPaused(true);
    
    UI.setMouseGrabbed(false);
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl = gui.textLabel(c0, Font.LARGE);
      lbl.setText ("Paused");
      lbl.setScale(1.2f);
      
      gui.space(c0);
      
      Container c1 = gui.hArray(c0);
      
      {
        Button btn0 = gui.button(c1);
        btn0.setText ("Continue");
        btn0.setToken(Action.CONTINUE);
        
        gui.setFocusWidget(btn0);
        
        Button btn1 = gui.button(c1);
        btn1.setText("Restart");
        
        if (PlayFuncs.canRestartLevel()) {
          btn1.setToken(Action.RESTART);
        } else {
          btn1.setColor(Color.GRAY);
          btn1.setToken(Action.DISABLED);
        }
        
        Button btn2 = gui.button(c1);
        btn2.setText ("Quit");
        btn2.setToken(Action.QUIT);
      }
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    // Rather than invoking super.performAction(token0, token1)
    // (which plays a menu sound) at the top of this method,
    // it needs to be called manually inside each case block
    // to ensure the call happens /after/ stopping the audio.
    
    switch ((Action) token0) {
      case CONTINUE: {
        Audio.setPaused(false);
        
        super.performAction(token0, token1);
        
        UI.gotoScreen(nextScreen);
        
        break;
      }
      case RESTART: {
        Audio.stopSounds();
        Audio.setPaused(false);
        
        super.performAction(token0, token1);
        
        PlayFuncs.restartLevel();
        
        UI.gotoScreen(PlayReadyScreen.INSTANCE);
        
        break;
      }
      case QUIT: {
        Audio.stop();
        
        super.performAction(token0, token1);
        
        if (series.getMode() == SeriesMode.CHALLENGE) {
          UI.gotoScreen(PlayGameOverScreen.INSTANCE);
        } else {
          PlayFuncs.deinitialize();
          
          UI.gotoScreen(LevelSetScreen.INSTANCE);
        }
        
        break;
      }
      case DISABLED: {
        // Playing a new sound while previous sounds are paused works.
        
        super.performAction(token0, token1);
        
        break;
      }
    }
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(t);
    
    hud.draw();
    
    super.paint(t);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (isKey(code, ch, Pref.KEY_PAUSE)) {
      exitRequested();
    } else if (isKey(code, ch, Pref.KEY_RESTART)) {
      if (PlayFuncs.canRestartLevel()) {
        performAction(Action.RESTART);
      }
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.CONTINUE);
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
    hud    = null;
  }
  
  private enum Action {
    CONTINUE,
    RESTART,
    QUIT,
    DISABLED
  }
}
