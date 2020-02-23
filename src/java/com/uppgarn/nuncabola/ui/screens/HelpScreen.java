/*
 * HelpScreen.java
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

package com.uppgarn.nuncabola.ui.screens;

import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.preferences.*;
import com.uppgarn.nuncabola.ui.*;

import static com.uppgarn.nuncabola.functions.BaseFuncs.*;

public final class HelpScreen extends MenuScreen {
  public static final HelpScreen INSTANCE = new HelpScreen();
  
  private Tab tab;
  
  private HelpScreen() {
  }
  
  @Override
  public void enter(Screen from) {
    if (from == TitleScreen.INSTANCE) {
      tab = Tab.RULES;
    }
    
    super.enter(from);
  }
  
  private void createTabButton(GUI gui, Container parent, Tab tab) {
    boolean selected = tab == this.tab;
    
    Button btn = gui.button(parent);
    btn.setText    (tab.toString());
    btn.setTokens  (Action.TAB, tab);
    btn.setSelected(selected);
    
    if (selected) {
      gui.setFocusWidget(btn);
    }
  }
  
  private void createMenu(GUI gui, Container parent) {
    gui.space(parent);
    
    Container c0 = gui.hStack(parent);
    
    {
      Button btn = gui.button(c0);
      btn.setText ("Back");
      btn.setToken(Action.BACK);
      
      gui.space(c0, true);
      
      Container c1 = gui.hArray(c0);
      
      for (Tab tab: Tab.values()) {
        createTabButton(gui, c1, tab);
      }
    }
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    gui.setXPosition( 0);
    gui.setYPosition(+1);
    
    Container c0 = gui.vStack(null);
    
    {
      createMenu       (gui, c0);
      tab.createWidgets(gui, c0);
    }
  }
  
  @Override
  protected void performAction(Object token0, Object token1) {
    super.performAction(token0, token1);
    
    switch ((Action) token0) {
      case BACK: {
        UI.gotoScreen(TitleScreen.INSTANCE);
        
        break;
      }
      case TAB: {
        tab = (Tab) token1;
        
        UI.gotoScreen(this);
        
        break;
      }
      case DEMO: {
        try {
          ReplayFuncs.initialize(DataFuncs.getSource((String) token1));
          
          UI.gotoScreen(DemoScreen.INSTANCE);
        } catch (FuncsException ex) {
          // Stay in this screen.
        }
        
        break;
      }
    }
  }
  
  @Override
  public void paint(float t) {
    GameFuncs.draw(t);
    
    super.paint(t);
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.BACK);
  }
  
  private enum Tab {
    RULES("Rules") {
      @Override
      public void createWidgets(GUI gui, Container parent) {
        Container c0 = gui.hStack(parent);
        
        {
          gui.filler(c0);
          
          Container c1 = gui.vArray(c0);
          
          {
            Container c2 = gui.vStack(c1);
            
            {
              gui.space(c2);
              
              ImageLabel lbl = gui.imageLabel(c2, 0.3f, 0.3f);
              lbl.setImagePath("gui/help1.jpg");
              
              gui.filler(c2);
            }
            
            c2 = gui.vStack(c1);
            
            {
              gui.space(c2);
              
              ImageLabel lbl = gui.imageLabel(c2, 0.3f, 0.3f);
              lbl.setImagePath("gui/help2.jpg");
              
              gui.filler(c2);
            }
          }
          
          gui.space(c0);
          
          c1 = gui.vArray(c0);
          
          {
            Container c2 = gui.vStack(c1);
            
            {
              gui.space(c2);
              
              String str =
                  "Move the mouse or joystick\n"
                + "or use keyboard arrows to\n"
                + "tilt the floor causing the\n"
                + "ball to roll.";
              
              MultiTextLabel lbl = gui.multiTextLabel(c2);
              lbl.setText (str);
              lbl.setColor(Color.WHITE);
              
              gui.filler(c2);
            }
            
            c2 = gui.vStack(c1);
            
            {
              gui.space(c2);
              
              String str =
                  "Roll over coins to collect\n"
                + "them. Collect coins to\n"
                + "unlock the goal and finish\n"
                + "the level.";
              
              MultiTextLabel lbl = gui.multiTextLabel(c2);
              lbl.setText (str);
              lbl.setColor(Color.WHITE);
              
              gui.filler(c2);
            }
          }
          
          gui.filler(c0);
        }
      }
    },
    
    CONTROLS("Controls") {
      @Override
      public void createWidgets(GUI gui, Container parent) {
        gui.space(parent);
        
        Container c0 = gui.hStack(parent);
        
        {
          gui.filler(c0);
          
          Container c1 = gui.vStack(c0);
          
          {
            Container c2 = gui.vStack(c1, Corners.ALL);
            
            {
              Container c3 = gui.hArray(c2);
              
              {
                String str0 = getKeyPref(Pref.KEY_PAUSE).toString();
                String str1 = "Pause / Release Pointer";
                
                TextLabel lbl0 = gui.textLabel(c3);
                lbl0.setText (str0);
                lbl0.setColor(Color.YELLOW);
                
                TextLabel lbl1 = gui.textLabel(c3);
                lbl1.setText (str1);
                lbl1.setColor(Color.WHITE);
              }
              
              c3 = gui.hArray(c2);
              
              {
                String str0 = "Escape";
                String str1 = "Exit / Cancel Menu";
                
                TextLabel lbl0 = gui.textLabel(c3);
                lbl0.setText (str0);
                lbl0.setColor(Color.YELLOW);
                
                TextLabel lbl1 = gui.textLabel(c3);
                lbl1.setText (str1);
                lbl1.setColor(Color.WHITE);
              }
              
              c3 = gui.hArray(c2);
              
              {
                String str0 = getKeyPref(Pref.KEY_CAMERA_1).toString();
                String str1 = "Chase Camera";
                
                TextLabel lbl0 = gui.textLabel(c3);
                lbl0.setText (str0);
                lbl0.setColor(Color.YELLOW);
                
                TextLabel lbl1 = gui.textLabel(c3);
                lbl1.setText (str1);
                lbl1.setColor(Color.WHITE);
              }
              
              c3 = gui.hArray(c2);
              
              {
                String str0 = getKeyPref(Pref.KEY_CAMERA_2).toString();
                String str1 = "Lazy Camera";
                
                TextLabel lbl0 = gui.textLabel(c3);
                lbl0.setText (str0);
                lbl0.setColor(Color.YELLOW);
                
                TextLabel lbl1 = gui.textLabel(c3);
                lbl1.setText (str1);
                lbl1.setColor(Color.WHITE);
              }
              
              c3 = gui.hArray(c2);
              
              {
                String str0 = getKeyPref(Pref.KEY_CAMERA_3).toString();
                String str1 = "Manual Camera";
                
                TextLabel lbl0 = gui.textLabel(c3);
                lbl0.setText (str0);
                lbl0.setColor(Color.YELLOW);
                
                TextLabel lbl1 = gui.textLabel(c3);
                lbl1.setText (str1);
                lbl1.setColor(Color.WHITE);
              }
              
              c3 = gui.hArray(c2);
              
              {
                String str0 = getKeyPref(Pref.KEY_RESTART).toString();
                String str1 = "Restart Level";
                
                TextLabel lbl0 = gui.textLabel(c3);
                lbl0.setText (str0);
                lbl0.setColor(Color.YELLOW);
                
                TextLabel lbl1 = gui.textLabel(c3);
                lbl1.setText (str1);
                lbl1.setColor(Color.WHITE);
              }
              
              c3 = gui.hArray(c2);
              
              {
                String str0 = "F12";
                String str1 = "Screenshot";
                
                TextLabel lbl0 = gui.textLabel(c3);
                lbl0.setText (str0);
                lbl0.setColor(Color.YELLOW);
                
                TextLabel lbl1 = gui.textLabel(c3);
                lbl1.setText (str1);
                lbl1.setColor(Color.WHITE);
              }
            }
            
            gui.space(c1);
            
            String str =
                "Left and right mouse buttons rotate the view.\n"
              + "Hold "
              + getKeyPref(Pref.KEY_ROTATE_FAST)
              + " for faster view rotation.";
            
            MultiTextLabel lbl = gui.multiTextLabel(c1);
            lbl.setText (str);
            lbl.setColor(Color.WHITE);
          }
          
          gui.filler(c0);
        }
      }
    },
    
    MODES("Modes") {
      @Override
      public void createWidgets(GUI gui, Container parent) {
        gui.space(parent);
        
        Container c0 = gui.hStack(parent);
        
        {
          gui.filler(c0);
          
          Container c1 = gui.vStack(c0);
          
          {
            Container c2 = gui.vStack(c1, Corners.ALL);
            
            {
              String str0 =
                  "Normal Mode";
              String str1 =
                  "Finish a level before the time runs out.\n"
                + "You need to collect coins in order to open the goal.";
              
              TextLabel lbl0 = gui.textLabel(c2);
              lbl0.setText(str0);
              
              MultiTextLabel lbl1 = gui.multiTextLabel(c2);
              lbl1.setText (str1);
              lbl1.setColor(Color.WHITE);
            }
            
            gui.space(c1);
            
            c2 = gui.vStack(c1, Corners.ALL);
            
            {
              String str0 =
                  "Challenge Mode";
              String str1 =
                  "Start playing from the first level of the set.\n"
                + "You start with only three balls, do not lose them.\n"
                + "Earn an extra ball for each 100 coins collected.";
              
              TextLabel lbl0 = gui.textLabel(c2);
              lbl0.setText(str0);
              
              MultiTextLabel lbl1 = gui.multiTextLabel(c2);
              lbl1.setText (str1);
              lbl1.setColor(Color.WHITE);
            }
          }
          
          gui.filler(c0);
        }
      }
    },
    
    TRICKS("Tricks") {
      @Override
      public void createWidgets(GUI gui, Container parent) {
        Container c0 = gui.hStack(parent);
        
        {
          gui.filler(c0);
          
          Container c1 = gui.vArray(c0);
          
          {
            Container c2 = gui.vStack(c1);
            
            {
              gui.space(c2);
              
              String str =
                  "Corners can be used to jump.\n"
                + "Get rolling and take aim\n"
                + "at the angle. You may be able\n"
                + "to reach new places.";
              
              MultiTextLabel lbl = gui.multiTextLabel(c2);
              lbl.setText (str);
              lbl.setColor(Color.WHITE);
              
              gui.filler(c2);
            }
            
            c2 = gui.vStack(c1);
            
            {
              gui.space(c2);
              
              String str =
                  "Tilting in 2 directions increases\n"
                + "the slope. Use the manual camera\n"
                + "and turn the camera by 45\n"
                + "degrees for best results.";
              
              MultiTextLabel lbl = gui.multiTextLabel(c2);
              lbl.setText (str);
              lbl.setColor(Color.WHITE);
              
              gui.filler(c2);
            }
          }
          
          gui.space(c0);
          
          c1 = gui.vArray(c0);
          
          {
            Container c2 = gui.vStack(c1);
            
            {
              gui.space(c2);
              
              ImageLabel lbl = gui.imageLabel(c2, 0.2375f, 0.2375f);
              lbl.setImagePath("gui/help3.jpg");
              
              Button btn = gui.button(c2);
              btn.setText("Watch demo");
              
              gui.filler(c2);
            }
            
            c2.setTokens(Action.DEMO, "gui/demo1.nbr");
            
            c2 = gui.vStack(c1);
            
            {
              gui.space(c2);
              
              ImageLabel lbl = gui.imageLabel(c2, 0.2375f, 0.2375f);
              lbl.setImagePath("gui/help4.jpg");
              
              Button btn = gui.button(c2);
              btn.setText("Watch demo");
              
              gui.filler(c2);
            }
            
            c2.setTokens(Action.DEMO, "gui/demo2.nbr");
          }
          
          gui.filler(c0);
        }
      }
    };
    
    private final String title;
    
    Tab(String title) {
      this.title = title;
    }
    
    public abstract void createWidgets(GUI gui, Container parent);
    
    @Override
    public final String toString() {
      return title;
    }
  }
  
  private enum Action {
    BACK,
    TAB,
    DEMO
  }
}
