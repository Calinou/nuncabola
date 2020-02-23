/*
 * OggStream.java
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

package com.uppgarn.codelibf.sound;

import com.uppgarn.codelibf.io.*;

import com.jcraft.jogg.*;
import com.jcraft.jorbis.*;

import java.io.*;
import java.nio.file.*;

public final class OggStream implements Closeable {
  private static final int BYTE_BUFFER_SIZE   = 4096;
  private static final int SAMPLE_BUFFER_SIZE = 1024;
  
  private InputStream in;
  
  private SyncState   syncState;
  private Page        page;
  private StreamState streamState;
  private Packet      packet;
  
  private Info     info;
  private Comment  comment;
  private DspState dspState;
  private Block    block;
  
  private int channels;
  private int sampleRate;
  
  private float[][][] pcmBufs;
  private int  []     pcmOffs;
  
  private short[] sampleBuf;
  private int     sampleReadOff;
  private int     sampleReadLen;
  
  public OggStream(Path file) throws OggStreamException {
    if (file == null) {
      throw new IllegalArgumentException();
    }
    
    try {
      // Open input stream.
      
      in = Files.newInputStream(file);
      
      try {
        // Initialize.
        
        initialize();
      } catch (OggStreamException ex) {
        try {
          in.close();
        } catch (IOException ex0) {
        }
        
        throw ex;
      }
    } catch (IOException ex) {
      throw new OggStreamException(ex);
    }
  }
  
  public OggStream(Source src) throws OggStreamException {
    if (src == null) {
      throw new IllegalArgumentException();
    }
    
    try {
      // Open input stream.
      
      in = src.newInputStream();
      
      try {
        // Initialize.
        
        initialize();
      } catch (OggStreamException ex) {
        try {
          in.close();
        } catch (IOException ex0) {
        }
        
        throw ex;
      }
    } catch (SourceException ex) {
      throw new OggStreamException(ex);
    }
  }
  
  private void initialize() throws OggStreamException {
    syncState   = new SyncState();
    page        = new Page();
    streamState = new StreamState();
    packet      = new Packet();
    
    info     = new Info();
    comment  = new Comment();
    dspState = new DspState();
    block    = new Block(dspState);
    
    readHeader();
    
    channels   = info.channels;
    sampleRate = info.rate;
    
    pcmBufs = new float[1][][];
    pcmOffs = new int  [channels];
    
    sampleBuf     = new short[SAMPLE_BUFFER_SIZE * channels];
    sampleReadOff = 0;
    sampleReadLen = 0;
  }
  
  private void readHeader() throws OggStreamException {
    syncState.init();
    
    fetchPage();
    
    streamState.init(page.serialno());
    
    if (streamState.pagein(page) == -1) {
      throw new OggStreamException();
    }
    
    info   .init();
    comment.init();
    
    for (int idx = 0; idx < 3; idx++) {
      if (!fetchPacket()) {
        throw new OggStreamException();
      }
      if (info.synthesis_headerin(comment, packet) == -1) {
        throw new OggStreamException();
      }
    }
    
    dspState.synthesis_init(info);
    
    block.init(dspState);
  }
  
  private void fetchBytes() throws OggStreamException {
    int off = syncState.buffer(BYTE_BUFFER_SIZE);
    
    if (off < 0) {
      throw new OggStreamException();
    }
    
    try {
      int read = in.read(syncState.data, off, BYTE_BUFFER_SIZE);
      
      if (read <= 0) {
        throw new OggStreamException();
      }
      
      syncState.wrote(read);
    } catch (IOException ex) {
      throw new OggStreamException(ex);
    }
  }
  
  private void fetchPage() throws OggStreamException {
    int result;
    
    while ((result = syncState.pageout(page)) == 0) {
      fetchBytes();
    }
    
    if (result == -1) {
      throw new OggStreamException();
    }
  }
  
  private boolean fetchPacket() throws OggStreamException {
    int result;
    
    while ((result = streamState.packetout(packet)) == 0) {
      if (page.eos() != 0) {
        return false;
      }
      
      fetchPage();
      
      streamState.pagein(page);
    }
    
    if (result == -1) {
      throw new OggStreamException();
    }
    
    return true;
  }
  
  private int fetchSamples() throws OggStreamException {
    int avail;
    
    while ((avail = dspState.synthesis_pcmout(pcmBufs, pcmOffs)) == 0) {
      if (!fetchPacket()) {
        return -1;
      }
      
      if (block.synthesis(packet) == 0) {
        dspState.synthesis_blockin(block);
      }
    }
    
    int len = Math.min(avail, SAMPLE_BUFFER_SIZE);
    
    for (int channel = 0; channel < channels; channel++) {
      float[] pcmBuf = pcmBufs[0][channel];
      
      for (int idx = len, pcmIdx = pcmOffs[channel], sampleIdx = channel;
          idx > 0;
          idx--, pcmIdx++, sampleIdx += channels) {
        float f = pcmBuf[pcmIdx] * (Short.MAX_VALUE + 1);
        int   i = (int) ((f > 0.0f) ? f + 0.5f : f - 0.5f);
        
        if (i < Short.MIN_VALUE) {
          i = Short.MIN_VALUE;
        } else if (i > Short.MAX_VALUE) {
          i = Short.MAX_VALUE;
        }
        
        sampleBuf[sampleIdx] = (short) i;
      }
    }
    
    dspState.synthesis_read(len);
    
    return channels * len;
  }
  
  private boolean fillSampleBuffer() throws OggStreamException {
    if (sampleReadOff == sampleReadLen) {
      int count = fetchSamples();
      
      if (count == -1) {
        return false;
      }
      
      sampleReadOff = 0;
      sampleReadLen = count;
    }
    
    return true;
  }
  
  public int getChannels() {
    return channels;
  }
  
  public int getSampleRate() {
    return sampleRate;
  }
  
  public int read(short[] buf, int off, int len) throws OggStreamException {
    if ((buf == null) || (off < 0) || (len < 0) || (len > buf.length - off)) {
      throw new IllegalArgumentException();
    }
    if (in == null) {
      throw new OggStreamException();
    }
    
    int count = 0;
    
    while (count < len) {
      if (!fillSampleBuffer()) {
        break;
      }
      
      int copyLen = Math.min(len - count, sampleReadLen - sampleReadOff);
      
      System.arraycopy(sampleBuf, sampleReadOff, buf, off + count, copyLen);
      
      sampleReadOff += copyLen;
      count         += copyLen;
    }
    
    return count;
  }
  
  @Override
  public void close() {
    if (in == null) {
      return;
    }
    
    streamState.clear();
    block      .clear();
    dspState   .clear();
    info       .clear();
    syncState  .clear();
    
    // Close input stream.
    
    try {
      in.close();
    } catch (IOException ex) {
    }
    
    in = null;
  }
}
