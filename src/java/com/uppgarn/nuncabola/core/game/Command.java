/*
 * Command.java
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

import com.uppgarn.nuncabola.core.math.*;

public abstract class Command {
  static {
    // Preload all subclasses.
    
    Unknown          .class.hashCode();
    EndOfUpdate      .class.hashCode();
    BallsClear       .class.hashCode();
    BallCreate       .class.hashCode();
    BallPosition     .class.hashCode();
    BallRadius       .class.hashCode();
    BallBasis        .class.hashCode();
    BallPendulumBasis.class.hashCode();
    BodyPath         .class.hashCode();
    BodyTime         .class.hashCode();
    Coins            .class.hashCode();
    CurrentBall      .class.hashCode();
    GoalsUnlock      .class.hashCode();
    ItemsClear       .class.hashCode();
    ItemCreate       .class.hashCode();
    ItemCollect      .class.hashCode();
    Level            .class.hashCode();
    MoverPath        .class.hashCode();
    MoverTime        .class.hashCode();
    PathEnable       .class.hashCode();
    Rate             .class.hashCode();
    SimulationStep   .class.hashCode();
    Sound            .class.hashCode();
    Status           .class.hashCode();
    SwitchEnter      .class.hashCode();
    SwitchExit       .class.hashCode();
    SwitchToggle     .class.hashCode();
    TeleporterEnter  .class.hashCode();
    TeleporterExit   .class.hashCode();
    TiltAxes         .class.hashCode();
    TiltAngles       .class.hashCode();
    Timer            .class.hashCode();
    ViewPosition     .class.hashCode();
    ViewCenter       .class.hashCode();
    ViewBasis        .class.hashCode();
  }
  
  private Command() {
  }
  
  public abstract Type getType();
  
  public enum Type {
    UNKNOWN,
    END_OF_UPDATE,
    BALLS_CLEAR,
    BALL_CREATE,
    BALL_POSITION,
    BALL_RADIUS,
    BALL_BASIS,
    BALL_PENDULUM_BASIS,
    BODY_PATH,
    BODY_TIME,
    COINS,
    CURRENT_BALL,
    GOALS_UNLOCK,
    ITEMS_CLEAR,
    ITEM_CREATE,
    ITEM_COLLECT,
    LEVEL,
    MOVER_PATH,
    MOVER_TIME,
    PATH_ENABLE,
    RATE,
    SIMULATION_STEP,
    SOUND,
    STATUS,
    SWITCH_ENTER,
    SWITCH_EXIT,
    SWITCH_TOGGLE,
    TELEPORTER_ENTER,
    TELEPORTER_EXIT,
    TILT_AXES,
    TILT_ANGLES,
    TIMER,
    VIEW_POSITION,
    VIEW_CENTER,
    VIEW_BASIS
  }
  
  public static final class Unknown extends Command {
    public static final Unknown INSTANCE = new Unknown();
    
    private Unknown() {
    }
    
    @Override
    public Type getType() {
      return Type.UNKNOWN;
    }
  }
  
  public static final class EndOfUpdate extends Command {
    public static final EndOfUpdate INSTANCE = new EndOfUpdate();
    
    private EndOfUpdate() {
    }
    
    @Override
    public Type getType() {
      return Type.END_OF_UPDATE;
    }
  }
  
  public static final class BallsClear extends Command {
    public static final BallsClear INSTANCE = new BallsClear();
    
    private BallsClear() {
    }
    
    @Override
    public Type getType() {
      return Type.BALLS_CLEAR;
    }
  }
  
  public static final class BallCreate extends Command {
    public static final BallCreate INSTANCE = new BallCreate();
    
    private BallCreate() {
    }
    
    @Override
    public Type getType() {
      return Type.BALL_CREATE;
    }
  }
  
  public static final class BallPosition extends Command {
    public final float x;
    public final float y;
    public final float z;
    
    public BallPosition(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    
    public BallPosition(Vector3 p) {
      this(p.x, p.y, p.z);
    }
    
    @Override
    public Type getType() {
      return Type.BALL_POSITION;
    }
  }
  
  public static final class BallRadius extends Command {
    public final float r;
    
    public BallRadius(float r) {
      this.r = r;
    }
    
    @Override
    public Type getType() {
      return Type.BALL_RADIUS;
    }
  }
  
  public static final class BallBasis extends Command {
    public final float xX;
    public final float xY;
    public final float xZ;
    public final float yX;
    public final float yY;
    public final float yZ;
    
    public BallBasis(
        float xX,
        float xY,
        float xZ,
        float yX,
        float yY,
        float yZ) {
      this.xX = xX;
      this.xY = xY;
      this.xZ = xZ;
      this.yX = yX;
      this.yY = yY;
      this.yZ = yZ;
    }
    
    public BallBasis(Basis3 e) {
      this(e.x.x, e.x.y, e.x.z, e.y.x, e.y.y, e.y.z);
    }
    
    @Override
    public Type getType() {
      return Type.BALL_BASIS;
    }
  }
  
  public static final class BallPendulumBasis extends Command {
    public final float xX;
    public final float xY;
    public final float xZ;
    public final float yX;
    public final float yY;
    public final float yZ;
    
    public BallPendulumBasis(
        float xX,
        float xY,
        float xZ,
        float yX,
        float yY,
        float yZ) {
      this.xX = xX;
      this.xY = xY;
      this.xZ = xZ;
      this.yX = yX;
      this.yY = yY;
      this.yZ = yZ;
    }
    
    public BallPendulumBasis(Basis3 e_) {
      this(e_.x.x, e_.x.y, e_.x.z, e_.y.x, e_.y.y, e_.y.z);
    }
    
    @Override
    public Type getType() {
      return Type.BALL_PENDULUM_BASIS;
    }
  }
  
  public static final class BodyPath extends Command {
    public final int bodyIdx;
    public final int pathIdx;
    
    public BodyPath(int bodyIdx, int pathIdx) {
      this.bodyIdx = bodyIdx;
      this.pathIdx = pathIdx;
    }
    
    @Override
    public Type getType() {
      return Type.BODY_PATH;
    }
  }
  
  public static final class BodyTime extends Command {
    public final int   bodyIdx;
    public final float t;
    
    public BodyTime(int bodyIdx, float t) {
      this.bodyIdx = bodyIdx;
      this.t       = t;
    }
    
    @Override
    public Type getType() {
      return Type.BODY_TIME;
    }
  }
  
  public static final class Coins extends Command {
    public final int coins;
    
    public Coins(int coins) {
      this.coins = coins;
    }
    
    @Override
    public Type getType() {
      return Type.COINS;
    }
  }
  
  public static final class CurrentBall extends Command {
    public final int ballIdx;
    
    public CurrentBall(int ballIdx) {
      this.ballIdx = ballIdx;
    }
    
    @Override
    public Type getType() {
      return Type.CURRENT_BALL;
    }
  }
  
  public static final class GoalsUnlock extends Command {
    public static final GoalsUnlock INSTANCE = new GoalsUnlock();
    
    private GoalsUnlock() {
    }
    
    @Override
    public Type getType() {
      return Type.GOALS_UNLOCK;
    }
  }
  
  public static final class ItemsClear extends Command {
    public static final ItemsClear INSTANCE = new ItemsClear();
    
    private ItemsClear() {
    }
    
    @Override
    public Type getType() {
      return Type.ITEMS_CLEAR;
    }
  }
  
  public static final class ItemCreate extends Command {
    public final float pX;
    public final float pY;
    public final float pZ;
    public final int   type;
    public final int   value;
    
    public ItemCreate(float pX, float pY, float pZ, int type, int value) {
      this.pX    = pX;
      this.pY    = pY;
      this.pZ    = pZ;
      this.type  = type;
      this.value = value;
    }
    
    public ItemCreate(Vector3 p, int type, int value) {
      this(p.x, p.y, p.z, type, value);
    }
    
    @Override
    public Type getType() {
      return Type.ITEM_CREATE;
    }
  }
  
  public static final class ItemCollect extends Command {
    public final int itemIdx;
    
    public ItemCollect(int itemIdx) {
      this.itemIdx = itemIdx;
    }
    
    @Override
    public Type getType() {
      return Type.ITEM_COLLECT;
    }
  }
  
  public static final class Level extends Command {
    public final String path;
    public final int    majorVersion;
    public final int    minorVersion;
    
    public Level(String path, int majorVersion, int minorVersion) {
      this.path         = path;
      this.majorVersion = majorVersion;
      this.minorVersion = minorVersion;
    }
    
    @Override
    public Type getType() {
      return Type.LEVEL;
    }
  }
  
  public static final class MoverPath extends Command {
    public final int moverIdx;
    public final int pathIdx;
    
    public MoverPath(int moverIdx, int pathIdx) {
      this.moverIdx = moverIdx;
      this.pathIdx  = pathIdx;
    }
    
    @Override
    public Type getType() {
      return Type.MOVER_PATH;
    }
  }
  
  public static final class MoverTime extends Command {
    public final int   moverIdx;
    public final float t;
    
    public MoverTime(int moverIdx, float t) {
      this.moverIdx = moverIdx;
      this.t        = t;
    }
    
    @Override
    public Type getType() {
      return Type.MOVER_TIME;
    }
  }
  
  public static final class PathEnable extends Command {
    public final int     pathIdx;
    public final boolean enabled;
    
    public PathEnable(int pathIdx, boolean enabled) {
      this.pathIdx = pathIdx;
      this.enabled = enabled;
    }
    
    @Override
    public Type getType() {
      return Type.PATH_ENABLE;
    }
  }
  
  public static final class Rate extends Command {
    public final int ups;
    
    public Rate(int ups) {
      this.ups = ups;
    }
    
    @Override
    public Type getType() {
      return Type.RATE;
    }
  }
  
  public static final class SimulationStep extends Command {
    public final float dt;
    
    public SimulationStep(float dt) {
      this.dt = dt;
    }
    
    @Override
    public Type getType() {
      return Type.SIMULATION_STEP;
    }
  }
  
  public static final class Sound extends Command {
    public final String path;
    public final float  amp;
    
    public Sound(String path, float amp) {
      this.path = path;
      this.amp  = amp;
    }
    
    @Override
    public Type getType() {
      return Type.SOUND;
    }
  }
  
  public static final class Status extends Command {
    public final com.uppgarn.nuncabola.core.game.Status status;
    
    public Status(com.uppgarn.nuncabola.core.game.Status status) {
      this.status = status;
    }
    
    @Override
    public Type getType() {
      return Type.STATUS;
    }
  }
  
  public static final class SwitchEnter extends Command {
    public final int switchIdx;
    
    public SwitchEnter(int switchIdx) {
      this.switchIdx = switchIdx;
    }
    
    @Override
    public Type getType() {
      return Type.SWITCH_ENTER;
    }
  }
  
  public static final class SwitchExit extends Command {
    public final int switchIdx;
    
    public SwitchExit(int switchIdx) {
      this.switchIdx = switchIdx;
    }
    
    @Override
    public Type getType() {
      return Type.SWITCH_EXIT;
    }
  }
  
  public static final class SwitchToggle extends Command {
    public final int switchIdx;
    
    public SwitchToggle(int switchIdx) {
      this.switchIdx = switchIdx;
    }
    
    @Override
    public Type getType() {
      return Type.SWITCH_TOGGLE;
    }
  }
  
  public static final class TeleporterEnter extends Command {
    public static final TeleporterEnter INSTANCE = new TeleporterEnter();
    
    private TeleporterEnter() {
    }
    
    @Override
    public Type getType() {
      return Type.TELEPORTER_ENTER;
    }
  }
  
  public static final class TeleporterExit extends Command {
    public static final TeleporterExit INSTANCE = new TeleporterExit();
    
    private TeleporterExit() {
    }
    
    @Override
    public Type getType() {
      return Type.TELEPORTER_EXIT;
    }
  }
  
  public static final class TiltAxes extends Command {
    public final float xX;
    public final float xY;
    public final float xZ;
    public final float zX;
    public final float zY;
    public final float zZ;
    
    public TiltAxes(
        float xX,
        float xY,
        float xZ,
        float zX,
        float zY,
        float zZ) {
      this.xX = xX;
      this.xY = xY;
      this.xZ = xZ;
      this.zX = zX;
      this.zY = zY;
      this.zZ = zZ;
    }
    
    public TiltAxes(Vector3 x, Vector3 z) {
      this(x.x, x.y, x.z, z.x, z.y, z.z);
    }
    
    @Override
    public Type getType() {
      return Type.TILT_AXES;
    }
  }
  
  public static final class TiltAngles extends Command {
    public final float x;
    public final float z;
    
    public TiltAngles(float x, float z) {
      this.x = x;
      this.z = z;
    }
    
    @Override
    public Type getType() {
      return Type.TILT_ANGLES;
    }
  }
  
  public static final class Timer extends Command {
    public final float timer;
    
    public Timer(float timer) {
      this.timer = timer;
    }
    
    @Override
    public Type getType() {
      return Type.TIMER;
    }
  }
  
  public static final class ViewPosition extends Command {
    public final float x;
    public final float y;
    public final float z;
    
    public ViewPosition(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    
    public ViewPosition(Vector3 p) {
      this(p.x, p.y, p.z);
    }
    
    @Override
    public Type getType() {
      return Type.VIEW_POSITION;
    }
  }
  
  public static final class ViewCenter extends Command {
    public final float x;
    public final float y;
    public final float z;
    
    public ViewCenter(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    
    public ViewCenter(Vector3 c) {
      this(c.x, c.y, c.z);
    }
    
    @Override
    public Type getType() {
      return Type.VIEW_CENTER;
    }
  }
  
  public static final class ViewBasis extends Command {
    public final float xX;
    public final float xY;
    public final float xZ;
    public final float yX;
    public final float yY;
    public final float yZ;
    
    public ViewBasis(
        float xX,
        float xY,
        float xZ,
        float yX,
        float yY,
        float yZ) {
      this.xX = xX;
      this.xY = xY;
      this.xZ = xZ;
      this.yX = yX;
      this.yY = yY;
      this.yZ = yZ;
    }
    
    public ViewBasis(Basis3 e) {
      this(e.x.x, e.x.y, e.x.z, e.y.x, e.y.y, e.y.z);
    }
    
    @Override
    public Type getType() {
      return Type.VIEW_BASIS;
    }
  }
}
