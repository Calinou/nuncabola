/*
 * PlayCompletionScreen.java
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

import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.series.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.components.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public final class PlayCompletionScreen extends MenuScreen {
  public static final PlayCompletionScreen INSTANCE =
    new PlayCompletionScreen();
  
  private Screen     from;
  private PlaySeries series;
  
  private Scoreboard scoreboard;
  
  private PlayCompletionScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    this.from = from;
    series    = PlayFuncs.getSeries();
    
    if ((from == PlayerScreen.INSTANCE) && PlayerScreen.INSTANCE.getChanged()) {
      series.setPlayer(getStringPref(Pref.PLAYER));
    }
    
    SettingsFuncs.setScoreType(LevelSet.SCORE_TYPES.get(0));
    
    super.enter(from);
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl;
      
      if (series.getTotalScoreResults().isRecord()) {
        lbl = gui.textLabel(c0, Font.MEDIUM);
        lbl.setText ("New Set Record");
        lbl.setColor(Color.GREEN);
      } else {
        lbl = gui.textLabel(c0, Font.LARGE);
        lbl.setText  ("Set Complete");
        lbl.setColors(Color.GREEN, Color.BLUE);
      }
      
      if (from == PlayGoalScreen.INSTANCE) {
        lbl.setScale(1.2f);
      }
      
      gui.space(c0);
      
      scoreboard = new Scoreboard(
        gui,
        c0,
        LevelSet.SCORE_TYPES,
        true,
        Action.SCORE_TYPE,
        true,
        Action.CHANGE_PLAYER);
      scoreboard.setContents(
        LevelSetFuncs.getSetScore().getTables(),
        series.getTotalScoreResults());
      scoreboard.setType(SettingsFuncs.getScoreType());
      
      gui.space(c0);
      
      Button btn = gui.button(c0);
      btn.setText ("Select Level");
      btn.setToken(Action.OK);
      
      gui.setFocusWidget(btn);
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case SCORE_TYPE: {
        ScoreType type = (ScoreType) token1;
        
        SettingsFuncs.setScoreType(type);
        scoreboard.setType(type);
        
        break;
      }
      case NAVIGATE_SCORE_TYPE: {
        ScoreType type = ScoreTypeListTool.getNextWrap(
          LevelSet.SCORE_TYPES,
          SettingsFuncs.getScoreType(),
          (Integer) token1);
        
        SettingsFuncs.setScoreType(type);
        scoreboard.setType(type);
        
        break;
      }
      case CHANGE_PLAYER: {
        PlayerScreen.INSTANCE.setNextScreen(this);
        
        UI.gotoScreen(PlayerScreen.INSTANCE);
        
        break;
      }
      case OK: {
        PlayFuncs.deinitialize();
        
        UI.gotoScreen(LevelSetScreen.INSTANCE);
        
        break;
      }
    }
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(t);
    
    super.paint(t);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (isKey(code, ch, Pref.KEY_SCORE_TYPE_CYCLE)) {
      performAction(Action.NAVIGATE_SCORE_TYPE, +1);
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public void mouseWheel(int wheel) {
    performAction(Action.NAVIGATE_SCORE_TYPE, -wheel);
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.OK);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    series     = null;
    scoreboard = null;
  }
  
  private enum Action {
    SCORE_TYPE,
    NAVIGATE_SCORE_TYPE,
    CHANGE_PLAYER,
    OK
  }
}
