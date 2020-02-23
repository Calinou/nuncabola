/*
 * ReplayNameTool.java
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

package com.uppgarn.nuncabola.core.replay;

import com.uppgarn.nuncabola.core.level.*;

public final class ReplayNameTool {
  public static String getName(
      String   pattern,
      LevelSet set,
      String   levelNumber) {
    StringBuilder strBld = new StringBuilder();
    
    boolean format = false;
    
    for (int idx = 0; idx < pattern.length(); idx++) {
      char ch = pattern.charAt(idx);
      
      if (format) {
        if (ch == '%') {
          strBld.append(ch);
        } else if (ch == 's') {
          strBld.append(set.getID());
        } else if (ch == 'l') {
          strBld.append(levelNumber);
        }
        
        format = false;
      } else {
        if (ch == '%') {
          format = true;
        } else {
          strBld.append(ch);
        }
      }
    }
    
    return strBld.toString();
  }
  
  private ReplayNameTool() {
  }
}
