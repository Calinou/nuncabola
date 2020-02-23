/*
 * LookManager.java
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

package com.uppgarn.nuncabola.core.gui;

import org.lwjgl.*;

import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

final class LookManager {
  private static final int COUNT = 256;
  
  private static final int VBO_SIZE = COUNT * Look.VBO_TOTAL;
  private static final int EBO_SIZE = COUNT * Look.EBO_TOTAL;
  
  private ByteBuffer vboBuf;
  private ByteBuffer eboBuf;
  private int        vbo;
  private int        ebo;
  private boolean[]  used;
  
  public LookManager() {
    vboBuf = BufferUtils.createByteBuffer(VBO_SIZE);
    eboBuf = BufferUtils.createByteBuffer(EBO_SIZE / COUNT);
    vbo    = createVBO();
    ebo    = createEBO();
    used   = new boolean[COUNT];
  }
  
  private int createVBO() {
    int vbo = glGenBuffersARB();
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, vbo);
    glBufferDataARB(GL_ARRAY_BUFFER_ARB, vboBuf, GL_STATIC_DRAW_ARB);
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
    
    return vbo;
  }
  
  private int createEBO() {
    int ebo = glGenBuffersARB();
    
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, ebo);
    glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, EBO_SIZE, GL_STATIC_DRAW_ARB);
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
    
    return ebo;
  }
  
  public Look createLook() {
    for (int idx = 0; idx < COUNT; idx++) {
      if (!used[idx]) {
        return new Look(vboBuf, eboBuf, vbo, ebo, used, idx);
      }
    }
    
    throw new AssertionError();
  }
  
  public void enableDrawing(boolean colorEnabled) {
    glBindBufferARB(GL_ARRAY_BUFFER_ARB,         vbo);
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, ebo);
    
    if (colorEnabled) {
      glEnableClientState(GL_COLOR_ARRAY);
      glColorPointer     (4, GL_UNSIGNED_BYTE, Look.VBO_ENTRY, Look.VBO_OFF0);
    }
    
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    glTexCoordPointer  (2, GL_FLOAT, Look.VBO_ENTRY, Look.VBO_OFF1);
    
    glEnableClientState(GL_VERTEX_ARRAY);
    glVertexPointer    (2, GL_SHORT, Look.VBO_ENTRY, Look.VBO_OFF2);
  }
  
  public void disableDrawing() {
    glBindBufferARB(GL_ARRAY_BUFFER_ARB,         0);
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
    
    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);
  }
  
  public void deinitialize() {
    glDeleteBuffersARB(vbo);
    glDeleteBuffersARB(ebo);
  }
}
