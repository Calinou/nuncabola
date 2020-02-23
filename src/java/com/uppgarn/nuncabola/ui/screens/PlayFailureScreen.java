/*
 * PlayFailureScreen.java
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

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public abstract class PlayFailureScreen extends MenuScreen {
  private Screen     from;
  private PlaySeries series;
  
  @Override
  public final void enter(Screen from) {
    this.from = from;
    series    = PlayFuncs.getSeries();
    
    super.enter(from);
    
    Audio.fadeOutMusic(2.0f);
  }
  
  protected abstract String getMessage();
  
  @Override
  protected final void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl = gui.textLabel(c0, Font.LARGE);
      lbl.setText  (getMessage());
      lbl.setColors(Color.RED, Color.GRAY);
      
      if (from == PlayMainScreen.INSTANCE) {
        lbl.setScale(1.2f);
      }
      
      gui.space(c0);
      
      Container c1 = gui.hArray(c0);
      
      {
        Button btn0 = gui.button(c1);
        btn0.setText("Save Replay");
        
        if (ReplayFileFuncs.gameReplayExists()) {
          btn0.setToken(Action.SAVE_REPLAY);
        } else {
          btn0.setColor(Color.GRAY);
          btn0.setToken(Action.DISABLED);
        }
        
        Button btn1 = gui.button(c1);
        btn1.setText("Retry Level");
        
        if (PlayFuncs.canRetryLevel()) {
          btn1.setToken(Action.RETRY_LEVEL);
          
          gui.setFocusWidget(btn1);
        } else {
          btn1.setColor(Color.GRAY);
          btn1.setToken(Action.DISABLED);
        }
        
        Button btn2 = gui.button(c1);
        
        if (PlayFuncs.canRetryLevel()) {
          btn2.setText("Next Level");
          
          if (PlayFuncs.canPlayNextLevel()) {
            btn2.setToken(Action.NEXT_LEVEL);
          } else {
            btn2.setColor(Color.GRAY);
            btn2.setToken(Action.DISABLED);
          }
        } else {
          btn2.setText ("Quit");
          btn2.setToken(Action.QUIT);
          
          gui.setFocusWidget(btn2);
        }
      }
    }
  }
  
  @Override
  protected final void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case SAVE_REPLAY: {
        PlayFuncs.stopRecording();
        
        PlaySaveScreen.INSTANCE.setNextScreen(this);
        
        UI.gotoScreen(PlaySaveScreen.INSTANCE);
        
        break;
      }
      case RETRY_LEVEL: {
        PlayFuncs.retryLevel();
        
        UI.gotoScreen(PlayIntroScreen.INSTANCE);
        
        break;
      }
      case NEXT_LEVEL: {
        try {
          PlayFuncs.playNextLevel();
          
          UI.gotoScreen(PlayIntroScreen.INSTANCE);
        } catch (FuncsException ex) {
          PlayFuncs.deinitialize();
          
          UI.gotoScreen(LevelSetScreen.INSTANCE);
        }
        
        break;
      }
      case QUIT: {
        if (series.getMode() == SeriesMode.CHALLENGE) {
          UI.gotoScreen(PlayGameOverScreen.INSTANCE);
        } else {
          PlayFuncs.deinitialize();
          
          UI.gotoScreen(LevelSetScreen.INSTANCE);
        }
        
        break;
      }
      case DISABLED: {
        break;
      }
    }
  }
  
  @Override
  public final void paint(float t) {
    GameFuncs.draw(t);
    
    super.paint(t);
  }
  
  @Override
  public final void timer(float dt) {
    if (from == PlayMainScreen.INSTANCE) {
      PlayFuncs.step(dt);
    }
    
    super.timer(dt);
  }
  
  @Override
  public final void keyDown(int code, char ch) {
    if (isKey(code, ch, Pref.KEY_RESTART)) {
      // In the goal and failure screens, the restart key maps to
      // retrying rather than restarting (which is no longer possible).
      // However, unlike the "Retry Level" button, it will take the
      // player directly to the "Ready?" screen as it would during play.
      
      if (PlayFuncs.canRetryLevel()) {
        Audio.stopSounds();
        Audio.fadeToMusic(series.getLevel().getMusicPath(), 2.0f);
        
        PlayFuncs.retryLevel();
        
        UI.gotoScreen(PlayReadyScreen.INSTANCE);
      }
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public final void exitRequested() {
    performAction(Action.QUIT);
  }
  
  @Override
  public final void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    series = null;
  }
  
  private enum Action {
    SAVE_REPLAY,
    RETRY_LEVEL,
    NEXT_LEVEL,
    QUIT,
    DISABLED
  }
}
