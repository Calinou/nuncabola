/*
 * PlayOverwriteScreen.java
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
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.ui.*;

public final class PlayOverwriteScreen extends MenuScreen {
  public static final PlayOverwriteScreen INSTANCE = new PlayOverwriteScreen();
  
  private String name;
  private Screen nextScreen;
  
  private PlayOverwriteScreen() {
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setNextScreen(Screen screen) {
    nextScreen = screen;
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl0 = gui.textLabel(c0, Font.MEDIUM);
      lbl0.setText ("Overwrite?");
      lbl0.setColor(Color.RED);
      lbl0.setScale(1.2f);
      
      gui.space(c0);
      
      String[] lbl1Strs = {"xxxxxxxx-01_01"};
      
      TextLabel lbl1 = gui.textLabel(c0, Font.MEDIUM, lbl1Strs);
      lbl1.setTruncation(Truncation.END);
      lbl1.setText      (name);
      lbl1.setColor     (Color.YELLOW);
      
      gui.space(c0);
      
      Container c1 = gui.hArray(c0);
      
      {
        Button btn0 = gui.button(c1);
        btn0.setText ("Overwrite");
        btn0.setToken(Action.OVERWRITE);
        
        Button btn1 = gui.button(c1);
        btn1.setText ("Cancel");
        btn1.setToken(Action.CANCEL);
        
        gui.setFocusWidget(btn1);
      }
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case OVERWRITE: {
        ReplayFileFuncs.renameGameReplayTo(name);
        
        UI.gotoScreen(nextScreen);
        
        break;
      }
      case CANCEL: {
        PlaySaveScreen.INSTANCE.setInitialName(name);
        
        UI.gotoScreen(PlaySaveScreen.INSTANCE);
        
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
    performAction(Action.CANCEL);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    name = null;
  }
  
  private enum Action {
    OVERWRITE,
    CANCEL
  }
}
