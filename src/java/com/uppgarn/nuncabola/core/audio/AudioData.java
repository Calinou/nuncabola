/*
 * AudioData.java
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

import java.nio.*;

final class AudioData {
  private final ShortBuffer buf;
  private final boolean     mono;
  
  public AudioData(ShortBuffer buf, boolean mono) {
    this.buf  = (ShortBuffer) buf.duplicate().rewind();
    this.mono = mono;
  }
  
  public ShortBuffer getBuffer() {
    return buf.duplicate();
  }
  
  public boolean isMono() {
    return mono;
  }
  
  public int getSize() {
    return buf.capacity();
  }
}
