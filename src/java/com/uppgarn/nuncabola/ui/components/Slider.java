/*
 * Slider.java
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

package com.uppgarn.nuncabola.ui.components;

import com.uppgarn.nuncabola.core.gui.*;

public final class Slider {
  private final int    start;
  private final int    increment;
  private final int    count;
  private final Object token;
  
  private int value;
  
  private Button[] btns;
  
  public Slider(
      GUI       gui,
      Container parent,
      int       start,
      int       increment,
      int       count,
      Object    token) {
    this.start     = start;
    this.increment = increment;
    this.count     = count;
    this.token     = token;
    
    value = start;
    
    create(gui, parent);
  }
  
  private int getValueForIndex(int idx) {
    return start + idx * increment;
  }
  
  private int getIndexForValue(int value) {
    int end = start + (count - 1) * increment;
    
    int clampedValue;
    
    if (increment > 0) {
      clampedValue = Math.min(Math.max(value, start), end);
    } else {
      clampedValue = Math.min(Math.max(value, end), start);
    }
    
    return Math.round((float) (clampedValue - start) / increment);
  }
  
  private void create(GUI gui, Container parent) {
    Container c0 = gui.hArray(parent);
    
    btns = new Button[count];
    
    for (int idx = 0; idx < count; idx++) {
      btns[idx] = gui.button(c0);
      btns[idx].setTokens(token, getValueForIndex(idx));
    }
    
    update();
  }
  
  private void update() {
    int selIdx = getIndexForValue(value);
    
    for (int idx = 0; idx < count; idx++) {
      btns[idx].setSelected(idx == selIdx);
    }
  }
  
  public void setValue(int value) {
    this.value = value;
    
    update();
  }
}
