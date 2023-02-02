/*
 * MeshCreator.java
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

package com.uppgarn.nuncabola.core.renderers;

import com.uppgarn.nuncabola.core.solid.*;

import org.lwjgl.*;

import java.nio.*;
import java.util.*;

final class MeshCreator {
  public static Mesh[][] createMeshArrays(SolidBase solBase, Asset[] assets) {
    return new MeshCreator(solBase, assets).createMeshArrays();
  }
  
  private final SolidBase solBase;
  private final Asset[]   assets;
  
  private ByteBuffer vboBuf;
  private ByteBuffer eboBuf;
  private int[]      indices;
  
  private MeshCreator(SolidBase solBase, Asset[] assets) {
    this.solBase = solBase;
    this.assets  = assets;
    
    int offsetCount  = solBase.offsets.length;
    int maxGeomCount = getMaximumGeomCount();
    
    vboBuf  = BufferUtils.createByteBuffer(offsetCount  * Mesh.VBO_TOTAL);
    eboBuf  = BufferUtils.createByteBuffer(maxGeomCount * Mesh.EBO_TOTAL);
    indices = new int[offsetCount];
  }
  
  private int getGeomCount(Body body) {
    int count = 0;
    
    // Lump geoms.
    
    for (int idx = 0; idx < body.lumpCount; idx++) {
      Lump lump = solBase.lumps[body.lump0Idx + idx];
      
      count += lump.geomCount;
    }
    
    // Body geoms.
    
    count += body.geomCount;
    
    return count;
  }
  
  private int getMaximumGeomCount() {
    int count = 0;
    
    for (Body body: solBase.bodies) {
      count = Math.max(count, getGeomCount(body));
    }
    
    return count;
  }
  
  private boolean isMaterialUsed(int geom0Idx, int geomCount, int mtrlIdx) {
    for (int idx = 0; idx < geomCount; idx++) {
      Geom geom = solBase.geoms[solBase.indices[geom0Idx + idx]];
      
      if (geom.mtrlIdx == mtrlIdx) {
        return true;
      }
    }
    
    return false;
  }
  
  private boolean isMaterialUsed(Body body, int mtrlIdx) {
    // Lump geoms.
    
    for (int idx = 0; idx < body.lumpCount; idx++) {
      Lump lump = solBase.lumps[body.lump0Idx + idx];
      
      if (isMaterialUsed(lump.geom0Idx, lump.geomCount, mtrlIdx)) {
        return true;
      }
    }
    
    // Body geoms.
    
    return isMaterialUsed(body.geom0Idx, body.geomCount, mtrlIdx);
  }
  
  private int getMaterialCount(Body body) {
    int count = 0;
    
    for (int mtrlIdx = 0; mtrlIdx < solBase.mtrls.length; mtrlIdx++) {
      if (isMaterialUsed(body, mtrlIdx)) {
        count++;
      }
    }
    
    return count;
  }
  
  private void setVertex(Offset offset) {
    Vertex vert = solBase.verts[offset.vertIdx];
    Side   side = solBase.sides[offset.sideIdx];
    Texc   texc = solBase.texcs[offset.texcIdx];
    
    vboBuf.putFloat(vert.p.x);
    vboBuf.putFloat(vert.p.y);
    vboBuf.putFloat(vert.p.z);
    
    vboBuf.putFloat(side.n.x);
    vboBuf.putFloat(side.n.y);
    vboBuf.putFloat(side.n.z);
    
    vboBuf.putFloat(texc.s);
    vboBuf.putFloat(texc.t);
  }
  
  private void setElement(int elem) {
    eboBuf.putShort((short) elem);
  }
  
  private void fillBuffers(int offsetIdx) {
    // Add vertex attributes to the VBO data if needed.
    
    if (indices[offsetIdx] == -1) {
      indices[offsetIdx] = vboBuf.position() / Mesh.VBO_ENTRY;
      
      setVertex(solBase.offsets[offsetIdx]);
    }
    
    // Add remapped offset index to the EBO data.
    
    setElement(indices[offsetIdx]);
  }
  
  private void fillBuffers(Geom geom, int mtrlIdx) {
    if (geom.mtrlIdx != mtrlIdx) {
      return;
    }
    
    fillBuffers(geom.offset0Idx);
    fillBuffers(geom.offset1Idx);
    fillBuffers(geom.offset2Idx);
  }
  
  private void fillBuffers(int geom0Idx, int geomCount, int mtrlIdx) {
    for (int idx = 0; idx < geomCount; idx++) {
      Geom geom = solBase.geoms[solBase.indices[geom0Idx + idx]];
      
      fillBuffers(geom, mtrlIdx);
    }
  }
  
  private void fillBuffers(Body body, int mtrlIdx) {
    // Lump geoms.
    
    for (int idx = 0; idx < body.lumpCount; idx++) {
      Lump lump = solBase.lumps[body.lump0Idx + idx];
      
      fillBuffers(lump.geom0Idx, lump.geomCount, mtrlIdx);
    }
    
    // Body geoms.
    
    fillBuffers(body.geom0Idx, body.geomCount, mtrlIdx);
  }
  
  private Mesh createMesh(Body body, int mtrlIdx) {
    vboBuf.clear();
    eboBuf.clear();
    
    Arrays.fill(indices, -1);
    
    fillBuffers(body, mtrlIdx);
    
    vboBuf.flip();
    eboBuf.flip();
    
    return new Mesh(assets[mtrlIdx], vboBuf, eboBuf);
  }
  
  private Mesh[] createMeshes(Body body) {
    Mesh[] meshes = new Mesh[getMaterialCount(body)];
    
    for (int mtrlIdx = 0, meshIdx = 0;
        mtrlIdx < solBase.mtrls.length;
        mtrlIdx++) {
      if (isMaterialUsed(body, mtrlIdx)) {
        meshes[meshIdx++] = createMesh(body, mtrlIdx);
      }
    }
    
    return meshes;
  }
  
  private Mesh[][] createMeshArrays() {
    Mesh[][] meshArrays = new Mesh[solBase.bodies.length][];
    
    for (int idx = 0; idx < solBase.bodies.length; idx++) {
      meshArrays[idx] = createMeshes(solBase.bodies[idx]);
    }
    
    return meshArrays;
  }
}
