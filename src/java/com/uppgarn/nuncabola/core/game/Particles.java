/*
 * Particles.java
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

public final class Particles {
  public static final int COUNT = 256;
  
  public Particle[] array;
  
  public Particles() {
    array = createArray();
  }
  
  public Particles(Particles src) {
    array = createArray(src);
  }
  
  private Particle[] createArray() {
    Particle[] array = new Particle[COUNT];
    
    for (int idx = 0; idx < array.length; idx++) {
      array[idx] = new Particle();
    }
    
    return array;
  }
  
  private Particle[] createArray(Particles src) {
    Particle[] array = new Particle[src.array.length];
    
    for (int idx = 0; idx < array.length; idx++) {
      array[idx] = new Particle(src.array[idx]);
    }
    
    return array;
  }
  
  public void burst(Item item) {
    ItemStyle style = ItemStyle.getStyle(item);
    
    for (int idx = 0, count = 0; (idx < array.length) && (count < 10); idx++) {
      Particle part = array[idx];
      
      if (part.t <= 0.0f) {
        part.burst(style, item.p);
        
        count++;
      }
    }
  }
  
  public void step(Vector3 g, float dt) {
    for (Particle part: array) {
      part.step(g, dt);
    }
  }
  
  public void copyFrom(Particles src) {
    for (int idx = 0; idx < array.length; idx++) {
      array[idx].copyFrom(src.array[idx]);
    }
  }
  
  public void copyFrom(Particles src0, Particles src1, float alpha) {
    for (int idx = 0; idx < array.length; idx++) {
      array[idx].copyFrom(src0.array[idx], src1.array[idx], alpha);
    }
  }
}
