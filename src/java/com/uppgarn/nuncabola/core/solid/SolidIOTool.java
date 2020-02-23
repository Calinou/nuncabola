/*
 * SolidIOTool.java
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

final class SolidIOTool {
  public static final int MAGIC = 0x4C4F53AF;
  
  public static final int VERSION_1_5 = 6;
  public static final int VERSION_1_6 = 7;
  public static final int VERSION_DEV = 8;
  
  public static final int VERSION_MINIMUM = VERSION_1_5;
  public static final int VERSION_CURRENT = VERSION_DEV;
  
  private SolidIOTool() {
  }
}
