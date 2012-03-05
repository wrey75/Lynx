/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - dispatching requests
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

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;

final class Client extends Thread {
  static Object LOCK=Client.class;
  private static final Object GrabServerLOCK=new Object();

  // BITSFORRESOURCES+BITSFORCLIENTS must be 29....
  static final int BITSFORRESOURCES=22;
  static final int BITSFORCLIENTS=7;

  static final int MAXCLIENTS=(1<<BITSFORCLIENTS);

  static final int CLIENTOFFSET=BITSFORRESOURCES;
  static final int CLIENTMASK=(((1<<BITSFORCLIENTS)-1)
                               <<BITSFORRESOURCES);

  static final int IDMASK=(1<<BITSFORRESOURCES)-1;
  static final int FALSE=0;
  static final int TRUE=1;

  private static final int DestroyAll=0;
  private static final int AllTemporary=0;
  private static final int RetainPermanent=1;
  private static final int RetainTemporary=2;

  private static final int ClientStateInitial=0;
  private static final int ClientStateAuthenticating=1;
  private static final int ClientStateRunning=2;
  private static final int ClientStateRetained=3;
  private static final int ClientStateGone=4;
  private static final int ClientStateCheckingSecurity=5;
  private static final int ClientStateCheckedSecurity=6;

  static final Client[] clients=new Client[MAXCLIENTS];
  static int nextClient=1;
  static int currentMaxClients=1;

  static int servergraber=-1;

  int seq;
  int index;
  boolean swap=false;
  int clientAsMask;
  int requestBuffer;

  boolean serverisgrabed=false;

  boolean suspended=false;
  boolean waitforreq=false;

  int closeDownMode=DestroyAll;
  boolean clientGone=false;

  int clientState=ClientStateInitial;

  byte[] bbuffer=null;
  byte[] sevent=null;
  char[] cbuffer=null;
  int[] xarray=new int[4];
  int[] yarray=new int[4];
  Event cevent=new Event();
  IO client;

  SaveSet saveSet=null;

  int reqType;
  int data;
  int length;

  int errorValue;
  int errorReason;

  static Vector listeners=new Vector();

  Client(){this(null);}

  Client(IO client){
    if(client==null){
      closeDownMode=RetainPermanent;
      index=0;
      return;
    }
    this.client=client; 

    if(!(client instanceof IOMSB)) swap=true;
    else swap=false;

    bbuffer=new byte[1024];
    cbuffer=new char[1024];

    if(swap)sevent=new byte[32];

    index=-1;

    synchronized(LOCK){
      if(servergraber!=-1){ this.serverisgrabed=true; }
      else{ this.serverisgrabed=false; }
      if(nextClient<MAXCLIENTS){
	clients[nextClient]=this;
	index=nextClient;
	clientAsMask=index<<CLIENTOFFSET;
	while(nextClient<MAXCLIENTS){
	  if(clients[nextClient]==null)break;
	  nextClient++;
	}
	if(index==currentMaxClients){
	  currentMaxClients++;
	}
	Resource.initClientResource(this);
      }
      else{
        index=-1;
        return;
      }
    }

    connected(index);

    if(index!=-1){
      try{init();}
      catch(Exception e){
      }
    }

//  if(index!=-1){ start(); }
//  else{ System.err.println("running over clients table"); }
  }

  public static void addListener(ClientListener cl){ 
    listeners.addElement(cl);
  }
  public static void removeListener(ClientListener cl){
    listeners.removeElement(cl);
  }
  static void connected(int index){
    for (Enumeration e = listeners.elements() ; e.hasMoreElements();){    
      ((ClientListener)(e.nextElement())).connected(index);
    }
  }
  static void disconnected(int index){
    for (Enumeration e = listeners.elements() ; e.hasMoreElements();){    
      ((ClientListener)(e.nextElement())).disconnected(index);
    }
  }

