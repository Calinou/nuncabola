/*
 * View.java
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

package com.uppgarn.nuncabola.core.game;

import com.uppgarn.nuncabola.core.math.*;

public final class View {
  /**
   * Position.
   */
  public Vector3 p;
  
  /**
   * Center.
   */
  public Vector3 c;
  
  /**
   * Reference frame.
   */
  public Basis3 e;
  
  public View() {
    p = new Vector3();
    c = new Vector3();
    e = new Basis3();
  }
  
  public View(View src) {
    p = new Vector3(src.p);
    c = new Vector3(src.c);
    e = new Basis3 (src.e);
  }
  
  public void copyFrom(View src) {
    p.copyFrom(src.p);
    c.copyFrom(src.c);
    e.copyFrom(src.e);
  }
  
  public void copyFrom(View src0, View src1, float alpha) {
    if    ((Math.abs(src1.e.x.x - src0.e.x.x) > 1.0f)
        || (Math.abs(src1.e.x.z - src0.e.x.z) > 1.0f)) {
      copyFrom(src1);
      
      return;
    }
    
    p.copyFrom(src0.p, src1.p, alpha);
    c.copyFrom(src0.c, src1.c, alpha);
    e.copyFrom(src0.e, src1.e, alpha);
  }
}
