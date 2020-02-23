/*
 * DemoScreen.java
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
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public final class DemoScreen extends Screen {
  public static final DemoScreen INSTANCE = new DemoScreen();
  
  private DemoScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    FadeFuncs.initialize(FadeFuncs.Mode.IN, 0.5f);
    
    gc();
  }
  
  private void goToHelpScreen() {
    UI.gotoScreen(HelpScreen.INSTANCE);
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(t);
  }
  
  @Override
  public void timer(float dt) {
    ReplayFuncs.step(dt);
    FadeFuncs  .step(dt);
    
    if (ReplayFuncs.isOver()) {
      goToHelpScreen();
      
      return;
    }
  }
  
  @Override
  public void exitRequested() {
    Audio.stopSounds();
    
    goToHelpScreen();
  }
  
  @Override
  public void leave(Screen to) {
    FadeFuncs  .deinitialize();
    ReplayFuncs.deinitialize();
  }
}
