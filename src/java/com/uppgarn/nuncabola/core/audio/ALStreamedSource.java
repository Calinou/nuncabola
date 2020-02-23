/*
 * ALStreamedSource.java
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

import static com.uppgarn.nuncabola.core.audio.AudioConstants.*;

import org.lwjgl.*;
import org.lwjgl.openal.*;

import static org.lwjgl.openal.AL10.*;

import java.nio.*;

final class ALStreamedSource {
  private final Callback callback;
  
  private Integer   source;
  private IntBuffer buffers;
  
  private boolean buffersInitialized;
  private boolean play;
  
  private boolean errorOccurred;
  
  public ALStreamedSource(Callback callback) {
    this.callback = callback;
    
    source  = createSource();
    buffers = createBuffers();
    
    buffersInitialized = false;
    play               = true;
    
    errorOccurred = (source == null) || (buffers == null);
  }
  
  private Integer createSource() {
    try {
      return alGenSources();
    } catch (OpenALException ex) {
      return null;
    }
  }
  
  private IntBuffer createBuffers() {
    try {
      IntBuffer buffers = BufferUtils.createIntBuffer(2);
      
      alGenBuffers(buffers);
      
      return buffers;
    } catch (OpenALException ex) {
      return null;
    }
  }
  
  public void setVolume(float volume) {
    if (errorOccurred) {
      return;
    }
    
    try {
      alSourcef(source, AL_GAIN, volume);
    } catch (OpenALException ex) {
      errorOccurred = true;
    }
  }
  
  public void play() {
    if (errorOccurred) {
      return;
    }
    
    play = true;
    
    try {
      alSourcePlay(source);
    } catch (OpenALException ex) {
      errorOccurred = true;
    }
  }
  
  public void pause() {
    if (errorOccurred) {
      return;
    }
    
    play = false;
    
    try {
      alSourcePause(source);
    } catch (OpenALException ex) {
      errorOccurred = true;
    }
  }
  
  public void reset() {
    if (errorOccurred) {
      return;
    }
    
    play = true;
    
    try {
      alSourceStop(source);
    } catch (OpenALException ex) {
      errorOccurred = true;
    }
  }
  
  private void fillBuffer(int buffer) {
    ShortBuffer data = callback.fetchData();
    
    alBufferData(buffer, AL_FORMAT_STEREO16, data, SAMPLE_RATE);
  }
  
  public void step() {
    if (errorOccurred) {
      return;
    }
    
    try {
      // Initialize buffers if this has not been done yet.
      
      if (!buffersInitialized) {
        buffersInitialized = true;
        
        for (int idx = 0; idx < buffers.capacity(); idx++) {
          fillBuffer(buffers.get(idx));
        }
        
        alSourceQueueBuffers(source, buffers);
        
        if (play) {
          alSourcePlay(source);
        }
      }
      
      // Refill processed buffers.
      
      for (int done = alGetSourcei(source, AL_BUFFERS_PROCESSED);
          done > 0;
          done--) {
        int buffer = alSourceUnqueueBuffers(source);
        
        fillBuffer(buffer);
        
        alSourceQueueBuffers(source, buffer);
      }
      
      // Restart source after a reset or a buffer underrun.
      
      if (play && (alGetSourcei(source, AL_SOURCE_STATE) != AL_PLAYING)) {
        alSourcePlay(source);
      }
    } catch (OpenALException ex) {
      errorOccurred = true;
    }
  }
  
  public void deinitialize() {
    if (source != null) {
      try {
        alDeleteSources(source);
      } catch (OpenALException ex) {
      }
    }
    if (buffers != null) {
      try {
        alDeleteBuffers(buffers);
      } catch (OpenALException ex) {
      }
    }
  }
  
  public interface Callback {
    ShortBuffer fetchData();
  }
}
