/*
 * Simulation.java
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

package com.uppgarn.nuncabola.core.physics;

import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.solid.*;
import com.uppgarn.nuncabola.core.util.*;

public final class Simulation {
  private final Solid              sol;
  private final SimulationListener listener;
  
  private PathEnabler       pathEnabler;
  private CollisionDetector collDetect;
  
  public Simulation(Solid sol, SimulationListener listener) {
    this.sol      = sol;
    this.listener = listener;
    
    pathEnabler = new PathEnabler      (sol, createPathEnablerListener());
    collDetect  = new CollisionDetector(sol);
  }
  
  private PathEnablerListener createPathEnablerListener() {
    return new PathEnablerListener() {
      @Override
      public void pathEnabled(int pathIdx, boolean enabled) {
        if (listener != null) {
          listener.pathEnabled(pathIdx, enabled);
        }
      }
    };
  }
  
  private int getAccumulatorTime(float dt) {
    int ms = 0;
    
    float accum = sol.accum + dt;
    
    while (accum >= 0.001f) {
      accum -= 0.001f;
      
      ms++;
    }
    
    return ms;
  }
  
  private float getMoversTime(float dt) {
    float t = dt;
    
    for (Mover mover: sol.movers) {
      Path path = sol.paths[mover.pathIdx];
      
      if (path.enabled && (mover.tm + getAccumulatorTime(t) > path.base.tm)) {
        t = Util.millisecondsToSeconds(path.base.tm - mover.tm);
      }
    }
    
    return t;
  }
  
  private float getSwitchesTime(float dt) {
    float t = dt;
    
    for (Switch zwitch: sol.switches) {
      if ((zwitch.tm < zwitch.base.tm)
          && (zwitch.tm + getAccumulatorTime(t) > zwitch.base.tm)) {
        t = Util.millisecondsToSeconds(zwitch.base.tm - zwitch.tm);
      }
    }
    
    return t;
  }
  
  private float getPathTime(float dt) {
    float t = dt;
    
    t = getMoversTime  (t);
    t = getSwitchesTime(t);
    
    return t;
  }
  
  /**
   * Integrates the rotation of the given basis {@code e} under
   * angular velocity {@code w} through time {@code dt}.
   */
  private void rotate(Basis3 e, Vector3 w, float dt) {
    if (!(w.length() > 0.0f)) {
      return;
    }
    
    Vector3 a  = rotate_a;
    Matrix4 m_ = rotate_m_;
    Basis3  f  = rotate_f;
    
    // Compute the rotation matrix.
    
    a .normalize(w);
    m_.rotate   (a, w.length() * dt);
    
    // Apply it to the basis.
    
    f.x.transform(e.x, m_);
    f.y.transform(e.y, m_);
    f.z.transform(e.z, m_);
    
    // Re-orthonormalize the basis.
    
    e.z.cross(f.x, f.y);
    e.y.cross(f.z, f.x);
    e.x.cross(f.y, f.z);
    
    e.x.normalize(e.x);
    e.y.normalize(e.y);
    e.z.normalize(e.z);
  }
  
  /**
   * Computes the new linear and angular velocities of a bouncing ball.
   * <p>
   * {@code q} gives the position of the point of impact and {@code w}
   * gives the velocity of the object being impacted.
   */
  private float bounce(Ball ball, Vector3 q, Vector3 w) {
    Vector3 n = bounce_n;
    Vector3 r = bounce_r;
    Vector3 d = bounce_d;
    
    Vector3 p = ball.p;
    Vector3 v = ball.v;
    
    // Find the normal of the impact.
    
    r.subtract (p, q);
    d.subtract (v, w);
    n.normalize(r);
    
    // Find the new angular velocity.
    
    ball.w.cross(d, r);
    ball.w.scale(ball.w, -1.0f / (ball.r * ball.r));
    
    // Find the new linear velocity.
    
    float vn = v.dot(n);
    float wn = w.dot(n);
    
    v.addScaled(v, n, 1.7f * (wn - vn));
    
    p.addScaled(q, n, ball.r);
    
    // Return the "energy" of the impact, to determine the sound amplitude.
    
    return Math.abs(n.dot(d));
  }
  
  /**
   * Computes the new angular velocity and orientation of a ball pendulum.
   * <p>
   * {@code a} gives the acceleration of the ball. {@code g} gives the
   * gravity vector.
   */
  private void pendulum(Ball ball, Vector3 a, Vector3 g, float dt) {
    Vector3 v  = pendulum_v;
    Vector3 a_ = pendulum_a_;
    Vector3 f_ = pendulum_f_;
    Vector3 r  = pendulum_r;
    Vector3 y_ = pendulum_y_;
    Vector3 t_ = pendulum_t_;
    
    t_.setZero();
    
    final float m  = 5.000f;
    final float ka = 0.500f;
    final float kd = 0.995f;
    
    // Find the total force over DT.
    
    a_.scale    (a,      ka);
    a_.addScaled(a_, g, -dt);
    
    // Find the force.
    
    f_.scale(a_, m / dt);
    
    // Find the position of the pendulum.
    
    r.scale(ball.e_.y, -ball.r);
    
    // Find the torque on the pendulum.
    
    if (Math.abs(r.dot(f_)) > 0.0f) {
      t_.cross(f_, r);
    }
    
    // Apply the torque and dampen the angular velocity.
    
    ball.w_.addScaled(ball.w_, t_, dt);
    ball.w_.scale    (ball.w_,     kd);
    
    // Apply the angular velocity to the pendulum basis.
    
    rotate(ball.e_, ball.w_, dt);
    
    // Apply a torque turning the pendulum toward the ball velocity.
    
    v .addScaled(ball.v, ball.e_.y, ball.v.dot(ball.e_.y));
    y_.cross    (v, ball.e_.z);
    y_.scale    (ball.e_.y, 2 * y_.dot(ball.e_.y));
    
    rotate(ball.e_, y_, dt);
  }
  
  private int stepAccumulator(float dt) {
    int ms = 0;
    
    sol.accum += dt;
    
    while (sol.accum >= 0.001f) {
      sol.accum -= 0.001f;
      
      ms++;
    }
    
    return ms;
  }
  
  private void stepMover(int moverIdx, float dt, int ms) {
    Mover mover = sol.movers[moverIdx];
    
    Path path = sol.paths[mover.pathIdx];
    
    if (path.enabled) {
      mover.t  += dt;
      mover.tm += ms;
      
      if (mover.tm >= path.base.tm) {
        mover.pathIdx = path.base.pathIdx;
        
        mover.t  = 0.0f;
        mover.tm = 0;
        
        if (listener != null) {
          listener.moverModified(moverIdx, mover.pathIdx, mover.t);
        }
      }
    }
  }
  
  /**
   * Computes the states of all movers after {@code dt} seconds
   * ({@code ms} milliseconds) have passed.
   */
  private void stepMovers(float dt, int ms) {
    for (int moverIdx = 0; moverIdx < sol.movers.length; moverIdx++) {
      stepMover(moverIdx, dt, ms);
    }
  }
  
  private void stepSwitch(int switchIdx, float dt, int ms) {
    Switch zwitch = sol.switches[switchIdx];
    
    if (zwitch.tm < zwitch.base.tm) {
      zwitch.t  += dt;
      zwitch.tm += ms;
      
      if (zwitch.tm >= zwitch.base.tm) {
        // Update the path.
        
        if ((zwitch.base.pathIdx >= 0)
            && (zwitch.base.pathIdx < sol.paths.length)) {
          pathEnabler.setPathEnabled(zwitch.base.pathIdx, zwitch.base.enabled);
        }
        
        // Toggle the state.
        
        zwitch.enabled = zwitch.base.enabled;
        
        if (listener != null) {
          listener.switchToggled(switchIdx);
        }
      }
    }
  }
  
  /**
   * Computes the states of all switches after {@code dt} seconds
   * ({@code ms} milliseconds) have passed.
   */
  private void stepSwitches(float dt, int ms) {
    for (int switchIdx = 0; switchIdx < sol.switches.length; switchIdx++) {
      stepSwitch(switchIdx, dt, ms);
    }
  }
  
  private void stepBall(Ball ball, float dt) {
    ball.p.addScaled(ball.p, ball.v, dt);
    
    rotate(ball.e, ball.w, dt);
  }
  
  /**
   * Computes the states of all balls after {@code dt} seconds
   * have passed.
   */
  private void stepBalls(float dt) {
    for (Ball ball: sol.balls) {
      stepBall(ball, dt);
    }
  }
  
  private void stepIteration(float dt) {
    if (listener != null) {
      listener.simulationStepped(dt);
    }
    
    int ms = stepAccumulator(dt);
    
    stepMovers  (dt, ms);
    stepSwitches(dt, ms);
    stepBalls   (dt);
  }
  
  public void step(float dt) {
    float tt = dt;
    
    while (tt > 0.0f) {
      float pt = getPathTime(tt);
      
      stepIteration(pt);
      
      tt -= pt;
    }
  }
  
  /**
   * Steps the physics forward {@code dt} seconds under the influence
   * of gravity vector {@code g}.
   * <p>
   * If the ball gets pinched between two moving solids, this loop
   * might not terminate. It is better to do something physically
   * impossible than to lock up the game. So, if we make more than
   * {@code c} iterations, punt it.
   */
  public float step(int ballIdx, Vector3 g, float dt) {
    Ball ball = sol.balls[ballIdx];
    
    Vector3 p_ = step_p_;
    Vector3 v_ = step_v_;
    Vector3 a  = step_a;
    
    float b  = 0.0f;
    float tt = dt;
    
    a.copyFrom(ball.v);
    
    ball.v.addScaled(ball.v, g, tt);
    
    // Test for collision.
    
    for (int c = 16; (c > 0) && (tt > 0.0f); c--) {
      // Avoid stepping across path changes.
      
      float pt = getPathTime(tt);
      
      // Miss collisions if we reach the iteration limit.
      
      float nt;
      
      if (c > 1) {
        nt = collDetect.testBodies(p_, v_, ball, pt);
      } else {
        nt = tt;
      }
      
      stepIteration(nt);
      
      if (nt < pt) {
        b = Math.max(b, bounce(ball, p_, v_));
      }
      
      tt -= nt;
    }
    
    a.subtract(ball.v, a);
    
    pendulum(ball, a, g, dt);
    
    return b;
  }
  
  // Storage for reusable objects to minimize object creation.
  
  private final Vector3 rotate_a    = new Vector3();
  private final Matrix4 rotate_m_   = new Matrix4();
  private final Basis3  rotate_f    = new Basis3();
  private final Vector3 bounce_n    = new Vector3();
  private final Vector3 bounce_r    = new Vector3();
  private final Vector3 bounce_d    = new Vector3();
  private final Vector3 pendulum_v  = new Vector3();
  private final Vector3 pendulum_a_ = new Vector3();
  private final Vector3 pendulum_f_ = new Vector3();
  private final Vector3 pendulum_r  = new Vector3();
  private final Vector3 pendulum_y_ = new Vector3();
  private final Vector3 pendulum_t_ = new Vector3();
  private final Vector3 step_p_     = new Vector3();
  private final Vector3 step_v_     = new Vector3();
  private final Vector3 step_a      = new Vector3();
}
