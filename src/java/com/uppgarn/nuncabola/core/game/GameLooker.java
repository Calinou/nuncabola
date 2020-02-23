/*
 * GameLooker.java
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

public final class GameLooker {
  private final Game game;
  
  private Vector3 x;
  private Vector3 y;
  
  private float phi;
  private float theta;
  
  public GameLooker(Game game) {
    this.game = game;
    
    x = new Vector3();
    y = new Vector3();
    
    phi   = 0.0f;
    theta = -((float) Math.toDegrees(
              Math.atan2(game.view.e.z.x, game.view.e.z.z)));
    
    updateVectors();
    updateViewCenter();
  }
  
  private void updateVectors() {
    float phiSin   = (float) Math.sin(Math.toRadians(phi));
    float phiCos   = (float) Math.cos(Math.toRadians(phi));
    float thetaSin = (float) Math.sin(Math.toRadians(theta));
    float thetaCos = (float) Math.cos(Math.toRadians(theta));
    
    x.x = +thetaCos;
    x.y = 0.0f;
    x.z = +thetaSin;
    
    y.x = +(thetaSin * phiCos);
    y.y = +phiSin;
    y.z = -(thetaCos * phiCos);
  }
  
  private void updateViewCenter() {
    game.view.c.add(game.view.p, y);
  }
  
  public void turn(float xk, float yk) {
    phi   +=  90.0f * yk;
    theta += 180.0f * xk;
    
    phi = Math.min(Math.max(phi, -89.999f), +89.999f);
    
    if (theta < -180.0f) {
      theta += 360.0f;
    } else if (theta > +180.0f) {
      theta -= 360.0f;
    }
    
    updateVectors();
    updateViewCenter();
  }
  
  public void move(float xk, float yk) {
    game.view.p.addScaled(game.view.p, x, xk);
    game.view.p.addScaled(game.view.p, y, yk);
    
    updateViewCenter();
  }
}
