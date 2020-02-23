/*
 * Util.java
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

package com.uppgarn.nuncabola.core.util;

public final class Util {
  public static float lerp(float f0, float f1, float alpha) {
    return f0 + (f1 - f0) * alpha;
  }
  
  public static int ceilPowerOf2(int value) {
    int i = Integer.highestOneBit(value);
    
    return (i == value) ? i : i * 2;
  }
  
  public static int roundPowerOf2(int value) {
    int next = ceilPowerOf2(value);
    int prev = next / 2;
    
    return ((next > 1) && (next - value > value - prev)) ? prev : next;
  }
  
  public static float snapToInt(float f, float threshold) {
    int i = Math.round(f);
    
    return (Math.abs(i - f) < threshold) ? i : f;
  }
  
  public static int secondsToMilliseconds(float s) {
    return (int) Math.round(s * 1000.0);
  }
  
  public static float millisecondsToSeconds(int ms) {
    return (float) (ms * 0.001);
  }
  
  private Util() {
  }
}
