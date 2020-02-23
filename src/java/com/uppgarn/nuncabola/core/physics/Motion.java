/*
 * Motion.java
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

public final class Motion {
  private final Solid sol;
  
  public Motion(Solid sol) {
    this.sol = sol;
  }
  
  private float erp(float t) {
    return 3.0f * t * t - 2.0f * t * t * t;
  }
  
  public void getBodyPosition(Vector3 p, Body body, float dt) {
    if (body.mover0Idx >= 0) {
      Mover mover = sol.movers[body.mover0Idx];
      
      PathBase pathBase0 = sol.base.pathBases[mover    .pathIdx];
      PathBase pathBase1 = sol.base.pathBases[pathBase0.pathIdx];
      
      float t = sol.paths[mover.pathIdx].enabled ? mover.t + dt : mover.t;
      float s = t / pathBase0.t;
      
      p.copyFrom(pathBase0.p, pathBase1.p, pathBase0.smooth ? erp(s) : s);
      
      return;
    }
    
    p.setZero();
  }
  
  public void getBodyOrientation(Quaternion e, Body body, float dt) {
    if (body.mover1Idx >= 0) {
      Mover mover = sol.movers[body.mover1Idx];
      
      PathBase pathBase0 = sol.base.pathBases[mover    .pathIdx];
      PathBase pathBase1 = sol.base.pathBases[pathBase0.pathIdx];
      
      if    (((pathBase0.flags & PathBase.ORIENTED) != 0)
          || ((pathBase1.flags & PathBase.ORIENTED) != 0)) {
        float t = sol.paths[mover.pathIdx].enabled ? mover.t + dt : mover.t;
        float s = t / pathBase0.t;
        
        e.copyFrom(pathBase0.e, pathBase1.e, pathBase0.smooth ? erp(s) : s);
        
        return;
      }
    }
    
    e.setIdentity();
  }
  
  public void getBodyVelocity(Vector3 v, Body body, float dt) {
    if (body.mover0Idx >= 0) {
      Mover mover = sol.movers[body.mover0Idx];
      
      if (sol.paths[mover.pathIdx].enabled) {
        Vector3 p = getBodyVelocity_p;
        Vector3 q = getBodyVelocity_q;
        
        getBodyPosition(p, body, 0.0f);
        getBodyPosition(q, body, dt);
        
        v.subtract(q, p);
        v.divide  (v, dt);
        
        return;
      }
    }
    
    v.setZero();
  }
  
  /**
   * Determines if the body might be rotating.
   */
  public boolean isBodyPathOriented(Body body) {
    if (body.mover1Idx >= 0) {
      Mover mover = sol.movers[body.mover1Idx];
      
      if (sol.paths[mover.pathIdx].enabled) {
        PathBase pathBase0 = sol.base.pathBases[mover    .pathIdx];
        PathBase pathBase1 = sol.base.pathBases[pathBase0.pathIdx];
        
        if    (((pathBase0.flags & PathBase.ORIENTED) != 0)
            || ((pathBase1.flags & PathBase.ORIENTED) != 0)) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  // Storage for reusable objects to minimize object creation.
  
  private final Vector3 getBodyVelocity_p = new Vector3();
  private final Vector3 getBodyVelocity_q = new Vector3();
}
