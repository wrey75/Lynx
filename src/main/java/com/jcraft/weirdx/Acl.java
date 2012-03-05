/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - Access Control based on hostnames/ip-addresses.
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
import java.net.*;

final class Acl {
  private static int mode=0;
  private static int count=0;
  private static int[] hosts;
  static{
    hosts=new int[3];
    for(int i=0; i<hosts.length;i++) hosts[i]=0;
  }
  static void parse(String str){
    str=","+str;
    char[] b=str.toCharArray();
    int i=0, s=0, e=0;
    while(i<b.length){
      while(i<b.length && b[i]==' ') i++;  if(b.length<=i) break;
      if(b[i]!=',')break;
      i++; if(b.length<=i) break;
      while(i<b.length && b[i]==' ') i++;  if(b.length<=i) break;

      if(b[i]=='-'){
	i++; s=i; 
	while(i<b.length && b[i]!=' ' && b[i]!=',')i++;
	e=i;
	if((e-s)==0){
//	  System.out.println("xhost: 0");
	  Acl.mode=1;
	}
	else{
//	  System.out.println("del: "+new String(b, s, e-s));
	  try{
	    InetAddress[] addr=InetAddress.getAllByName(new String(b, s, e-s));
	    byte[] bb;
	    for(int j=0; j<addr.length; j++){
	      bb=addr[j].getAddress();
	      delete(((bb[0]&0xff)<<24) |
		      ((bb[1]&0xff)<<16) |
		      ((bb[2]&0xff)<<8) |
		      ((bb[3]&0xff)));
	    }
	  }
	  catch(Exception ee){}
	}
	if(b.length<=i) break;
	continue;
      }

      if(b[i]=='+'){
	i++; 
      }
      s=i; 
      while(i<b.length && b[i]!=' ' && b[i]!=',')i++;
      e=i;
      if((e-s)==0){
//	System.out.println("xhost: +");
	Acl.mode=0;
      }
      else{
//	System.out.println("add: "+new String(b, s, e-s));
	try{
	  InetAddress[] addr=InetAddress.getAllByName(new String(b, s, e-s));
	  byte[] bb;
	  for(int j=0; j<addr.length; j++){
	      bb=addr[j].getAddress();
	      add(((bb[0]&0xff)<<24) |
		  ((bb[1]&0xff)<<16) |
		  ((bb[2]&0xff)<<8) |
		  ((bb[3]&0xff)));
	    }
	  }
	  catch(Exception ee){}
	}
	if(b.length<=i) break;
	continue;
    }
  }
  static synchronized void add(int address){
    int i=0;
    for(;i<hosts.length;i++){
      if(hosts[i]==address)return;
      if(hosts[i]==0)break;
    }
    if(i==hosts.length){
      int[] foo=new int[hosts.length*2];
      System.arraycopy(hosts, 0, foo, 0, hosts.length);
      hosts=foo;
    }
    hosts[i]=address;
    count++;
//    mode=1;
  }

  static synchronized void delete(int address){
    int i=0;
    for(;i<hosts.length;i++) if(hosts[i]==address)break;
    if(i==hosts.length) return;
    hosts[i]=0;
    count--;
    if(i==hosts.length-1) return;
    // shift
    System.arraycopy(hosts, i+1, hosts, i, hosts.length-i-1);
    hosts[hosts.length-1]=0;
  }

  static boolean check(InetAddress inet){
    if(mode==0) return true;
    byte[] bb=inet.getAddress();
    int address=(((bb[0]&0xff)<<24) |
                 ((bb[1]&0xff)<<16) |
		 ((bb[2]&0xff)<<8) |
		 (bb[3]&0xff));
    int i=0;
    while(true){
      if(hosts.length<=i || hosts[i]==0) break;
      //System.out.println("hosts["+i+"]: "+hosts[i]+", address: "+address);
      if(hosts[i]==address)return true;
      i++;
    }
    return false;
  }

  static void reqChangeHosts(Client c) throws IOException{
    int foo, n;
    boolean mode;
    foo=c.data;
    if(foo!=0)mode=false;    // delete
    else mode=true;          // insert
    n=c.length;
    foo=c.client.readByte();
    if(foo!=0){
      //System.out.println("ChangeHosts: invalid family: "+foo); 
    }
    c.client.readPad(1);
    foo=c.client.readShort();                    
    n=n-2;
    while(n!=0){
      foo=0;
      foo|=(c.client.readByte()<<24);
      foo|=(c.client.readByte()<<16);
      foo|=(c.client.readByte()<<8);
      foo|=c.client.readByte();
//      foo=c.client.readInt();
      n--;
      if(mode)add(foo);
      else delete(foo);
    }
  }

  static void reqSetAccessControl(Client c) throws IOException{
    int mode=c.data;
    if(mode!=0)Acl.mode=1;
    else Acl.mode=0;
  }
  static void reqListHosts(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    synchronized (Acl.class){
      synchronized(io){
        io.writeByte(1);
        io.writeByte((byte)mode);
        io.writeShort(c.seq);
        io.writeInt(count*2);
        io.writeShort(count);
        io.writePad(22);
        for(int i=0; i<count; i++){
	  io.writeByte((byte)0);
	  io.writePad(1);
	  io.writeShort(4);
  	  foo=hosts[i];
	  io.writeByte((byte)((foo&0xff000000)>>24)&0xff);
	  io.writeByte((byte)((foo&0xff0000)>>16)&0xff);
	  io.writeByte((byte)((foo&0xff00)>>8)&0xff);
	  io.writeByte((byte)(foo&0xff));
	}
	io.flush();
      }
    }
  }
}
