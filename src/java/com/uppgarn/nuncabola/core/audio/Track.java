/*
 * Track.java
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

import com.uppgarn.codelibf.io.*;

import java.nio.*;

final class Track {
  private final String path;
  private final Source src;
  
  private float amp;
  private float ampStep;
  
  private AudioStream stream;
  
  public Track(String path, Source src) {
    this.path = path;
    this.src  = src;
    
    amp     = 0.0f;
    ampStep = 0.0f;
    
    stream = null;
  }
  
  public String getPath() {
    return path;
  }
  
  private void fade(int dir, float time) {
    ampStep = (1.0f * dir) / (SAMPLE_RATE * time);
  }
  
  public void fadeIn(float time) {
    fade(+1, time);
  }
  
  public void fadeOut(float time) {
    fade(-1, time);
  }
  
  private void step(ShortBuffer buf, short[] inBuf, int len, boolean mono) {
    for (int idx = 0; idx < len;) {
      short l =            (short) (amp * inBuf[idx++]);
      short r = mono ? l : (short) (amp * inBuf[idx++]);
      
      buf.put(l);
      buf.put(r);
      
      amp += ampStep;
      
      if (amp < 0.0f) {
        amp = 0.0f;
      } else if (amp > 1.0f) {
        amp = 1.0f;
      }
    }
  }
  
  public boolean step(ShortBuffer buf, short[] inBuf) {
    if (stream == null) {
      stream = new AudioStream(src, true);
    }
    
    int read = stream.read(inBuf, buf.remaining() / (stream.isMono() ? 2 : 1));
    
    if (read == 0) {
      return false;
    }
    
    step(buf, inBuf, read, stream.isMono());
    
    return (amp > 0.0f) || (ampStep >= 0.0f);
  }
  
  public void deinitialize() {
    if (stream != null) {
      stream.close();
    }
  }
}
