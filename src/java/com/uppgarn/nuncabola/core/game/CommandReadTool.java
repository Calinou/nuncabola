/*
 * CommandReadTool.java
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

import static com.uppgarn.nuncabola.core.binary.BinaryReadTool.*;
import static com.uppgarn.nuncabola.core.game.CommandIOTool.*;

import com.uppgarn.codelibf.io.*;

import java.io.*;

public final class CommandReadTool {
  private static Status getStatus(int value) {
    switch (value) {
      case 0: {
        return Status.NONE;
      }
      case 1: {
        return Status.TIME_OUT;
      }
      case 2: {
        return Status.GOAL;
      }
      case 3: {
        return Status.FALL_OUT;
      }
      
      default: {
        return Status.NONE;
      }
    }
  }
  
  public static Command readCommand(InputStream in) throws IOException {
    int id  = readUnsignedByte (in);
    int len = readUnsignedShort(in);
    
    switch (getType(id)) {
      case UNKNOWN: {
        InputStreamTool.skip(in, len);
        
        return Command.Unknown.INSTANCE;
      }
      case END_OF_UPDATE: {
        return Command.EndOfUpdate.INSTANCE;
      }
      case BALLS_CLEAR: {
        return Command.BallsClear.INSTANCE;
      }
      case BALL_CREATE: {
        return Command.BallCreate.INSTANCE;
      }
      case BALL_POSITION: {
        float x = readFloat(in);
        float y = readFloat(in);
        float z = readFloat(in);
        
        return new Command.BallPosition(x, y, z);
      }
      case BALL_RADIUS: {
        float r = readFloat(in);
        
        return new Command.BallRadius(r);
      }
      case BALL_BASIS: {
        float xX = readFloat(in);
        float xY = readFloat(in);
        float xZ = readFloat(in);
        float yX = readFloat(in);
        float yY = readFloat(in);
        float yZ = readFloat(in);
        
        return new Command.BallBasis(xX, xY, xZ, yX, yY, yZ);
      }
      case BALL_PENDULUM_BASIS: {
        float xX = readFloat(in);
        float xY = readFloat(in);
        float xZ = readFloat(in);
        float yX = readFloat(in);
        float yY = readFloat(in);
        float yZ = readFloat(in);
        
        return new Command.BallPendulumBasis(xX, xY, xZ, yX, yY, yZ);
      }
      case BODY_PATH: {
        int bodyIdx = readInt(in);
        int pathIdx = readInt(in);
        
        return new Command.BodyPath(bodyIdx, pathIdx);
      }
      case BODY_TIME: {
        int   bodyIdx = readInt  (in);
        float t       = readFloat(in);
        
        return new Command.BodyTime(bodyIdx, t);
      }
      case COINS: {
        int coins = readInt(in);
        
        return new Command.Coins(coins);
      }
      case CURRENT_BALL: {
        int ballIdx = readInt(in);
        
        return new Command.CurrentBall(ballIdx);
      }
      case GOALS_UNLOCK: {
        return Command.GoalsUnlock.INSTANCE;
      }
      case ITEMS_CLEAR: {
        return Command.ItemsClear.INSTANCE;
      }
      case ITEM_CREATE: {
        float pX    = readFloat(in);
        float pY    = readFloat(in);
        float pZ    = readFloat(in);
        int   type  = readInt  (in);
        int   value = readInt  (in);
        
        return new Command.ItemCreate(pX, pY, pZ, type, value);
      }
      case ITEM_COLLECT: {
        int itemIdx = readInt(in);
        
        return new Command.ItemCollect(itemIdx);
      }
      case LEVEL: {
        String path         = readString(in);
        int    majorVersion = readInt   (in);
        int    minorVersion = readInt   (in);
        
        return new Command.Level(path, majorVersion, minorVersion);
      }
      case MOVER_PATH: {
        int moverIdx = readInt(in);
        int pathIdx  = readInt(in);
        
        return new Command.MoverPath(moverIdx, pathIdx);
      }
      case MOVER_TIME: {
        int   moverIdx = readInt  (in);
        float t        = readFloat(in);
        
        return new Command.MoverTime(moverIdx, t);
      }
      case PATH_ENABLE: {
        int     pathIdx = readInt(in);
        boolean enabled = readInt(in) != 0;
        
        return new Command.PathEnable(pathIdx, enabled);
      }
      case RATE: {
        int ups = readInt(in);
        
        return new Command.Rate(ups);
      }
      case SIMULATION_STEP: {
        float dt = readFloat(in);
        
        return new Command.SimulationStep(dt);
      }
      case SOUND: {
        String path = readString(in);
        float  amp  = readFloat (in);
        
        return new Command.Sound(path, amp);
      }
      case STATUS: {
        Status status = getStatus(readInt(in));
        
        return new Command.Status(status);
      }
      case SWITCH_ENTER: {
        int switchIdx = readInt(in);
        
        return new Command.SwitchEnter(switchIdx);
      }
      case SWITCH_EXIT: {
        int switchIdx = readInt(in);
        
        return new Command.SwitchExit(switchIdx);
      }
      case SWITCH_TOGGLE: {
        int switchIdx = readInt(in);
        
        return new Command.SwitchToggle(switchIdx);
      }
      case TELEPORTER_ENTER: {
        return Command.TeleporterEnter.INSTANCE;
      }
      case TELEPORTER_EXIT: {
        return Command.TeleporterExit.INSTANCE;
      }
      case TILT_AXES: {
        float xX = readFloat(in);
        float xY = readFloat(in);
        float xZ = readFloat(in);
        float zX = readFloat(in);
        float zY = readFloat(in);
        float zZ = readFloat(in);
        
        return new Command.TiltAxes(xX, xY, xZ, zX, zY, zZ);
      }
      case TILT_ANGLES: {
        float x = readFloat(in);
        float z = readFloat(in);
        
        return new Command.TiltAngles(x, z);
      }
      case TIMER: {
        float timer = readFloat(in);
        
        return new Command.Timer(timer);
      }
      case VIEW_POSITION: {
        float x = readFloat(in);
        float y = readFloat(in);
        float z = readFloat(in);
        
        return new Command.ViewPosition(x, y, z);
      }
      case VIEW_CENTER: {
        float x = readFloat(in);
        float y = readFloat(in);
        float z = readFloat(in);
        
        return new Command.ViewCenter(x, y, z);
      }
      case VIEW_BASIS: {
        float xX = readFloat(in);
        float xY = readFloat(in);
        float xZ = readFloat(in);
        float yX = readFloat(in);
        float yY = readFloat(in);
        float yZ = readFloat(in);
        
        return new Command.ViewBasis(xX, xY, xZ, yX, yY, yZ);
      }
      
      default: {
        throw new AssertionError();
      }
    }
  }
  
  private CommandReadTool() {
  }
}
