/*
 * BallRenderer.java
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

final class BallRenderer {
  private BallLayer solidLayer;
  private BallLayer innerLayer;
  private BallLayer outerLayer;
  
  public BallRenderer() {
    solidLayer = new BallLayer("solid");
    innerLayer = new BallLayer("inner");
    outerLayer = new BallLayer("outer");
  }
  
  private void drawSolid(
      State   state,
      Matrix4 ballM_,
      Matrix4 ballBillM_,
      float   t) {
    SolidKit kit = solidLayer.getKit();
    
    if (kit == null) {
      return;
    }
    
    glPushMatrix();
    {
      // Apply the ball rotation.
      
      glMultMatrix(Gfx.buffer(ballM_));
      
      // Draw the solid billboard geometry.
      
      if (kit.getSolid().base.bills.length != 0) {
        if (!solidLayer.getDepthTest()) {
          glDisable(GL_DEPTH_TEST);
        }
        if (!solidLayer.getDepthMask()) {
          glDepthMask(false);
        }
        {
          kit.getRenderer().drawBillboards(state, ballBillM_, t);
        }
        if (!solidLayer.getDepthMask()) {
          glDepthMask(true);
        }
        if (!solidLayer.getDepthTest()) {
          glEnable(GL_DEPTH_TEST);
        }
      }
      
      // Draw the solid opaque and transparent geometry.
      
      kit.getRenderer().drawOpaqueAndTransparent(
        state,
        solidLayer.getDepthMask(),
        solidLayer.getDepthTest());
    }
    glPopMatrix();
  }
  
  private void drawNonSolid(
      State     state,
      BallLayer layer,
      Matrix4   pendM_,
      Matrix4   billM_,
      Matrix4   pendBillM_,
      float     t) {
    SolidKit kit = layer.getKit();
    
    if (kit == null) {
      return;
    }
    
    // Apply the pendulum rotation.
    
    if (layer.getPendulum()) {
      glPushMatrix();
      glMultMatrix(Gfx.buffer(pendM_));
    }
    
    // Draw the inner/outer opaque and transparent geometry.
    
    kit.getRenderer().drawOpaqueAndTransparent(
      state,
      layer.getDepthMask(),
      layer.getDepthTest());
    
    // Draw the inner/outer billboard geometry.
    
    if (kit.getSolid().base.bills.length != 0) {
      if (!layer.getDepthTest()) {
        glDisable(GL_DEPTH_TEST);
      }
      if (!layer.getDepthMask()) {
        glDepthMask(false);
      }
      {
        Matrix4 m = layer.getPendulum() ? pendBillM_ : billM_;
        
        kit.getRenderer().drawBillboards(state, m, t);
      }
      if (!layer.getDepthMask()) {
        glDepthMask(true);
      }
      if (!layer.getDepthTest()) {
        glEnable(GL_DEPTH_TEST);
      }
    }
    
    if (layer.getPendulum()) {
      glPopMatrix();
    }
  }
  
  private void drawInner(
      State   state,
      Matrix4 pendM_,
      Matrix4 billM_,
      Matrix4 pendBillM_,
      float   t) {
    drawNonSolid(state, innerLayer, pendM_, billM_, pendBillM_, t);
  }
  
  private void drawOuter(
      State   state,
      Matrix4 pendM_,
      Matrix4 billM_,
      Matrix4 pendBillM_,
      float   t) {
    drawNonSolid(state, outerLayer, pendM_, billM_, pendBillM_, t);
  }
  
  private void passInner(
      State   state,
      Matrix4 pendM_,
      Matrix4 billM_,
      Matrix4 pendBillM_,
      float   t) {
    if (innerLayer.getDrawClip()) {
      // Sort the inner ball using clip planes.
      
      glEnable(GL_CLIP_PLANE1);
      drawInner(state, pendM_, billM_, pendBillM_, t);
      glDisable(GL_CLIP_PLANE1);
      
      glEnable(GL_CLIP_PLANE2);
      drawInner(state, pendM_, billM_, pendBillM_, t);
      glDisable(GL_CLIP_PLANE2);
    } else if (innerLayer.getDrawBack()) {
      // Sort the inner ball using face culling.
      
      glCullFace(GL_FRONT);
      drawInner(state, pendM_, billM_, pendBillM_, t);
      glCullFace(GL_BACK);
      drawInner(state, pendM_, billM_, pendBillM_, t);
    } else {
      // Draw the inner ball normally.
      
      drawInner(state, pendM_, billM_, pendBillM_, t);
    }
  }
  
  private void passSolid(
      State   state,
      Matrix4 ballM_,
      Matrix4 pendM_,
      Matrix4 billM_,
      Matrix4 ballBillM_,
      Matrix4 pendBillM_,
      float   t) {
    if (solidLayer.getDrawClip()) {
      // Sort the solid ball with the inner ball using clip planes.
      
      glEnable(GL_CLIP_PLANE1);
      drawSolid(state, ballM_,                 ballBillM_,             t);
      glDisable(GL_CLIP_PLANE1);
      
      passInner(state,         pendM_, billM_,             pendBillM_, t);
      
      glEnable(GL_CLIP_PLANE2);
      drawSolid(state, ballM_,                 ballBillM_,             t);
      glDisable(GL_CLIP_PLANE2);
    } else if (solidLayer.getDrawBack()) {
      // Sort the solid ball with the inner ball using face culling.
      
      glCullFace(GL_FRONT);
      drawSolid(state, ballM_,                 ballBillM_,             t);
      glCullFace(GL_BACK);
      
      passInner(state,         pendM_, billM_,             pendBillM_, t);
      drawSolid(state, ballM_,                 ballBillM_,             t);
    } else {
      // Draw the solid ball after the inner ball.
      
      passInner(state,         pendM_, billM_,             pendBillM_, t);
      drawSolid(state, ballM_,                 ballBillM_,             t);
    }
  }
  
  private void passOuter(
      State   state,
      Matrix4 ballM_,
      Matrix4 pendM_,
      Matrix4 billM_,
      Matrix4 ballBillM_,
      Matrix4 pendBillM_,
      float   t) {
    if (outerLayer.getDrawClip()) {
      // Sort the outer ball with the solid ball using clip planes.
      
      glEnable(GL_CLIP_PLANE1);
      drawOuter(state,         pendM_, billM_,             pendBillM_, t);
      glDisable(GL_CLIP_PLANE1);
      
      passSolid(state, ballM_, pendM_, billM_, ballBillM_, pendBillM_, t);
      
      glEnable(GL_CLIP_PLANE2);
      drawOuter(state,         pendM_, billM_,             pendBillM_, t);
      glDisable(GL_CLIP_PLANE2);
    } else if (outerLayer.getDrawBack()) {
      // Sort the outer ball with the solid ball using face culling.
      
      glCullFace(GL_FRONT);
      drawOuter(state,         pendM_, billM_,             pendBillM_, t);
      glCullFace(GL_BACK);
      
      passSolid(state, ballM_, pendM_, billM_, ballBillM_, pendBillM_, t);
      drawOuter(state,         pendM_, billM_,             pendBillM_, t);
    } else {
      // Draw the outer ball after the solid ball.
      
      passSolid(state, ballM_, pendM_, billM_, ballBillM_, pendBillM_, t);
      drawOuter(state,         pendM_, billM_,             pendBillM_, t);
    }
  }
  
  public void draw(State state, Ball ball, Matrix4 billM_, float t) {
    Matrix4 ballM_     = draw_ballM_;
    Matrix4 pendM_     = draw_pendM_;
    Matrix4 ballT_     = draw_ballT_;
    Matrix4 ballBillM_ = draw_ballBillM_;
    Matrix4 pendT_     = draw_pendT_;
    Matrix4 pendBillM_ = draw_pendBillM_;
    
    ballM_.basis(ball.e .x, ball.e .y, ball.e .z);
    pendM_.basis(ball.e_.x, ball.e_.y, ball.e_.z);
    
    // Compute transforms for ball and pendulum billboards.
    
    ballT_.transpose(ballM_);
    pendT_.transpose(pendM_);
    
    ballBillM_.multiply(ballT_, billM_);
    pendBillM_.multiply(pendT_, billM_);
    
    glPushMatrix();
    {
      final float fudge = 0.001f;
      
      glTranslatef(ball.p.x, ball.p.y + fudge, ball.p.z);
      glScalef(ball.r, ball.r, ball.r);
      
      glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
      
      // Go to GREAT pains to ensure all layers are drawn back-to-front.
      
      passOuter(state, ballM_, pendM_, billM_, ballBillM_, pendBillM_, t);
    }
    glPopMatrix();
  }
  
  public void step(float dt) {
    solidLayer.step(dt);
    innerLayer.step(dt);
    outerLayer.step(dt);
  }
  
  public void deinitialize() {
    solidLayer.deinitialize();
    innerLayer.deinitialize();
    outerLayer.deinitialize();
  }
  
  // Storage for reusable objects to minimize object creation.
  
  private final Matrix4 draw_ballM_     = new Matrix4();
  private final Matrix4 draw_pendM_     = new Matrix4();
  private final Matrix4 draw_ballT_     = new Matrix4();
  private final Matrix4 draw_ballBillM_ = new Matrix4();
  private final Matrix4 draw_pendT_     = new Matrix4();
  private final Matrix4 draw_pendBillM_ = new Matrix4();
}
