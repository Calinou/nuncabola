/*
 * Item.java
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

package com.uppgarn.nuncabola.core.solid;

import com.uppgarn.nuncabola.core.math.*;

public final class Item {
  private static final ItemBase DEFAULT_BASE = new ItemBase();
  
  /**
   * Position.
   */
  public Vector3 p;
  
  /**
   * Type.
   */
  public int type;
  
  /**
   * Value.
   */
  public int value;
  
  public Item() {
    this(DEFAULT_BASE);
  }
  
  public Item(ItemBase base) {
    p = new Vector3(base.p);
    
    type  = base.type;
    value = base.value;
  }
  
  public Item(Item src) {
    p = new Vector3(src.p);
    
    type  = src.type;
    value = src.value;
  }
  
  public void copyFrom(Item src) {
    p.copyFrom(src.p);
    
    type  = src.type;
    value = src.value;
  }
}
