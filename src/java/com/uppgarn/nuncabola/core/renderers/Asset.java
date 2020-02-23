/*
 * Asset.java
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

import com.uppgarn.nuncabola.core.graphics.*;
import com.uppgarn.nuncabola.core.image.*;
import com.uppgarn.nuncabola.core.math.*;
import com.uppgarn.nuncabola.core.solid.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

final class Asset {
  public static final int WHITE_INT = 0xFFFFFFFF;
  
  private static int toByte(float f) {
    return (int) (f * 255.0f);
  }
  
  private static int toInt(Color4 c) {
    int r = toByte(c.r);
    int g = toByte(c.g);
    int b = toByte(c.b);
    int a = toByte(c.a);
    
    return r | (g << 8) | (b << 16) | (a << 24);
  }
  
  private final Material mtrl;
  
  private final int flags;
  
  private final int dInt;
  private final int aInt;
  private final int sInt;
  private final int eInt;
  private final int hInt;
  
  private int texture;
  
  public Asset(Material mtrl, boolean shadowedEnabled) {
    this.mtrl = mtrl;
    
    flags = shadowedEnabled ? mtrl.flags : mtrl.flags & ~Material.SHADOWED;
    
    dInt = toInt (mtrl.d);
    aInt = toInt (mtrl.a);
    sInt = toInt (mtrl.s);
    eInt = toInt (mtrl.e);
    hInt = toByte(mtrl.h);
    
    texture = createTexture();
  }
  
  private Image getImage(String path) {
    if (!path.isEmpty()) {
      for (int idx0 = 0; idx0 < 2; idx0++) {
        String path0 = (idx0 == 0) ? "textures/".concat(path) : path;
        
        if (path0.endsWith(".png") || path0.endsWith(".jpg")) {
          Image img = RendererHome.loadImage(path0);
          
          if (img != null) {
            return img;
          }
        } else {
          for (int idx1 = 0; idx1 < 2; idx1++) {
            String path1 = path0.concat((idx1 == 0) ? ".png" : ".jpg");
            
            Image img = RendererHome.loadImage(path1);
            
            if (img != null) {
              return img;
            }
          }
        }
      }
    }
    
    return null;
  }
  
  private Image getImage() {
    if (mtrl.d.a == 0.0f) {
      return null;
    }
    
    return getImage(mtrl.path);
  }
  
  private int createTexture() {
    int texture = Gfx.createTexture(getImage(), true);
    
    if (texture != 0) {
      // Set the texture to clamp or repeat based on material type.
      
      int sParam = ((flags & Material.CLAMP_S) != 0)
                   ? GL_CLAMP_TO_EDGE : GL_REPEAT;
      int tParam = ((flags & Material.CLAMP_T) != 0)
                   ? GL_CLAMP_TO_EDGE : GL_REPEAT;
      
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, sParam);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, tParam);
    }
    
    return texture;
  }
  
  public Material getMaterial() {
    return mtrl;
  }
  
  public int getFlags() {
    return flags;
  }
  
  public int getDInt() {
    return dInt;
  }
  
  public int getAInt() {
    return aInt;
  }
  
  public int getSInt() {
    return sInt;
  }
  
  public int getEInt() {
    return eInt;
  }
  
  public int getHInt() {
    return hInt;
  }
  
  public int getTexture() {
    return texture;
  }
  
  public void deinitialize() {
    glDeleteTextures(texture);
  }
}
