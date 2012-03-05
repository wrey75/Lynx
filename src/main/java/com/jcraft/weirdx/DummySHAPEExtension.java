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

import java.awt.*;
import java.awt.image.*;

final class DummySHAPEExtension extends Extension{
  static final int ShapeSet=0;
  static final int ShapeUnion=1;
  static final int ShapeIntersect=2;
  static final int ShapeSubtract=3;
  static final int ShapeInvert=4;

  static final int ShapeBounding=0;
  static final int ShapeClip=1;

//  static final int ShapeNotifyMask=(1 << 0);
//  static final int ShapeNotify=0;

  static int ctyp;
  static int etyp;

  DummySHAPEExtension(){
    eventcount=1;
    errorcount=0;
    ctyp=Resource.newType();
    etyp=Resource.newType();
    name="SHAPE";
  }

  void dispatch(Client c) throws IOException{
    Window window;
    int kind, x, y, w, h, shaped, op;
    Rectangle[] rect;
    int foo;
    IO io=c.client;

    switch(c.data){
      case 0:
        synchronized(io){
          io.writeByte(1);
	  io.writePad(1);
	  io.writeShort(c.seq);
	  io.writeInt(0);
	  io.writeShort(1);
	  io.writeShort(0);
	  io.writePad(20);
	  io.flush();
	}
	break;
      case 1:	
	op=io.readByte();
	kind=io.readByte();
	int oder=io.readByte();
	io.readPad(1);
	foo=io.readInt();
	c.length-=3;
	window=c.lookupWindow(foo);
	if(window==null){
	  c.errorValue=foo;
	  c.errorReason=3; // BadWindow
	  return;
	}
	x=(short)io.readShort(); 
	y=(short)io.readShort();
	c.length--;
	int len=(c.length)/2;
	rect=new Rectangle[len];
	{
	  int xx, yy, ww, hh;
	  while(len>0){
	    xx=(short)io.readShort();
	    yy=(short)io.readShort();
	    ww=(short)io.readShort();
	    hh=(short)io.readShort();
	    rect[rect.length-len]=new Rectangle(xx, yy, ww, hh);
	    len--;
	  }
	}
//	synchronized(DummySHAPEExtension.class){                    
//	  window.ddxwindow.setShapeRectangles(op, kind, oder, x, y, rect);
//	}                                                      
	break;
      case 2:	
	op=io.readByte();
	kind=io.readByte();
	io.readPad(2);
	foo=io.readInt();
	c.length-=3;
	window=c.lookupWindow(foo);
	if(window==null){
	  c.errorValue=foo;
	  c.errorReason=3; // BadWindow
	  return;
	}
	x=(short)io.readShort();
	y=(short)io.readShort();
	foo=io.readInt();
	c.length-=2;
        if(foo==0){
  	  sendShapeNotify(kind, window, x, y, window.width, window.height, 0);
          return;
        }

        Drawable d=c.lookupDrawable(foo);
	if(d==null || !(d instanceof Pixmap)){
	  c.errorValue=foo;
	  c.errorReason=4; // BadPixmap;
	  return;
	}
	Pixmap pixmap=(Pixmap)d;
	if(pixmap.depth!=1){
	  c.errorValue=foo;
	  c.errorReason=8; // BadMatch
	  return;
	}

//	synchronized(DummySHAPEExtension.class){                        
//	  if(kind==1){                                             
//	    window.ddxwindow.setShapeMask(op, kind, x, y, pixmap); 
//        }                                                   
//	}
	w=pixmap.width; h=pixmap.height; shaped=1;
	sendShapeNotify(kind, window, x, y, w, h, shaped);
        break;
      case 3:	
	int dk, sk;
	op=io.readByte();
	dk=io.readByte();
	sk=io.readByte();
	io.readPad(1);
	foo=io.readInt();
	c.length-=3;
	Window dwindow=c.lookupWindow(foo);
	if(dwindow==null){
	  c.errorValue=foo;
	  c.errorReason=3; // BadWindow
	  return;
	}
	x=(short)io.readShort();
	y=(short)io.readShort();
	foo=io.readInt();
	c.length-=2;
	Window swindow=c.lookupWindow(foo);
	if(swindow==null){
	  c.errorValue=foo;
	  c.errorReason=3; // BadWindow
	  return;
	}
//	synchronized(DummySHAPEExtension.class){                
//	  dwindow.ddxwindow.setCombineMask(op, sk, dk, x, y, swindow); 
//	}                                                  
        break;
      case 4:	
	foo=io.readByte();
	io.readPad(3);
	foo=io.readInt();
	foo=io.readShort();
	foo=io.readShort();
        break;
      case 5:	
	foo=io.readInt();
	c.length-=2;
	window=c.lookupWindow(foo);
	if(window==null){
	  c.errorValue=foo;
	  c.errorReason=3; // BadWindow
	  return;
	}
        synchronized(io){
          io.writeByte(1);
	  io.writePad(1);
	  io.writeShort(c.seq);
	  io.writeInt(0);
	  io.writeByte(0);  // bouding shaped
	  io.writeByte(0);  // clip shaped
	  io.writePad(2);
	  io.writeShort(-window.borderWidth);  // bounding x
	  io.writeShort(-window.borderWidth);
	  io.writeShort(window.width+2*window.borderWidth);
	  io.writeShort(window.height+2*window.borderWidth);
	  io.writeShort(0);  // clip x
	  io.writeShort(0);
	  io.writeShort(window.width);
	  io.writeShort(window.height);
	  io.writePad(4);
	  io.flush();
	}
        break;
      case 6:	
	foo=io.readInt();
	c.length-=2;
	window=c.lookupWindow(foo);
	if(window==null){
	  c.errorValue=foo;
	  c.errorReason=3; // BadWindow
	  return;
	}
	foo=io.readByte();
        io.readPad(3);
	c.length--;
	if(foo!=0 && foo!=1){
	  c.errorValue=foo;
	  c.errorReason=2; // BadWindow
	  return;
	}

        {
	  Head head=(Head)Resource.lookupIDByType(window.id, DummySHAPEExtension.etyp);
  	  if(foo==1){
	    if(head!=null){
	      for(ShapeEvent se=head.next; se!=null; se=se.next){
	        if(se.client==c){
		  return;
	        }
	      }
	    }
	    ShapeEvent se=new ShapeEvent(Resource.fakeClientId(c),
		  		         DummySHAPEExtension.ctyp,
				         c, window);
	    Resource.add(se);
	    if(head==null){
	      head=new Head(window.id, DummySHAPEExtension.etyp);
	      Resource.add(head);
	    }
	    else{
	      se.next=head.next;
	    }
	    head.next=se;
  	    return;
	  }
	  // foo==0
	  if(head!=null){
	    ShapeEvent nse=null;
	    ShapeEvent se=null;
	    for(se=head.next; se!=null ; se=se.next){
	      if(se.client==c)
	        break;
	      nse=se;
	    }
	    if(se!=null){
	      Resource.freeResource(se.id, DummySHAPEExtension.ctyp);
	      if(nse!=null){
	        nse.next=se.next;
	      }
	      else{
	        head.next=se.next;
	      }
	    }
  	  }
	}
        break;
      case 7:	
	foo=io.readInt();
	c.length-=2;
	window=c.lookupWindow(foo);
	if(window==null){
	  c.errorValue=foo;
	  c.errorReason=3; // BadWindow
	  return;
	}
        byte result=0;
        {
 	  Head head=
            (Head)Resource.lookupIDByType(window.id, DummySHAPEExtension.etyp);
          if(head!=null){
            for(ShapeEvent se=head.next; se!=null; se=se.next){
              if(se.client==c){ result=1; break; }
  	    }
          } 
        }

        synchronized(io){
          io.writeByte(1);
	  io.writeByte(result);
	  io.writeShort(c.seq);
	  io.writeInt(0);
	  io.writePad(24);
	  io.flush();
	}
        break;
      case 8:	
	foo=io.readInt();
	c.length-=2;
	window=c.lookupWindow(foo);
	if(window==null){
	  c.errorValue=foo;
	  c.errorReason=3; // BadWindow
	  return;
	}

	kind=io.readByte();
	io.readPad(3);

        synchronized(io){
          io.writeByte(1);
	  io.writeByte(0);
	  io.writeShort(c.seq);
	  io.writeInt(2*1);
	  io.writeInt(1);
	  io.writePad(20);
	  io.writeShort(0);
	  io.writeShort(0);
	  io.writeShort(window.width);
	  io.writeShort(window.height);
	  io.flush();
	}
        break;
      default:
	System.err.println("Shape: unknown code="+c.data);
      }
  }

