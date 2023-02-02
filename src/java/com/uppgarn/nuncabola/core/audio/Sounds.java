/*
 * Sounds.java
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

package com.uppgarn.nuncabola.core.audio;

import com.uppgarn.nuncabola.core.folder.*;

import java.util.*;

final class Sounds {
  private AudioDataManager mgr;
  
  private float volume;
  
  private List<Sound> sounds;
  
  public Sounds(Folder dataFolder, int cacheSize) {
    mgr = AudioDataManager.create(dataFolder, cacheSize);
    
    volume = 1.0f;
    
    sounds = new ArrayList<>();
  }
  
  public void setVolume(float volume) {
    this.volume = volume;
    
    for (int idx = 0; idx < sounds.size(); idx++) {
      Sound sound = sounds.get(idx);
      
      sound.setVolume(volume);
    }
  }
  
  public void play(String path, float amp) {
    // If the requested sound is already playing, restart it.
    
    for (int idx = 0; idx < sounds.size(); idx++) {
      Sound sound = sounds.get(idx);
      
      if (sound.getPath().equals(path)) {
        sound.restart(amp);
        
        return;
      }
    }
    
    // Otherwise, create a new sound.
    
    sounds.add(new Sound(path, mgr.get(path), volume, amp));
  }
  
  public void setPaused(boolean paused) {
    for (int idx = 0; idx < sounds.size(); idx++) {
      Sound sound = sounds.get(idx);
      
      sound.setPaused(paused);
    }
  }
  
  public void stop() {
    for (int idx = 0; idx < sounds.size(); idx++) {
      Sound sound = sounds.get(idx);
      
      sound.deinitialize();
    }
    
    sounds.clear();
  }
  
  public void step() {
    for (int idx = sounds.size() - 1; idx >= 0; idx--) {
      Sound sound = sounds.get(idx);
      
      if (!sound.step()) {
        // The sound has finished, delete it.
        
        sound.deinitialize();
        
        sounds.remove(idx);
      }
    }
  }
  
  public void deinitialize() {
    stop();
    
    mgr.deinitialize();
  }
}
