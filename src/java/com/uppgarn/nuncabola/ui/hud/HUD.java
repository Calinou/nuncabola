/*
 * HUD.java
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

package com.uppgarn.nuncabola.ui.hud;

import com.uppgarn.nuncabola.core.audio.*;
import com.uppgarn.nuncabola.core.fps.*;
import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.core.replay.*;
import com.uppgarn.nuncabola.core.series.*;
import com.uppgarn.nuncabola.preferences.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import java.util.*;

public final class HUD {
  private static final Map<Camera, String> CAMERA_STRS =
    createCameraStrings();
  private static final Map<Speed,  char[]> SPEED_CHS   =
    createSpeedCharacters();
  
  private static Map<Camera, String> createCameraStrings() {
    Map<Camera, String> strs = new EnumMap<>(Camera.class);
    
    strs.put(Camera.CHASE,  "Chase");
    strs.put(Camera.LAZY,   "Lazy");
    strs.put(Camera.MANUAL, "Manual");
    
    return strs;
  }
  
  private static Map<Speed, char[]> createSpeedCharacters() {
    Map<Speed, char[]> chs = new EnumMap<>(Speed.class);
    
    chs.put(Speed.SLOWEST, new char[] {'\u2791', '8'});
    chs.put(Speed.SLOWER,  new char[] {'\u278D', '4'});
    chs.put(Speed.SLOW,    new char[] {'\u278B', '2'});
    chs.put(Speed.NORMAL,  new char[] {'\u278A', '1'});
    chs.put(Speed.FAST,    new char[] {'\u278B', '2'});
    chs.put(Speed.FASTER,  new char[] {'\u278D', '4'});
    chs.put(Speed.FASTEST, new char[] {'\u2791', '8'});
    
    return chs;
  }
  
  private final FPSCounter fpsCounter;
  
  private GUI         fpsGUI;
  private NumberLabel fpsLbl;
  
  private GUI       cameraGUI;
  private TextLabel cameraLbl;
  
  private GUI         speedGUI;
  private TextLabel[] speedLbls;
  
  private GUI         challengeGUI;
  private NumberLabel ballsLbl;
  private NumberLabel totalCoinsLbl;
  
  private GUI       timerGUI;
  private TimeLabel timerLbl;
  
  private GUI         coinsGUI;
  private NumberLabel coinsLbl;
  private NumberLabel goalLbl;
  
  private boolean visible;
  
  private float   cameraTimer;
  private float   speedTimer;
  private boolean gameGUIsVisible;
  private boolean challengeGUIVisible;
  
  public HUD(FPSCounter fpsCounter) {
    this.fpsCounter = fpsCounter;
    
    createFPSGUI();
    createCameraGUI();
    createSpeedGUI();
    createChallengeGUI();
    createTimerGUI();
    createCoinsGUI();
    
    visible = true;
    
    cameraTimer         = 0.0f;
    speedTimer          = 0.0f;
    gameGUIsVisible     = false;
    challengeGUIVisible = false;
  }
  
  private void createFPSGUI() {
    fpsGUI = new GUI();
    fpsGUI.setXPosition(-1);
    fpsGUI.setYPosition(+1);
    
    {
      fpsLbl = fpsGUI.numberLabel(null, Font.SMALL, 1000, Corners.SE);
    }
    
    fpsGUI.build();
  }
  
  private void createCameraGUI() {
    cameraGUI = new GUI();
    cameraGUI.setXPosition(+1);
    cameraGUI.setYPosition(+1);
    
    {
      String[] cameraLblStrs = CAMERA_STRS.values().toArray(
        new String[CAMERA_STRS.size()]);
      
      cameraLbl = cameraGUI.textLabel(
        null,
        Font.SMALL,
        cameraLblStrs,
        Corners.SW);
      cameraLbl.setColor(Color.WHITE);
    }
    
    cameraGUI.build();
  }
  
  private void createSpeedGUI() {
    speedGUI = new GUI();
    speedGUI.setXPosition(+1);
    speedGUI.setYPosition( 0);
    
    {
      Container c0 = speedGUI.vArray(null, Corners.LEFT);
      
      speedLbls = new TextLabel[Speed.MOTION_VALUES.size()];
      
      for (int idx = Speed.MOTION_VALUES.size() - 1; idx >= 0; idx--) {
        Speed speed = Speed.MOTION_VALUES.get(idx);
        
        char[] chs = SPEED_CHS.get(speed);
        char   ch  = GUIHome.canDisplay(Font.SMALL, chs[0]) ? chs[0] : chs[1];
        
        speedLbls[idx] = speedGUI.textLabel(c0);
        speedLbls[idx].setText(Character.toString(ch));
      }
    }
    
    speedGUI.build();
  }
  
  private void createChallengeGUI() {
    challengeGUI = new GUI();
    challengeGUI.setXPosition(-1);
    challengeGUI.setYPosition(-1);
    
    {
      Container c0 = challengeGUI.hStack(null, Corners.NE);
      
      {
        Container c1 = challengeGUI.vStack(c0);
        
        {
          TextLabel lbl0 = challengeGUI.textLabel(c1);
          lbl0.setText ("Balls");
          lbl0.setColor(Color.WHITE);
          
          TextLabel lbl1 = challengeGUI.textLabel(c1);
          lbl1.setText ("Score");
          lbl1.setColor(Color.WHITE);
        }
        
        c1 = challengeGUI.vStack(c0);
        
        {
          ballsLbl      = challengeGUI.numberLabel(c1, Font.SMALL, 10);
          totalCoinsLbl = challengeGUI.numberLabel(c1, Font.SMALL, 1000);
        }
      }
    }
    
    challengeGUI.build();
  }
  
  private void createTimerGUI() {
    timerGUI = new GUI();
    timerGUI.setXPosition( 0);
    timerGUI.setYPosition(-1);
    
    {
      timerLbl = timerGUI.timeLabel(null, Font.MEDIUM, Corners.TOP);
    }
    
    timerGUI.build();
  }
  
  private void createCoinsGUI() {
    coinsGUI = new GUI();
    coinsGUI.setXPosition(+1);
    coinsGUI.setYPosition(-1);
    
    {
      Container c0 = coinsGUI.hStack(null, Corners.NW);
      
      {
        Container c1 = coinsGUI.vStack(c0);
        
        {
          coinsLbl = coinsGUI.numberLabel(c1, Font.SMALL, 100);
          goalLbl  = coinsGUI.numberLabel(c1, Font.SMALL, 100);
        }
        
        c1 = coinsGUI.vStack(c0);
        
        {
          TextLabel lbl0 = coinsGUI.textLabel(c1);
          lbl0.setText ("Coins");
          lbl0.setColor(Color.WHITE);
          
          TextLabel lbl1 = coinsGUI.textLabel(c1);
          lbl1.setText ("Goal");
          lbl1.setColor(Color.WHITE);
        }
      }
    }
    
    coinsGUI.build();
  }
  
  public void draw() {
    if (!visible) {
      return;
    }
    
    if (cameraTimer > 0.0f) {
      cameraGUI.draw();
    }
    if (speedTimer > 0.0f) {
      speedGUI.draw();
    }
    
    if (gameGUIsVisible) {
      if (getBooleanPref(Pref.FPS)) {
        fpsLbl.setNumber(fpsCounter.getFPS());
        
        fpsGUI.draw();
      }
      
      if (challengeGUIVisible) {
        challengeGUI.draw();
      }
      
      timerGUI.draw();
      coinsGUI.draw();
    }
  }
  
  public void step(float dt) {
    cameraGUI.step(dt);
    speedGUI .step(dt);
    timerGUI .step(dt);
    coinsGUI .step(dt);
    
    cameraTimer -= dt;
    speedTimer  -= dt;
  }
  
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
  
  public void toggleVisible() {
    visible = !visible;
  }
  
  private void setBalls(int balls) {
    ballsLbl.setNumber(balls);
  }
  
  private void setTotalCoins(int totalCoins) {
    totalCoinsLbl.setNumber(totalCoins);
  }
  
  private void setTimer(int timer, boolean pulse) {
    int last = timerLbl.getTime();
    
    timerLbl.setTime(timer);
    
    if (!pulse) {
      timerLbl.setScale(1.0f);
    } else if (timer < last) {
      if ((timer <= 1000) && (timer / 100 < last / 100)) {
        timerLbl.setScale(1.50f);
        
        Audio.playSound("snd/tick.ogg");
      } else if ((timer < 500) && (timer / 50 < last / 50)) {
        timerLbl.setScale(1.25f);
        
        Audio.playSound("snd/tock.ogg");
      }
    }
  }
  
  private void setCoins(int coins, boolean pulse) {
    int diff = coins - coinsLbl.getNumber();
    
    coinsLbl.setNumber(coins);
    
    if (!pulse) {
      coinsLbl.setScale(1.0f);
    } else if (diff > 0) {
      if (diff >= 10) {
        coinsLbl.setScale(2.00f);
      } else if (diff >= 5) {
        coinsLbl.setScale(1.50f);
      } else {
        coinsLbl.setScale(1.25f);
      }
    }
  }
  
  private void setGoal(int goal, boolean pulse) {
    int diff = goalLbl.getNumber() - goal;
    
    goalLbl.setNumber(goal);
    
    if (!pulse) {
      goalLbl.setScale(1.0f);
    } else if (diff > 0) {
      if ((goal == 0) || (diff >= 10)) {
        goalLbl.setScale(2.00f);
      } else if (diff >= 5) {
        goalLbl.setScale(1.50f);
      } else  {
        goalLbl.setScale(1.25f);
      }
    }
  }
  
  public void setContents(Series series, boolean pulse) {
    if (series == null) {
      gameGUIsVisible     = false;
      challengeGUIVisible = false;
      
      setBalls     (0);
      setTotalCoins(0);
      setTimer     (0, false);
      setCoins     (0, false);
      setGoal      (0, false);
    } else {
      gameGUIsVisible     = true;
      challengeGUIVisible = series.getMode() == SeriesMode.CHALLENGE;
      
      setBalls     (series.getBalls());
      setTotalCoins(series.getTotalCoins());
      setTimer     (series.getTimer(), pulse);
      setCoins     (series.getCoins(), pulse);
      setGoal      (series.getGoal (), pulse);
    }
  }
  
  public void setCamera(Camera camera) {
    if (camera == null) {
      cameraTimer = 0.0f;
    } else {
      cameraLbl.setText (CAMERA_STRS.get(camera));
      cameraLbl.setScale(1.2f);
      
      cameraTimer = 2.0f;
    }
  }
  
  private Color getSpeedLabelColor(Speed lblSpeed, Speed speed) {
    if (speed != Speed.NONE) {
      if (lblSpeed.isSlowerThan(Speed.NORMAL)) {
        if (!speed.isFasterThan(lblSpeed)) {
          return Color.RED;
        }
      } else if (lblSpeed.isFasterThan(Speed.NORMAL)) {
        if (!speed.isSlowerThan(lblSpeed)) {
          return Color.GREEN;
        }
      } else {
        return Color.WHITE;
      }
    }
    
    return Color.GRAY;
  }
  
  public void setSpeed(Speed speed) {
    if (speed == null) {
      speedTimer = 0.0f;
    } else {
      for (int idx = 0; idx < Speed.MOTION_VALUES.size(); idx++) {
        Speed lblSpeed = Speed.MOTION_VALUES.get(idx);
        
        speedLbls[idx].setColor(getSpeedLabelColor(lblSpeed, speed));
        
        if (lblSpeed == speed) {
          speedLbls[idx].setScale(1.2f);
        }
      }
      
      speedTimer = 2.0f;
    }
  }
  
  public void deinitialize() {
    fpsGUI      .deinitialize();
    cameraGUI   .deinitialize();
    speedGUI    .deinitialize();
    challengeGUI.deinitialize();
    timerGUI    .deinitialize();
    coinsGUI    .deinitialize();
  }
}
