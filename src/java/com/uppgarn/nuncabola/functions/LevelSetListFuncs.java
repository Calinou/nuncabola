/*
 * LevelSetListFuncs.java
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
import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.preferences.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import java.io.*;
import java.util.*;

public final class LevelSetListFuncs {
  private static List<LevelSet> sets;
  
  public static void initialize() {
    sets = createSets();
  }
  
  private static LevelSetList loadSetList() {
    try {
      return LevelSetListReadTool.readLevelSetList(
        DataFuncs.getSource("sets.txt"));
    } catch (IOException ex) {
      return new LevelSetList();
    }
  }
  
  private static Set<String> getSetPaths(LevelSetList setList) {
    return DataFuncs.getPaths("", false, new PathFilter() {
      @Override
      public boolean accepts(String path) {
        return
             path.startsWith("set-")
          && path.endsWith  (".txt")
          && !setList.getSetPaths().contains(path);
      }
    });
  }
  
  private static LevelSet loadSet(String path) {
    try {
      LevelSet set = LevelSetReadTool.readLevelSet(DataFuncs.getSource(path));
      
      if (set.getID().equals("misc") && !getBooleanPref(Pref.CHEAT)) {
        return null;
      }
      
      return set;
    } catch (IOException ex) {
      return null;
    }
  }
  
  private static List<LevelSet> loadSets(Collection<String> paths) {
    List<LevelSet> sets = new ArrayList<>(paths.size());
    
    for (String path: paths) {
      LevelSet set = loadSet(path);
      
      if (set != null) {
        sets.add(set);
      }
    }
    
    return sets;
  }
  
  private static List<LevelSet> createSets() {
    LevelSetList setList  = loadSetList();
    Set<String>  setPaths = getSetPaths(setList);
    
    List<LevelSet> sets0 = loadSets(setList.getSetPaths());
    List<LevelSet> sets1 = loadSets(setPaths);
    
    sets1.sort(new Comparator<LevelSet>() {
      @Override
      public int compare(LevelSet o1, LevelSet o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
      }
    });
    
    List<LevelSet> sets = new ArrayList<>(sets0.size() + sets1.size());
    sets.addAll(sets0);
    sets.addAll(sets1);
    
    return sets;
  }
  
  public static int getSetCount() {
    return sets.size();
  }
  
  public static LevelSet getSet(int idx) {
    return sets.get(idx);
  }
  
  public static void deinitialize() {
    sets = null;
  }
  
  private LevelSetListFuncs() {
  }
}
