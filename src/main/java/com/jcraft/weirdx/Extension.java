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

import java.io.*;

abstract class Extension {
  static int currentMaxType=127;
  static int currentMaxEventType=/*Event.EXTENSION_EVENT_BASE=*/64;
  static int currentMaxErrorType=17;

  static Extension[] ext=new Extension[10];

  static void init(String foo){
    if(foo==null) return;
    int start=0, end;
    while(true){
      if((end=foo.indexOf(',', start))>0){
	load(foo.substring(start, end));
	start=end+1;
	continue;
      }
      if(start<foo.length()){ load(foo.substring(start)); }
      break;
    }
  }

  static void load(String name){
    try{
      Class c=Class.forName("com.jcraft.weirdx."+name+"Extension");
      Extension e=(Extension)c.newInstance();
      currentMaxType++; e.type=currentMaxType; 
      currentMaxEventType++; e.eventbase=currentMaxEventType;
      currentMaxEventType+=(e.eventcount-1);
      currentMaxErrorType++; e.errorbase=currentMaxErrorType;
      currentMaxErrorType+=(e.errorcount-1);
      for(int i=0; i<ext.length; i++){
	if(ext[i]==null){
	  ext[i]=e;
	  return;
	}
      }
      Extension[] foo=new Extension[ext.length*2];
      System.arraycopy(ext, 0, foo, 0, ext.length);
      foo[ext.length]=e;
      ext=foo;
    }
    catch(Exception e){
      System.err.println(e);
    }
  }

  int type;
  int eventbase;
  int eventcount;
  int errorbase;
  int errorcount;
  String name;

  static final void dispatch(int reqType, Client c) throws IOException{
    for(int i=0; i<ext.length; i++){
      if(ext[i]==null)break;
      if(ext[i].type==reqType){
	ext[i].dispatch(c);
	return;
      }
    }
    System.err.println("Extension: unknown reqType "+reqType);
  }

  static void swap(int etyp, Event e){
    for(int i=0; i<ext.length; i++){
      if(ext[i]==null)break;
      if(ext[i].eventcount==0)continue;
      if(ext[i].eventbase<=etyp && 
	 etyp<=ext[i].eventbase+ext[i].eventcount){
	ext[i].swap(e);
	return;
      }
    }
  }

  abstract void dispatch(Client c) throws IOException;
  abstract void swap(Event e);

  static void reqListExtensions(Client c) throws IOException{
    int foo;
    int n;
    IO io=c.client;
    foo=c.length;

    synchronized(io){
      io.writeByte(1);
      int count=0;
      while(true){
        if(ext[count]==null)break;
        count++;
      }

      io.writeByte(count);
      io.writeShort(c.seq);

      if(count==0){
        io.writeInt(0);
        io.writePad(24);
        return;
      }

      int i=0;
      byte[] buf=c.bbuffer;
      byte[] b;
      n=0;
      while(true){
        if(ext[i]==null)break;
        b=ext[i].name.getBytes();
        buf[n]=(byte)b.length; n++;
        System.arraycopy(b, 0, buf, n, b.length); n+=b.length;
        i++;
      }
      io.writeInt((n+3)/4);
      io.writePad(24);
      io.writeByte(buf, 0, n);
      if(((-n)&3)>0){
        io.writePad((-n)&3);
      }
      io.flush();
    }
  }

  static void reqQueryExtension(Client c) throws IOException{
    int foo;
    int n;
    IO io=c.client;
    foo=c.length;
    n=io.readShort();
    io.readPad(2);

    io.readByte(c.bbuffer, 0, n);
    io.readPad((-n) & 3);

    String name=new String(c.bbuffer, 0, n);

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(0);
      for(int i=0; i<ext.length; i++){
        if(ext[i]==null) break;
        if(ext[i].name.equals(name)){
   	  io.writeByte(1);  // true
	  io.writeByte(ext[i].type);
	  if(ext[i].eventcount==0) io.writeByte(0);
	  else io.writeByte(ext[i].eventbase);
	  if(ext[i].errorcount==0) io.writeByte(0);
	  else io.writeByte(ext[i].errorbase);
	  io.writePad(20);
	  return;
        }
      }
      io.writeByte(0);  // false
      io.writeByte(0);
      io.writeByte(0);
      io.writeByte(0);
      io.writePad(20);
      io.flush();
    }
  }
}
