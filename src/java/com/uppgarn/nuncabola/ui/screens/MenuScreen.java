/*
 * MenuScreen.java
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
import com.uppgarn.nuncabola.preferences.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import org.lwjgl.input.*;

public abstract class MenuScreen extends GUIScreen {
  @Override
  public void enter(Screen from) {
    super.enter(from);
    
    if (Mouse.isCreated()) {
      getGUI().point(Mouse.getX(), Mouse.getY());
    }
  }
  
  protected void performFocus(Widget widget) {
    widget.setScale(1.2f);
  }
  
  protected final void performAction(Object token0) {
    performAction(token0, null);
  }
  
  protected void performAction(Object token0, Object token1) {
    Audio.playSound("snd/menu.ogg");
  }
  
  private void move(Direction dir) {
    Widget widget = getGUI().move(dir);
    
    if (widget != null) {
      performFocus(widget);
    }
  }
  
  private void point(int x, int y) {
    Widget widget = getGUI().point(x, y);
    
    if (widget != null) {
      performFocus(widget);
    }
  }
  
  private void activate() {
    Widget widget = getGUI().getFocusWidget();
    
    if (widget != null) {
      performAction(widget.getToken0(), widget.getToken1());
    }
  }
  
  @Override
  public void keyDown(int code, char ch) {
    if        (isKey(code, ch, Pref.KEY_LEFT)) {
      move(Direction.LEFT);
    } else if (isKey(code, ch, Pref.KEY_RIGHT)) {
      move(Direction.RIGHT);
    } else if (isKey(code, ch, Pref.KEY_FORWARD)) {
      move(Direction.UP);
    } else if (isKey(code, ch, Pref.KEY_BACKWARD)) {
      move(Direction.DOWN);
    } else if (code == Keyboard.KEY_RETURN) {
      activate();
    }
  }
  
  @Override
  public void mouseMove(int x, int y, int dx, int dy) {
    point(x, y);
  }
  
  @Override
  public void mouseDown(int button) {
    if (button == 0) {
      activate();
    }
  }
  
  @Override
  public void controllerMove(int axis, float value, boolean recentered) {
    if (!recentered) {
      return;
    }
    
    if        (axis == getIntPref(Pref.CONTROLLER_AXIS_X)) {
      if (value < -0.5f) {
        move(Direction.LEFT);
      } else if (value > +0.5f) {
        move(Direction.RIGHT);
      }
    } else if (axis == getIntPref(Pref.CONTROLLER_AXIS_Y)) {
      if (value < -0.5f) {
        move(Direction.UP);
      } else if (value > +0.5f) {
        move(Direction.DOWN);
      }
    }
  }
  
  @Override
  public void controllerDown(int button) {
    if (button == getIntPref(Pref.CONTROLLER_BUTTON_A)) {
      activate();
    }
  }
}
