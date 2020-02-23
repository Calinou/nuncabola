/*
 * FutureHolder.java
 *
 * Copyright (c) 1998-2020 Florian Priester
 *
 * CodeLibF is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */

package com.uppgarn.codelibf.util;

public final class FutureHolder<T> {
  private boolean ready;
  
  private T obj;
  
  public FutureHolder() {
    ready = false;
  }
  
  public FutureHolder(T obj) {
    ready = true;
    
    this.obj = obj;
  }
  
  public synchronized boolean isReady() {
    return ready;
  }
  
  public synchronized T get() {
    if (!ready) {
      throw new IllegalStateException();
    }
    
    return obj;
  }
  
  public synchronized void set(T obj) {
    if (ready) {
      throw new IllegalStateException();
    }
    
    ready = true;
    
    this.obj = obj;
  }
}
