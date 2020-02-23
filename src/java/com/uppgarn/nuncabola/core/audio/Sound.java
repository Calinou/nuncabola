/*
 * Sound.java
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

package com.uppgarn.nuncabola.core.audio;

import com.uppgarn.codelibf.util.*;

final class Sound {
  private final String                  path;
  private final FutureHolder<AudioData> holder;
  
  private float   volume;
  private boolean paused;
  
  private ALBufferedSource source;
  private long             startTime;
  
  private Sound next;
  
  public Sound(String path, FutureHolder<AudioData> holder, float volume) {
    this.path   = path;
    this.holder = holder;
    
    this.volume = volume;
    this.paused = false;
    
    source = null;
    
    next = null;
    
    step();
  }
  
  public String getPath() {
    return path;
  }
  
  public void setPaused(boolean paused) {
    if (this.paused == paused) {
      return;
    }
    
    this.paused = paused;
    
    if (source != null) {
      if (paused) {
        source.pause();
      } else {
        if (!source.isStopped()) {
          source.play();
        }
      }
    }
  }
  
  public void restart(float volume) {
    // Prevent a quieter sound from preempting a louder one
    // if fewer than 100ms of the latter have been played.
    
    if ((volume < this.volume)
        && ((source == null)
          || (System.nanoTime() - startTime < 100 * 1000000L))) {
      return;
    }
    
    this.volume = volume;
    
    if (source != null) {
      source.rewind();
      source.setVolume(volume);
      
      startTime = System.nanoTime();
      
      if (!paused) {
        source.play();
      }
    }
  }
  
  public boolean step() {
    if (source == null) {
      // Check if the audio data are ready.
      
      if (!holder.isReady()) {
        // Not yet, keep waiting.
        
        return true;
      }
      
      // Get audio data.
      
      AudioData data = holder.get();
      
      if (data == null) {
        // Loading failed.
        
        return false;
      }
      
      // Create source.
      
      source = new ALBufferedSource(data);
      source.setVolume(volume);
      
      // Play.
      
      startTime = System.nanoTime();
      
      if (!paused) {
        source.play();
      }
    }
    
    return !source.isStopped();
  }
  
  public Sound getNext() {
    return next;
  }
  
  public void setNext(Sound sound) {
    next = sound;
  }
  
  public void deinitialize() {
    if (source != null) {
      source.deinitialize();
    }
  }
}
