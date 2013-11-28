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

package com.jcraft.weirdx.res;
import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.weirdx.Client;

public class XResource{
	private static Log LOG = LogFactory.getLog(XResource.class);
	
  protected static Object LOCK=Client.class;
  static final int RC_VANILLA=0;
  static final int RC_CACHED=1<<31;
  public static final int RC_DRAWABLE=1<<30;

  static final int SERVER_BIT=0x20000000;
  static final int SERVER_MINID=32;
  static final int RESOURCE_ID_MASK=Client.IDMASK;

  static final int CLIENTOFFSET=Client.CLIENTOFFSET;
  protected static final int CLIENTMASK=(((1<<Client.BITSFORCLIENTS)-1) 
                               <<Client.BITSFORRESOURCES);  // 0x1fc00000

  static final int  RC_NEVERRETAIN=1<<29;
  static final int  RC_LASTPREDEF=RC_NEVERRETAIN;
  static final int  RC_ANY=~0;

  protected static final int  RT_WINDOW=1|RC_CACHED|RC_DRAWABLE;
  protected static final int  RT_PIXMAP=2|RC_CACHED|RC_DRAWABLE;
  public static final int  RT_GC=3|RC_CACHED;
  public static final int  RT_FONT=4;
  protected static final int  RT_CURSOR=5;
  protected static final int  RT_COLORMAP=6;
  protected static final int  RT_CMAPENTRY=7;
  protected static final int  RT_OTHERCLIENT=8|RC_NEVERRETAIN;
  protected static final int  RT_PASSIVEGRAB=9|RC_NEVERRETAIN;
  static final int  RT_LASTPREDEF=9;
  protected static final int  RT_NONE=0;

  static ClientResource[] clients=new ClientResource[Client.MAXCLIENTS];

  static int lastResourceType=RT_LASTPREDEF;
  static int lastResourceClass=RC_LASTPREDEF;

  public static synchronized int newType(){
    int next=lastResourceType+1;
    if ((next & lastResourceClass)!=0) return 0;
    lastResourceType=next;
    return next;
  }
  static synchronized int newClss(){
    int next=lastResourceClass >> 1;
    if ((next & lastResourceType)!=0) return 0;
    lastResourceClass=next;
    return next;
  }

  public int id;
  int rtype;

  XResource(){}
  protected XResource(int id, int rtype){
    this.id=id; this.rtype=rtype;
  }

  public int hashCode(){
    return id;
  }

  public boolean equals(Object o){
    if(o==null || id !=o.hashCode()) return false;
    if(!(o instanceof XResource)) return true;
    if(!(o instanceof Key)){
      return rtype==((XResource)o).rtype;
    }
    Key key=(Key)o;
    if(key.clss==0){return rtype==key.rtype; }
    else if(key.clss==RC_ANY){ return true; }
    else{ return (rtype&key.clss)!=0; }
  }

  public static void add(XResource r){
    int client=((r.id & CLIENTMASK) >> CLIENTOFFSET);
    ClientResource cr=clients[client];
    cr.put(r, r);
  }

  static void RemoveResource(XResource r){
    int client=((r.id & CLIENTMASK) >> CLIENTOFFSET);
    ClientResource cr=clients[client];
    RemoveResource(cr, r);
  }

  static void RemoveResource(ClientResource cr, XResource r){
    synchronized(LOCK){
      if ((r.rtype & RC_CACHED)!=0){ Client.flushCache(r.id); }
    }
    cr.remove(r);
  }
 
  public static void freeResource(int id, int skip) {
    while(true){
      XResource r=lookupIDByClass(id, RC_ANY);
      if(r==null) break;
      RemoveResource(r);
      try{
	if (r.rtype !=skip){
          deleteit(r); 
	}
      }catch(Exception e){}
    }
  }

  static void freeResourceByType(int id, int typ, int skip) {
    XResource r=lookupIDByType(id, typ);
    if(r!=null){
      RemoveResource(r);  // should check type
      try{
	if (r.rtype !=skip){
          deleteit(r); 
	}
      }
      catch(Exception e){}
    }
  }

