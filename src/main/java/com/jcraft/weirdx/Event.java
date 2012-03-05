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
import java.util.*;

final class Event {
  static final int KeyPress=2;
  static final int KeyRelease=3;
  static final int ButtonPress=4;
  static final int ButtonRelease=5;
  static final int MotionNotify=6;
  static final int EnterNotify=7;
  static final int LeaveNotify=8;
  static final int FocusIn=9;
  static final int FocusOut=10;
  static final int KeymapNotify=11;
  static final int Expose=12;
  static final int GraphicsExpose=13;
  static final int NoExpose=14;
  static final int VisibilityNotify=15;
  static final int CreateNotify=16;
  static final int DestroyNotify=17;
  static final int UnmapNotify=18;
  static final int MapNotify=19;
  static final int MapRequest=20;
  static final int ReparentNotify=21;
  static final int ConfigureNotify=22;
  static final int ConfigureRequest=23;
  static final int GravityNotify=24;
  static final int ResizeRequest=25;
  static final int CirculateNotify=26;
  static final int CirculateRequest=27;
  static final int PropertyNotify=28;
  static final int SelectionClear=29;
  static final int SelectionRequest=30;
  static final int SelectionNotify=31;
  static final int ColormapNotify=32;
  static final int ClientMessage=33;
  static final int MappingNotify=34;
  static final int LASTEvent=35;

  static final int NoEventMask=0;
  static final int KeyPressMask=(1<<0);
  static final int KeyReleaseMask=(1<<1);
  static final int ButtonPressMask=(1<<2);
  static final int ButtonReleaseMask=(1<<3);
  static final int EnterWindowMask=(1<<4);
  static final int LeaveWindowMask=(1<<5);  
  static final int PointerMotionMask=(1<<6);  
  static final int PointerMotionHintMask=(1<<7);  
  static final int Button1MotionMask=(1<<8);  
  static final int Button2MotionMask=(1<<9);  
  static final int Button3MotionMask=(1<<10); 
  static final int Button4MotionMask=(1<<11); 
  static final int Button5MotionMask=(1<<12); 
  static final int ButtonMotionMask=(1<<13); 
  static final int KeymapStateMask=(1<<14);
  static final int ExposureMask=(1<<15); 
  static final int VisibilityChangeMask=(1<<16); 
  static final int StructureNotifyMask=(1<<17); 
  static final int ResizeRedirectMask=(1<<18); 
  static final int SubstructureNotifyMask=(1<<19); 
  static final int SubstructureRedirectMask=(1<<20); 
  static final int FocusChangeMask=(1<<21); 
  static final int PropertyChangeMask=(1<<22); 
  static final int ColormapChangeMask=(1<<23); 
  static final int OwnerGrabButtonMask=(1<<24); 

  static final int Button1Mask=(1<<8);
  static final int Button2Mask=(1<<9);
  static final int Button3Mask=(1<<10);
  static final int Button4Mask=(1<<11);
  static final int Button5Mask=(1<<12);

  static final int NoSuchEvent=0x80000000;
  static final int StructureAndSubMask=
    ( StructureNotifyMask | SubstructureNotifyMask );

  static final int CantBeFiltered=NoEventMask;

  static final int[] filters={
    NoSuchEvent,		       /* 0                */
    NoSuchEvent,		       /* 1                */
    KeyPressMask,		       /* KeyPress         */
    KeyReleaseMask,		       /* KeyRelease       */
    ButtonPressMask,                   /* ButtonPress      */
    ButtonReleaseMask,	               /* ButtonRelease    */
    PointerMotionMask,	               /* MotionNotify     */
    EnterWindowMask,	               /* EnterNotify      */
    LeaveWindowMask,	               /* LeaveNotify      */
    FocusChangeMask,	               /* FocusIn          */
    FocusChangeMask,	               /* FocusOut         */
    KeymapStateMask,	               /* KeymapNotify     */
    ExposureMask,		       /* Expose           */
    NoEventMask,		       /* GraphicsExpose   */
    NoEventMask,		       /* NoExpose         */
    VisibilityChangeMask,	       /* VisibilityNotify */
    SubstructureNotifyMask,	       /* CreateNotify     */
    StructureAndSubMask,	       /* DestroyNotify    */
    StructureAndSubMask,	       /* UnmapNotify      */
    StructureAndSubMask,	       /* MapNotify        */
    SubstructureRedirectMask,          /* MapRequest       */
    StructureAndSubMask,	       /* ReparentNotify   */
    StructureAndSubMask,	       /* ConfigureNotify  */
    SubstructureRedirectMask,          /* ConfigureRequest */
    StructureAndSubMask,	       /* GravityNotify    */
    ResizeRedirectMask,	               /* ResizeRequest    */
    StructureAndSubMask,	       /* CirculateNotify  */
    SubstructureRedirectMask,          /* CirculateRequest */
    PropertyChangeMask,	               /* PropertyNotify   */
    NoEventMask,		       /* SelectionClear   */
    NoEventMask,		       /* SelectionRequest */
    NoEventMask,		       /* SelectionNotify  */
    ColormapChangeMask,	               /* ColormapNotify   */
    NoEventMask,		       /* ClientMessage    */
    NoEventMask		               /* MappingNotify    */
  };

