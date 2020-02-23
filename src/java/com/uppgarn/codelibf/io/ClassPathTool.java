/*
 * ClassPathTool.java
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

package com.uppgarn.codelibf.io;

import java.net.*;
import java.nio.file.*;
import java.security.*;

public final class ClassPathTool {
  public static Path getJarDirectory(Class<?> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException();
    }
    
    try {
      CodeSource src = clazz.getProtectionDomain().getCodeSource();
      
      if (src != null) {
        URL url = src.getLocation();
        
        if (url != null) {
          URI uri;
          
          try {
            uri = url.toURI();
          } catch (URISyntaxException ex) {
            uri = null;
          }
          
          if (uri != null) {
            Path file;
            
            try {
              file = Paths.get(uri);
            } catch (IllegalArgumentException
                | FileSystemNotFoundException ex) {
              file = null;
            }
            
            if (file != null) {
              Path name = file.getFileName();
              
              if ((name != null) && name.toString().endsWith(".jar")) {
                return file.getParent();
              }
            }
          }
        }
      }
      
      return null;
    } catch (SecurityException ex) {
      return null;
    }
  }
  
  private ClassPathTool() {
  }
}
