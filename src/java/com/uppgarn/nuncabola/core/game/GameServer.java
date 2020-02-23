/*
 * GameServer.java
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

package com.uppgarn.nuncabola.core.game;

import java.util.*;

public abstract class GameServer {
  private Rate rate;
  
  private List<Command> cmds;
  private List<Command> cmdsR;
  
  protected GameServer() {
    rate = Rate.DEFAULT;
    
    cmds  = new ArrayList<>();
    cmdsR = Collections.unmodifiableList(cmds);
  }
  
  public final Rate getRate() {
    return rate;
  }
  
  public final List<Command> getCommands() {
    return cmdsR;
  }
  
  protected final void clearCommands() {
    cmds.clear();
  }
  
  protected final void sendCommand(Command cmd) {
    if (cmd.getType() == Command.Type.RATE) {
      Command.Rate myCmd = (Command.Rate) cmd;
      
      if (myCmd.ups > 0) {
        rate = new Rate(myCmd.ups);
      }
    }
    
    cmds.add(cmd);
  }
}
