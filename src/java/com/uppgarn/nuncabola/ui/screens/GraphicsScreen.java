/*
 * GraphicsScreen.java
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
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.opengl.*;

public final class GraphicsScreen extends MenuScreen {
  public static final GraphicsScreen INSTANCE = new GraphicsScreen();
  
  private GraphicsScreen() {
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
        lbl.setText("Graphics");
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Resolution");
        
        String[] btnStrs = {"99999 x 99999"};
        
        int width  = getIntPref(Pref.SCREEN_WIDTH);
        int height = getIntPref(Pref.SCREEN_HEIGHT);
        
        Button btn = gui.button(c1, Font.SMALL, btnStrs);
        btn.setText (width + " x " + height);
        btn.setToken(Action.RESOLUTION);
      }
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Fullscreen");
        
        Container c2 = gui.hArray(c1);
        
        {
          boolean enabled = Display.getDisplayMode().isFullscreenCapable();
          boolean on      = getBooleanPref(Pref.FULLSCREEN);
          
          Button btn0 = gui.button(c2);
          btn0.setText    ("On");
          btn0.setSelected(on);
          
          if (enabled) {
            btn0.setTokens(Action.FULLSCREEN, true);
          } else {
            btn0.setColor(Color.GRAY);
            btn0.setToken(Action.DISABLED);
          }
          
          Button btn1 = gui.button(c2);
          btn1.setText    ("Off");
          btn1.setSelected(!on);
          
          if (enabled) {
            btn1.setTokens(Action.FULLSCREEN, false);
          } else {
            btn1.setColor(Color.GRAY);
            btn1.setToken(Action.DISABLED);
          }
        }
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("V-Sync");
        
        Container c2 = gui.hArray(c1);
        
        {
          boolean on = getBooleanPref(Pref.V_SYNC);
          
          Button btn0 = gui.button(c2);
          btn0.setText    ("On");
          btn0.setTokens  (Action.V_SYNC, true);
          btn0.setSelected(on);
          
          Button btn1 = gui.button(c2);
          btn1.setText    ("Off");
          btn1.setTokens  (Action.V_SYNC, false);
          btn1.setSelected(!on);
        }
      }
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Antialiasing");
        
        Container c2 = gui.hArray(c1);
        
        {
          int multisample = getIntPref(Pref.MULTISAMPLE);
          
          Button btn0 = gui.button(c2);
          btn0.setText    ("8x");
          btn0.setTokens  (Action.MULTISAMPLE, 8);
          btn0.setSelected(multisample == 8);
          
          Button btn1 = gui.button(c2);
          btn1.setText    ("4x");
          btn1.setTokens  (Action.MULTISAMPLE, 4);
          btn1.setSelected(multisample == 4);
          
          Button btn2 = gui.button(c2);
          btn2.setText    ("2x");
          btn2.setTokens  (Action.MULTISAMPLE, 2);
          btn2.setSelected(multisample == 2);
          
          Button btn3 = gui.button(c2);
          btn3.setText    ("Off");
          btn3.setTokens  (Action.MULTISAMPLE, 0);
          btn3.setSelected(multisample == 0);
        }
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Reflection");
        
        Container c2 = gui.hArray(c1);
        
        {
          boolean on = getBooleanPref(Pref.REFLECTION);
          
          Button btn0 = gui.button(c2);
          btn0.setText    ("On");
          btn0.setTokens  (Action.REFLECTION, true);
          btn0.setSelected(on);
          
          Button btn1 = gui.button(c2);
          btn1.setText    ("Off");
          btn1.setTokens  (Action.REFLECTION, false);
          btn1.setSelected(!on);
        }
      }
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Background");
        
        Container c2 = gui.hArray(c1);
        
        {
          boolean on = getBooleanPref(Pref.BACKGROUND);
          
          Button btn0 = gui.button(c2);
          btn0.setText    ("On");
          btn0.setTokens  (Action.BACKGROUND, true);
          btn0.setSelected(on);
          
          Button btn1 = gui.button(c2);
          btn1.setText    ("Off");
          btn1.setTokens  (Action.BACKGROUND, false);
          btn1.setSelected(!on);
        }
      }
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Shadow");
        
        Container c2 = gui.hArray(c1);
        
        {
          boolean on = getBooleanPref(Pref.SHADOW);
          
          Button btn0 = gui.button(c2);
          btn0.setText    ("On");
          btn0.setTokens  (Action.SHADOW, true);
          btn0.setSelected(on);
          
          Button btn1 = gui.button(c2);
          btn1.setText    ("Off");
          btn1.setTokens  (Action.SHADOW, false);
          btn1.setSelected(!on);
        }
      }
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case BACK: {
        UI.gotoScreen(OptionsScreen.INSTANCE);
        
        break;
      }
      case RESOLUTION: {
        UI.gotoScreen(ResolutionScreen.INSTANCE);
        
        break;
      }
      case FULLSCREEN: {
        boolean on    = (Boolean) token1;
        boolean oldOn = getBooleanPref(Pref.FULLSCREEN);
        
        if (oldOn != on) {
          // Leave the current screen so its resources are
          // deinitialized /before/ recreating the display.
          // The screen will be reentered on success.
          
          UI.gotoScreen(null);
          
          // Try to set new fullscreen state. While it would be possible
          // to simply call Display.setFullscreen, tests have shown that
          // recreating the display is the safer approach.
          
          setPref(Pref.FULLSCREEN, on);
          
          try {
            UI.rebuild();
            UI.gotoScreen(this);
          } catch (UIException ex) {
            // The attempt failed, try to restore old fullscreen state.
            
            setPref(Pref.FULLSCREEN, oldOn);
            
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
      case V_SYNC: {
        boolean on    = (Boolean) token1;
        boolean oldOn = getBooleanPref(Pref.V_SYNC);
        
        if (oldOn != on) {
          // Leave the current screen so its resources are
          // deinitialized /before/ recreating the display.
          // The screen will be reentered on success.
          
          UI.gotoScreen(null);
          
          // Try to set new v-sync state. While it would be possible
          // to simply call Display.setVSyncEnabled, tests have shown
          // that recreating the display is the safer approach.
          
          setPref(Pref.V_SYNC, on);
          
          try {
            UI.rebuild();
            UI.gotoScreen(this);
          } catch (UIException ex) {
            // The attempt failed, try to restore old v-sync state.
            
            setPref(Pref.V_SYNC, oldOn);
            
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
      case MULTISAMPLE: {
        int multisample    = (Integer) token1;
        int oldMultisample = getIntPref(Pref.MULTISAMPLE);
        
        if (oldMultisample != multisample) {
          // Leave the current screen so its resources are
          // deinitialized /before/ recreating the display.
          // The screen will be reentered on success.
          
          UI.gotoScreen(null);
          
          // Try to set new multisample state.
          // This requires recreating the display.
          
          setPref(Pref.MULTISAMPLE, multisample);
          
          try {
            UI.rebuild();
            UI.gotoScreen(this);
          } catch (UIException ex) {
            // The attempt failed, try to restore old multisample state.
            
            setPref(Pref.MULTISAMPLE, oldMultisample);
            
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
      case REFLECTION: {
        boolean on    = (Boolean) token1;
        boolean oldOn = getBooleanPref(Pref.REFLECTION);
        
        if (oldOn != on) {
          // Leave the current screen so its resources are
          // deinitialized /before/ recreating the display.
          // The screen will be reentered on success.
          
          UI.gotoScreen(null);
          
          // Try to set new reflection state.
          // This requires recreating the display.
          
          setPref(Pref.REFLECTION, on);
          
          try {
            UI.rebuild();
            UI.gotoScreen(this);
          } catch (UIException ex) {
            // The attempt failed, try to restore old reflection state.
            
            setPref(Pref.REFLECTION, oldOn);
            
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
      case BACKGROUND: {
        boolean on = (Boolean) token1;
        
        if (getBooleanPref(Pref.BACKGROUND) != on) {
          setPref(Pref.BACKGROUND, on);
          
          UI.rebuildRenderers();
          UI.gotoScreen(this);
        }
        
        break;
      }
      case SHADOW: {
        boolean on = (Boolean) token1;
        
        if (getBooleanPref(Pref.SHADOW) != on) {
          setPref(Pref.SHADOW, on);
          
          UI.rebuildRenderers();
          UI.gotoScreen(this);
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
    RESOLUTION,
    FULLSCREEN,
    V_SYNC,
    MULTISAMPLE,
    REFLECTION,
    BACKGROUND,
    SHADOW,
    DISABLED
  }
}
