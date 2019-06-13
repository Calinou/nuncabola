/*
 * ReplayIntroScreen.java
 *
 * Copyright (c) 2003-2019 Nuncabola authors
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
import com.uppgarn.nuncabola.ui.hud.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public final class ReplayIntroScreen extends ReplayActionScreen {
  public static final ReplayIntroScreen INSTANCE = new ReplayIntroScreen();
  
  private Screen from;
  
  private ReplayIntroScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    this.from = from;
    
    if (from != ReplayWarningScreen.INSTANCE) {
      FadeFuncs.initialize(FadeFuncs.Mode.IN, 0.5f);
    } else {
      GameFuncs.setFade(0.0f);
    }
    
    HUD hud = UI.getHUD();
    
    hud.setVisible (true);
    hud.setContents(ReplayFuncs.getSeries(), false);
    hud.setCamera  (null);
    hud.setSpeed   (null);
    
    super.enter(from);
    
    Audio.fadeToMusic(ReplayFuncs.getSeries().getLevel().getMusicPath(), 0.5f);
    
    gc();
  }
  
  private void goToReplayMainScreen() {
    UI.gotoScreen(ReplayMainScreen.INSTANCE);
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    TextLabel lbl = gui.textLabel(null, Font.LARGE);
    lbl.setText  ("Replay");
    lbl.setColors(Color.GREEN, Color.BLUE);
    lbl.setScale (1.2f);
  }
  
  @Override
  public void timer(float dt) {
    if (UI.getScreenTime() >= 1.0f) {
      goToReplayMainScreen();
      
      return;
    }
    
    if (from != ReplayWarningScreen.INSTANCE) {
      FadeFuncs.step(dt);
    }
    
    super.timer(dt);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (code == Keyboard.KEY_RETURN) {
      goToReplayMainScreen();
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public void mouseDown(int button) {
    if (button == 0) {
      goToReplayMainScreen();
    }
  }
  
  @Override
  public void controllerDown(int button) {
    if (button == getIntPref(Pref.CONTROLLER_BUTTON_A)) {
      goToReplayMainScreen();
    }
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (from != ReplayWarningScreen.INSTANCE) {
      FadeFuncs.deinitialize();
    }
  }
}
