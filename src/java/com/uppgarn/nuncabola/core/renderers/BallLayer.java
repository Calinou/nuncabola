/*
 * BallLayer.java
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

final class BallLayer {
  private SolidKit kit;
  
  private boolean pendulum;
  private boolean drawBack;
  private boolean drawClip;
  private boolean depthMask;
  private boolean depthTest;
  
  public BallLayer(String name) {
    kit = createKit(name);
    
    pendulum  = createFlag("pendulum",  false);
    drawBack  = createFlag("drawback",  false);
    drawClip  = createFlag("drawclip",  false);
    depthMask = createFlag("depthmask", false);
    depthTest = createFlag("depthtest", true);
  }
  
  private SolidKit createKit(String name) {
    String path = RendererHome.getBallPath() + "-" + name + ".sol";
    
    Solid sol = RendererHome.loadSolid(path);
    
    if (sol == null) {
      return null;
    }
    
    return new SolidKit(sol);
  }
  
  private boolean createFlag(String key, boolean def) {
    if (kit != null) {
      String str = kit.getSolid().base.meta.get(key);
      
      if (str != null) {
        try {
          return Integer.parseInt(str.trim()) != 0;
        } catch (NumberFormatException ex) {
          return false;
        }
      }
    }
    
    return def;
  }
  
  public SolidKit getKit() {
    return kit;
  }
  
  public boolean getPendulum() {
    return pendulum;
  }
  
  public boolean getDrawBack() {
    return drawBack;
  }
  
  public boolean getDrawClip() {
    return drawClip;
  }
  
  public boolean getDepthMask() {
    return depthMask;
  }
  
  public boolean getDepthTest() {
    return depthTest;
  }
  
  public void step(float dt) {
    if (kit != null) {
      kit.step(dt);
    }
  }
  
  public void deinitialize() {
    if (kit != null) {
      kit.deinitialize();
    }
  }
}
