/*
 * BackgroundRenderer.java
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
import com.uppgarn.nuncabola.core.solid.*;

import static com.uppgarn.nuncabola.core.renderers.RendererConstants.*;

import static org.lwjgl.opengl.GL11.*;

public final class BackgroundRenderer {
  private SolidKit kit;
  
  public BackgroundRenderer(String texturePath) {
    kit = createKit(texturePath);
  }
  
  private SolidKit createKit(String texturePath) {
    Solid sol = RendererHome.loadSolid("geom/back/back.sol");
    
    if ((sol == null) || (sol.base.mtrls.length == 0)) {
      return null;
    }
    
    Material mtrl = sol.base.mtrls[0];
    mtrl.flags |= Material.CLAMP_T;
    mtrl.path   = texturePath;
    
    return new SolidKit(sol);
  }
  
  void draw(State state) {
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);
    glDepthMask(false);
    
    glPushMatrix();
    {
      glScalef(-BACK_DIST, BACK_DIST, -BACK_DIST);
      
      if (kit != null) {
        kit.getRenderer().drawOpaqueAndTransparent(state, true, true);
      }
    }
    glPopMatrix();
    
    glDepthMask(true);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);
  }
  
  public void draw() {
    Gfx.setPerspective(RendererHome.getFOV(), 0.1f, FAR_DIST);
    
    State state = new State();
    
    state.enableDrawing();
    
    draw(state);
    
    state.disableDrawing();
  }
  
  public void deinitialize() {
    if (kit != null) {
      kit.deinitialize();
    }
  }
}
