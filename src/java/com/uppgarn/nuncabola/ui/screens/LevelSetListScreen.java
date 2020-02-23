/*
 * LevelSetListScreen.java
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
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.components.*;

public final class LevelSetListScreen extends MenuScreen {
  private static final int PAGE_SIZE = 6;
  
  public static final LevelSetListScreen INSTANCE = new LevelSetListScreen();
  
  private int first;
  
  private int total;
  
  private Navigation     nav;
  private ImageLabel     shotLbl;
  private MultiTextLabel descLbl;
  
  private LevelSetListScreen() {
    first = 0;
  }
  
  @Override
  public void enter(Screen from) {
    if ((from == TitleScreen.INSTANCE) || (from == PlayerScreen.INSTANCE)) {
      LevelSetListFuncs.initialize();
      
      total = LevelSetListFuncs.getSetCount();
      first = (Math.min(first, Math.max(0, total - 1)) / PAGE_SIZE) * PAGE_SIZE;
    }
    
    super.enter(from);
    
    Audio.fadeToMusic("bgm/inter.ogg", 0.5f);
    
    if ((from != this) && (total > 0)) {
      Audio.playSound("snd/select.ogg");
    }
  }
  
  private void createSetButton(GUI gui, Container parent, int idx) {
    LevelSet set = LevelSetListFuncs.getSet(idx);
    
    String[] btnStrs = {""};
    
    Button btn = gui.button(parent, Font.SMALL, btnStrs);
    btn.setTruncation(Truncation.END);
    btn.setText      (set.getName());
    btn.setTokens    (Action.LEVEL_SET, set);
  }
  
  private void updateSetWidgets(LevelSet set) {
    shotLbl.setImagePath(set.getShotPath());
    descLbl.setText     (set.getDescription());
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    if (total == 0) {
      Container c0 = gui.vStack(null);
      
      {
        TextLabel lbl = gui.textLabel(c0, Font.MEDIUM);
        lbl.setText("No Level Sets");
        
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
        
        gui.space(c1);
        
        nav = new Navigation(
          gui,
          c1,
          Action.NAVIGATE,
          Action.DISABLED,
          first,
          total,
          PAGE_SIZE);
        
        gui.space(c1, true);
        
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Level Set");
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        Container c2 = gui.vArray(c1);
        
        for (int idx = first; idx < first + PAGE_SIZE; idx++) {
          if (idx < total) {
            createSetButton(gui, c2, idx);
          } else {
            gui.textLabel(c2);
          }
        }
        
        shotLbl = gui.imageLabel(c1, 0.425f, 0.425f);
        shotLbl.setImagePath(LevelSetListFuncs.getSet(first).getShotPath());
      }
      
      gui.space(c0);
      
      descLbl = gui.multiTextLabel(c0, Font.SMALL, "\n\n\n\n");
      descLbl.setTruncation(Truncation.END);
      descLbl.setColors    (Color.WHITE, Color.YELLOW);
    }
  }
  
  @Override
  protected void performFocus(Widget widget) {
    super.performFocus(widget);
    
    switch ((Action) widget.getToken0()) {
      case LEVEL_SET: {
        updateSetWidgets((LevelSet) widget.getToken1());
        
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
        LevelSetListFuncs.deinitialize();
        
        UI.gotoScreen(TitleScreen.INSTANCE);
        
        break;
      }
      case NAVIGATE: {
        int dir = (Integer) token1;
        
        first += PAGE_SIZE * dir;
        
        UI.gotoScreen(this);
        
        nav.setFocus(dir);
        
        break;
      }
      case LEVEL_SET: {
        LevelSetFuncs.initialize((LevelSet) token1);
        
        UI.gotoScreen(LevelSetScreen.INSTANCE);
        
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
  public void exitRequested() {
    performAction(Action.BACK);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      LevelSetListFuncs.deinitialize();
    }
    
    nav     = null;
    shotLbl = null;
    descLbl = null;
  }
  
  private enum Action {
    BACK,
    NAVIGATE,
    LEVEL_SET,
    DISABLED
  }
}
