/*
 * BillboardRenderer.java
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

import com.uppgarn.nuncabola.core.graphics.*;

import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;

final class BillboardRenderer {
  private static final int VBO_ENTRY = 16;
  private static final int VBO_OFF0  = 0;
  private static final int VBO_OFF1  = 8;
  private static final int VBO_BLOCK = 4;
  
  private static final float[] DATA = {
    0.0f, 0.0f, -0.5f,  0.0f,
    1.0f, 0.0f,  0.5f,  0.0f,
    0.0f, 1.0f, -0.5f,  1.0f,
    1.0f, 1.0f,  0.5f,  1.0f,
    
    0.0f, 0.0f, -0.5f, -0.5f,
    1.0f, 0.0f,  0.5f, -0.5f,
    0.0f, 1.0f, -0.5f,  0.5f,
    1.0f, 1.0f,  0.5f,  0.5f,
    
    0.0f, 0.0f, -1.0f, -1.0f,
    1.0f, 0.0f,  1.0f, -1.0f,
    0.0f, 1.0f, -1.0f,  1.0f,
    1.0f, 1.0f,  1.0f,  1.0f
  };
  
  private int vbo;
  
  public BillboardRenderer() {
    vbo = createVBO();
  }
  
  private int createVBO() {
    int vbo = glGenBuffersARB();
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, vbo);
    glBufferDataARB(GL_ARRAY_BUFFER_ARB, Gfx.buffer(DATA), GL_STATIC_DRAW_ARB);
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
    
    return vbo;
  }
  
  public void enableDrawing() {
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, vbo);
    
    glDisableClientState(GL_NORMAL_ARRAY);
    
    glTexCoordPointer(2, GL_FLOAT, VBO_ENTRY, VBO_OFF0);
    glVertexPointer  (2, GL_FLOAT, VBO_ENTRY, VBO_OFF1);
  }
  
  public void draw(int type) {
    glDrawArrays(GL_TRIANGLE_STRIP, type * VBO_BLOCK, VBO_BLOCK);
  }
  
  public void disableDrawing() {
    glEnableClientState(GL_NORMAL_ARRAY);
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
  }
  
  public void deinitialize() {
    glDeleteBuffersARB(vbo);
  }
}
