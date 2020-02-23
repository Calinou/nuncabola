/*
 * ReplayGameServer.java
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

import com.uppgarn.nuncabola.core.game.*;

import com.uppgarn.codelibf.io.*;

import java.io.*;

public final class ReplayGameServer extends GameServer {
  private final Source src;
  
  private InputStream in;
  
  private boolean errorOccurred;
  private boolean closed;
  
  public ReplayGameServer(Source src) {
    this.src = src;
    
    // Open replay.
    
    InputStream in;
    
    try {
      in = new BufferedInputStream(src.newInputStream());
      
      // Skip header.
      
      try {
        ReplayInfoReadTool.readReplayInfo(in);
      } catch (IOException ex) {
        // An error occurred, close replay.
        
        try {
          in.close();
        } catch (IOException ex0) {
        }
        
        in = null;
      }
    } catch (SourceException ex) {
      // Replay opening failed.
      
      in = null;
    }
    
    this.in = in;
    
    errorOccurred = this.in == null;
    closed        = false;
    
    readUpdate();
  }
  
  private void readUpdate() {
    if (errorOccurred) {
      return;
    }
    
    try {
      Command cmd;
      
      do {
        cmd = CommandReadTool.readCommand(in);
        
        sendCommand(cmd);
      } while (cmd.getType() != Command.Type.END_OF_UPDATE);
    } catch (IOException ex) {
      errorOccurred = true;
    }
  }
  
  public void step() {
    assert !closed;
    
    clearCommands();
    
    readUpdate();
  }
  
  public void close() {
    if (closed) {
      return;
    }
    
    // Close replay.
    
    if (in != null) {
      try {
        in.close();
      } catch (IOException ex) {
      }
    }
    
    closed = true;
  }
}
