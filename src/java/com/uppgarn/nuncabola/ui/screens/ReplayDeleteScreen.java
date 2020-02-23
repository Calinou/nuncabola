/*
 * ReplayDeleteScreen.java
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

import java.io.*;
import java.nio.file.*;

public final class ReplayDeleteScreen extends MenuScreen {
  public static final ReplayDeleteScreen INSTANCE = new ReplayDeleteScreen();
  
  private ReplayDeleteScreen() {
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl = gui.textLabel(c0, Font.MEDIUM);
      lbl.setText ("Delete Replay?");
      lbl.setColor(Color.RED);
      lbl.setScale(1.2f);
      
      gui.space(c0);
      
      Container c1 = gui.hArray(c0);
      
      {
        Button btn0 = gui.button(c1);
        btn0.setText ("Delete");
        btn0.setToken(Action.DELETE);
        
        Button btn1 = gui.button(c1);
        btn1.setText ("Keep");
        btn1.setToken(Action.KEEP);
        
        gui.setFocusWidget(btn1);
      }
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case DELETE: {
        Path file = ReplayFuncs.getFile();
        
        ReplayFuncs.deinitialize();
        
        try {
          Files.delete(file);
        } catch (IOException ex) {
        }
        
        UI.gotoScreen(ReplayListScreen.INSTANCE);
        
        break;
      }
      case KEEP: {
        ReplayFuncs.deinitialize();
        
        UI.gotoScreen(ReplayListScreen.INSTANCE);
        
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
    performAction(Action.KEEP);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      ReplayFuncs.deinitialize();
    }
  }
  
  private enum Action {
    DELETE,
    KEEP
  }
}
