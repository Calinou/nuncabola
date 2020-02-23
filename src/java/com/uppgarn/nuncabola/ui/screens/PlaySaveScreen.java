/*
 * PlaySaveScreen.java
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
import com.uppgarn.nuncabola.ui.components.*;

import org.lwjgl.input.*;

public final class PlaySaveScreen extends MenuScreen {
  public static final PlaySaveScreen INSTANCE = new PlaySaveScreen();
  
  private String initName;
  private Screen nextScreen;
  
  private StringBuilder nameBld;
  
  private TextLabel    nameLbl;
  private MiniKeyboard keyboard;
  private Button       saveBtn;
  
  private PlaySaveScreen() {
  }
  
  public void setInitialName(String name) {
    initName = name;
  }
  
  public void setNextScreen(Screen screen) {
    nextScreen = screen;
  }
  
  @Override
  public void enter(Screen from) {
    String name;
    
    if (initName == null) {
      name = PlayFuncs.getDefaultReplayName();
    } else {
      name = initName;
    }
    
    nameBld = new StringBuilder(name);
    
    super.enter(from);
  }
  
  private void updateNameLabel() {
    nameLbl.setText(nameBld.toString());
  }
  
  private void updateSaveButton() {
    if (ReplayFileFuncs.isValidName(nameBld.toString())) {
      saveBtn.setColor(null);
      saveBtn.setToken(Action.SAVE);
    } else {
      saveBtn.setColor(Color.GRAY);
      saveBtn.setToken(Action.DISABLED);
    }
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl = gui.textLabel(c0, Font.MEDIUM);
      lbl.setText("Replay Name");
      
      gui.space(c0);
      
      String[] nameLblStrs = {"xxxxxxxx-01_01"};
      
      nameLbl = gui.textLabel(c0, Font.MEDIUM, nameLblStrs);
      nameLbl.setTruncation(Truncation.BEGINNING);
      nameLbl.setColor     (Color.YELLOW);
      
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
        
        saveBtn = gui.button(c1);
        saveBtn.setText("Save");
        
        gui.setFocusWidget(saveBtn);
      }
    }
    
    updateNameLabel();
    updateSaveButton();
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case CHARACTER: {
        nameBld.append((char) (Character) token1);
        
        updateNameLabel();
        updateSaveButton();
        
        break;
      }
      case BACKSPACE: {
        if (nameBld.length() > 0) {
          nameBld.deleteCharAt(nameBld.length() - 1);
          
          updateNameLabel();
          updateSaveButton();
        }
        
        break;
      }
      case CLEAR: {
        nameBld.setLength(0);
        
        updateNameLabel();
        updateSaveButton();
        
        break;
      }
      case CAPS_LOCK: {
        keyboard.toggleCapsLock();
        
        break;
      }
      case CANCEL: {
        UI.gotoScreen(nextScreen);
        
        break;
      }
      case SAVE: {
        String name = nameBld.toString();
        
        if (ReplayFileFuncs.replayExists(name)) {
          PlayOverwriteScreen.INSTANCE.setName      (name);
          PlayOverwriteScreen.INSTANCE.setNextScreen(nextScreen);
          
          UI.gotoScreen(PlayOverwriteScreen.INSTANCE);
        } else {
          ReplayFileFuncs.renameGameReplayTo(name);
          
          UI.gotoScreen(nextScreen);
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
  public void keyDown(int code, char ch) {
    if (code == Keyboard.KEY_BACK) {
      getGUI().setFocusWidget(saveBtn);
      
      performAction(Action.BACKSPACE);
    } else if (code == Keyboard.KEY_DELETE) {
      getGUI().setFocusWidget(saveBtn);
      
      performAction(Action.CLEAR);
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public void textEntered(char ch) {
    getGUI().setFocusWidget(saveBtn);
    
    performAction(Action.CHARACTER, ch);
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
    
    initName = null;
    nameBld  = null;
    nameLbl  = null;
    keyboard = null;
    saveBtn  = null;
  }
  
  private enum Action {
    CHARACTER,
    BACKSPACE,
    CLEAR,
    CAPS_LOCK,
    CANCEL,
    SAVE,
    DISABLED
  }
}
