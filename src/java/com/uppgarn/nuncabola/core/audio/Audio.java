/*
 * Audio.java
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

import com.uppgarn.codelibf.util.*;

import org.lwjgl.*;
import org.lwjgl.openal.*;

public final class Audio {
  private static boolean enabled;
  
  private static Sounds sounds;
  private static Music  music;
  
  private static TerminableThread thread;
  
  public static void initialize(Folder dataFolder, int bufferSize) {
    try {
      AL.create();
      
      enabled = true;
      
      sounds = new Sounds(dataFolder, 524288);
      music  = new Music (dataFolder, bufferSize);
      
      thread = new AudioThread();
      thread.setPriority(Thread.MAX_PRIORITY);
      thread.start();
    } catch (LWJGLException ex) {
      enabled = false;
    }
  }
  
  public static void setSoundVolume(int volume) {
    if (!enabled) {
      return;
    }
    
    synchronized (sounds) {
      sounds.setVolume(volume / 10.0f);
    }
  }
  
  public static void setMusicVolume(int volume) {
    if (!enabled) {
      return;
    }
    
    synchronized (music) {
      music.setVolume(volume / 10.0f);
    }
  }
  
  public static void playSound(String path) {
    playSound(path, 1.0f);
  }
  
  public static void playSound(String path, float amp) {
    if (!enabled) {
      return;
    }
    
    synchronized (sounds) {
      sounds.play(path, amp);
    }
  }
  
  public static void fadeToMusic(String path, float time) {
    if (!enabled) {
      return;
    }
    
    synchronized (music) {
      music.fadeTo(path, time);
    }
  }
  
  public static void fadeOutMusic(float time) {
    if (!enabled) {
      return;
    }
    
    synchronized (music) {
      music.fadeOut(time);
    }
  }
  
  public static void setSoundsPaused(boolean paused) {
    if (!enabled) {
      return;
    }
    
    synchronized (sounds) {
      sounds.setPaused(paused);
    }
  }
  
  public static void setMusicPaused(boolean paused) {
    if (!enabled) {
      return;
    }
    
    synchronized (music) {
      music.setPaused(paused);
    }
  }
  
  public static void setPaused(boolean paused) {
    setSoundsPaused(paused);
    setMusicPaused (paused);
  }
  
  public static void stopSounds() {
    if (!enabled) {
      return;
    }
    
    synchronized (sounds) {
      sounds.stop();
    }
  }
  
  public static void stopMusic() {
    if (!enabled) {
      return;
    }
    
    synchronized (music) {
      music.stop();
    }
  }
  
  public static void stop() {
    stopSounds();
    stopMusic();
  }
  
  public static void deinitialize() {
    if (!enabled) {
      return;
    }
    
    thread.terminate();
    
    sounds.deinitialize();
    music .deinitialize();
    
    AL.destroy();
    
    sounds = null;
    music  = null;
    thread = null;
  }
  
  private Audio() {
  }
  
  private static final class AudioThread extends TerminableThread {
    @Override
    public void run() {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          synchronized (sounds) {
            sounds.step();
          }
          synchronized (music) {
            music .step();
          }
          
          Thread.sleep(1);
        }
      } catch (InterruptedException ex) {
        // Allow thread to exit.
      }
    }
  }
}
