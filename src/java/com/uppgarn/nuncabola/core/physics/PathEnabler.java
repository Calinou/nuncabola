/*
 * PathEnabler.java
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

package com.uppgarn.nuncabola.core.physics;

import com.uppgarn.nuncabola.core.solid.*;

public final class PathEnabler {
  private final Solid               sol;
  private final PathEnablerListener listener;
  
  public PathEnabler(Solid sol, PathEnablerListener listener) {
    this.sol      = sol;
    this.listener = listener;
  }
  
  public void setPathEnabled(int pathIdx, boolean enabled) {
    // Tortoise-and-hare cycle traverser.
    
    int idx0 = pathIdx;
    int idx1 = pathIdx;
    
    do {
      idx0 = sol.paths[idx0].base.pathIdx;
      idx1 = sol.paths[idx1].base.pathIdx;
      idx1 = sol.paths[idx1].base.pathIdx;
    } while (idx0 != idx1);
    
    // Walk nodes before cycle.
    
    idx0 = pathIdx;
    
    while (idx0 != idx1) {
      Path path0 = sol.paths[idx0];
      Path path1 = sol.paths[idx1];
      
      if (path0.enabled != enabled) {
        path0.enabled = enabled;
        
        if (listener != null) {
          listener.pathEnabled(idx0, path0.enabled);
        }
      }
      
      idx0 = path0.base.pathIdx;
      idx1 = path1.base.pathIdx;
    }
    
    // Walk nodes within cycle.
    
    do {
      Path path1 = sol.paths[idx1];
      
      if (path1.enabled != enabled) {
        path1.enabled = enabled;
        
        if (listener != null) {
          listener.pathEnabled(idx1, path1.enabled);
        }
      }
      
      idx1 = path1.base.pathIdx;
    } while (idx0 != idx1);
  }
}
