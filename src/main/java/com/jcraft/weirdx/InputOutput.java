/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - Guess.
 *
 * Copyright (C) 1999-2004 JCraft, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.jcraft.weirdx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is ovewritten by IOLSB or IOMSB for
 * byte ordering.
 * 
 * @author wrey
 *
 */
public class InputOutput {
	private static Log LOG = LogFactory.getLog(InputOutput.class);
	private static final int LSB = 0x6c;
	private static final int MSB = 0x42;
	private int mode = 0;
	
  InputStream in = null;
  OutputStream out = null;
  byte[] inbuffer=new byte[1024];
  byte[] outbuffer=new byte[1024];
  int instart=0, inend=0, outindex=0, inrest=0;
  byte[] ba;
  byte[] sa;
  byte[] ia;
  
  InputOutput(){ ba=new byte[1]; sa=new byte[2]; ia=new byte[8]; }

	int readShort() throws IOException{
		if( inrest < 2 ){
			read(2);
		}
	    inrest -= 2;
	    
	    int s = inbuffer[instart++] & 0xff;
	    if( mode == LSB ){
	    	s |= ((inbuffer[instart++] & 0xff) << 8);
	    }
	    else {
	    	s = ((s<<8)&0xffff)|(inbuffer[instart++]&0xff);
	    }
	    return s;
  }
	
 	public int readInt() throws IOException {
	    if((inrest)<4){
	    	read(4); 
	    }
	    inrest-=4;
	    
	    int i=inbuffer[instart++]&0xff;
	    if( mode == LSB ){
		    i|=((inbuffer[instart++]&0xff)<<8);
		    i|=((inbuffer[instart++]&0xff)<<16);
		    i|=((inbuffer[instart++]&0xff)<<24);
	    }
	    else {
	    	i=((i<<8)&0xffff)|(inbuffer[instart++]&0xff);
	    	i=((i<<8)&0xffffff)|(inbuffer[instart++]&0xff);
	    	i=(i<<8)|(inbuffer[instart++]&0xff);
	    }
	    return i;
 	}
  
 	void writeShort(int val) throws IOException {
	    if( outbuffer.length-outindex < 2 ) {
	    	flush();
	    }
	    
	    if( mode == LSB ){
	    	outbuffer[outindex++]=(byte)(val&0xff);
	    	outbuffer[outindex++]=(byte)((val >> 8)&0xff);
	    }
	    else {
		    outbuffer[outindex++]=(byte)((val >> 8)&0xff);
		    outbuffer[outindex++]=(byte)(val&0xff);
	    }
 	}
 	
    void writeInt(int val) throws IOException {
	    if((outbuffer.length-outindex)<4){ flush(); }
    	if( mode == LSB ){
    	    outbuffer[outindex++]=(byte)((val) & 0xff);
    	    outbuffer[outindex++]=(byte)((val >> 8) & 0xff);
    	    outbuffer[outindex++]=(byte)((val >> 16) & 0xff);
    	    outbuffer[outindex++]=(byte)((val >> 24) & 0xff);
    	}
    	else {
    	    outbuffer[outindex++]=(byte)((val >> 24) & 0xff);
    	    outbuffer[outindex++]=(byte)((val >> 16) & 0xff);
    	    outbuffer[outindex++]=(byte)((val >> 8) & 0xff);
    	    outbuffer[outindex++]=(byte)((val) & 0xff);
    	}
    }

  // protected void setInputStream(InputStream in){this.in=in; }
  // protected void setOutputStream(OutputStream out){ this.out=out; }
  public void setSocket( Socket s ) throws IOException {
	  int byteOrder;
	  
	  this.in = s.getInputStream();
	  this.out = s.getOutputStream();
	  
	  byteOrder = in.read();
	  switch( byteOrder ){
		  case LSB :
		  case MSB :
			  mode = byteOrder;
			  break;
			  
		  default :
			  LOG.error("protocol error: LSB/MSB byte expected (received "+
					     Integer.toHexString(byteOrder));
			  throw new IOException("Protocol error");
	  }
  }
  
  public boolean isMSB() {
	  return (mode == MSB); 
  }
  
  int available() throws IOException{
    if(0<inrest) return 1;
    return in.available();
  }
  int readByte() throws IOException{
    if((inrest)<1){ read(1); }
    inrest--;
    return inbuffer[instart++]&0xff;
  }
  void readByte(byte[] array) throws java.io.IOException{
    readByte(array, 0, array.length);
  }
  void readByte(byte[] array, int begin, int length) throws java.io.IOException{
    int i=0;
    while(true){
      if((i=(inrest))<length){
	if(i!=0){
	  System.arraycopy(inbuffer, instart, array, begin, i);
	  begin+=i;
	  length-=i;
	  instart+=i;
	  inrest-=i;
	}
	read(length);
	continue;
      }
      System.arraycopy(inbuffer, instart, array, begin, length);
      instart+=length;
      inrest-=length;
      break;
    }
  }

  void readPad(int n) throws java.io.IOException{
    while (n > 0){
      if(inrest<n){ 
	n-=inrest;
	instart+=inrest;
	inrest=0;
	read(n); 
	continue;
      }
      instart+=n;
      inrest-=n;
      break;
    }
  }

  protected final void read(int n) throws IOException {
	  if( n > inbuffer.length ){
		  n=inbuffer.length;
	  }
	  instart = inend = 0;

    int i;

    while(true){
      i=in.read(inbuffer, inend, inbuffer.length-inend);
      if(i==-1){ throw new IOException(); }
      inend+=i;
      if(n<=inend)break;
    }
    inrest=inend-instart;
  }

  void writeByte(byte val) throws IOException{
    if((outbuffer.length-outindex)<1){ flush(); }
    outbuffer[outindex++]=val;
  }

  void writeByte(int val) throws IOException{
    writeByte( (byte)val );
  }

  void writeByte(byte[] array) throws java.io.IOException{
    writeByte(array, 0, array.length);
  }

  void writeByte(byte[] array, int begin, int length) throws java.io.IOException{
    if(length<=0) return;
    int i=0;
    while(true){
      if((i=(outbuffer.length-outindex))<length){
	if(i!=0){
	  System.arraycopy(array, begin, outbuffer, outindex, i);
	  begin+=i;
	  length-=i;
	  outindex+=i;
	}
	flush();
	continue;
      }
      System.arraycopy(array, begin, outbuffer, outindex, length);
      outindex+=length;
      break;
    }
  }

  void writePad(int n) throws java.io.IOException{
    int i;
    while(true){
      if((i=(outbuffer.length-outindex))<n){
	if(i!=0){
	  outindex+=i;
	  n-=i;
	}
	flush();
	continue;
      }
      outindex+=n;
      break;
    }
  }

  synchronized void flush() throws java.io.IOException{
    if(outindex==0)return;
    out.write(outbuffer, 0, outindex);
    outindex=0;
  }
  synchronized void immediateWrite(byte[] array, int b, int l)throws java.io.IOException{
    out.write(array, b, l);
  }
  void close() throws java.io.IOException{
    in.close(); out.close(); 
  }
}
