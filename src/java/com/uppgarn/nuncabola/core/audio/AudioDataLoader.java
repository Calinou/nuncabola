/*
 * AudioDataLoader.java
 *
 * Copyright (c) 2003-2017 Nuncabola authors
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

import org.lwjgl.*;

import java.nio.*;
import java.util.*;

final class AudioDataLoader {
  private static final int MAX_SIZE = 524288;
  
  private static ShortBuffer createBuffer(OggStream in)
      throws OggStreamException {
    short[] array = new short[2048];
    int     len   = 0;
    
    for (int read;
        ((read = in.read(array, len, array.length - len)) > 0)
          && ((len += read) < MAX_SIZE);) {
      if (len == array.length) {
        array = Arrays.copyOf(array, Math.min(array.length * 2, MAX_SIZE));
      }
    }
    
    return BufferUtils.createShortBuffer(len).put(array, 0, len);
  }
  
  private static AudioData load(OggStream in) throws OggStreamException {
    int channels   = in.getChannels();
    int sampleRate = in.getSampleRate();
    
    if (!((channels == 1) || (channels == 2)) || (sampleRate != SAMPLE_RATE)) {
      // Unsupported format.
      
      return null;
    }
    
    ShortBuffer buf  = createBuffer(in);
    boolean     mono = channels == 1;
    
    return new AudioData(buf, mono);
  }
  
  public static AudioData load(Source src) {
    try (OggStream in = new OggStream(src)) {
      return load(in);
    } catch (OggStreamException ex) {
      return null;
    }
  }
  
  private AudioDataLoader() {
  }
}
