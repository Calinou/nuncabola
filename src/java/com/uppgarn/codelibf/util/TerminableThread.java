/*
 * TerminableThread.java
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

public abstract class TerminableThread extends Thread {
  private final Object interruptionLock = new Object();
  
  protected TerminableThread() {
  }
  
  protected TerminableThread(String name) {
    super(name);
  }
  
  protected final Object getInterruptionLock() {
    return interruptionLock;
  }
  
  @Override
  public abstract void run();
  
  @Override
  public void interrupt() {
    synchronized (interruptionLock) {
      super.interrupt();
    }
  }
  
  public final void terminate() {
    if (Thread.currentThread() == this) {
      throw new IllegalStateException();
    }
    
    interrupt();
    
    boolean currentThreadInterrupted = false;
    
    while (isAlive()) {
      try {
        join();
      } catch (InterruptedException ex) {
        currentThreadInterrupted = true;
      }
    }
    
    if (currentThreadInterrupted) {
      Thread.currentThread().interrupt();
    }
  }
}
