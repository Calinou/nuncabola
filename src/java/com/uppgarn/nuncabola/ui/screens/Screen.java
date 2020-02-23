/*
 * Screen.java
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

public abstract class Screen {
  public void enter(Screen from) {
  }
  
  public void paint(float t) {
  }
  
  public void timer(float dt) {
  }
  
  public void keyDown(int code, char ch) {
  }
  
  public void keyUp(int code, char ch, char origCh) {
  }
  
  public void mouseMove(int x, int y, int dx, int dy) {
  }
  
  public void mouseDown(int button) {
  }
  
  public void mouseUp(int button) {
  }
  
  public void mouseWheel(int wheel) {
  }
  
  public void controllerMove(int axis, float value, boolean recentered) {
  }
  
  public void controllerDown(int button) {
  }
  
  public void controllerUp(int button) {
  }
  
  public void textEntered(char ch) {
  }
  
  public void windowDeactivated() {
  }
  
  public void exitRequested() {
  }
  
  public void leave(Screen to) {
  }
}
