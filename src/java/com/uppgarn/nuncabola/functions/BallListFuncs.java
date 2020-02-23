/*
 * BallListFuncs.java
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

package com.uppgarn.nuncabola.functions;

import com.uppgarn.nuncabola.core.folder.*;
import com.uppgarn.nuncabola.preferences.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import com.uppgarn.codelibf.util.*;

import java.nio.file.*;
import java.util.*;

public final class BallListFuncs {
  private static List<String> paths;
  
  public static void initialize() {
    paths = createPaths();
  }
  
  private static Set<String> getDirectories() {
    return DataFuncs.getPaths("ball", true, new PathFilter() {
      @Override
      public boolean accepts(String path) {
        String subPath = "ball/" + path + "/" + path;
        
        return
             DataFuncs.exists(subPath.concat("-solid.sol"), false)
          || DataFuncs.exists(subPath.concat("-inner.sol"), false)
          || DataFuncs.exists(subPath.concat("-outer.sol"), false);
      }
    });
  }
  
  private static List<String> createPaths() {
    Set<String> dirs     = getDirectories();
    String      currPath = getStringPref(Pref.BALL_PATH);
    
    List<String> paths = new ArrayList<>(dirs.size() + 1);
    
    for (String dir: dirs) {
      paths.add("ball/" + dir + "/" + dir);
    }
    if (!paths.contains(currPath)) {
      paths.add(currPath);
    }
    
    paths.sort(
      Paths.get("a").equals(Paths.get("A"))
      ? String.CASE_INSENSITIVE_ORDER : null);
    
    return paths;
  }
  
  public static int getBallCount() {
    return paths.size();
  }
  
  public static String getPath(int idx) {
    return paths.get(idx);
  }
  
  public static String getName(int idx) {
    return StringTool.afterLast(paths.get(idx), '/');
  }
  
  public static int getIndexOfCurrentBall() {
    return paths.indexOf(getStringPref(Pref.BALL_PATH));
  }
  
  public static void deinitialize() {
    paths = null;
  }
  
  private BallListFuncs() {
  }
}
