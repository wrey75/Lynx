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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class Property{
	private static Log LOG = LogFactory.getLog(Property.class);
  static final byte PropModeReplace=0;
  static final byte PropModePrepend=1;
  static final byte PropModeAppend=2;

  Property next;
  int propertyName;
  int type;
  short	format;
  int size;
  byte[] data;

  Property(){ }
  static Property getProperty(Window w, int name, int type){
    synchronized(w){
      Property p=w.getProperty();
      while(p!=null){
	if(p.propertyName==name)break;
	p=p.next;
      }
      if(p!=null){
	return p;
      }
      return null;
    }
  }

  static void delProperty(Client c, Window w, int name, int type)
    throws IOException{
    Property p;
    synchronized(w){
      p=w.getProperty();
      Property prev=null;
      while(p!=null){
	if(p.propertyName==name)break;
	prev=p;
	p=p.next;
      }
      if(p!=null){
	if(prev==null){           // head !!
	  w.setProperty(p.next);
	  if(p.next==null){
	    // checkWindowOptionalNeed();
	  }
	}
	else{
	  prev.next=p.next;
	}
	c.cevent.mkPropertyNotify(w.id, p.propertyName,
				  (int)System.currentTimeMillis(),
				  //Property.PropertyDelete
				  1);
      }
    }
    if(p!=null){
      w.sendEvent(c.cevent, 1, null);
    }
  }

  static void changeWindowProperty(Client c,
				   Window w, 
				   int property, int type,
				   short format, byte mode,
				   int len,
				   byte[] value,
				   boolean sendevent) throws IOException{
    synchronized(w){
      Property p;
      int totalSize=len*(format/8);
      p=w.getProperty();
      while(p!=null){
	if(p.propertyName==property)break;
	p=p.next;
      }

      if(p!=null){
	if((format!=p.format) && (mode!=PropModeReplace)){
	  LOG.error("error!");
	  c.errorReason=8; // BadMatch
	  return;
	}
	if((type!=p.type) && (mode!=PropModeReplace)){
	  LOG.error("error!");
	  c.errorReason=8; // BadMatch
	  return;
	}
	if(mode==PropModeReplace){
	  p.data=value;
	  p.size=len;
	  p.format=format;
	  p.type=type;
	}
	else if(len==0){
	}
	else if(mode==PropModeAppend){
	  byte[] foo=new byte[(format/8) * (len+p.size)];
	  if(p.size>0) 
	      System.arraycopy(p.data, 0, foo, 0, p.size);
	  System.arraycopy(value, 0, foo, p.size, totalSize);
	  p.size+=len;
	  p.data=foo;
	}
	else if(mode==PropModePrepend){
	  byte[] foo=new byte[(format/8) * (len+p.size)];
	  System.arraycopy(value, 0, foo, 0, totalSize);
	  if(p.size>0) 
	      System.arraycopy(p.data, 0, foo, totalSize, p.size);
	  p.size+=len;
	  p.data=foo;
	}
	// change;
      }
      else{
	//    w.setProperty(null);
	p=new Property();
	p.propertyName=property;
	p.type=type;
	p.format=(short)format;
	p.data=value;
	p.size=len;
	p.next=w.getProperty();
	w.setProperty(p);
      }

      if(p!=null){
        if(p.propertyName==9 && // CUT_BUFFER0 
           w==w.screen.root && p.size>0){
          CopyPaste.setString(new String(p.data, 0, p.size));
        }
        if(w.screen.windowmode!=WeirdX.InBrowser &&
          p.propertyName==39 &&
          p.type==31 &&
          p.size>0 &&
          w.ddxwindow!=null){
          java.awt.Window frame=w.getFrame();
          if(frame!=null && (frame instanceof java.awt.Frame)){
            ((java.awt.Frame)frame).setTitle(new String(p.data));
	  }
        }
      }
    }

    if (sendevent){
      c.cevent.mkPropertyNotify(w.id, 
				property, 
				(int)System.currentTimeMillis(), 0);
      w.sendEvent(c.cevent, 1, null);
    }
  }

  @SuppressWarnings("unused")
static void reqRotateProperties(Client c) throws IOException{
    int foo, propty;
    IO io=c.client;

    foo=io.readInt();
    Window w=c.lookupWindow(foo);
    c.length-=2;
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }
    int n=(short)io.readShort();
    int delta=(short)io.readShort();

    c.length--;

    if(n==0){ return; }

    int[] atoms=new int[n];
    Property[] props=new Property[n];
    Property p;
    int i=0;
    while(n!=0){
      atoms[i]=io.readInt();
      c.length--;
      if(!Atom.valid(atoms[i])){
	c.errorValue=atoms[i];
	c.errorReason=5; // BadAtom
	return;
      }
      p=w.getProperty();
      while(p!=null){
	if(p.propertyName==atoms[i]){
	  props[i]=p;
	  break;
	}
	p=p.next;
      }
      if(p==null){
	c.errorReason=8; // BadMatch
	return;
      }
      i++;
      n--;
    }
    for(int j=0; j<atoms.length; j++){
      for(int k=j+1; k<atoms.length; k++){
	if(atoms[j]==atoms[k]){
	  c.errorReason=8; // BadMatch
	  return;
	}
      }
    }
    if(((delta<0 ? -1*delta : delta)%atoms.length)!=0){
      while(delta<0){
	delta+=atoms.length;
      }
      for(i=0; i<atoms.length; i++){
	c.cevent.mkPropertyNotify(w.id, 
				  props[i].propertyName,
				  (int)System.currentTimeMillis(),
				  //Property.PropertyNewValue
				  0);
	w.sendEvent(c.cevent, 1, null);
	props[i].propertyName=atoms[(i+delta)%atoms.length];
      }
    }
  }

  static void reqDeleteProperty(Client c) throws IOException{
    int foo, propty;
    IO io=c.client;
    foo=io.readInt();
    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
    }
    propty=foo=io.readInt();
    c.length-=3;
    if(c.errorReason!=0){
      return;
    }
    if(w.parent!=null){
      Property.delProperty(c, w, propty, 0);
    }
  }

  @SuppressWarnings("unused")
