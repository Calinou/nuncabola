/*
 * PassInfo.java
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

final class PassInfo {
  private boolean[][] meshInfoArrays;
  private boolean[]   bodyInfos;
  
  public PassInfo(Mesh[][] meshArrays, Pass pass) {
    meshInfoArrays = createMeshInfoArrays(meshArrays, pass);
    bodyInfos      = createBodyInfos();
  }
  
  private boolean createMeshInfo(Mesh mesh, Pass pass) {
    return pass.includes(mesh.getAsset());
  }
  
  private boolean[] createMeshInfos(Mesh[] meshes, Pass pass) {
    boolean[] meshInfos = new boolean[meshes.length];
    
    for (int idx = 0; idx < meshes.length; idx++) {
      meshInfos[idx] = createMeshInfo(meshes[idx], pass);
    }
    
    return meshInfos;
  }
  
  private boolean[][] createMeshInfoArrays(Mesh[][] meshArrays, Pass pass) {
    boolean[][] meshInfoArrays = new boolean[meshArrays.length][];
    
    for (int idx = 0; idx < meshArrays.length; idx++) {
      meshInfoArrays[idx] = createMeshInfos(meshArrays[idx], pass);
    }
    
    return meshInfoArrays;
  }
  
  private boolean createBodyInfo(boolean[] meshInfos) {
    for (boolean meshInfo: meshInfos) {
      if (meshInfo) {
        return true;
      }
    }
    
    return false;
  }
  
  private boolean[] createBodyInfos() {
    boolean[] bodyInfos = new boolean[meshInfoArrays.length];
    
    for (int idx = 0; idx < meshInfoArrays.length; idx++) {
      bodyInfos[idx] = createBodyInfo(meshInfoArrays[idx]);
    }
    
    return bodyInfos;
  }
  
  public boolean includesMesh(int bodyIdx, int meshIdx) {
    return meshInfoArrays[bodyIdx][meshIdx];
  }
  
  public boolean includesBody(int bodyIdx) {
    return bodyInfos[bodyIdx];
  }
}
