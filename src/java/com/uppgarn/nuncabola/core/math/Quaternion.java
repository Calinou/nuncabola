/*
 * Quaternion.java
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

import static com.uppgarn.nuncabola.core.math.MathConstants.*;

public final class Quaternion {
  public float w;
  public float x;
  public float y;
  public float z;
  
  public Quaternion() {
    setIdentity();
  }
  
  public Quaternion(float w, float x, float y, float z) {
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Quaternion(Quaternion src) {
    w = src.w;
    x = src.x;
    y = src.y;
    z = src.z;
  }
  
  public float length() {
    return (float) Math.sqrt(w * w + x * x + y * y + z * z);
  }
  
  public float dot(Quaternion q) {
    return w * q.w + x * q.x + y * q.y + z * q.z;
  }
  
  public void setIdentity() {
    w = 1.0f;
    x = 0.0f;
    y = 0.0f;
    z = 0.0f;
  }
  
  public void set(float w, float x, float y, float z) {
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void normalize(Quaternion q) {
    float d = q.length();
    
    if (d == 0.0f) {
      w = 1.0f;
      x = 0.0f;
      y = 0.0f;
      z = 0.0f;
    } else {
      w = q.w / d;
      x = q.x / d;
      y = q.y / d;
      z = q.z / d;
    }
  }
  
  public void conjugate(Quaternion q) {
    w =  q.w;
    x = -q.x;
    y = -q.y;
    z = -q.z;
  }
  
  public void multiply(Quaternion q0, Quaternion q1) {
    float tw = q0.w * q1.w - q0.x * q1.x - q0.y * q1.y - q0.z * q1.z;
    float tx = q0.w * q1.x + q0.x * q1.w + q0.y * q1.z - q0.z * q1.y;
    float ty = q0.w * q1.y - q0.x * q1.z + q0.y * q1.w + q0.z * q1.x;
    float tz = q0.w * q1.z + q0.x * q1.y - q0.y * q1.x + q0.z * q1.w;
    
    w = tw;
    x = tx;
    y = ty;
    z = tz;
  }
  
  public void byAxisAngle(Vector3 v, float a) {
    float c = (float) Math.cos(a * 0.5f);
    float s = (float) Math.sin(a * 0.5f);
    
    float nx;
    float ny;
    float nz;
    
    float d = v.length();
    
    if (d == 0.0f) {
      nx = 0.0f;
      ny = 0.0f;
      nz = 0.0f;
    } else {
      nx = v.x / d;
      ny = v.y / d;
      nz = v.z / d;
    }
    
    w =      c;
    x = nx * s;
    y = ny * s;
    z = nz * s;
  }
  
  public void copyFrom(Quaternion src) {
    w = src.w;
    x = src.x;
    y = src.y;
    z = src.z;
  }
  
  public void copyFrom(Quaternion src0, Quaternion src1, float alpha) {
    if (alpha <= 0.0f) {
      copyFrom(src0);
      
      return;
    }
    if (alpha >= 1.0f) {
      copyFrom(src1);
      
      return;
    }
    
    // src0 . src1 = |src0||src1| cos A
    // |src0| = |src1| = 1
    
    float c = src0.dot(src1);
    int   i = +1;
    
    // Ensure the shortest path.
    
    if (c < 0.0f) {
      c = -c;
      i = -1;
    }
    
    // Interpolate.
    
    float k0;
    float k1;
    
    if (1.0f - c < (float) TINY) {
      // Normalized lerp for very similar orientations.
      
      k0 = 1.0f - alpha;
      k1 = alpha;
    } else {
      // Spherical linear interpolation.
      
      float r = (float) Math.acos(c);
      float s = (float) Math.sin (r);
      
      k0 = (float) Math.sin((1.0f - alpha) * r) / s;
      k1 = (float) Math.sin(alpha          * r) / s * i;
    }
    
    w = src0.w * k0 + src1.w * k1;
    x = src0.x * k0 + src1.x * k1;
    y = src0.y * k0 + src1.y * k1;
    z = src0.z * k0 + src1.z * k1;
    
    normalize(this);
  }
}
