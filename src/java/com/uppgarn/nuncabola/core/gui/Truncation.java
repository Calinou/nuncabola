/*
 * Truncation.java
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

package com.uppgarn.nuncabola.core.gui;

import com.uppgarn.nuncabola.core.image.*;

public enum Truncation {
  NONE,
  BEGINNING,
  END;
  
  // For reasons of text measurement/rendering compatibility,
  // use a full string rather than Character.toString('\u2026').
  
  private static final String ELLIPSIS = "...";
  
  String getText(String text, int availWidth, TextImagizer imagizer) {
    // Return text unmodified if no truncation requested
    // or if the entire text fits the available space.
    
    if ((this == NONE)
        || text.isEmpty()
        || (imagizer.getSize(text).getWidth() <= availWidth)) {
      return text;
    }
    
    if (this == BEGINNING) {
      // Truncate text at the beginning.
      
      int left  = 0;
      int right = text.length();
      
      while (right - left > 1) {
        int mid = (left + right) / 2;
        
        String str = ELLIPSIS.concat(text.substring(mid));
        
        if (imagizer.getSize(str).getWidth() <= availWidth) {
          right = mid;
        } else {
          left  = mid;
        }
      }
      
      return ELLIPSIS.concat(text.substring(right));
    } else {
      // Truncate text at the end.
      
      int left  = 0;
      int right = text.length();
      
      while (right - left > 1) {
        int mid = (left + right) / 2;
        
        String str = text.substring(0, mid).concat(ELLIPSIS);
        
        if (imagizer.getSize(str).getWidth() <= availWidth) {
          left  = mid;
        } else {
          right = mid;
        }
      }
      
      return text.substring(0, left).concat(ELLIPSIS);
    }
  }
}