  void sendShapeNotify(int kind, Window window, 
		       int x, int y, int w, int h, int shaped)
    throws IOException{
    Head head=(Head)Resource.lookupIDByType(window.id, DummySHAPEExtension.etyp);
    if(head==null)return;
    for(ShapeEvent se=head.next; se!=null; se=se.next){
      Client c=se.client;
      if(c==Client.clients[0] || c.clientGone){
	continue;
      }
      mkShapeNotify(c.cevent, kind, window.id, x, y, w, h, shaped);
      c.cevent.putSequence(c.seq);
      c.sendEvent(1, c.cevent);
    }
  }

  void mkShapeNotify(Event e, int kind, int window, 
		     int x, int y, int w, int h, int shaped){
    e.clear();
    e.writeByte((byte)eventbase);
    e.writeByte((byte)kind);
    e.writePad(2);
    e.writeInt(window);
    e.writeShort(x); e.writeShort(y);
    e.writeShort(w); e.writeShort(h);
    int time=(int)System.currentTimeMillis();
    e.writeInt(time);
    e.writeByte((byte)shaped);
  }

  void swap(Event e){
    e.index=4;
    e.swapInt();
    e.swapShort(); e.swapShort(); 
    e.swapShort(); e.swapShort(); 
    e.swapInt();
  }

  class Head extends Resource{
    ShapeEvent next;
    Head(int id, int typ){
      super(id, typ);
      this.next=null;
    }
    void delete() throws IOException{
      ShapeEvent pNext;
      for(ShapeEvent pCur=next; pCur!=null; pCur=pNext) {
	pNext=pCur.next;
	Resource.freeResource (pCur.id, DummySHAPEExtension.ctyp);
      }
      next=null;
      return;
    }
  }

  class ShapeEvent extends Resource{
    ShapeEvent next;
    Client client;
    Window window;
    ShapeEvent(int id, int typ, Client client, Window window){
      super(id, typ);
      this.client=client;
      this.window=window;
      this.next=null;
    }

    void delete() throws IOException{
      Head head=(Head)lookupIDByType(window.id, DummySHAPEExtension.etyp);
      if(head!=null) {
	ShapeEvent pCur, pPrev=null;
	for(pCur=head.next; pCur!=null && pCur !=this; pCur=pCur.next){
	  pPrev=pCur;
	}
	if(pCur!=null){
	  if(pPrev!=null){ pPrev.next=next; }
	  else{ head.next=next; }
	}
      }
    }
  }
}
