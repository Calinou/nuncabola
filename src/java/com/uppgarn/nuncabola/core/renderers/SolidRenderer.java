/*
 * SolidRenderer.java
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
import com.uppgarn.nuncabola.core.physics.*;
import com.uppgarn.nuncabola.core.solid.*;

import static com.uppgarn.nuncabola.core.renderers.RendererConstants.*;

import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.*;

final class SolidRenderer {
  private final Solid   sol;
  private final boolean shadowedEnabled;
  
  private Motion motion;
  
  private Asset[]             assets;
  private Mesh [][]           meshArrays;
  private Map<Pass, PassInfo> passInfos;
  private boolean             reflectiveUsed;
  
  public SolidRenderer(Solid sol, boolean shadowedEnabled) {
    this.sol             = sol;
    this.shadowedEnabled = shadowedEnabled;
    
    motion = new Motion(sol);
    
    assets         = createAssets();
    meshArrays     = createMeshArrays();
    passInfos      = createPassInfos();
    reflectiveUsed = createReflectiveUsed();
  }
  
  private Asset[] createAssets() {
    Asset[] assets = new Asset[sol.base.mtrls.length];
    
    for (int idx = 0; idx < sol.base.mtrls.length; idx++) {
      assets[idx] = new Asset(sol.base.mtrls[idx], shadowedEnabled);
    }
    
    return assets;
  }
  
  private Mesh[][] createMeshArrays() {
    return MeshCreator.createMeshArrays(sol.base, assets);
  }
  
  private Map<Pass, PassInfo> createPassInfos() {
    Map<Pass, PassInfo> passInfos = new EnumMap<>(Pass.class);
    
    for (Pass pass: Pass.values()) {
      passInfos.put(pass, new PassInfo(meshArrays, pass));
    }
    
    return passInfos;
  }
  
  private boolean createReflectiveUsed() {
    for (Material mtrl: sol.base.mtrls) {
      if ((mtrl.flags & Material.REFLECTIVE) != 0) {
        return true;
      }
    }
    
    return false;
  }
  
  public boolean isReflectiveUsed() {
    return reflectiveUsed;
  }
  
  private void transform(State state, Body body) {
    Quaternion e = transform_e;
    Vector3    p = transform_p;
    Vector3    v = transform_v;
    
    // Apply the body position and rotation to the model-view matrix.
    
    motion.getBodyPosition   (p, body, 0.0f);
    motion.getBodyOrientation(e, body, 0.0f);
    
    float a = v.asAxisAngle(e);
    
    if (!((p.x == 0.0f) && (p.y == 0.0f) && (p.z == 0.0f))) {
      glTranslatef(p.x, p.y, p.z);
    }
    if (!(((v.x == 0.0f) && (v.y == 0.0f) && (v.z == 0.0f)) || (a == 0.0f))) {
      glRotatef((float) Math.toDegrees(a), v.x, v.y, v.z);
    }
    
    // Apply the shadow transform to the texture matrix.
    
    if (sol.balls.length > 0) {
      Ball ball = sol.balls[0];
      
      if (ball.r > 0.0f) {
        if (state.setTextureStage(TextureStage.SHADOW)) {
          glMatrixMode(GL_TEXTURE);
          {
            float k = 0.25f / ball.r;
            
            glLoadIdentity();
            
            // Center the shadow texture on the ball.
            
            glTranslatef(0.5f, 0.5f, 0.0f);
            
            // Transform ball XZ position to ST texture coordinate.
            
            glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            
            // Scale the shadow texture to the radius of the ball.
            
            glScalef(k, k, k);
            
            // Move the shadow texture under the ball.
            
            glTranslatef(-ball.p.x, -ball.p.y, -ball.p.z);
            
            // Apply the body position and rotation.
            
            glTranslatef(p.x, p.y, p.z);
            glRotatef((float) Math.toDegrees(a), v.x, v.y, v.z);
            
            // Vertically center clipper texture on ball position.
            
            if (state.setTextureStage(TextureStage.CLIP)) {
              glLoadIdentity();
              glTranslatef(
                p.x - ball.p.x,
                p.y - ball.p.y + 0.5f,
                p.z - ball.p.z);
              glRotatef((float) Math.toDegrees(a), v.x, v.y, v.z);
            }
          }
          glMatrixMode(GL_MODELVIEW);
          
          state.setTextureStage(TextureStage.TEXTURE);
        }
      }
    }
  }
  
  private void drawMesh(
      State    state,
      int      bodyIdx,
      int      meshIdx,
      PassInfo passInfo) {
    if (!passInfo.includesMesh(bodyIdx, meshIdx)) {
      return;
    }
    
    Mesh mesh = meshArrays[bodyIdx][meshIdx];
    
    mesh.draw(state);
  }
  
  private void drawBody(State state, int bodyIdx, PassInfo passInfo) {
    if (!passInfo.includesBody(bodyIdx)) {
      return;
    }
    
    Body body = sol.bodies[bodyIdx];
    
    glPushMatrix();
    {
      transform(state, body);
      
      for (int meshIdx = 0; meshIdx < meshArrays[bodyIdx].length; meshIdx++) {
        drawMesh(state, bodyIdx, meshIdx, passInfo);
      }
    }
    glPopMatrix();
  }
  
  private void drawBodies(State state, PassInfo passInfo) {
    for (int bodyIdx = 0; bodyIdx < sol.bodies.length; bodyIdx++) {
      drawBody(state, bodyIdx, passInfo);
    }
  }
  
  public void drawOpaqueAndTransparent(
      State   state,
      boolean depthMask,
      boolean depthTest) {
    // Render all opaque geometry, decals last.
    
    drawBodies(state, passInfos.get(Pass.OPAQUE));
    drawBodies(state, passInfos.get(Pass.OPAQUE_DECAL));
    
    // Render all transparent geometry, decals first.
    
    if (!depthTest) {
      glDisable(GL_DEPTH_TEST);
    }
    if (!depthMask) {
      glDepthMask(false);
    }
    {
      drawBodies(state, passInfos.get(Pass.TRANSPARENT_DECAL));
      drawBodies(state, passInfos.get(Pass.TRANSPARENT));
    }
    if (!depthMask) {
      glDepthMask(true);
    }
    if (!depthTest) {
      glEnable(GL_DEPTH_TEST);
    }
    
    // Revert the buffer object state.
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB,         0);
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
  }
  
  public void drawReflective(State state) {
    // Render all reflective geometry.
    
    drawBodies(state, passInfos.get(Pass.REFLECTIVE));
    
    // Revert the buffer object state.
    
    glBindBufferARB(GL_ARRAY_BUFFER_ARB,         0);
    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);
  }
  
  private void drawBackgroundBillboard(State state, Billboard bill, float t) {
    float t_ = (bill.t > 0.0f) ? t % bill.t - bill.t / 2 : 0.0f;
    
    float w = bill.w0 + bill.w1 * t_ + bill.w2 * t_ * t_;
    float h = bill.h0 + bill.h1 * t_ + bill.h2 * t_ * t_;
    
    // Render only billboards facing the viewer.
    
    if ((w > 0.0f) && (h > 0.0f)) {
      float rx = bill.rx0 + bill.rx1 * t_ + bill.rx2 * t_ * t_;
      float ry = bill.ry0 + bill.ry1 * t_ + bill.ry2 * t_ * t_;
      float rz = bill.rz0 + bill.rz1 * t_ + bill.rz2 * t_ * t_;
      
      state.apply(assets[bill.mtrlIdx]);
      
      glPushMatrix();
      {
        if (ry != 0.0f) {
          glRotatef(ry, 0.0f, 1.0f, 0.0f);
        }
        if (rx != 0.0f) {
          glRotatef(rx, 1.0f, 0.0f, 0.0f);
        }
        
        glTranslatef(0.0f, 0.0f, -bill.d);
        
        if ((bill.flags & Billboard.FLAT) != 0) {
          glRotatef(-rx - 90.0f, 1.0f, 0.0f, 0.0f);
          glRotatef(-ry,         0.0f, 0.0f, 1.0f);
        }
        if ((bill.flags & Billboard.EDGE) != 0) {
          glRotatef(-rx,         1.0f, 0.0f, 0.0f);
        }
        
        if (rz != 0.0f) {
          glRotatef(rz, 0.0f, 0.0f, 1.0f);
        }
        
        glScalef(w, h, 1.0f);
        
        int type = ((bill.flags & Billboard.EDGE) != 0) ? 0 : 1;
        
        RendererHome.getBillboardRenderer().draw(type);
      }
      glPopMatrix();
    }
  }
  
  public void drawBackgroundBillboards(State state, float t) {
    if (sol.base.bills.length == 0) {
      return;
    }
    
    glDepthMask(false);
    
    RendererHome.getBillboardRenderer().enableDrawing();
    {
      for (Billboard bill: sol.base.bills) {
        if ((bill.d >= 0.0f) && (bill.d < FAR_DIST)) {
          drawBackgroundBillboard(state, bill, t);
        }
      }
    }
    RendererHome.getBillboardRenderer().disableDrawing();
    
    glDepthMask(true);
  }
  
  private void drawBillboard(
      State     state,
      Billboard bill,
      Matrix4   m_,
      float     t) {
    float t_ = bill.t * t;
    float s_ = (float) Math.sin(t_);
    
    float w  = bill.w0  + bill.w1  * t_ + bill.w2  * s_;
    float h  = bill.h0  + bill.h1  * t_ + bill.h2  * s_;
    float rx = bill.rx0 + bill.rx1 * t_ + bill.rx2 * s_;
    float ry = bill.ry0 + bill.ry1 * t_ + bill.ry2 * s_;
    float rz = bill.rz0 + bill.rz1 * t_ + bill.rz2 * s_;
    
    state.apply(assets[bill.mtrlIdx]);
    
    glPushMatrix();
    {
      glTranslatef(bill.p.x, bill.p.y, bill.p.z);
      
      if ((m_ != null) && ((bill.flags & Billboard.NO_FACE) == 0)) {
        glMultMatrix(Gfx.buffer(m_));
      }
      
      if (rx != 0.0f) {
        glRotatef(rx, 1.0f, 0.0f, 0.0f);
      }
      if (ry != 0.0f) {
        glRotatef(ry, 0.0f, 1.0f, 0.0f);
      }
      if (rz != 0.0f) {
        glRotatef(rz, 0.0f, 0.0f, 1.0f);
      }
      
      glScalef(w, h, 1.0f);
      
      RendererHome.getBillboardRenderer().draw(1);
    }
    glPopMatrix();
  }
  
  public void drawBillboards(State state, Matrix4 m_, float t) {
    if (sol.base.bills.length == 0) {
      return;
    }
    
    RendererHome.getBillboardRenderer().enableDrawing();
    {
      for (Billboard bill: sol.base.bills) {
        drawBillboard(state, bill, m_, t);
      }
    }
    RendererHome.getBillboardRenderer().disableDrawing();
  }
  
  public void deinitialize() {
    for (Mesh[] meshes: meshArrays) {
      for (Mesh mesh: meshes) {
        mesh.deinitialize();
      }
    }
    
    for (Asset asset: assets) {
      asset.deinitialize();
    }
  }
  
  // Storage for reusable objects to minimize object creation.
  
  private final Quaternion transform_e = new Quaternion();
  private final Vector3    transform_p = new Vector3();
  private final Vector3    transform_v = new Vector3();
}
