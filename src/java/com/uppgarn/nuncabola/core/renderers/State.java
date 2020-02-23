/*
 * State.java
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
import com.uppgarn.nuncabola.core.solid.*;

import static org.lwjgl.opengl.ARBPointParameters.*;
import static org.lwjgl.opengl.ARBPointSprite.*;
import static org.lwjgl.opengl.GL11.*;

final class State {
  private static final Asset DEFAULT_ASSET = new Asset(new Material(), false);
  
  private TextureEnv textureEnv;
  
  private boolean colorMtrlEnabled;
  
  private Asset lastAsset;
  private int   lastDInt;
  private int   lastAInt;
  
  public State() {
    textureEnv = null;
    
    colorMtrlEnabled = false;
    
    lastAsset = DEFAULT_ASSET;
    lastDInt  = lastAsset.getDInt();
    lastAInt  = lastAsset.getAInt();
  }
  
  public void enableDrawing() {
    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_NORMAL_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    
    glBindTexture(GL_TEXTURE_2D, 0);
  }
  
  public TextureEnv getTextureEnv() {
    return textureEnv;
  }
  
  private void setTextureEnv(TextureEnv env) {
    if (textureEnv != env) {
      if (textureEnv != null) {
        textureEnv.configure(false);
      }
      
      textureEnv = env;
      textureEnv.configure(true);
    }
  }
  
  public void setTextureEnv(TextureEnv[] envs) {
    for (TextureEnv env: envs) {
      if (env.isSupported()) {
        setTextureEnv(env);
        
        break;
      }
    }
  }
  
  public boolean setTextureStage(TextureStage stage) {
    if (textureEnv == null) {
      return false;
    }
    
    return textureEnv.apply(stage);
  }
  
  public void setColorMaterialEnabled(boolean enabled) {
    colorMtrlEnabled = enabled;
    
    if (colorMtrlEnabled) {
      glEnable(GL_COLOR_MATERIAL);
    } else {
      glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
      
      glDisable(GL_COLOR_MATERIAL);
      
      // Remember state.
      
      lastDInt = Asset.WHITE_INT;
      lastAInt = Asset.WHITE_INT;
    }
  }
  
  public int getFlags() {
    return lastAsset.getFlags();
  }
  
  public void apply(Asset asset) {
    Material mtrl      = asset    .getMaterial();
    Material lastMtrl  = lastAsset.getMaterial();
    int      flags     = asset    .getFlags();
    int      lastFlags = lastAsset.getFlags();
    
    // Bind the texture.
    
    if (asset.getTexture() != lastAsset.getTexture()) {
      glBindTexture(GL_TEXTURE_2D, asset.getTexture());
    }
    
    // Set material properties.
    
    if ((asset.getDInt() != lastDInt) && !colorMtrlEnabled) {
      glMaterial (GL_FRONT_AND_BACK, GL_DIFFUSE,   Gfx.buffer(mtrl.d));
    }
    if ((asset.getAInt() != lastAInt) && !colorMtrlEnabled) {
      glMaterial (GL_FRONT_AND_BACK, GL_AMBIENT,   Gfx.buffer(mtrl.a));
    }
    if (asset.getSInt() != lastAsset.getSInt()) {
      glMaterial (GL_FRONT_AND_BACK, GL_SPECULAR,  Gfx.buffer(mtrl.s));
    }
    if (asset.getEInt() != lastAsset.getEInt()) {
      glMaterial (GL_FRONT_AND_BACK, GL_EMISSION,  Gfx.buffer(mtrl.e));
    }
    if (asset.getHInt() != lastAsset.getHInt()) {
      glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, mtrl.h);
    }
    
    // Ball shadow.
    
    if (RendererHome.getShadow()) {
      if ((flags & Material.SHADOWED) != 0) {
        if ((lastFlags & Material.SHADOWED) == 0) {
          RendererHome.getShadowRenderer().drawSet  (this);
        }
      } else {
        if ((lastFlags & Material.SHADOWED) != 0) {
          RendererHome.getShadowRenderer().drawClear(this);
        }
      }
    }
    
    // Environment mapping.
    
    if ((flags & Material.ENVIRONMENT) != 0) {
      if ((lastFlags & Material.ENVIRONMENT) == 0) {
        glEnable(GL_TEXTURE_GEN_S);
        glEnable(GL_TEXTURE_GEN_T);
        
        glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_SPHERE_MAP);
        glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_SPHERE_MAP);
      }
    } else {
      if ((lastFlags & Material.ENVIRONMENT) != 0) {
        glDisable(GL_TEXTURE_GEN_S);
        glDisable(GL_TEXTURE_GEN_T);
      }
    }
    
    // Additive blending.
    
    if ((flags & Material.ADDITIVE) != 0) {
      if ((lastFlags & Material.ADDITIVE) == 0) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
      }
    } else {
      if ((lastFlags & Material.ADDITIVE) != 0) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
      }
    }
    
    // Visibility-from-behind.
    
    if ((flags & Material.TWO_SIDED) != 0) {
      if ((lastFlags & Material.TWO_SIDED) == 0) {
        glDisable(GL_CULL_FACE);
        glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
      }
    } else {
      if ((lastFlags & Material.TWO_SIDED) != 0) {
        glEnable(GL_CULL_FACE);
        glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, 0.0f);
      }
    }
    
    // Decal offset.
    
    if ((flags & Material.DECAL) != 0) {
      if ((lastFlags & Material.DECAL) == 0) {
        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(-1.0f, -2.0f);
      }
    } else {
      if ((lastFlags & Material.DECAL) != 0) {
        glDisable(GL_POLYGON_OFFSET_FILL);
      }
    }
    
    // Alpha test.
    
    if ((flags & Material.ALPHA_TEST) != 0) {
      boolean updateFunc;
      
      if ((lastFlags & Material.ALPHA_TEST) == 0) {
        glEnable(GL_ALPHA_TEST);
        
        updateFunc = true;
      } else {
        updateFunc =
             (mtrl.alphaFunc != lastMtrl.alphaFunc)
          || (mtrl.alphaRef  != lastMtrl.alphaRef);
      }
      
      if (updateFunc) {
        glAlphaFunc(AlphaFunctions.getFunction(mtrl.alphaFunc), mtrl.alphaRef);
      }
    } else {
      if ((lastFlags & Material.ALPHA_TEST) != 0) {
        glDisable(GL_ALPHA_TEST);
      }
    }
    
    // Point sprite.
    
    if ((flags & Material.PARTICLE) != 0) {
      if ((lastFlags & Material.PARTICLE) == 0) {
        glEnable (GL_POINT_SPRITE_ARB);
        glTexEnvi(GL_POINT_SPRITE_ARB, GL_COORD_REPLACE_ARB, GL_TRUE);
        glPointParameterARB(
          GL_POINT_DISTANCE_ATTENUATION_ARB,
          Gfx.buffer(0.0f, 0.0f, 1.0f));
        glPointParameterfARB(GL_POINT_SIZE_MIN_ARB, 1.0f);
        glPointParameterfARB(GL_POINT_SIZE_MAX_ARB, Gfx.getHeight() / 4);
      }
    } else {
      if ((lastFlags & Material.PARTICLE) != 0) {
        glDisable(GL_POINT_SPRITE_ARB);
      }
    }
    
    // Lighting.
    
    if ((flags & Material.LIT) != 0) {
      if ((lastFlags & Material.LIT) == 0) {
        glEnable(GL_LIGHTING);
      }
    } else {
      if ((lastFlags & Material.LIT) != 0) {
        glDisable(GL_LIGHTING);
      }
    }
    
    // Remember state.
    
    lastAsset = asset;
    lastDInt  = lastAsset.getDInt();
    lastAInt  = lastAsset.getAInt();
  }
  
  public void applyDefaults() {
    apply(DEFAULT_ASSET);
  }
  
  public void disableDrawing() {
    applyDefaults();
    
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    glDisableClientState(GL_NORMAL_ARRAY);
    glDisableClientState(GL_VERTEX_ARRAY);
  }
}
