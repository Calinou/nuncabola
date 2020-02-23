/*
 * ReplayMainScreen.java
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
import com.uppgarn.nuncabola.core.replay.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.hud.*;

public final class ReplayMainScreen extends ReplayActionScreen {
  public static final ReplayMainScreen INSTANCE = new ReplayMainScreen();
  
  private ReplaySeries series;
  private HUD          hud;
  
  private ReplayMainScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    series = ReplayFuncs.getSeries();
    hud    = UI.getHUD();
    
    GameFuncs.setFade(0.0f);
    
    super.enter(from);
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    // Hack: Even though unused, this is needed due to
    // ReplayActionScreen being a subclass of GUIScreen.
  }
  
  @Override
  public void timer(float dt) {
    ReplayFuncs.step(dt);
    
    if (ReplayFuncs.isOver()) {
      UI.gotoScreen(ReplayEndScreen.INSTANCE);
      
      return;
    }
    
    hud.setContents(series, true);
    
    super.timer(dt);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    series = null;
    hud    = null;
  }
}
