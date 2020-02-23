/*
 * ScreenshotFuncs.java
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

import com.uppgarn.nuncabola.core.graphics.*;
import com.uppgarn.nuncabola.core.image.*;
import com.uppgarn.nuncabola.preferences.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import com.uppgarn.codelibf.util.*;

import java.io.*;
import java.nio.file.*;

public final class ScreenshotFuncs {
  public static void takeScreenshot(Path file) throws IOException {
    ImageSaver.save(file, Gfx.getScreenshot());
  }
  
  public static void takeScreenshot() {
    int idx = getIntPref(Pref.SCREENSHOT) + 1;
    
    Path file = getScreenshotDirectory().resolve(
      "screen" + StringTool.prePad(Integer.toString(idx), 5, '0') + ".png");
    
    try {
      takeScreenshot(file);
      
      // Increase index only on success.
      
      setPref(Pref.SCREENSHOT, idx);
    } catch (IOException ex) {
    }
  }
  
  private ScreenshotFuncs() {
  }
}
