/*
 * TextureEnv.java
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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

enum TextureEnv {
  DEFAULT(TextureStage.TEXTURE) {
    @Override
    void configure(TextureStage stage, boolean enabled) {
      switch (stage) {
        case TEXTURE: {
          if (enabled) {
            glEnable(GL_TEXTURE_2D);
            
            // Modulate is the default mode.
            
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
            
            glMatrixMode(GL_TEXTURE);
            glLoadIdentity();
            glMatrixMode(GL_MODELVIEW);
          } else {
            glDisable(GL_TEXTURE_2D);
          }
          
          break;
        }
        
        default: {
          break;
        }
      }
    }
  },
  
  SHADOW(TextureStage.SHADOW, TextureStage.TEXTURE) {
    @Override
    void configure(TextureStage stage, boolean enabled) {
      switch (stage) {
        case TEXTURE: {
          DEFAULT.configure(stage, enabled);
          
          break;
        }
        case SHADOW: {
          if (enabled) {
            glDisable(GL_TEXTURE_2D);
            
            // Modulate primary color and shadow alpha.
            
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
            
            glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_MODULATE);
            glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_PREVIOUS);
            glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE1_RGB, GL_TEXTURE);
            glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
            glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_ONE_MINUS_SRC_ALPHA);
            
            // Copy incoming alpha.
            
            glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
            glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_PREVIOUS);
            glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
            
            glMatrixMode(GL_TEXTURE);
            glLoadIdentity();
            glMatrixMode(GL_MODELVIEW);
          } else {
            glDisable(GL_TEXTURE_2D);
          }
          
          break;
        }
        case CLIP: {
          if (enabled) {
            glDisable(GL_TEXTURE_2D);
            
            // Interpolate shadowed and non-shadowed primary color.
            
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
            
            glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_INTERPOLATE);
            glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_PREVIOUS);
            glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE1_RGB, GL_PRIMARY_COLOR);
            glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE2_RGB, GL_TEXTURE);
            glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
            glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_SRC_COLOR);
            glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND2_RGB, GL_SRC_ALPHA);
            
            // Copy incoming alpha.
            
            glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
            glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_PREVIOUS);
            glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
            
            glMatrixMode(GL_TEXTURE);
            glLoadIdentity();
            glMatrixMode(GL_MODELVIEW);
          } else {
            glDisable(GL_TEXTURE_2D);
          }
          
          break;
        }
      }
    }
  },
  
  SHADOW_CLIP(TextureStage.SHADOW, TextureStage.CLIP, TextureStage.TEXTURE) {
    @Override
    void configure(TextureStage stage, boolean enabled) {
      SHADOW.configure(stage, enabled);
    }
  },
  
  POSE(TextureStage.SHADOW, TextureStage.TEXTURE) {
    @Override
    void configure(TextureStage stage, boolean enabled) {
      // We can't do the obvious thing and use a single texture unit
      // for this, because everything assumes that the "texture" stage
      // is permanently available.
      
      switch (stage) {
        case TEXTURE: {
          DEFAULT.configure(stage, enabled);
          
          break;
        }
        case SHADOW: {
          if (enabled) {
            glDisable(GL_TEXTURE_2D);
            
            // Make shadow texture override everything else.
            
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
          } else {
            glDisable(GL_TEXTURE_2D);
          }
          
          break;
        }
        
        default: {
          break;
        }
      }
    }
  };
  
  private static final int[] UNITS = {
    GL_TEXTURE0,
    GL_TEXTURE1,
    GL_TEXTURE2
  };
  
  private final TextureStage[] stages;
  
  TextureEnv(TextureStage... stages) {
    this.stages = stages;
  }
  
  public final boolean isSupported() {
    return stages.length <= Gfx.getMaximumTextureUnits();
  }
  
  abstract void configure(TextureStage stage, boolean enabled);
  
  public final void configure(boolean enabled) {
    for (int idx = 0; idx < stages.length; idx++) {
      glActiveTexture      (UNITS[idx]);
      glClientActiveTexture(UNITS[idx]);
      
      configure(stages[idx], enabled);
    }
    
    // Last stage remains selected.
  }
  
  public final boolean apply(TextureStage stage) {
    for (int idx = 0; idx < stages.length; idx++) {
      if (stages[idx] == stage) {
        glActiveTexture      (UNITS[idx]);
        glClientActiveTexture(UNITS[idx]);
        
        return true;
      }
    }
    
    return false;
  }
}
