/*
 * Status.java
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

package com.uppgarn.nuncabola.core.game;

public enum Status {
  NONE    (false),
  GOAL    (true),
  FALL_OUT(true),
  TIME_OUT(true);
  
  private final boolean over;
  
  Status(boolean over) {
    this.over = over;
  }
  
  public boolean isOver() {
    return over;
  }
}
