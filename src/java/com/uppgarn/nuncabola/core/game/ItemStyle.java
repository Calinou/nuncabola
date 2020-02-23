/*
 * ItemStyle.java
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

package com.uppgarn.nuncabola.core.game;

import com.uppgarn.nuncabola.core.solid.*;

public enum ItemStyle {
  COIN1,
  COIN5,
  COIN10,
  GROW,
  SHRINK;
  
  public static ItemStyle getStyle(Item item) {
    switch (item.type) {
      case ItemBase.COIN: {
        if (item.value >= 10) {
          return COIN10;
        } else if (item.value >= 5) {
          return COIN5;
        } else {
          return COIN1;
        }
      }
      case ItemBase.GROW: {
        return GROW;
      }
      case ItemBase.SHRINK: {
        return SHRINK;
      }
      
      default: {
        return null;
      }
    }
  }
}
