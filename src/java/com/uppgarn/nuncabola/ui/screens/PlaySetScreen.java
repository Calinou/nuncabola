/*
 * PlaySetScreen.java
 *
 * Copyright (c) 2003-2019 Nuncabola authors
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

public final class PlaySetScreen extends PlayPreparationScreen {
  public static final PlaySetScreen INSTANCE = new PlaySetScreen();
  
  private PlaySetScreen() {
  }
  
  @Override
  protected String getMessage() {
    return "Set?";
  }
  
  @Override
  protected String getSoundPath() {
    return "snd/set.ogg";
  }
  
  @Override
  protected float getFlightPosition() {
    return 0.5f;
  }
  
  @Override
  protected Screen getNextScreen() {
    return PlayMainScreen.INSTANCE;
  }
  
  @Override
  protected boolean isSeamless() {
    return false;
  }
}
