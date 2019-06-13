/*
 * AudioStream.java
 *
 * Copyright (c) 2003-2019 Nuncabola authors
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
import com.uppgarn.codelibf.sound.*;

final class AudioStream {
  private final Source  src;
  private final boolean loop;
  
  private OggStream in;
  private boolean   mono;
  
  public AudioStream(Source src, boolean loop) {
    this.src  = src;
    this.loop = loop;
    
    openStream();
    
    mono = (in == null) || (in.getChannels() == 1);
  }
  
  private void openStream() {
    try {
      in = new OggStream(src);
      
      int channels   = in.getChannels();
      int sampleRate = in.getSampleRate();
      
      if (!((channels == 1) || (channels == 2))
          || (sampleRate != SAMPLE_RATE)) {
        // Unsupported format.
        
        in.close();
        
        in = null;
      }
    } catch (OggStreamException ex) {
      in = null;
    }
  }
  
  private void closeStream() {
    if (in != null) {
      in.close();
      
      in = null;
    }
  }
  
  public boolean isMono() {
    return mono;
  }
  
  public int read(short[] buf, int len) {
    int count = 0;
    
    boolean lastEOF = false;
    
    while ((in != null) && (count < len)) {
      try {
        int read = in.read(buf, count, len - count);
        
        if (read > 0) {
          count += read;
          
          lastEOF = false;
        } else {
          closeStream();
          
          if (loop && !lastEOF) {
            openStream();
            
            lastEOF = true;
          }
        }
      } catch (OggStreamException ex) {
        closeStream();
      }
    }
    
    return count;
  }
  
  public void close() {
    closeStream();
  }
}
