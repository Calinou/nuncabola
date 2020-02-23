/*
 * AudioDataManager.java
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

import com.uppgarn.nuncabola.core.folder.*;

import com.uppgarn.codelibf.util.*;

import java.util.*;

final class AudioDataManager {
  public static AudioDataManager create(Folder dataFolder, int cacheSize) {
    AudioDataManager instance = new AudioDataManager(dataFolder, cacheSize);
    instance.start();
    
    return instance;
  }
  
  private final Folder dataFolder;
  
  private AudioDataCache cache;
  private Queue<Request> requests;
  
  private TerminableThread thread;
  
  private AudioDataManager(Folder dataFolder, int cacheSize) {
    this.dataFolder = dataFolder;
    
    cache    = new AudioDataCache(cacheSize);
    requests = new ArrayDeque<>();
    
    thread = new RequestThread();
  }
  
  private void start() {
    thread.start();
  }
  
  public FutureHolder<AudioData> get(String path) {
    FutureHolder<AudioData> holder = new FutureHolder<>();
    
    AudioData data;
    
    synchronized (cache) {
      data = cache.get(path);
    }
    
    if (data != null) {
      holder.set(data);
    } else {
      synchronized (requests) {
        requests.add(new Request(holder, path));
        requests.notify();
      }
    }
    
    return holder;
  }
  
  public void deinitialize() {
    thread.terminate();
  }
  
  private static final class Request {
    public final FutureHolder<AudioData> holder;
    public final String                  path;
    
    public Request(FutureHolder<AudioData> holder, String path) {
      this.holder = holder;
      this.path   = path;
    }
  }
  
  private final class RequestThread extends TerminableThread {
    private void handle(Request request) {
      FutureHolder<AudioData> holder = request.holder;
      String                  path   = request.path;
      
      AudioData data;
      
      synchronized (cache) {
        data = cache.get(path);
      }
      
      if (data == null) {
        data = AudioDataLoader.load(dataFolder.getSource(path));
        
        if (data != null) {
          synchronized (cache) {
            cache.add(path, data);
          }
        }
      }
      
      holder.set(data);
    }
    
    @Override
    public void run() {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          Request request;
          
          synchronized (requests) {
            while ((request = requests.poll()) == null) {
              requests.wait();
            }
          }
          
          handle(request);
        }
      } catch (InterruptedException ex) {
        // Allow thread to exit.
      }
    }
  }
}
