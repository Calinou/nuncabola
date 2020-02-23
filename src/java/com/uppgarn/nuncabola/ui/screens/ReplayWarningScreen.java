/*
 * ReplayWarningScreen.java
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

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public final class ReplayWarningScreen extends GUIScreen {
  public static final ReplayWarningScreen INSTANCE = new ReplayWarningScreen();
  
  private ReplayWarningScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    FadeFuncs.initialize(FadeFuncs.Mode.IN, 0.5f);
    
    super.enter(from);
    
    Audio.fadeToMusic(ReplayFuncs.getSeries().getLevel().getMusicPath(), 0.5f);
  }
  
  private void goToReplayIntroScreen() {
    UI.gotoScreen(ReplayIntroScreen.INSTANCE);
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    Container c0 = gui.vStack(null);
    
    {
      TextLabel lbl0 = gui.textLabel(c0, Font.MEDIUM);
      lbl0.setText("Warning!");
      
      gui.space(c0);
      
      String str =
          "The current replay was recorded with a\n"
        + "different (or unknown) version of this level.\n"
        + "Be prepared to encounter visual errors.";
      
      MultiTextLabel lbl1 = gui.multiTextLabel(c0);
      lbl1.setText (str);
      lbl1.setColor(Color.WHITE);
    }
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(t);
    
    super.paint(t);
  }
  
  @Override
  public void timer(float dt) {
    FadeFuncs.step(dt);
    
    super.timer(dt);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (code == Keyboard.KEY_RETURN) {
      goToReplayIntroScreen();
    }
  }
  
  @Override
  public void mouseDown(int button) {
    if (button == 0) {
      goToReplayIntroScreen();
    }
  }
  
  @Override
  public void controllerDown(int button) {
    if (button == getIntPref(Pref.CONTROLLER_BUTTON_A)) {
      goToReplayIntroScreen();
    }
  }
  
  @Override
  public void exitRequested() {
    UI.gotoScreen(ReplayEndScreen.INSTANCE);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    FadeFuncs.deinitialize();
    
    if (to == null) {
      ReplayFuncs.deinitialize();
    }
  }
}
