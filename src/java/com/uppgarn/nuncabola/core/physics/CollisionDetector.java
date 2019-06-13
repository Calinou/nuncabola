/*
 * CollisionDetector.java
 *
 * Copyright (c) 2003-2019 Nuncabola authors
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

package com.uppgarn.nuncabola.core.physics;

import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.solid.*;

final class CollisionDetector {
  private static final float SMALL = 1.0E-3f;
  private static final float LARGE = 1.0E+5f;
  
  private final Solid sol;
  
  private Motion motion;
  
  public CollisionDetector(Solid sol) {
    this.sol = sol;
    
    motion = new Motion(sol);
  }
  
  /**
   * Solves (p + v * t) . (p + v * t) == r * r for smallest t.
   */
  private float solve(Vector3 p, Vector3 v, float r) {
    // Given vectors A = P, B = V * t, C = A + B, |C| = r,
    // solve for smallest t.
    //
    // Some useful dot product properties:
    //
    // 1) A . A = |A| * |A|
    // 2) A . (B + C) = A . B + A . C
    // 3) (r * A) . B = r * (A . B)
    //
    // Deriving a quadratic equation:
    //
    // C . C = r * r                                     (1)
    // (A + B) . (A + B) = r * r
    // A . (A + B) + B . (A + B) = r * r                 (2)
    // A . A + A . B + B . A + B . B = r * r             (2)
    // A . A + 2 * (A . B) + B . B = r * r
    // P . P + 2 * (P . V * t) + (V * t . V * t) = r * r
    // P . P + 2 * (P . V) * t + (V . V) * t * t = r * r (3)
    // (V . V) * t * t + 2 * (P . V) * t + P . P - r * r = 0
    //
    // This equation is solved using the quadratic formula.
    
    float a = v.dot(v);
    float b = v.dot(p) * 2.0f;
    float c = p.dot(p) - r * r;
    float d = b * b - 4.0f * a * c;
    
    // Hack: This seems to cause failures to detect low-velocity collision.
    // Yet, the potential division by zero below seems fine.
    //
    // if (Math.abs(a) < SMALL) {
    //   return LARGE;
    // }
    
    // Testing for equality against zero is acceptable.
    
    if (a == 0.0f) {
      return LARGE;
    }
    
    if (d < 0.0f) {
      return LARGE;
    } else if (d > 0.0f) {
      float dSqrt = (float) Math.sqrt(d);
      float t0    = 0.5f * (-b - dSqrt) / a;
      float t1    = 0.5f * (-b + dSqrt) / a;
      float t     = (t0 < t1) ? t0 : t1;
      
      return (t < 0.0f) ? LARGE : t;
    } else {
      return -b * 0.5f / a;
    }
  }
  
  /**
   * Computes the earliest time and position of the intersection of
   * a sphere and a vertex.
   * <p>
   * The sphere has radius {@code r} and moves along vector {@code v}
   * from point {@code p}. The vertex moves along vector {@code w}
   * from point {@code q} in a coordinate system based at {@code o}.
   */
  private float intersectVertex(
      Vector3 q_,
      Vector3 o,
      Vector3 q,
      Vector3 w,
      Vector3 p,
      Vector3 v,
      float   r) {
    Vector3 o_ = intersectVertex_o_;
    Vector3 p_ = intersectVertex_p_;
    Vector3 v_ = intersectVertex_v_;
    
    float t = LARGE;
    
    o_.add     (o, q);
    p_.subtract(p, o_);
    v_.subtract(v, w);
    
    if (p_.dot(v_) < 0.0f) {
      t = solve(p_, v_, r);
      
      if (t < LARGE) {
        q_.addScaled(o_, w, t);
      }
    }
    
    return t;
  }
  
  /**
   * Computes the earliest time and position of the intersection of
   * a sphere and an edge.
   * <p>
   * The sphere has radius {@code r} and moves along vector {@code v}
   * from point {@code p}. The edge moves along vector {@code w}
   * from point {@code q} in a coordinate system based at {@code o}.
   * The edge extends along the length of vector {@code u}.
   */
  private float intersectEdge(
      Vector3 q_,
      Vector3 o,
      Vector3 q,
      Vector3 u,
      Vector3 w,
      Vector3 p,
      Vector3 v,
      float   r) {
    Vector3 d  = intersectEdge_d;
    Vector3 e  = intersectEdge_e;
    Vector3 p_ = intersectEdge_p_;
    Vector3 v_ = intersectEdge_v_;
    
    d.subtract(p, o);
    d.subtract(d, q);
    e.subtract(v, w);
    
    // Think projections. Vectors D, extending from the edge vertex Q
    // to the sphere, and E, the relative velocity of sphere wrt the
    // edge, are made orthogonal to the edge vector U. Division of the
    // dot products is required to obtain the true projection ratios
    // since U does not have unit length.
    
    float du = d.dot(u);
    float eu = e.dot(u);
    float uu = u.dot(u);
    
    p_.addScaled(d, u, -du / uu);
    
    // First, test for intersection.
    
    if (p_.dot(p_) < r * r) {
      // The sphere already intersects the line of the edge.
      
      if ((du < 0.0f) || (du > uu)) {
        // The sphere is behind the endpoints of the edge, and
        // can't hit the edge without hitting the vertices first.
        
        return LARGE;
      }
      
      // The sphere already intersects the edge.
      
      if (p_.dot(e) >= 0.0f) {
        // Moving apart.
        
        return LARGE;
      }
      
      p_.normalize(p_);
      q_.addScaled(p, p_, -r);
      
      return 0.0f;
    }
    
    v_.addScaled(e, u, -eu / uu);
    
    float t = solve(p_, v_, r);
    float s = (du + eu * t) / uu; // projection of D + E * t on U
    
    if ((t >= 0.0f) && (t < LARGE) && (s > 0.0f) && (s < 1.0f)) {
      d .addScaled(o, w, t);
      e .addScaled(q, u, s);
      q_.add      (e, d);
    } else {
      t = LARGE;
    }
    
    return t;
  }
  
  /**
   * Computes the earliest time and position of the intersection of
   * a sphere and a plane.
   * <p>
   * The sphere has radius {@code r} and moves along vector {@code v}
   * from point {@code p}. The plane moves along vector {@code w}.
   * The plane has normal {@code n} and is positioned at distance
   * {@code d} from the origin {@code o} along that normal.
   */
  private float intersectSide(
      Vector3 q_,
      Vector3 o,
      Vector3 w,
      Vector3 n,
      float   d,
      Vector3 p,
      Vector3 v,
      float   r) {
    float vn = v.dot(n);
    float wn = w.dot(n);
    float t  = LARGE;
    
    if (vn - wn < 0.0f) {
      float on = o.dot(n);
      float pn = p.dot(n);
      
      float u = (r + d + on - pn) / (vn - wn);
      float a = (    d + on - pn) / (vn - wn);
      
      if (u >= 0.0f) {
        t = u;
        
        q_.addScaled(p,  v, +t);
        q_.addScaled(q_, n, -r);
      } else if (a >= 0.0f) {
        t = 0.0f;
        
        q_.addScaled(p,  v, +t);
        q_.addScaled(q_, n, -r);
      }
    }
    
    return t;
  }
  
  private float testVertex(
      Vector3 t_,
      Vertex  vert,
      Vector3 o,
      Vector3 w,
      Ball    ball) {
    return intersectVertex(t_, o, vert.p, w, ball.p, ball.v, ball.r);
  }
  
  private float testEdge(
      Vector3 t_,
      Edge    edge,
      Vector3 o,
      Vector3 w,
      Ball    ball) {
    Vector3 u = testEdge_u;
    
    Vertex vert0 = sol.base.verts[edge.vert0Idx];
    Vertex vert1 = sol.base.verts[edge.vert1Idx];
    
    u.subtract(vert1.p, vert0.p);
    
    return intersectEdge(t_, o, vert0.p, u, w, ball.p, ball.v, ball.r);
  }
  
  private float testSide(
      Vector3 t_,
      Side    side,
      Lump    lump,
      Vector3 o,
      Vector3 w,
      Ball    ball,
      float   dt) {
    float t = intersectSide(t_, o, w, side.n, side.d, ball.p, ball.v, ball.r);
    
    if (t < dt) {
      for (int idx = 0; idx < lump.sideCount; idx++) {
        Side side0 = sol.base.sides[sol.base.indices[lump.side0Idx + idx]];
        
        if ((side != side0)
            && (t_.dot(side0.n) -
                o .dot(side0.n) -
                w .dot(side0.n) * t > side0.d)) {
          return LARGE;
        }
      }
    }
    
    return t;
  }
  
  private float testLump(
      Vector3 t_,
      Lump    lump,
      Vector3 o,
      Vector3 w,
      Ball    ball,
      float   dt) {
    // Short circuit a non-solid lump.
    
    if ((lump.flags & Lump.DETAIL) != 0) {
      return dt;
    }
    
    Vector3 u_ = testLump_u_;
    
    u_.setZero();
    
    float t = dt;
    
    if (ball.r > 0.0f) {
      // Test all vertices.
      
      for (int idx = 0; idx < lump.vertCount; idx++) {
        Vertex vert = sol.base.verts[sol.base.indices[lump.vert0Idx + idx]];
        
        float u = testVertex(u_, vert, o, w, ball);
        
        if (u < t) {
          t_.copyFrom(u_);
          
          t = u;
        }
      }
      
      // Test all edges.
      
      for (int idx = 0; idx < lump.edgeCount; idx++) {
        Edge edge = sol.base.edges[sol.base.indices[lump.edge0Idx + idx]];
        
        float u = testEdge(u_, edge, o, w, ball);
        
        if (u < t) {
          t_.copyFrom(u_);
          
          t = u;
        }
      }
    }
    
    // Test all sides.
    
    for (int idx = 0; idx < lump.sideCount; idx++) {
      Side side = sol.base.sides[sol.base.indices[lump.side0Idx + idx]];
      
      float u = testSide(u_, side, lump, o, w, ball, t);
      
      if (u < t) {
        t_.copyFrom(u_);
        
        t = u;
      }
    }
    
    return t;
  }
  
  private boolean testFore(
      Side    side,
      Vector3 o,
      Vector3 w,
      Ball    ball,
      float   dt) {
    Vector3 q = testFore_q;
    
    // If the ball is not behind the plane, the test passes.
    
    q.subtract(ball.p, o);
    
    float d = side.d;
    
    if (q.dot(side.n) - d + ball.r >= 0.0f) {
      return true;
    }
    
    // If it's not behind the plane after DT seconds, the test passes.
    
    q.addScaled(q, ball.v, dt);
    
    d += w.dot(side.n) * dt;
    
    if (q.dot(side.n) - d + ball.r >= 0.0f) {
      return true;
    }
    
    // Else, test fails.
    
    return false;
  }
  
  private boolean testBack(
      Side    side,
      Vector3 o,
      Vector3 w,
      Ball    ball,
      float   dt) {
    Vector3 q = testBack_q;
    
    // If the ball is not in front of the plane, the test passes.
    
    q.subtract(ball.p, o);
    
    float d = side.d;
    
    if (q.dot(side.n) - d - ball.r <= 0.0f) {
      return true;
    }
    
    // If it's not in front of the plane after DT seconds, the test passes.
    
    q.addScaled(q, ball.v, dt);
    
    d += w.dot(side.n) * dt;
    
    if (q.dot(side.n) - d - ball.r <= 0.0f) {
      return true;
    }
    
    // Else, test fails.
    
    return false;
  }
  
  private float testNode(
      Vector3 t_,
      Node    node,
      Vector3 o,
      Vector3 w,
      Ball    ball,
      float   dt) {
    Vector3 u_ = testNode_u_.push();
    
    float t = dt;
    
    // Test all lumps.
    
    for (int idx = 0; idx < node.lumpCount; idx++) {
      Lump lump = sol.base.lumps[node.lump0Idx + idx];
      
      float u = testLump(u_, lump, o, w, ball, t);
      
      if (u < t) {
        t_.copyFrom(u_);
        
        t = u;
      }
    }
    
    // Test in front of this node.
    
    if ((node.node0Idx >= 0)
        && testFore(sol.base.sides[node.sideIdx], o, w, ball, t)) {
      Node node0 = sol.base.nodes[node.node0Idx];
      
      float u = testNode(u_, node0, o, w, ball, t);
      
      if (u < t) {
        t_.copyFrom(u_);
        
        t = u;
      }
    }
    
    // Test behind this node.
    
    if ((node.node1Idx >= 0)
        && testBack(sol.base.sides[node.sideIdx], o, w, ball, t)) {
      Node node1 = sol.base.nodes[node.node1Idx];
      
      float u = testNode(u_, node1, o, w, ball, t);
      
      if (u < t) {
        t_.copyFrom(u_);
        
        t = u;
      }
    }
    
    testNode_u_.pop();
    
    return t;
  }
  
  private float testBody(
      Vector3 t_,
      Vector3 v_,
      Body    body,
      Ball    ball,
      float   dt) {
    Vector3    u_ = testBody_u_;
    Vector3    o_ = testBody_o_;
    Quaternion e_ = testBody_e_;
    Vector3    w_ = testBody_w_;
    
    Node node = sol.base.nodes[body.base.nodeIdx];
    
    motion.getBodyPosition   (o_, body, 0.0f);
    motion.getBodyVelocity   (w_, body, dt);
    motion.getBodyOrientation(e_, body, 0.0f);
    
    // For rotating bodies, rather than rotate every normal and vertex
    // of the body, we temporarily pretend the ball is rotating and
    // moving about a static body.
    
    // Linear velocity of a point rotating about the origin:
    // v = w x p
    
    if ((e_.w != 1.0f) || motion.isBodyPathOriented(body)) {
      // The body has a non-identity orientation or it is rotating.
      
      Ball       ball0 = testBody_ball0;
      Quaternion e     = testBody_e;
      Vector3    p0    = testBody_p0;
      Vector3    p1    = testBody_p1;
      Vector3    z     = testBody_z;
      
      z.setZero();
      
      // First, calculate position at start and end of time interval.
      
      p0.subtract (ball.p, o_);
      p1.copyFrom (p0);
      e .conjugate(e_);
      p0.rotate   (p0, e);
      
      p1.addScaled(p1, ball.v, dt);
      p1.addScaled(p1, w_,    -dt);
      
      motion.getBodyOrientation(e, body, dt);
      
      e .conjugate(e);
      p1.rotate   (p1, e);
      
      // Set up ball instance with values relative to body.
      
      ball0  .copyFrom(ball);
      ball0.p.copyFrom(p0);
      
      // Calculate velocity from start/end positions and time.
      
      ball0.v.subtract(p1, p0);
      ball0.v.scale   (ball0.v, 1.0f / dt);
      
      float u = testNode(u_, node, z, z, ball0, dt);
      
      if (u < dt) {
        // Compute the final orientation.
        
        motion.getBodyOrientation(e, body, u);
        
        // Return world space coordinates.
        
        t_.rotate(u_, e);
        t_.add   (o_, t_);
        
        // Move forward.
        
        t_.addScaled(t_, w_, u);
        
        // Express "non-ball" velocity.
        
        v_.rotate  (ball0.v, e);
        v_.subtract(ball .v, v_);
        
        return u;
      }
    } else {
      float u = testNode(u_, node, o_, w_, ball, dt);
      
      if (u < dt) {
        t_.copyFrom(u_);
        v_.copyFrom(w_);
        
        return u;
      }
    }
    
    return dt;
  }
  
  public float testBodies(Vector3 t_, Vector3 v_, Ball ball, float dt) {
    Vector3 u_ = testBodies_u_;
    Vector3 w_ = testBodies_w_;
    
    float t = dt;
    
    for (Body body: sol.bodies) {
      float u = testBody(u_, w_, body, ball, t);
      
      if (u < t) {
        t_.copyFrom(u_);
        v_.copyFrom(w_);
        
        t = u;
      }
    }
    
    return t;
  }
  
  // Storage for reusable objects to minimize object creation.
  
  private final Vector3      intersectVertex_o_ = new Vector3();
  private final Vector3      intersectVertex_p_ = new Vector3();
  private final Vector3      intersectVertex_v_ = new Vector3();
  private final Vector3      intersectEdge_d    = new Vector3();
  private final Vector3      intersectEdge_e    = new Vector3();
  private final Vector3      intersectEdge_p_   = new Vector3();
  private final Vector3      intersectEdge_v_   = new Vector3();
  private final Vector3      testEdge_u         = new Vector3();
  private final Vector3      testLump_u_        = new Vector3();
  private final Vector3      testFore_q         = new Vector3();
  private final Vector3      testBack_q         = new Vector3();
  private final Vector3Stack testNode_u_        = new Vector3Stack(16);
  private final Vector3      testBody_u_        = new Vector3();
  private final Vector3      testBody_o_        = new Vector3();
  private final Quaternion   testBody_e_        = new Quaternion();
  private final Vector3      testBody_w_        = new Vector3();
  private final Ball         testBody_ball0     = new Ball();
  private final Quaternion   testBody_e         = new Quaternion();
  private final Vector3      testBody_p0        = new Vector3();
  private final Vector3      testBody_p1        = new Vector3();
  private final Vector3      testBody_z         = new Vector3();
  private final Vector3      testBodies_u_      = new Vector3();
  private final Vector3      testBodies_w_      = new Vector3();
}
