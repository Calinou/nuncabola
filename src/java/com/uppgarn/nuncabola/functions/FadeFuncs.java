/*
 * FadeFuncs.java
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

package com.uppgarn.nuncabola.functions;

public final class FadeFuncs {
  private static Mode  mode;
  private static float time;
  
  private static float elapsedTime;
  
  public static void initialize(Mode mode, float time) {
    FadeFuncs.mode = mode;
    FadeFuncs.time = time;
    
    elapsedTime = 0.0f;
    
    update();
  }
  
  private static void update() {
    GameFuncs.setFade(mode.getValue(elapsedTime / time));
  }
  
  public static boolean isOver() {
    return elapsedTime >= time;
  }
  
  public static void step(float dt) {
    elapsedTime = Math.min(elapsedTime + dt, time);
    
    update();
  }
  
  public static void deinitialize() {
    // No-op.
  }
  
  private FadeFuncs() {
  }
  
  public enum Mode {
    IN (1.0f, 0.0f),
    OUT(0.0f, 1.0f);
    
    private final float start;
    private final float end;
    
    Mode(float start, float end) {
      this.start = start;
      this.end   = end;
    }
    
    float getValue(float k) {
      return start + (end - start) * k;
    }
  }
}