static void reqGetProperty(Client c) throws IOException{
    int foo;
    int dlt;
    int prprty;
    int typ;
    int ffst, lngth;
    IO io=c.client;
    dlt=c.data;

    foo=io.readInt();
    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
    }
    prprty=io.readInt(); 
    typ=io.readInt();
    ffst=io.readInt();
    lngth=io.readInt();
    c.length-=6;
    if(c.errorReason!=0){
      return;
    }
    int frmt;
    int seq;
    int ba;
    Property prop;
    prop=Property.getProperty(w, prprty, typ);

    synchronized(io){
      io.writeByte(1);
      if(prop==null){
        io.writeByte(0);
        io.writeShort(c.seq);
        io.writeInt(0);
        io.writeInt(0);
        io.writeInt(0);
        io.writeInt(0);
        io.writePad(12);
	io.flush();
        return;
      }

      if((typ!=prop.type) && typ!=0 ){
        io.writeByte(prop.format);
        io.writeShort(c.seq);
        io.writeInt(0);
        io.writeInt(prop.type);
        io.writeInt(0);
        io.writeInt(0);
        io.writePad(12);
	io.flush();
        return;
      }

      int n=(prop.format/8)*prop.size;

      int ind=ffst*4;
      if(n<ind){
        //System.err.println("procGetProperty: ind-n="+new Integer((ind-n)));
      }

      int len=((n-ind)<lngth*4)? n-ind : lngth*4;
      if((lngth*4)<0) len=n-ind;
      ba=n-(ind+len);
      if(dlt!=0 && ba==0){
        c.cevent.mkPropertyNotify(w.id, prprty, 
				  (int)System.currentTimeMillis(), 1);
        w.sendEvent(c.cevent, 1, null);
      }

      io.writeByte(prop.format);
      io.writeShort(c.seq);
      io.writeInt((len+3)/4);
      io.writeInt(prop.type);
      io.writeInt(ba);
      if((prop.format/8)==0) io.writeInt(0);
      else io.writeInt(len/(prop.format/8));

      io.writePad(12);

      if(len>0){
        if(c.swap && (prop.format==16 || prop.format==32)){
	  byte[] b=new byte[len];
	  System.arraycopy(prop.data, ind, b, 0, len);
	  switch(prop.format){
	  case 16:
	    swapS(b, 0, len);
	    break;
	  case 32:
	    swapL(b, 0, len);
	    break;
	  default:
	  }
	  io.writeByte(b, 0, len);
        }
        else{
	  io.writeByte(prop.data, ind, len);
        }
        if(((-len)&3) > 0){ io.writePad((-len)&3); }
      }
      io.flush();
    }

    if(dlt!=0 && ba==0){
      Property.delProperty(c, w, prprty, typ);
    }
  }

  @SuppressWarnings("unused")
