/*
 * Navigation.java
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

import com.uppgarn.codelibf.util.*;

public final class Navigation {
  private final GUI    gui;
  private final Object token;
  private final Object disabledToken;
  
  private final int pageIdx;
  private final int pageCount;
  
  private Button prevBtn;
  private Button nextBtn;
  
  public Navigation(
      GUI       gui,
      Container parent,
      Object    token,
      Object    disabledToken,
      int       elemIdx,
      int       elemCount,
      int       elemStep) {
    this.gui           = gui;
    this.token         = token;
    this.disabledToken = disabledToken;
    
    pageIdx   = elemIdx / elemStep;
    pageCount = Math.max(elemCount - 1, 0) / elemStep + 1;
    
    create(gui, parent);
  }
  
  private void create(GUI gui, Container parent) {
    if (pageCount == 1) {
      return;
    }
    
    Container c0 = gui.hStack(parent);
    
    {
      final char ch00 = '\u25C0', ch01 = '<';
      char       ch0  = GUIHome.canDisplay(Font.SMALL, ch00) ? ch00 : ch01;
      
      prevBtn = gui.button(c0);
      prevBtn.setText(" " + ch0 + " ");
      
      if (pageIdx > 0) {
        prevBtn.setTokens(token, -1);
      } else {
        prevBtn.setColor(Color.GRAY);
        prevBtn.setToken(disabledToken);
      }
      
      int      digitCount = Integer.toString(pageCount).length();
      String   digitStr   = StringTool.times('9', digitCount);
      String[] lblStrs    = {digitStr + "/" + digitStr};
      
      TextLabel lbl = gui.textLabel(c0, Font.SMALL, lblStrs);
      lbl.setText ((pageIdx + 1) + "/" + pageCount);
      lbl.setColor(Color.WHITE);
      
      final char ch10 = '\u25B6', ch11 = '>';
      char       ch1  = GUIHome.canDisplay(Font.SMALL, ch10) ? ch10 : ch11;
      
      nextBtn = gui.button(c0);
      nextBtn.setText(" " + ch1 + " ");
      
      if (pageIdx < pageCount - 1) {
        nextBtn.setTokens(token, +1);
      } else {
        nextBtn.setColor(Color.GRAY);
        nextBtn.setToken(disabledToken);
      }
    }
  }
  
  private Button getButton(int dir) {
    if (dir < 0) {
      return prevBtn;
    } else if (dir > 0) {
      return nextBtn;
    }
    
    return null;
  }
  
  public void setFocus(int dir) {
    Button btn = getButton(dir);
    
    if (btn != null) {
      gui.setFocusWidget(btn);
    }
  }
}
