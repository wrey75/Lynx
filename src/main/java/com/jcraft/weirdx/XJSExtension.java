/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - Guess.
 *
 * Copyright (C) 2000 Christian Werner<Christian.Werner@t-online.de>
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
/* 
 * Authors:  Christian Werner<Christian.Werner@t-online.de>
 *
 * 08-Mar-2000: This file is written by Christian Werner.
 * 02-Aug-2000: ymnk extracts initialization code for XJS from WeirdX class
 *              and add it to this class.
 */ 

package com.jcraft.weirdx;
import java.lang.reflect.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.*;

final class XJSExtension extends Extension{
  private static Object jswin=null;
  private static Method jseval=null;
  private static Method jscall=null;

  static int ctyp;
  static int etyp;

  XJSExtension(){
    try{
      Method getw=null;
      Class c=Class.forName("netscape.javascript.JSObject");
      Method ms[] = c.getMethods();
      for(int i=0; i<ms.length; i++){
	if(ms[i].getName().compareTo("getWindow")==0){
	  getw=ms[i];
	}
	else if(ms[i].getName().compareTo("call")==0){
	  jscall=ms[i];
	}
	else if(ms[i].getName().compareTo("eval")==0){
	  jseval=ms[i];
	}
      }
      Object a[]=new Object[1];
      a[0]=WeirdX.weirdx;
      jswin=getw.invoke(c, a);
    }
    catch(Exception ee){}

    eventcount=0;
    errorcount=0;
    ctyp=Resource.newType();
    etyp=Resource.newType();
    name="WeirdX-JavaScript";
  }

  void swap(Event e){
  }

  void dispatch(Client c) throws IOException{
    int len, i, k, m;
    IO io=c.client;
    String result=null;

    switch(c.data){
      case 0:
	io.readPad(28);
	io.writeByte(1);
	io.writeByte(jswin!=null?1:0);
	io.writeShort(c.seq);
	io.writePad(28);
	return;
      case 1:
	len=c.length*4-32;
	io.readPad(28);
	if(len>0){
	  io.readByte(c.bbuffer, 0, len);
	  for(i=0; i<len; i++){
	    if(c.bbuffer[i]==0) break;
	    c.cbuffer[i]=(char)(c.bbuffer[i] & 0xFF);
	  }
	  String str=new String(c.cbuffer, 0, i);
	  try{
	    Object a[]=new Object[1];
	    a[0]=str;
	    Object r=jseval.invoke(jswin, a);
	    if (r instanceof String) result=(String)r;
	    else result=r.toString();
	  }
	  catch(InvocationTargetException ite){
	    result=""+ite.getTargetException();
	  }
	  catch(Exception e){
	    result=""+e;
	  }
	}
	else{
	  result="";
	}
	break;
      case 2:
	len=c.length*4-32;
	int nargs=io.readInt();
	io.readPad(24);
	String args[]=new String[nargs];
	if(len>0){
	  io.readByte(c.bbuffer, 0, len);
	  for(i=0; i<len; i++){
	    if(c.bbuffer[i]==0) break;
	    c.cbuffer[i]=(char)(c.bbuffer[i] & 0xFF);
	  }
	  String proc=new String(c.cbuffer, 0, i);
	  ++i;
	  for(k=0; k<nargs; k++){
	    for(m=0; i<len; i++){  
	      if(c.bbuffer[i]==0) break;
	      c.cbuffer[m]=(char)(c.bbuffer[i] & 0xFF);
	      m++;
	    }
	    args[k]=new String(c.cbuffer, 0, m);
	  }
	  try{
	    Object[] a=new Object[2];
	    a[0]=proc;
	    a[1]=args;
	    Object r=jscall.invoke(jswin, a);
	    if (r instanceof String) result=(String)r;
	    else result=r.toString();
	  }
	  catch(InvocationTargetException ite){
	    result=""+ite.getTargetException();
	  }
	  catch(Exception e){
	    result=""+e;
	  }
	}
	else{
	  result="";
	}
	break;
      default:
	System.err.println("XJS: unknown code="+c.data);
    }
    if(result!=null){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      len=((result.length()+4)>>2)<<2;
      io.writeInt(len/4);
      io.writeInt(len);
      io.writePad(20);
      for(i=0; i<result.length(); i++){
	io.writeByte((byte)result.charAt(i));
      }
      for(; i<len; i++){
	io.writeByte(0);
      }
    }
  }
}
