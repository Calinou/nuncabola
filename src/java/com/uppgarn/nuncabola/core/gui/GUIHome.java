/*
 * GUIHome.java
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

package com.uppgarn.nuncabola.core.gui;

import com.uppgarn.nuncabola.core.folder.*;
import com.uppgarn.nuncabola.core.graphics.*;
import com.uppgarn.nuncabola.core.image.*;

import java.io.*;
import java.util.*;

public final class GUIHome {
  private static Folder dataFolder;
  
  private static int refHeight;
  private static int padding;
  
  private static Theme       theme;
  private static LookManager lookMgr;
  
  private static Map<Font, TextImagizer> textImagizers;
  private static Map<Font, Digit[]     > digitArrays;
  
  public static void initialize(Folder dataFolder, String themeDir) {
    GUIHome.dataFolder = dataFolder;
    
    refHeight = createReferenceHeight();
    padding   = createPadding();
    
    theme   = Theme.create(dataFolder, "gui/".concat(themeDir));
    lookMgr = new LookManager();
    
    textImagizers = createTextImagizers();
    digitArrays   = createDigitArrays();
  }
  
  private static int createReferenceHeight() {
    final float ratio = 4.0f / 3;
    
    return Math.min(Math.round(Gfx.getWidth() / ratio), Gfx.getHeight());
  }
  
  private static int createPadding() {
    return Math.round(refHeight / 60.0f);
  }
  
  private static Map<Font, TextImagizer> createTextImagizers() {
    Map<Font, TextImagizer> textImagizers = new EnumMap<>(Font.class);
    
    TextImagizerCreator imagizerCreator = new TextImagizerCreator(
      dataFolder.getSource("ttf/DejaVuSans-Bold.ttf"));
    
    for (Font font: Font.values()) {
      float size = font.getPointSize(refHeight);
      
      textImagizers.put(font, imagizerCreator.create(true, size));
    }
    
    return textImagizers;
  }
  
  private static Map<Font, Digit[]> createDigitArrays() {
    Map<Font, Digit[]> digitArrays = new EnumMap<>(Font.class);
    
    for (Font font: Font.values()) {
      Digit[] digits = new Digit[11];
      
      for (int idx = 0; idx < 11; idx++) {
        digits[idx] = new Digit(font, idx);
      }
      
      digitArrays.put(font, digits);
    }
    
    return digitArrays;
  }
  
  static int getReferenceHeight() {
    return refHeight;
  }
  
  static int getPadding() {
    return padding;
  }
  
  static Theme getTheme() {
    return theme;
  }
  
  static LookManager getLookManager() {
    return lookMgr;
  }
  
  static TextImagizer getTextImagizer(Font font) {
    return textImagizers.get(font);
  }
  
  static Digit getDigit(Font font, int d) {
    return digitArrays.get(font)[d];
  }
  
  static Image loadImage(String path) {
    try {
      return ImageLoader.load(dataFolder.getSource(path), false);
    } catch (IOException ex) {
      return null;
    }
  }
  
  public static boolean canDisplay(Font font, char ch) {
    return textImagizers.get(font).canDisplay(ch);
  }
  
  public static void deinitialize() {
    for (Digit[] digits: digitArrays.values()) {
      for (Digit digit: digits) {
        digit.deinitialize();
      }
    }
    
    lookMgr.deinitialize();
    theme  .deinitialize();
    
    dataFolder    = null;
    theme         = null;
    lookMgr       = null;
    textImagizers = null;
    digitArrays   = null;
  }
  
  private GUIHome() {
  }
}
