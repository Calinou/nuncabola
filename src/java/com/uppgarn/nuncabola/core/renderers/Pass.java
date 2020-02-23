/*
 * Pass.java
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

package com.uppgarn.nuncabola.core.renderers;

import com.uppgarn.nuncabola.core.solid.*;

enum Pass {
  OPAQUE(
    0,
    Material.REFLECTIVE | Material.TRANSPARENT | Material.DECAL),
  OPAQUE_DECAL(
    Material.DECAL,
    Material.REFLECTIVE | Material.TRANSPARENT),
  TRANSPARENT_DECAL(
    Material.DECAL | Material.TRANSPARENT,
    Material.REFLECTIVE),
  TRANSPARENT(
    Material.TRANSPARENT,
    Material.REFLECTIVE | Material.DECAL),
  REFLECTIVE(
    Material.REFLECTIVE,
    0);
  
  private final int inFlags;
  private final int exFlags;
  
  Pass(int inFlags, int exFlags) {
    this.inFlags = inFlags;
    this.exFlags = exFlags;
  }
  
  public boolean includes(Asset asset) {
    int flags = asset.getFlags();
    
    return ((flags & inFlags) == inFlags) && ((flags & exFlags) == 0);
  }
}
