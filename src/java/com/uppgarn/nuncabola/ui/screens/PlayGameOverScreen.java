/*
 * PlayGameOverScreen.java
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

public final class PlayGameOverScreen extends GUIScreen {
  public static final PlayGameOverScreen INSTANCE = new PlayGameOverScreen();
  
  private PlayGameOverScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    super.enter(from);
    
    Audio.fadeOutMusic(2.0f);
    Audio.playSound("snd/over.ogg");
  }
  
  private void goToLevelSetScreen() {
    PlayFuncs.deinitialize();
    
    UI.gotoScreen(LevelSetScreen.INSTANCE);
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    TextLabel lbl = gui.textLabel(null, Font.LARGE);
    lbl.setText  ("GAME OVER");
    lbl.setColors(Color.RED, Color.GRAY);
    lbl.setScale (1.2f);
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(t);
    
    super.paint(t);
  }
  
  @Override
  public void timer(float dt) {
    if (UI.getScreenTime() >= 3.0f) {
      goToLevelSetScreen();
      
      return;
    }
    
    super.timer(dt);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (code == Keyboard.KEY_RETURN) {
      exitRequested();
    }
  }
  
  @Override
  public void mouseDown(int button) {
    if (button == 0) {
      exitRequested();
    }
  }
  
  @Override
  public void controllerDown(int button) {
    if (button == getIntPref(Pref.CONTROLLER_BUTTON_A)) {
      exitRequested();
    }
  }
  
  @Override
  public void exitRequested() {
    goToLevelSetScreen();
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
  }
}
