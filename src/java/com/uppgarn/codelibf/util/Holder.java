/*
 * Holder.java
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

public final class Holder<T> {
  private T obj;
  
  public Holder() {
    this(null);
  }
  
  public Holder(T obj) {
    this.obj = obj;
  }
  
  public T get() {
    return obj;
  }
  
  public void set(T obj) {
    this.obj = obj;
  }
}
