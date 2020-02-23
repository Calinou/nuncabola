/*
 * Ball.java
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

package com.uppgarn.nuncabola.core.solid;

import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.util.*;

public final class Ball {
  private static final BallBase DEFAULT_BASE = new BallBase();
  
  /**
   * Position.
   */
  public Vector3 p;
  
  /**
   * Radius.
   */
  public float r;
  
  /**
   * Basis of orientation.
   */
  public Basis3 e;
  
  /**
   * Basis of pendulum.
   */
  public Basis3 e_;
  
  /**
   * Velocity.
   */
  public Vector3 v;
  
  /**
   * Angular velocity.
   */
  public Vector3 w;
  
  /**
   * Angular pendulum velocity.
   */
  public Vector3 w_;
  
  public Ball() {
    this(DEFAULT_BASE);
  }
  
  public Ball(BallBase base) {
    p = new Vector3(base.p);
    r = base.r;
    
    e  = new Basis3();
    e_ = new Basis3();
    
    v  = new Vector3();
    w  = new Vector3();
    w_ = new Vector3();
  }
  
  public Ball(Ball src) {
    p = new Vector3(src.p);
    r = src.r;
    
    e  = new Basis3(src.e);
    e_ = new Basis3(src.e_);
    
    v  = new Vector3(src.v);
    w  = new Vector3(src.w);
    w_ = new Vector3(src.w_);
  }
  
  public void copyFrom(Ball src) {
    p.copyFrom(src.p);
    
    r = src.r;
    
    e .copyFrom(src.e);
    e_.copyFrom(src.e_);
    
    v .copyFrom(src.v);
    w .copyFrom(src.w);
    w_.copyFrom(src.w_);
  }
  
  public void copyFrom(Ball src0, Ball src1, float alpha) {
    p.copyFrom(src0.p, src1.p, alpha);
    
    r = Util.lerp(src0.r, src1.r, alpha);
    
    e .copyFrom(src0.e,  src1.e,  alpha);
    e_.copyFrom(src0.e_, src1.e_, alpha);
    
    v .copyFrom(src0.v,  src1.v,  alpha);
    w .copyFrom(src0.w,  src1.w,  alpha);
    w_.copyFrom(src0.w_, src1.w_, alpha);
  }
}