  public void run(){
    int foo;
    int n;

    try{
      while(true){
	waitforreq=true;
	errorReason=0;
	reqType=client.readByte(); 

        seq++;

	suspended=false;
	waitforreq=false;

        if(reqType==36){                           // GrabServer
          setGraber(index);
          if(servergraber!=index){
            suspended=true;
            while(servergraber!=index){
              try{Thread.sleep(10);}catch(Exception e){}
              setGraber(index);
            }
 	    suspended=false;
          }
        }
        else{
          if(servergraber!=-1 && servergraber!=index){
            suspended=true;
            while(servergraber!=-1 && servergraber!=index){
              try{Thread.sleep(10);}catch(Exception e){}
	    }
 	    suspended=false;
    	  }
	}
	data=client.readByte();
	length=client.readShort();
 	switch(reqType){
	case 1:
	  Window.reqCreateWindow(this);
	  break;
	case 2:
	  Window.reqChangeWindowAttributes(this);
	  break;
	case 3:
	  Window.reqGetWindowAttributes(this);
	  break;
	case 4:
	  Window.reqDestroyWindow(this);
	  break;
	case 5:
	  Window.reqDestroySubwindows(this);
	  break;
	case 6:
	  SaveSet.reqChangeSaveSet(this);
	  break;
	case 7:
	  Window.reqReparentWindow(this);
	  break;
	case 8:
	  Window.reqMapWindow(this);
	  break;
	case 9:
	  Window.reqMapSubWindows(this);
	  break;
	case 10:
	  Window.reqUnmapWindow(this);
	  break;
	case 11:
	  Window.reqUnmapSubWindows(this);
	  break;
	case 12:
	  Window.reqConfigureWindow(this);
	  break;
	case 13:
	  Window.reqCirculateWindow(this);
	  break;
	case 14:
	  Window.reqGetGeometry(this);
	  break;
	case 15:
	  Window.reqQueryTree(this);
	  break;
	case 16:
	  Atom.reqInternAtom(this);
	  break;
	case 17:
	  Atom.reqGetAtomName(this);
	  break;
	case 18:
	  Property.reqChangeProperty(this);
          break;
	case 19:
	  Property.reqDeleteProperty(this);
	  break;
	case 20:
	  Property.reqGetProperty(this);
	  break;
	case 21:
	  Property.reqListProperties(this);
	  break;
	case 22:
	  Selection.reqSetSelectionOwner(this);
	  break;
	case 23:
	  Selection.reqGetSelectionOwner(this);
	  break;
	case 24:
	  Selection.reqConvertSelection(this);
	  break;
	case 25:
	  Window.reqSendEvent(this);
	  break;
	case 26:
	  Window.reqGrabPointer(this);
	  break;
	case 27:
	  Window.reqUngrabPointer(this);
	  break;
	case 28:
	  Window.reqGrabButton(this);
	  break;
	case 29:
	  Window.reqUngrabButton(this);
	  break;
	case 30:
          // ChangeActivePointerGrab
	  foo=client.readInt();
	  foo=client.readInt();
	  foo=client.readShort();
	  client.readPad(2);
	  break;
	case 31:
	  Keyboard.reqGrabKeyboard(this);
	  break;
	case 32:
          // UngrabKeyboard
	  foo=client.readInt();
	  break;
	case 33:
          // GrabKey
	  foo=client.readInt();
	  foo=client.readShort();
	  foo=client.readByte();
	  foo=client.readByte();
	  foo=client.readByte();
	  client.readPad(3);
	  break;
	case 34:
          // UngrabKey
	  foo=client.readInt();
	  foo=client.readShort();
	  client.readPad(2);
	  break;
	case 35:
	  Window.reqAllowEvents(this);
	  break;
	case 36:
	  Client.reqGrabServer(this);
	  break;
	case 37:
	  Client.reqUngrabServer(this);
	  break;
	case 38:
	  Window.reqQueryPointer(this);
	  break;
	case 39:
	  Window.reqGetMotionEvents(this);
	  break;
	case 40:
	  Window.reqTranslateCoordinates(this);
	  break;
	case 41:
          // WarpPointer
	  foo=client.readInt();
	  foo=client.readInt();
	  foo=client.readShort();
	  foo=client.readShort();
	  foo=client.readShort();
	  foo=client.readShort();
	  foo=client.readShort();
	  foo=client.readShort();
	  break;
	case 42:
	  Window.reqSetInputFocus(this);
	  break;
	case 43:
	  Window.reqGetInputFocus(this);
	  break;
	case 44:
	  Keyboard.reqQueryKeymap(this);
	  break;
	case 45:
	  Font.reqOpenFont(this);
	  break;
	case 46:
	  Font.reqCloseFont(this);
	  break;
	case 47:
	  Font.reqQueryFont(this);
	  break;
	case 48:
	  Font.reqQueryTextExtents(this);
	  break;
	case 49:
	  Font.reqListFonts(this);
	  break;
	case 50:
	  Font.reqListFontsWithInfo(this);
	  break;
	case 51:
	  Font.reqSetFontPath(this);
	  break;
	case 52:
	  Font.reqGetFontPath(this);
	  break;
	case 53:
	  Pixmap.reqCreatePixmap(this);
	  break;
	case 54:
	  Pixmap.reqFreePixmap(this);
	  break;
	case 55:
	  GC.reqCreateGC(this);
	  break;
	case 56:
	  GC.reqChangeGC(this);
	  break;
	case 57:
	  GC.reqCopyGC(this);
	  break;
	case 58:
	  GC.reqSetDashes(this);
	  break;
	case 59:
	  GC.reqSetClipRectangles(this);
	  break;
	case 60:
	  GC.reqFreeGC(this);
	  break;
	case 61:
	  Window.reqClearArea(this);
	  break;
	case 62:
	  Window.reqCopyArea(this);
	  break;
	case 63:
	  Window.reqCopyPlane(this);
	  break;
	case 64:
	case 65:
	case 66:
	case 67:
	case 68:
	case 69:
	case 70:
	case 71:
	case 74:
	case 75:
	case 76:
	case 77:
	  foo=client.readInt();
	  length-=2;
	  Drawable d=lookupDrawable(foo);
	  if(d==null){
	    errorValue=foo;
	    errorReason=9; // BadDrawable; ??
	    break;
	  }
	  foo=client.readInt();
	  length--;
	  GC gc=lookupGC(foo);
	  if(gc==null){
	    errorValue=foo;
	    errorReason=13; // GContext
	    break;
	  }
	  switch(reqType){
	    case 64:
	      Draw.reqPolyPoint(this, d, gc);
	      break;
	    case 65:
	      Draw.reqPolyLine(this, d, gc);
	      break;
	    case 66:
	      Draw.reqPolySegment(this, d, gc);
	      break;
	    case 67:
	      Draw.reqPolyRectangle(this, d, gc);
	      break;
	    case 68:
	      Draw.reqPolyArc(this, d, gc);
	      break;
	    case 69:
	      Draw.reqFillPoly(this, d, gc);
	      break;
	    case 70:
	      Draw.reqPolyFillRectangle(this, d, gc);
	      break;
	    case 71:
	      Draw.reqFillPolyArc(this, d, gc);
	      break;
	    case 74:
	      int x=(short)client.readShort();
	      int y=(short)client.readShort();
	      length--;
	      Draw.reqPolyText8(this, d, gc, x, y);
	      break;
	    case 75:
	      x=(short)client.readShort();
	      y=(short)client.readShort();
	      length--;
	      Draw.reqPolyText16(this, d, gc, x, y);
	      break;
	    case 76:
	      x=(short)client.readShort();
	      y=(short)client.readShort();
	      length--;
	      Draw.reqImageText8(this, d, gc, x, y);
	      break;
	    case 77:
	      x=(short)client.readShort();
	      y=(short)client.readShort();
	      length--;
	      Draw.reqImageText16(this, d, gc, x, y);
	      break;
	  }
	  break;
	case 72:
	  Pixmap.reqPutImage(this);
	  break;
	case 73:
	  Pixmap.reqGetImage(this);
	  break;
	case 78:
	  Colormap.reqCreateColormap(this);
	  break;
	case 79:
	  // FreeColormap
	  foo=client.readInt();
	  break;
	case 80:
          // CopyColormapAndFree
	  foo=client.readInt();
	  foo=client.readInt();
	  break;
	case 81:
	  Colormap.reqInstallColormap(this);
	  break;
	case 82:
	  Colormap.reqUninstallColormap(this);
	  break;
	case 83:
	  Colormap.reqListInstalledColormaps(this);
	  break;
	case 84:
	  Colormap.reqAllocColor(this);
	  break;
	case 85:
	  Colormap.reqAllocNamedColor(this);
	  break;
	case 86:
	  Colormap.reqAllocColorCells(this);
	  break;
	case 87:
	  Colormap.reqAllocColorPlanes(this);
	  break;
	case 88:
	  Colormap.reqFreeColors(this);
	  break;
	case 89:
	  Colormap.reqStoreColors(this);
	  break;
	case 90:
	  Colormap.reqStoreNamedColor(this);
	  break;
	case 91:
	  Colormap.reqQueryColors(this);
	  break;
	case 92:
	  Colormap.reqLookupColor(this);
	  break;
	case 93:
	  Cursor.reqCreateCursor(this);
	  break;
	case 94:
	  Cursor.reqCreateGlyphCursor(this);
	  break;
	case 95:
	  Cursor.reqFreeCursor(this);
	  break;
	case 96:
	  Cursor.reqRecolorCursor(this);
	  break;
	case 97:
	  Window.reqQueryBestSize(this);
	  break;
	case 98:
	  Extension.reqQueryExtension(this);
	  break;
	case 99:
	  Extension.reqListExtensions(this);
	  break;
	case 100:
	  Keyboard.reqChangeKeyboardMapping(this);
	  break;
	case 101:
	  Keyboard.reqGetKeyboardMapping(this);
	  break;
	case 102:
          // ChangeKeyboardControl
          n=length;
	  foo=client.readInt();
	  n-=2;
	  client.readPad(n*4);
	  break;
	case 103:
	  Keyboard.reqGetKeyboardControl(this);
	  break;
	case 104:
          // Bell
	  Toolkit.getDefaultToolkit().beep();
	  break;
	case 105:
          // ChangePointerControl
	  foo=length;
	  foo=client.readShort();
	  foo=client.readShort();
	  foo=client.readShort();
	  foo=client.readByte();
	  foo=client.readByte();
	  break;
	case 106:
	  Window.reqGetPointerControl(this);
	  break;
	case 107:
          // SetScreenSaver
	  foo=client.readShort();
	  foo=client.readShort();
	  foo=client.readByte();
	  foo=client.readByte();
	  client.readPad(2);
	  break;
	case 108:
          Window.reqGetScreenSaver(this);
	  break;
	case 109:
	  Acl.reqChangeHosts(this);
	  break;
	case 110:
	  Acl.reqListHosts(this);
	  break;
	case 111:
	  Acl.reqSetAccessControl(this);
	  break;
	case 112:
          // SetCloseDownMode
	  length--;
	  if(data==AllTemporary || 
	     data==RetainPermanent || 
	     data==RetainTemporary){
	    closeDownMode=data;
	  }
	  else{
	    errorValue=data;
	    errorReason=2;
	  }
	  break;
	case 113:
	  Client.reqKillClient(this);
	  break;
	case 114:
	  Property.reqRotateProperties(this);
	  break;
	case 115:
          // ForceScreenSaver
	  break;
	case 116:
	  Window.reqSetPointerMapping(this);
	  break;
	case 117:
	  Window.reqGetPointerMapping(this);
	  break;
	case 118:
	  Keyboard.reqSetModifierMapping(this);
	  break;
	case 119:
	  Keyboard.reqGetModifierMapping(this);
	  break;
	case 127:
          // NoOperation
	  n=length;
	  n--;
	  while(n!=0){
	    foo=client.readInt();
	    n--;
	  }
	  break;
	default:
	  if(reqType==-1){
	    //System.err.println("error: "+reqType);
	    return;
	  }
	  Extension.dispatch(reqType, this);
	  break;
	}
	if(errorReason!=0){
	  while(length!=0){
	    client.readInt();
	    length--;
	  }
	  cevent.mkError(errorReason, errorValue, 
			 (reqType<=127 ? 0 : data), reqType);
	  sendEvent(cevent, 
		    1, 
		    0,
		    Event.NoEventMask,
		    null
		    );
	}
	client.flush();
      }
    }
    catch(Exception e){
      closeDown();
      return;
    }
  }

