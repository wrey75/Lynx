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

final class Selection{
  int  selection;
  int  lastTimeChanged;
  int  wid;
  Window window;
  Client client;
  static Selection[] currentSelection;
  static{
    currentSelection=new Selection[10];
  }

  Selection(int selection, int time, Window w, Client c){
    this.selection=selection;
    lastTimeChanged=time;
    window=w;
    wid=(w!=null ? w.id : 0);
    client=c;
  }

  static synchronized Selection getSelection(int selection){
    Selection p=null;
    int len=currentSelection.length;
    for(int i=0; i<len && currentSelection[i]!=null; i++){
      if(currentSelection[i].selection==selection){
	p=currentSelection[i];
	break;
      }
    }
    return p;
  }

  static synchronized void addSelection(int selection, int time, 
					Window w, Client c){
    Selection p=new Selection(selection, time, w, (w!=null ? c : null));
    if(currentSelection[currentSelection.length-1]!=null){
      Selection[] foo=new Selection[currentSelection.length*2];
      System.arraycopy(currentSelection, 0, foo, 0, currentSelection.length);
      currentSelection=foo;
    }
    int i;
    for(i=0; currentSelection[i]!=null; i++);
    currentSelection[i]=p;
  }

  static void reqConvertSelection(Client c) throws IOException{
    int foo;
    int selection, requestor, target, property, time;
    boolean paramsOkay=true;
    IO io=c.client;
    requestor=io.readInt();
    Window w=c.lookupWindow(requestor);
    if(w==null){
      c.errorValue=requestor;
      c.errorReason=3; // BadWindow;
    }
    selection=io.readInt();
    paramsOkay=Atom.valid(selection);
    target=io.readInt();
    paramsOkay &=Atom.valid(target);
    property=io.readInt();
    if (property !=0){
      paramsOkay &=Atom.valid(property);
    }
    time=io.readInt();
    c.length-=6;
    if(c.errorReason!=0){
      return;
    }

    if (paramsOkay) {
      Selection s=getSelection(selection);
      if(s!=null && s.client!=null){
	c.cevent.mkSelectionRequest(time, 
				    s.wid,
				    requestor,
				    selection,
				    target,
				    property);
	if(s.client.sendEvent(c.cevent,
				    1,
				    Event.NoEventMask,
				    Event.NoEventMask,
				    null)!=0)
	  return;
      }
      c.cevent.mkSelectionNotify(time, 
				  requestor,
				  selection,
				  target,
				  0);
      c.sendEvent(c.cevent,
		  1,
		  Event.NoEventMask,
		  Event.NoEventMask,
		  null);

      return;
    }
    else {
      //System.out.println("error!!");
      c.errorValue=property;
      c.errorReason=5; // BadAtom
      return;
    }
  }

  static void reqSetSelectionOwner(Client c) throws IOException{
    int foo;
    int selection;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    Window w=null;
    if(foo!=0){
      w=c.lookupWindow(foo);
      if(w==null){
	c.errorValue=foo;
	c.errorReason=3; // BadWindow
	return;
      }
    }

    selection=io.readInt();
    foo=io.readInt();
    c.length-=2;
    int time=0;
    time=(int)System.currentTimeMillis();
    time=foo; // ??

    if (Atom.valid(selection)){
      int i=0;
      Selection s=getSelection(selection);
      if (s!=null){
	if (s.client!=null && (w==null || (s.client!=c))){
	  if(s.client!=null){
	    c.cevent.mkSelectionClear(time, s.wid, s.selection);
	    s.client.sendEvent(c.cevent, 
			       1, 	
			       Event.NoEventMask,		      
			       Event.NoEventMask,
			       null
			       );
	  }
	}
	s.window=w;
	s.wid=(w!=null ? w.id : 0);
	s.lastTimeChanged=time;
	s.client=(w!=null ? c : null);
      }
      else{
	//System.out.println("add");
	addSelection(selection, time, w, c);
      }
      return;
    }
    else {
      c.errorValue=selection;
      c.errorReason=5;
      return;
    }
  }

  static void reqGetSelectionOwner(Client c) throws IOException{
    int foo;
    int selection;
    IO io=c.client;
    selection=io.readInt();

    c.length-=2;

    if(!Atom.valid(selection)){
      c.errorValue=selection;
      c.errorReason=5; // BadAtom
      return;
    }

    synchronized(io){
      io.writeByte(1);
      Selection s=getSelection(selection);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(0);
      if(s!=null){io.writeInt(s.wid);}
      else{io.writeInt(0);}
      io.writePad(20);
      io.flush();
    }
  }

  static synchronized void delete(Client c){
    Selection s;
    int len=currentSelection.length;
    for(int i=0; i<len && (s=currentSelection[i])!=null; i++){
      if(s.client==c){
	if(i+1==len){
	  currentSelection[i]=null;
	}
	else{
	  for(int j=i+1; j<len; j++){
	    currentSelection[j-1]=currentSelection[j];
	    if(currentSelection[j]==null)break;
	  }
	}
	s.window=null;
	s.client=null;
      }
    }
  }

  static synchronized void delete(Window w){ 
    Selection s;
    int len=currentSelection.length;
    for(int i=0; i<len && (s=currentSelection[i])!=null; i++){
      if(s.window==w){
	if(i+1==len){ currentSelection[i]=null;	}
	else{
	  for(int j=i+1; j<len; j++){
	    currentSelection[j-1]=currentSelection[j];
	    if(currentSelection[j]==null)break;
	  }
	}
	s.window=null;
	s.client=null;
      }
    }
  }
}
