/*
 * Nuncabola.java
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

package com.uppgarn.nuncabola;

import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.general.*;
import com.uppgarn.nuncabola.ui.*;

import com.uppgarn.codelibf.io.*;
import com.uppgarn.codelibf.util.*;

import org.lwjgl.*;

import java.nio.file.*;

public final class Nuncabola {
  public static void main(String[] args) throws Throwable {
    // Create error logger.
    
    ErrorLogger errorLogger = new ErrorLogger(
      SystemPathTool
        .getApplicationDataDirectory(ProgramConstants.INTERNAL_NAME)
        .resolve("error_log.txt"),
      10);
    
    try {
      // Run main code.
      
      int returnCode = run(args, errorLogger);
      
      System.exit(returnCode);
    } catch (Throwable t) {
      // Log error.
      
      errorLogger.log(t);
      
      throw t;
    }
  }
  
  private static int run(String[] args, ErrorLogger errorLogger) {
    int returnCode = 0;
    
    // Parse arguments.
    
    Path dataDir    = null;
    Path replayFile = null;
    
    for (int idx = 0; idx < args.length; idx++) {
      String arg = args[idx];
      
      // Usage.
      
      if (arg.equals("-h") || arg.equals("--help")) {
        System.out.println(getUsage());
        
        return 0;
      }
      
      // Version.
      
      if (arg.equals("-v") || arg.equals("--version")) {
        System.out.println(ProgramConstants.VERSION);
        
        return 0;
      }
      
      // Data directory.
      
      if (arg.equals("-d") || arg.equals("--data")) {
        if (idx + 1 == args.length) {
          System.err.println("Option '" + arg + "' requires an argument.");
          
          return 1;
        }
        
        String arg0 = args[++idx];
        
        try {
          dataDir = Paths.get(arg0);
        } catch (InvalidPathException ex) {
          System.err.println(
            "Invalid argument '" + arg0 + "' for option '" + arg + "'.");
          
          return 1;
        }
        
        continue;
      }
      
      // Replay.
      
      if (arg.equals("-r") || arg.equals("--replay")) {
        if (idx + 1 == args.length) {
          System.err.println("Option '" + arg + "' requires an argument.");
          
          return 1;
        }
        
        String arg0 = args[++idx];
        
        try {
          replayFile = Paths.get(arg0);
        } catch (InvalidPathException ex) {
          System.err.println(
            "Invalid argument '" + arg0 + "' for option '" + arg + "'.");
          
          return 1;
        }
        
        continue;
      }
      
      // Assume a single unrecognized argument is a replay name.
      
      if (args.length == 1) {
        try {
          replayFile = Paths.get(arg);
        } catch (InvalidPathException ex) {
          // Ignore invalid argument.
        }
        
        break;
      }
    }
    
    // Set native library paths for LWJGL and JInput.
    
    String path = getNativeLibraryPath();
    
    if (path != null) {
      System.setProperty("org.lwjgl.librarypath",            path);
      System.setProperty("net.java.games.input.librarypath", path);
    }
    
    // Initialize LWJGL.
    
    try {
      Sys.initialize();
    } catch (LinkageError err) {
      System.err.println("Failure to load LWJGL.");
      err.printStackTrace();
      
      return 1;
    }
    
    // Initialize base functions.
    
    try {
      BaseFuncs.initialize(dataDir);
    } catch (FuncsException ex) {
      alert("Failure to establish game data directory.", null);
      
      return 1;
    }
    
    // Determine UI mode.
    
    UIMode mode;
    
    if (replayFile == null) {
      mode = UIMode.Standard.INSTANCE;
    } else {
      mode = new UIMode.Replay(replayFile);
    }
    
    // Initialize UI.
    
    try {
      UI.initialize(mode);
      
      try {
        // Run main loop.
        
        UI.loop(errorLogger);
      } finally {
        // Deinitialize UI.
        
        UI.deinitialize();
      }
    } catch (UIException ex) {
      alert("Failure to create UI.", ex);
      
      returnCode = 1;
    }
    
    // Deinitialize base functions.
    
    BaseFuncs.deinitialize();
    
    return returnCode;
  }
  
  private static String getUsage() {
    return
        "Usage: java -jar "
      + ProgramConstants.INTERNAL_NAME_LC
      + ".jar [options ...]"
      + StringTool.LS
      + "Options:"
      + StringTool.LS
      + "  -h, --help           Show this usage message."
      + StringTool.LS
      + "  -v, --version        Show version."
      + StringTool.LS
      + "  -d, --data <dir>     Use 'dir' as game data directory."
      + StringTool.LS
      + "  -r, --replay <file>  Play the replay 'file'.";
  }
  
  private static String getPlatform() {
    String os = System.getProperty("os.name");
    
    if (os.startsWith("Windows")) {
      return "windows";
    } else if (os.startsWith("Mac OS X") || os.startsWith("Darwin")) {
      return "macosx";
    } else {
      return "linux";
    }
  }
  
  private static String getNativeLibraryPath() {
    Path jarDir = ClassPathTool.getJarDirectory(Nuncabola.class);
    
    if (jarDir == null) {
      return null;
    }
    
    Path libDir      = jarDir   .resolve("lib");
    Path nativeDir   = libDir   .resolve("native");
    Path platformDir = nativeDir.resolve(getPlatform());
    
    return platformDir.toString();
  }
  
  private static void alert(String msg, Throwable t) {
    System.err.println(msg);
    
    if (t != null) {
      t.printStackTrace();
    }
    
    Sys.alert(ProgramConstants.TITLE, msg);
  }
}
