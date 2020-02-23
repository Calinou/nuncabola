/*
 * Solid.java
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

package com.uppgarn.nuncabola.core.solid;

public final class Solid {
  public final SolidBase base;
  
  public Path  [] paths;
  public Body  [] bodies;
  public Switch[] switches;
  public Item  [] items;
  public Ball  [] balls;
  
  public Mover[] movers;
  
  public float accum;
  
  public Solid(SolidBase base) {
    this.base = base;
    
    MoverCreator moverCreator = new MoverCreator();
    
    paths    = createPaths();
    bodies   = createBodies(moverCreator);
    switches = createSwitches();
    items    = createItems();
    balls    = createBalls();
    
    movers = moverCreator.create();
    
    accum = 0.0f;
  }
  
  public Solid(Solid src) {
    base = src.base;
    
    paths    = createPaths   (src);
    bodies   = createBodies  (src);
    switches = createSwitches(src);
    items    = createItems   (src);
    balls    = createBalls   (src);
    
    movers = createMovers(src);
    
    accum = src.accum;
  }
  
  private Path[] createPaths() {
    Path[] paths = new Path[base.pathBases.length];
    
    for (int idx = 0; idx < paths.length; idx++) {
      paths[idx] = new Path(base.pathBases[idx]);
    }
    
    return paths;
  }
  
  private Body[] createBodies(MoverCreator moverCreator) {
    Body[] bodies = new Body[base.bodyBases.length];
    
    for (int idx = 0; idx < bodies.length; idx++) {
      bodies[idx] = new Body(base.bodyBases[idx], moverCreator);
    }
    
    return bodies;
  }
  
  private Switch[] createSwitches() {
    Switch[] switches = new Switch[base.switchBases.length];
    
    for (int idx = 0; idx < switches.length; idx++) {
      switches[idx] = new Switch(base.switchBases[idx]);
    }
    
    return switches;
  }
  
  private Item[] createItems() {
    Item[] items = new Item[base.itemBases.length];
    
    for (int idx = 0; idx < items.length; idx++) {
      items[idx] = new Item(base.itemBases[idx]);
    }
    
    return items;
  }
  
  private Ball[] createBalls() {
    Ball[] balls = new Ball[base.ballBases.length];
    
    for (int idx = 0; idx < balls.length; idx++) {
      balls[idx] = new Ball(base.ballBases[idx]);
    }
    
    return balls;
  }
  
  private Path[] createPaths(Solid src) {
    Path[] paths = new Path[src.paths.length];
    
    for (int idx = 0; idx < paths.length; idx++) {
      paths[idx] = new Path(src.paths[idx]);
    }
    
    return paths;
  }
  
  private Body[] createBodies(Solid src) {
    Body[] bodies = new Body[src.bodies.length];
    
    for (int idx = 0; idx < bodies.length; idx++) {
      bodies[idx] = new Body(src.bodies[idx]);
    }
    
    return bodies;
  }
  
  private Switch[] createSwitches(Solid src) {
    Switch[] switches = new Switch[src.switches.length];
    
    for (int idx = 0; idx < switches.length; idx++) {
      switches[idx] = new Switch(src.switches[idx]);
    }
    
    return switches;
  }
  
  private Item[] createItems(Solid src) {
    Item[] items = new Item[src.items.length];
    
    for (int idx = 0; idx < items.length; idx++) {
      items[idx] = new Item(src.items[idx]);
    }
    
    return items;
  }
  
  private Ball[] createBalls(Solid src) {
    Ball[] balls = new Ball[src.balls.length];
    
    for (int idx = 0; idx < balls.length; idx++) {
      balls[idx] = new Ball(src.balls[idx]);
    }
    
    return balls;
  }
  
  private Mover[] createMovers(Solid src) {
    Mover[] movers = new Mover[src.movers.length];
    
    for (int idx = 0; idx < movers.length; idx++) {
      movers[idx] = new Mover(src.movers[idx]);
    }
    
    return movers;
  }
  
  public void copyFrom(Solid src) {
    assert src.base == base;
    
    // Paths.
    
    for (int idx = 0; idx < paths.length; idx++) {
      paths[idx].copyFrom(src.paths[idx]);
    }
    
    // Bodies.
    
    for (int idx = 0; idx < bodies.length; idx++) {
      bodies[idx].copyFrom(src.bodies[idx]);
    }
    
    // Switches.
    
    for (int idx = 0; idx < switches.length; idx++) {
      switches[idx].copyFrom(src.switches[idx]);
    }
    
    // Items.
    
    if (items.length == src.items.length) {
      for (int idx = 0; idx < items.length; idx++) {
        items[idx].copyFrom(src.items[idx]);
      }
    } else {
      items = createItems(src);
    }
    
    // Balls.
    
    if (balls.length == src.balls.length) {
      for (int idx = 0; idx < balls.length; idx++) {
        balls[idx].copyFrom(src.balls[idx]);
      }
    } else {
      balls = createBalls(src);
    }
    
    // Movers.
    
    for (int idx = 0; idx < movers.length; idx++) {
      movers[idx].copyFrom(src.movers[idx]);
    }
    
    accum = src.accum;
  }
  
  public void copyFrom(Solid src0, Solid src1, float alpha) {
    assert src0.base == base;
    assert src1.base == base;
    
    // Paths.
    
    for (int idx = 0; idx < paths.length; idx++) {
      paths[idx].copyFrom(src1.paths[idx]);
    }
    
    // Bodies.
    
    for (int idx = 0; idx < bodies.length; idx++) {
      bodies[idx].copyFrom(src1.bodies[idx]);
    }
    
    // Switches.
    
    for (int idx = 0; idx < switches.length; idx++) {
      switches[idx].copyFrom(src1.switches[idx]);
    }
    
    // Items.
    
    if (items.length == src1.items.length) {
      for (int idx = 0; idx < items.length; idx++) {
        items[idx].copyFrom(src1.items[idx]);
      }
    } else {
      items = createItems(src1);
    }
    
    // Balls.
    
    if    ((balls.length == src0.balls.length)
        && (balls.length == src1.balls.length)) {
      for (int idx = 0; idx < balls.length; idx++) {
        balls[idx].copyFrom(src0.balls[idx], src1.balls[idx], alpha);
      }
    } else {
      balls = createBalls(src1);
    }
    
    // Movers.
    
    for (int idx = 0; idx < movers.length; idx++) {
      movers[idx].copyFrom(src0.movers[idx], src1.movers[idx], alpha);
    }
    
    accum = src1.accum;
  }
}
