/*
 * GUIScreen.java
 *
 * Copyright (c) 2003-2022 Nuncabola authors
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

public abstract class GUIScreen extends Screen {
  private GUI gui;
  
  @Override
  public void enter(Screen from) {
    gui = new GUI();
    
    configureGUI(gui);
    
    gui.build();
  }
  
  protected final GUI getGUI() {
    return gui;
  }
  
  protected abstract void configureGUI(GUI gui);
  
  @Override
  public void paint(float t) {
    gui.draw();
  }
  
  @Override
  public void timer(float dt) {
    gui.step(dt);
  }
  
  @Override
  public void leave(Screen to) {
    gui.deinitialize();
    
    gui = null;
  }
}