  public static XResource lookupIDByClass(int id, int clss){
    int client=((id & CLIENTMASK) >> CLIENTOFFSET);
    if(clients.length<=client)return null;
    ClientResource cr=clients[client];
    if(cr==null)return null;

    synchronized(cr){
      try{
	cr.key.id=id;
	cr.key.clss=clss;
	XResource r=(XResource)cr.get(cr.key);
	return r;
      }
      catch(Exception e){}
      return null;
    }
  }

  public static XResource lookupIDByType(int id, int rtype){
    int client=((id & CLIENTMASK) >> CLIENTOFFSET);
    if(clients.length<=client)return null;
    ClientResource cr=clients[client];
    if(cr==null)return null;

    synchronized(cr){
      cr.key.id=id;
      cr.key.clss=0;
      cr.key.rtype=rtype;
      XResource r=(XResource)cr.get(cr.key);
      return r;
    }
  }

  public static void freeClientNeverResources(Client c){
    if(c==null) return;
    ClientResource cr=clients[c.index];
    synchronized(LOCK){
      try{
      for (Enumeration<? extends XResource> e=cr.elements() ; e.hasMoreElements();){
	XResource r = e.nextElement();
	if ((r.rtype & RC_NEVERRETAIN)!=0){
          deleteit(r); 
	  //System.out.println("freeclientResource: "+r);
	  RemoveResource(cr, r);
	}
      }
      }catch(Exception e){}
    }
  }

  public static void freeClientResources(Client c){
    if(c==null) return;
    ClientResource cr=clients[c.index];
    synchronized(LOCK){
      try{
      for (Enumeration e=cr.elements() ; e.hasMoreElements();){
	XResource r=(XResource)e.nextElement();
        deleteit(r);
        RemoveResource(cr, r);
      }
      }catch(Exception e){}
    }
  }

  static void deleteit(XResource r){
    try{ r.delete(); }
    catch(Exception ee){ 
      //System.out.println("r.delete: exception ->"+ee);
      //System.out.println("r="+r);
    }
  }

  void delete() throws IOException{ }

  public static Client lookupClient(int rid){
    XResource res=lookupIDByClass(rid, RC_ANY);
    int clientIndex=((rid & CLIENTMASK) >> CLIENTOFFSET);
    if (clientIndex!=0 && 
	res!=null && 
	clients[clientIndex]!=null && 
	(rid & SERVER_BIT)==0){
      return Client.clients[clientIndex];
    }
    return null;
  }

  public static void initClientResource(Client c){
    ClientResource cr=clients[c.index]=new ClientResource();
    cr.fakeID=c.clientAsMask | (c.index!=0 ? SERVER_BIT : SERVER_MINID);
    cr.endFakeID=(cr.fakeID | RESOURCE_ID_MASK)+1;
    cr.expectID=c.clientAsMask;
  }

  public static int fakeClientId(Client c){
    int id, maxid;
    id=clients[c.index].fakeID++;
    if (id !=clients[c.index].endFakeID){
      return id;
    }
    //System.out.println("error: fakeClient "+id);
    return id;
  }

/*
  static void dump(){
    System.out.println("Resource.dump:");
    for(int i=0; i<clients.length; i++){
      if(clients[i]==null) continue;
      ClientResource cr=clients[i];
      int j=0;
      for (Enumeration e=cr.elements() ; e.hasMoreElements();){
      Resource r=(Resource)e.nextElement();
        j++;
      }
      System.out.print("  client="+i+", gone="+Client.clients[i]+
			 ", resource="+j);
      if(Client.clients[i]==null)
	System.out.println("");
      else
	System.out.println(", closeDownMode="+
			   Client.clients[i].closeDownMode);
    }
  }
  static void dump(){
    System.out.println("Resource.dump:");
    for(int i=0; i<clients.length; i++){
      if(clients[i]==null) continue;
      System.out.println("  client="+i);
      ClientResource cr=clients[i];
      for (Enumeration e=cr.elements() ; e.hasMoreElements();){
        Resource r=(Resource)e.nextElement();
        System.out.println("    resource="+r);
      }
    }
  }
*/
}

class ClientResource extends Hashtable{
  int fakeID;
  int endFakeID;
  int expectID;
  Key key=new Key();
}

class Key extends XResource{
  int clss=0;
  public int hashCode(){
    return id;
  }
  public boolean equals(Object o){
    //System.out.println("Key: equals "+o);
    if(o==null)return false;
    return id==o.hashCode();
  }
}
