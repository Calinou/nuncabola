/*
 * ViewDistance.java
 *
 * Copyright (c) 2003-2022 Nuncabola authors
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

public final class ViewDistance {
  public static final ViewDistance DEFAULT = new ViewDistance(
    0.75f,
    2.00f,
    0.25f);
  
  /**
   * Ideal view position distance above ball.
   */
  public final float py;
  
  /**
   * Ideal view position distance behind ball.
   */
  public final float pz;
  
  /**
   * Ideal view center distance above ball.
   */
  public final float cy;
  
  public ViewDistance(float py, float pz, float cy) {
    this.py = py;
    this.pz = pz;
    this.cy = cy;
  }
}
