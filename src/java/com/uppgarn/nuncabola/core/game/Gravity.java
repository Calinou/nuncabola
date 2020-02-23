/*
 * Gravity.java
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

final class Gravity {
  private static final Vector3 NORMAL   = new Vector3(0.0f, -9.8f, 0.0f);
  private static final Vector3 INVERTED = new Vector3(0.0f, +9.8f, 0.0f);
  
  public static void get(Vector3 g, boolean inverted, Tilt tilt) {
    Matrix4 x_ = new Matrix4();
    Matrix4 z_ = new Matrix4();
    Matrix4 m_ = new Matrix4();
    
    z_.rotate  (tilt.z, (float) Math.toRadians(tilt.rz));
    x_.rotate  (tilt.x, (float) Math.toRadians(tilt.rx));
    m_.multiply(z_, x_);
    
    g.transform(inverted ? INVERTED : NORMAL, m_);
  }
  
  private Gravity() {
  }
}
