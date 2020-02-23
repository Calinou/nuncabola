/*
 * MathReadTool.java
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

import static com.uppgarn.nuncabola.core.binary.BinaryReadTool.*;

import java.io.*;

public final class MathReadTool {
  public static void readVector3(Vector3 v, InputStream in) throws IOException {
    v.x = readFloat(in);
    v.y = readFloat(in);
    v.z = readFloat(in);
  }
  
  public static void readQuaternion(Quaternion q, InputStream in)
      throws IOException {
    q.w = readFloat(in);
    q.x = readFloat(in);
    q.y = readFloat(in);
    q.z = readFloat(in);
  }
  
  public static void readColor4(Color4 c, InputStream in) throws IOException {
    c.r = readFloat(in);
    c.g = readFloat(in);
    c.b = readFloat(in);
    c.a = readFloat(in);
  }
  
  private MathReadTool() {
  }
}
