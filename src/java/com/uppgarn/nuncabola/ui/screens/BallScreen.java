/*
 * BallScreen.java
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
import com.uppgarn.nuncabola.core.renderers.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public final class BallScreen extends MenuScreen {
  public static final BallScreen INSTANCE = new BallScreen();
  
  private boolean replayInitialized;
  
  private int total;
  private int current;
  
  private TextLabel nameLbl;
  
  private BallScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    BallListFuncs  .initialize();
    BackgroundFuncs.initialize();
    
    try {
      ReplayFuncs.initialize(DataFuncs.getSource("gui/ball.nbr"), false);
      
      replayInitialized = true;
    } catch (FuncsException ex) {
      replayInitialized = false;
    }
    
    total   = BallListFuncs.getBallCount();
    current = BallListFuncs.getIndexOfCurrentBall();
    
    super.enter(from);
  }
  
  private void updateNameLabel() {
    nameLbl.setText(BallListFuncs.getName(current));
  }
  
  @Override
  protected void configureGUI(GUI gui) {
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
        lbl.setText("Ball Model");
      }
      
      gui.space(c0);
      
      c1 = gui.hStack(c0);
      
      {
        final char ch00 = '\u25C0', ch01 = '<';
        char       ch0  = GUIHome.canDisplay(Font.SMALL, ch00) ? ch00 : ch01;
        
        Button btn0 = gui.button(c1);
        btn0.setText  (" " + ch0 + " ");
        btn0.setTokens(Action.NAVIGATE, -1);
        
        String[] nameLblStrs = {"xxxxxxxxxxxxxxxxxxxxxxx"};
        
        nameLbl = gui.textLabel(c1, Font.SMALL, nameLblStrs, Corners.ALL, true);
        nameLbl.setTruncation(Truncation.END);
        nameLbl.setColor     (Color.WHITE);
        
        final char ch10 = '\u25B6', ch11 = '>';
        char       ch1  = GUIHome.canDisplay(Font.SMALL, ch10) ? ch10 : ch11;
        
        Button btn1 = gui.button(c1);
        btn1.setText  (" " + ch1 + " ");
        btn1.setTokens(Action.NAVIGATE, +1);
      }
      
      for (int idx = 0; idx < 12; idx++) {
        gui.space(c0);
      }
    }
    
    updateNameLabel();
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case BACK: {
        UI.gotoScreen(OptionsScreen.INSTANCE);
        
        break;
      }
      case NAVIGATE: {
        current = ((current + (Integer) token1) % total + total) % total;
        
        String path = BallListFuncs.getPath(current);
        
        setPref(Pref.BALL_PATH, path);
        
        RendererHome.setBallPath(path);
        
        updateNameLabel();
        nameLbl.setScale(1.2f);
        
        break;
      }
    }
  }
  
  @Override
  public void paint(float t) {
    BackgroundFuncs.draw();
    
    if (replayInitialized) {
      GameFuncs.draw(t, Pose.BALL);
    }
    
    super.paint(t);
  }
  
  @Override
  public void timer(float dt) {
    if (replayInitialized) {
      ReplayFuncs.step(dt);
      
      if (ReplayFuncs.isOver()) {
        ReplayFuncs.repeat();
      }
    }
    
    super.timer(dt);
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.BACK);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (replayInitialized) {
      ReplayFuncs.deinitialize();
    }
    
    BackgroundFuncs.deinitialize();
    BallListFuncs  .deinitialize();
    
    nameLbl = null;
  }
  
  private enum Action {
    BACK,
    NAVIGATE
  }
}
