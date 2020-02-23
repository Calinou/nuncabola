/*
 * MathWriteTool.java
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

import static com.uppgarn.nuncabola.core.binary.BinaryWriteTool.*;

import java.io.*;

public final class MathWriteTool {
  public static void writeVector3(OutputStream out, Vector3 v)
      throws IOException {
    writeFloat(out, v.x);
    writeFloat(out, v.y);
    writeFloat(out, v.z);
  }
  
  public static void writeQuaternion(OutputStream out, Quaternion q)
      throws IOException {
    writeFloat(out, q.w);
    writeFloat(out, q.x);
    writeFloat(out, q.y);
    writeFloat(out, q.z);
  }
  
  public static void writeColor4(OutputStream out, Color4 c)
      throws IOException {
    writeFloat(out, c.r);
    writeFloat(out, c.g);
    writeFloat(out, c.b);
    writeFloat(out, c.a);
  }
  
  private MathWriteTool() {
  }
}
