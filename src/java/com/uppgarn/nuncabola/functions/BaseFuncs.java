/*
 * BaseFuncs.java
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

import com.uppgarn.nuncabola.core.controls.*;
import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.general.*;
import com.uppgarn.nuncabola.preferences.*;

import com.uppgarn.codelibf.io.*;

import java.io.*;
import java.nio.file.*;

public final class BaseFuncs {
  private static Path dataDir;
  private static Path userDir;
  private static Path userDataDir;
  private static Path scoreDir;
  private static Path replayDir;
  private static Path screenshotDir;
  
  private static Prefs prefs;
  
  public static void initialize(Path specDataDir) throws FuncsException {
    dataDir       = createDataDirectory(specDataDir);
    userDir       = createUserDirectory();
    userDataDir   = createUserSubdirectory("Data");
    scoreDir      = createUserSubdirectory("Scores");
    replayDir     = createUserSubdirectory("Replays");
    screenshotDir = createUserSubdirectory("Screenshots");
    
    prefs = loadPrefs();
  }
  
  private static Path createDataDirectory(Path specDataDir)
      throws FuncsException {
    Path dir;
    
    if (specDataDir != null) {
      // Use directory specified by the user.
      
      dir = specDataDir;
    } else {
      // Use directory based on the location of the program JAR file.
      
      Path jarDir = ClassPathTool.getJarDirectory(BaseFuncs.class);
      
      if (jarDir == null) {
        dir = null;
      } else {
        dir = jarDir.resolve("data");
      }
    }
    
    if ((dir == null) || !Files.isDirectory(dir)) {
      throw new FuncsException();
    }
    
    return dir;
  }
  
  private static Path createUserDirectory() {
    return SystemPathTool.getApplicationDataDirectory(
      ProgramConstants.INTERNAL_NAME);
  }
  
  private static Path createUserSubdirectory(String name) {
    Path dir = userDir.resolve(name);
    
    if (!Files.exists(dir)) {
      try {
        Files.createDirectory(dir);
      } catch (IOException ex) {
      }
    }
    
    return dir;
  }
  
  private static Path getPrefsFile() {
    return userDir.resolve("preferences.txt");
  }
  
  private static Prefs loadPrefs() {
    try {
      return PrefsReadTool.readPrefs(getPrefsFile());
    } catch (IOException ex) {
      return new Prefs();
    }
  }
  
  private static void savePrefs() {
    try {
      PrefsWriteTool.writePrefs(getPrefsFile(), prefs);
    } catch (IOException ex) {
    }
  }
  
  public static Path getDataDirectory() {
    return dataDir;
  }
  
  public static Path getUserDirectory() {
    return userDir;
  }
  
  public static Path getUserDataDirectory() {
    return userDataDir;
  }
  
  public static Path getScoreDirectory() {
    return scoreDir;
  }
  
  public static Path getReplayDirectory() {
    return replayDir;
  }
  
  public static Path getScreenshotDirectory() {
    return screenshotDir;
  }
  
  public static boolean getBooleanPref(Pref pref) {
    return prefs.getBoolean(pref);
  }
  
  public static int getIntPref(Pref pref) {
    return prefs.getInt(pref);
  }
  
  public static String getStringPref(Pref pref) {
    return prefs.getString(pref);
  }
  
  public static Camera getCameraPref(Pref pref) {
    return prefs.getCamera(pref);
  }
  
  public static Key getKeyPref(Pref pref) {
    return prefs.getKey(pref);
  }
  
  public static boolean isKey(int code, char ch, Pref pref) {
    return getKeyPref(pref).matches(code, ch);
  }
  
  public static MouseButton getMouseButtonPref(Pref pref) {
    return prefs.getMouseButton(pref);
  }
  
  public static boolean isMouseButton(int idx, Pref pref) {
    return getMouseButtonPref(pref).matches(idx);
  }
  
  public static void setPref(Pref pref, Object value) {
    prefs.set(pref, value);
  }
  
  public static void gc() {
    if (getBooleanPref(Pref.GC_EXPLICIT)) {
      System.gc();
    }
  }
  
  public static void deinitialize() {
    savePrefs();
    
    dataDir       = null;
    userDir       = null;
    userDataDir   = null;
    scoreDir      = null;
    replayDir     = null;
    screenshotDir = null;
    prefs         = null;
  }
  
  private BaseFuncs() {
  }
}
