/*
 * AudioDataCache.java
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

package com.uppgarn.nuncabola.core.audio;

import java.util.*;

final class AudioDataCache {
  private List<Entry> entries;
  
  private int free;
  
  public AudioDataCache(int size) {
    entries = new ArrayList<>();
    
    free = size;
  }
  
  private int indexOf(String path) {
    for (int idx = entries.size() - 1; idx >= 0; idx--) {
      if (entries.get(idx).path.equals(path)) {
        return idx;
      }
    }
    
    return -1;
  }
  
  private void freeUp(int needed) {
    if (needed <= 0) {
      return;
    }
    
    int total = 0;
    
    for (int idx = 0; idx < entries.size(); idx++) {
      total += entries.get(idx).data.getSize();
      
      if (total >= needed) {
        entries.subList(0, idx + 1).clear();
        
        free += total;
        
        break;
      }
    }
  }
  
  public void add(String path, AudioData data) {
    if (indexOf(path) != -1) {
      return;
    }
    
    freeUp(data.getSize() - free);
    
    if (data.getSize() <= free) {
      entries.add(new Entry(path, data));
      
      free -= data.getSize();
    }
  }
  
  public AudioData get(String path) {
    int idx = indexOf(path);
    
    if (idx == -1) {
      return null;
    }
    
    // A requested entry is moved to the end of the list, making it
    // less likely to be removed when space needs to be freed up.
    
    Entry entry = entries.remove(idx);
    entries.add(entry);
    
    return entry.data;
  }
  
  private static final class Entry {
    public final String    path;
    public final AudioData data;
    
    public Entry(String path, AudioData data) {
      this.path = path;
      this.data = data;
    }
  }
}
