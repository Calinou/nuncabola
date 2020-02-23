/*
 * Pref.java
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

public enum Pref {
  ANISO                   (8),
  AUDIO_BUFFER            (2048),
  BACKGROUND              (true),
  BALL_PATH               ("ball/basic-ball/basic-ball"),
  CAMERA                  (Camera.CHASE),
  CAMERA_SPEED            (500),
  CHEAT                   (false),
  CONTROLLER              (true),
  CONTROLLER_INDEX        (0),
  CONTROLLER_AXIS_X       (-1),
  CONTROLLER_AXIS_X_INVERT(false),
  CONTROLLER_AXIS_Y       (-1),
  CONTROLLER_AXIS_Y_INVERT(false),
  CONTROLLER_AXIS_Z       (-1),
  CONTROLLER_AXIS_Z_INVERT(false),
  CONTROLLER_BUTTON_A     (0),
  CONTROLLER_BUTTON_EXIT  (4),
  CONTROLLER_ROTATE_L     (3),
  CONTROLLER_ROTATE_R     (2),
  CONTROLLER_ROTATE_FAST  (9),
  CONTROLLER_CAMERA_1     (5),
  CONTROLLER_CAMERA_2     (6),
  CONTROLLER_CAMERA_3     (7),
  CONTROLLER_CAMERA_TOGGLE(8),
  FPS                     (false),
  FULLSCREEN              (false),
  GC_EXPLICIT             (false),
  KEY_LEFT                (Key.get("Left")),
  KEY_RIGHT               (Key.get("Right")),
  KEY_FORWARD             (Key.get("Up")),
  KEY_BACKWARD            (Key.get("Down")),
  KEY_ROTATE_L            (Key.get("s")),
  KEY_ROTATE_R            (Key.get("d")),
  KEY_ROTATE_FAST         (Key.get("Shift")),
  KEY_CAMERA_1            (Key.get("1")),
  KEY_CAMERA_2            (Key.get("2")),
  KEY_CAMERA_3            (Key.get("3")),
  KEY_CAMERA_TOGGLE       (Key.get("x")),
  KEY_PAUSE               (Key.get("Escape")),
  KEY_RESTART             (Key.get("r")),
  KEY_SCORE_TYPE_CYCLE    (Key.get("Tab")),
  LOCK_GOALS              (true),
  MIPMAP                  (true),
  MOUSE_INVERT            (false),
  MOUSE_SENSE             (300),
  MOUSE_ROTATE_L          (MouseButton.get("Left")),
  MOUSE_ROTATE_R          (MouseButton.get("Right")),
  MOUSE_CAMERA_1          (MouseButton.get("None")),
  MOUSE_CAMERA_2          (MouseButton.get("None")),
  MOUSE_CAMERA_3          (MouseButton.get("None")),
  MOUSE_CAMERA_TOGGLE     (MouseButton.get("Middle")),
  MULTISAMPLE             (0),
  PLAYER                  (""),
  REFLECTION              (true),
  REPLAY_NAME_PATTERN     ("%s-%l"),
  RESPONSE_CONTROLLER     (50),
  RESPONSE_KEYBOARD       (50),
  RESPONSE_MOUSE          (50),
  ROTATE_SLOW             (158),
  ROTATE_FAST             (315),
  SCREEN_WIDTH            (0),
  SCREEN_HEIGHT           (0),
  SCREENSHOT              (0),
  SHADOW                  (true),
  STATS                   (false),
  TEXTURES                (1),
  THEME                   ("classic"),
  VIEW_DISTANCE_PY        (75),
  VIEW_DISTANCE_PZ        (200),
  VIEW_DISTANCE_CY        (25),
  VIEW_FOV                (50),
  VOLUME_SOUND            (10),
  VOLUME_MUSIC            (6),
  V_SYNC                  (true),
  WINDOW_X                (-1),
  WINDOW_Y                (-1);
  
  private final Object def;
  
  Pref(Object def) {
    this.def = def;
  }
  
  public Class<?> getValueClass() {
    return def.getClass();
  }
  
  public Object getDefault() {
    return def;
  }
}
