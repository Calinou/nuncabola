/*
 * Mesh.java
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

import com.uppgarn.nuncabola.core.solid.*;

import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

final class Mesh {
  public static final int VBO_ENTRY = 32;
  public static final int VBO_OFF0  = 0;
  public static final int VBO_OFF1  = 12;
  public static final int VBO_OFF2  = 24;
  public static final int VBO_BLOCK = 1;
  public static final int VBO_TOTAL = VBO_BLOCK * VBO_ENTRY;
  public static final int EBO_ENTRY = 2;
  public static final int EBO_BLOCK = 3;
  public static final int EBO_TOTAL = EBO_BLOCK * EBO_ENTRY;
  
  private final Asset asset;
  
  private int vbo;
  private int vertCount;
  private int ebo;
  private int elemCount;
  
  public Mesh(Asset asset, ByteBuffer vboBuf, ByteBuffer eboBuf) {
    this.asset = asset;
    
    vbo       = createVBO(vboBuf);
    vertCount = vboBuf.limit() / VBO_ENTRY;
    ebo       = createEBO(eboBuf);
    elemCount = eboBuf.limit() / EBO_ENTRY;
  }
  
  private int createVBO(ByteBuffer vboBuf) {
    int vbo = glGenBuffersARB();
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, vbo);
    glBufferDataARB(GL_ARRAY_BUFFER_ARB, vboBuf, GL_STATIC_DRAW_ARB);
    glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
    
    return vbo;
  }
  
  private int createEBO(ByteBuffer eboBuf) {
    int ebo = glGenBuffersARB();
    
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, ebo);
    glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, eboBuf, GL_STATIC_DRAW_ARB);
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
    
    return ebo;
  }
  
  public Asset getAsset() {
    return asset;
  }
  
  public void draw(State state) {
    state.apply(asset);
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB,         vbo);
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, ebo);
    
    glVertexPointer(3, GL_FLOAT, VBO_ENTRY, VBO_OFF0);
    glNormalPointer(   GL_FLOAT, VBO_ENTRY, VBO_OFF1);
    
    if (state.setTextureStage(TextureStage.SHADOW)) {
      glTexCoordPointer(3, GL_FLOAT, VBO_ENTRY, VBO_OFF0);
      
      if (state.setTextureStage(TextureStage.CLIP)) {
        glTexCoordPointer(3, GL_FLOAT, VBO_ENTRY, VBO_OFF0);
      }
      
      state.setTextureStage(TextureStage.TEXTURE);
    }
    
    glTexCoordPointer(2, GL_FLOAT, VBO_ENTRY, VBO_OFF2);
    
    if ((state.getFlags() & Material.PARTICLE) != 0) {
      glDrawArrays(GL_POINTS, 0, vertCount);
    } else {
      glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, 0);
    }
  }
  
  public void deinitialize() {
    glDeleteBuffersARB(vbo);
    glDeleteBuffersARB(ebo);
  }
}
