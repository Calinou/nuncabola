/*
 * OptionsScreen.java
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
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.components.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import com.uppgarn.codelibf.util.*;

public final class OptionsScreen extends MenuScreen {
  public static final OptionsScreen INSTANCE = new OptionsScreen();
  
  private Slider mouseSenseSld;
  private Slider soundVolSld;
  private Slider musicVolSld;
  
  private OptionsScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    BackgroundFuncs.initialize();
    
    super.enter(from);
    
    Audio.fadeToMusic("bgm/inter.ogg", 0.5f);
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
        lbl.setText("Options");
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Graphics");
        
        Button btn = gui.button(c1);
        btn.setText ("Configure");
        btn.setToken(Action.GRAPHICS);
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Mouse Sensitivity");
        
        mouseSenseSld = new Slider(gui, c1, 600, -50, 11, Action.MOUSE_SENSE);
        mouseSenseSld.setValue(getIntPref(Pref.MOUSE_SENSE));
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Sound Volume");
        
        soundVolSld = new Slider(gui, c1, 0, 1, 11, Action.VOLUME_SOUND);
        soundVolSld.setValue(getIntPref(Pref.VOLUME_SOUND));
      }
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Music Volume");
        
        musicVolSld = new Slider(gui, c1, 0, 1, 11, Action.VOLUME_MUSIC);
        musicVolSld.setValue(getIntPref(Pref.VOLUME_MUSIC));
      }
      
      gui.space(c0);
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Player Name");
        
        String[] btnStrs = {"WWWWWWWW"};
        
        Button btn = gui.button(c1, Font.SMALL, btnStrs);
        btn.setTruncation(Truncation.END);
        btn.setText      (getStringPref(Pref.PLAYER));
        btn.setToken     (Action.PLAYER);
      }
      
      c1 = gui.hArray(c0);
      
      {
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Ball Model");
        
        String[] btnStrs = {""};
        
        String ballPath = getStringPref(Pref.BALL_PATH);
        
        Button btn = gui.button(c1, Font.SMALL, btnStrs);
        btn.setTruncation(Truncation.END);
        btn.setText      (StringTool.afterLast(ballPath, '/'));
        btn.setToken     (Action.BALL);
      }
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case BACK: {
        UI.gotoScreen(TitleScreen.INSTANCE);
        
        break;
      }
      case GRAPHICS: {
        UI.gotoScreen(GraphicsScreen.INSTANCE);
        
        break;
      }
      case MOUSE_SENSE: {
        int mouseSense = (Integer) token1;
        
        setPref(Pref.MOUSE_SENSE, mouseSense);
        
        mouseSenseSld.setValue(mouseSense);
        
        break;
      }
      case VOLUME_SOUND: {
        int volume = (Integer) token1;
        
        setPref(Pref.VOLUME_SOUND, volume);
        
        Audio.setSoundVolume(volume);
        Audio.playSound("snd/bump.ogg");
        
        soundVolSld.setValue(volume);
        
        break;
      }
      case VOLUME_MUSIC: {
        int volume = (Integer) token1;
        
        setPref(Pref.VOLUME_MUSIC, volume);
        
        Audio.setMusicVolume(volume);
        Audio.playSound("snd/bump.ogg");
        
        musicVolSld.setValue(volume);
        
        break;
      }
      case PLAYER: {
        PlayerScreen.INSTANCE.setNextScreen(this);
        
        UI.gotoScreen(PlayerScreen.INSTANCE);
        
        break;
      }
      case BALL: {
        UI.gotoScreen(BallScreen.INSTANCE);
        
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
    
    mouseSenseSld = null;
    soundVolSld   = null;
    musicVolSld   = null;
  }
  
  private enum Action {
    BACK,
    GRAPHICS,
    MOUSE_SENSE,
    VOLUME_SOUND,
    VOLUME_MUSIC,
    PLAYER,
    BALL
  }
}
