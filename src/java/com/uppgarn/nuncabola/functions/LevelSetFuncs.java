/*
 * LevelSetFuncs.java
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

import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.graphics.*;
import com.uppgarn.nuncabola.core.level.*;
import com.uppgarn.nuncabola.core.renderers.*;
import com.uppgarn.nuncabola.core.solid.*;
import com.uppgarn.nuncabola.preferences.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

import com.uppgarn.codelibf.util.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.*;

public final class LevelSetFuncs {
  private static LevelSet set;
  
  private static List<Level>  levels;
  private static List<Level>  levelsR;
  private static List<String> levelNumbers;
  
  private static LevelSetScore setScore;
  
  public static void initialize(LevelSet set) {
    LevelSetFuncs.set = set;
    
    levels       = createLevels();
    levelsR      = Collections.unmodifiableList(levels);
    levelNumbers = LevelNumberTool.getNumbers(levels);
    
    setScore = loadSetScore();
  }
  
  private static Level loadLevel(String path) {
    try {
      Meta meta = SolidReadTool.readSolidMeta(DataFuncs.getSource(path));
      
      return LevelParser.parse(meta, path);
    } catch (IOException ex) {
      return null;
    }
  }
  
  private static List<Level> createLevels() {
    List<Level> levels = new ArrayList<>(set.getLevelCount());
    
    for (String path: set.getLevelPaths()) {
      levels.add(loadLevel(path));
    }
    
    return levels;
  }
  
  private static Path getSetScoreFile() {
    Path file;
    
    try {
      String fileName =
          "scores-"
        + set.getID()
        + (getBooleanPref(Pref.CHEAT) ? "-cheat" : "")
        + ".txt";
      
      file = getScoreDirectory().resolve(fileName).normalize();
    } catch (InvalidPathException ex) {
      return null;
    }
    
    // Security check: File must be inside designated directory.
    
    if (!getScoreDirectory().equals(file.getParent())) {
      return null;
    }
    
    return file;
  }
  
  private static LevelSetScore loadSetScore() {
    Path file = getSetScoreFile();
    
    if (file == null) {
      return new LevelSetScore(set, levels);
    }
    
    try {
      return LevelSetScoreReadTool.readLevelSetScore(file, set, levels);
    } catch (IOException ex) {
      return new LevelSetScore(set, levels);
    }
  }
  
  public static LevelSet getSet() {
    return set;
  }
  
  public static int getLevelCount() {
    return levels.size();
  }
  
  public static Level getLevel(int idx) {
    return levels.get(idx);
  }
  
  public static List<Level> getLevels() {
    return levelsR;
  }
  
  public static String getLevelNumber(int idx) {
    return levelNumbers.get(idx);
  }
  
  public static boolean hasLoadedLevels() {
    return getNextLoadedLevelIndex(0) != -1;
  }
  
  public static int getNextLoadedLevelIndex(int startIdx) {
    for (int idx = startIdx; idx < levels.size(); idx++) {
      if (levels.get(idx) != null) {
        return idx;
      }
    }
    
    return -1;
  }
  
  public static LevelSetScore getSetScore() {
    return setScore;
  }
  
  public static LevelScore getLevelScore(int idx) {
    return setScore.getLevelScore(idx);
  }
  
  public static void saveSetScore() {
    Path file = getSetScoreFile();
    
    if (file == null) {
      return;
    }
    
    try {
      LevelSetScoreWriteTool.writeLevelSetScore(file, setScore, set);
    } catch (IOException ex) {
    }
  }
  
  public static void completeLevels() {
    boolean scoreDirty = false;
    
    for (LevelScore levelScore: setScore.getLevelScores()) {
      if (levelScore.getStatus() != LevelStatus.COMPLETED) {
        levelScore.setStatus(LevelStatus.COMPLETED);
        
        scoreDirty = true;
      }
    }
    
    if (scoreDirty) {
      saveSetScore();
    }
  }
  
  private static Path getSetScreenshotDirectory() {
    Path dir;
    
    try {
      String fileName = set.getID();
      
      dir = getScreenshotDirectory().resolve(fileName).normalize();
    } catch (InvalidPathException ex) {
      return null;
    }
    
    // Security check: File must be inside designated directory.
    
    if (!getScreenshotDirectory().equals(dir.getParent())) {
      return null;
    }
    
    return dir;
  }
  
  private static Path getLevelScreenshotFile(Path dir, String levelPath) {
    Path file;
    
    try {
      String fileName = levelPath;
      
      fileName = StringTool.afterLast (fileName, '/');
      fileName = StringTool.beforeLast(fileName, '.');
      fileName = fileName.concat(".png");
      
      file = dir.resolve(fileName).normalize();
    } catch (InvalidPathException ex) {
      return null;
    }
    
    // Security check: File must be inside designated directory.
    
    if (!dir.equals(file.getParent())) {
      return null;
    }
    
    return file;
  }
  
  private static void createScreenshot(Path dir, String levelPath) {
    Path file = getLevelScreenshotFile(dir, levelPath);
    
    if (file == null) {
      return;
    }
    
    // Initialize game functions.
    
    try {
      GameFuncs.load(levelPath);
    } catch (FuncsException ex) {
      return;
    }
    
    // Configure game.
    
    GameFuncs.getGame().goalsUnlocked = true;
    GameFuncs.getGame().goalFactor    = 1.0f;
    
    GameFlyer gameFlyer = new GameFlyer(
      GameFuncs.getGame(),
      GameFuncs.getViewDistance());
    gameFlyer.fly(1.0f);
    
    // Draw game.
    
    Gfx.clear();
    
    GameFuncs.draw(0.0f, Pose.STATIC);
    
    // Update display contents.
    
    try {
      Display.swapBuffers();
    } catch (LWJGLException ex) {
      throw new RuntimeException(ex);
    }
    
    // Take screenshot.
    
    try {
      ScreenshotFuncs.takeScreenshot(file);
    } catch (IOException ex) {
    }
  }
  
  public static void createScreenshots() {
    Path dir = getSetScreenshotDirectory();
    
    if (dir == null) {
      return;
    }
    
    try {
      if (!Files.exists(dir)) {
        Files.createDirectory(dir);
      }
      
      for (String path: set.getLevelPaths()) {
        createScreenshot(dir, path);
      }
    } catch (IOException ex) {
    }
  }
  
  public static void deinitialize() {
    set          = null;
    levels       = null;
    levelsR      = null;
    levelNumbers = null;
    setScore     = null;
  }
  
  private LevelSetFuncs() {
  }
}
