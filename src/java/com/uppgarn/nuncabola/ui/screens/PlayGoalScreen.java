/*
 * PlayGoalScreen.java
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

public final class PlayGoalScreen extends MenuScreen {
  public static final PlayGoalScreen INSTANCE = new PlayGoalScreen();
  
  private Screen     from;
  private PlaySeries series;
  
  private int coins;
  private int totalCoins;
  private int balls;
  
  private float animTime;
  
  private NumberLabel coinsLbl;
  private NumberLabel totalCoinsLbl;
  private NumberLabel ballsLbl;
  private Scoreboard  scoreboard;
  
  private PlayGoalScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    this.from = from;
    series    = PlayFuncs.getSeries();
    
    if ((from == PlayerScreen.INSTANCE) && PlayerScreen.INSTANCE.getChanged()) {
      String player = getStringPref(Pref.PLAYER);
      
      series.setPlayer(player);
      
      if (ReplayFileFuncs.gameReplayExists()) {
        ReplayFileFuncs.changeGameReplayPlayer(player);
      }
    }
    
    if (from == PlayMainScreen.INSTANCE) {
      coins      = series.getCoins();
      totalCoins = series.getTotalCoins();
      balls      = series.getBalls();
    } else {
      coins      = 0;
      totalCoins = series.getNewTotalCoins();
      balls      = series.getNewBalls();
    }
    
    animTime = 0.0f;
    
    super.enter(from);
    
    Audio.fadeOutMusic(2.0f);
  }
  
  private void createBonusLevelButton(GUI gui, Container parent, int idx) {
    LevelScore levelScore = LevelSetFuncs.getLevelScore(idx);
    
    boolean accessible = levelScore.getStatus().isAccessible();
    
    TextLabel lbl = gui.textLabel(parent);
    lbl.setText (LevelSetFuncs.getLevelNumber(idx));
    lbl.setColor(accessible ? Color.GREEN : Color.GRAY);
  }
  
  private void playOutstandingExtraBallSound() {
    if ((series.getMode() == SeriesMode.CHALLENGE)
        && (balls < series.getNewBalls())) {
      Audio.playSound("snd/ball.ogg");
    }
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    int          levelIdx   = series.getLevelIndex();
    LevelScore   levelScore = LevelSetFuncs.getLevelScore(levelIdx);
    ScoreResults results    = series.getScoreResults();
    
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl;
      
      if (results.isRecord()) {
        lbl = gui.textLabel(c0, Font.MEDIUM);
        lbl.setText ("New Record");
        lbl.setColor(Color.GREEN);
      } else {
        lbl = gui.textLabel(c0, Font.LARGE);
        lbl.setText  ("GOAL");
        lbl.setColors(Color.GREEN, Color.BLUE);
      }
      
      if (from == PlayMainScreen.INSTANCE) {
        lbl.setScale(1.2f);
      }
      
      gui.space(c0);
      
      if (series.getMode() == SeriesMode.CHALLENGE) {
        Container c1 = gui.hStack(c0, Corners.ALL);
        
        {
          gui.filler(c1);
          
          Container c2 = gui.vStack(c1);
          
          {
            Container c3 = gui.hStack(c2);
            
            {
              TextLabel lbl0 = gui.textLabel(c3);
              lbl0.setText ("Coins");
              lbl0.setColor(Color.WHITE);
              
              coinsLbl = gui.numberLabel(c3, Font.MEDIUM, 1000);
              coinsLbl.setNumber(coins);
              
              TextLabel lbl1 = gui.textLabel(c3);
              lbl1.setText ("Score");
              lbl1.setColor(Color.WHITE);
              
              totalCoinsLbl = gui.numberLabel(c3, Font.MEDIUM, 1000);
              totalCoinsLbl.setNumber(totalCoins);
              
              TextLabel lbl2 = gui.textLabel(c3);
              lbl2.setText ("Balls");
              lbl2.setColor(Color.WHITE);
              
              ballsLbl = gui.numberLabel(c3, Font.MEDIUM, 100);
              ballsLbl.setNumber(balls);
            }
            
            c3 = gui.hArray(c2);
            
            {
              gui.textLabel(c3);
              
              for (int idx = 0;
                  (idx = LevelSetFuncs.getNextLoadedLevelIndex(idx)) != -1;
                  idx++) {
                if (LevelSetFuncs.getLevel(idx).isBonus()) {
                  createBonusLevelButton(gui, c3, idx);
                }
              }
              
              gui.textLabel(c3);
            }
          }
          
          gui.filler(c1);
        }
        
        gui.space(c0);
      }
      
      scoreboard = new Scoreboard(
        gui,
        c0,
        Level.SCORE_TYPES,
        true,
        Action.SCORE_TYPE,
        results.isRecord(),
        Action.CHANGE_PLAYER);
      scoreboard.setContents(levelScore.getTables(), results);
      scoreboard.setType    (SettingsFuncs.getScoreType());
      
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
        btn1.setText ("Retry Level");
        btn1.setToken(Action.RETRY_LEVEL);
        
        gui.setFocusWidget(btn1);
        
        Button btn2 = gui.button(c1);
        
        if (PlayFuncs.canPlayNextLevel()) {
          btn2.setText ("Next Level");
          btn2.setToken(Action.NEXT_LEVEL);
        } else {
          btn2.setText ("Finish");
          btn2.setToken(Action.FINISH);
        }
      }
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
          Level.SCORE_TYPES,
          SettingsFuncs.getScoreType(),
          (Integer) token1);
        
        SettingsFuncs.setScoreType(type);
        scoreboard.setType(type);
        
        break;
      }
      case CHANGE_PLAYER: {
        playOutstandingExtraBallSound();
        
        PlayFuncs.stopRecording();
        
        PlayerScreen.INSTANCE.setNextScreen(this);
        
        UI.gotoScreen(PlayerScreen.INSTANCE);
        
        break;
      }
      case SAVE_REPLAY: {
        playOutstandingExtraBallSound();
        
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
        playOutstandingExtraBallSound();
        
        try {
          PlayFuncs.playNextLevel();
          
          UI.gotoScreen(PlayIntroScreen.INSTANCE);
        } catch (FuncsException ex) {
          PlayFuncs.deinitialize();
          
          UI.gotoScreen(LevelSetScreen.INSTANCE);
        }
        
        break;
      }
      case FINISH: {
        playOutstandingExtraBallSound();
        
        PlayFuncs.finish();
        
        if (series.getMode() == SeriesMode.CHALLENGE) {
          UI.gotoScreen(PlayCompletionScreen.INSTANCE);
        } else {
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
  public void paint(float t) {
    GameFuncs.draw(t);
    
    super.paint(t);
  }
  
  @Override
  public void timer(float dt) {
    if (from == PlayMainScreen.INSTANCE) {
      PlayFuncs.step(dt);
      
      if (series.getMode() == SeriesMode.CHALLENGE) {
        animTime += dt;
        
        if (animTime >= 0.05f) {
          if (coins > 0) {
            coins--;
            totalCoins++;
            
            coinsLbl.setNumber(coins);
            coinsLbl.setScale (1.1f);
            
            totalCoinsLbl.setNumber(totalCoins);
            totalCoinsLbl.setScale (1.1f);
            
            if (totalCoins % Series.EXTRA_BALL_MARK == 0) {
              balls++;
              
              ballsLbl.setNumber(balls);
              ballsLbl.setScale (2.0f);
              
              Audio.playSound("snd/ball.ogg");
            }
          }
          
          animTime = 0.0f;
        }
      }
    }
    
    super.timer(dt);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (isKey(code, ch, Pref.KEY_RESTART)) {
      // In the goal and failure screens, the restart key maps to
      // retrying rather than restarting (which is no longer possible).
      // However, unlike the "Retry Level" button, it will take the
      // player directly to the "Ready?" screen as it would during play.
      
      Audio.fadeToMusic(series.getLevel().getMusicPath(), 2.0f);
      
      PlayFuncs.retryLevel();
      
      UI.gotoScreen(PlayReadyScreen.INSTANCE);
    } else if (isKey(code, ch, Pref.KEY_SCORE_TYPE_CYCLE)) {
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
    performAction(Action.QUIT);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    series        = null;
    coinsLbl      = null;
    totalCoinsLbl = null;
    ballsLbl      = null;
    scoreboard    = null;
  }
  
  private enum Action {
    SCORE_TYPE,
    NAVIGATE_SCORE_TYPE,
    CHANGE_PLAYER,
    SAVE_REPLAY,
    RETRY_LEVEL,
    NEXT_LEVEL,
    FINISH,
    QUIT,
    DISABLED
  }
}
