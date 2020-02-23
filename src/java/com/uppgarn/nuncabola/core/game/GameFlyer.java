/*
 * GameFlyer.java
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

public final class GameFlyer {
  private final Game         game;
  private final ViewDistance viewDist;
  
  public GameFlyer(Game game, ViewDistance viewDist) {
    this.game     = game;
    this.viewDist = viewDist;
  }
  
  public void fly(float k) {
    Flyer.fly(game.view, game.sol, viewDist, k);
  }
}
