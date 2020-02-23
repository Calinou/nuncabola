/*
 * Path.java
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

package com.uppgarn.nuncabola.core.solid;

public final class Path {
  public final PathBase base;
  
  /**
   * Enabled state.
   */
  public boolean enabled;
  
  public Path(PathBase base) {
    this.base = base;
    
    enabled = base.enabled;
  }
  
  public Path(Path src) {
    base = src.base;
    
    enabled = src.enabled;
  }
  
  public void copyFrom(Path src) {
    assert src.base == base;
    
    enabled = src.enabled;
  }
}
