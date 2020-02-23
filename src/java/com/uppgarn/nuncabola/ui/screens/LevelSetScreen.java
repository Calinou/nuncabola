/*
 * LevelSetScreen.java
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
import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.series.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.components.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public final class LevelSetScreen extends MenuScreen {
  private static final int ROW_COUNT    = 5;
  private static final int COLUMN_COUNT = 5;
  
  public static final LevelSetScreen INSTANCE = new LevelSetScreen();
  
  private LevelSet set;
  
  private Button     challengeBtn;
  private ImageLabel shotLbl;
  private Scoreboard scoreboard;
  private Button     lockGoalsBtn;
  private Button     unlockGoalsBtn;
  
  private LevelSetScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    set = LevelSetFuncs.getSet();
    
    super.enter(from);
    
    Audio.fadeToMusic("bgm/inter.ogg", 0.5f);
  }
  
  private void createLevelButton(GUI gui, Container parent, int idx) {
    Level      level      = LevelSetFuncs.getLevel     (idx);
    LevelScore levelScore = LevelSetFuncs.getLevelScore(idx);
    
    boolean accessible = levelScore.getStatus().isAccessible();
    boolean completed  = levelScore.getStatus() == LevelStatus.COMPLETED;
    
    Color color0;
    Color color1;
    
    if (accessible) {
      color0 = level.isBonus() ? Color.GREEN : Color.WHITE;
      color1 = completed       ? color0      : Color.YELLOW;
    } else {
      color0 = null;
      color1 = null;
    }
    
    TextLabel lbl = gui.textLabel(parent);
    lbl.setText  (LevelSetFuncs.getLevelNumber(idx));
    lbl.setColors(color0, color1);
    
    if (accessible || getBooleanPref(Pref.CHEAT)) {
      lbl.setTokens(Action.LEVEL, idx);
    }
  }
  
  private void updateLevelWidgets(int idx) {
    if (idx == -1) {
      // No level, use set information.
      
      shotLbl.setImagePath(set.getShotPath());
      
      scoreboard.setContents(LevelSetFuncs.getSetScore().getTables());
    } else {
      // Level.
      
      Level      level      = LevelSetFuncs.getLevel     (idx);
      LevelScore levelScore = LevelSetFuncs.getLevelScore(idx);
      
      shotLbl.setImagePath(level.getShotPath());
      
      scoreboard.setContents(levelScore.getTables());
    }
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    if (!LevelSetFuncs.hasLoadedLevels()) {
      Container c0 = gui.vStack(null);
      
      {
        TextLabel lbl = gui.textLabel(c0, Font.MEDIUM);
        lbl.setText("No Levels");
        
        gui.space(c0);
        
        Button btn = gui.button(c0);
        btn.setText ("Back");
        btn.setToken(Action.BACK);
        
        gui.setFocusWidget(btn);
      }
      
      return;
    }
    
    Container c0 = gui.vStack(null);
    
    {
      Container c1 = gui.hStack(c0);
      
      {
        Button btn = gui.button(c1);
        btn.setText ("Back");
        btn.setToken(Action.BACK);
        
        gui.setFocusWidget(btn);
        
        gui.space(c1, true);
        
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText(set.getName());
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        Container c2 = gui.vArray(c1);
        
        {
          for (int row = 0, idx = 0; row < ROW_COUNT; row++) {
            Container c3 = gui.hArray(c2);
            
            for (int col = 0; col < COLUMN_COUNT; col++) {
              if (idx != -1) {
                idx = LevelSetFuncs.getNextLoadedLevelIndex(idx);
              }
              
              if (idx != -1) {
                createLevelButton(gui, c3, idx++);
              } else {
                gui.textLabel(c3);
              }
            }
          }
          
          challengeBtn = gui.button(c2);
          challengeBtn.setText ("Challenge");
          challengeBtn.setToken(Action.CHALLENGE);
        }
        
        shotLbl = gui.imageLabel(c1, 0.425f, 0.425f);
        shotLbl.setImagePath(set.getShotPath());
      }
      
      gui.space(c0);
      
      scoreboard = new Scoreboard(
        gui,
        c0,
        Level.SCORE_TYPES,
        false,
        Action.SCORE_TYPE,
        false,
        null);
      scoreboard.setType(SettingsFuncs.getScoreType());
      
      gui.space(c0);
      
      c1 = gui.hStack(c0);
      
      {
        gui.filler(c1);
        
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Goal State in Completed Levels");
        
        Container c2 = gui.hArray(c1);
        
        {
          boolean lock = getBooleanPref(Pref.LOCK_GOALS);
          
          lockGoalsBtn = gui.button(c2);
          lockGoalsBtn.setText    ("Locked");
          lockGoalsBtn.setTokens  (Action.LOCK_GOALS, true);
          lockGoalsBtn.setSelected(lock);
          
          unlockGoalsBtn = gui.button(c2);
          unlockGoalsBtn.setText    ("Unlocked");
          unlockGoalsBtn.setTokens  (Action.LOCK_GOALS, false);
          unlockGoalsBtn.setSelected(!lock);
        }
        
        gui.filler(c1);
      }
    }
  }
  
  @Override
  protected void performFocus(Widget widget) {
    super.performFocus(widget);
    
    switch ((Action) widget.getToken0()) {
      case BACK:
      case CHALLENGE: {
        updateLevelWidgets(-1);
        
        break;
      }
      case LEVEL: {
        updateLevelWidgets((Integer) widget.getToken1());
        
        break;
      }
      
      default: {
        break;
      }
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case BACK: {
        LevelSetFuncs.deinitialize();
        
        UI.gotoScreen(LevelSetListScreen.INSTANCE);
        
        break;
      }
      case LEVEL: {
        SeriesMode mode;
        boolean    normalUnlockGoals;
        
        if (challengeBtn.isSelected()) {
          mode              = SeriesMode.CHALLENGE;
          normalUnlockGoals = false;
        } else {
          mode              = SeriesMode.NORMAL;
          normalUnlockGoals = !getBooleanPref(Pref.LOCK_GOALS);
        }
        
        try {
          PlayFuncs.initialize(mode, normalUnlockGoals, (Integer) token1);
          
          UI.gotoScreen(PlayIntroScreen.INSTANCE);
        } catch (FuncsException ex) {
          // Stay in this screen.
        }
        
        break;
      }
      case CHALLENGE: {
        if (!getBooleanPref(Pref.CHEAT)) {
          try {
            PlayFuncs.initialize(
              SeriesMode.CHALLENGE,
              false,
              LevelSetFuncs.getNextLoadedLevelIndex(0));
            
            UI.gotoScreen(PlayIntroScreen.INSTANCE);
          } catch (FuncsException ex) {
            // Stay in this screen.
          }
        } else {
          challengeBtn.setSelected(!challengeBtn.isSelected());
        }
        
        break;
      }
      case SCORE_TYPE: {
        ScoreType type = (ScoreType) token1;
        
        SettingsFuncs.setScoreType(type);
        scoreboard.setType(type);
        
        break;
      }
      case NAVIGATE_SCORE_TYPE: {
        ScoreType type = ScoreTypeListTool.getNextWrap(
          Level.SCORE_TYPES,
          SettingsFuncs.getScoreType(),
          (Integer) token1);
        
        SettingsFuncs.setScoreType(type);
        scoreboard.setType(type);
        
        break;
      }
      case LOCK_GOALS: {
        boolean lock = (Boolean) token1;
        
        setPref(Pref.LOCK_GOALS, lock);
        
        lockGoalsBtn  .setSelected( lock);
        unlockGoalsBtn.setSelected(!lock);
        
        break;
      }
      case COMPLETE_LEVELS: {
        LevelSetFuncs.completeLevels();
        
        UI.gotoScreen(this);
        
        break;
      }
      case CREATE_SCREENSHOTS: {
        LevelSetFuncs.createScreenshots();
        
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
    if (!LevelSetFuncs.hasLoadedLevels()) {
      super.keyDown(code, ch);
      
      return;
    }
    
    if (isKey(code, ch, Pref.KEY_SCORE_TYPE_CYCLE)) {
      performAction(Action.NAVIGATE_SCORE_TYPE, +1);
    } else if (Character.toLowerCase(ch) == 'c') {
      if (getBooleanPref(Pref.CHEAT)) {
        performAction(Action.COMPLETE_LEVELS);
      }
    } else if (code == Keyboard.KEY_F8) {
      if (getBooleanPref(Pref.CHEAT)) {
        performAction(Action.CREATE_SCREENSHOTS);
      }
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public void mouseWheel(int wheel) {
    if (!LevelSetFuncs.hasLoadedLevels()) {
      return;
    }
    
    performAction(Action.NAVIGATE_SCORE_TYPE, -wheel);
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.BACK);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    set            = null;
    challengeBtn   = null;
    shotLbl        = null;
    scoreboard     = null;
    lockGoalsBtn   = null;
    unlockGoalsBtn = null;
  }
  
  private enum Action {
    BACK,
    LEVEL,
    CHALLENGE,
    SCORE_TYPE,
    NAVIGATE_SCORE_TYPE,
    LOCK_GOALS,
    COMPLETE_LEVELS,
    CREATE_SCREENSHOTS
  }
}