  static int major=11;
  static int minor=0;
  static int releaseNumber=0;
  static int motionBufferSize=0;
  static int maxRequestLength=65535;
  static byte[] vendor=null;
//static int imageByteOrder=1;
//static int bitmapBitOrder=1;
  static int bitmapScanUnit=32;
  static int bitmapScanPad=32;
  static int initialLength=0;

  void writeByte(IO out) throws java.io.IOException{
    synchronized(out){
      out.writeByte(1);
      out.writePad(1);
      out.writeShort(major);        
      out.writeShort(minor);
      out.writeShort(initialLength);  
      out.writeInt(releaseNumber);
      out.writeInt(clientAsMask);
      out.writeInt(IDMASK);

      out.writeInt(motionBufferSize);
      out.writeShort(vendor.length);
      out.writeShort(maxRequestLength);
      out.writeByte(Screen.screen.length);
      out.writeByte(Format.format.length);
      out.writeByte(WeirdX.imageByteOrder);
      out.writeByte(WeirdX.bitmapBitOrder);
      out.writeByte(bitmapScanUnit);
      out.writeByte(bitmapScanPad);
      out.writeByte(Keyboard.keyboard.minKeyCode);
      out.writeByte(Keyboard.keyboard.maxKeyCode);
      out.writePad(4);
      out.writeByte(vendor);
      if(((-vendor.length)&3)!=0){ out.writePad((-vendor.length)&3); }
      for(int i=0; i<Format.format.length; i++){ 
        Format.format[i].writeByte(out); 
      }
      for(int i=0; i<Screen.screen.length; i++){ 
        Screen.screen[i].writeByte(out); 
      }
      out.flush();
    }
  }

