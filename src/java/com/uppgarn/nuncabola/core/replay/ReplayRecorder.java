/*
 * ReplayRecorder.java
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
import com.uppgarn.nuncabola.core.series.*;

import com.uppgarn.codelibf.util.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public final class ReplayRecorder {
  private final Path   file;
  private final Series series;
  
  private SeekableByteChannel channel;
  private OutputStream        out;
  private CommandRecorder     cmdRecorder;
  
  private boolean closed;
  
  public ReplayRecorder(Path file, Series series) {
    this.file   = file;
    this.series = series;
    
    // Create replay.
    
    SeekableByteChannel channel;
    OutputStream        out;
    
    try {
      channel = Files.newByteChannel(
                  file,
                  StandardOpenOption.WRITE,
                  StandardOpenOption.CREATE,
                  StandardOpenOption.TRUNCATE_EXISTING);
      out     = new BufferedOutputStream(Channels.newOutputStream(channel));
      
      // Write header.
      
      try {
        ReplayInfoWriteTool.writeReplayInfo(out, new ReplayInfo(series));
        
        out.flush();
      } catch (IOException ex) {
        // An error occurred, close replay.
        
        try {
          channel.close();
        } catch (IOException ex0) {
        }
        
        channel = null;
        out     = null;
      }
    } catch (IOException ex) {
      // Replay creation failed.
      
      channel = null;
      out     = null;
    }
    
    this.channel = channel;
    this.out     = out;
    
    // Create command recorder.
    
    if (this.channel == null) {
      cmdRecorder = null;
    } else {
      cmdRecorder = CommandRecorder.create(this.out);
    }
    
    closed = false;
  }
  
  public void record(Command cmd) {
    assert !closed;
    
    if (channel != null) {
      // Record command.
      
      cmdRecorder.record(cmd);
    }
  }
  
  public void close() {
    if (closed) {
      return;
    }
    
    if (channel != null) {
      // Close command recorder.
      
      cmdRecorder.close();
      
      // Rewrite header with updated data (status, time, coins).
      // This both assumes and requires that the header has
      // the same length it had when it was first written.
      // In particular, the player name should not have changed.
      
      try {
        out.flush();
        
        channel.position(0);
        
        ReplayInfoWriteTool.writeReplayInfo(out, new ReplayInfo(series));
        
        out.flush();
      } catch (IOException ex) {
      }
      
      // Close replay.
      
      try {
        channel.close();
      } catch (IOException ex) {
      }
    }
    
    // Delete an empty replay.
    
    if (series.getTime() == 0) {
      try {
        Files.delete(file);
      } catch (IOException ex) {
      }
    }
    
    closed = true;
  }
  
  private static final class CommandRecorder {
    public static CommandRecorder create(OutputStream out) {
      CommandRecorder instance = new CommandRecorder(out);
      instance.start();
      
      return instance;
    }
    
    private final OutputStream out;
    
    private Queue<Command> cmds;
    
    private TerminableThread thread;
    
    private CommandRecorder(OutputStream out) {
      this.out = out;
      
      cmds = new ArrayDeque<>();
      
      thread = new CommandThread();
    }
    
    private void start() {
      thread.start();
    }
    
    public void record(Command cmd) {
      synchronized (cmds) {
        cmds.add(cmd);
        cmds.notify();
      }
    }
    
    public void close() {
      thread.terminate();
    }
    
    private final class CommandThread extends TerminableThread {
      private boolean errorOccurred = false;
      
      private void handle(Command cmd) {
        if (errorOccurred) {
          return;
        }
        
        synchronized (getInterruptionLock()) {
          boolean wasInterrupted = Thread.interrupted();
          
          // Write command.
          
          try {
            CommandWriteTool.writeCommand(out, cmd);
            
            if (cmd.getType() == Command.Type.END_OF_UPDATE) {
              out.flush();
            }
          } catch (IOException ex) {
            errorOccurred = true;
          }
          
          if (wasInterrupted) {
            Thread.currentThread().interrupt();
          }
        }
      }
      
      @Override
      public void run() {
        try {
          while (!Thread.currentThread().isInterrupted()) {
            Command cmd;
            
            synchronized (cmds) {
              while ((cmd = cmds.poll()) == null) {
                cmds.wait();
              }
            }
            
            handle(cmd);
          }
        } catch (InterruptedException ex) {
          // Allow thread to exit.
        }
        
        synchronized (cmds) {
          for (Command cmd; (cmd = cmds.poll()) != null;) {
            handle(cmd);
          }
        }
      }
    }
  }
}