  static final int AtMostOneClient=
	(SubstructureRedirectMask | ResizeRedirectMask | ButtonPressMask);

  static final int MotionMask=
        (PointerMotionMask | Button1MotionMask |
	Button2MotionMask | Button3MotionMask | Button4MotionMask |
	Button5MotionMask | ButtonMotionMask );

  static final int  PropagateMask=
        (KeyPressMask | KeyReleaseMask | ButtonPressMask | 
	 ButtonReleaseMask | MotionMask );

  static final int  PointerGrabMask=(
	ButtonPressMask | ButtonReleaseMask |
	EnterWindowMask | LeaveWindowMask |
	PointerMotionHintMask | KeymapStateMask |
	MotionMask );

  static final int EXTENSION_EVENT_BASE=64;

  byte[] event;
  int index=0;
  static final byte[] zeros=new byte[32];
  static{
    for(int i=0; i<32; i++)zeros[i]=0;
  }

  Event(byte[] bb){ event=bb; }
  Event(){ event=new byte[32]; }

  void clear(){
    System.arraycopy(zeros, 0, event, 0, 32);
    index=0;
  }

  void writeByte(byte val){
    event[index++]=val;
  }

  void writeShort(int val){
    event[index++]=(byte)((val>>8)&0xff);
    event[index++]=(byte)(val&0xff);
  }

  void writeInt(int val){
    event[index++]=(byte)((val>>24)&0xff);
    event[index++]=(byte)((val>>16)&0xff);
    event[index++]=(byte)((val>>8)&0xff);
    event[index++]=(byte)(val&0xff);
  }

  void writePad(int i){ index+=i;}

  void _clear(){
    System.arraycopy(zeros, 0, event, 0, 32);
    index=0;
  }

  private void _writeByte(byte val){
    event[index++]=val;
  }

  private void _writeShort(int val){
    event[index++]=(byte)((val>>8)&0xff);
    event[index++]=(byte)(val&0xff);

  }

  private void _writeInt(int val){
    event[index++]=(byte)((val>>24)&0xff);
    event[index++]=(byte)((val>>16)&0xff);
    event[index++]=(byte)((val>>8)&0xff);
    event[index++]=(byte)(val&0xff);

  }

  private void _writePad(int i){ index+=i;}

  void putEvent(int e){ 
    index=4;
    _writeInt(e);
  }

  void putSequence(int seq){ 
    index=2;
    _writeShort(seq);
  }

  private int readShort(){
    int s=0;
    s=event[index++]&0xff;
    s=((s<<8)&0xffff) | (event[index++]&0xff);
    return s;
  }

  int getState(){ 
    index=28;
    return readShort();
  }
  int getSameScreen(){ return event[30]; }
  int getDetail(){ return event[1]; }
  int getFlags(){ return event[31]; }

  void fixUpEventFromWindow(Window w, 
                            int child, 
                            int rootx, int rooty, 
                            boolean calcChild){
    int flags=0;
    if(calcChild){
      Window tmpw=Window.spriteTrace[Window.spriteTraceGood-1];
      while(tmpw!=null){
	if(tmpw==w){
	  child=0;
	  break;
	}
	if(tmpw.parent==w){
	  child=w.id;
	  break;
	}
	tmpw=tmpw.parent;
      }
    }
    int ex=0, ey=0;
    flags=1;
    ex=rootx-w.x;
    ey=rooty-w.y;
    index=0;
    _writePad(8);
    _writeInt(Window.spriteTrace[0].id);
    _writeInt(w.id);
    _writeInt(child);
    _writeShort(rootx);
    _writeShort(rooty);
    _writeShort(ex);
    _writeShort(ey);
    _writePad(2);
    _writeByte((byte)flags);
  }

