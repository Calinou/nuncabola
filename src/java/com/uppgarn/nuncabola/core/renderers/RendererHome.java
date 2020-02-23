/*
 * RendererHome.java
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

package com.uppgarn.nuncabola.core.renderers;

import com.uppgarn.nuncabola.core.folder.*;
import com.uppgarn.nuncabola.core.image.*;
import com.uppgarn.nuncabola.core.solid.*;

import java.io.*;

public final class RendererHome {
  private static Folder  dataFolder;
  private static boolean background;
  private static boolean shadow;
  private static float   fov;
  
  private static String ballPath;
  
  private static BillboardRenderer billRend;
  private static HaloRenderer      haloRend;
  private static ItemRenderer      itemRend;
  private static ParticleRenderer  partRend;
  private static ShadowRenderer    shadowRend;
  private static FadeRenderer      fadeRend;
  
  private static BallRenderer ballRend;
  
  public static void initialize(
      Folder  dataFolder,
      boolean background,
      boolean shadow,
      float   fov,
      String  ballPath) {
    RendererHome.dataFolder = dataFolder;
    RendererHome.background = background;
    RendererHome.shadow     = shadow;
    RendererHome.fov        = fov;
    
    RendererHome.ballPath = ballPath;
    
    billRend   = new BillboardRenderer();
    haloRend   = new HaloRenderer();
    itemRend   = new ItemRenderer();
    partRend   = new ParticleRenderer();
    shadowRend = shadow ? new ShadowRenderer() : null;
    fadeRend   = new FadeRenderer();
    
    ballRend = new BallRenderer();
  }
  
  static boolean getBackground() {
    return background;
  }
  
  static boolean getShadow() {
    return shadow;
  }
  
  static float getFOV() {
    return fov;
  }
  
  static String getBallPath() {
    return ballPath;
  }
  
  static Solid loadSolid(String path) {
    try {
      return SolidReadTool.readSolid(dataFolder.getSource(path));
    } catch (IOException ex) {
      return null;
    }
  }
  
  static Image loadImage(String path) {
    try {
      return ImageLoader.load(dataFolder.getSource(path), false);
    } catch (IOException ex) {
      return null;
    }
  }
  
  static BillboardRenderer getBillboardRenderer() {
    return billRend;
  }
  
  static HaloRenderer getHaloRenderer() {
    return haloRend;
  }
  
  static ItemRenderer getItemRenderer() {
    return itemRend;
  }
  
  static ParticleRenderer getParticleRenderer() {
    return partRend;
  }
  
  static ShadowRenderer getShadowRenderer() {
    return shadowRend;
  }
  
  static FadeRenderer getFadeRenderer() {
    return fadeRend;
  }
  
  static BallRenderer getBallRenderer() {
    return ballRend;
  }
  
  public static void setBallPath(String path) {
    if (ballPath.equals(path)) {
      return;
    }
    
    ballPath = path;
    
    ballRend.deinitialize();
    
    ballRend = new BallRenderer();
  }
  
  public static void step(float dt) {
    haloRend.step(dt);
    itemRend.step(dt);
    ballRend.step(dt);
  }
  
  public static void deinitialize() {
    billRend.deinitialize();
    haloRend.deinitialize();
    itemRend.deinitialize();
    partRend.deinitialize();
    
    if (shadowRend != null) {
      shadowRend.deinitialize();
    }
    
    ballRend.deinitialize();
    
    dataFolder = null;
    ballPath   = null;
    billRend   = null;
    haloRend   = null;
    itemRend   = null;
    partRend   = null;
    shadowRend = null;
    fadeRend   = null;
    ballRend   = null;
  }
  
  private RendererHome() {
  }
}
