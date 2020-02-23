/*
 * PlayPoseScreen.java
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

import com.uppgarn.nuncabola.core.renderers.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.ui.*;

import org.lwjgl.input.*;

public final class PlayPoseScreen extends Screen {
  public static final PlayPoseScreen INSTANCE = new PlayPoseScreen();
  
  private PlayPoseScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    GameFuncs.setFade(0.0f);
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(0.0f, Pose.STATIC);
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if (code == Keyboard.KEY_F10) {
      exitRequested();
    }
  }
  
  @Override
  public void exitRequested() {
    UI.gotoScreen(PlayIntroScreen.INSTANCE);
  }
  
  @Override
  public void leave(Screen to) {
    if (to == null) {
      PlayFuncs        .deinitialize();
      LevelSetFuncs    .deinitialize();
      LevelSetListFuncs.deinitialize();
    }
  }
}
