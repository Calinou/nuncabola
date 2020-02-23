/*
 * Speed.java
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

package com.uppgarn.nuncabola.core.replay;

import java.util.*;

public enum Speed {
  NONE(0.0f),
  
  SLOWEST(1.0f / 8),
  SLOWER (1.0f / 4),
  SLOW   (1.0f / 2),
  NORMAL (1.0f),
  FAST   (1.0f * 2),
  FASTER (1.0f * 4),
  FASTEST(1.0f * 8);
  
  public static final List<Speed> MOTION_VALUES = Collections.unmodifiableList(
    Arrays.asList(SLOWEST, SLOWER, SLOW, NORMAL, FAST, FASTER, FASTEST));
  
  private static final Speed[] ARRAY = values();
  
  private final float factor;
  
  Speed(float factor) {
    this.factor = factor;
  }
  
  public float getFactor() {
    return factor;
  }
  
  public boolean isSlowerThan(Speed speed) {
    return ordinal() < speed.ordinal();
  }
  
  public boolean isFasterThan(Speed speed) {
    return ordinal() > speed.ordinal();
  }
  
  public Speed getNext(int delta) {
    return ARRAY[Math.min(Math.max(ordinal() + delta, 0), ARRAY.length - 1)];
  }
}
