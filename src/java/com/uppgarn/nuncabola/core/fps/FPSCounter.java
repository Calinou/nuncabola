/*
 * FPSCounter.java
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

package com.uppgarn.nuncabola.core.fps;

public final class FPSCounter {
  private int   fps;
  private float ms;
  
  private boolean firstUpdate;
  private long    lastTime;
  private int     frames;
  private double  seconds;
  
  public FPSCounter() {
    fps = 0;
    ms  = 0.0f;
    
    firstUpdate = true;
    lastTime    = 0;
    frames      = 0;
    seconds     = 0.0;
  }
  
  public int getFPS() {
    return fps;
  }
  
  public float getMS() {
    return ms;
  }
  
  public boolean update(boolean painted) {
    long   time = System.nanoTime();
    double dt   = (time - lastTime) / 1000000000.0;
    
    lastTime = time;
    
    if (firstUpdate) {
      firstUpdate = false;
    } else if (painted) {
      // Accumulate painted frames and spent seconds.
      
      frames  += 1;
      seconds += dt;
      
      // Average over 1s.
      
      if (seconds >= 1.0) {
        // Compute frames-per-second and frame time stats.
        
        fps = (int)   Math.round(frames / seconds);
        ms  = (float) (seconds * 1000.0 / frames);
        
        // Reset the counters for the next update.
        
        frames  = 0;
        seconds = 0.0;
        
        return true;
      }
    }
    
    return false;
  }
}
