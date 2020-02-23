/*
 * LevelParser.java
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

package com.uppgarn.nuncabola.core.level;

import com.uppgarn.nuncabola.core.solid.*;

public final class LevelParser {
  private static void setVersion(Level level, Meta meta) {
    String   str   = meta.get("version");
    String[] parts = (str == null) ? new String[0] : str.trim().split("\\.");
    
    try {
      if (parts.length > 0) {
        level  .setMajorVersion(Math.max(Integer.parseInt(parts[0]), 0));
        
        if (parts.length > 1) {
          level.setMinorVersion(Math.max(Integer.parseInt(parts[1]), 0));
        }
      }
    } catch (NumberFormatException ex) {
    }
  }
  
  private static void setShotPath(Level level, Meta meta) {
    String path = meta.get("shot");
    
    if (path != null) {
      level.setShotPath(path);
    }
  }
  
  private static void setBackgroundSolidPath(Level level, Meta meta) {
    String path = meta.get("back");
    
    if (path != null) {
      level.setBackgroundSolidPath(path);
    }
  }
  
  private static void setBackgroundGradientPath(Level level, Meta meta) {
    String path = meta.get("grad");
    
    if (path != null) {
      level.setBackgroundGradientPath(path);
    }
  }
  
  private static void setMusicPath(Level level, Meta meta) {
    String path = meta.get("song");
    
    if (path != null) {
      level.setMusicPath(path);
    }
  }
  
  private static void setTime(Level level, Meta meta) {
    String str = meta.get("time");
    
    if (str != null) {
      try {
        level.setTime(
          Math.min(Math.max(Integer.parseInt(str.trim()), 0), Level.MAX_TIME));
      } catch (NumberFormatException ex) {
      }
    }
  }
  
  private static void setGoal(Level level, Meta meta) {
    String str = meta.get("goal");
    
    if (str != null) {
      try {
        level.setGoal(Math.max(Integer.parseInt(str.trim()), 0));
      } catch (NumberFormatException ex) {
      }
    }
  }
  
  private static void setMessage(Level level, Meta meta) {
    String msg = meta.get("message");
    
    if (msg != null) {
      // Convert backslash newlines to '\n'.
      // Remove newlines at start and end of message.
      
      StringBuilder strBld = new StringBuilder(msg.length());
      
      int newlineCount = 0;
      
      for (int idx = 0; idx < msg.length(); idx++) {
        char ch = msg.charAt(idx);
        
        if (ch == '\\') {
          newlineCount++;
        } else {
          if (idx == newlineCount) {
            newlineCount = 0;
          } else {
            for (; newlineCount > 0; newlineCount--) {
              strBld.append('\n');
            }
          }
          
          strBld.append(ch);
        }
      }
      
      level.setMessage(strBld.toString());
    }
  }
  
  private static void setBonus(Level level, Meta meta) {
    String str = meta.get("bonus");
    
    if (str != null) {
      try {
        level.setBonus(Integer.parseInt(str.trim()) != 0);
      } catch (NumberFormatException ex) {
        level.setBonus(false);
      }
    }
  }
  
  private static void fillDefaultScoreTables(Level level, Meta meta) {
    for (ScoreType type: Level.SCORE_TYPES) {
      String key;
      
      switch (type) {
        case MOST_COINS: {
          key = "coin_hs";
          
          break;
        }
        case BEST_TIME: {
          key = "time_hs";
          
          break;
        }
        case FAST_UNLOCK: {
          key = "goal_hs";
          
          break;
        }
        
        default: {
          throw new AssertionError();
        }
      }
      
      String   str   = meta.get(key);
      String[] parts = (str == null) ? new String[0] : str.trim().split("\\s+");
      
      for (int rank = 0; rank < ScoreTable.SIZE; rank++) {
        int time  = (level.getTime() == 0) ? Level.MAX_TIME : level.getTime();
        int coins = (!type.isGoalUnlockingRequired() && type.isTimeBased())
                    ? 0 : level.getGoal();
        
        if (rank < parts.length) {
          try {
            int value = Integer.parseInt(parts[rank]);
            
            if (type.isTimeBased()) {
              time  = Math.min(Math.max(value, 0), time);
            } else {
              coins = Math.max(value, coins);
            }
          } catch (NumberFormatException ex) {
          }
        }
        
        ScoreEntry entry = new ScoreEntry(
          new Score(time, coins),
          ScoreTable.getDefaultPlayer(rank));
        
        level.getDefaultScoreTables().getTable(type).setEntry(rank, entry);
      }
    }
  }
  
  public static Level parse(Meta meta, String solidPath) {
    Level level = new Level();
    
    setVersion               (level, meta);
    setShotPath              (level, meta);
    setBackgroundSolidPath   (level, meta);
    setBackgroundGradientPath(level, meta);
    setMusicPath             (level, meta);
    setTime                  (level, meta);
    setGoal                  (level, meta);
    setMessage               (level, meta);
    setBonus                 (level, meta);
    
    fillDefaultScoreTables(level, meta);
    
    level.setSolidPath(solidPath);
    
    return level;
  }
  
  private LevelParser() {
  }
}
