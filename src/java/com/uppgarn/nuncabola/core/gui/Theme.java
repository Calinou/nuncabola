/*
 * Theme.java
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
import com.uppgarn.nuncabola.core.util.*;

import com.uppgarn.codelibf.io.*;

import static org.lwjgl.opengl.GL11.*;

import java.io.*;
import java.nio.charset.*;

final class Theme {
  public static Theme create(Folder dataFolder, String path) {
    float[] slices = {0.25f, 0.25f, 0.25f, 0.25f};
    
    Source src = dataFolder.getSource(path.concat("/theme.txt"));
    
    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(src.newInputStream(), StandardCharsets.UTF_8))) {
      for (String str; (str = in.readLine()) != null;) {
        if (str.startsWith("slice ")) {
          // Slices.
          
          String[] parts = str.substring(6).trim().split("\\s+", 4);
          
          try {
            for (int idx = 0; idx < parts.length; idx++) {
              slices[idx] =
                Math.min(Math.max(Float.parseFloat(parts[idx]), 0.0f), 1.0f);
            }
          } catch (NumberFormatException ex) {
          }
        }
      }
    } catch (IOException ex) {
    }
    
    return new Theme(path, slices);
  }
  
  private float[] ss;
  private float[] ts;
  
  private int unselectedUnfocusedTexture;
  private int unselectedFocusedTexture;
  private int selectedUnfocusedTexture;
  private int selectedFocusedTexture;
  
  private Theme(String path, float[] slices) {
    ss = createSs(slices);
    ts = createTs(slices);
    
    unselectedUnfocusedTexture = createTexture(path, "plain",        slices);
    unselectedFocusedTexture   = createTexture(path, "plain-focus",  slices);
    selectedUnfocusedTexture   = createTexture(path, "hilite",       slices);
    selectedFocusedTexture     = createTexture(path, "hilite-focus", slices);
  }
  
  private float[] createSs(float[] slices) {
    float[] ss = new float[4];
    
    ss[0] = 0.0f;
    ss[1] = slices[0];
    ss[2] = 1.0f - slices[1];
    ss[3] = 1.0f;
    
    return ss;
  }
  
  private float[] createTs(float[] slices) {
    float[] ts = new float[4];
    
    ts[0] = 1.0f;
    ts[1] = 1.0f - slices[2];
    ts[2] = slices[3];
    ts[3] = 0.0f;
    
    return ts;
  }
  
  private Image getTextureImage(Image img, float[] slices) {
    int size = 0;
    
    for (int idx = 0; idx < 4; idx++) {
      size = Math.max(size, Math.round(GUIHome.getPadding() / slices[idx]));
    }
    
    if ((size > 0) && (size <= 1 << 30)) {
      int size2        = Util.roundPowerOf2(size);
      int widthFactor  = img.getWidth () / size2;
      int heightFactor = img.getHeight() / size2;
      int factor       = Math.min(widthFactor, heightFactor);
      
      if (factor > 1) {
        return img.scaledDown(factor);
      }
    }
    
    return img;
  }
  
  private int createTexture(String path, String name, float[] slices) {
    String imgPath = path + "/back-" + name + ".png";
    
    Image img        = GUIHome.loadImage(imgPath);
    Image textureImg = (img == null) ? null : getTextureImage(img, slices);
    
    return Gfx.createTexture(textureImg, false);
  }
  
  public float getS(int idx) {
    return ss[idx];
  }
  
  public float getT(int idx) {
    return ts[idx];
  }
  
  public int getTexture(boolean selected, boolean focused) {
    if (!selected) {
      return !focused ? unselectedUnfocusedTexture : unselectedFocusedTexture;
    } else {
      return !focused ? selectedUnfocusedTexture   : selectedFocusedTexture;
    }
  }
  
  public void deinitialize() {
    glDeleteTextures(unselectedUnfocusedTexture);
    glDeleteTextures(unselectedFocusedTexture);
    glDeleteTextures(selectedUnfocusedTexture);
    glDeleteTextures(selectedFocusedTexture);
  }
}
