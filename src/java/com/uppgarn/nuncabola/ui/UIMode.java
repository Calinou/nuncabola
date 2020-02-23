/*
 * UIMode.java
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

package com.uppgarn.nuncabola.ui;

import java.nio.file.*;

public abstract class UIMode {
  private UIMode() {
  }
  
  public abstract Type getType();
  
  public enum Type {
    STANDARD,
    REPLAY
  }
  
  public static final class Standard extends UIMode {
    public static final Standard INSTANCE = new Standard();
    
    private Standard() {
    }
    
    @Override
    public Type getType() {
      return Type.STANDARD;
    }
  }
  
  public static final class Replay extends UIMode {
    private final Path file;
    
    public Replay(Path file) {
      this.file = file;
    }
    
    public Path getFile() {
      return file;
    }
    
    @Override
    public Type getType() {
      return Type.REPLAY;
    }
  }
}