  void mkCirculateRequest(int parent, int window, int place){
    _clear();
    _writeByte((byte)27);
    _writePad(1); _writePad(2);
    _writeInt(parent); 
    _writeInt(window);
    _writePad(4);
    _writeByte((byte)place);
    //_writePad(15);
  }
  void mkCirculateNotify(int event, int window, int parent, int place){
    _clear();
    _writeByte((byte)26);
    _writePad(1); _writePad(2);
    _writeInt(event);
    _writeInt(window);
    _writeInt(parent); 
    _writeByte((byte)place);
    //_writePad(15);
  }
  void mkColormapNotify(int window, int cmap, int neww, int state){
    _clear();
    _writeByte((byte)32);
    _writePad(1);
    _writePad(2);
    _writeInt(window);
    _writeInt(cmap);
    _writeByte((byte)neww);
    _writeByte((byte)state);
    //_writePad(18);
  }
  void mkFocusIn(int detail, int event, int mode){
    _clear();
    _writeByte((byte)9);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt(event);
    _writeByte((byte)mode);
    //_writePad(23);
  }
  void mkFocusOut(int detail, int event, int mode){
    _clear();
    _writeByte((byte)10);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt(event);
    _writeByte((byte)mode);
    //_writePad(23);
  }
  void mkPropertyNotify(int window,
			int atom,
			int time,
			int state){
    _clear();
    _writeByte((byte)28);
    _writePad(1);
    _writePad(2);
    _writeInt(window);
    _writeInt(atom);
    _writeInt(time);
    _writeByte((byte)state);
    //_writePad(15);
  }

  void mkSelectionNotify(int time,
			 int requestor, int selection,
			 int target, int property){
    _clear();
    _writeByte((byte)31);
    _writePad(1);
    _writePad(2);
    _writeInt(time);
    _writeInt(requestor);
    _writeInt(selection);
    _writeInt(target);
    _writeInt(property);
    //_writePad(8);
  }

