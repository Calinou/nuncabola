/*
 * Gfx.java
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

package com.uppgarn.nuncabola.core.graphics;

import com.uppgarn.nuncabola.core.image.*;
import com.uppgarn.nuncabola.core.math.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.SGISGenerateMipmap.*;

import java.nio.*;

public final class Gfx {
  private static int     width;
  private static int     height;
  private static boolean reflection;
  private static int     multisample;
  private static boolean mipmap;
  private static int     aniso;
  private static int     textures;
  
  private static int maxTextureUnits;
  private static int maxTextureSize;
  
  private static ByteBuffer   byteBuf;
  private static FloatBuffer  floatBuf;
  private static DoubleBuffer doubleBuf;
  
  private static boolean wire;
  
  public static void initialize(
      int     width,
      int     height,
      boolean reflection,
      int     multisample,
      boolean mipmap,
      int     aniso,
      int     textures) throws GfxException {
    checkVersion();
    checkExtensions();
    
    Gfx.width       = width;
    Gfx.height      = height;
    Gfx.reflection  = reflection;
    Gfx.multisample = multisample;
    Gfx.mipmap      = mipmap;
    Gfx.aniso       = aniso;
    Gfx.textures    = textures;
    
    maxTextureUnits = glGetInteger(GL_MAX_TEXTURE_UNITS);
    maxTextureSize  = glGetInteger(GL_MAX_TEXTURE_SIZE);
    
    byteBuf   = BufferUtils.createByteBuffer  (4);
    floatBuf  = BufferUtils.createFloatBuffer (48);
    doubleBuf = BufferUtils.createDoubleBuffer(4);
    
    wire = false;
    
    configure();
  }
  
  private static void checkVersion() throws GfxException {
    if (!GLContext.getCapabilities().OpenGL13) {
      throw new GfxException("Unsupported OpenGL version");
    }
  }
  
  private static void failExtension(String ext) throws GfxException {
    throw new GfxException("Missing required OpenGL extension (" + ext + ")");
  }
  
  private static void checkExtensions() throws GfxException {
    if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
      failExtension("ARB_vertex_buffer_object");
    }
    if (!GLContext.getCapabilities().GL_ARB_point_sprite) {
      failExtension("ARB_point_sprite");
    }
    if (!GLContext.getCapabilities().GL_ARB_point_parameters) {
      failExtension("ARB_point_parameters");
    }
  }
  
  private static void configure() {
    glViewport(0, 0, width, height);
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    
    glEnable(GL_NORMALIZE);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    
    glLightModeli(GL_LIGHT_MODEL_COLOR_CONTROL, GL_SEPARATE_SPECULAR_COLOR);
    
    glPixelStorei(GL_PACK_ALIGNMENT,   1);
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDepthFunc(GL_LEQUAL);
    
    if (multisample > 0) {
      glEnable(GL_MULTISAMPLE);
    }
    
    glReadBuffer(GL_FRONT);
  }
  
  public static int getWidth() {
    return width;
  }
  
  public static int getHeight() {
    return height;
  }
  
  public static boolean getReflection() {
    return reflection;
  }
  
  public static int getMaximumTextureUnits() {
    return maxTextureUnits;
  }
  
  public static void setPerspective(float fov, float n, float f) {
    float c = (float) (1.0 / Math.tan(Math.toRadians(fov / 2)));
    float a = (float) width / height;
    
    glMatrixMode(GL_PROJECTION);
    {
      glLoadIdentity();
      
      glMultMatrix(buffer(
         c / a,
         0.0f,
         0.0f,
         0.0f,
         0.0f,
         c,
         0.0f,
         0.0f,
         0.0f,
         0.0f,
        -(f + n) / (f - n),
        -1.0f,
         0.0f,
         0.0f,
        -2.0f * n * f / (f - n),
         0.0f));
    }
    glMatrixMode(GL_MODELVIEW);
    {
      glLoadIdentity();
    }
  }
  
  public static void setOrtho() {
    glMatrixMode(GL_PROJECTION);
    {
      glLoadIdentity();
      
      glOrtho(0.0, width, 0.0, height, -1.0, +1.0);
    }
    glMatrixMode(GL_MODELVIEW);
    {
      glLoadIdentity();
    }
  }
  
  public static void clear() {
    int bits = GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT;
    
    if (reflection) {
      bits |= GL_STENCIL_BUFFER_BIT;
    }
    
    glClear(bits);
  }
  
  public static void setWire(boolean wire) {
    if (Gfx.wire != wire) {
      Gfx.wire = wire;
      
      glPolygonMode(GL_FRONT_AND_BACK, wire ? GL_LINE : GL_FILL);
    }
  }
  
  public static void toggleWire() {
    setWire(!wire);
  }
  
  public static Image getScreenshot() {
    ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
    
    glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buf);
    
    return new Image(buf, width, height, true);
  }
  
  private static Image getTextureImage(Image img) {
    // Scale the image as configured, or to fit the OpenGL limitations.
    
    int factor = textures;
    
    while ((img.getWidth () / factor > maxTextureSize)
        || (img.getHeight() / factor > maxTextureSize)) {
      factor *= 2;
    }
    
    if (factor == 1) {
      return img;
    } else {
      return img.scaledDown(factor);
    }
  }
  
  public static int createTexture(Image img, boolean allowMipmap) {
    if (img == null) {
      return 0;
    }
    
    Image textureImg = getTextureImage(img);
    
    // Generate a new OpenGL texture.
    
    int texture = glGenTextures();
    
    // Configure the texture.
    
    glBindTexture(GL_TEXTURE_2D, texture);
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    
    if (allowMipmap
        && mipmap
        && GLContext.getCapabilities().GL_SGIS_generate_mipmap) {
      glTexParameteri(
        GL_TEXTURE_2D,
        GL_GENERATE_MIPMAP_SGIS,
        GL_TRUE);
      glTexParameteri(
        GL_TEXTURE_2D,
        GL_TEXTURE_MIN_FILTER,
        GL_LINEAR_MIPMAP_LINEAR);
    }
    
    if ((aniso > 0)
        && GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso);
    }
    
    // Copy the image to the texture.
    
    int format = textureImg.hasAlpha() ? GL_RGBA : GL_RGB;
    
    glTexImage2D(
      GL_TEXTURE_2D,
      0,
      format,
      textureImg.getWidth (),
      textureImg.getHeight(),
      0,
      format,
      GL_UNSIGNED_BYTE,
      textureImg.getBuffer());
    
    return texture;
  }
  
  public static ByteBuffer buffer(byte b0, byte b1, byte b2, byte b3) {
    byteBuf.clear();
    byteBuf.put(b0);
    byteBuf.put(b1);
    byteBuf.put(b2);
    byteBuf.put(b3);
    byteBuf.flip();
    
    return byteBuf;
  }
  
  public static FloatBuffer buffer(float f0, float f1, float f2) {
    floatBuf.clear();
    floatBuf.put(f0);
    floatBuf.put(f1);
    floatBuf.put(f2);
    floatBuf.put(0.0f);
    floatBuf.flip();
    
    return floatBuf;
  }
  
  public static FloatBuffer buffer(float f0, float f1, float f2, float f3) {
    floatBuf.clear();
    floatBuf.put(f0);
    floatBuf.put(f1);
    floatBuf.put(f2);
    floatBuf.put(f3);
    floatBuf.flip();
    
    return floatBuf;
  }
  
  public static FloatBuffer buffer(
      float f0,
      float f1,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12,
      float f13,
      float f14,
      float f15) {
    floatBuf.clear();
    floatBuf.put(f0);
    floatBuf.put(f1);
    floatBuf.put(f2);
    floatBuf.put(f3);
    floatBuf.put(f4);
    floatBuf.put(f5);
    floatBuf.put(f6);
    floatBuf.put(f7);
    floatBuf.put(f8);
    floatBuf.put(f9);
    floatBuf.put(f10);
    floatBuf.put(f11);
    floatBuf.put(f12);
    floatBuf.put(f13);
    floatBuf.put(f14);
    floatBuf.put(f15);
    floatBuf.flip();
    
    return floatBuf;
  }
  
  public static FloatBuffer buffer(float[] array) {
    floatBuf.clear();
    floatBuf.put(array);
    floatBuf.flip();
    
    return floatBuf;
  }
  
  public static FloatBuffer buffer(Matrix4 m) {
    floatBuf.clear();
    floatBuf.put(m.m00);
    floatBuf.put(m.m01);
    floatBuf.put(m.m02);
    floatBuf.put(m.m03);
    floatBuf.put(m.m10);
    floatBuf.put(m.m11);
    floatBuf.put(m.m12);
    floatBuf.put(m.m13);
    floatBuf.put(m.m20);
    floatBuf.put(m.m21);
    floatBuf.put(m.m22);
    floatBuf.put(m.m23);
    floatBuf.put(m.m30);
    floatBuf.put(m.m31);
    floatBuf.put(m.m32);
    floatBuf.put(m.m33);
    floatBuf.flip();
    
    return floatBuf;
  }
  
  public static FloatBuffer buffer(Color4 c) {
    floatBuf.clear();
    floatBuf.put(c.r);
    floatBuf.put(c.g);
    floatBuf.put(c.b);
    floatBuf.put(c.a);
    floatBuf.flip();
    
    return floatBuf;
  }
  
  public static DoubleBuffer buffer(
      double d0,
      double d1,
      double d2,
      double d3) {
    doubleBuf.clear();
    doubleBuf.put(d0);
    doubleBuf.put(d1);
    doubleBuf.put(d2);
    doubleBuf.put(d3);
    doubleBuf.flip();
    
    return doubleBuf;
  }
  
  public static void deinitialize() {
    byteBuf   = null;
    floatBuf  = null;
    doubleBuf = null;
  }
  
  private Gfx() {
  }
}
