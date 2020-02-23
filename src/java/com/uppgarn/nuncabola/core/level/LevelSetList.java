/*
 * LevelSetList.java
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

package com.uppgarn.nuncabola.core.level;

import java.util.*;

public final class LevelSetList {
  private List<String> setPaths;
  
  public LevelSetList() {
    setPaths = Collections.emptyList();
  }
  
  public int getSetCount() {
    return setPaths.size();
  }
  
  public String getSetPath(int idx) {
    return setPaths.get(idx);
  }
  
  public List<String> getSetPaths() {
    return setPaths;
  }
  
  public void setSetPaths(List<String> paths) {
    setPaths = Collections.unmodifiableList(new ArrayList<>(paths));
  }
}
