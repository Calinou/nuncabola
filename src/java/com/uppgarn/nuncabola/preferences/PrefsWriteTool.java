/*
 * PrefsWriteTool.java
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

package com.uppgarn.nuncabola.preferences;

import com.uppgarn.nuncabola.core.game.*;

import static com.uppgarn.nuncabola.preferences.PrefsIOTool.*;

import com.uppgarn.codelibf.util.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class PrefsWriteTool {
  private static String getValueString(Object value) {
    if (value instanceof Boolean) {
      // Boolean.
      
      return ((Boolean) value) ? "1" : "0";
    } else if (value instanceof Camera) {
      // Camera.
      
      return Integer.toString(((Camera) value).ordinal());
    } else {
      // Other type.
      
      return value.toString();
    }
  }
  
  private static void writePrefs(Writer out, Prefs prefs) throws IOException {
    int maxNameLen = getMaximumNameLength();
    
    for (Map.Entry<String, Pref> entry: getPrefMap().entrySet()) {
      String name  = entry.getKey();
      Pref   pref  = entry.getValue();
      Object value = prefs.get(pref);
      
      if (!((pref == Pref.CHEAT) && !((Boolean) value))) {
        out.write(name);
        out.write(StringTool.space(maxNameLen - name.length() + 1));
        out.write(getValueString(value));
        out.write(StringTool.LS);
      }
    }
  }
  
  public static void writePrefs(OutputStream outStream, Prefs prefs)
      throws IOException {
    Writer out = new BufferedWriter(
      new OutputStreamWriter(outStream, StandardCharsets.UTF_8));
    
    writePrefs(out, prefs);
    
    out.flush();
  }
  
  public static void writePrefs(Path file, Prefs prefs) throws IOException {
    try (OutputStream out = Files.newOutputStream(file)) {
      writePrefs(out, prefs);
    }
  }
  
  private PrefsWriteTool() {
  }
}
