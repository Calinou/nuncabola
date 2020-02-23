/*
 * DisplayTool.java
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

package com.uppgarn.nuncabola.core.display;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import java.util.*;

public final class DisplayTool {
  private static boolean isModeBetter(
      DisplayMode mode1,
      DisplayMode mode2,
      DisplayMode deskMode) {
    // Four criteria are evaluated, in the following order:
    //
    // 1. A color depth matching the desktop color depth wins.
    // 2. Otherwise, the higher color depth wins.
    // 3. A refresh rate matching the desktop refresh rate wins.
    // 4. Otherwise, the higher refresh rate wins.
    
    int bpp1    = mode1   .getBitsPerPixel();
    int bpp2    = mode2   .getBitsPerPixel();
    int deskBpp = deskMode.getBitsPerPixel();
    
    if ((bpp1 == deskBpp) && (bpp2 != deskBpp)) {
      return true;
    }
    if ((bpp2 == deskBpp) && (bpp1 != deskBpp)) {
      return false;
    }
    if (bpp1 > bpp2) {
      return true;
    }
    if (bpp2 > bpp1) {
      return false;
    }
    
    int freq1    = mode1   .getFrequency();
    int freq2    = mode2   .getFrequency();
    int deskFreq = deskMode.getFrequency();
    
    if (freq1 == deskFreq) {
      return true;
    }
    if (freq2 == deskFreq) {
      return false;
    }
    
    return freq1 >= freq2;
  }
  
  private static DisplayMode[] getAvailableModes() {
    try {
      return Display.getAvailableDisplayModes();
    } catch (LWJGLException ex) {
      return new DisplayMode[0];
    }
  }
  
  public static Set<DisplayMode> getModes() {
    Set<DisplayMode> modes = new HashSet<>();
    
    DisplayMode[] availModes = getAvailableModes();
    DisplayMode   deskMode   = Display.getDesktopDisplayMode();
    
    // Iterate over all available modes, only keeping the best
    // for each resolution.
    
    for (DisplayMode mode: availModes) {
      boolean add = true;
      
      for (Iterator<DisplayMode> it = modes.iterator(); it.hasNext();) {
        DisplayMode oldMode = it.next();
        
        if    ((oldMode.getWidth () == mode.getWidth ())
            && (oldMode.getHeight() == mode.getHeight())) {
          // Choose between two modes with the same resolution.
          
          if (isModeBetter(oldMode, mode, deskMode)) {
            // Keep old mode.
            
            add = false;
          } else {
            // Remove old mode, add new one.
            
            it.remove();
          }
          
          break;
        }
      }
      
      if (add) {
        modes.add(mode);
      }
    }
    
    return modes;
  }
  
  public static List<DisplayMode> getSortedModes() {
    List<DisplayMode> modes = new ArrayList<>(getModes());
    
    // Sort modes. Width before height, higher before lower.
    
    modes.sort(new Comparator<DisplayMode>() {
      @Override
      public int compare(DisplayMode o1, DisplayMode o2) {
        int result = Integer.compare(o2.getWidth(), o1.getWidth());
        
        if (result != 0) {
          return result;
        }
        
        return Integer.compare(o2.getHeight(), o1.getHeight());
      }
    });
    
    return modes;
  }
  
  public static DisplayMode getMode(int width, int height) {
    int modeWidth;
    int modeHeight;
    
    Set<DisplayMode> modes = getModes();
    
    if ((width <= 0) || (height <= 0)) {
      // No width and/or height given, select default size.
      
      DisplayMode deskMode = Display.getDesktopDisplayMode();
      
      if ((deskMode.getWidth() > 800) && (deskMode.getHeight() > 600)) {
        modeWidth  = 800;
        modeHeight = 600;
      } else {
        modeWidth  = 640;
        modeHeight = 480;
      }
    } else {
      modeWidth  = width;
      modeHeight = height;
    }
    
    // Disallow unreasonable values by clamping the mode size to
    // the widest available width and the highest available height.
    
    int maxModeWidth  = 0;
    int maxModeHeight = 0;
    
    for (DisplayMode mode: modes) {
      maxModeWidth  = Math.max(maxModeWidth,  mode.getWidth ());
      maxModeHeight = Math.max(maxModeHeight, mode.getHeight());
    }
    
    modeWidth  = Math.min(modeWidth,  maxModeWidth);
    modeHeight = Math.min(modeHeight, maxModeHeight);
    
    // Find fullscreen-capable mode with the desired size.
    
    for (DisplayMode mode: modes) {
      if ((mode.getWidth() == modeWidth) && (mode.getHeight() == modeHeight)) {
        return mode;
      }
    }
    
    // Failing that, return a windowed-only mode.
    
    return new DisplayMode(modeWidth, modeHeight);
  }
  
  private DisplayTool() {
  }
}
