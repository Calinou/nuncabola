/*
 * SourceException.java
 *
 * Copyright (c) 1998-2017 Florian Priester
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

import java.io.*;

public final class SourceException extends IOException {
  public SourceException() {
  }
  
  public SourceException(Throwable cause) {
    super(cause);
  }
}