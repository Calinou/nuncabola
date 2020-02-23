/*
 * ReplayFileTool.java
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

import com.uppgarn.nuncabola.general.*;

import java.io.*;
import java.nio.file.*;

public final class ReplayFileTool {
  public static boolean changePlayer(Path file, String player) {
    try {
      Path temp = Files.createTempFile(ProgramConstants.INTERNAL_NAME_LC, null);
      
      try {
        try (InputStream  in  = new BufferedInputStream (
               Files.newInputStream (file));
             OutputStream out = new BufferedOutputStream(
               Files.newOutputStream(temp))) {
          ReplayInfo info = ReplayInfoReadTool.readReplayInfo(in);
          info.setPlayer(player);
          
          ReplayInfoWriteTool.writeReplayInfo(out, info);
          
          byte[] buf = new byte[1024];
          
          for (int read; (read = in.read(buf)) != -1;) {
            out.write(buf, 0, read);
          }
          
          out.flush();
        }
        
        try {
          Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
          
          return true;
        } catch (IOException ex) {
          return false;
        }
      } catch (IOException ex) {
        try {
          Files.delete(temp);
        } catch (IOException ex0) {
        }
        
        return false;
      }
    } catch (IOException ex) {
      return false;
    }
  }
  
  private ReplayFileTool() {
  }
}
