/*
 * ReplayEndScreen.java
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
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.ui.*;

public final class ReplayEndScreen extends MenuScreen {
  public static final ReplayEndScreen INSTANCE = new ReplayEndScreen();
  
  private ReplayEndScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    super.enter(from);
    
    Audio.fadeOutMusic(2.0f);
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl = gui.textLabel(c0, Font.LARGE);
      lbl.setText  ("Replay Ends");
      lbl.setColors(Color.RED, Color.GRAY);
      lbl.setScale (1.2f);
      
      gui.space(c0);
      
      Container c1 = gui.hArray(c0);
      
      {
        Button btn0 = gui.button(c1);
        btn0.setText ("Repeat");
        btn0.setToken(Action.REPEAT);
        
        if (UI.getMode().getType() == UIMode.Type.REPLAY) {
          Button btn1 = gui.button(c1);
          btn1.setText ("Exit");
          btn1.setToken(Action.EXIT);
          
          gui.setFocusWidget(btn1);
        } else {
          Button btn1 = gui.button(c1);
          btn1.setText ("Delete");
          btn1.setToken(Action.DELETE);
          
          Button btn2 = gui.button(c1);
          btn2.setText ("Keep");
          btn2.setToken(Action.KEEP);
          
          gui.setFocusWidget(btn2);
        }
      }
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case REPEAT: {
        ReplayFuncs.repeat();
        
        UI.gotoScreen(ReplayIntroScreen.INSTANCE);
        
        break;
      }
      case DELETE: {
        UI.gotoScreen(ReplayDeleteScreen.INSTANCE);
        
        break;
      }
      case KEEP: {
        ReplayFuncs.deinitialize();
        
        UI.gotoScreen(ReplayListScreen.INSTANCE);
        
        break;
      }
      case EXIT: {
        UI.gotoScreen(null);
        
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
    if (UI.getMode().getType() == UIMode.Type.REPLAY) {
      performAction(Action.EXIT);
    } else {
      performAction(Action.KEEP);
    }
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      ReplayFuncs.deinitialize();
    }
  }
  
  private enum Action {
    REPEAT,
    DELETE,
    KEEP,
    EXIT
  }
}
