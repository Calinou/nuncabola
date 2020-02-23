/*
 * Prefs.java
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

package com.uppgarn.nuncabola.preferences;

import com.uppgarn.nuncabola.core.controls.*;
import com.uppgarn.nuncabola.core.game.*;

import java.util.*;

public final class Prefs {
  private Map<Pref, Object> values;
  
  public Prefs() {
    values = createValues();
  }
  
  private Map<Pref, Object> createValues() {
    Map<Pref, Object> values = new EnumMap<>(Pref.class);
    
    for (Pref pref: Pref.values()) {
      values.put(pref, pref.getDefault());
    }
    
    return values;
  }
  
  public Object get(Pref pref) {
    return values.get(pref);
  }
  
  public boolean getBoolean(Pref pref) {
    return (Boolean) get(pref);
  }
  
  public int getInt(Pref pref) {
    return (Integer) get(pref);
  }
  
  public String getString(Pref pref) {
    return (String) get(pref);
  }
  
  public Camera getCamera(Pref pref) {
    return (Camera) get(pref);
  }
  
  public Key getKey(Pref pref) {
    return (Key) get(pref);
  }
  
  public MouseButton getMouseButton(Pref pref) {
    return (MouseButton) get(pref);
  }
  
  public void set(Pref pref, Object value) {
    values.put(pref, value);
  }
}
