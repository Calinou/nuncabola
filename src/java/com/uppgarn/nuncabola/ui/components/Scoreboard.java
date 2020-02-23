/*
 * Scoreboard.java
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
import com.uppgarn.nuncabola.core.level.*;

import java.util.*;

public final class Scoreboard {
  private static final Map<ScoreType, String> TYPE_STRS = createTypeStrings();
  
  private static Map<ScoreType, String> createTypeStrings() {
    Map<ScoreType, String> strs = new EnumMap<>(ScoreType.class);
    
    strs.put(ScoreType.MOST_COINS,  "Most Coins");
    strs.put(ScoreType.BEST_TIME,   "Best Time");
    strs.put(ScoreType.FAST_UNLOCK, "Fast Unlock");
    
    return strs;
  }
  
  private final List<ScoreType> types;
  private final int             rowCount;
  private final Object          typeToken;
  private final boolean         changePlayer;
  private final Object          changePlayerToken;
  
  private ScoreTables  tables;
  private ScoreResults results;
  private ScoreType    type;
  
  private TextLabel     typeLbl;
  private TimeLabel  [] timeLbls;
  private TextLabel  [] playerLbls;
  private NumberLabel[] coinsLbls;
  private Button     [] btns;
  
  public Scoreboard(
      GUI             gui,
      Container       parent,
      List<ScoreType> types,
      boolean         extraRow,
      Object          typeToken,
      boolean         changePlayer,
      Object          changePlayerToken) {
    this.types             = new ArrayList<>(types);
    this.rowCount          = ScoreTable.SIZE + (extraRow ? 1 : 0);
    this.typeToken         = typeToken;
    this.changePlayer      = changePlayer;
    this.changePlayerToken = changePlayerToken;
    
    tables  = null;
    results = null;
    type    = types.get(0);
    
    create(gui, parent);
  }
  
  private void createTable(GUI gui, Container parent) {
    Container c0 = gui.vStack(parent);
    
    {
      Container c1 = gui.vStack(c0, Corners.ALL);
      
      {
        // Title.
        
        typeLbl = gui.textLabel(c1);
        
        // Times/Players/Coins.
        
        timeLbls   = new TimeLabel  [rowCount];
        playerLbls = new TextLabel  [rowCount];
        coinsLbls  = new NumberLabel[rowCount];
        
        for (int idx = 0; idx < rowCount; idx++) {
          Container c2;
          
          if (idx < ScoreTable.SIZE) {
            c2 = gui.hStack(c1);
          } else {
            gui.space(c0);
            
            c2 = gui.hStack(c0, Corners.ALL);
          }
          
          {
            String[] playerLblStrs = {"12345678"};
            
            timeLbls  [idx] = gui.timeLabel  (c2);
            
            playerLbls[idx] = gui.textLabel  (c2, Font.SMALL, playerLblStrs);
            playerLbls[idx].setTruncation(Truncation.END);
            
            coinsLbls [idx] = gui.numberLabel(c2, Font.SMALL, 100000);
          }
        }
      }
    }
  }
  
  private void createButtons(GUI gui, Container parent) {
    Container c0 = gui.vStack(parent);
    
    {
      gui.filler(c0);
      
      btns = new Button[types.size()];
      
      for (int idx = 0; idx < types.size(); idx++) {
        ScoreType type = types.get(idx);
        
        btns[idx] = gui.button(c0);
        btns[idx].setText  (TYPE_STRS.get(type));
        btns[idx].setTokens(typeToken, type);
      }
      
      if (changePlayer) {
        gui.space(c0);
        
        Button btn = gui.button(c0);
        btn.setText ("Change Name");
        btn.setToken(changePlayerToken);
      }
      
      gui.filler(c0);
    }
  }
  
  private void create(GUI gui, Container parent) {
    Container c0 = gui.hStack(parent);
    
    {
      gui.filler(c0);
      
      createTable(gui, c0);
      
      gui.space(c0);
      
      createButtons(gui, c0);
      
      gui.filler(c0);
    }
    
    update();
  }
  
  private void updateTable() {
    ScoreTable  table  = (tables  == null) ? null : tables .getTable (type);
    ScoreResult result = (results == null) ? null : results.getResult(type);
    
    // Title.
    
    if (table == null) {
      typeLbl.setText("Unavailable");
    } else {
      typeLbl.setText(TYPE_STRS.get(type));
    }
    
    // Entries.
    
    for (int idx = 0; idx < rowCount; idx++) {
      ScoreEntry entry;
      
      if (idx < ScoreTable.SIZE) {
        if (table == null) {
          entry = null;
        } else {
          entry = table.getEntry(idx);
        }
      } else {
        if (result == null) {
          entry = null;
        } else if (result.isRecord()) {
          entry = result.getRemovedEntry();
        } else {
          entry = result.getEntry();
        }
      }
      
      if (entry == null) {
        // Empty line.
        
        timeLbls  [idx].setTime  (-1);
        playerLbls[idx].setText  ("");
        coinsLbls [idx].setNumber(-1);
      } else {
        // Filled line.
        
        timeLbls  [idx].setTime  (entry.getScore().getTime());
        playerLbls[idx].setText  (entry.getPlayer());
        coinsLbls [idx].setNumber(entry.getScore().getCoins());
        
        if ((idx == ScoreTable.SIZE) && !result.isRecord()) {
          playerLbls[idx].setColor (Color.RED);
        } else if ((result != null) && (idx == result.getRank())) {
          playerLbls[idx].setColor (Color.GREEN);
        } else {
          playerLbls[idx].setColors(Color.WHITE, Color.YELLOW);
        }
      }
    }
  }
  
  private void updateButtons() {
    for (int idx = 0; idx < types.size(); idx++) {
      ScoreType type = types.get(idx);
      
      Color color;
      
      if ((results != null) && results.getResult(type).isRecord()) {
        color = Color.GREEN;
      } else {
        color = null;
      }
      
      btns[idx].setColor   (color);
      btns[idx].setSelected(type == this.type);
    }
  }
  
  private void update() {
    updateTable();
    updateButtons();
  }
  
  public void setContents(ScoreTables tables) {
    setContents(tables, null);
  }
  
  public void setContents(ScoreTables tables, ScoreResults results) {
    this.tables  = tables;
    this.results = results;
    
    update();
  }
  
  public void setType(ScoreType type) {
    this.type = type;
    
    update();
  }
}
