/*
 * PlayTimeOutScreen.java
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

public final class PlayTimeOutScreen extends PlayFailureScreen {
  public static final PlayTimeOutScreen INSTANCE = new PlayTimeOutScreen();
  
  private PlayTimeOutScreen() {
  }
  
  @Override
  protected String getMessage() {
    return "Time's Up!";
  }
}