  private final void prolog() throws java.io.IOException{
    int foo;
    foo=client.readByte(); 
    foo=client.readShort(); 
    foo=client.readShort(); 
    int name=foo=client.readShort(); 
    int data=foo=client.readShort(); 
    client.readPad(2);
    if(name>0){
      client.readByte(bbuffer, 0, name);
      client.readPad((-name)&3);
    }
    if(data>0){
      client.readByte(bbuffer, 0, data);
      client.readPad((-data)&3);
    }
  }

  private final void init() throws java.io.IOException{
    prolog();
    writeByte(client);
    seq=0;
  }

  private static final void reqGrabServer(Client c) throws IOException{
    synchronized(LOCK){
      for(int i=1; i<clients.length; i++){
	if(i==c.index) continue;
	if(clients[i]==null) continue; 
	if(!clients[i].isAlive())continue; 
	clients[i].serverisgrabed=true;
      }
    }
    int i=0;
    int ii=100;
    while(true){
      try{Thread.yield();}catch(Exception e){}
      for(i=1; i<clients.length; i++){
	if(i==c.index ||
	   clients[i]==null ||
	   !clients[i].isAlive() ||
	   clients[i].waitforreq ||
	   clients[i].suspended)
	  continue; 
//	System.out.println("wait for: "+i);
	i=0;
	break;
      }
      if(i!=0)break;
      ii--;
      if(ii==0){
//	System.out.println("give up..: "+i);
	break;
      }
    }
  }

