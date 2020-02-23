/*
 * ReplayListScreen.java
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

import com.uppgarn.nuncabola.core.audio.*;
import com.uppgarn.nuncabola.core.game.*;
import com.uppgarn.nuncabola.core.gui.*;
import com.uppgarn.nuncabola.core.replay.*;
import com.uppgarn.nuncabola.functions.*;
import com.uppgarn.nuncabola.ui.*;
import com.uppgarn.nuncabola.ui.components.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

public final class ReplayListScreen extends MenuScreen {
  private static final int ROW_COUNT    = 2;
  private static final int COLUMN_COUNT = 4;
  private static final int PAGE_SIZE    = ROW_COUNT * COLUMN_COUNT;
  
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm:ss")
    .withZone(ZoneId.systemDefault());
  
  private static final Map<Status, String> STATUS_STRS = createStatusStrings();
  
  public static final ReplayListScreen INSTANCE = new ReplayListScreen();
  
  private static Map<Status, String> createStatusStrings() {
    Map<Status, String> strs = new EnumMap<>(Status.class);
    
    strs.put(Status.NONE,     "Aborted");
    strs.put(Status.GOAL,     "Success");
    strs.put(Status.FALL_OUT, "Fall-out");
    strs.put(Status.TIME_OUT, "Time-out");
    
    return strs;
  }
  
  private int first;
  private int selected;
  
  private int total;
  
  private StringBuilder patternStrBld;
  private long          patternLastTime;
  
  private Navigation  nav;
  private Widget[]    btns;
  private TextLabel   nameLbl;
  private TextLabel   playerLbl;
  private TextLabel   dateLbl;
  private TimeLabel   timeLbl;
  private NumberLabel coinsLbl;
  private TextLabel   statusLbl;
  
  private ReplayListScreen() {
    first    = 0;
    selected = 0;
  }
  
  @Override
  public void enter(Screen from) {
    if (from != this) {
      ReplayListFuncs.initialize();
      
      total    = ReplayListFuncs.getReplayCount();
      first    = (Math.min(first, Math.max(0, total - 1)) / PAGE_SIZE)
                   * PAGE_SIZE;
      selected = Math.min(selected, Math.max(0, total - 1));
      
      patternStrBld   = new StringBuilder();
      patternLastTime = 0;
    }
    
    super.enter(from);
    
    Audio.fadeToMusic("bgm/inter.ogg", 0.5f);
  }
  
  private Widget createReplayButton(GUI gui, Container parent, int idx) {
    String     shortName = ReplayListFuncs.getShortName(idx);
    ReplayInfo info      = ReplayListFuncs.getInfo     (idx);
    
    Container c0 = gui.vStack(parent);
    
    {
      gui.space(c0);
      
      ImageLabel lbl = gui.imageLabel(c0, 0.2f, 0.2f);
      lbl.setImagePath((info == null) ? null : info.getShotPath());
      
      String[] btnStrs = {"abcd-00_00"};
      
      Button btn = gui.button(c0, Font.SMALL, btnStrs);
      btn.setTruncation(Truncation.END);
      btn.setText      (shortName);
    }
    
    c0.setTokens(Action.REPLAY, idx);
    
    return c0;
  }
  
  private void createReplayInfoBox(GUI gui, Container parent) {
    Container c0 = gui.hStack(parent);
    
    {
      Container c1 = gui.hStack(c0, Corners.ALL, true);
      
      {
        Container c2 = gui.vStack(c1);
        
        {
          TextLabel lbl0 = gui.textLabel(c2);
          lbl0.setText ("Replay");
          lbl0.setColor(Color.WHITE);
          
          TextLabel lbl1 = gui.textLabel(c2);
          lbl1.setText ("Player");
          lbl1.setColor(Color.WHITE);
          
          TextLabel lbl2 = gui.textLabel(c2);
          lbl2.setText ("Date");
          lbl2.setColor(Color.WHITE);
        }
        
        c2 = gui.vStack(c1, Corners.NONE, true);
        
        {
          String[] nameLblStrs   = {""};
          String[] playerLblStrs = {""};
          String[] dateLblStrs   = {"1970-01-01 00:00:00 "}; // leave some room
          
          nameLbl   = gui.textLabel(c2, Font.SMALL, nameLblStrs);
          nameLbl  .setTruncation(Truncation.END);
          
          playerLbl = gui.textLabel(c2, Font.SMALL, playerLblStrs);
          playerLbl.setTruncation(Truncation.END);
          
          dateLbl   = gui.textLabel(c2, Font.SMALL, dateLblStrs);
        }
      }
      
      gui.space(c0);
      
      c1 = gui.hStack(c0, Corners.ALL);
      
      {
        Container c2 = gui.vStack(c1);
        
        {
          TextLabel lbl0 = gui.textLabel(c2);
          lbl0.setText ("Time");
          lbl0.setColor(Color.WHITE);
          
          TextLabel lbl1 = gui.textLabel(c2);
          lbl1.setText ("Coins");
          lbl1.setColor(Color.WHITE);
          
          TextLabel lbl2 = gui.textLabel(c2);
          lbl2.setText ("Status");
          lbl2.setColor(Color.WHITE);
        }
        
        c2 = gui.vStack(c1);
        
        {
          String[] statusLblStrs = STATUS_STRS.values().toArray(
            new String[STATUS_STRS.size()]);
          
          timeLbl   = gui.timeLabel  (c2);
          coinsLbl  = gui.numberLabel(c2, Font.SMALL, 1000);
          statusLbl = gui.textLabel  (c2, Font.SMALL, statusLblStrs);
        }
      }
    }
  }
  
  private void updateReplayInfoBox() {
    nameLbl.setText(ReplayListFuncs.getName(selected));
    
    if (ReplayListFuncs.isValidReplay(selected)) {
      // Valid replay.
      
      ReplayInfo info = ReplayListFuncs.getInfo(selected);
      
      playerLbl.setText  (info.getPlayer());
      dateLbl  .setText  (DATE_FORMATTER.format(info.getDate()));
      timeLbl  .setTime  (info.getTime());
      coinsLbl .setNumber(info.getCoins());
      statusLbl.setText  (STATUS_STRS.get(info.getStatus()));
      
      if (info.getStatus() == Status.GOAL) {
        statusLbl.setColor(Color.GREEN);
      } else {
        statusLbl.setColor(Color.RED);
      }
    } else {
      // Invalid replay.
      
      playerLbl.setText  ("");
      dateLbl  .setText  ("");
      timeLbl  .setTime  (-1);
      coinsLbl .setNumber(-1);
      statusLbl.setText  ("");
    }
  }
  
  @Override
  protected void configureGUI(GUI gui) {
    if (total == 0) {
      Container c0 = gui.vStack(null);
      
      {
        TextLabel lbl = gui.textLabel(c0, Font.MEDIUM);
        lbl.setText("No Replays");
        
        gui.space(c0);
        
        Button btn = gui.button(c0);
        btn.setText ("Back");
        btn.setToken(Action.BACK);
        
        gui.setFocusWidget(btn);
      }
      
      return;
    }
    
    Container c0 = gui.vStack(null);
    
    {
      Container c1 = gui.hStack(c0);
      
      {
        Button btn = gui.button(c1);
        btn.setText ("Back");
        btn.setToken(Action.BACK);
        
        gui.setFocusWidget(btn);
        
        gui.space(c1);
        
        nav = new Navigation(
          gui,
          c1,
          Action.NAVIGATE,
          Action.DISABLED,
          first,
          total,
          PAGE_SIZE);
        
        gui.space(c1, true);
        
        TextLabel lbl = gui.textLabel(c1);
        lbl.setText("Select Replay");
      }
      
      c1 = gui.vArray(c0);
      
      btns = new Widget[PAGE_SIZE];
      
      for (int row = 0, idx = first; row < ROW_COUNT; row++) {
        Container c2 = gui.hArray(c1);
        
        for (int col = 0; col < COLUMN_COUNT; col++) {
          if (idx < total) {
            btns[idx - first] = createReplayButton(gui, c2, idx);
          } else {
            gui.space(c2);
          }
          
          idx++;
        }
      }
      
      gui.space(c0);
      
      createReplayInfoBox(gui, c0);
    }
    
    updateReplayInfoBox();
  }
  
  @Override
  protected void performFocus(Widget widget) {
    super.performFocus(widget);
    
    switch ((Action) widget.getToken0()) {
      case REPLAY: {
        selected = (Integer) widget.getToken1();
        
        updateReplayInfoBox();
        
        break;
      }
      
      default: {
        break;
      }
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
      case NAVIGATE: {
        int dir = (Integer) token1;
        
        first += PAGE_SIZE * dir;
        
        selected = first;
        
        UI.gotoScreen(this);
        
        nav.setFocus(dir);
        
        break;
      }
      case NAVIGATE_INDEX: {
        int idx = (Integer) token1;
        
        selected = idx;
        
        int newFirst = (idx / PAGE_SIZE) * PAGE_SIZE;
        
        if (first != newFirst) {
          first = newFirst;
          
          UI.gotoScreen(this);
        } else {
          updateReplayInfoBox();
        }
        
        getGUI().setFocusWidget(btns[idx - first]);
        
        break;
      }
      case REPLAY: {
        int idx = (Integer) token1;
        
        if (ReplayListFuncs.isValidReplay(idx)) {
          try {
            ReplayFuncs.initialize(ReplayListFuncs.getFile(idx));
            
            selected = idx;
            
            if (GameFuncs.getGame().levelCompatible) {
              UI.gotoScreen(ReplayIntroScreen  .INSTANCE);
            } else {
              UI.gotoScreen(ReplayWarningScreen.INSTANCE);
            }
          } catch (FuncsException ex) {
            // Stay in this screen.
          }
        }
        
        break;
      }
      case DISABLED: {
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
  public void textEntered(char ch) {
    if (total == 0) {
      return;
    }
    
    if (System.currentTimeMillis() - patternLastTime >= 1000) { // 1s
      patternStrBld.setLength(0);
    }
    if (!((patternStrBld.length() == 1)
        && (Character.toLowerCase(patternStrBld.charAt(0))
          == Character.toLowerCase(ch)))) {
      patternStrBld.append(ch);
    }
    
    int startIdx = selected;
    
    if (patternStrBld.length() == 1) {
      Widget widget = getGUI().getFocusWidget();
      
      if ((widget.getToken0() == Action.REPLAY)
          && ((Integer) widget.getToken1() == startIdx)) {
        startIdx = (startIdx + 1) % total;
      }
    }
    
    int idx = ReplayListFuncs.getIndex(startIdx, patternStrBld.toString());
    
    if (idx != -1) {
      performAction(Action.NAVIGATE_INDEX, idx);
    } else {
      performAction(Action.DISABLED);
    }
    
    patternLastTime = System.currentTimeMillis();
  }
  
  @Override
  public void exitRequested() {
    performAction(Action.BACK);
  }
  
  @Override
  public void leave(Screen to) {
    super.leave(to);
    
    if (to != this) {
      ReplayListFuncs.deinitialize();
      
      patternStrBld = null;
    }
    
    nav       = null;
    btns      = null;
    nameLbl   = null;
    playerLbl = null;
    dateLbl   = null;
    timeLbl   = null;
    coinsLbl  = null;
    statusLbl = null;
  }
  
  private enum Action {
    BACK,
    NAVIGATE,
    NAVIGATE_INDEX,
    REPLAY,
    DISABLED
  }
}
