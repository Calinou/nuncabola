/*
 * CommandIOTool.java
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

package com.uppgarn.nuncabola.core.game;

final class CommandIOTool {
  private static final Command.Type[] TYPES = createTypes();
  private static final int         [] IDS   = createIDs();
  
  private static Command.Type[] createTypes() {
    // Keeping the order found below is crucial.
    
    return new Command.Type[] {
      Command.Type.UNKNOWN,
      Command.Type.END_OF_UPDATE,
      Command.Type.BALL_CREATE,
      Command.Type.ITEM_CREATE,
      Command.Type.ITEM_COLLECT,
      Command.Type.TILT_ANGLES,
      Command.Type.SOUND,
      Command.Type.TIMER,
      Command.Type.STATUS,
      Command.Type.COINS,
      Command.Type.TELEPORTER_ENTER,
      Command.Type.TELEPORTER_EXIT,
      Command.Type.BODY_PATH,
      Command.Type.BODY_TIME,
      Command.Type.GOALS_UNLOCK,
      Command.Type.SWITCH_ENTER,
      Command.Type.SWITCH_TOGGLE,
      Command.Type.SWITCH_EXIT,
      Command.Type.RATE,
      Command.Type.BALL_RADIUS,
      Command.Type.ITEMS_CLEAR,
      Command.Type.BALLS_CLEAR,
      Command.Type.BALL_POSITION,
      Command.Type.BALL_BASIS,
      Command.Type.BALL_PENDULUM_BASIS,
      Command.Type.VIEW_POSITION,
      Command.Type.VIEW_CENTER,
      Command.Type.VIEW_BASIS,
      Command.Type.CURRENT_BALL,
      Command.Type.PATH_ENABLE,
      Command.Type.SIMULATION_STEP,
      Command.Type.LEVEL,
      Command.Type.TILT_AXES,
      Command.Type.MOVER_PATH,
      Command.Type.MOVER_TIME
    };
  }
  
  private static int[] createIDs() {
    int[] ids = new int[TYPES.length];
    
    for (int idx = 0; idx < TYPES.length; idx++) {
      ids[TYPES[idx].ordinal()] = idx;
    }
    
    return ids;
  }
  
  public static Command.Type getType(int id) {
    if ((id >= 0) && (id < TYPES.length)) {
      return TYPES[id];
    }
    
    return Command.Type.UNKNOWN;
  }
  
  public static int getID(Command.Type type) {
    return IDS[type.ordinal()];
  }
  
  private CommandIOTool() {
  }
}
