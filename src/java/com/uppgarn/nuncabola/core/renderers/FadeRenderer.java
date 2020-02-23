/*
 * FadeRenderer.java
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

final class FadeRenderer {
  public void draw(State state, float k) {
    if (k <= 0.0f) {
      return;
    }
    
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();
    {
      glDisable(GL_DEPTH_TEST);
      glDisable(GL_TEXTURE_2D);
      
      glColor4f(0.0f, 0.0f, 0.0f, k);
      
      RendererHome.getBillboardRenderer().enableDrawing();
      {
        state.applyDefaults();
        
        RendererHome.getBillboardRenderer().draw(2);
      }
      RendererHome.getBillboardRenderer().disableDrawing();
      
      glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
      
      glEnable(GL_TEXTURE_2D);
      glEnable(GL_DEPTH_TEST);
    }
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
    glPopMatrix();
  }
}
