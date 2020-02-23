/*
 * Vector3.java
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

import com.uppgarn.nuncabola.core.util.*;

public final class Vector3 {
  public float x;
  public float y;
  public float z;
  
  public Vector3() {
    setZero();
  }
  
  public Vector3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Vector3(Vector3 src) {
    x = src.x;
    y = src.y;
    z = src.z;
  }
  
  public float length() {
    return (float) Math.sqrt(x * x + y * y + z * z);
  }
  
  public float dot(Vector3 v) {
    return x * v.x + y * v.y + z * v.z;
  }
  
  public void setZero() {
    x = 0.0f;
    y = 0.0f;
    z = 0.0f;
  }
  
  public void set(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void normalize(Vector3 v) {
    float d = v.length();
    
    if (d == 0.0f) {
      x = 0.0f;
      y = 0.0f;
      z = 0.0f;
    } else {
      x = v.x / d;
      y = v.y / d;
      z = v.z / d;
    }
  }
  
  public void negate(Vector3 v) {
    x = -v.x;
    y = -v.y;
    z = -v.z;
  }
  
  public void scale(Vector3 v, float k) {
    x = v.x * k;
    y = v.y * k;
    z = v.z * k;
  }
  
  public void divide(Vector3 v, float d) {
    x = v.x / d;
    y = v.y / d;
    z = v.z / d;
  }
  
  public void add(Vector3 v0, Vector3 v1) {
    x = v0.x + v1.x;
    y = v0.y + v1.y;
    z = v0.z + v1.z;
  }
  
  public void subtract(Vector3 v0, Vector3 v1) {
    x = v0.x - v1.x;
    y = v0.y - v1.y;
    z = v0.z - v1.z;
  }
  
  public void cross(Vector3 v0, Vector3 v1) {
    float tx = v0.y * v1.z - v0.z * v1.y;
    float ty = v0.z * v1.x - v0.x * v1.z;
    float tz = v0.x * v1.y - v0.y * v1.x;
    
    x = tx;
    y = ty;
    z = tz;
  }
  
  public void addScaled(Vector3 v0, Vector3 v1, float k) {
    x = v0.x + v1.x * k;
    y = v0.y + v1.y * k;
    z = v0.z + v1.z * k;
  }
  
  public float asAxisAngle(Quaternion q) {
    float d = (float) Math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z);
    
    if (d == 0.0f) {
      x = 0.0f;
      y = 0.0f;
      z = 0.0f;
    } else {
      x = q.x / d;
      y = q.y / d;
      z = q.z / d;
    }
    
    return 2.0f * (float) Math.acos(q.w);
  }
  
  public void rotate(Vector3 v, Quaternion q) {
    float aw = q.w * 0.0f - q.x * v.x  - q.y * v.y  - q.z * v.z;
    float ax = q.w * v.x  + q.x * 0.0f + q.y * v.z  - q.z * v.y;
    float ay = q.w * v.y  - q.x * v.z  + q.y * 0.0f + q.z * v.x;
    float az = q.w * v.z  + q.x * v.y  - q.y * v.x  + q.z * 0.0f;
    
    x = aw * -q.x + ax *  q.w + ay * -q.z - az * -q.y;
    y = aw * -q.y - ax * -q.z + ay *  q.w + az * -q.x;
    z = aw * -q.z + ax * -q.y - ay * -q.x + az *  q.w;
  }
  
  public void transform(Vector3 v, Matrix4 m) {
    float tx = v.x * m.m00 + v.y * m.m10 + v.z * m.m20;
    float ty = v.x * m.m01 + v.y * m.m11 + v.z * m.m21;
    float tz = v.x * m.m02 + v.y * m.m12 + v.z * m.m22;
    
    x = tx;
    y = ty;
    z = tz;
  }
  
  public void copyFrom(Vector3 src) {
    x = src.x;
    y = src.y;
    z = src.z;
  }
  
  public void copyFrom(Vector3 src0, Vector3 src1, float alpha) {
    x = Util.lerp(src0.x, src1.x, alpha);
    y = Util.lerp(src0.y, src1.y, alpha);
    z = Util.lerp(src0.z, src1.z, alpha);
  }
}
