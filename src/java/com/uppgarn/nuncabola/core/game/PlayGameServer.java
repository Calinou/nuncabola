/*
 * PlayGameServer.java
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

import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.physics.*;
import com.uppgarn.nuncabola.core.solid.*;
import com.uppgarn.nuncabola.core.util.*;

public final class PlayGameServer extends GameServer {
  private static final String SOUND_BUMP_SMALL  = "snd/bumplil.ogg";
  private static final String SOUND_BUMP_NORMAL = "snd/bump.ogg";
  private static final String SOUND_BUMP_BIG    = "snd/bumpbig.ogg";
  private static final String SOUND_ITEM        = "snd/coin.ogg";
  private static final String SOUND_GROW        = "snd/grow.ogg";
  private static final String SOUND_SHRINK      = "snd/shrink.ogg";
  private static final String SOUND_SWITCH      = "snd/switch.ogg";
  private static final String SOUND_TELEPORTER  = "snd/jump.ogg";
  private static final String SOUND_UNLOCK      = "snd/switch.ogg";
  private static final String SOUND_GOAL        = "snd/goal.ogg";
  private static final String SOUND_FALL_OUT    = "snd/fall.ogg";
  private static final String SOUND_TIME_OUT    = "snd/time.ogg";
  
  private final Level        level;
  private final SolidBase    solBase;
  private final boolean      unlockGoals;
  private final ViewDistance viewDist;
  private final Rate         rate;
  
  private final int levelTime;
  private final int levelGoal;
  
  private Solid sol;
  private Ball  ball;
  private float ballOrigR;
  
  private boolean goalsUnlocked;
  
  private boolean teleEnabled;
  private boolean teleInProgress;
  private boolean teleJumpDone;
  private float   teleT;
  private Vector3 teleP;
  
  private BallSize ballSize;
  private boolean  resizeInProgress;
  private float    resizeR0;
  private float    resizeR1;
  private float    resizeT;
  
  private Tilt tilt;
  
  private View  view;
  private float viewK;
  
  private Status status;
  private float  timer;
  private int    coins;
  
  private float extraTime;
  
  private Input input;
  
  private Simulation     sim;
  private PathEnabler    pathEnabler;
  private EntityDetector entDetect;
  
  public PlayGameServer(
      Level        level,
      SolidBase    solBase,
      boolean      unlockGoals,
      ViewDistance viewDist,
      Rate         rate) {
    this.level       = level;
    this.solBase     = solBase;
    this.unlockGoals = unlockGoals;
    this.viewDist    = viewDist;
    this.rate        = rate;
    
    levelTime = level.getTime();
    levelGoal = level.getGoal();
    
    sol       = new Solid(solBase);
    ball      = sol.balls[0];
    ballOrigR = ball.r;
    
    goalsUnlocked = (levelGoal == 0) || unlockGoals;
    
    teleEnabled    = true;
    teleInProgress = false;
    teleJumpDone   = false;
    teleT          = 0.0f;
    teleP          = new Vector3();
    
    ballSize         = BallSize.NORMAL;
    resizeInProgress = false;
    resizeR0         = 0.0f;
    resizeR1         = 0.0f;
    resizeT          = 0.0f;
    
    tilt = new Tilt();
    
    view  = new View();
    viewK = 1.0f;
    
    Flyer.fly(view, sol, viewDist, 0.0f);
    
    status = Status.NONE;
    timer  = levelTime / 100.0f;
    coins  = 0;
    
    extraTime = 0.0f;
    
    input = new Input();
    
    sim         = new Simulation (sol, createSimulationListener());
    pathEnabler = new PathEnabler(sol, createPathEnablerListener());
    entDetect   = new EntityDetector();
    
    start();
  }
  
  private SimulationListener createSimulationListener() {
    return new SimulationListener() {
      @Override
      public void simulationStepped(float dt) {
        sendSimulationStepCommand(dt);
      }
      
      @Override
      public void moverModified(int moverIdx, int pathIdx, float t) {
        sendMoverTimeCommand(moverIdx, t);
        sendMoverPathCommand(moverIdx, pathIdx);
      }
      
      @Override
      public void switchToggled(int switchIdx) {
        sendSwitchToggleCommand(switchIdx);
      }
      
      @Override
      public void pathEnabled(int pathIdx, boolean enabled) {
        sendPathEnableCommand(pathIdx, enabled);
      }
    };
  }
  
  private PathEnablerListener createPathEnablerListener() {
    return new PathEnablerListener() {
      @Override
      public void pathEnabled(int pathIdx, boolean enabled) {
        sendPathEnableCommand(pathIdx, enabled);
      }
    };
  }
  
  private void sendEndOfUpdateCommand() {
    sendCommand(Command.EndOfUpdate.INSTANCE);
  }
  
  private void sendBallsClearCommand() {
    sendCommand(Command.BallsClear.INSTANCE);
  }
  
  private void sendBallCreateCommand() {
    sendCommand(Command.BallCreate.INSTANCE);
  }
  
  private void sendBallPositionCommand() {
    sendCommand(new Command.BallPosition(ball.p));
  }
  
  private void sendBallRadiusCommand() {
    sendCommand(new Command.BallRadius(ball.r));
  }
  
  private void sendBallBasisCommand() {
    sendCommand(new Command.BallBasis(ball.e));
  }
  
  private void sendBallPendulumBasisCommand() {
    sendCommand(new Command.BallPendulumBasis(ball.e_));
  }
  
  private void sendCoinsCommand() {
    sendCommand(new Command.Coins(coins));
  }
  
  private void sendGoalsUnlockCommand() {
    sendCommand(Command.GoalsUnlock.INSTANCE);
  }
  
  private void sendItemsClearCommand() {
    sendCommand(Command.ItemsClear.INSTANCE);
  }
  
  private void sendItemCreateCommand(Item item) {
    sendCommand(new Command.ItemCreate(item.p, item.type, item.value));
  }
  
  private void sendItemCollectCommand(int itemIdx) {
    sendCommand(new Command.ItemCollect(itemIdx));
  }
  
  private void sendLevelCommand() {
    sendCommand(new Command.Level(
      level.getSolidPath(),
      level.getMajorVersion(),
      level.getMinorVersion()));
  }
  
  private void sendMoverPathCommand(int moverIdx, int pathIdx) {
    sendCommand(new Command.MoverPath(moverIdx, pathIdx));
  }
  
  private void sendMoverTimeCommand(int moverIdx, float t) {
    sendCommand(new Command.MoverTime(moverIdx, t));
  }
  
  private void sendPathEnableCommand(int pathIdx, boolean enabled) {
    sendCommand(new Command.PathEnable(pathIdx, enabled));
  }
  
  private void sendRateCommand() {
    sendCommand(new Command.Rate(rate.getUPS()));
  }
  
  private void sendSimulationStepCommand(float dt) {
    sendCommand(new Command.SimulationStep(dt));
  }
  
  private void sendSoundCommand(String path, float amp) {
    sendCommand(new Command.Sound(path, amp));
  }
  
  private void sendStatusCommand() {
    sendCommand(new Command.Status(status));
  }
  
  private void sendSwitchEnterCommand(int switchIdx) {
    sendCommand(new Command.SwitchEnter(switchIdx));
  }
  
  private void sendSwitchExitCommand(int switchIdx) {
    sendCommand(new Command.SwitchExit(switchIdx));
  }
  
  private void sendSwitchToggleCommand(int switchIdx) {
    sendCommand(new Command.SwitchToggle(switchIdx));
  }
  
  private void sendTeleporterEnterCommand() {
    sendCommand(Command.TeleporterEnter.INSTANCE);
  }
  
  private void sendTeleporterExitCommand() {
    sendCommand(Command.TeleporterExit.INSTANCE);
  }
  
  private void sendTiltAxesCommand() {
    sendCommand(new Command.TiltAxes(tilt.x, tilt.z));
  }
  
  private void sendTiltAnglesCommand() {
    sendCommand(new Command.TiltAngles(tilt.rx, tilt.rz));
  }
  
  private void sendTimerCommand() {
    sendCommand(new Command.Timer(timer));
  }
  
  private void sendViewPositionCommand() {
    sendCommand(new Command.ViewPosition(view.p));
  }
  
  private void sendViewCenterCommand() {
    sendCommand(new Command.ViewCenter(view.c));
  }
  
  private void sendViewBasisCommand() {
    sendCommand(new Command.ViewBasis(view.e));
  }
  
  private void start() {
    sendLevelCommand();
    sendRateCommand();
    
    sendTimerCommand();
    
    if (goalsUnlocked) {
      sendGoalsUnlockCommand();
    }
    
    sendBallsClearCommand();
    sendBallCreateCommand();
    sendBallPositionCommand();
    sendBallBasisCommand();
    sendBallPendulumBasisCommand();
    sendBallRadiusCommand();
    
    sendItemsClearCommand();
    
    for (Item item: sol.items) {
      sendItemCreateCommand(item);
    }
    
    sendViewPositionCommand();
    sendViewCenterCommand();
    sendViewBasisCommand();
    
    sendEndOfUpdateCommand();
  }
  
  private boolean isUpdateAvailable() {
    switch (status) {
      case NONE: {
        return true;
      }
      case GOAL: {
        return extraTime < 1.0f;
      }
      case FALL_OUT: {
        return extraTime < 2.0f;
      }
      case TIME_OUT: {
        return false;
      }
      
      default: {
        throw new AssertionError();
      }
    }
  }
  
  private void stepTilt() {
    tilt.x.copyFrom(view.e.x);
    tilt.z.copyFrom(view.e.z);
    
    // Smooth jittery or discontinuous input.
    
    float k = rate.getTime() / Math.max(input.getResponse(), rate.getTime());
    
    tilt.rx += (input.getX() - tilt.rx) * k;
    tilt.rz += (input.getZ() - tilt.rz) * k;
    
    sendTiltAxesCommand();
    sendTiltAnglesCommand();
  }
  
  private void stepResizing() {
    if (!resizeInProgress) {
      return;
    }
    
    final float resizeTime = 0.5f;
    
    resizeT += rate.getTime();
    
    if (resizeT >= resizeTime) {
      resizeInProgress = false;
      resizeT          = resizeTime;
    }
    
    float r = Util.lerp(resizeR0, resizeR1, resizeT / resizeTime);
    
    // No sinking through the floor! Keep ball's bottom constant.
    
    ball.p.y += r - ball.r;
    ball.r    = r;
    
    sendBallRadiusCommand();
  }
  
  private String getBumpSound() {
    final float defaultR = 0.25f;
    
    float r         = resizeInProgress ? resizeR1 : ball.r;
    float halfSmall = BallSize.SMALL.getHalfRadius(defaultR);
    float halfBig   = BallSize.BIG  .getHalfRadius(defaultR);
    
    if (r < halfSmall) {
      return SOUND_BUMP_SMALL;
    } else if (r > halfBig) {
      return SOUND_BUMP_BIG;
    } else {
      return SOUND_BUMP_NORMAL;
    }
  }
  
  private void stepSimulation() {
    if (teleInProgress) {
      return;
    }
    
    Vector3 g = new Vector3();
    
    Gravity.get(g, status == Status.GOAL, tilt);
    
    float b = sim.step(0, g, rate.getTime());
    
    if (b > 0.5f) {
      // Allow a sound amplitude > 1.0f in order to
      // convey the actual intensity of the bump.
      
      sendSoundCommand(getBumpSound(), (b - 0.5f) * 2.0f);
    }
    
    sendBallPositionCommand();
    sendBallBasisCommand();
    sendBallPendulumBasisCommand();
  }
  
  private void stepTeleportation() {
    if (!teleInProgress) {
      return;
    }
    
    teleT += rate.getTime();
    
    if (teleT >= 0.5f) {
      // Translate view at the exact instant of the jump.
      
      if (!teleJumpDone) {
        teleJumpDone = true;
        
        Vector3 dp = new Vector3();
        
        dp    .subtract(teleP, ball.p);
        view.p.add     (view.p, dp);
      }
      
      // Translate ball and hold it at the destination.
      
      ball.p.copyFrom(teleP);
      
      if (teleT >= 1.0f) {
        teleInProgress = false;
      }
    }
    
    sendBallPositionCommand();
    sendBallBasisCommand();
    sendBallPendulumBasisCommand();
  }
  
  private void stepViewBasis(Vector3 v) {
    // Apply camera.
    
    if (input.getCamera() != Camera.MANUAL) {
      // Viewpoint chases ball position.
      
      view.e.z.subtract (view.p, ball.p);
      view.e.z.normalize(view.e.z);
      
      if (input.getCamera() == Camera.CHASE) {
        view.e.z.addScaled(
          view.e.z,
          v,
          v.length() * input.getCameraSpeed() * rate.getTime());
      }
    } else {
      // View vector is given by view angle.
      
      float a = (float) Math.atan2(view.e.z.x, view.e.z.z);
      
      view.e.z.x = (float) Math.sin(a);
      view.e.z.y = 0.0f;
      view.e.z.z = (float) Math.cos(a);
    }
    
    // Apply manual rotation.
    
    float da = input.getRotation() * rate.getTime() * 90.0f;
    
    if (da != 0.0f) {
      Matrix4 m_ = new Matrix4();
      Vector3 y_ = new Vector3(0.0f, 1.0f, 0.0f);
      
      m_      .rotate   (y_, (float) Math.toRadians(da));
      view.e.z.transform(view.e.z, m_);
    }
    
    // Orthonormalize the new view reference frame.
    
    view.e.x.cross    (view.e.y, view.e.z);
    view.e.z.cross    (view.e.x, view.e.y);
    view.e.x.normalize(view.e.x);
    view.e.z.normalize(view.e.z);
  }
  
  private void stepViewDistanceFactor(Vector3 v) {
    float k = 1.0f + view.e.z.dot(v) / 10.0f;
    
    viewK = Math.max(viewK + (k - viewK) * rate.getTime(), 0.5f);
  }
  
  private void updateViewPosition() {
    Vector3 v = new Vector3();
    
    v.scale    (   view.e.y, viewDist.py * viewK);
    v.addScaled(v, view.e.z, viewDist.pz * viewK);
    
    view.p.add(ball.p, v);
  }
  
  private void updateViewCenter() {
    float k = teleInProgress ? 2.0f * Math.abs(teleT - 0.5f) : 1.0f;
    
    view.c.addScaled(ball.p, view.e.y, viewDist.cy * k);
  }
  
  private void stepView() {
    Vector3 v = new Vector3();
    
    v.negate(ball.v);
    v.y = 0.0f;
    
    stepViewBasis         (v);
    stepViewDistanceFactor(v);
    
    updateViewPosition();
    updateViewCenter();
    
    sendViewPositionCommand();
    sendViewCenterCommand();
    sendViewBasisCommand();
  }
  
  private void stepTimer() {
    if (status.isOver()) {
      return;
    }
    
    if (levelTime > 0) {
      timer = Math.max(timer - rate.getTime(), 0.0f);
    } else {
      timer += rate.getTime();
    }
    
    sendTimerCommand();
  }
  
  private void handleCoinItem(int value) {
    if (value <= 0) {
      return;
    }
    
    coins += value;
    
    sendCoinsCommand();
    
    if ((coins >= levelGoal) && (coins - value < levelGoal)) {
      // The goal-unlocking procedure is carried out even
      // if the goals have been unlocked from the start.
      
      sendSoundCommand(SOUND_UNLOCK, 1.0f);
      
      goalsUnlocked = true;
      
      sendGoalsUnlockCommand();
    }
  }
  
  private void handleResizeItem(BallSize newBallSize, String sound) {
    if (ballSize == newBallSize) {
      return;
    }
    
    sendSoundCommand(sound, 1.0f);
    
    ballSize         = newBallSize;
    resizeInProgress = true;
    resizeR0         = ball.r;
    resizeR1         = ballSize.getRadius(ballOrigR);
    resizeT          = 0.0f;
  }
  
  private void handleGrowItem() {
    handleResizeItem(ballSize.getNextBigger (), SOUND_GROW);
  }
  
  private void handleShrinkItem() {
    handleResizeItem(ballSize.getNextSmaller(), SOUND_SHRINK);
  }
  
  private void handleItem(int itemIdx, Item item) {
    sendItemCollectCommand(itemIdx);
    
    switch (item.type) {
      case ItemBase.COIN: {
        handleCoinItem(item.value);
        
        break;
      }
      case ItemBase.GROW: {
        handleGrowItem();
        
        break;
      }
      case ItemBase.SHRINK: {
        handleShrinkItem();
        
        break;
      }
    }
    
    item.type = ItemBase.NONE;
    
    sendSoundCommand(SOUND_ITEM, 1.0f);
  }
  
  private void testItems() {
    if (status.isOver()) {
      return;
    }
    
    for (int itemIdx = 0; itemIdx < sol.items.length; itemIdx++) {
      Item item = sol.items[itemIdx];
      
      if ((item.type != ItemBase.NONE) && entDetect.testItem(item, ball)) {
        handleItem(itemIdx, item);
      }
    }
  }
  
  private boolean handleSwitchInside(int switchIdx, Switch zwitch) {
    boolean timed          = zwitch.base.tm != 0;
    boolean inDefaultState = zwitch.enabled == zwitch.base.enabled;
    boolean wasInside      = zwitch.ballInside;
    
    if (!zwitch.ballInside) {
      // The ball enters.
      
      zwitch.ballInside = true;
      
      sendSwitchEnterCommand(switchIdx);
    }
    
    if (timed ? inDefaultState : !wasInside) {
      // Start the timer.
      
      zwitch.t  = 0.0f;
      zwitch.tm = 0;
      
      // Toggle the state.
      
      zwitch.enabled = !zwitch.enabled;
      
      sendSwitchToggleCommand(switchIdx);
      
      // Update the path.
      
      if ((zwitch.base.pathIdx >= 0)
          && (zwitch.base.pathIdx < sol.paths.length)) {
        pathEnabler.setPathEnabled(zwitch.base.pathIdx, zwitch.enabled);
      }
      
      // Report an interaction with a visible switch.
      
      if (!zwitch.base.invisible) {
        return true;
      }
    }
    
    return false;
  }
  
  private void handleSwitchOutside(int switchIdx, Switch zwitch) {
    if (zwitch.ballInside) {
      // The ball exits.
      
      zwitch.ballInside = false;
      
      sendSwitchExitCommand(switchIdx);
    }
  }
  
  private void testSwitches() {
    if (status.isOver()) {
      return;
    }
    
    boolean visibleSwitchToggled = false;
    
    for (int switchIdx = 0; switchIdx < sol.switches.length; switchIdx++) {
      Switch zwitch = sol.switches[switchIdx];
      
      HaloTest result = entDetect.testSwitch(zwitch.base, ball);
      
      if (result == HaloTest.INSIDE) {
        if (handleSwitchInside(switchIdx, zwitch)) {
          visibleSwitchToggled = true;
        }
      } else if (result == HaloTest.OUTSIDE) {
        handleSwitchOutside(switchIdx, zwitch);
      }
    }
    
    if (visibleSwitchToggled) {
      sendSoundCommand(SOUND_SWITCH, 1.0f);
    }
  }
  
  private void handleTeleporterInside(Teleporter tele) {
    teleEnabled    = false;
    teleInProgress = true;
    teleJumpDone   = false;
    teleT          = 0.0f;
    
    teleP.subtract(ball.p, tele.p);
    teleP.add     (tele.q, teleP);
    
    sendSoundCommand(SOUND_TELEPORTER, 1.0f);
    sendTeleporterEnterCommand();
  }
  
  private void handleTeleportersOutside() {
    teleEnabled = true;
    
    sendTeleporterExitCommand();
  }
  
  private void testTeleporters() {
    if (status.isOver() || teleInProgress) {
      return;
    }
    
    if (teleEnabled) {
      for (Teleporter tele: sol.base.teles) {
        if (entDetect.testTeleporter(tele, ball) == HaloTest.INSIDE) {
          handleTeleporterInside(tele);
          
          break;
        }
      }
    } else {
      boolean outside = true;
      
      for (Teleporter tele: sol.base.teles) {
        if (entDetect.testTeleporter(tele, ball) != HaloTest.OUTSIDE) {
          outside = false;
          
          break;
        }
      }
      
      if (outside) {
        handleTeleportersOutside();
      }
    }
  }
  
  private void handleStatus(Status newStatus, String sound) {
    sendSoundCommand(sound, 1.0f);
    
    status = newStatus;
    
    sendStatusCommand();
  }
  
  private void testGoal() {
    if (status.isOver()) {
      return;
    }
    
    if (goalsUnlocked) {
      for (Goal goal: sol.base.goals) {
        if (entDetect.testGoal(goal, ball)) {
          handleStatus(Status.GOAL, SOUND_GOAL);
          
          break;
        }
      }
    }
  }
  
  private void testFallOut() {
    if (status.isOver()) {
      return;
    }
    
    if ((sol.base.verts.length == 0) || (ball.p.y < sol.base.verts[0].p.y)) {
      handleStatus(Status.FALL_OUT, SOUND_FALL_OUT);
    }
  }
  
  private void testTimeOut() {
    if (status.isOver()) {
      return;
    }
    
    if ((levelTime > 0) && (timer <= 0.0f)) {
      handleStatus(Status.TIME_OUT, SOUND_TIME_OUT);
    }
  }
  
  private void step() {
    clearCommands();
    
    if (!isUpdateAvailable()) {
      return;
    }
    
    if (status.isOver()) {
      extraTime += rate.getTime();
    }
    
    stepTilt();
    stepResizing();
    stepSimulation();
    stepTeleportation();
    stepView();
    stepTimer();
    
    testItems();
    testSwitches();
    testTeleporters();
    testGoal();
    testFallOut();
    testTimeOut();
    
    sendEndOfUpdateCommand();
  }
  
  public void step(Input input) {
    if (!status.isOver()) {
      this.input.copyFrom(input);
    }
    
    step();
  }
}
