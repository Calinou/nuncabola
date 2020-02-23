/*
 * PrefsReadTool.java
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

import static com.uppgarn.nuncabola.preferences.PrefsIOTool.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

public final class PrefsReadTool {
  private static Boolean getBoolean(String str) {
    switch (str) {
      case "1":
      case "true": {
        return true;
      }
      case "0":
      case "false": {
        return false;
      }
      
      default: {
        return null;
      }
    }
  }
  
  private static Integer getInteger(String str, Pref pref) {
    try {
      int value = Integer.parseInt(str);
      
      switch (pref) {
        case ANISO: {
          return Math.max(value, 0);
        }
        case AUDIO_BUFFER: {
          return Math.min(Math.max(value, 1), 65536);
        }
        case CAMERA_SPEED: {
          return Math.max(value, 0);
        }
        case CONTROLLER_INDEX: {
          return Math.max(value, 0);
        }
        case CONTROLLER_AXIS_X:
        case CONTROLLER_AXIS_Y:
        case CONTROLLER_AXIS_Z: {
          return Math.max(value, -1);
        }
        case CONTROLLER_BUTTON_A:
        case CONTROLLER_BUTTON_EXIT:
        case CONTROLLER_ROTATE_L:
        case CONTROLLER_ROTATE_R:
        case CONTROLLER_ROTATE_FAST:
        case CONTROLLER_CAMERA_1:
        case CONTROLLER_CAMERA_2:
        case CONTROLLER_CAMERA_3:
        case CONTROLLER_CAMERA_TOGGLE: {
          return Math.max(value, 0);
        }
        case MOUSE_SENSE: {
          return Math.max(value, 1);
        }
        case MULTISAMPLE: {
          return Math.max(value, 0);
        }
        case RESPONSE_CONTROLLER:
        case RESPONSE_KEYBOARD:
        case RESPONSE_MOUSE: {
          return Math.max(value, 0);
        }
        case ROTATE_SLOW:
        case ROTATE_FAST: {
          return Math.min(Math.max(value, -1000), +1000);
        }
        case SCREEN_WIDTH:
        case SCREEN_HEIGHT: {
          return Math.max(value, 0);
        }
        case SCREENSHOT: {
          return Math.max(value, 0);
        }
        case TEXTURES: {
          return Math.max(value, 1);
        }
        case VOLUME_SOUND:
        case VOLUME_MUSIC: {
          return Math.min(Math.max(value, 0), 10);
        }
        case WINDOW_X:
        case WINDOW_Y: {
          return Math.max(value, -1);
        }
        
        default: {
          return value;
        }
      }
    } catch (NumberFormatException ex) {
      return null;
    }
  }
  
  private static String getString(String str) {
    return str;
  }
  
  private static Camera getCamera(String str) {
    try {
      int idx = Integer.parseInt(str);
      
      Camera[] cameras = Camera.values();
      
      if ((idx >= 0) && (idx < cameras.length)) {
        return cameras[idx];
      }
      
      return null;
    } catch (NumberFormatException ex) {
      return null;
    }
  }
  
  private static Key getKey(String str) {
    return Key.get(str);
  }
  
  private static MouseButton getMouseButton(String str) {
    return MouseButton.get(str);
  }
  
  private static Object getValue(String str, Pref pref) {
    Class<?> clazz = pref.getValueClass();
    
    if        (clazz == Boolean    .class) {
      return getBoolean    (str);
    } else if (clazz == Integer    .class) {
      return getInteger    (str, pref);
    } else if (clazz == String     .class) {
      return getString     (str);
    } else if (clazz == Camera     .class) {
      return getCamera     (str);
    } else if (clazz == Key        .class) {
      return getKey        (str);
    } else if (clazz == MouseButton.class) {
      return getMouseButton(str);
    }
    
    throw new AssertionError();
  }
  
  private static Prefs readPrefs(BufferedReader in) throws IOException {
    Prefs prefs = new Prefs();
    
    for (String str; (str = in.readLine()) != null;) {
      String[] parts = str.trim().split("\\s+", 2);
      
      String name     = parts[0];
      String valueStr = (parts.length == 1) ? "" : parts[1];
      Pref   pref     = getPref(name);
      
      if (pref != null) {
        Object value = getValue(valueStr, pref);
        
        if (value != null) {
          prefs.set(pref, value);
        }
      }
    }
    
    return prefs;
  }
  
  public static Prefs readPrefs(InputStream inStream) throws IOException {
    BufferedReader in = new BufferedReader(
      new InputStreamReader(inStream, StandardCharsets.UTF_8));
    
    return readPrefs(in);
  }
  
  public static Prefs readPrefs(Path file) throws IOException {
    try (InputStream in = Files.newInputStream(file)) {
      return readPrefs(in);
    }
  }
  
  private PrefsReadTool() {
  }
}