  void mkSelectionRequest(int time, int owner, 
			  int requestor, int selection,
			  int target, int property){
    _clear();
    _writeByte((byte)30);
    _writePad(1);
    _writePad(2);
    _writeInt(time);
    _writeInt(owner);
    _writeInt(requestor);
    _writeInt(selection);
    _writeInt(target);
    _writeInt(property);
    //_writePad(4);
  }
  void mkSelectionClear(int time, int owner, int selection){
    _clear();
    _writeByte((byte)29);
    _writePad(1);
    _writePad(2);
    _writeInt(time);
    _writeInt(owner);
    _writeInt(selection);
    //_writePad(16);
  }
  void mkVisibilityNotify(int window, int state){
    _clear();
    _writeByte((byte)15);
    _writePad(1);
    _writePad(2);
    _writeInt(window);
    _writeByte((byte)state);
    //_writePad(23);
  }
  void mkGravityNotify(int event, int window,
		      int x, int y){
    _clear();
    _writeByte((byte)24);
    _writePad(1);
    _writePad(2);
    _writeInt(event);
    _writeInt(window);
    _writeShort(x);
    _writeShort(y);
    //_writePad(16);
  }
  void mkReparentNotify(int event, int window, int parent,
		      int x, int y, int overr){
    _clear();
    _writeByte((byte)21);
    _writePad(1);
    _writePad(2);
    _writeInt(event);
    _writeInt(window);
    _writeInt(parent);
    _writeShort(x);
    _writeShort(y);
    _writeByte((byte)overr);
    _writePad(11);
  }
  void mkError(int code, int id, int minor, int major){
    _clear();
    _writeByte((byte)0);
    _writeByte((byte)code);
    _writePad(2);
    _writeInt(id);
    _writeShort(minor);
    _writeByte((byte)major);
    _writePad(21);
  }
  void mkDestroyNotify(int event, int window){
    _clear();
    _writeByte((byte)17);
    _writePad(1);
    _writePad(2);
    _writeInt(event);
    _writeInt(window);
    _writePad(20);
  }
  void mkEnterNotify(int detail, int root, int event, int child,
		     int rootx, int rooty, int ex, int ey, int state,
		     int mode, int sames_focus){
    _clear();
    _writeByte((byte)7);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt((int)System.currentTimeMillis());
    _writeInt(root);
    _writeInt(event);
    _writeInt(child);
    _writeShort(rootx); _writeShort(rooty);
    _writeShort(ex); _writeShort(ey);
//    _writeShort(state);
    _writeShort(0);
    _writeByte((byte)mode);
    _writeByte((byte)sames_focus);
  }
  void mkLeaveNotify(int detail, int root, int event, int child,
		     int rootx, int rooty, int ex, int ey, int state,
		     int mode, int sames_focus){
    _clear();
    _writeByte((byte)8);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt((int)System.currentTimeMillis());
    _writeInt(root);
    _writeInt(event);
    _writeInt(child);
    _writeShort(rootx); _writeShort(rooty);
    _writeShort(ex); _writeShort(ey);
    _writeShort(state);
    _writeByte((byte)mode);
    _writeByte((byte)sames_focus);
  }
  void mkClientMessage(int window, int typ){
    _clear();
    _writeByte((byte)33);
    _writePad(1);
    _writePad(2);
    _writeInt(window);
    _writeInt(typ);
  }
  void mkKeyPress(int detail, int root, int ev, int child,
		     int rootx, int rooty, int ex, int ey, int state,
		     int sames){
    int i;
    index=0;

    event[index++]=(byte)2;
    event[index++]=(byte)detail;

    index+=2;

    i=(int)System.currentTimeMillis();
    event[index++]=(byte)((i>>24)&0xff);
    event[index++]=(byte)((i>>16)&0xff);
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=root;
    event[index++]=(byte)((i>>24)&0xff);
    event[index++]=(byte)((i>>16)&0xff);
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=ev;
    event[index++]=(byte)((i>>24)&0xff);
    event[index++]=(byte)((i>>16)&0xff);
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=child;
    event[index++]=(byte)((i>>24)&0xff);
    event[index++]=(byte)((i>>16)&0xff);
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=rootx;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=rooty;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=ex;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=ey;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=state;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    event[index++]=(byte)sames;
  }
  void mkKeyRelease(int detail, int root, int ev, int child,
		     int rootx, int rooty, int ex, int ey, int state,
		     int sames){
    int i;
    index=0;

    event[index++]=(byte)3;
    event[index++]=(byte)detail;

    index+=2;

    i=(int)System.currentTimeMillis();
    event[index++]=(byte)((i>>24)&0xff);
    event[index++]=(byte)((i>>16)&0xff);
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=root;
    event[index++]=(byte)((i>>24)&0xff);
    event[index++]=(byte)((i>>16)&0xff);
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=ev;
    event[index++]=(byte)((i>>24)&0xff);
    event[index++]=(byte)((i>>16)&0xff);
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=child;
    event[index++]=(byte)((i>>24)&0xff);
    event[index++]=(byte)((i>>16)&0xff);
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=rootx;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=rooty;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=ex;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=ey;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    i=state;
    event[index++]=(byte)((i>>8)&0xff);
    event[index++]=(byte)(i&0xff);

    event[index++]=(byte)sames;
  }
  void mkNoExposure(int drawable, int minor, int major){
    _clear();
    _writeByte((byte)14);
    _writePad(1);
    _writePad(2);
    _writeInt(drawable);
    _writeShort(minor); 
    _writeByte((byte)major);
    //_writePad(21);
  }
  void mkButtonPress(int detail, int root, int event, int child,
		     int rootx, int rooty, int ex, int ey, int state,
		     int sames){
    _clear();
    _writeByte((byte)4);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt((int)System.currentTimeMillis());
    _writeInt(root);
    _writeInt(event);
    _writeInt(child);
    _writeShort(rootx); _writeShort(rooty);
    _writeShort(ex); _writeShort(ey);
    _writeShort(state);
    _writeByte((byte)sames);
    //_writePad(1);
  }
  void mkResizeRequest(int window, int w, int h){
    _clear();
    _writeByte((byte)25);
    _writePad(1);
    _writePad(2);
    _writeInt(window);
    _writeShort(w); _writeShort(h);
    //_writePad(20);
  }
  void mkCreateNotify(int parent, int window,
		      int x, int y, int w, int h, int bw,
		      int redirect){
    _clear();
    _writeByte((byte)16);
    _writePad(1);
    _writePad(2);
    _writeInt(parent);
    _writeInt(window);
    _writeShort(x); _writeShort(y); 
    _writeShort(w); _writeShort(h); _writeShort(bw);
    _writeByte((byte)redirect);
    //_writePad(9);
  }
  void mkConfigureNotify(int window, int sibling,
			  int x, int y, int w, int h, int bw,
			  int redirect){
    _clear();
    _writeByte((byte)22);
    _writePad(1);
    _writePad(2);
    _writePad(4);
    _writeInt(window);
    _writeInt(sibling);
    _writeShort(x); _writeShort(y); 
    _writeShort(w); _writeShort(h); _writeShort(bw);
    _writeByte((byte)redirect);
    //_writePad(5);
  }
  void mkConfigureRequest(int smode, int parent, int window, int sibling,
			  int x, int y, int w, int h, int bw,
			  int mask){
    _clear();
    _writeByte((byte)23);
    _writeByte((byte)smode);
    _writePad(2);
    _writeInt(parent);
    _writeInt(window);
    _writeInt(sibling);
    _writeShort(x); _writeShort(y); 
    _writeShort(w); _writeShort(h); _writeShort(bw);
    _writeShort(mask);
    //_writePad(4);
  }
  void mkButtonRelease(int detail, int root, int event, int child,
		     int rootx, int rooty, int ex, int ey, int state,
		     int sames){
    _clear();
    _writeByte((byte)5);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt((int)System.currentTimeMillis());
    _writeInt(root);
    _writeInt(event);
    _writeInt(child);
    _writeShort(rootx); _writeShort(rooty);
    _writeShort(ex); _writeShort(ey);
    _writeShort(state);
    _writeByte((byte)sames);
    //_writePad(1);
  }
  void mkMotionNotify(int detail, int root, int event, int child,
		      int rootx, int rooty, int ex, int ey, int state,
		      int sames){
    _clear();
    _writeByte((byte)6);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt((int)System.currentTimeMillis());
    _writeInt(root);
    _writeInt(event);
    _writeInt(child);
    _writeShort(rootx); _writeShort(rooty);
    _writeShort(ex); _writeShort(ey);
    _writeShort(state);
    _writeByte((byte)sames);
    //_writePad(1);
  }
  void mkPointer(int type, int detail, int time, int child, 
		  int rootx, int rooty, int state, int mode, int flags){
    index=0;
    _writeByte((byte)type);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt(time);
    _writePad(4);
    _writePad(4);
    _writeInt(child);
    _writeShort(rootx);
    _writeShort(rooty);
    _writePad(4);
    _writeShort(state);
    _writeByte((byte)mode);
    _writeByte((byte)flags);
  }

