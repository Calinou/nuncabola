/*
 * ResolutionScreen.java
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

import com.uppgarn.nuncabola.core.display.*;
import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.opengl.*;

import java.util.*;

public final class ResolutionScreen extends MenuScreen {
  private static final int COLUMN_COUNT = 4;
  
  public static final ResolutionScreen INSTANCE = new ResolutionScreen();
  
  private ResolutionScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    BackgroundFuncs.initialize();
    
    super.enter(from);
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
        lbl.setText("Resolution");
      }
      
      gui.space(c0);
      
      Container parent = null;
      
      List<DisplayMode> modes = DisplayTool.getSortedModes();
      
      int width  = getIntPref(Pref.SCREEN_WIDTH);
      int height = getIntPref(Pref.SCREEN_HEIGHT);
      
      for (int idx = 0; idx < modes.size(); idx++) {
        DisplayMode mode = modes.get(idx);
        
        if (idx % COLUMN_COUNT == 0) {
          parent = gui.hArray(c0);
        }
        
        boolean selected =    (mode.getWidth () == width)
                           && (mode.getHeight() == height);
        
        Button btn = gui.button(parent);
        btn.setText    (mode.getWidth() + " x " + mode.getHeight());
        btn.setTokens  (Action.RESOLUTION, mode);
        btn.setSelected(selected);
      }
      
      for (int idx = modes.size(); idx % COLUMN_COUNT != 0; idx++) {
        gui.space(parent);
      }
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case BACK: {
        UI.gotoScreen(GraphicsScreen.INSTANCE);
        
        break;
      }
      case RESOLUTION: {
        DisplayMode mode = (DisplayMode) token1;
        
        int oldWidth  = getIntPref(Pref.SCREEN_WIDTH);
        int oldHeight = getIntPref(Pref.SCREEN_HEIGHT);
        
        if ((oldWidth != mode.getWidth()) || (oldHeight != mode.getHeight())) {
          // Leave the current screen so its resources are
          // deinitialized /before/ recreating the display.
          // The screen will be reentered on success.
          
          UI.gotoScreen(null);
          
          // Try to set new display mode. While it would be possible
          // to simply call Display.setDisplayMode, tests have shown
          // that recreating the display is the safer approach.
          
          setPref(Pref.SCREEN_WIDTH,  mode.getWidth ());
          setPref(Pref.SCREEN_HEIGHT, mode.getHeight());
          
          try {
            UI.rebuild();
            UI.gotoScreen(this);
          } catch (UIException ex) {
            // The attempt failed, try to restore old display mode.
            
            setPref(Pref.SCREEN_WIDTH,  oldWidth);
            setPref(Pref.SCREEN_HEIGHT, oldHeight);
            
            try {
              UI.rebuild();
              UI.gotoScreen(this);
            } catch (UIException ex0) {
              // Restoring failed, let program exit.
            }
          }
        }
        
        break;
      }
    }
  }
  
  @Override
  public void paint(float t) {
    BackgroundFuncs.draw();
    
    super.paint(t);
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.BACK);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    BackgroundFuncs.deinitialize();
  }
  
  private enum Action {
    BACK,
    RESOLUTION
  }
}
