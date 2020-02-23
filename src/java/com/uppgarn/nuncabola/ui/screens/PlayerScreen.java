/*
 * PlayerScreen.java
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
import com.uppgarn.nuncabola.ui.components.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public final class PlayerScreen extends MenuScreen {
  public static final PlayerScreen INSTANCE = new PlayerScreen();
  
  private Screen  okNextScreen;
  private Screen  cancelNextScreen;
  private boolean changed;
  
  private Screen from;
  
  private StringBuilder playerBld;
  
  private TextLabel    playerLbl;
  private MiniKeyboard keyboard;
  private Button       okBtn;
  
  private PlayerScreen() {
  }
  
  public void setNextScreen(Screen screen) {
    setNextScreens(screen, screen);
  }
  
  public void setNextScreens(Screen okScreen, Screen cancelScreen) {
    okNextScreen     = okScreen;
    cancelNextScreen = cancelScreen;
  }
  
  public boolean getChanged() {
    return changed;
  }
  
  @Override
  public void enter(Screen from) {
    this.from = from;
    
    if (from == OptionsScreen.INSTANCE) {
      BackgroundFuncs.initialize();
    }
    
    playerBld = new StringBuilder(getStringPref(Pref.PLAYER));
    
    super.enter(from);
    
    changed = false;
  }
  
  private void updatePlayerLabel() {
    playerLbl.setText(playerBld.toString());
  }
  
  private void updateOKButton() {
    if (!playerBld.toString().trim().isEmpty()) {
      okBtn.setColor(null);
      okBtn.setToken(Action.OK);
    } else {
      okBtn.setColor(Color.GRAY);
      okBtn.setToken(Action.DISABLED);
    }
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl = gui.textLabel(c0, Font.MEDIUM);
      lbl.setText("Player Name");
      
      gui.space(c0);
      
      String[] playerLblStrs = {"WWWWWWWW"};
      
      playerLbl = gui.textLabel(c0, Font.MEDIUM, playerLblStrs);
      playerLbl.setTruncation(Truncation.BEGINNING);
      playerLbl.setColor     (Color.YELLOW);
      
      gui.space(c0);
      
      keyboard = new MiniKeyboard(
        gui,
        c0,
        Action.CHARACTER,
        Action.BACKSPACE,
        Action.CAPS_LOCK);
      
      gui.space(c0);
      
      Container c1 = gui.hArray(c0);
      
      {
        Button btn = gui.button(c1);
        btn.setText ("Cancel");
        btn.setToken(Action.CANCEL);
        
        okBtn = gui.button(c1);
        okBtn.setText("OK");
        
        gui.setFocusWidget(okBtn);
      }
    }
    
    updatePlayerLabel();
    updateOKButton();
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case CHARACTER: {
        playerBld.append((char) (Character) token1);
        
        updatePlayerLabel();
        updateOKButton();
        
        break;
      }
      case BACKSPACE: {
        if (playerBld.length() > 0) {
          playerBld.deleteCharAt(playerBld.length() - 1);
          
          updatePlayerLabel();
          updateOKButton();
        }
        
        break;
      }
      case CLEAR: {
        playerBld.setLength(0);
        
        updatePlayerLabel();
        updateOKButton();
        
        break;
      }
      case CAPS_LOCK: {
        keyboard.toggleCapsLock();
        
        break;
      }
      case CANCEL: {
        UI.gotoScreen(cancelNextScreen);
        
        break;
      }
      case OK: {
        String player = playerBld.toString();
        
        if (!getStringPref(Pref.PLAYER).equals(player)) {
          setPref(Pref.PLAYER, player);
          
          changed = true;
        }
        
        UI.gotoScreen(okNextScreen);
        
        break;
      }
      case DISABLED: {
        break;
      }
    }
  }
  
  @Override
  public void paint(float t) {
    if (from == OptionsScreen.INSTANCE) {
      BackgroundFuncs.draw();
    } else {
      GameFuncs.draw(t);
    }
    
    super.paint(t);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (code == Keyboard.KEY_BACK) {
      getGUI().setFocusWidget(okBtn);
      
      performAction(Action.BACKSPACE);
    } else if (code == Keyboard.KEY_DELETE) {
      getGUI().setFocusWidget(okBtn);
      
      performAction(Action.CLEAR);
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public void textEntered(char ch) {
    getGUI().setFocusWidget(okBtn);
    
    performAction(Action.CHARACTER, ch);
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.CANCEL);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (from == OptionsScreen.INSTANCE) {
      BackgroundFuncs.deinitialize();
    }
    
    if (((from == PlayGoalScreen.INSTANCE)
          || (from == PlayCompletionScreen.INSTANCE))
        && (to == null)) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
    
    playerBld = null;
    playerLbl = null;
    keyboard  = null;
    okBtn     = null;
  }
  
  private enum Action {
    CHARACTER,
    BACKSPACE,
    CLEAR,
    CAPS_LOCK,
    CANCEL,
    OK,
    DISABLED
  }
}
