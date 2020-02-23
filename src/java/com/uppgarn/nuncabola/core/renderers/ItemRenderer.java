/*
 * ItemRenderer.java
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
import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.solid.*;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

final class ItemRenderer {
  private Map<ItemStyle, SolidKit> kits;
  
  public ItemRenderer() {
    kits = createKits();
  }
  
  private SolidKit createKit(String name) {
    String path = "item/" + name + ".sol";
    
    Solid sol = RendererHome.loadSolid(path);
    
    if (sol == null) {
      return null;
    }
    
    return new SolidKit(sol);
  }
  
  private Map<ItemStyle, SolidKit> createKits() {
    Map<ItemStyle, SolidKit> kits = new EnumMap<>(ItemStyle.class);
    
    kits.put(ItemStyle.COIN1,  createKit("coin/coin"));
    kits.put(ItemStyle.COIN5,  createKit("coin/coin5"));
    kits.put(ItemStyle.COIN10, createKit("coin/coin10"));
    kits.put(ItemStyle.GROW,   createKit("grow/grow"));
    kits.put(ItemStyle.SHRINK, createKit("shrink/shrink"));
    
    return kits;
  }
  
  public void getColor(Color4 c, ItemStyle style) {
    SolidKit kit = kits.get(style);
    
    if (kit != null) {
      Solid sol = kit.getSolid();
      
      if (sol.base.mtrls.length > 0) {
        c.copyFrom(sol.base.mtrls[0].d);
        
        return;
      }
    }
    
    c.set(1.0f, 1.0f, 1.0f, 1.0f);
  }
  
  public void draw(State state, Item item, Matrix4 billM_, float t) {
    ItemStyle style = ItemStyle.getStyle(item);
    
    if (style == null) {
      return;
    }
    
    SolidKit kit = kits.get(style);
    
    glPushMatrix();
    {
      glTranslatef(item.p.x, item.p.y, item.p.z);
      glScalef(ItemBase.RADIUS, ItemBase.RADIUS, ItemBase.RADIUS);
      
      glDepthMask(false);
      {
        if (kit != null) {
          kit.getRenderer().drawBillboards(state, billM_, t);
        }
      }
      glDepthMask(true);
      
      if (kit != null) {
        kit.getRenderer().drawOpaqueAndTransparent(state, false, true);
      }
    }
    glPopMatrix();
  }
  
  public void step(float dt) {
    for (SolidKit kit: kits.values()) {
      if (kit != null) {
        kit.step(dt);
      }
    }
  }
  
  public void deinitialize() {
    for (SolidKit kit: kits.values()) {
      if (kit != null) {
        kit.deinitialize();
      }
    }
  }
}
