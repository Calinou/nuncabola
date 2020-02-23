/*
 * Input.java
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

public final class Input {
  /**
   * Angle limit of floor tilting.
   */
  public static final float MAX_TILT     = 20.0f;
  
  /**
   * Maximum rate of view rotation.
   */
  public static final float MAX_ROTATION = 10.0f;
  
  private float  x;
  private float  z;
  private float  response;
  private float  rotation;
  private Camera camera;
  private float  cameraSpeed;
  
  public Input() {
    x           = 0.0f;
    z           = 0.0f;
    response    = 0.05f;
    rotation    = 0.0f;
    camera      = Camera.CHASE;
    cameraSpeed = 0.5f;
  }
  
  public float getX() {
    return x;
  }
  
  public void setX(float x) {
    this.x = Math.min(Math.max(x, -MAX_TILT), +MAX_TILT);
  }
  
  public float getZ() {
    return z;
  }
  
  public void setZ(float z) {
    this.z = Math.min(Math.max(z, -MAX_TILT), +MAX_TILT);
  }
  
  public float getResponse() {
    return response;
  }
  
  public void setResponse(float response) {
    this.response = Math.max(response, 0.0f);
  }
  
  public float getRotation() {
    return rotation;
  }
  
  public void setRotation(float rotation) {
    this.rotation = Math.min(Math.max(rotation, -MAX_ROTATION), +MAX_ROTATION);
  }
  
  public Camera getCamera() {
    return camera;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public float getCameraSpeed() {
    return cameraSpeed;
  }
  
  public void setCameraSpeed(float speed) {
    cameraSpeed = Math.max(speed, 0.0f);
  }
  
  public void copyFrom(Input src) {
    x           = src.x;
    z           = src.z;
    response    = src.response;
    rotation    = src.rotation;
    camera      = src.camera;
    cameraSpeed = src.cameraSpeed;
  }
}
