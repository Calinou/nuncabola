/*
 * AlphaFunctions.java
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

package com.uppgarn.nuncabola.core.renderers;

import static org.lwjgl.opengl.GL11.*;

final class AlphaFunctions {
  private static final int[] FUNCTIONS = {
    GL_ALWAYS,
    GL_EQUAL,
    GL_GEQUAL,
    GL_GREATER,
    GL_LEQUAL,
    GL_LESS,
    GL_NEVER,
    GL_NOTEQUAL
  };
  
  public static int getFunction(int idx) {
    if ((idx >= 0) && (idx < FUNCTIONS.length)) {
      return FUNCTIONS[idx];
    }
    
    return GL_ALWAYS;
  }
  
  private AlphaFunctions() {
  }
}
