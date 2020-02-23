/*
 * Matrix4.java
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

public final class Matrix4 {
  public float m00;
  public float m01;
  public float m02;
  public float m03;
  
  public float m10;
  public float m11;
  public float m12;
  public float m13;
  
  public float m20;
  public float m21;
  public float m22;
  public float m23;
  
  public float m30;
  public float m31;
  public float m32;
  public float m33;
  
  public Matrix4() {
    setIdentity();
  }
  
  public Matrix4(
      float m00,
      float m01,
      float m02,
      float m03,
      float m10,
      float m11,
      float m12,
      float m13,
      float m20,
      float m21,
      float m22,
      float m23,
      float m30,
      float m31,
      float m32,
      float m33) {
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m03 = m03;
    
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m13 = m13;
    
    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;
    this.m23 = m23;
    
    this.m30 = m30;
    this.m31 = m31;
    this.m32 = m32;
    this.m33 = m33;
  }
  
  public Matrix4(Matrix4 src) {
    m00 = src.m00;
    m01 = src.m01;
    m02 = src.m02;
    m03 = src.m03;
    
    m10 = src.m10;
    m11 = src.m11;
    m12 = src.m12;
    m13 = src.m13;
    
    m20 = src.m20;
    m21 = src.m21;
    m22 = src.m22;
    m23 = src.m23;
    
    m30 = src.m30;
    m31 = src.m31;
    m32 = src.m32;
    m33 = src.m33;
  }
  
  public void setIdentity() {
    m00 = 1.0f;
    m01 = 0.0f;
    m02 = 0.0f;
    m03 = 0.0f;
    
    m10 = 0.0f;
    m11 = 1.0f;
    m12 = 0.0f;
    m13 = 0.0f;
    
    m20 = 0.0f;
    m21 = 0.0f;
    m22 = 1.0f;
    m23 = 0.0f;
    
    m30 = 0.0f;
    m31 = 0.0f;
    m32 = 0.0f;
    m33 = 1.0f;
  }
  
  public void set(
      float m00,
      float m01,
      float m02,
      float m03,
      float m10,
      float m11,
      float m12,
      float m13,
      float m20,
      float m21,
      float m22,
      float m23,
      float m30,
      float m31,
      float m32,
      float m33) {
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m03 = m03;
    
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m13 = m13;
    
    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;
    this.m23 = m23;
    
    this.m30 = m30;
    this.m31 = m31;
    this.m32 = m32;
    this.m33 = m33;
  }
  
  public void transpose(Matrix4 m) {
    float tm00 = m.m00;
    float tm01 = m.m10;
    float tm02 = m.m20;
    float tm03 = m.m30;
    
    float tm10 = m.m01;
    float tm11 = m.m11;
    float tm12 = m.m21;
    float tm13 = m.m31;
    
    float tm20 = m.m02;
    float tm21 = m.m12;
    float tm22 = m.m22;
    float tm23 = m.m32;
    
    float tm30 = m.m03;
    float tm31 = m.m13;
    float tm32 = m.m23;
    float tm33 = m.m33;
    
    m00 = tm00;
    m01 = tm01;
    m02 = tm02;
    m03 = tm03;
    
    m10 = tm10;
    m11 = tm11;
    m12 = tm12;
    m13 = tm13;
    
    m20 = tm20;
    m21 = tm21;
    m22 = tm22;
    m23 = tm23;
    
    m30 = tm30;
    m31 = tm31;
    m32 = tm32;
    m33 = tm33;
  }
  
  public boolean invert(Matrix4 m) {
    double mm00 = m.m00;
    double mm01 = m.m01;
    double mm02 = m.m02;
    double mm03 = m.m03;
    
    double mm10 = m.m10;
    double mm11 = m.m11;
    double mm12 = m.m12;
    double mm13 = m.m13;
    
    double mm20 = m.m20;
    double mm21 = m.m21;
    double mm22 = m.m22;
    double mm23 = m.m23;
    
    double mm30 = m.m30;
    double mm31 = m.m31;
    double mm32 = m.m32;
    double mm33 = m.m33;
    
    double tm00 = +(mm11 * (mm22 * mm33 - mm23 * mm32) -
                    mm21 * (mm12 * mm33 - mm13 * mm32) +
                    mm31 * (mm12 * mm23 - mm13 * mm22));
    double tm01 = -(mm10 * (mm22 * mm33 - mm23 * mm32) -
                    mm20 * (mm12 * mm33 - mm13 * mm32) +
                    mm30 * (mm12 * mm23 - mm13 * mm22));
    double tm02 = +(mm10 * (mm21 * mm33 - mm23 * mm31) -
                    mm20 * (mm11 * mm33 - mm13 * mm31) +
                    mm30 * (mm11 * mm23 - mm13 * mm21));
    double tm03 = -(mm10 * (mm21 * mm32 - mm22 * mm31) -
                    mm20 * (mm11 * mm32 - mm12 * mm31) +
                    mm30 * (mm11 * mm22 - mm12 * mm21));
    
    double tm10 = -(mm01 * (mm22 * mm33 - mm23 * mm32) -
                    mm21 * (mm02 * mm33 - mm03 * mm32) +
                    mm31 * (mm02 * mm23 - mm03 * mm22));
    double tm11 = +(mm00 * (mm22 * mm33 - mm23 * mm32) -
                    mm20 * (mm02 * mm33 - mm03 * mm32) +
                    mm30 * (mm02 * mm23 - mm03 * mm22));
    double tm12 = -(mm00 * (mm21 * mm33 - mm23 * mm31) -
                    mm20 * (mm01 * mm33 - mm03 * mm31) +
                    mm30 * (mm01 * mm23 - mm03 * mm21));
    double tm13 = +(mm00 * (mm21 * mm32 - mm22 * mm31) -
                    mm20 * (mm01 * mm32 - mm02 * mm31) +
                    mm30 * (mm01 * mm22 - mm02 * mm21));
    
    double tm20 = +(mm01 * (mm12 * mm33 - mm13 * mm32) -
                    mm11 * (mm02 * mm33 - mm03 * mm32) +
                    mm31 * (mm02 * mm13 - mm03 * mm12));
    double tm21 = -(mm00 * (mm12 * mm33 - mm13 * mm32) -
                    mm10 * (mm02 * mm33 - mm03 * mm32) +
                    mm30 * (mm02 * mm13 - mm03 * mm12));
    double tm22 = +(mm00 * (mm11 * mm33 - mm13 * mm31) -
                    mm10 * (mm01 * mm33 - mm03 * mm31) +
                    mm30 * (mm01 * mm13 - mm03 * mm11));
    double tm23 = -(mm00 * (mm11 * mm32 - mm12 * mm31) -
                    mm10 * (mm01 * mm32 - mm02 * mm31) +
                    mm30 * (mm01 * mm12 - mm02 * mm11));
    
    double tm30 = -(mm01 * (mm12 * mm23 - mm13 * mm22) -
                    mm11 * (mm02 * mm23 - mm03 * mm22) +
                    mm21 * (mm02 * mm13 - mm03 * mm12));
    double tm31 = +(mm00 * (mm12 * mm23 - mm13 * mm22) -
                    mm10 * (mm02 * mm23 - mm03 * mm22) +
                    mm20 * (mm02 * mm13 - mm03 * mm12));
    double tm32 = -(mm00 * (mm11 * mm23 - mm13 * mm21) -
                    mm10 * (mm01 * mm23 - mm03 * mm21) +
                    mm20 * (mm01 * mm13 - mm03 * mm11));
    double tm33 = +(mm00 * (mm11 * mm22 - mm12 * mm21) -
                    mm10 * (mm01 * mm22 - mm02 * mm21) +
                    mm20 * (mm01 * mm12 - mm02 * mm11));
    
    double d = mm00 * tm00 + mm10 * tm10 + mm20 * tm20 + mm30 * tm30;
    
    if (Math.abs(d) > TINY) {
      m00 = (float) (tm00 / d);
      m01 = (float) (tm10 / d);
      m02 = (float) (tm20 / d);
      m03 = (float) (tm30 / d);
      
      m10 = (float) (tm01 / d);
      m11 = (float) (tm11 / d);
      m12 = (float) (tm21 / d);
      m13 = (float) (tm31 / d);
      
      m20 = (float) (tm02 / d);
      m21 = (float) (tm12 / d);
      m22 = (float) (tm22 / d);
      m23 = (float) (tm32 / d);
      
      m30 = (float) (tm03 / d);
      m31 = (float) (tm13 / d);
      m32 = (float) (tm23 / d);
      m33 = (float) (tm33 / d);
      
      return true;
    }
    
    return false;
  }
  
  public void multiply(Matrix4 m0, Matrix4 m1) {
    float tm00 = m0.m00 * m1.m00
               + m0.m10 * m1.m01
               + m0.m20 * m1.m02
               + m0.m30 * m1.m03;
    float tm01 = m0.m01 * m1.m00
               + m0.m11 * m1.m01
               + m0.m21 * m1.m02
               + m0.m31 * m1.m03;
    float tm02 = m0.m02 * m1.m00
               + m0.m12 * m1.m01
               + m0.m22 * m1.m02
               + m0.m32 * m1.m03;
    float tm03 = m0.m03 * m1.m00
               + m0.m13 * m1.m01
               + m0.m23 * m1.m02
               + m0.m33 * m1.m03;
    
    float tm10 = m0.m00 * m1.m10
               + m0.m10 * m1.m11
               + m0.m20 * m1.m12
               + m0.m30 * m1.m13;
    float tm11 = m0.m01 * m1.m10
               + m0.m11 * m1.m11
               + m0.m21 * m1.m12
               + m0.m31 * m1.m13;
    float tm12 = m0.m02 * m1.m10
               + m0.m12 * m1.m11
               + m0.m22 * m1.m12
               + m0.m32 * m1.m13;
    float tm13 = m0.m03 * m1.m10
               + m0.m13 * m1.m11
               + m0.m23 * m1.m12
               + m0.m33 * m1.m13;
    
    float tm20 = m0.m00 * m1.m20
               + m0.m10 * m1.m21
               + m0.m20 * m1.m22
               + m0.m30 * m1.m23;
    float tm21 = m0.m01 * m1.m20
               + m0.m11 * m1.m21
               + m0.m21 * m1.m22
               + m0.m31 * m1.m23;
    float tm22 = m0.m02 * m1.m20
               + m0.m12 * m1.m21
               + m0.m22 * m1.m22
               + m0.m32 * m1.m23;
    float tm23 = m0.m03 * m1.m20
               + m0.m13 * m1.m21
               + m0.m23 * m1.m22
               + m0.m33 * m1.m23;
    
    float tm30 = m0.m00 * m1.m30
               + m0.m10 * m1.m31
               + m0.m20 * m1.m32
               + m0.m30 * m1.m33;
    float tm31 = m0.m01 * m1.m30
               + m0.m11 * m1.m31
               + m0.m21 * m1.m32
               + m0.m31 * m1.m33;
    float tm32 = m0.m02 * m1.m30
               + m0.m12 * m1.m31
               + m0.m22 * m1.m32
               + m0.m32 * m1.m33;
    float tm33 = m0.m03 * m1.m30
               + m0.m13 * m1.m31
               + m0.m23 * m1.m32
               + m0.m33 * m1.m33;
    
    m00 = tm00;
    m01 = tm01;
    m02 = tm02;
    m03 = tm03;
    
    m10 = tm10;
    m11 = tm11;
    m12 = tm12;
    m13 = tm13;
    
    m20 = tm20;
    m21 = tm21;
    m22 = tm22;
    m23 = tm23;
    
    m30 = tm30;
    m31 = tm31;
    m32 = tm32;
    m33 = tm33;
  }
  
  public void rotate(Vector3 v, float a) {
    float s = (float) Math.sin(a);
    float c = (float) Math.cos(a);
    
    float ux;
    float uy;
    float uz;
    
    float d = v.length();
    
    if (d == 0.0f) {
      ux = 0.0f;
      uy = 0.0f;
      uz = 0.0f;
    } else {
      ux = v.x / d;
      uy = v.y / d;
      uz = v.z / d;
    }
    
    float um00 = ux * ux;
    float um01 = uy * ux;
    float um02 = uz * ux;
    
    float um10 = ux * uy;
    float um11 = uy * uy;
    float um12 = uz * uy;
    
    float um20 = ux * uz;
    float um21 = uy * uz;
    float um22 = uz * uz;
    
    m00 = um00 + c * (1.0f - um00) + s * 0.0f;
    m01 = um01 + c * (0.0f - um01) + s *  uz;
    m02 = um02 + c * (0.0f - um02) + s * -uy;
    m03 = 0.0f;
    
    m10 = um10 + c * (0.0f - um10) + s * -uz;
    m11 = um11 + c * (1.0f - um11) + s * 0.0f;
    m12 = um12 + c * (0.0f - um12) + s *  ux;
    m13 = 0.0f;
    
    m20 = um20 + c * (0.0f - um20) + s *  uy;
    m21 = um21 + c * (0.0f - um21) + s * -ux;
    m22 = um22 + c * (1.0f - um22) + s * 0.0f;
    m23 = 0.0f;
    
    m30 = 0.0f;
    m31 = 0.0f;
    m32 = 0.0f;
    m33 = 1.0f;
  }
  
  public void basis(Vector3 x, Vector3 y, Vector3 z) {
    m00 = x.x;
    m01 = x.y;
    m02 = x.z;
    m03 = 0.0f;
    
    m10 = y.x;
    m11 = y.y;
    m12 = y.z;
    m13 = 0.0f;
    
    m20 = z.x;
    m21 = z.y;
    m22 = z.z;
    m23 = 0.0f;
    
    m30 = 0.0f;
    m31 = 0.0f;
    m32 = 0.0f;
    m33 = 1.0f;
  }
  
  public void view(Vector3 c, Vector3 p, Vector3 u) {
    float zx = p.x - c.x;
    float zy = p.y - c.y;
    float zz = p.z - c.z;
    
    float zd = (float) Math.sqrt(zx * zx + zy * zy + zz * zz);
    
    if (zd == 0.0f) {
      zx = 0.0f;
      zy = 0.0f;
      zz = 0.0f;
    } else {
      zx /= zd;
      zy /= zd;
      zz /= zd;
    }
    
    float xx = u.y * zz - u.z * zy;
    float xy = u.z * zx - u.x * zz;
    float xz = u.x * zy - u.y * zx;
    
    float xd = (float) Math.sqrt(xx * xx + xy * xy + xz * xz);
    
    if (xd == 0.0f) {
      xx = 0.0f;
      xy = 0.0f;
      xz = 0.0f;
    } else {
      xx /= xd;
      xy /= xd;
      xz /= xd;
    }
    
    float yx = zy * xz - zz * xy;
    float yy = zz * xx - zx * xz;
    float yz = zx * xy - zy * xx;
    
    m00 = xx;
    m01 = xy;
    m02 = xz;
    m03 = 0.0f;
    
    m10 = yx;
    m11 = yy;
    m12 = yz;
    m13 = 0.0f;
    
    m20 = zx;
    m21 = zy;
    m22 = zz;
    m23 = 0.0f;
    
    m30 = 0.0f;
    m31 = 0.0f;
    m32 = 0.0f;
    m33 = 1.0f;
  }
  
  public void copyFrom(Matrix4 src) {
    m00 = src.m00;
    m01 = src.m01;
    m02 = src.m02;
    m03 = src.m03;
    
    m10 = src.m10;
    m11 = src.m11;
    m12 = src.m12;
    m13 = src.m13;
    
    m20 = src.m20;
    m21 = src.m21;
    m22 = src.m22;
    m23 = src.m23;
    
    m30 = src.m30;
    m31 = src.m31;
    m32 = src.m32;
    m33 = src.m33;
  }
}
