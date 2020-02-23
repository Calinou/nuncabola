/*
 * GameRenderer.java
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
import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.solid.*;

import static com.uppgarn.nuncabola.core.renderers.RendererConstants.*;

import static org.lwjgl.opengl.GL11.*;

public final class GameRenderer {
  private static final TextureEnv[] GEOMETRY_AND_SHADOW_ENVS = {
    TextureEnv.SHADOW_CLIP,
    TextureEnv.SHADOW,
    TextureEnv.DEFAULT
  };
  private static final TextureEnv[] GEOMETRY_ONLY_ENVS       = {
    TextureEnv.DEFAULT
  };
  private static final TextureEnv[] SHADOW_ONLY_ENVS         = {
    TextureEnv.POSE,
    TextureEnv.DEFAULT
  };
  
  private final Game game;
  
  private final Level level;
  private final Solid sol;
  
  private SolidRenderer      solRend;
  private BackgroundRenderer bgRend;
  private SolidKit           bgSolKit;
  
  public GameRenderer(Game game) {
    this.game = game;
    
    level = game.base.level;
    sol   = game.sol;
    
    solRend  = createSolidRenderer();
    bgRend   = createBackgroundRenderer();
    bgSolKit = createBackgroundSolidKit();
  }
  
  private SolidRenderer createSolidRenderer() {
    return new SolidRenderer(sol, true);
  }
  
  private BackgroundRenderer createBackgroundRenderer() {
    return new BackgroundRenderer(level.getBackgroundGradientPath());
  }
  
  private SolidKit createBackgroundSolidKit() {
    if (RendererHome.getBackground()) {
      String solidPath = level.getBackgroundSolidPath();
      
      if (!solidPath.isEmpty()) {
        Solid bgSol = RendererHome.loadSolid(solidPath);
        
        if (bgSol != null) {
          return new SolidKit(bgSol);
        }
      }
    }
    
    return null;
  }
  
  private TextureEnv[] getTextureEnvs(Pose pose, boolean shadowEnabled) {
    if (RendererHome.getShadow() && shadowEnabled) {
      switch (pose) {
        case NONE: {
          return GEOMETRY_AND_SHADOW_ENVS;
        }
        case STATIC: {
          return GEOMETRY_ONLY_ENVS;
        }
        case BALL: {
          return SHADOW_ONLY_ENVS;
        }
        
        default: {
          throw new AssertionError();
        }
      }
    } else {
      return GEOMETRY_ONLY_ENVS;
    }
  }
  
  private float getFOV() {
    float k = game.teleInProgress ? 2.0f * Math.abs(game.teleT - 0.5f) : 1.0f;
    
    return RendererHome.getFOV() * k;
  }
  
  private void drawBackground(State state, int d, float t) {
    glPushMatrix();
    {
      if (d < 0) {
        Tilt tilt = game.tilt;
        
        glRotatef(tilt.rz * 2, tilt.z.x, tilt.z.y, tilt.z.z);
        glRotatef(tilt.rx * 2, tilt.x.x, tilt.x.y, tilt.x.z);
      }
      
      View view = game.view;
      
      glTranslatef(view.p.x, view.p.y * d, view.p.z);
      
      bgRend.draw(state);
      
      if (RendererHome.getBackground() && (bgSolKit != null)) {
        bgSolKit.getRenderer().drawBackgroundBillboards(state, t);
      }
    }
    glPopMatrix();
  }
  
  private void drawTilt(int d) {
    // Rotate the environment about the position of the ball.
    
    Ball ball = sol.balls[0];
    Tilt tilt = game.tilt;
    
    glTranslatef(+ball.p.x, +ball.p.y * d, +ball.p.z);
    glRotatef(-tilt.rz * d, tilt.z.x, tilt.z.y, tilt.z.z);
    glRotatef(-tilt.rx * d, tilt.x.x, tilt.x.y, tilt.x.z);
    glTranslatef(-ball.p.x, -ball.p.y * d, -ball.p.z);
  }
  
  private void drawReflection(State state) {
    glPushMatrix();
    {
      drawTilt(1);
      
      // Draw the floor.
      
      solRend.drawReflective(state);
    }
    glPopMatrix();
  }
  
  private void drawLight(float t) {
    // Configure the lighting.
    
    glLight(GL_LIGHT0, GL_POSITION, Gfx.buffer(-8.0f, +32.0f, -8.0f, 0.0f));
    glLight(GL_LIGHT0, GL_DIFFUSE,  Gfx.buffer(1.0f, 0.8f, 0.8f, 1.0f));
    glLight(GL_LIGHT0, GL_SPECULAR, Gfx.buffer(1.0f, 0.8f, 0.8f, 1.0f));
    glLight(GL_LIGHT0, GL_AMBIENT,  Gfx.buffer(0.8f, 0.8f, 0.8f, 1.0f));
    
    glLight(GL_LIGHT1, GL_POSITION, Gfx.buffer(+8.0f, +32.0f, +8.0f, 0.0f));
    glLight(GL_LIGHT1, GL_DIFFUSE,  Gfx.buffer(0.8f, 1.0f, 0.8f, 1.0f));
    glLight(GL_LIGHT1, GL_SPECULAR, Gfx.buffer(0.8f, 1.0f, 0.8f, 1.0f));
    glLight(GL_LIGHT1, GL_AMBIENT,  Gfx.buffer(0.8f, 0.8f, 0.8f, 1.0f));
    
    float x = (float) Math.cos(t);
    float z = (float) Math.sin(t);
    
    glLight(GL_LIGHT2, GL_POSITION, Gfx.buffer(x, 0.0f, z, 0.0f));
    glLight(GL_LIGHT2, GL_DIFFUSE,  Gfx.buffer(1.0f, 1.0f, 1.0f, 1.0f));
    glLight(GL_LIGHT2, GL_SPECULAR, Gfx.buffer(1.0f, 1.0f, 1.0f, 1.0f));
    
    // Enable scene lights.
    
    glEnable(GL_LIGHT0);
    glEnable(GL_LIGHT1);
  }
  
  private void clipReflection() {
    // Fudge to eliminate the floor from reflection.
    
    glClipPlane(GL_CLIP_PLANE0, Gfx.buffer(0.0, 1.0, 0.0, -0.00001));
  }
  
  private void clipBall(int d) {
    // Compute the plane giving the front of the ball, as seen from view.p.
    
    Ball ball = sol.balls[0];
    View view = game.view;
    
    float c0 = ball.p.x;
    float c1 = ball.p.y * d;
    float c2 = ball.p.z;
    
    float pz0 = view.p.x - c0;
    float pz1 = view.p.y - c1;
    float pz2 = view.p.z - c2;
    float pz3;
    
    float r = (float) Math.sqrt(pz0 * pz0 + pz1 * pz1 + pz2 * pz2);
    
    pz0 /= r;
    pz1 /= r;
    pz2 /= r;
    pz3  = -(pz0 * c0 + pz1 * c1 + pz2 * c2);
    
    // Find the plane giving the back of the ball, as seen from view.p.
    
    float nz0 = -pz0;
    float nz1 = -pz1;
    float nz2 = -pz2;
    float nz3 = -pz3;
    
    // Reflect these planes as necessary, and store them in the GL state.
    
    pz1 *= d;
    nz1 *= d;
    
    glClipPlane(GL_CLIP_PLANE1, Gfx.buffer((double) nz0, nz1, nz2, nz3));
    glClipPlane(GL_CLIP_PLANE2, Gfx.buffer((double) pz0, pz1, pz2, pz3));
  }
  
  private void drawItems(State state, Matrix4 billM_, float t) {
    for (Item item: sol.items) {
      RendererHome.getItemRenderer().draw(state, item, billM_, t);
    }
  }
  
  private void drawGoalBeams(State state) {
    if (!game.goalsUnlocked) {
      return;
    }
    
    for (Goal goal: sol.base.goals) {
      RendererHome.getHaloRenderer().drawGoalBeam(state, goal, game.goalFactor);
    }
  }
  
  private void drawTeleporterBeams(State state) {
    for (Teleporter tele: sol.base.teles) {
      RendererHome.getHaloRenderer().drawTeleporterBeam(
        state,
        tele,
        !game.teleEnabled);
    }
  }
  
  private void drawSwitchBeams(State state) {
    for (Switch zwitch: sol.switches) {
      RendererHome.getHaloRenderer().drawSwitchBeam(state, zwitch);
    }
  }
  
  private void drawGoalEffects(State state) {
    if (!game.goalsUnlocked) {
      return;
    }
    
    for (Goal goal: sol.base.goals) {
      RendererHome.getHaloRenderer().drawGoalEffects(
        state,
        goal,
        game.goalFactor);
    }
  }
  
  private void drawTeleporterEffects(State state) {
    for (Teleporter tele: sol.base.teles) {
      RendererHome.getHaloRenderer().drawTeleporterEffects(state, tele);
    }
  }
  
  private void drawForeground(
      State   state,
      Matrix4 m_,
      int     d,
      float   t,
      Pose    pose) {
    glPushMatrix();
    {
      // Rotate the environment about the position of the ball.
      
      drawTilt(d);
      
      // Compute clipping planes for reflection and ball facing.
      
      clipReflection();
      clipBall(d);
      
      if (d < 0) {
        glEnable(GL_CLIP_PLANE0);
      }
      
      // Draw the items.
      
      if ((pose != Pose.STATIC) && (pose != Pose.BALL)) {
        drawItems(state, m_, t);
      }
      
      // Draw the floor.
      
      if (pose != Pose.BALL) {
        solRend.drawOpaqueAndTransparent(state, false, true);
      } else if (state.getTextureEnv() == TextureEnv.POSE) {
        // We need the check above because otherwise the
        // active texture env is set up in a way that makes
        // level geometry visible, and we don't want that.
        
        glDepthMask(false);
        solRend.drawOpaqueAndTransparent(state, false, true);
        glDepthMask(true);
      }
      
      // Draw the ball.
      
      if (pose != Pose.STATIC) {
        RendererHome.getBallRenderer().draw(state, sol.balls[0], m_, t);
      }
      
      if (pose != Pose.BALL) {
        glDepthMask(false);
        {
          // Draw the billboards, entity beams, and item particles.
          
          solRend.drawBillboards(state, m_, t);
          
          drawGoalBeams      (state);
          drawTeleporterBeams(state);
          drawSwitchBeams    (state);
          
          RendererHome.getParticleRenderer().draw(state, game.parts, m_, t);
          
          glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
          
          // Draw the entity particles using only the sparkle light.
          
          glDisable(GL_LIGHT0);
          glDisable(GL_LIGHT1);
          glEnable (GL_LIGHT2);
          {
            drawGoalEffects      (state);
            drawTeleporterEffects(state);
          }
          glDisable(GL_LIGHT2);
          glEnable (GL_LIGHT1);
          glEnable (GL_LIGHT0);
        }
        glDepthMask(true);
      }
      
      if (d < 0) {
        glDisable(GL_CLIP_PLANE0);
      }
    }
    glPopMatrix();
  }
  
  public void draw(float t, Pose pose, float fade) {
    if (sol.balls.length == 0) {
      return;
    }
    
    State state = new State();
    
    state.setTextureEnv(getTextureEnvs(pose, true));
    state.enableDrawing();
    
    Gfx.setPerspective(getFOV(), 0.1f, FAR_DIST);
    
    glPushMatrix();
    {
      Matrix4 t_ = draw_t_;
      Matrix4 u_ = draw_u_;
      Matrix4 m_ = draw_m_;
      Vector3 v  = draw_v;
      
      View view = game.view;
      
      // Compute direct and reflected view bases.
      
      v.x = +view.p.x;
      v.y = -view.p.y;
      v.z = +view.p.z;
      
      t_.view(view.c, view.p, view.e.y);
      u_.view(view.c, v,      view.e.y);
      
      m_.transpose(t_);
      
      // Apply the current view.
      
      v.subtract(view.c, view.p);
      
      glTranslatef(0.0f, 0.0f, -v.length());
      glMultMatrix(Gfx.buffer(m_));
      glTranslatef(-view.c.x, -view.c.y, -view.c.z);
      
      if (pose != Pose.BALL) {
        // Draw the background.
        
        drawBackground(state, +1, t);
        
        // Draw the reflection.
        
        if (Gfx.getReflection() && solRend.isReflectiveUsed()) {
          glEnable(GL_STENCIL_TEST);
          {
            // Draw the mirrors only into the stencil buffer.
            
            glStencilFunc(GL_ALWAYS, 1, 0xFFFFFFFF);
            glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
            glColorMask(false, false, false, false);
            glDepthMask(false);
            
            drawReflection(state);
            
            glDepthMask(true);
            glColorMask(true, true, true, true);
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
            glStencilFunc(GL_EQUAL, 1, 0xFFFFFFFF);
            
            // Draw the scene reflected into color and depth buffers.
            
            glFrontFace(GL_CW);
            glPushMatrix();
            {
              glScalef(+1.0f, -1.0f, +1.0f);
              
              drawLight(t);
              
              drawBackground(state,     -1, t);
              drawForeground(state, u_, -1, t, pose);
            }
            glPopMatrix();
            glFrontFace(GL_CCW);
            
            glStencilFunc(GL_ALWAYS, 0, 0xFFFFFFFF);
          }
          glDisable(GL_STENCIL_TEST);
        }
      }
      
      // Ready the lights for foreground rendering.
      
      drawLight(t);
      
      if (pose != Pose.BALL) {
        // When reflection is disabled, mirrors must be rendered
        // opaque to prevent the background from showing.
        
        if (!Gfx.getReflection() && solRend.isReflectiveUsed()) {
          state.setColorMaterialEnabled(true);
          {
            glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            
            drawReflection(state);
          }
          state.setColorMaterialEnabled(false);
        }
        
        // Draw the mirrors.
        
        drawReflection(state);
      }
      
      // Draw the rest of the foreground.
      
      drawForeground(state, t_, +1, t, pose);
    }
    glPopMatrix();
    
    // Draw the fade overlay.
    
    RendererHome.getFadeRenderer().draw(state, fade);
    
    state.disableDrawing();
    state.setTextureEnv(getTextureEnvs(pose, false));
  }
  
  public void deinitialize() {
    solRend.deinitialize();
    bgRend .deinitialize();
    
    if (bgSolKit != null) {
      bgSolKit.deinitialize();
    }
  }
  
  // Storage for reusable objects to minimize object creation.
  
  private final Matrix4 draw_t_ = new Matrix4();
  private final Matrix4 draw_u_ = new Matrix4();
  private final Matrix4 draw_m_ = new Matrix4();
  private final Vector3 draw_v  = new Vector3();
}
