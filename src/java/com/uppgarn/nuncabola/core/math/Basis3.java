/*
 * Basis3.java
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

package com.uppgarn.nuncabola.core.math;

public final class Basis3 {
  public final Vector3 x;
  public final Vector3 y;
  public final Vector3 z;
  
  public Basis3() {
    x = new Vector3();
    y = new Vector3();
    z = new Vector3();
    
    setStandard();
  }
  
  public Basis3(Basis3 src) {
    x = new Vector3(src.x);
    y = new Vector3(src.y);
    z = new Vector3(src.z);
  }
  
  public void setStandard() {
    x.set(1.0f, 0.0f, 0.0f);
    y.set(0.0f, 1.0f, 0.0f);
    z.set(0.0f, 0.0f, 1.0f);
  }
  
  public void set(float xX, float xY, float xZ, float yX, float yY, float yZ) {
    x.set(xX, xY, xZ);
    y.set(yX, yY, yZ);
    
    z.cross(x, y);
  }
  
  public void orthonormalize() {
    x.cross(y, z);
    z.cross(x, y);
    
    x.normalize(x);
    y.normalize(y);
    z.normalize(z);
  }
  
  public void copyFrom(Basis3 src) {
    x.copyFrom(src.x);
    y.copyFrom(src.y);
    z.copyFrom(src.z);
  }
  
  public void copyFrom(Basis3 src0, Basis3 src1, float alpha) {
    x.copyFrom(src0.x, src1.x, alpha);
    y.copyFrom(src0.y, src1.y, alpha);
    z.copyFrom(src0.z, src1.z, alpha);
    
    orthonormalize();
  }
}
