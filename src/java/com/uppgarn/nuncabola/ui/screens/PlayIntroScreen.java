/*
 * PlayIntroScreen.java
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
import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.core.series.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public final class PlayIntroScreen extends GUIScreen {
  public static final PlayIntroScreen INSTANCE = new PlayIntroScreen();
  
  private Screen     from;
  private PlaySeries series;
  
  private PlayIntroScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    this.from = from;
    series    = PlayFuncs.getSeries();
    
    if (from != PlayPoseScreen.INSTANCE) {
      GameFlyer gameFlyer = new GameFlyer(
        GameFuncs.getGame(),
        GameFuncs.getViewDistance());
      gameFlyer.fly(1.0f);
      
      FadeFuncs.initialize(FadeFuncs.Mode.IN, 0.5f);
    }
    
    super.enter(from);
    
    Audio.fadeToMusic(series.getLevel().getMusicPath(), 2.0f);
    
    // Ensure all relevant screens are loaded to avoid delays while playing.
    
    PlayReadyScreen  .INSTANCE.hashCode();
    PlaySetScreen    .INSTANCE.hashCode();
    PlayMainScreen   .INSTANCE.hashCode();
    PlayGoalScreen   .INSTANCE.hashCode();
    PlayFallOutScreen.INSTANCE.hashCode();
    PlayTimeOutScreen.INSTANCE.hashCode();
    
    gc();
  }
  
  private void goToPlayReadyScreen() {
    UI.gotoScreen(PlayReadyScreen.INSTANCE);
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      Container c1 = gui.hStack(c0);
      
      {
        gui.filler(c1);
        
        Container c2 = gui.vStack(c1, Corners.ALL);
        
        {
          boolean bonus  = series.getLevel().isBonus();
          String  number = LevelSetFuncs.getLevelNumber(series.getLevelIndex());
          
          String levelStr;
          Font   font;
          Color  color0;
          Color  color1;
          
          if (!bonus) {
            levelStr = "Level "      .concat(number);
            font     = Font.LARGE;
            color0   = null;
            color1   = null;
          } else {
            levelStr = "Bonus Level ".concat(number);
            font     = Font.MEDIUM;
            color0   = Color.GREEN;
            color1   = Color.WHITE;
          }
          
          TextLabel lbl0 = gui.textLabel(c2, font);
          lbl0.setText  (levelStr);
          lbl0.setColors(color0, color1);
          
          String setStr = LevelSetFuncs.getSet().getName();
          if (series.getMode() == SeriesMode.CHALLENGE) {
            setStr = setStr.concat(": Challenge Mode");
          }
          
          TextLabel lbl1 = gui.textLabel(c2);
          lbl1.setText (setStr);
          lbl1.setColor(Color.WHITE);
        }
        
        gui.filler(c1);
      }
      
      String msg = series.getLevel().getMessage();
      
      if (!msg.isEmpty()) {
        gui.space(c0);
        
        MultiTextLabel lbl = gui.multiTextLabel(c0);
        lbl.setText (msg);
        lbl.setColor(Color.WHITE);
      }
    }
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(t);
    
    super.paint(t);
  }
  
  @Override
  public void timer(float dt) {
    if (from != PlayPoseScreen.INSTANCE) {
      FadeFuncs.step(dt);
    }
    
    super.timer(dt);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (code == Keyboard.KEY_RETURN) {
      goToPlayReadyScreen();
    } else if (code == Keyboard.KEY_F10) {
      UI.gotoScreen(PlayPoseScreen.INSTANCE);
    }
  }
  
  @Override
  public void mouseDown(int button) {
    if (button == 0) {
      goToPlayReadyScreen();
    }
  }
  
  @Override
  public void controllerDown(int button) {
    if (button == getIntPref(Pref.CONTROLLER_BUTTON_A)) {
      goToPlayReadyScreen();
    }
  }
  
  @Override
  public void exitRequested() {
    if (series.getMode() == SeriesMode.CHALLENGE) {
      UI.gotoScreen(PlayGameOverScreen.INSTANCE);
    } else {
      PlayFuncs.deinitialize();
      
      UI.gotoScreen(LevelSetScreen.INSTANCE);
    }
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (from != PlayPoseScreen.INSTANCE) {
      FadeFuncs.deinitialize();
    }
    
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    series = null;
  }
}
