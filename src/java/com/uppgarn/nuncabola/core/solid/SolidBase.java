/*
 * SolidBase.java
 *
 * Copyright (c) 2003-2022 Nuncabola authors
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

public final class SolidBase {
  public Meta meta;
  
  public int[] indices;
  
  public Material  [] mtrls;
  public Vertex    [] verts;
  public Edge      [] edges;
  public Side      [] sides;
  public Texc      [] texcs;
  public Offset    [] offsets;
  public Geom      [] geoms;
  public Lump      [] lumps;
  public Node      [] nodes;
  public Body      [] bodies;
  public Goal      [] goals;
  public Teleporter[] teles;
  public Billboard [] bills;
  public Vista     [] vistas;
  
  public PathBase  [] pathBases;
  public MoverBase [] moverBases;
  public SwitchBase[] switchBases;
  public ItemBase  [] itemBases;
  public BallBase  [] ballBases;
  
  public SolidBase() {
    meta = new Meta();
    
    indices = new int[0];
    
    mtrls   = new Material  [0];
    verts   = new Vertex    [0];
    edges   = new Edge      [0];
    sides   = new Side      [0];
    texcs   = new Texc      [0];
    offsets = new Offset    [0];
    geoms   = new Geom      [0];
    lumps   = new Lump      [0];
    nodes   = new Node      [0];
    bodies  = new Body      [0];
    goals   = new Goal      [0];
    teles   = new Teleporter[0];
    bills   = new Billboard [0];
    vistas  = new Vista     [0];
    
    pathBases   = new PathBase  [0];
    moverBases  = new MoverBase [0];
    switchBases = new SwitchBase[0];
    itemBases   = new ItemBase  [0];
    ballBases   = new BallBase  [0];
  }
}
