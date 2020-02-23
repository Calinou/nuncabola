/*
 * ShadowRenderer.java
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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

/**
 * A note about lighting and shadow: technically speaking, it's wrong.
 * The light position and shadow projection behave as if the light-source
 * rotates with the floor. However, the skybox does not rotate, thus the
 * light should also remain stationary.
 * <p>
 * The correct behavior would eliminate a significant 3D cue: the shadow
 * of the ball indicates the ball's position relative to the floor even
 * when the ball is in the air. This was the motivating idea behind the
 * shadow in the first place, so correct shadow projection would only
 * magnify the problem.
 */
final class ShadowRenderer {
  private int shadowTexture;
  private int clipTexture;
  
  public ShadowRenderer() {
    shadowTexture = createShadowTexture();
    clipTexture   = createClipTexture();
  }
  
  private int createShadowTexture() {
    return Gfx.createTexture(RendererHome.loadImage("png/shadow.png"), true);
  }
  
  private int createClipTexture() {
    int texture = glGenTextures();
    
    glBindTexture(GL_TEXTURE_2D, texture);
    
    glTexImage2D(
      GL_TEXTURE_2D,
      0,
      GL_LUMINANCE_ALPHA,
      1,
      2,
      0,
      GL_LUMINANCE_ALPHA,
      GL_UNSIGNED_BYTE,
      Gfx.buffer((byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00));
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    
    return texture;
  }
  
  public void drawSet(State state) {
    if (state.setTextureStage(TextureStage.SHADOW)) {
      glEnable(GL_TEXTURE_2D);
      glEnableClientState(GL_TEXTURE_COORD_ARRAY);
      
      glBindTexture(GL_TEXTURE_2D, shadowTexture);
      
      if (state.setTextureStage(TextureStage.CLIP)) {
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        glBindTexture(GL_TEXTURE_2D, clipTexture);
      }
      
      state.setTextureStage(TextureStage.TEXTURE);
    }
  }
  
  public void drawClear(State state) {
    if (state.setTextureStage(TextureStage.SHADOW)) {
      glBindTexture(GL_TEXTURE_2D, 0);
      
      glDisable(GL_TEXTURE_2D);
      glDisableClientState(GL_TEXTURE_COORD_ARRAY);
      
      if (state.setTextureStage(TextureStage.CLIP)) {
        glBindTexture(GL_TEXTURE_2D, 0);
        
        glDisable(GL_TEXTURE_2D);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
      }
      
      state.setTextureStage(TextureStage.TEXTURE);
    }
  }
  
  public void deinitialize() {
    glDeleteTextures(shadowTexture);
    glDeleteTextures(clipTexture);
  }
}
