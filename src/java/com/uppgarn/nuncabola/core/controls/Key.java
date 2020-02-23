/*
 * Key.java
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

import com.uppgarn.codelibf.util.*;

import org.lwjgl.input.*;

import java.util.*;

public final class Key {
  private static final Map<String, Key> KEY_MAP = createKeyMap();
  
  private static Key[] getKeys() {
    return new Key[] {
      new Key("None", 0),
      
      new Key("0",             Keyboard.KEY_0),
      new Key("1",             Keyboard.KEY_1),
      new Key("2",             Keyboard.KEY_2),
      new Key("3",             Keyboard.KEY_3),
      new Key("4",             Keyboard.KEY_4),
      new Key("5",             Keyboard.KEY_5),
      new Key("6",             Keyboard.KEY_6),
      new Key("7",             Keyboard.KEY_7),
      new Key("8",             Keyboard.KEY_8),
      new Key("9",             Keyboard.KEY_9),
      new Key("Backspace",     Keyboard.KEY_BACK),
      new Key("Caps Lock",     Keyboard.KEY_CAPITAL),
      new Key("Delete",        Keyboard.KEY_DELETE),
      new Key("Down",          Keyboard.KEY_DOWN),
      new Key("End",           Keyboard.KEY_END),
      new Key("Escape",        Keyboard.KEY_ESCAPE),
      new Key("F1",            Keyboard.KEY_F1),
      new Key("F2",            Keyboard.KEY_F2),
      new Key("F3",            Keyboard.KEY_F3),
      new Key("F4",            Keyboard.KEY_F4),
      new Key("F5",            Keyboard.KEY_F5),
      new Key("F6",            Keyboard.KEY_F6),
      new Key("F7",            Keyboard.KEY_F7),
      new Key("F8",            Keyboard.KEY_F8),
      new Key("F9",            Keyboard.KEY_F9),
      new Key("F10",           Keyboard.KEY_F10),
      new Key("F11",           Keyboard.KEY_F11),
      new Key("F12",           Keyboard.KEY_F12),
      new Key("Home",          Keyboard.KEY_HOME),
      new Key("Insert",        Keyboard.KEY_INSERT),
      new Key("Left",          Keyboard.KEY_LEFT),
      new Key("Left Alt",      Keyboard.KEY_LMENU),
      new Key("Left Control",  Keyboard.KEY_LCONTROL),
      new Key("Left Meta",     Keyboard.KEY_LMETA),
      new Key("Left Shift",    Keyboard.KEY_LSHIFT),
      new Key("Menu",          Keyboard.KEY_APPS),
      new Key("Num Lock",      Keyboard.KEY_NUMLOCK),
      new Key("Numpad 0",      Keyboard.KEY_NUMPAD0),
      new Key("Numpad 1",      Keyboard.KEY_NUMPAD1),
      new Key("Numpad 2",      Keyboard.KEY_NUMPAD2),
      new Key("Numpad 3",      Keyboard.KEY_NUMPAD3),
      new Key("Numpad 4",      Keyboard.KEY_NUMPAD4),
      new Key("Numpad 5",      Keyboard.KEY_NUMPAD5),
      new Key("Numpad 6",      Keyboard.KEY_NUMPAD6),
      new Key("Numpad 7",      Keyboard.KEY_NUMPAD7),
      new Key("Numpad 8",      Keyboard.KEY_NUMPAD8),
      new Key("Numpad 9",      Keyboard.KEY_NUMPAD9),
      new Key("Numpad .",      Keyboard.KEY_DECIMAL),
      new Key("Numpad +",      Keyboard.KEY_ADD),
      new Key("Numpad -",      Keyboard.KEY_SUBTRACT),
      new Key("Numpad *",      Keyboard.KEY_MULTIPLY),
      new Key("Numpad /",      Keyboard.KEY_DIVIDE),
      new Key("Numpad Enter",  Keyboard.KEY_NUMPADENTER),
      new Key("Page Down",     Keyboard.KEY_NEXT),
      new Key("Page Up",       Keyboard.KEY_PRIOR),
      new Key("Pause",         Keyboard.KEY_PAUSE),
      new Key("Return",        Keyboard.KEY_RETURN),
      new Key("Right",         Keyboard.KEY_RIGHT),
      new Key("Right Alt",     Keyboard.KEY_RMENU),
      new Key("Right Control", Keyboard.KEY_RCONTROL),
      new Key("Right Meta",    Keyboard.KEY_RMETA),
      new Key("Right Shift",   Keyboard.KEY_RSHIFT),
      new Key("Scroll Lock",   Keyboard.KEY_SCROLL),
      new Key("Space",         Keyboard.KEY_SPACE),
      new Key("Sys Req",       Keyboard.KEY_SYSRQ),
      new Key("Tab",           Keyboard.KEY_TAB),
      new Key("Up",            Keyboard.KEY_UP),
      
      new Key("Alt",     Keyboard.KEY_LMENU,    Keyboard.KEY_RMENU),
      new Key("Control", Keyboard.KEY_LCONTROL, Keyboard.KEY_RCONTROL),
      new Key("Meta",    Keyboard.KEY_LMETA,    Keyboard.KEY_RMETA),
      new Key("Shift",   Keyboard.KEY_LSHIFT,   Keyboard.KEY_RSHIFT)
    };
  }
  
  private static Map<String, Key> createKeyMap() {
    Map<String, Key> map = new HashMap<>();
    
    for (Key key: getKeys()) {
      String name0 = key.name.toLowerCase(Locale.ENGLISH);
      String name1 = StringTool.remove(name0, ' ');
      
      map.put(name0, key);
      
      if (!name1.equals(name0)) {
        map.put(name1, key);
      }
    }
    
    return map;
  }
  
  public static Key get(String name) {
    // Search for predefined key.
    
    Key key = KEY_MAP.get(name.toLowerCase(Locale.ENGLISH));
    
    if (key != null) {
      return key;
    }
    
    // Interpret name as character.
    
    if (name.length() == 1) {
      char ch = Character.toLowerCase(name.charAt(0));
      
      return new Key(Character.toString(ch), 0, 0, ch);
    }
    
    return null;
  }
  
  private final String name;
  private final int    code0;
  private final int    code1;
  private final char   ch;
  
  private Key(String name, int code0) {
    this(name, code0, 0);
  }
  
  private Key(String name, int code0, int code1) {
    this(name, code0, code1, (char) 0);
  }
  
  private Key(String name, int code0, int code1, char ch) {
    this.name  = name;
    this.code0 = code0;
    this.code1 = code1;
    this.ch    = ch;
  }
  
  public boolean matches(int code, char ch) {
    return ((code0   != 0) && (code0   == code))
        || ((code1   != 0) && (code1   == code))
        || ((this.ch != 0) && (this.ch == Character.toLowerCase(ch)));
  }
  
  @Override
  public String toString() {
    return name;
  }
}
