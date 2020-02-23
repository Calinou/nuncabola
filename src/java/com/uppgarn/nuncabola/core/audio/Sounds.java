/*
 * Sounds.java
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

import com.uppgarn.nuncabola.core.folder.*;

final class Sounds {
  private AudioDataManager mgr;
  
  private float volume;
  private Sound first;
  
  public Sounds(Folder dataFolder, int cacheSize) {
    mgr = AudioDataManager.create(dataFolder, cacheSize);
    
    volume = 1.0f;
    first  = null;
  }
  
  public void setVolume(float volume) {
    this.volume = volume;
  }
  
  public void play(String path, float amp) {
    float soundVolume = Math.min(Math.max(amp, 0.0f), 1.0f) * volume;
    
    // If the requested sound is already playing, restart it.
    
    for (Sound sound = first; sound != null; sound = sound.getNext()) {
      if (sound.getPath().equals(path)) {
        sound.restart(soundVolume);
        
        return;
      }
    }
    
    // Otherwise, create a new sound.
    
    Sound sound = new Sound(path, mgr.get(path), soundVolume);
    sound.setNext(first);
    
    first = sound;
  }
  
  public void setPaused(boolean paused) {
    for (Sound sound = first; sound != null; sound = sound.getNext()) {
      sound.setPaused(paused);
    }
  }
  
  public void stop() {
    for (Sound sound = first; sound != null; sound = sound.getNext()) {
      sound.deinitialize();
    }
    
    first = null;
  }
  
  public void step() {
    for (Sound sound = first, prev = null; sound != null;) {
      Sound next = sound.getNext();
      
      if (!sound.step()) {
        // The sound has finished, delete it.
        
        sound.deinitialize();
        
        if (prev != null) {
          prev.setNext(next);
        } else {
          first = next;
        }
      } else {
        prev = sound;
      }
      
      sound = next;
    }
  }
  
  public void deinitialize() {
    stop();
    
    mgr.deinitialize();
  }
}
