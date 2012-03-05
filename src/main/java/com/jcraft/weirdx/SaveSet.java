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

final class SaveSet{
  private static final int SetModeInsert=0;
  private static final int SetModeDelete=1;
  private Vector bag;

  private SaveSet(){
    bag=new Vector();
  }

  static void reqChangeSaveSet(Client c) throws IOException{
    int foo;
    foo=c.client.readInt();
    c.length-=2;
    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }
    if(c.data!=SetModeInsert && c.data!=SetModeDelete){
      c.errorValue=c.data;
      c.errorReason=2; // BadWindow;
      return;
    }
    if(c.saveSet==null){
      c.saveSet=new SaveSet();
    }
    c.saveSet.proc(w, c.data);
  }

  void proc(Window w, int mode){
    if(mode==SetModeInsert){
      bag.addElement(w);
      return;
    }
    bag.removeElement(w);
  }

  void delete() throws IOException{
    bag.removeAllElements();
    bag=null;
  }

  static void delete(Window w){
    for (int i=0; i< Client.currentMaxClients; i++) {    
      Client c=Client.clients[i];
      if (c!=null && c.saveSet!=null){
        c.saveSet.proc(w, SetModeDelete);
      }
    }
  }

  static void handle(Client c) throws IOException{
    if(c.saveSet!=null){
      SaveSet ss=c.saveSet;
      for (Enumeration e=ss.bag.elements() ; e.hasMoreElements();){    
	Window w=(Window)e.nextElement();
	//System.out.println("saveset.handle: w="+Integer.toHexString(w.id));
	Window parent=w.parent;
	while(parent!=null &&
	      (Client.clients[((parent.id & Client.CLIENTMASK) >> Client.CLIENTOFFSET)]==c)){
	  parent=parent.parent;
	}
	if(parent!=null){
	  if(parent!=w.parent){
	    try{
	      w.reparent(parent,
			 w.x-w.borderWidth-parent.x,
			 w.y-w.borderWidth-parent.y, c);
	    }
	    catch(Exception ee){
	      //System.out.println("saveset: reparent: "+ee);
	    }
	    if(!w.isRealized() && w.isMapped()){
              w.attr&=~(1<<19);
	    }
	    try{ w.mapWindow(c); }
	    catch(Exception ee){
	      //System.out.println("saveset: mapWindow "+ee);
	    }
	  }
	}
      }
      c.saveSet.delete();
      c.saveSet=null;
    }
  }
}
