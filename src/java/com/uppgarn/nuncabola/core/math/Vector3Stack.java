/*
 * Vector3Stack.java
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

package com.uppgarn.nuncabola.core.math;

public final class Vector3Stack {
  private Vector3[] array;
  
  private int current;
  
  public Vector3Stack(int size) {
    array = createArray(size);
    
    current = -1;
  }
  
  private Vector3[] createArray(int size) {
    Vector3[] array = new Vector3[size];
    
    for (int idx = 0; idx < size; idx++) {
      array[idx] = new Vector3();
    }
    
    return array;
  }
  
  public Vector3 push() {
    return (++current < array.length) ? array[current] : new Vector3();
  }
  
  public void pop() {
    current--;
  }
}
