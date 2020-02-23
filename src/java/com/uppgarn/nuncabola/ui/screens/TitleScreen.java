/*
 * TitleScreen.java
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
import com.uppgarn.nuncabola.core.graphics.*;
import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.general.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public final class TitleScreen extends MenuScreen {
  private static final String CHEAT_STR = "xyzzy";
  
  public static final TitleScreen INSTANCE = new TitleScreen();
  
  private StringBuilder charQueue;
  
  private Button playBtn;
  
  private TitleScreen() {
    charQueue = new StringBuilder(CHEAT_STR.length());
  }
  
  @Override
  public void enter(Screen from) {
    TitleFuncs.initialize();
    
    super.enter(from);
    
    Audio.fadeToMusic("bgm/title.ogg", 0.5f);
  }
  
  private String getPlayButtonText(boolean cheat) {
    return cheat ? "Cheat" : "Play";
  }
  
  private void updatePlayButton() {
    playBtn.setText(getPlayButtonText(getBooleanPref(Pref.CHEAT)));
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl = gui.textLabel(c0, Font.LARGE);
      lbl.setText(ProgramConstants.NAME);
      
      gui.space(c0);
      
      Container c1 = gui.hStack(c0);
      
      {
        gui.filler(c1);
        
        Container c2 = gui.vArray(c1);
        
        {
          String[] playBtnStrs = {
            getPlayButtonText(false),
            getPlayButtonText(true)
          };
          
          playBtn = gui.button(c2, Font.MEDIUM, playBtnStrs);
          playBtn.setToken   (Action.PLAY);
          playBtn.setSelected(true);
          
          gui.setFocusWidget(playBtn);
          
          Button btn0 = gui.button(c2, Font.MEDIUM);
          btn0.setText ("Replay");
          btn0.setToken(Action.REPLAY);
          
          Button btn1 = gui.button(c2, Font.MEDIUM);
          btn1.setText ("Help");
          btn1.setToken(Action.HELP);
          
          Button btn2 = gui.button(c2, Font.MEDIUM);
          btn2.setText ("Options");
          btn2.setToken(Action.OPTIONS);
          
          Button btn3 = gui.button(c2, Font.MEDIUM);
          btn3.setText ("Exit");
          btn3.setToken(Action.EXIT);
        }
        
        gui.filler(c1);
      }
    }
    
    updatePlayButton();
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case PLAY: {
        if (getStringPref(Pref.PLAYER).isEmpty()) {
          PlayerScreen.INSTANCE.setNextScreens(
            LevelSetListScreen.INSTANCE,
            this);
          
          UI.gotoScreen(PlayerScreen      .INSTANCE);
        } else {
          UI.gotoScreen(LevelSetListScreen.INSTANCE);
        }
        
        break;
      }
      case REPLAY: {
        UI.gotoScreen(ReplayListScreen.INSTANCE);
        
        break;
      }
      case HELP: {
        UI.gotoScreen(HelpScreen.INSTANCE);
        
        break;
      }
      case OPTIONS: {
        UI.gotoScreen(OptionsScreen.INSTANCE);
        
        break;
      }
      case EXIT: {
        UI.gotoScreen(null);
        
        break;
      }
      case CHARACTER: {
        if (charQueue.length() == CHEAT_STR.length()) {
          charQueue.deleteCharAt(0);
        }
        
        charQueue.append((char) (Character) token1);
        
        boolean cheat = CHEAT_STR.contentEquals(charQueue);
        
        if (getBooleanPref(Pref.CHEAT) != cheat) {
          setPref(Pref.CHEAT, cheat);
          
          if (!cheat) {
            Gfx.setWire(false);
          }
          
          updatePlayButton();
          playBtn.setScale(1.2f);
        }
        
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
  public void timer(float dt) {
    TitleFuncs.step(dt);
    
    super.timer(dt);
  }
  
  @Override
  public void textEntered(char ch) {
    char lcCh = Character.toLowerCase(ch);
    
    if ((lcCh >= 'a') && (lcCh <= 'z')) {
      performAction(Action.CHARACTER, lcCh);
    }
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.EXIT);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    TitleFuncs.deinitialize();
    
    playBtn = null;
  }
  
  private enum Action {
    PLAY,
    REPLAY,
    HELP,
    OPTIONS,
    EXIT,
    CHARACTER
  }
}