static void reqListProperties(Client c) throws IOException{
    int foo, n;
    IO io=c.client;

    foo=io.readInt();
    Window w=c.lookupWindow(foo);
    c.length-=2;
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }
    synchronized(io){
      io.writeByte(1); 
      Property p=w.getProperty();
      int i=0;
      while(p!=null){i++; p=p.next;}

      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(i);
      io.writeShort(i);
      io.writePad(22);
      
      p=w.getProperty();
      while(p!=null){
        io.writeInt(p.propertyName);
        p=p.next;
      }
      io.flush();
    }
  }

  @SuppressWarnings("unused")
static void reqChangeProperty(Client c) throws IOException{
    int foo;
    byte mode;
    int prpty;
    byte frmt;
    int typ;
    int n;
    IO io=c.client;

    mode=(byte)c.data;
    if((mode!=PropModeReplace) && (mode!=PropModeAppend) &&
       (mode!=PropModePrepend)){
      //System.err.println("error!!");
      c.errorValue=mode;
      c.errorReason=2; // BadValue;
    }

    n=c.length;;
    foo=io.readInt();
    Window w=c.lookupWindow(foo);
    if(c.errorReason==0 && w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
    }
    prpty=io.readInt();
    typ=io.readInt();
    frmt=(byte)io.readByte();
    if(c.errorReason==0 && (frmt!=8) && (frmt!=16) && (frmt!=32)){
      c.errorValue=frmt;
      c.errorReason=3; // BadWindow;
    }
    io.readPad(3);
    foo=io.readInt();
    int totalSize=foo * (frmt/8);
    byte[] bar=null;
    if(totalSize>0){
      bar=new byte[totalSize];
      io.readByte(bar, 0, totalSize);
      if(c.swap){
	switch(frmt){
	case 16:
	  swapS(bar, 0, totalSize);
	  break;
	case 32:
	  swapL(bar, 0, totalSize);
	  break;
	default:
	}
      }
      io.readPad((-totalSize)&3);
    }
    c.length=0;
    if(c.errorReason!=0){
      return;
    }
    changeWindowProperty(c, w, prpty, typ, frmt, mode, foo, bar, true);
  }

  private final static void swapS(byte[] b, int s, int len){
    if((len)%2!=0){
      // ??
      return;
    }
    len+=s;
    byte bb;
    for(int i=s; i<len; i+=2){
      bb=b[i]; b[i]=b[i+1]; b[i+1]=bb;
    } 
  }

  private final static void swapL(byte[] b, int s, int len){
    if((len)%4!=0){
      // ??
      return;
    }
    len+=s;
    byte bb;
    for(int i=s; i<len; i+=4){
      bb=b[i]; b[i]=b[i+3]; b[i+3]=bb;
      bb=b[i+1]; b[i+1]=b[i+2]; b[i+2]=bb;
    } 
  }
}