  private static final void reqUngrabServer(Client c) throws IOException{
    synchronized(LOCK){
      for(int i=1; i<clients.length; i++){
	if(i==c.index ||
	   clients[i]==null ||
	   !clients[i].isAlive()){
	  continue; 
	}
	clients[i].serverisgrabed=false;
      }
      unGraber();
    }
  }

  private static final void reqKillClient(Client c) throws IOException{
    int foo;
    foo=c.client.readInt();
    c.length-=2;
    if (foo==AllTemporary){
      closeDownRetainedResources();
      return;
    }

    Client killclient;
    if ((killclient=Resource.lookupClient(foo))!=null){
      killclient.closeDown();
    }
    else {
      c.errorValue=foo;
      c.errorReason=2; // BadValue;
    }
    return;
  }

  static final void closeDownRetainedResources(){
    synchronized(LOCK){
      for(int i=1; i<clients.length;i++){
	Client c=clients[i];
	if(c!=null 
	   && (c.closeDownMode==RetainTemporary)
	   && (c.clientGone)
	   ){
	  c.closeDown();
	}
      }
    }
  }

  static final void closeDownAll(){
    synchronized(LOCK){
      for(int i=1; i<clients.length;i++){
	if(clients[i]!=null){
	  clients[i].closeDown();
	}
      }
    }
  }

