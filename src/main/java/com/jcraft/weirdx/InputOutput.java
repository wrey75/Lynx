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



public abstract class InputOutput {
  InputStream in=null;
  OutputStream out=null;
  byte[] inbuffer=new byte[1024];
  byte[] outbuffer=new byte[1024];
  int instart=0, inend=0, outindex=0, inrest=0;
  byte[] ba;
  byte[] sa;
  byte[] ia;
  InputOutput (){ ba=new byte[1]; sa=new byte[2]; ia=new byte[8]; }

  public abstract int readShort() throws java.io.IOException;
  public abstract int readInt() throws java.io.IOException;
  public abstract void writeShort(int val) throws java.io.IOException;
  public abstract  void writeInt(int val) throws java.io.IOException;

  void setInputStream(InputStream in){this.in=in; }
  void setOutputStream(OutputStream out){ this.out=out; }
  
  /**
   * Write a CARD16 (i.e.&nbsp;an unsigned value).
   * 
   * @param val a value between 0 and 65535 included.
   * @throws IOException if an I/O error occurs.
   */
  void write16(int val) throws IOException {
	  if(val < 0 || val > 65535){
		  throw new IllegalArgumentException("writeCARD16(" + val + "): expected 0 < x < 65536");
	  }
	  writeShort(val);
  }
  
  void write32(int val) throws IOException {
	  writeInt(val);
  }
  
  int available() throws java.io.IOException{
    if(0<inrest) return 1;
    return in.available();
  }
  public int readByte() throws java.io.IOException{
    if((inrest)<1){ read(1); }
    inrest--;
    return inbuffer[instart++]&0xff;
  }
  void readByte(byte[] array) throws java.io.IOException{
    readByte(array, 0, array.length);
  }
  public void readByte(byte[] array, int begin, int length) throws java.io.IOException{
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

  public void readPad(int n) throws java.io.IOException{
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

  protected final void read(int n) throws java.io.IOException{
    if (n>inbuffer.length){
      n=inbuffer.length;
    }
    instart=inend=0;

    int i;

    while(true){
      i=in.read(inbuffer, inend, inbuffer.length-inend);
      if(i==-1){ throw new java.io.IOException(); }
      inend+=i;
      if(n<=inend)break;
    }
    inrest=inend-instart;
  }

  void writeByte(byte val) throws java.io.IOException{
    if((outbuffer.length-outindex)<1){ flush(); }
    outbuffer[outindex++]=val;
  }

  public void writeByte(int val) throws java.io.IOException{
    writeByte((byte)val);
  }

  public void writeByte(byte[] array) throws java.io.IOException{
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

  	/**
  	 * Add padding in the message.
  	 * 
  	 * @param n the number of bytes to pad.
  	 * @throws IOException if an I/O error occurs.
  	 */
	public void writePad(int n) throws IOException {
		int i;
		while(true){
			if( (i = (outbuffer.length-outindex)) < n ){
				if(i!=0){
					outindex += i;
					n -= i;
				}
				flush();
				continue;
			}
			outindex+=n;
			break;
		}
	}

  public synchronized void flush() throws java.io.IOException{
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

