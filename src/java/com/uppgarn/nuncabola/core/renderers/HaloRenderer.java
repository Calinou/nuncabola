/*
 * HaloRenderer.java
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
import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.solid.*;

import static org.lwjgl.opengl.GL11.*;

final class HaloRenderer {
  private static final Color4     GOAL_COLOR        =
     new Color4(1.0f, 1.0f, 0.0f, 0.5f);
  private static final Color4[]   TELEPORTER_COLORS = {
     new Color4(0.7f, 0.5f, 1.0f, 0.5f),
     new Color4(0.7f, 0.5f, 1.0f, 0.8f)
  };
  private static final Color4[][] SWITCH_COLORS     = {
    {new Color4(1.0f, 0.0f, 0.0f, 0.5f),
     new Color4(1.0f, 0.0f, 0.0f, 0.8f)},
    {new Color4(0.0f, 1.0f, 0.0f, 0.5f),
     new Color4(0.0f, 1.0f, 0.0f, 0.8f)}
  };
  
  private SolidKit beamKit;
  
  private SolidKit goalKit;
  private SolidKit teleKit;
  
  public HaloRenderer() {
    beamKit = createKit("beam");
    
    goalKit = createKit("goal");
    teleKit = createKit("jump");
  }
  
  private SolidKit createKit(String name) {
    String path = "geom/" + name + "/" + name + ".sol";
    
    Solid sol = RendererHome.loadSolid(path);
    
    if (sol == null) {
      return null;
    }
    
    return new SolidKit(sol);
  }
  
  private void drawBeam(State state, Vector3 p, float r, float h, Color4 c) {
    glPushMatrix();
    {
      glTranslatef(p.x, p.y, p.z);
      glScalef(r, h, r);
      glColor4f(c.r, c.g, c.b, c.a);
      
      if (beamKit != null) {
        beamKit.getRenderer().drawOpaqueAndTransparent(state, true, true);
      }
    }
    glPopMatrix();
  }
  
  public void drawGoalBeam(State state, Goal goal, float k) {
    drawBeam(state, goal.p, goal.r, Goal.HEIGHT * k, GOAL_COLOR);
  }
  
  public void drawTeleporterBeam(
      State      state,
      Teleporter tele,
      boolean    highlight) {
    Color4 c = TELEPORTER_COLORS[highlight ? 1 : 0];
    
    drawBeam(state, tele.p, tele.r, Teleporter.HEIGHT, c);
  }
  
  public void drawSwitchBeam(State state, Switch zwitch) {
    if (zwitch.base.invisible) {
      return;
    }
    
    Color4 c = SWITCH_COLORS[zwitch.enabled ? 1 : 0][zwitch.ballInside ? 1 : 0];
    
    drawBeam(state, zwitch.base.p, zwitch.base.r, SwitchBase.HEIGHT, c);
  }
  
  public void drawGoalEffects(State state, Goal goal, float k) {
    glPointSize(Gfx.getHeight() / 6.0f);
    
    glPushMatrix();
    {
      glTranslatef(goal.p.x, goal.p.y, goal.p.z);
      glScalef(goal.r, k, goal.r);
      
      if (goalKit != null) {
        goalKit.getRenderer().drawOpaqueAndTransparent(state, true, true);
      }
    }
    glPopMatrix();
  }
  
  public void drawTeleporterEffects(State state, Teleporter tele) {
    glPointSize(Gfx.getHeight() / 12.0f);
    
    glPushMatrix();
    {
      glTranslatef(tele.p.x, tele.p.y, tele.p.z);
      glScalef(tele.r, 1.0f, tele.r);
      
      if (teleKit != null) {
        teleKit.getRenderer().drawOpaqueAndTransparent(state, true, true);
      }
    }
    glPopMatrix();
  }
  
  public void step(float dt) {
    if (goalKit != null) {
      goalKit.step(dt);
    }
    if (teleKit != null) {
      teleKit.step(dt);
    }
  }
  
  public void deinitialize() {
    if (beamKit != null) {
      beamKit.deinitialize();
    }
    
    if (goalKit != null) {
      goalKit.deinitialize();
    }
    if (teleKit != null) {
      teleKit.deinitialize();
    }
  }
}
