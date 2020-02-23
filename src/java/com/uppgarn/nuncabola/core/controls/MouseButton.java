/*
 * MouseButton.java
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

package com.uppgarn.nuncabola.core.controls;

import java.util.*;

public final class MouseButton {
  private static final Map<String, MouseButton> BUTTON_MAP = createButtonMap();
  
  private static MouseButton[] getButtons() {
    return new MouseButton[] {
      new MouseButton("None", -1),
      
      new MouseButton("Left",   0),
      new MouseButton("Right",  1),
      new MouseButton("Middle", 2)
    };
  }
  
  private static Map<String, MouseButton> createButtonMap() {
    Map<String, MouseButton> map = new HashMap<>();
    
    for (MouseButton button: getButtons()) {
      map.put(button.name.toLowerCase(Locale.ENGLISH), button);
    }
    
    return map;
  }
  
  public static MouseButton get(String name) {
    // Search for predefined mouse button.
    
    MouseButton button = BUTTON_MAP.get(name.toLowerCase(Locale.ENGLISH));
    
    if (button != null) {
      return button;
    }
    
    // Interpret name as index.
    
    try {
      int idx = Integer.parseInt(name);
      
      if (idx >= 0) {
        return new MouseButton(Integer.toString(idx), idx);
      }
      
      return null;
    } catch (NumberFormatException ex) {
      return null;
    }
  }
  
  private final String name;
  private final int    idx;
  
  private MouseButton(String name, int idx) {
    this.name = name;
    this.idx  = idx;
  }
  
  public boolean matches(int idx) {
    return (this.idx != -1) && (this.idx == idx);
  }
  
  @Override
  public String toString() {
    return name;
  }
}
