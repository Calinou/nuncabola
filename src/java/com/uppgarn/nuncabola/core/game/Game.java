/*
 * Game.java
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
import com.uppgarn.nuncabola.core.solid.*;
import com.uppgarn.nuncabola.core.util.*;

public final class Game {
  public final GameBase base;
  
  public final Solid sol;
  
  public boolean goalsUnlocked;
  public float   goalFactor;
  
  public boolean teleEnabled;
  public boolean teleInProgress;
  public float   teleT;
  
  public Tilt tilt;
  
  public View view;
  
  public Particles parts;
  
  public Status status;
  public int    timer;
  public int    coins;
  
  public boolean levelCompatible;
  
  public Game(GameBase base) {
    this.base = base;
    
    sol = new Solid(base.solBase);
    
    goalsUnlocked = false;
    goalFactor    = 0.0f;
    
    teleEnabled    = true;
    teleInProgress = false;
    teleT          = 0.0f;
    
    tilt = new Tilt();
    
    view = new View();
    
    parts = new Particles();
    
    status = Status.NONE;
    timer  = 0;
    coins  = 0;
    
    levelCompatible = true;
  }
  
  public Game(Game src) {
    base = src.base;
    
    sol = new Solid(src.sol);
    
    goalsUnlocked = src.goalsUnlocked;
    goalFactor    = src.goalFactor;
    
    teleEnabled    = src.teleEnabled;
    teleInProgress = src.teleInProgress;
    teleT          = src.teleT;
    
    tilt = new Tilt(src.tilt);
    
    view = new View(src.view);
    
    parts = new Particles(src.parts);
    
    status = src.status;
    timer  = src.timer;
    coins  = src.coins;
    
    levelCompatible = src.levelCompatible;
  }
  
  private boolean isTeleportationJump(Game other) {
    if (teleInProgress && other.teleInProgress && (other.teleT < 0.75f)) {
      Solid sol0 = sol;
      Solid sol1 = other.sol;
      
      if ((sol0.balls.length > 0) && (sol1.balls.length > 0)) {
        Ball ball0 = sol0.balls[0];
        Ball ball1 = sol1.balls[0];
        
        if (ball0.r == ball1.r) {
          Vector3 p0 = ball0.p;
          Vector3 p1 = ball1.p;
          
          return (p0.x != p1.x) || (p0.y != p1.y) || (p0.z != p1.z);
        }
      }
    }
    
    return false;
  }
  
  public void copyFrom(Game src) {
    assert src.base == base;
    
    sol.copyFrom(src.sol);
    
    goalsUnlocked = src.goalsUnlocked;
    goalFactor    = src.goalFactor;
    
    teleEnabled    = src.teleEnabled;
    teleInProgress = src.teleInProgress;
    teleT          = src.teleT;
    
    tilt.copyFrom(src.tilt);
    
    view.copyFrom(src.view);
    
    parts.copyFrom(src.parts);
    
    status = src.status;
    timer  = src.timer;
    coins  = src.coins;
    
    levelCompatible = src.levelCompatible;
  }
  
  public void copyFrom(Game src0, Game src1, float alpha) {
    assert src0.base == base;
    assert src1.base == base;
    
    boolean teleJump = src0.isTeleportationJump(src1);
    
    if (teleJump) {
      sol.copyFrom(src1.sol);
    } else {
      sol.copyFrom(src0.sol, src1.sol, alpha);
    }
    
    goalsUnlocked = src1.goalsUnlocked;
    goalFactor    = Util.lerp(src0.goalFactor, src1.goalFactor, alpha);
    
    teleEnabled = src1.teleEnabled;
    
    if (!src0.teleInProgress && src1.teleInProgress) {
      teleT          = src1.teleT * alpha;
      teleInProgress = true;
    } else {
      teleT          = Util.lerp(src0.teleT, src1.teleT, alpha);
      teleInProgress = src0.teleInProgress && (teleT < 1.0f);
    }
    
    tilt.copyFrom(src0.tilt, src1.tilt, alpha);
    
    if (teleJump) {
      view.copyFrom(src1.view);
    } else {
      view.copyFrom(src0.view, src1.view, alpha);
    }
    
    parts.copyFrom(src0.parts, src1.parts, alpha);
    
    status = src1.status;
    timer  = src1.timer;
    coins  = src1.coins;
    
    levelCompatible = src1.levelCompatible;
  }
}