  /*private*/ final void closeDown(){
    boolean really_close_down=clientGone || closeDownMode==DestroyAll;
    synchronized(LOCK){
      if(!clientGone){
	if(servergraber==index){
	  for(int i=1; i<clients.length; i++){
	    if(i==index ||
	       clients[i]==null ||
	       !clients[i].isAlive()){
	      continue; 
	    }
	    clients[i].serverisgrabed=false;
	  }
	  unGraber();
	}
	if(Window.grab!=null &&
	   this!=Window.grab.getClient()){
	  Window.grab=null;
	}
	Selection.delete(this);

	if(!really_close_down){
	  Resource.freeClientNeverResources(this);
	}
	clientGone=true;
	if(client!=null) try{client.close();}catch(Exception ee){}
      }

      if (really_close_down){
	try{SaveSet.handle(this);}
	catch(Exception ee){ 
	  // System.out.println("saveSet.hanlde: "+ee);
	}      
	Resource.freeClientResources(this);
	if(index < nextClient){
	  nextClient=index;
	}
	clients[index]=null;
	while (clients[currentMaxClients-1]==null){
	  currentMaxClients--;
	}
	disconnected(index);
      }
    }
//    try{stop();}catch(Exception e){}
  }

  final int sendEvent(Event event, int count, 
		      int mask, int filter, Grab grab)
    throws java.io.IOException{
    if((this!=clients[0]) && ((filter==0) || ((mask & filter)!=0))){
      if((grab!=null) && !grab.sameClient(this)){
        return -1;
      }
      synchronized(client){
	event.putSequence(seq);
	sendEvent(count, event);
      }
      return 1;      
    }
    else{
      return 0;
    }
  }

  void sendEvent(int count, Event event) throws java.io.IOException{
    byte[] bb;
    if(swap){ 
      bb=sevent;
      event.swap(bb);
    }
    else{ bb=event.event; }

    try{
      client.immediateWrite(bb, 0, 32);
    }
    catch(IOException e){
      if(!clientGone){
	closeDown();
      }
    }
  }

  int bag1id=0, bag2id=0;
  Drawable bag1d, bag2d;
  Drawable lookupDrawable(int id){
    if(bag1id==id) return bag1d;
    if(bag2id==id){
      bag2id=bag1id; bag1id=id;
      Drawable d=bag2d; bag2d=bag1d; bag1d=d;
      return d;
    }
    Resource r=Resource.lookupIDByClass(id, Resource.RC_DRAWABLE);
    if((r!=null) && (r instanceof Drawable)){
      bag2id=bag1id; bag2d=bag1d;
      bag1id=id; bag1d=(Drawable)r;
      return bag1d;
    }
    return null;
  }

  Window lookupWindow(int id){
    Drawable d=lookupDrawable(id);
    if(d==null || (d instanceof Window)) return (Window)d;
    return null;
  }

  int bag1gid=0, bag2gid=0;
  GC bag1gc, bag2gc;
  GC lookupGC(int id){
    if(bag1gid==id) return bag1gc;
    if(bag2gid==id){
      bag2gid=bag1gid; bag1gid=id;
      GC gc=bag2gc; bag2gc=bag1gc; bag1gc=gc;
      return gc;
    }
    Resource r=Resource.lookupIDByType(id, Resource.RT_GC);
    if((r!=null) && (r instanceof GC)){
      bag2gid=bag1gid; bag2gc=bag1gc;
      bag1gid=id; bag1gc=(GC)r;
      return bag1gc;
    }
    return null;
  }

  static void flushCache(int id){        // synchronized in Resource.java
    for(int i=1; i<Client.clients.length;i++){
      Client c=Client.clients[i];
      if(c!=null){
	if(c.bag1id==id){
	  c.bag1id=c.bag2id; c.bag1d=c.bag2d;
	  c.bag2id=0; c.bag2d=null;
	  continue;
	}
	if(c.bag2id==id){
	  c.bag2id=0; c.bag2d=null;
	  continue;
	}
	if(c.bag1gid==id){
	  c.bag1gid=c.bag2gid; c.bag1gc=c.bag2gc;
	  c.bag2gid=0; c.bag2gc=null;
	  continue;
	}
	if(c.bag2gid==id){
	  c.bag2gid=0; c.bag2gc=null;
	  continue;
	}
      }
    }
  }

  private static final int setGraber(int i){
    synchronized(GrabServerLOCK){
      if(servergraber==-1) servergraber=i;
      return servergraber;
    }
  }

  private static final void unGraber(){
    servergraber=-1;
  }
}
