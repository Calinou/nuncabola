/*
 * ReplayFileFuncs.java
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

import com.uppgarn.nuncabola.core.replay.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import com.uppgarn.codelibf.util.*;

import java.io.*;
import java.nio.file.*;

public final class ReplayFileFuncs {
  public static final String EXTENSION = ".nbr";
  
  private static final String GAME_REPLAY_NAME = "Last";
  
  private static Path getReplayFile(String name) {
    Path file;
    
    try {
      String fileName = name.concat(EXTENSION);
      
      file = getReplayDirectory().resolve(fileName).normalize();
    } catch (InvalidPathException ex) {
      return null;
    }
    
    // Security check: File must be inside designated directory.
    
    if (!getReplayDirectory().equals(file.getParent())) {
      return null;
    }
    
    return file;
  }
  
  public static Path getGameReplayFile() {
    return getReplayFile(GAME_REPLAY_NAME);
  }
  
  public static boolean replayExists(String name) {
    Path file = getReplayFile(name);
    
    if (file == null) {
      return false;
    }
    
    return Files.exists(file);
  }
  
  public static boolean gameReplayExists() {
    return replayExists(GAME_REPLAY_NAME);
  }
  
  public static String getDisambiguatedName(String name) {
    String baseStr = name.concat("_");
    
    for (int idx = 1; idx < 100; idx++) {
      String str = baseStr.concat(
        StringTool.prePad(Integer.toString(idx), 2, '0'));
      
      if (!replayExists(str)) {
        return str;
      }
    }
    
    return name;
  }
  
  public static boolean isValidName(String name) {
    Path file = getReplayFile(name);
    
    if (file == null) {
      return false;
    }
    
    return !file.getFileName().toString().equals(EXTENSION)
      && !file.equals(getGameReplayFile());
  }
  
  public static void renameGameReplayTo(String name) {
    assert isValidName(name);
    
    try {
      Files.move(
        getGameReplayFile(),
        getReplayFile(name),
        StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
    }
  }
  
  public static void changeGameReplayPlayer(String player) {
    ReplayFileTool.changePlayer(getGameReplayFile(), player);
  }
  
  private ReplayFileFuncs() {
  }
}
