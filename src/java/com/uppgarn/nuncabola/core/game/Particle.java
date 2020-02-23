/*
 * Particle.java
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
import com.uppgarn.nuncabola.core.util.*;

import java.util.*;

public final class Particle {
  private static final Random RANDOM = new Random(0);
  
  /**
   * Time until death (doubles as opacity).
   */
  public float t;
  
  /**
   * Style.
   */
  public ItemStyle style;
  
  /**
   * Position.
   */
  public Vector3 p;
  
  /**
   * Velocity.
   */
  public Vector3 v;
  
  /**
   * Angular velocity.
   */
  public float w;
  
  public Particle() {
    t = 0.0f;
    
    style = null;
    
    p = new Vector3();
    v = new Vector3();
    w = 0.0f;
  }
  
  public Particle(Particle src) {
    t = src.t;
    
    style = src.style;
    
    p = new Vector3(src.p);
    v = new Vector3(src.v);
    w = src.w;
  }
  
  private float rnd(float min, float max) {
    return min + (max - min) * RANDOM.nextFloat();
  }
  
  public void burst(ItemStyle style, Vector3 p) {
    t = 1.0f;
    
    this.style = style;
    
    this.p.copyFrom(p);
    
    float a0 = rnd(-1.0f * (float) Math.PI, +1.0f * (float) Math.PI);
    float a1 = rnd(+0.3f * (float) Math.PI, +0.5f * (float) Math.PI);
    float a2 = rnd(-4.0f * (float) Math.PI, +4.0f * (float) Math.PI);
    
    v.x = 4.0f * (float) Math.cos(a0) * (float) Math.cos(a1);
    v.y = 4.0f *                        (float) Math.sin(a1);
    v.z = 4.0f * (float) Math.sin(a0) * (float) Math.cos(a1);
    
    w = (float) Math.toDegrees(a2);
  }
  
  public void step(Vector3 g, float dt) {
    if (t <= 0.0f) {
      return;
    }
    
    t -= dt;
    
    v.addScaled(v, g, dt);
    p.addScaled(p, v, dt);
  }
  
  public void copyFrom(Particle src) {
    t = src.t;
    
    style = src.style;
    
    p.copyFrom(src.p);
    v.copyFrom(src.v);
    
    w = src.w;
  }
  
  public void copyFrom(Particle src0, Particle src1, float alpha) {
    if (src0.t <= 0.0f) {
      copyFrom(src1);
      
      return;
    }
    
    t = Util.lerp(src0.t, src1.t, alpha);
    
    style = src1.style;
    
    p.copyFrom(src0.p, src1.p, alpha);
    v.copyFrom(src0.v, src1.v, alpha);
    
    w = Util.lerp(src0.w, src1.w, alpha);
  }
}
