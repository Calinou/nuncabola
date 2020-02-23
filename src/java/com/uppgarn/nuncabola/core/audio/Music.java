/*
 * Music.java
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

import org.lwjgl.*;

import java.nio.*;

final class Music {
  private final Folder dataFolder;
  
  private float   volume;
  private Track   current;
  private Track   queue;
  private boolean paused;
  
  private ALStreamedSource source;
  
  public Music(Folder dataFolder, int bufferSize) {
    this.dataFolder = dataFolder;
    
    volume  = 1.0f;
    current = null;
    queue   = null;
    paused  = false;
    
    // Create source.
    
    source = new ALStreamedSource(new Callback(bufferSize));
    source.setVolume(volume);
  }
  
  public void setVolume(float volume) {
    this.volume = volume;
    
    source.setVolume(volume);
  }
  
  public void fadeTo(String path, float time) {
    // Delete the queued track, if any.
    
    if (queue != null) {
      queue.deinitialize();
      
      queue = null;
    }
    
    if (current == null) {
      // No current track, create a new one.
      
      current = new Track(path, dataFolder.getSource(path));
      current.fadeIn(time);
    } else if (!current.getPath().equals(path)) {
      // Fade out current track and queue a new one.
      
      current.fadeOut(time);
      
      queue = new Track(path, dataFolder.getSource(path));
      queue.fadeIn(time);
    } else {
      // Fade in current track.
      
      current.fadeIn(time);
    }
  }
  
  public void fadeOut(float time) {
    // Delete the queued track, if any.
    
    if (queue != null) {
      queue.deinitialize();
      
      queue = null;
    }
    
    if (current != null) {
      // Fade out current track.
      
      current.fadeOut(time);
    }
  }
  
  public void setPaused(boolean paused) {
    if (this.paused == paused) {
      return;
    }
    
    this.paused = paused;
    
    if (paused) {
      // Fade before pausing for a nicer transition.
      
      final int fade = 20;
      
      for (int k = fade - 1; k >= 0; k--) {
        source.setVolume(volume * ((float) k / fade));
        
        try {
          Thread.sleep(1);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }
      
      source.pause();
      source.setVolume(volume);
    } else {
      source.play();
    }
  }
  
  public void stop() {
    if (current != null) {
      current.deinitialize();
      
      current = null;
    }
    if (queue != null) {
      queue.deinitialize();
      
      queue = null;
    }
    
    paused = false;
    
    source.reset();
  }
  
  public void step() {
    source.step();
  }
  
  public void deinitialize() {
    stop();
    
    source.deinitialize();
  }
  
  private final class Callback implements ALStreamedSource.Callback {
    private ShortBuffer buf;
    private short[]     inBuf;
    
    public Callback(int bufferSize) {
      int stereoBufferSize = bufferSize * 2;
      
      buf   = BufferUtils.createShortBuffer(stereoBufferSize);
      inBuf = new short                    [stereoBufferSize];
    }
    
    @Override
    public ShortBuffer fetchData() {
      buf.clear();
      
      // Step the current track, filling the buffer.
      
      if ((current != null) && !current.step(buf, inBuf)) {
        // The track has faded out, move to the queued track, if any.
        
        if (queue != null) {
          current.deinitialize();
          
          current = queue;
          queue   = null;
        }
      }
      
      // Zero out any remaining sample slots.
      
      while (buf.hasRemaining()) {
        buf.put((short) 0);
      }
      
      buf.flip();
      
      return buf;
    }
  }
}
