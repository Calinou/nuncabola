/*
 * EntityDetector.java
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

public final class EntityDetector {
  public boolean testItem(Item item, Ball ball) {
    Vector3 r = testItem_r;
    
    r.subtract(ball.p, item.p);
    
    return r.length() < ball.r + ItemBase.RADIUS;
  }
  
  public HaloTest testSwitch(SwitchBase switchBase, Ball ball) {
    Vector3 r = testSwitch_r;
    
    r.subtract(ball.p, switchBase.p);
    r.y = 0.0f;
    
    // Distance of the far side from the edge of the halo.
    
    float d = r.length() + ball.r - switchBase.r;
    
    // The "touch" distance, which must be cleared before being
    // able to trigger an untimed switch, is the ball's diameter.
    // (This is different from teleporters.)
    
    if ((d <= ((switchBase.tm == 0) ? ball.r * 2 : 0.0f))
        && (ball.p.y > switchBase.p.y)
        && (ball.p.y < switchBase.p.y + SwitchBase.HEIGHT / 2)) {
      return (d <= 0.0f) ? HaloTest.INSIDE : HaloTest.TOUCH;
    } else {
      return HaloTest.OUTSIDE;
    }
  }
  
  public HaloTest testTeleporter(Teleporter tele, Ball ball) {
    Vector3 r = testTeleporter_r;
    
    r.subtract(ball.p, tele.p);
    r.y = 0.0f;
    
    // Distance of the far side from the edge of the halo.
    
    float d = r.length() + ball.r - tele.r;
    
    // The "touch" distance, which must be cleared before being
    // able to trigger a teleporter, is the ball's radius.
    // (This is different from switches.)
    
    if ((d <= ball.r)
        && (ball.p.y > tele.p.y)
        && (ball.p.y < tele.p.y + Teleporter.HEIGHT / 2)) {
      return (d <= 0.0f) ? HaloTest.INSIDE : HaloTest.TOUCH;
    } else {
      return HaloTest.OUTSIDE;
    }
  }
  
  public boolean testGoal(Goal goal, Ball ball) {
    Vector3 r = testGoal_r;
    
    r.subtract(ball.p, goal.p);
    r.y = 0.0f;
    
    return (r.length() + ball.r < goal.r)
        && (ball.p.y > goal.p.y)
        && (ball.p.y < goal.p.y + Goal.HEIGHT / 2);
  }
  
  // Storage for reusable objects to minimize object creation.
  
  private final Vector3 testItem_r       = new Vector3();
  private final Vector3 testSwitch_r     = new Vector3();
  private final Vector3 testTeleporter_r = new Vector3();
  private final Vector3 testGoal_r       = new Vector3();
}
