/*
 * MiniKeyboard.java
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

package com.uppgarn.nuncabola.ui.components;

import com.uppgarn.nuncabola.core.gui.*;

public final class MiniKeyboard {
  private final Object characterToken;
  private final Object backspaceToken;
  private final Object capsLockToken;
  
  private boolean capsLock;
  
  private Button[] btns;
  
  public MiniKeyboard(
      GUI       gui,
      Container parent,
      Object    characterToken,
      Object    backspaceToken,
      Object    capsLockToken) {
    this.characterToken = characterToken;
    this.backspaceToken = backspaceToken;
    this.capsLockToken  = capsLockToken;
    
    capsLock = true;
    
    create(gui, parent);
  }
  
  private void create(GUI gui, Container parent) {
    Container c0 = gui.hStack(parent);
    
    {
      gui.filler(c0);
      
      Container c1 = gui.vStack(c0);
      
      btns = new Button[26];
      
      {
        Container c2 = gui.hStack(c1);
        
        {
          gui.filler(c2);
          
          for (char ch = '0'; ch <= '9'; ch++) {
            Button btn = gui.button(c2);
            btn.setText  (Character.toString(ch));
            btn.setTokens(characterToken, ch);
          }
          
          gui.filler(c2);
        }
        
        c2 = gui.hStack(c1);
        
        {
          gui.filler(c2);
          
          for (char ch = 'A'; ch <= 'J'; ch++) {
            btns[ch - 'A'] = gui.button(c2);
          }
          
          gui.filler(c2);
        }
        
        c2 = gui.hStack(c1);
        
        {
          gui.filler(c2);
          
          for (char ch = 'K'; ch <= 'T'; ch++) {
            btns[ch - 'A'] = gui.button(c2);
          }
          
          gui.filler(c2);
        }
        
        c2 = gui.hStack(c1);
        
        {
          Button capsBtn = gui.button(c2, Font.SMALL, null, true);
          capsBtn.setText ("caps");
          capsBtn.setToken(capsLockToken);
          
          for (char ch = 'U'; ch <= 'Z'; ch++) {
            btns[ch - 'A'] = gui.button(c2);
          }
          
          final char ch0 = '\u2190', ch1 = '<';
          char       ch  = GUIHome.canDisplay(Font.SMALL, ch0) ? ch0 : ch1;
          
          Button bsBtn = gui.button(c2);
          bsBtn.setText (Character.toString(ch));
          bsBtn.setToken(backspaceToken);
        }
      }
      
      gui.filler(c0);
    }
    
    update();
  }
  
  private void update() {
    for (char ch = 'A'; ch <= 'Z'; ch++) {
      int  idx   = ch - 'A';
      char btnCh = capsLock ? ch : Character.toLowerCase(ch);
      
      btns[idx].setText  (Character.toString(btnCh));
      btns[idx].setTokens(characterToken, btnCh);
    }
  }
  
  public void toggleCapsLock() {
    capsLock = !capsLock;
    
    update();
  }
}
