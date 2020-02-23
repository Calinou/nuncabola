/*
 * PlayPreparationScreen.java
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
import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public abstract class PlayPreparationScreen extends PlayActionScreen {
  private GameFlyer gameFlyer;
  
  @Override
  public void enter(Screen from) {
    gameFlyer = new GameFlyer(GameFuncs.getGame(), GameFuncs.getViewDistance());
    gameFlyer.fly(getFlightPosition());
    
    GameFuncs.setFade(0.0f);
    
    super.enter(from);
    
    Audio.playSound(getSoundPath());
  }
  
  protected abstract String getMessage();
  
  protected abstract String getSoundPath();
  
  protected abstract float getFlightPosition();
  
  protected abstract Screen getNextScreen();
  
  protected abstract boolean isSeamless();
  
  private void goToPlayMainScreen() {
    UI.gotoScreen(PlayMainScreen.INSTANCE);
  }
  
  @Override
  protected final void configureGUI(GUI gui) {
    TextLabel lbl = gui.textLabel(null, Font.LARGE);
    lbl.setText (getMessage());
    lbl.setScale(1.2f);
  }
  
  @Override
  public final void timer(float dt) {
    float time = UI.getScreenTime();
    
    if (time >= 1.0f) {
      UI.gotoScreen(getNextScreen(), isSeamless());
      
      return;
    }
    
    gameFlyer.fly(getFlightPosition() - 0.5f * time);
    
    super.timer(dt);
  }
  
  @Override
  public final void keyDown(int code, char ch) {
    if (code == Keyboard.KEY_RETURN) {
      goToPlayMainScreen();
    } else {
      super.keyDown(code, ch);
    }
  }
  
  @Override
  public final void mouseDown(int button) {
    if (button == 0) {
      goToPlayMainScreen();
    } else {
      super.mouseDown(button);
    }
  }
  
  @Override
  public final void controllerDown(int button) {
    if (button == getIntPref(Pref.CONTROLLER_BUTTON_A)) {
      goToPlayMainScreen();
    } else {
      super.controllerDown(button);
    }
  }
  
  @Override
  public final void leave(Screen to) {
    super.leave(to);
    
    gameFlyer = null;
  }
}
