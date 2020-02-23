/*
 * ItemBase.java
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

public final class ItemBase {
  public static final int NONE   = 0;
  public static final int COIN   = 1;
  public static final int GROW   = 2;
  public static final int SHRINK = 3;
  
  public static final float RADIUS = 0.15f;
  
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
  
  public ItemBase() {
    p = new Vector3();
    
    type  = NONE;
    value = 0;
  }
}
