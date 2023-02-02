/*
 * StringTool.java
 *
 * Copyright (c) 1998-2022 Florian Priester
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

public final class StringTool {
  public static final String LS = System.lineSeparator();
  
  public static String times(char ch, int count) {
    if (count < 0) {
      throw new IllegalArgumentException();
    }
    
    if (count == 0) {
      return "";
    }
    if (count == 1) {
      return String.valueOf(ch);
    }
    
    char[] chars = new char[count];
    
    for (int idx = 0; idx < count; idx++) {
      chars[idx] = ch;
    }
    
    return new String(chars);
  }
  
  public static String space(int count) {
    return times(' ', count);
  }
  
  public static String prePad(String str, int len, char pad) {
    if ((str == null) || (len < 0)) {
      throw new IllegalArgumentException();
    }
    
    int strLen = str.length();
    int padLen = len - strLen;
    
    if (padLen <= 0) {
      return str;
    }
    
    char[] chars = new char[len];
    
    for (int idx = 0; idx < padLen; idx++) {
      chars[idx] = pad;
    }
    for (int idx = 0; idx < strLen; idx++) {
      chars[padLen + idx] = str.charAt(idx);
    }
    
    return new String(chars);
  }
  
  public static String postPad(String str, int len, char pad) {
    if ((str == null) || (len < 0)) {
      throw new IllegalArgumentException();
    }
    
    int strLen = str.length();
    int padLen = len - strLen;
    
    if (padLen <= 0) {
      return str;
    }
    
    char[] chars = new char[len];
    
    for (int idx = 0; idx < strLen; idx++) {
      chars[idx] = str.charAt(idx);
    }
    for (int idx = 0; idx < padLen; idx++) {
      chars[strLen + idx] = pad;
    }
    
    return new String(chars);
  }
  
  public static String limit(String str, int len) {
    if ((str == null) || (len < 0)) {
      throw new IllegalArgumentException();
    }
    
    if (str.length() <= len) {
      return str;
    }
    
    return str.substring(0, len);
  }
  
  public static String beforeFirst(String str, char ch) {
    if (str == null) {
      throw new IllegalArgumentException();
    }
    
    int idx = str.indexOf(ch);
    
    if (idx == -1) {
      return str;
    }
    
    return str.substring(0, idx);
  }
  
  public static String beforeLast(String str, char ch) {
    if (str == null) {
      throw new IllegalArgumentException();
    }
    
    int idx = str.lastIndexOf(ch);
    
    if (idx == -1) {
      return str;
    }
    
    return str.substring(0, idx);
  }
  
  public static String afterFirst(String str, char ch) {
    if (str == null) {
      throw new IllegalArgumentException();
    }
    
    int idx = str.indexOf(ch);
    
    if (idx == -1) {
      return str;
    }
    
    return str.substring(idx + 1);
  }
  
  public static String afterLast(String str, char ch) {
    if (str == null) {
      throw new IllegalArgumentException();
    }
    
    int idx = str.lastIndexOf(ch);
    
    if (idx == -1) {
      return str;
    }
    
    return str.substring(idx + 1);
  }
  
  public static String remove(String str, char remove) {
    if (str == null) {
      throw new IllegalArgumentException();
    }
    
    int strLen = str.length();
    int newLen = strLen;
    
    for (int idx = 0; idx < strLen; idx++) {
      if (str.charAt(idx) == remove) {
        newLen--;
      }
    }
    
    if (newLen == strLen) {
      return str;
    }
    if (newLen == 0) {
      return "";
    }
    
    char[] chars = new char[newLen];
    
    for (int idx = 0, newIdx = 0; newIdx < newLen; idx++) {
      char ch = str.charAt(idx);
      
      if (ch != remove) {
        chars[newIdx++] = ch;
      }
    }
    
    return new String(chars);
  }
  
  public static String removePrefix(String str, String prefix) {
    if ((str == null) || (prefix == null)) {
      throw new IllegalArgumentException();
    }
    
    if (!str.startsWith(prefix)) {
      return str;
    }
    
    return str.substring(prefix.length());
  }
  
  public static String removeSuffix(String str, String suffix) {
    if ((str == null) || (suffix == null)) {
      throw new IllegalArgumentException();
    }
    
    if (!str.endsWith(suffix)) {
      return str;
    }
    
    return str.substring(0, str.length() - suffix.length());
  }
  
  private StringTool() {
  }
}
