/*
 * TitleFuncs.java
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

import java.nio.file.*;

public final class TitleFuncs {
  private static Stage stage;
  
  public static void initialize() {
    stage = null;
    
    gotoStage(Stage.TITLE);
  }
  
  private static void gotoStage(Stage newStage) {
    if (stage != null) {
      stage.leave();
    }
    
    stage = newStage;
    
    if (stage != null) {
      stage.enter();
    }
  }
  
  public static void step(float dt) {
    stage.timer(dt);
  }
  
  public static void deinitialize() {
    gotoStage(null);
  }
  
  private TitleFuncs() {
  }
  
  private enum Stage {
    /**
     * Pan across title level.
     */
    TITLE() {
      private boolean enabled;
      
      private GameFlyer gameFlyer;
      private float     panTime;
      
      @Override
      public void enter() {
        try {
          GameFuncs.load("map-medium/title.sol");
          
          enabled = true;
        } catch (FuncsException ex) {
          enabled = false;
          
          return;
        }
        
        GameFuncs.getGame().goalsUnlocked = true;
        GameFuncs.getGame().goalFactor    = 1.0f;
        
        gameFlyer = new GameFlyer(
          GameFuncs.getGame(),
          GameFuncs.getViewDistance());
        gameFlyer.fly(1.0f);
        
        panTime = 0.0f;
        
        FadeFuncs.initialize(FadeFuncs.Mode.IN, 0.5f);
      }
      
      @Override
      public void timer(float dt) {
        if (!enabled) {
          return;
        }
        
        final float duration = 20.0f;
        
        panTime += dt;
        
        if (panTime <= duration) {
          gameFlyer.fly((float) Math.cos((float) Math.PI * panTime / duration));
          
          FadeFuncs.step(dt);
        } else {
          gotoStage(TITLE_FADE_OUT);
        }
      }
      
      @Override
      public void leave() {
        if (!enabled) {
          return;
        }
        
        FadeFuncs.deinitialize();
        
        gameFlyer = null;
      }
    },
    
    /**
     * Fade out. Load random replay.
     */
    TITLE_FADE_OUT() {
      @Override
      public void enter() {
        FadeFuncs.initialize(FadeFuncs.Mode.OUT, 1.0f);
      }
      
      @Override
      public void timer(float dt) {
        if (!FadeFuncs.isOver()) {
          FadeFuncs.step(dt);
        } else {
          ReplayListFuncs.initialize();
          
          Path file = ReplayListFuncs.getRandomFile();
          
          ReplayListFuncs.deinitialize();
          
          if (file != null) {
            try {
              ReplayFuncs.initialize(file, false);
              
              if (GameFuncs.getGame().levelCompatible) {
                gotoStage(REPLAY);
              } else {
                ReplayFuncs.deinitialize();
                
                gotoStage(TITLE);
              }
            } catch (FuncsException ex) {
              gotoStage(TITLE);
            }
          } else {
            gotoStage(TITLE);
          }
        }
      }
      
      @Override
      public void leave() {
        FadeFuncs.deinitialize();
      }
    },
    
    /**
     * Run replay.
     */
    REPLAY() {
      @Override
      public void enter() {
        FadeFuncs.initialize(FadeFuncs.Mode.IN, 1.0f);
      }
      
      @Override
      public void timer(float dt) {
        if (!ReplayFuncs.isOver() || !FadeFuncs.isOver()) {
          ReplayFuncs.step(dt);
          FadeFuncs  .step(dt);
        } else {
          gotoStage(REPLAY_FADE_OUT);
        }
      }
      
      @Override
      public void leave() {
        FadeFuncs  .deinitialize();
        ReplayFuncs.deinitialize();
      }
    },
    
    /**
     * Fade out. Return to title level.
     */
    REPLAY_FADE_OUT() {
      @Override
      public void enter() {
        FadeFuncs.initialize(FadeFuncs.Mode.OUT, 1.0f);
      }
      
      @Override
      public void timer(float dt) {
        if (!FadeFuncs.isOver()) {
          FadeFuncs.step(dt);
        } else {
          gotoStage(TITLE);
        }
      }
      
      @Override
      public void leave() {
        FadeFuncs.deinitialize();
      }
    };
    
    public void enter() {
    }
    
    public void timer(float dt) {
    }
    
    public void leave() {
    }
  }
}