  void setPointer(int type, int detail, int time, int child, 
		  int rootx, int rooty, int state, int mode, int flags){
    index=0;
    _writeByte((byte)type);
    _writeByte((byte)detail);
    _writePad(2);
    _writeInt(time);
    _writePad(8);
    _writeInt(child);
    _writeShort(rootx);
    _writeShort(rooty);
    _writePad(4);
    _writeShort(state);
    _writeByte((byte)mode);
    _writeByte((byte)flags);
  }

  void setType(byte bb){event[0]=bb;}
  byte getType(){return event[0];}

  void mkExpose(int id, int x, int y, int w, int h, int count){
    _clear();
    _writeByte((byte)12);
    _writePad(1); _writePad(2);
    _writeInt(id);
    _writeShort(x);_writeShort(y);_writeShort(w);_writeShort(h);
    _writeShort(count);
    //_writePad(14);
  }

  void mkGraphicsExposure(int id, 
                         int x, int y, int w, int h, 
                         int minorEvent, int count, int majorEvent){
    _clear();
    _writeByte((byte)13);
    _writePad(1); _writePad(2);
    _writeInt(id);
    _writeShort(x);_writeShort(y);_writeShort(w);_writeShort(h);
    _writeShort(minorEvent);
    _writeShort(count);
    _writeByte((byte)majorEvent);
    //_writePad(14);
  }

  void mkMapNotify(int window, int override){

    _clear();
    _writeByte((byte)19);
    _writePad(1); _writePad(2);
    _writeInt(window); //_writePad(4);
    _writeInt(window);
    _writeByte((byte)override);
    //_writePad(19);
  }
  void mkUnmapNotify(int window, int from){
    _clear();
    _writeByte((byte)18);
    _writePad(1); _writePad(2);
    _writePad(4);
    _writeInt(window);
    _writeByte((byte)from);
    //_writePad(19);
  }
  void mkMapRequest(int parent, int window){
    _clear();
    _writeByte((byte)20);
    _writePad(1); _writePad(2);
    _writeInt(parent);
    _writeInt(window);
    //_writePad(20);
  }

