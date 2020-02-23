/*
 * SolidKit.java
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

package com.uppgarn.nuncabola.core.renderers;

import com.uppgarn.nuncabola.core.physics.*;
import com.uppgarn.nuncabola.core.solid.*;

final class SolidKit {
  private final Solid sol;
  
  private SolidRenderer rend;
  private Simulation    sim;
  
  public SolidKit(Solid sol) {
    this.sol = sol;
    
    rend = new SolidRenderer(sol, false);
    sim  = null;
  }
  
  public Solid getSolid() {
    return sol;
  }
  
  public SolidRenderer getRenderer() {
    return rend;
  }
  
  public void step(float dt) {
    if (sim == null) {
      sim = new Simulation(sol, null);
    }
    
    sim.step(dt);
  }
  
  public void deinitialize() {
    rend.deinitialize();
  }
}
