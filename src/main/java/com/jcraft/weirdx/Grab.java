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

final class Grab extends Resource{
  static final int ownerEvents=(1<<0);
  static final int keyboardMode=(1<<1);
  static final int pointerMode=(1<<2);
  static final int coreGrab=(1<<3);
  static final int coreMods=(1<<4);
  private static final int NotifyGrab=1;

  Grab(int id){
    super(id, Resource.RT_PASSIVEGRAB);
    modifiersDetail=new Detail();
    detail=new Detail();
    enable();
  }

  private boolean enable=false;

  boolean isEnabled(){return enable;}
  void enable(){enable=true;}
  void disable(){
    enable=false;
    attr=0; eventMask=0;
  }

  boolean sameClient(Client c){
    return ((resource & CLIENTMASK)==c.clientAsMask);
  }
  void set(int resource, Window window,
		  int oevents, int emask, int kmode, int pmode,
		  Window confineto){
    this.resource=resource;
    this.window=window;
    this.attr=0;
    this.attr|=(oevents!=0?ownerEvents:0);
    this.eventMask=emask;
    this.attr|=(kmode!=0?keyboardMode:0);
    this.attr|=(pmode!=0?pointerMode:0);
    this.confineTo=confineto;
  }

  Grab	next;
  int resource;
  Window window;
  int attr;
  int type;
  Detail modifiersDetail;
  Detail detail;
  Window confineTo;
  int eventMask;

  Client getClient(){
    return Client.clients[((resource & Client.CLIENTMASK) >> Client.CLIENTOFFSET)];
  }

  static Grab createGrab(Client c, Window gw,
				int emask, int oe, int kmode, int pmode,
				int mod, int type, int button, 
				Window cto){
    Grab grab=new Grab(Resource.fakeClientId(c));
    grab.resource=grab.id;
    grab.window=gw;
    grab.eventMask=emask;
    grab.attr|=(oe!=0?ownerEvents:0);
    grab.attr|=(kmode!=0?keyboardMode:0);
    grab.attr|=(pmode!=0?pointerMode:0);
    grab.modifiersDetail.exact=mod;
    grab.modifiersDetail.pMask=null;
    grab.type=type;
    grab.detail.exact=button;
    grab.detail.pMask=null;
    grab.confineTo=cto;
    return grab;
  }

  void freeGrab(){
  }
  boolean grabSupersedesSecond(Grab pSecondGrab){
    if (!modifiersDetail.detailSupersedesSecond(pSecondGrab.modifiersDetail, 
						Window.AnyModifier))
      return false;

    if (detail.detailSupersedesSecond(pSecondGrab.detail, Window.AnyKey))
      return true;
    return false;
  }

  boolean grabMatchesSecond(Grab pSecondGrab){
    if (type !=pSecondGrab.type) return false;
    if (grabSupersedesSecond(pSecondGrab) ||
	pSecondGrab.grabSupersedesSecond(this))
      return true;
    if (pSecondGrab.detail.detailSupersedesSecond(detail, Window.AnyKey) && 
	modifiersDetail.detailSupersedesSecond(pSecondGrab.modifiersDetail,
					       Window.AnyModifier))
      return true;
    if (detail.detailSupersedesSecond(pSecondGrab.detail, Window.AnyKey) && 
	pSecondGrab.modifiersDetail.detailSupersedesSecond(modifiersDetail,
							   Window.AnyModifier))
      return true;
    return false;
  }

  int addPassiveGrabToList(){
    for (Grab grab=window.getPassiveGrabs(); grab!=null; grab=grab.next){
      if (grabMatchesSecond(grab)) {
	if ((resource & CLIENTMASK) !=
            (grab.resource & CLIENTMASK)){
	  freeGrab();
	  return 0;
	}
      }
    }

    window.makeOptional();
    next=window.optional.passiveGrabs;
    window.optional.passiveGrabs=this;
    Resource.add(this);
    return 1;
  }

  static void deactivatePointerGrab() throws IOException {
    Window.enter_leaveEvent(Window.grab.window, Window.sprite.win, NotifyGrab);
    Window.grab=null;
  }

  void activatePointerGrab(int time, boolean autoGrab)
    throws IOException {
    Window oldWin=(Window.grab!=null) ? Window.grab.window : Window.sprite.win;
    Window.enter_leaveEvent(oldWin, window, NotifyGrab);
    Window.grab=this;
  }

  boolean deletePassiveGrabFromList(){
    Grab grab;
    int i=0;
    for (grab=window.getPassiveGrabs(); grab!=null; grab=grab.next){
      i++;
    }
    if (i==0){
      return true;
    }
                                                                  
    Grab[] deletes=new Grab[i];
    int ndels=0;
    int nadds=0;
    int nups=0;
    boolean ok=true;

    for (grab=window.getPassiveGrabs(); grab!=null && ok; grab=grab.next){
      if (((grab.resource & CLIENTMASK)!=(resource & CLIENTMASK)) ||
	  !grab.grabMatchesSecond(this)){
	continue;
      }
      if (grabSupersedesSecond(grab)){
	deletes[ndels++]=grab;
      }
    }

    if (!ok){
    }
    else  {
      for (i=0; i < ndels; i++){
	Resource.freeResource(deletes[i].id, RT_NONE);
      }
    }
    return ok;
  }
  
  void delete() throws IOException{
    Grab prev=null;
    for (Grab g=window.getPassiveGrabs(); g!=null; g=g.next){
      if (this==g) {
	if (prev!=null){
	  prev.next=g.next;
	}
	else{
	  window.optional.passiveGrabs=g.next;
	  break;
	}
	prev=g;
      }
    }
  }
}

class Detail {
  int exact;
  byte[] pMask=null;

  boolean isInGrabMask(Detail secondDetail, int exception){
    if (exact==exception){
      if (pMask==null) return true;
      if (secondDetail.exact==exception) return false;
      if ((pMask[secondDetail.exact>>5] & (secondDetail.exact<<31))!=0)
	return true;
    }
    return false;
  }

  static boolean identicalExactDetails(int firstExact, 
				       int secondExact,
				       int exception){
    if ((firstExact==exception) || (secondExact==exception)) return false;
    if (firstExact==secondExact) return true;
    return false;
  }
  boolean detailSupersedesSecond(Detail secondDetail,int exception){
    if (isInGrabMask(secondDetail, exception)) return true;
    if (identicalExactDetails(exact, secondDetail.exact, exception))
      return true;
    return false;
  }
}
