/*
 * BallSize.java
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

enum BallSize {
  SMALL (0.5f),
  NORMAL(1.0f),
  BIG   (1.5f);
  
  private final float factor;
  
  BallSize(float factor) {
    this.factor = factor;
  }
  
  public float getRadius(float base) {
    return base * factor;
  }
  
  public float getHalfRadius(float base) {
    return base * (factor / 2 + 0.5f);
  }
  
  public BallSize getNextSmaller() {
    return (this == BIG)   ? NORMAL : SMALL;
  }
  
  public BallSize getNextBigger() {
    return (this == SMALL) ? NORMAL : BIG;
  }
}
