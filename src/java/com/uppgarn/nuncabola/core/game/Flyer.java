/*
 * Flyer.java
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

final class Flyer {
  public static void fly(View view, Solid sol, ViewDistance viewDist, float k) {
    Vector3 p0 = new Vector3();
    Vector3 c0 = new Vector3();
    Vector3 p1 = new Vector3();
    Vector3 c1 = new Vector3();
    
    // k = 0.0 view is at the ball.
    
    if (sol.balls.length >= 1) {
      Ball ball = sol.balls[0];
      
      p0.copyFrom(ball.p);
      c0.copyFrom(ball.p);
    }
    
    p0.y += viewDist.py;
    p0.z += viewDist.pz;
    c0.y += viewDist.cy;
    
    // k = +1.0 view is vista 0.
    
    if ((k >= 0.0f) && (sol.base.vistas.length >= 1)) {
      Vista vista0 = sol.base.vistas[0];
      
      p1.copyFrom(vista0.p);
      c1.copyFrom(vista0.q);
    }
    
    // k = -1.0 view is vista 1.
    
    if ((k <= 0.0f) && (sol.base.vistas.length >= 2)) {
      Vista vista1 = sol.base.vistas[1];
      
      p1.copyFrom(vista1.p);
      c1.copyFrom(vista1.q);
    }
    
    // Interpolate the views.
    
    view.p.copyFrom(p0, p1, k * k);
    view.c.copyFrom(c0, c1, k * k);
    
    // Orthonormalize the view basis.
    
    view.e.setStandard();
    view.e.z.subtract(view.p, view.c);
    
    view.e.x.cross    (view.e.y, view.e.z);
    view.e.z.cross    (view.e.x, view.e.y);
    view.e.x.normalize(view.e.x);
    view.e.z.normalize(view.e.z);
  }
  
  private Flyer() {
  }
}
