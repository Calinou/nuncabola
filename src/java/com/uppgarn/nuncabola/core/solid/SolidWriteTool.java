/*
 * SolidWriteTool.java
 *
 * Copyright (c) 2003-2017 Nuncabola authors
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

import static com.uppgarn.nuncabola.core.binary.BinaryWriteTool.*;
import static com.uppgarn.nuncabola.core.math.MathWriteTool.*;
import static com.uppgarn.nuncabola.core.solid.SolidIOTool.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class SolidWriteTool {
  private static void writeMeta(OutputStream out, Dictionary dict)
      throws IOException {
    out.write(dict.bytes);
    
    for (int offset: dict.offsets) {
      writeInt(out, offset);
    }
  }
  
  private static void writeMaterial(OutputStream out, Material mtrl)
      throws IOException {
    writeColor4(out, mtrl.d);
    writeColor4(out, mtrl.a);
    writeColor4(out, mtrl.s);
    writeColor4(out, mtrl.e);
    writeFloat (out, mtrl.h);
    writeInt   (out, mtrl.flags);
    writeString(out, mtrl.path, 64);
    
    if ((mtrl.flags & Material.ALPHA_TEST) != 0) {
      writeInt  (out, mtrl.alphaFunc);
      writeFloat(out, mtrl.alphaRef);
    }
  }
  
  private static void writeVertex(OutputStream out, Vertex vert)
      throws IOException {
    writeVector3(out, vert.p);
  }
  
  private static void writeEdge(OutputStream out, Edge edge)
      throws IOException {
    writeInt(out, edge.vert0Idx);
    writeInt(out, edge.vert1Idx);
  }
  
  private static void writeSide(OutputStream out, Side side)
      throws IOException {
    writeVector3(out, side.n);
    writeFloat  (out, side.d);
  }
  
  private static void writeTexc(OutputStream out, Texc texc)
      throws IOException {
    writeFloat(out, texc.s);
    writeFloat(out, texc.t);
  }
  
  private static void writeOffset(OutputStream out, Offset offset)
      throws IOException {
    writeInt(out, offset.texcIdx);
    writeInt(out, offset.sideIdx);
    writeInt(out, offset.vertIdx);
  }
  
  private static void writeGeom(OutputStream out, Geom geom)
      throws IOException {
    writeInt(out, geom.mtrlIdx);
    writeInt(out, geom.offset0Idx);
    writeInt(out, geom.offset1Idx);
    writeInt(out, geom.offset2Idx);
  }
  
  private static void writeLump(OutputStream out, Lump lump)
      throws IOException {
    writeInt(out, lump.flags);
    writeInt(out, lump.vert0Idx);
    writeInt(out, lump.vertCount);
    writeInt(out, lump.edge0Idx);
    writeInt(out, lump.edgeCount);
    writeInt(out, lump.geom0Idx);
    writeInt(out, lump.geomCount);
    writeInt(out, lump.side0Idx);
    writeInt(out, lump.sideCount);
  }
  
  private static void writeNode(OutputStream out, Node node)
      throws IOException {
    writeInt(out, node.sideIdx);
    writeInt(out, node.node0Idx);
    writeInt(out, node.node1Idx);
    writeInt(out, node.lump0Idx);
    writeInt(out, node.lumpCount);
  }
  
  private static void writePathBase(OutputStream out, PathBase pathBase)
      throws IOException {
    writeVector3(out, pathBase.p);
    writeFloat  (out, pathBase.t);
    writeInt    (out, pathBase.pathIdx);
    writeInt    (out, pathBase.enabled ? 1 : 0);
    writeInt    (out, pathBase.smooth  ? 1 : 0);
    writeInt    (out, pathBase.flags);
    
    if ((pathBase.flags & PathBase.ORIENTED) != 0) {
      writeQuaternion(out, pathBase.e);
    }
  }
  
  private static void writeBodyBase(OutputStream out, BodyBase bodyBase)
      throws IOException {
    writeInt(out, bodyBase.path0Idx);
    writeInt(out, (bodyBase.path1Idx == bodyBase.path0Idx)
                  ? -1 : bodyBase.path1Idx);
    writeInt(out, bodyBase.nodeIdx);
    writeInt(out, bodyBase.lump0Idx);
    writeInt(out, bodyBase.lumpCount);
    writeInt(out, bodyBase.geom0Idx);
    writeInt(out, bodyBase.geomCount);
  }
  
  private static void writeItemBase(OutputStream out, ItemBase itemBase)
      throws IOException {
    writeVector3(out, itemBase.p);
    writeInt    (out, itemBase.type);
    writeInt    (out, itemBase.value);
  }
  
  private static void writeGoal(OutputStream out, Goal goal)
      throws IOException {
    writeVector3(out, goal.p);
    writeFloat  (out, goal.r);
  }
  
  private static void writeTeleporter(OutputStream out, Teleporter tele)
      throws IOException {
    writeVector3(out, tele.p);
    writeVector3(out, tele.q);
    writeFloat  (out, tele.r);
  }
  
  private static void writeSwitchBase(OutputStream out, SwitchBase switchBase)
      throws IOException {
    writeVector3(out, switchBase.p);
    writeFloat  (out, switchBase.r);
    writeInt    (out, switchBase.pathIdx);
    writeFloat  (out, switchBase.t);
    writeFloat  (out, switchBase.t);
    writeInt    (out, switchBase.enabled   ? 1 : 0);
    writeInt    (out, switchBase.enabled   ? 1 : 0);
    writeInt    (out, switchBase.invisible ? 1 : 0);
  }
  
  private static void writeBillboard(OutputStream out, Billboard bill)
      throws IOException {
    writeInt    (out, bill.flags);
    writeInt    (out, bill.mtrlIdx);
    writeFloat  (out, bill.t);
    writeFloat  (out, bill.d);
    writeFloat  (out, bill.w0);
    writeFloat  (out, bill.w1);
    writeFloat  (out, bill.w2);
    writeFloat  (out, bill.h0);
    writeFloat  (out, bill.h1);
    writeFloat  (out, bill.h2);
    writeFloat  (out, bill.rx0);
    writeFloat  (out, bill.rx1);
    writeFloat  (out, bill.rx2);
    writeFloat  (out, bill.ry0);
    writeFloat  (out, bill.ry1);
    writeFloat  (out, bill.ry2);
    writeFloat  (out, bill.rz0);
    writeFloat  (out, bill.rz1);
    writeFloat  (out, bill.rz2);
    writeVector3(out, bill.p);
  }
  
  private static void writeBallBase(OutputStream out, BallBase ballBase)
      throws IOException {
    writeVector3(out, ballBase.p);
    writeFloat  (out, ballBase.r);
  }
  
  private static void writeVista(OutputStream out, Vista vista)
      throws IOException {
    writeVector3(out, vista.p);
    writeVector3(out, vista.q);
  }
  
  public static void writeSolidBase(OutputStream out, SolidBase solBase)
      throws IOException {
    writeInt(out, MAGIC);
    writeInt(out, VERSION_CURRENT);
    
    // Dictionary.
    
    Dictionary dict = Dictionary.create(solBase.meta);
    
    // Element counts.
    
    writeInt(out, dict.bytes  .length);
    writeInt(out, dict.offsets.length / 2);
    
    writeInt(out, solBase.mtrls      .length);
    writeInt(out, solBase.verts      .length);
    writeInt(out, solBase.edges      .length);
    writeInt(out, solBase.sides      .length);
    writeInt(out, solBase.texcs      .length);
    writeInt(out, solBase.offsets    .length);
    writeInt(out, solBase.geoms      .length);
    writeInt(out, solBase.lumps      .length);
    writeInt(out, solBase.nodes      .length);
    writeInt(out, solBase.pathBases  .length);
    writeInt(out, solBase.bodyBases  .length);
    writeInt(out, solBase.itemBases  .length);
    writeInt(out, solBase.goals      .length);
    writeInt(out, solBase.teles      .length);
    writeInt(out, solBase.switchBases.length);
    writeInt(out, solBase.bills      .length);
    writeInt(out, solBase.ballBases  .length);
    writeInt(out, solBase.vistas     .length);
    
    writeInt(out, solBase.indices.length);
    
    // Meta.
    
    writeMeta(out, dict);
    
    // Materials.
    
    for (Material mtrl: solBase.mtrls) {
      writeMaterial(out, mtrl);
    }
    
    // Vertices.
    
    for (Vertex vert: solBase.verts) {
      writeVertex(out, vert);
    }
    
    // Edges.
    
    for (Edge edge: solBase.edges) {
      writeEdge(out, edge);
    }
    
    // Sides.
    
    for (Side side: solBase.sides) {
      writeSide(out, side);
    }
    
    // Texcs.
    
    for (Texc texc: solBase.texcs) {
      writeTexc(out, texc);
    }
    
    // Offsets.
    
    for (Offset offset: solBase.offsets) {
      writeOffset(out, offset);
    }
    
    // Geoms.
    
    for (Geom geom: solBase.geoms) {
      writeGeom(out, geom);
    }
    
    // Lumps.
    
    for (Lump lump: solBase.lumps) {
      writeLump(out, lump);
    }
    
    // Nodes.
    
    for (Node node: solBase.nodes) {
      writeNode(out, node);
    }
    
    // Path bases.
    
    for (PathBase pathBase: solBase.pathBases) {
      writePathBase(out, pathBase);
    }
    
    // Body bases.
    
    for (BodyBase bodyBase: solBase.bodyBases) {
      writeBodyBase(out, bodyBase);
    }
    
    // Item bases.
    
    for (ItemBase itemBase: solBase.itemBases) {
      writeItemBase(out, itemBase);
    }
    
    // Goals.
    
    for (Goal goal: solBase.goals) {
      writeGoal(out, goal);
    }
    
    // Teleporters.
    
    for (Teleporter tele: solBase.teles) {
      writeTeleporter(out, tele);
    }
    
    // Switch bases.
    
    for (SwitchBase switchBase: solBase.switchBases) {
      writeSwitchBase(out, switchBase);
    }
    
    // Billboards.
    
    for (Billboard bill: solBase.bills) {
      writeBillboard(out, bill);
    }
    
    // Ball bases.
    
    for (BallBase ballBase: solBase.ballBases) {
      writeBallBase(out, ballBase);
    }
    
    // Vistas.
    
    for (Vista vista: solBase.vistas) {
      writeVista(out, vista);
    }
    
    // Indices.
    
    for (int index: solBase.indices) {
      writeInt(out, index);
    }
  }
  
  public static void writeSolidBase(java.nio.file.Path file, SolidBase solBase)
      throws IOException {
    try (OutputStream out = new BufferedOutputStream(
        Files.newOutputStream(file))) {
      writeSolidBase(out, solBase);
      
      out.flush();
    }
  }
  
  public static void writeSolid(OutputStream out, Solid sol)
      throws IOException {
    writeSolidBase(out, sol.base);
  }
  
  public static void writeSolid(java.nio.file.Path file, Solid sol)
      throws IOException {
    writeSolidBase(file, sol.base);
  }
  
  private SolidWriteTool() {
  }
  
  private static final class Dictionary {
    public static Dictionary create(Meta meta) {
      ByteArrayOutputStream buf     = new ByteArrayOutputStream();
      int[]                 offsets = new int[meta.size() * 2];
      
      int idx    = 0;
      int offset = 0;
      
      for (Map.Entry<String, String> entry: meta.entrySet()) {
        for (int it = 0; it < 2; it++) {
          String str      = (it == 0) ? entry.getKey() : entry.getValue();
          byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
          
          offsets[idx++] = offset;
          
          buf.write(strBytes, 0, strBytes.length);
          buf.write(0);
          
          offset += strBytes.length + 1;
        }
      }
      
      return new Dictionary(buf.toByteArray(), offsets);
    }
    
    public final byte[] bytes;
    public final int [] offsets;
    
    private Dictionary(byte[] bytes, int[] offsets) {
      this.bytes   = bytes;
      this.offsets = offsets;
    }
  }
}
