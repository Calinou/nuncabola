/*
 * SolidReadTool.java
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

import com.uppgarn.nuncabola.core.util.*;

import static com.uppgarn.nuncabola.core.binary.BinaryReadTool.*;
import static com.uppgarn.nuncabola.core.math.MathReadTool.*;
import static com.uppgarn.nuncabola.core.solid.SolidIOTool.*;

import com.uppgarn.codelibf.io.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class SolidReadTool {
  private static String getString(byte[] bytes, int off) throws IOException {
    if ((off < 0) || (off > bytes.length)) {
      throw new InvalidDataException();
    }
    
    int len = 0;
    
    for (int idx = off; (idx < bytes.length) && (bytes[idx] != 0); idx++) {
      len++;
    }
    
    return new String(bytes, off, len, StandardCharsets.UTF_8);
  }
  
  private static Meta readMeta(InputStream in, int byteCount, int entryCount)
      throws IOException {
    Meta meta = new Meta(entryCount);
    
    if (byteCount > 0) {
      byte[] bytes = InputStreamTool.readBytes(in, byteCount);
      
      for (int idx = 0; idx < entryCount; idx++) {
        String key   = getString(bytes, readInt(in));
        String value = getString(bytes, readInt(in));
        
        meta.put(key, value);
      }
    }
    
    return meta;
  }
  
  private static Material readMaterial(InputStream in, int version)
      throws IOException {
    Material mtrl = new Material();
    
    readColor4(mtrl.d, in);
    readColor4(mtrl.a, in);
    readColor4(mtrl.s, in);
    readColor4(mtrl.e, in);
    
    mtrl.h     = readFloat (in);
    mtrl.flags = readInt   (in);
    mtrl.path  = readString(in, 64);
    
    if (version >= VERSION_1_6) {
      if ((mtrl.flags & Material.ALPHA_TEST) != 0) {
        mtrl.alphaFunc = readInt  (in);
        mtrl.alphaRef  = readFloat(in);
      }
    } else {
      if (mtrl.flags == 0) {
        // Invisible material.
        
        mtrl.flags = Material.TRANSPARENT;
        mtrl.d.a   = 0.0f;
      } else {
        // Visible material.
        
        final int opaque      = 1 << 0;
        final int transparent = 1 << 1;
        final int reflective  = 1 << 2;
        final int environment = 1 << 3;
        final int additive    = 1 << 4;
        final int clamped     = 1 << 5;
        final int decal       = 1 << 6;
        final int twoSided    = 1 << 7;
        
        int flags = 0;
        
        if ((mtrl.flags & opaque)      != 0) {
          flags |= Material.SHADOWED;
        }
        if ((mtrl.flags & transparent) != 0) {
          flags |= Material.TRANSPARENT;
        }
        if ((mtrl.flags & reflective)  != 0) {
          flags |= Material.REFLECTIVE | Material.SHADOWED;
        }
        if ((mtrl.flags & environment) != 0) {
          flags |= Material.ENVIRONMENT;
        }
        if ((mtrl.flags & additive)    != 0) {
          flags |= Material.ADDITIVE;
        }
        if ((mtrl.flags & clamped)     != 0) {
          flags |= Material.CLAMP_S | Material.CLAMP_T;
        }
        if ((mtrl.flags & decal)       != 0) {
          flags |= Material.DECAL | Material.SHADOWED;
        }
        if ((mtrl.flags & twoSided)    != 0) {
          flags |= Material.TWO_SIDED;
        }
        
        mtrl.flags = flags;
      }
    }
    
    return mtrl;
  }
  
  private static Vertex readVertex(InputStream in) throws IOException {
    Vertex vert = new Vertex();
    
    readVector3(vert.p, in);
    
    return vert;
  }
  
  private static Edge readEdge(InputStream in) throws IOException {
    Edge edge = new Edge();
    
    edge.vert0Idx = readInt(in);
    edge.vert1Idx = readInt(in);
    
    return edge;
  }
  
  private static Side readSide(InputStream in) throws IOException {
    Side side = new Side();
    
    readVector3(side.n, in);
    
    side.d = readFloat(in);
    
    return side;
  }
  
  private static Texc readTexc(InputStream in) throws IOException {
    Texc texc = new Texc();
    
    texc.s = readFloat(in);
    texc.t = readFloat(in);
    
    return texc;
  }
  
  private static Offset readOffset(InputStream in) throws IOException {
    Offset offset = new Offset();
    
    offset.texcIdx = readInt(in);
    offset.sideIdx = readInt(in);
    offset.vertIdx = readInt(in);
    
    return offset;
  }
  
  private static Geom readGeom(
      SolidBase   solBase,
      int[]       currOffsetCount,
      InputStream in,
      int         version) throws IOException {
    Geom geom = new Geom();
    
    geom.mtrlIdx = readInt(in);
    
    if (version >= VERSION_1_6) {
      geom.offset0Idx = readInt(in);
      geom.offset1Idx = readInt(in);
      geom.offset2Idx = readInt(in);
    } else {
      if (solBase.mtrls[geom.mtrlIdx].d.a == 0.0f) {
        geom.mtrlIdx = -1;
      }
      
      geom.offset0Idx = currOffsetCount[0]++;
      geom.offset1Idx = currOffsetCount[0]++;
      geom.offset2Idx = currOffsetCount[0]++;
      
      solBase.offsets[geom.offset0Idx] = readOffset(in);
      solBase.offsets[geom.offset1Idx] = readOffset(in);
      solBase.offsets[geom.offset2Idx] = readOffset(in);
    }
    
    return geom;
  }
  
  private static Lump readLump(InputStream in) throws IOException {
    Lump lump = new Lump();
    
    lump.flags     = readInt(in);
    lump.vert0Idx  = readInt(in);
    lump.vertCount = readInt(in);
    lump.edge0Idx  = readInt(in);
    lump.edgeCount = readInt(in);
    lump.geom0Idx  = readInt(in);
    lump.geomCount = readInt(in);
    lump.side0Idx  = readInt(in);
    lump.sideCount = readInt(in);
    
    return lump;
  }
  
  private static Node readNode(InputStream in) throws IOException {
    Node node = new Node();
    
    node.sideIdx   = readInt(in);
    node.node0Idx  = readInt(in);
    node.node1Idx  = readInt(in);
    node.lump0Idx  = readInt(in);
    node.lumpCount = readInt(in);
    
    return node;
  }
  
  private static PathBase readPathBase(InputStream in, int version)
      throws IOException {
    PathBase pathBase = new PathBase();
    
    readVector3(pathBase.p, in);
    
    pathBase.tm      = Util.secondsToMilliseconds(readFloat(in));
    pathBase.t       = Util.millisecondsToSeconds(pathBase.tm);
    pathBase.pathIdx = readInt(in);
    pathBase.enabled = readInt(in) != 0;
    pathBase.smooth  = readInt(in) != 0;
    
    if (version >= VERSION_1_6) {
      pathBase.flags = readInt(in);
      
      if ((pathBase.flags & PathBase.ORIENTED) != 0) {
        readQuaternion(pathBase.e, in);
      }
    }
    
    return pathBase;
  }
  
  private static Body readBody(
      InputStream      in,
      int              version,
      MoverBaseCreator moverBaseCreator) throws IOException {
    Body body = new Body();
    
    int path0Idx = readInt(in);
    int path1Idx = (version >= VERSION_1_6) ? readInt(in) : -1;
    
    body.mover0Idx = (path0Idx >= 0)
                     ? moverBaseCreator.add(path0Idx) : -1;
    body.mover1Idx = ((path1Idx >= 0) && (path1Idx != path0Idx))
                     ? moverBaseCreator.add(path1Idx) : body.mover0Idx;
    
    body.nodeIdx   = readInt(in);
    body.lump0Idx  = readInt(in);
    body.lumpCount = readInt(in);
    body.geom0Idx  = readInt(in);
    body.geomCount = readInt(in);
    
    return body;
  }
  
  private static ItemBase readItemBase(InputStream in) throws IOException {
    ItemBase itemBase = new ItemBase();
    
    readVector3(itemBase.p, in);
    
    itemBase.type  = readInt(in);
    itemBase.value = readInt(in);
    
    return itemBase;
  }
  
  private static Goal readGoal(InputStream in) throws IOException {
    Goal goal = new Goal();
    
    readVector3(goal.p, in);
    
    goal.r = readFloat(in);
    
    return goal;
  }
  
  private static Teleporter readTeleporter(InputStream in) throws IOException {
    Teleporter tele = new Teleporter();
    
    readVector3(tele.p, in);
    readVector3(tele.q, in);
    
    tele.r = readFloat(in);
    
    return tele;
  }
  
  private static SwitchBase readSwitchBase(InputStream in) throws IOException {
    SwitchBase switchBase = new SwitchBase();
    
    readVector3(switchBase.p, in);
    
    switchBase.r         = readFloat(in);
    switchBase.pathIdx   = readInt  (in);
    switchBase.tm        = Util.secondsToMilliseconds(readFloat(in));
    switchBase.t         = Util.millisecondsToSeconds(switchBase.tm);
    
    readFloat(in);
    
    switchBase.enabled   = readInt(in) != 0;
    
    readInt  (in);
    
    switchBase.invisible = readInt(in) != 0;
    
    return switchBase;
  }
  
  private static Billboard readBillboard(InputStream in) throws IOException {
    Billboard bill = new Billboard();
    
    bill.flags   = readInt  (in);
    bill.mtrlIdx = readInt  (in);
    bill.t       = readFloat(in);
    bill.d       = readFloat(in);
    bill.w0      = readFloat(in);
    bill.w1      = readFloat(in);
    bill.w2      = readFloat(in);
    bill.h0      = readFloat(in);
    bill.h1      = readFloat(in);
    bill.h2      = readFloat(in);
    bill.rx0     = readFloat(in);
    bill.rx1     = readFloat(in);
    bill.rx2     = readFloat(in);
    bill.ry0     = readFloat(in);
    bill.ry1     = readFloat(in);
    bill.ry2     = readFloat(in);
    bill.rz0     = readFloat(in);
    bill.rz1     = readFloat(in);
    bill.rz2     = readFloat(in);
    
    readVector3(bill.p, in);
    
    return bill;
  }
  
  private static BallBase readBallBase(InputStream in) throws IOException {
    BallBase ballBase = new BallBase();
    
    readVector3(ballBase.p, in);
    
    ballBase.r = readFloat(in);
    
    return ballBase;
  }
  
  private static Vista readVista(InputStream in) throws IOException {
    Vista vista = new Vista();
    
    readVector3(vista.p, in);
    readVector3(vista.q, in);
    
    return vista;
  }
  
  public static Meta readSolidMeta(InputStream in) throws IOException {
    int magic   = readInt(in);
    int version = readInt(in);
    
    if ((magic != MAGIC)
        || (version < VERSION_MINIMUM)
        || (version > VERSION_CURRENT)) {
      throw new InvalidDataException();
    }
    
    // Element counts.
    
    int dictByteCount  = readInt(in);
    int dictEntryCount = readInt(in);
    
    // Skip irrelevant data.
    
    InputStreamTool.skip(in, (version >= VERSION_1_6) ? 76 : 72);
    
    // Meta.
    
    return readMeta(in, dictByteCount, dictEntryCount);
  }
  
  public static Meta readSolidMeta(java.nio.file.Path file) throws IOException {
    try (InputStream in = new BufferedInputStream(
        Files.newInputStream(file),
        768)) {
      return readSolidMeta(in);
    }
  }
  
  public static Meta readSolidMeta(Source src) throws IOException {
    try (InputStream in = new BufferedInputStream(src.newInputStream(), 768)) {
      return readSolidMeta(in);
    }
  }
  
  public static SolidBase readSolidBase(InputStream in) throws IOException {
    int magic   = readInt(in);
    int version = readInt(in);
    
    if ((magic != MAGIC)
        || (version < VERSION_MINIMUM)
        || (version > VERSION_CURRENT)) {
      throw new InvalidDataException();
    }
    
    // Solid base.
    
    SolidBase solBase = new SolidBase();
    
    // Element counts.
    
    int dictByteCount  = readInt(in);
    int dictEntryCount = readInt(in);
    
    int mtrlCount   = readInt(in);
    int vertCount   = readInt(in);
    int edgeCount   = readInt(in);
    int sideCount   = readInt(in);
    int texcCount   = readInt(in);
    int offsetCount = (version >= VERSION_1_6) ? readInt(in) : 0;
    int geomCount   = readInt(in);
    int lumpCount   = readInt(in);
    int nodeCount   = readInt(in);
    int pathCount   = readInt(in);
    int bodyCount   = readInt(in);
    int itemCount   = readInt(in);
    int goalCount   = readInt(in);
    int teleCount   = readInt(in);
    int switchCount = readInt(in);
    int billCount   = readInt(in);
    int ballCount   = readInt(in);
    int vistaCount  = readInt(in);
    
    int indexCount = readInt(in);
    
    if (version < VERSION_1_6) {
      offsetCount = geomCount * 3;
    }
    
    // Meta.
    
    solBase.meta = readMeta(in, dictByteCount, dictEntryCount);
    
    // Create arrays.
    
    solBase.indices = new int[indexCount];
    
    solBase.mtrls   = new Material  [mtrlCount];
    solBase.verts   = new Vertex    [vertCount];
    solBase.edges   = new Edge      [edgeCount];
    solBase.sides   = new Side      [sideCount];
    solBase.texcs   = new Texc      [texcCount];
    solBase.offsets = new Offset    [offsetCount];
    solBase.geoms   = new Geom      [geomCount];
    solBase.lumps   = new Lump      [lumpCount];
    solBase.nodes   = new Node      [nodeCount];
    solBase.bodies  = new Body      [bodyCount];
    solBase.goals   = new Goal      [goalCount];
    solBase.teles   = new Teleporter[teleCount];
    solBase.bills   = new Billboard [billCount];
    solBase.vistas  = new Vista     [vistaCount];
    
    solBase.pathBases   = new PathBase  [pathCount];
    solBase.switchBases = new SwitchBase[switchCount];
    solBase.itemBases   = new ItemBase  [itemCount];
    solBase.ballBases   = new BallBase  [ballCount];
    
    MoverBaseCreator moverBaseCreator = new MoverBaseCreator();
    
    // Materials.
    
    for (int idx = 0; idx < mtrlCount; idx++) {
      solBase.mtrls[idx] = readMaterial(in, version);
    }
    
    // Vertices.
    
    for (int idx = 0; idx < vertCount; idx++) {
      solBase.verts[idx] = readVertex(in);
    }
    
    // Edges.
    
    for (int idx = 0; idx < edgeCount; idx++) {
      solBase.edges[idx] = readEdge(in);
    }
    
    // Sides.
    
    for (int idx = 0; idx < sideCount; idx++) {
      solBase.sides[idx] = readSide(in);
    }
    
    // Texcs.
    
    for (int idx = 0; idx < texcCount; idx++) {
      solBase.texcs[idx] = readTexc(in);
    }
    
    // Offsets.
    
    if (version >= VERSION_1_6) {
      for (int idx = 0; idx < offsetCount; idx++) {
        solBase.offsets[idx] = readOffset(in);
      }
    }
    
    // Geoms.
    
    int[] currOffsetCount = new int[1];
    
    for (int idx = 0; idx < geomCount; idx++) {
      solBase.geoms[idx] = readGeom(solBase, currOffsetCount, in, version);
    }
    
    // Lumps.
    
    for (int idx = 0; idx < lumpCount; idx++) {
      solBase.lumps[idx] = readLump(in);
    }
    
    // Nodes.
    
    for (int idx = 0; idx < nodeCount; idx++) {
      solBase.nodes[idx] = readNode(in);
    }
    
    // Path bases.
    
    for (int idx = 0; idx < pathCount; idx++) {
      solBase.pathBases[idx] = readPathBase(in, version);
    }
    
    // Bodies.
    
    for (int idx = 0; idx < bodyCount; idx++) {
      solBase.bodies[idx] = readBody(in, version, moverBaseCreator);
    }
    
    // Mover bases.
    
    solBase.moverBases = moverBaseCreator.create();
    
    // Item bases.
    
    for (int idx = 0; idx < itemCount; idx++) {
      solBase.itemBases[idx] = readItemBase(in);
    }
    
    // Goals.
    
    for (int idx = 0; idx < goalCount; idx++) {
      solBase.goals[idx] = readGoal(in);
    }
    
    // Teleporters.
    
    for (int idx = 0; idx < teleCount; idx++) {
      solBase.teles[idx] = readTeleporter(in);
    }
    
    // Switch bases.
    
    for (int idx = 0; idx < switchCount; idx++) {
      solBase.switchBases[idx] = readSwitchBase(in);
    }
    
    // Billboards.
    
    for (int idx = 0; idx < billCount; idx++) {
      solBase.bills[idx] = readBillboard(in);
    }
    
    // Ball bases.
    
    for (int idx = 0; idx < ballCount; idx++) {
      solBase.ballBases[idx] = readBallBase(in);
    }
    
    // Vistas.
    
    for (int idx = 0; idx < vistaCount; idx++) {
      solBase.vistas[idx] = readVista(in);
    }
    
    // Indices.
    
    for (int idx = 0; idx < indexCount; idx++) {
      solBase.indices[idx] = readInt(in);
    }
    
    // Add lit flag to old materials.
    
    if (version <= VERSION_1_6) {
      for (Material mtrl: solBase.mtrls) {
        mtrl.flags |= Material.LIT;
      }
      for (Billboard bill: solBase.bills) {
        solBase.mtrls[bill.mtrlIdx].flags &= ~Material.LIT;
      }
    }
    
    return solBase;
  }
  
  public static SolidBase readSolidBase(java.nio.file.Path file)
      throws IOException {
    try (InputStream in = new BufferedInputStream(Files.newInputStream(file))) {
      return readSolidBase(in);
    }
  }
  
  public static SolidBase readSolidBase(Source src) throws IOException {
    try (InputStream in = new BufferedInputStream(src.newInputStream())) {
      return readSolidBase(in);
    }
  }
  
  public static Solid readSolid(InputStream in) throws IOException {
    return new Solid(readSolidBase(in));
  }
  
  public static Solid readSolid(java.nio.file.Path file) throws IOException {
    return new Solid(readSolidBase(file));
  }
  
  public static Solid readSolid(Source src) throws IOException {
    return new Solid(readSolidBase(src));
  }
  
  private SolidReadTool() {
  }
  
  private static final class MoverBaseCreator {
    private List<MoverBase> moverBases;
    
    public MoverBaseCreator() {
      moverBases = new ArrayList<>();
    }
    
    public int add(int pathIdx) {
      MoverBase moverBase = new MoverBase();
      moverBase.pathIdx = pathIdx;
      
      moverBases.add(moverBase);
      
      return moverBases.size() - 1;
    }
    
    public MoverBase[] create() {
      return moverBases.toArray(new MoverBase[moverBases.size()]);
    }
  }
}
