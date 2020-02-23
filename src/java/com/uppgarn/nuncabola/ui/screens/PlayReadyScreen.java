/*
 * PlayReadyScreen.java
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

import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.hud.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public final class PlayReadyScreen extends PlayPreparationScreen {
  public static final PlayReadyScreen INSTANCE = new PlayReadyScreen();
  
  private PlayReadyScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    HUD hud = UI.getHUD();
    
    hud.setVisible (true);
    hud.setContents(null, false);
    hud.setCamera  (PlayFuncs.getInput().getCamera());
    hud.setSpeed   (null);
    
    super.enter(from);
    
    if (from != PlayIntroScreen.INSTANCE) {
      gc();
    }
  }
  
  @Override
  protected String getMessage() {
    return "Ready?";
  }
  
  @Override
  protected String getSoundPath() {
    return "snd/ready.ogg";
  }
  
  @Override
  protected float getFlightPosition() {
    return 1.0f;
  }
  
  @Override
  protected Screen getNextScreen() {
    return PlaySetScreen.INSTANCE;
  }
  
  @Override
  protected boolean isSeamless() {
    return true;
  }
}
