/*
 * ParticleRenderer.java
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

import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.graphics.*;
import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.solid.*;

import static org.lwjgl.opengl.GL11.*;

final class ParticleRenderer {
  private Asset asset;
  
  public ParticleRenderer() {
    asset = createAsset();
  }
  
  private Asset createAsset() {
    Material mtrl = new Material();
    
    mtrl.flags = Material.TRANSPARENT;
    mtrl.path  = "png/part.png";
    
    return new Asset(mtrl, false);
  }
  
  private void draw(Particle part, Matrix4 m_, float t) {
    if ((part.t <= 0.0f) || (part.style == null)) {
      return;
    }
    
    Color4 c = draw_c;
    
    RendererHome.getItemRenderer().getColor(c, part.style);
    
    glColor4f(c.r, c.g, c.b, part.t);
    
    glPushMatrix();
    {
      final float size = 0.1f;
      
      glTranslatef(part.p.x, part.p.y, part.p.z);
      glMultMatrix(Gfx.buffer(m_));
      glRotatef(part.w * t, 0.0f, 0.0f, 1.0f);
      glScalef(size, size, 1.0f);
      
      RendererHome.getBillboardRenderer().draw(2);
    }
    glPopMatrix();
  }
  
  public void draw(State state, Particles parts, Matrix4 m_, float t) {
    RendererHome.getBillboardRenderer().enableDrawing();
    {
      state.apply(asset);
      
      for (Particle part: parts.array) {
        draw(part, m_, t);
      }
    }
    RendererHome.getBillboardRenderer().disableDrawing();
  }
  
  public void deinitialize() {
    asset.deinitialize();
  }
  
  // Storage for reusable objects to minimize object creation.
  
  private final Color4 draw_c = new Color4();
}