  byte[] sevent;

  void swapShort(){
    byte i=event[index];
    sevent[index]=event[index+1];
    sevent[index+1]=i;
    index+=2;
  }

  void swapInt(){
    byte i=event[index];
    sevent[index]=event[index+3];
    sevent[index+3]=i;
    i=event[index+1];
    sevent[index+1]=event[index+2];
    sevent[index+2]=i;
    index+=4;
  }

  private void _swapShort(){
    byte i=event[index];
    sevent[index]=event[index+1];
    sevent[index+1]=i;
    index+=2;
  }

  private void _swapInt(){
    byte i=event[index];
    sevent[index]=event[index+3];
    sevent[index+3]=i;
    i=event[index+1];
    sevent[index+1]=event[index+2];
    sevent[index+2]=i;
    index+=4;
  }

  void swap(byte[] b){
    System.arraycopy(event, 0, b, 0, 32);
    sevent=b;
    swapaux();
  }

  void swap(){
    sevent=event;
    swapaux();
  }

  private void swapaux(){
    index=2;
    byte i;
    i=event[index]; sevent[index]=event[index+1]; sevent[index+1]=i;
    index+=2;

    i=(byte)((event[0]&~0x80)&0xff);

    switch(i){
      case 0:
	_swapInt();
	_swapShort();
	break;
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:

	i=event[index]; sevent[index]=event[index+3];  sevent[index+3]=i;
	i=event[index+1]; sevent[index+1]=event[index+2]; sevent[index+2]=i;
	index+=4;

	i=event[index]; sevent[index]=event[index+3];  sevent[index+3]=i;
	i=event[index+1]; sevent[index+1]=event[index+2]; sevent[index+2]=i;
	index+=4;

	i=event[index]; sevent[index]=event[index+3];  sevent[index+3]=i;
	i=event[index+1]; sevent[index+1]=event[index+2]; sevent[index+2]=i;
	index+=4;

	i=event[index]; sevent[index]=event[index+3];  sevent[index+3]=i;
	i=event[index+1]; sevent[index+1]=event[index+2]; sevent[index+2]=i;
	index+=4;

	i=event[index]; sevent[index]=event[index+1]; sevent[index+1]=i;
	index+=2;

	i=event[index]; sevent[index]=event[index+1]; sevent[index+1]=i;
	index+=2;

	i=event[index]; sevent[index]=event[index+1]; sevent[index+1]=i;
	index+=2;

	i=event[index]; sevent[index]=event[index+1]; sevent[index+1]=i;
	index+=2;

	i=event[index]; sevent[index]=event[index+1]; sevent[index+1]=i;
	break;
      case 9:
      case 10:
	_swapInt();
	break;
      case 12:
	_swapInt();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	break;
      case 13:
	_swapInt();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	break;
      case 14:
	_swapInt();
	_swapShort();
	break;
      case 15:
	_swapInt();
	break;
      case 16:
	_swapInt();
	_swapInt();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	break;
      case 17:
      case 18:
      case 19:
      case 20:
	_swapInt();
	_swapInt();
	break;
      case 21:
	_swapInt();
	_swapInt();
	_swapInt();
	_swapShort();
	_swapShort();
	break;
      case 22:
	_swapInt();
	_swapInt();
	_swapInt();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	break;
      case 23:
	_swapInt();
	_swapInt();
	_swapInt();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	_swapShort();
	break;
      case 24:
	_swapInt();
	_swapInt();
	_swapShort();
	_swapShort();
	break;
      case 25:
	_swapInt();
	_swapShort();
	_swapShort();
	break;
      case 26:
      case 27:
      case 28:
      case 29:
	_swapInt();
	_swapInt();
	_swapInt();
	break;
      case 30:
	_swapInt();
	_swapInt();
	_swapInt();
	_swapInt();
	_swapInt();
	_swapInt();
	break;
      case 31:
	_swapInt();
	_swapInt();
	_swapInt();
	_swapInt();
	_swapInt();
	break;
      case 32:
	_swapInt();
	_swapInt();
	break;
      case 33:
	_swapInt();
	_swapInt();  // type
        switch(event[1]){
 	  case 32:
            for(int ii=0; ii<5; ii++) 
              _swapInt();
            break;
 	  default:
	}
	break;
      default: // 11, 34
        if(64<i){
	  Extension.swap((int)i, this);
	}
	//System.out.println("default: "+i);
    }
  }
}
