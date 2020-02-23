/*
 * CommandWriteTool.java
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

import static com.uppgarn.nuncabola.core.binary.BinaryWriteTool.*;
import static com.uppgarn.nuncabola.core.game.CommandIOTool.*;

import java.io.*;
import java.nio.charset.*;

public final class CommandWriteTool {
  private static int getLength(String str) {
    return str.getBytes(StandardCharsets.UTF_8).length + 1;
  }
  
  private static int getStatusInt(Status status) {
    switch (status) {
      case NONE: {
        return 0;
      }
      case GOAL: {
        return 2;
      }
      case FALL_OUT: {
        return 3;
      }
      case TIME_OUT: {
        return 1;
      }
      
      default: {
        throw new AssertionError();
      }
    }
  }
  
  public static void writeCommand(OutputStream out, Command cmd)
      throws IOException {
    if (cmd.getType() != Command.Type.UNKNOWN) {
      out.write(getID(cmd.getType()));
    }
    
    switch (cmd.getType()) {
      case UNKNOWN: {
        break;
      }
      case END_OF_UPDATE:
      case BALLS_CLEAR:
      case BALL_CREATE:
      case GOALS_UNLOCK:
      case ITEMS_CLEAR:
      case TELEPORTER_ENTER:
      case TELEPORTER_EXIT: {
        writeShort(out, 0);
        
        break;
      }
      case BALL_POSITION: {
        Command.BallPosition myCmd = (Command.BallPosition) cmd;
        
        writeShort(out, 12);
        
        writeFloat(out, myCmd.x);
        writeFloat(out, myCmd.y);
        writeFloat(out, myCmd.z);
        
        break;
      }
      case BALL_RADIUS: {
        Command.BallRadius myCmd = (Command.BallRadius) cmd;
        
        writeShort(out, 4);
        
        writeFloat(out, myCmd.r);
        
        break;
      }
      case BALL_BASIS: {
        Command.BallBasis myCmd = (Command.BallBasis) cmd;
        
        writeShort(out, 24);
        
        writeFloat(out, myCmd.xX);
        writeFloat(out, myCmd.xY);
        writeFloat(out, myCmd.xZ);
        writeFloat(out, myCmd.yX);
        writeFloat(out, myCmd.yY);
        writeFloat(out, myCmd.yZ);
        
        break;
      }
      case BALL_PENDULUM_BASIS: {
        Command.BallPendulumBasis myCmd = (Command.BallPendulumBasis) cmd;
        
        writeShort(out, 24);
        
        writeFloat(out, myCmd.xX);
        writeFloat(out, myCmd.xY);
        writeFloat(out, myCmd.xZ);
        writeFloat(out, myCmd.yX);
        writeFloat(out, myCmd.yY);
        writeFloat(out, myCmd.yZ);
        
        break;
      }
      case BODY_PATH: {
        Command.BodyPath myCmd = (Command.BodyPath) cmd;
        
        writeShort(out, 8);
        
        writeInt(out, myCmd.bodyIdx);
        writeInt(out, myCmd.pathIdx);
        
        break;
      }
      case BODY_TIME: {
        Command.BodyTime myCmd = (Command.BodyTime) cmd;
        
        writeShort(out, 8);
        
        writeInt  (out, myCmd.bodyIdx);
        writeFloat(out, myCmd.t);
        
        break;
      }
      case COINS: {
        Command.Coins myCmd = (Command.Coins) cmd;
        
        writeShort(out, 4);
        
        writeInt(out, myCmd.coins);
        
        break;
      }
      case CURRENT_BALL: {
        Command.CurrentBall myCmd = (Command.CurrentBall) cmd;
        
        writeShort(out, 4);
        
        writeInt(out, myCmd.ballIdx);
        
        break;
      }
      case ITEM_CREATE: {
        Command.ItemCreate myCmd = (Command.ItemCreate) cmd;
        
        writeShort(out, 20);
        
        writeFloat(out, myCmd.pX);
        writeFloat(out, myCmd.pY);
        writeFloat(out, myCmd.pZ);
        writeInt  (out, myCmd.type);
        writeInt  (out, myCmd.value);
        
        break;
      }
      case ITEM_COLLECT: {
        Command.ItemCollect myCmd = (Command.ItemCollect) cmd;
        
        writeShort(out, 4);
        
        writeInt(out, myCmd.itemIdx);
        
        break;
      }
      case LEVEL: {
        Command.Level myCmd = (Command.Level) cmd;
        
        writeShort(out, getLength(myCmd.path) + 8);
        
        writeString(out, myCmd.path);
        writeInt   (out, myCmd.majorVersion);
        writeInt   (out, myCmd.minorVersion);
        
        break;
      }
      case MOVER_PATH: {
        Command.MoverPath myCmd = (Command.MoverPath) cmd;
        
        writeShort(out, 8);
        
        writeInt(out, myCmd.moverIdx);
        writeInt(out, myCmd.pathIdx);
        
        break;
      }
      case MOVER_TIME: {
        Command.MoverTime myCmd = (Command.MoverTime) cmd;
        
        writeShort(out, 8);
        
        writeInt  (out, myCmd.moverIdx);
        writeFloat(out, myCmd.t);
        
        break;
      }
      case PATH_ENABLE: {
        Command.PathEnable myCmd = (Command.PathEnable) cmd;
        
        writeShort(out, 8);
        
        writeInt(out, myCmd.pathIdx);
        writeInt(out, myCmd.enabled ? 1 : 0);
        
        break;
      }
      case RATE: {
        Command.Rate myCmd = (Command.Rate) cmd;
        
        writeShort(out, 4);
        
        writeInt(out, myCmd.ups);
        
        break;
      }
      case SIMULATION_STEP: {
        Command.SimulationStep myCmd = (Command.SimulationStep) cmd;
        
        writeShort(out, 4);
        
        writeFloat(out, myCmd.dt);
        
        break;
      }
      case SOUND: {
        Command.Sound myCmd = (Command.Sound) cmd;
        
        writeShort(out, getLength(myCmd.path) + 4);
        
        writeString(out, myCmd.path);
        writeFloat (out, myCmd.amp);
        
        break;
      }
      case STATUS: {
        Command.Status myCmd = (Command.Status) cmd;
        
        writeShort(out, 4);
        
        writeInt(out, getStatusInt(myCmd.status));
        
        break;
      }
      case SWITCH_ENTER: {
        Command.SwitchEnter myCmd = (Command.SwitchEnter) cmd;
        
        writeShort(out, 4);
        
        writeInt(out, myCmd.switchIdx);
        
        break;
      }
      case SWITCH_EXIT: {
        Command.SwitchExit myCmd = (Command.SwitchExit) cmd;
        
        writeShort(out, 4);
        
        writeInt(out, myCmd.switchIdx);
        
        break;
      }
      case SWITCH_TOGGLE: {
        Command.SwitchToggle myCmd = (Command.SwitchToggle) cmd;
        
        writeShort(out, 4);
        
        writeInt(out, myCmd.switchIdx);
        
        break;
      }
      case TILT_AXES: {
        Command.TiltAxes myCmd = (Command.TiltAxes) cmd;
        
        writeShort(out, 24);
        
        writeFloat(out, myCmd.xX);
        writeFloat(out, myCmd.xY);
        writeFloat(out, myCmd.xZ);
        writeFloat(out, myCmd.zX);
        writeFloat(out, myCmd.zY);
        writeFloat(out, myCmd.zZ);
        
        break;
      }
      case TILT_ANGLES: {
        Command.TiltAngles myCmd = (Command.TiltAngles) cmd;
        
        writeShort(out, 8);
        
        writeFloat(out, myCmd.x);
        writeFloat(out, myCmd.z);
        
        break;
      }
      case TIMER: {
        Command.Timer myCmd = (Command.Timer) cmd;
        
        writeShort(out, 4);
        
        writeFloat(out, myCmd.timer);
        
        break;
      }
      case VIEW_POSITION: {
        Command.ViewPosition myCmd = (Command.ViewPosition) cmd;
        
        writeShort(out, 12);
        
        writeFloat(out, myCmd.x);
        writeFloat(out, myCmd.y);
        writeFloat(out, myCmd.z);
        
        break;
      }
      case VIEW_CENTER: {
        Command.ViewCenter myCmd = (Command.ViewCenter) cmd;
        
        writeShort(out, 12);
        
        writeFloat(out, myCmd.x);
        writeFloat(out, myCmd.y);
        writeFloat(out, myCmd.z);
        
        break;
      }
      case VIEW_BASIS: {
        Command.ViewBasis myCmd = (Command.ViewBasis) cmd;
        
        writeShort(out, 24);
        
        writeFloat(out, myCmd.xX);
        writeFloat(out, myCmd.xY);
        writeFloat(out, myCmd.xZ);
        writeFloat(out, myCmd.yX);
        writeFloat(out, myCmd.yY);
        writeFloat(out, myCmd.yZ);
        
        break;
      }
    }
  }
  
  private CommandWriteTool() {
  }
}
