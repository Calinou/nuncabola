/*
 * GameClient.java
 *
 * Copyright (c) 2003-2022 Nuncabola authors
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

import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.solid.*;

import java.util.*;

public final class GameClient {
  private final Game               game;
  private final GameClientListener listener;
  
  private final LevelOverride levelOverride;
  private final Solid         sol;
  
  private Rate rate;
  private int  currBallIdx;
  
  private boolean firstUpdate;
  private boolean levelVersionSet;
  private boolean goalsJustUnlocked;
  private boolean teleJustEntered;
  private boolean tiltAxesSet;
  private boolean viewSet;
  
  public GameClient(Game game, GameClientListener listener) {
    this.game     = game;
    this.listener = listener;
    
    levelOverride = game.levelOverride;
    sol           = game.sol;
    
    rate        = Rate.DEFAULT;
    currBallIdx = 0;
    
    firstUpdate       = true;
    levelVersionSet   = false;
    goalsJustUnlocked = false;
    teleJustEntered   = false;
    tiltAxesSet       = false;
    viewSet           = false;
  }
  
  public void execute(Command cmd) {
    switch (cmd.getType()) {
      case END_OF_UPDATE: {
        if (firstUpdate) {
          if (!levelVersionSet) {
            // For compatibility with older replays that do not include
            // level information in the first update.
            
            levelOverride.setMajorVersion(1);
            levelOverride.setMinorVersion(0);
          }
          
          if (!viewSet) {
            // For compatibility with older replays that do not include
            // view information in the first update.
            
            Flyer.fly(game.view, sol, ViewDistance.DEFAULT, 0.0f);
          }
        } else {
          // Step goal-unlocking.
          
          if (game.goalsUnlocked && !goalsJustUnlocked) {
            game.goalFactor = Math.min(game.goalFactor + rate.getTime(), 1.0f);
          }
          
          // Step teleportation.
          
          if (game.teleInProgress && !teleJustEntered) {
            game.teleT += rate.getTime();
            
            if (game.teleT >= 1.0f) {
              game.teleInProgress = false;
            }
          }
          
          // Compute gravity for particles.
          
          Vector3 g = new Vector3();
          
          Gravity.get(g, game.status == Status.GOAL, game.tilt);
          
          // Step particles.
          
          game.parts.step(g, rate.getTime());
        }
        
        firstUpdate       = false;
        levelVersionSet   = false;
        goalsJustUnlocked = false;
        teleJustEntered   = false;
        tiltAxesSet       = false;
        viewSet           = false;
        
        break;
      }
      case BALLS_CLEAR: {
        sol.balls = new Ball[0];
        
        break;
      }
      case BALL_CREATE: {
        // Create a new ball.
        
        Ball ball = new Ball();
        
        // Add it to the list of balls.
        
        int len = sol.balls.length;
        
        sol.balls = Arrays.copyOf(sol.balls, len + 1);
        sol.balls[len] = ball;
        
        // Mark it as the current ball.
        
        currBallIdx = len;
        
        break;
      }
      case BALL_POSITION: {
        Command.BallPosition myCmd = (Command.BallPosition) cmd;
        
        if (currBallIdx < sol.balls.length) {
          sol.balls[currBallIdx].p.set(myCmd.x, myCmd.y, myCmd.z);
        }
        
        break;
      }
      case BALL_RADIUS: {
        Command.BallRadius myCmd = (Command.BallRadius) cmd;
        
        if (currBallIdx < sol.balls.length) {
          sol.balls[currBallIdx].r = myCmd.r;
        }
        
        break;
      }
      case BALL_BASIS: {
        Command.BallBasis myCmd = (Command.BallBasis) cmd;
        
        if (currBallIdx < sol.balls.length) {
          sol.balls[currBallIdx].e.set(
            myCmd.xX,
            myCmd.xY,
            myCmd.xZ,
            myCmd.yX,
            myCmd.yY,
            myCmd.yZ);
        }
        
        break;
      }
      case BALL_PENDULUM_BASIS: {
        Command.BallPendulumBasis myCmd = (Command.BallPendulumBasis) cmd;
        
        if (currBallIdx < sol.balls.length) {
          sol.balls[currBallIdx].e_.set(
            myCmd.xX,
            myCmd.xY,
            myCmd.xZ,
            myCmd.yX,
            myCmd.yY,
            myCmd.yZ);
        }
        
        break;
      }
      case BODY_PATH: {
        Command.BodyPath myCmd = (Command.BodyPath) cmd;
        
        // For compatibility with older replays that do not include
        // explicit mover information.
        
        if ((myCmd.bodyIdx >= 0) && (myCmd.bodyIdx < sol.base.bodies.length)) {
          // Update linear mover only.
          
          int mover0Idx = sol.base.bodies[myCmd.bodyIdx].mover0Idx;
          
          if ((mover0Idx >= 0)
              && (myCmd.pathIdx >= 0)
              && (myCmd.pathIdx < sol.paths.length)) {
            sol.movers[mover0Idx].pathIdx = myCmd.pathIdx;
          }
        }
        
        break;
      }
      case BODY_TIME: {
        Command.BodyTime myCmd = (Command.BodyTime) cmd;
        
        // For compatibility with older replays that do not include
        // explicit mover information.
        
        if ((myCmd.bodyIdx >= 0) && (myCmd.bodyIdx < sol.base.bodies.length)) {
          // Update linear mover only.
          
          int mover0Idx = sol.base.bodies[myCmd.bodyIdx].mover0Idx;
          
          if (mover0Idx >= 0) {
            sol.movers[mover0Idx].t = myCmd.t;
          }
        }
        
        break;
      }
      case COINS: {
        Command.Coins myCmd = (Command.Coins) cmd;
        
        if (!game.status.isOver()) {
          game.coins = myCmd.coins;
        }
        
        break;
      }
      case CURRENT_BALL: {
        Command.CurrentBall myCmd = (Command.CurrentBall) cmd;
        
        if ((myCmd.ballIdx >= 0) && (myCmd.ballIdx < sol.balls.length)) {
          currBallIdx = myCmd.ballIdx;
        }
        
        break;
      }
      case GOALS_UNLOCK: {
        // Unlock goals and, if this is the first update,
        // instantly max out the goal factor.
        
        if (!game.goalsUnlocked) {
          game.goalsUnlocked = true;
          game.goalFactor    = firstUpdate ? 1.0f : 0.0f;
          
          goalsJustUnlocked = true;
        }
        
        break;
      }
      case ITEMS_CLEAR: {
        sol.items = new Item[0];
        
        break;
      }
      case ITEM_CREATE: {
        Command.ItemCreate myCmd = (Command.ItemCreate) cmd;
        
        // Create a new item.
        
        Item item = new Item();
        
        item.p.set(myCmd.pX, myCmd.pY, myCmd.pZ);
        
        item.type  = myCmd.type;
        item.value = myCmd.value;
        
        // Add it to the list of items.
        
        int len = sol.items.length;
        
        sol.items = Arrays.copyOf(sol.items, len + 1);
        sol.items[len] = item;
        
        break;
      }
      case ITEM_COLLECT: {
        Command.ItemCollect myCmd = (Command.ItemCollect) cmd;
        
        if ((myCmd.itemIdx >= 0) && (myCmd.itemIdx < sol.items.length)) {
          Item item = sol.items[myCmd.itemIdx];
          
          // Set up particle effects.
          
          game.parts.burst(item);
          
          // Discard item.
          
          item.type = ItemBase.NONE;
        }
        
        break;
      }
      case LEVEL: {
        Command.Level myCmd = (Command.Level) cmd;
        
        levelOverride.setMajorVersion(myCmd.majorVersion);
        levelOverride.setMinorVersion(myCmd.minorVersion);
        
        levelVersionSet = true;
        
        break;
      }
      case MOVER_PATH: {
        Command.MoverPath myCmd = (Command.MoverPath) cmd;
        
        if ((myCmd.moverIdx >= 0)
            && (myCmd.moverIdx < sol.movers.length)
            && (myCmd.pathIdx >= 0)
            && (myCmd.pathIdx < sol.paths.length)) {
          sol.movers[myCmd.moverIdx].pathIdx = myCmd.pathIdx;
        }
        
        break;
      }
      case MOVER_TIME: {
        Command.MoverTime myCmd = (Command.MoverTime) cmd;
        
        if ((myCmd.moverIdx >= 0) && (myCmd.moverIdx < sol.movers.length)) {
          sol.movers[myCmd.moverIdx].t = myCmd.t;
        }
        
        break;
      }
      case PATH_ENABLE: {
        Command.PathEnable myCmd = (Command.PathEnable) cmd;
        
        if ((myCmd.pathIdx >= 0) && (myCmd.pathIdx < sol.paths.length)) {
          sol.paths[myCmd.pathIdx].enabled = myCmd.enabled;
        }
        
        break;
      }
      case RATE: {
        Command.Rate myCmd = (Command.Rate) cmd;
        
        if (myCmd.ups > 0) {
          rate = new Rate(myCmd.ups);
        }
        
        break;
      }
      case SIMULATION_STEP: {
        Command.SimulationStep myCmd = (Command.SimulationStep) cmd;
        
        // Step each mover ahead. This way we cut down on replay
        // size significantly while still keeping things in sync
        // with occasional MoverPath and MoverTime commands.
        
        for (Mover mover: sol.movers) {
          if (sol.paths[mover.pathIdx].enabled) {
            mover.t += myCmd.dt;
          }
        }
        
        break;
      }
      case SOUND: {
        Command.Sound myCmd = (Command.Sound) cmd;
        
        if (listener != null) {
          listener.soundRequested(myCmd.path, myCmd.amp);
        }
        
        break;
      }
      case STATUS: {
        Command.Status myCmd = (Command.Status) cmd;
        
        if (!game.status.isOver()) {
          game.status = myCmd.status;
        }
        
        break;
      }
      case SWITCH_ENTER: {
        Command.SwitchEnter myCmd = (Command.SwitchEnter) cmd;
        
        if ((myCmd.switchIdx >= 0) && (myCmd.switchIdx < sol.switches.length)) {
          sol.switches[myCmd.switchIdx].ballInside = true;
        }
        
        break;
      }
      case SWITCH_EXIT: {
        Command.SwitchExit myCmd = (Command.SwitchExit) cmd;
        
        if ((myCmd.switchIdx >= 0) && (myCmd.switchIdx < sol.switches.length)) {
          sol.switches[myCmd.switchIdx].ballInside = false;
        }
        
        break;
      }
      case SWITCH_TOGGLE: {
        Command.SwitchToggle myCmd = (Command.SwitchToggle) cmd;
        
        if ((myCmd.switchIdx >= 0) && (myCmd.switchIdx < sol.switches.length)) {
          Switch zwitch = sol.switches[myCmd.switchIdx];
          
          zwitch.enabled = !zwitch.enabled;
        }
        
        break;
      }
      case TELEPORTER_ENTER: {
        game.teleEnabled    = false;
        game.teleInProgress = true;
        game.teleT          = 0.0f;
        
        teleJustEntered = true;
        
        break;
      }
      case TELEPORTER_EXIT: {
        game.teleEnabled = true;
        
        break;
      }
      case TILT_AXES: {
        Command.TiltAxes myCmd = (Command.TiltAxes) cmd;
        
        game.tilt.x.set(myCmd.xX, myCmd.xY, myCmd.xZ);
        game.tilt.z.set(myCmd.zX, myCmd.zY, myCmd.zZ);
        
        tiltAxesSet = true;
        
        break;
      }
      case TILT_ANGLES: {
        Command.TiltAngles myCmd = (Command.TiltAngles) cmd;
        
        game.tilt.rx = myCmd.x;
        game.tilt.rz = myCmd.z;
        
        // For compatibility with older replays that do not include
        // explicit tilt axes information.
        
        if (!tiltAxesSet) {
          game.tilt.x.copyFrom(game.view.e.x);
          game.tilt.z.copyFrom(game.view.e.z);
        }
        
        break;
      }
      case TIMER: {
        Command.Timer myCmd = (Command.Timer) cmd;
        
        if (!game.status.isOver()) {
          int timer = (int) (myCmd.timer * 100.0f);
          
          if (levelOverride.getTime() == 0) {
            game.time = timer;
          } else {
            int time       = levelOverride.getTime() + game.gainedTime - timer;
            int gainedTime = Math.round((game.time - time) / 100.0f) * 100;
            
            if (gainedTime <= 0) {
              game.time = time;
            } else {
              game.gainedTime += gainedTime;
            }
          }
        }
        
        break;
      }
      case VIEW_POSITION: {
        Command.ViewPosition myCmd = (Command.ViewPosition) cmd;
        
        game.view.p.set(myCmd.x, myCmd.y, myCmd.z);
        
        viewSet = true;
        
        break;
      }
      case VIEW_CENTER: {
        Command.ViewCenter myCmd = (Command.ViewCenter) cmd;
        
        game.view.c.set(myCmd.x, myCmd.y, myCmd.z);
        
        break;
      }
      case VIEW_BASIS: {
        Command.ViewBasis myCmd = (Command.ViewBasis) cmd;
        
        game.view.e.set(
          myCmd.xX,
          myCmd.xY,
          myCmd.xZ,
          myCmd.yX,
          myCmd.yY,
          myCmd.yZ);
        
        break;
      }
      
      default: {
        break;
      }
    }
  }
}
