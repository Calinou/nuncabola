/*
 * PrefsIOTool.java
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

import java.util.*;

final class PrefsIOTool {
  private static final Map<String, Pref> PREF_MAP   =
    createPrefMap();
  private static final Map<String, Pref> PREF_MAP_R =
    Collections.unmodifiableMap(PREF_MAP);
  
  private static Map<String, Pref> createPrefMap() {
    Map<String, Pref> map = new LinkedHashMap<>(Pref.values().length);
    
    map.put("fullscreen",             Pref.FULLSCREEN);
    map.put("width",                  Pref.SCREEN_WIDTH);
    map.put("height",                 Pref.SCREEN_HEIGHT);
    map.put("window_x",               Pref.WINDOW_X);
    map.put("window_y",               Pref.WINDOW_Y);
    map.put("camera",                 Pref.CAMERA);
    map.put("textures",               Pref.TEXTURES);
    map.put("reflection",             Pref.REFLECTION);
    map.put("multisample",            Pref.MULTISAMPLE);
    map.put("mipmap",                 Pref.MIPMAP);
    map.put("aniso",                  Pref.ANISO);
    map.put("background",             Pref.BACKGROUND);
    map.put("shadow",                 Pref.SHADOW);
    map.put("audio_buff",             Pref.AUDIO_BUFFER);
    map.put("mouse_sense",            Pref.MOUSE_SENSE);
    map.put("mouse_response",         Pref.RESPONSE_MOUSE);
    map.put("mouse_invert",           Pref.MOUSE_INVERT);
    map.put("vsync",                  Pref.V_SYNC);
    map.put("mouse_camera_1",         Pref.MOUSE_CAMERA_1);
    map.put("mouse_camera_2",         Pref.MOUSE_CAMERA_2);
    map.put("mouse_camera_3",         Pref.MOUSE_CAMERA_3);
    map.put("mouse_camera_toggle",    Pref.MOUSE_CAMERA_TOGGLE);
    map.put("mouse_camera_l",         Pref.MOUSE_ROTATE_L);
    map.put("mouse_camera_r",         Pref.MOUSE_ROTATE_R);
    map.put("fps",                    Pref.FPS);
    map.put("sound_volume",           Pref.VOLUME_SOUND);
    map.put("music_volume",           Pref.VOLUME_MUSIC);
    map.put("joystick",               Pref.CONTROLLER);
    map.put("joystick_device",        Pref.CONTROLLER_INDEX);
    map.put("joystick_response",      Pref.RESPONSE_CONTROLLER);
    map.put("joystick_axis_x",        Pref.CONTROLLER_AXIS_X);
    map.put("joystick_axis_y",        Pref.CONTROLLER_AXIS_Y);
    map.put("joystick_axis_u",        Pref.CONTROLLER_AXIS_Z);
    map.put("joystick_axis_x_invert", Pref.CONTROLLER_AXIS_X_INVERT);
    map.put("joystick_axis_y_invert", Pref.CONTROLLER_AXIS_Y_INVERT);
    map.put("joystick_axis_u_invert", Pref.CONTROLLER_AXIS_Z_INVERT);
    map.put("joystick_button_a",      Pref.CONTROLLER_BUTTON_A);
    map.put("joystick_button_r",      Pref.CONTROLLER_ROTATE_R);
    map.put("joystick_button_l",      Pref.CONTROLLER_ROTATE_L);
    map.put("joystick_button_exit",   Pref.CONTROLLER_BUTTON_EXIT);
    map.put("joystick_camera_1",      Pref.CONTROLLER_CAMERA_1);
    map.put("joystick_camera_2",      Pref.CONTROLLER_CAMERA_2);
    map.put("joystick_camera_3",      Pref.CONTROLLER_CAMERA_3);
    map.put("joystick_camera_toggle", Pref.CONTROLLER_CAMERA_TOGGLE);
    map.put("joystick_rotate_fast",   Pref.CONTROLLER_ROTATE_FAST);
    map.put("keyboard_response",      Pref.RESPONSE_KEYBOARD);
    map.put("key_camera_1",           Pref.KEY_CAMERA_1);
    map.put("key_camera_2",           Pref.KEY_CAMERA_2);
    map.put("key_camera_3",           Pref.KEY_CAMERA_3);
    map.put("key_camera_toggle",      Pref.KEY_CAMERA_TOGGLE);
    map.put("key_camera_r",           Pref.KEY_ROTATE_R);
    map.put("key_camera_l",           Pref.KEY_ROTATE_L);
    map.put("key_forward",            Pref.KEY_FORWARD);
    map.put("key_backward",           Pref.KEY_BACKWARD);
    map.put("key_left",               Pref.KEY_LEFT);
    map.put("key_right",              Pref.KEY_RIGHT);
    map.put("key_pause",              Pref.KEY_PAUSE);
    map.put("key_restart",            Pref.KEY_RESTART);
    map.put("key_score_next",         Pref.KEY_SCORE_TYPE_CYCLE);
    map.put("key_rotate_fast",        Pref.KEY_ROTATE_FAST);
    map.put("view_fov",               Pref.VIEW_FOV);
    map.put("view_dp",                Pref.VIEW_DISTANCE_PY);
    map.put("view_dc",                Pref.VIEW_DISTANCE_CY);
    map.put("view_dz",                Pref.VIEW_DISTANCE_PZ);
    map.put("rotate_fast",            Pref.ROTATE_FAST);
    map.put("rotate_slow",            Pref.ROTATE_SLOW);
    map.put("cheat",                  Pref.CHEAT);
    map.put("stats",                  Pref.STATS);
    map.put("screenshot",             Pref.SCREENSHOT);
    map.put("lock_goals",             Pref.LOCK_GOALS);
    map.put("camera_1_speed",         Pref.CAMERA_SPEED);
    map.put("player",                 Pref.PLAYER);
    map.put("ball_file",              Pref.BALL_PATH);
    map.put("replay_name",            Pref.REPLAY_NAME_PATTERN);
    map.put("theme",                  Pref.THEME);
    map.put("gc_explicit",            Pref.GC_EXPLICIT);
    
    return map;
  }
  
  public static Map<String, Pref> getPrefMap() {
    return PREF_MAP_R;
  }
  
  public static Pref getPref(String name) {
    return PREF_MAP.get(name);
  }
  
  public static int getMaximumNameLength() {
    int len = 0;
    
    for (String name: PREF_MAP.keySet()) {
      len = Math.max(len, name.length());
    }
    
    return len;
  }
  
  private PrefsIOTool() {
  }
}
