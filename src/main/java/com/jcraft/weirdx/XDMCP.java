/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - XDMCP support
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
import java.util.*;

final class XDMCP extends Thread implements ClientListener{

  private final static int start=1;
  private final static int query=2;
  private final static int collect_query=3;
  private final static int broadcast=4;
  private final static int collect_broadcast_query=5;
  private final static int update_broadcast_willing=6;
  private final static int indirect=7;
  private final static int collect_indirect_query=8;
  private final static int update_indirect_willing=9;
  private final static int start_connection=10;
  private final static int await_request_response=11;
  private final static int manage=12;
  private final static int await_manage_response=13;
  private final static int stop_connection=14;
  private final static int run_session=15;
  private final static int keep_alive=16;
  private final static int await_alive=17;
  private final static int reset_display=18;

  final static int BroadcastQuery=1;
  final static int Query=2;
  final static int IndirectQuery=3;
  private final static int ForwardQuery=4;
  private final static int Willing=5;
  private final static int Unwilling=6;
  private final static int Request=7;
  private final static int Accept=8;
  private final static int Decline=9;
  private final static int Manage=10;
  private final static int Refuse=11;
  private final static int Failed=12;
  private final static int KeepAlive=13;
  private final static int Alive=14;

  private int state=0;

  private final static byte[] manu_display_id="JCraft-WeirdX-1".getBytes();
  private final static byte[] display_class="JCraft-WeirdX".getBytes();

  private final static byte[] authname="MIT-MAGIC-COOKIE-1".getBytes();
  static byte[] mitcookie=null;

  private final static int XDMCPcp_port=177;
  private static byte[] myaddress=null;

  static int timeout=5000;

  int displayNum=0;
  UDPIOByte io;
  byte[] buf=new byte[1024];
  int sessionID;
  String XDMCP;
  int fstclient=-1;

  String host;
  int mode=query;

  void close(){
    try{ if(io!=null) io.close(); } catch(Exception e){ }
  }

  void getLocalHost(String name){
    myaddress=new byte[4];
    try{
      InetAddress local;
      if(name!=null&&name.length()>0){ local=InetAddress.getByName(name); }
      else{ local=InetAddress.getLocalHost(); }
      byte[] b=local.getHostAddress().getBytes();
      int bi=0;
      for(int i=0; i<4; i++){
	myaddress[i]=0;
	while(bi<b.length && b[bi]!='.'){
	  myaddress[i]=(byte)(myaddress[i]*10+(b[bi]-'0'));
	  bi++;
	}
	if(b.length<=bi)break;
	bi++;
      }
    }
    catch(Exception e){ System.err.println(e); }
  }

  XDMCP(String host){
    this(Query, host, null, 0); 
  }

  XDMCP(String query, String localhost, int num){ 
    this(Query, query, localhost, num);
  }

  XDMCP(int op, String host, String localhost, int num){ 
    super();

    this.host=host;
    this.displayNum=num;

    if(myaddress==null){ getLocalHost(localhost); }

    if(myaddress[0]==127 &&
       myaddress[1]==0 &&
       myaddress[2]==0 &&
       myaddress[3]==1){
      System.err.println("XDMCP warning: InetAddress.getLocalHost() return loopback address");
    }

    if(op==Query) mode=query;
    else if(op==BroadcastQuery) mode=broadcast;
    else if(op==IndirectQuery) mode=indirect;

    if(op==Query){ 
      io=new UDPIOByte(host, XDMCPcp_port); 
      io.setTimeout(timeout);
    }
  }

  public void connected(int index){
    if(fstclient==-1){
      fstclient=index;
    }
  }

  public void disconnected(int index){
    if(fstclient==index){
      fstclient=-1;
      WeirdX.resetScreen(0);
      state=reset_display;
    }
  }

  public void run(){
    try{stateMachine();}
    catch(Exception e){System.out.println(e);}
  }

  void stateMachine() throws IOException{
    int foo, len, opcode, sessionId;

    state=start;

    while(true){
      switch(state){
      case start:
	state=mode;
	if(mode==broadcast){ 
          if(io!=null) io.close();
          io=new UDPIOByte(host, XDMCPcp_port); 
	  io.setTimeout(timeout);
        }
	else if(mode==indirect){ 
          if(io!=null) io.close();
          io=new UDPIOByte(host, XDMCPcp_port); 
	  io.setTimeout(timeout);
        }
	break;
      case query:
	io.putShort(1);
	io.putShort(Query); // Query
	io.putShort(1);
	io.putShort(0);
	io.flush();
	state=collect_query;
	break;

      case collect_query:
	try{
	  foo=io.getShort();
	  opcode=io.getShort();
	  len=io.getShort();
	  if(opcode==Willing){
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    state=start_connection;
	  }
	  else if(opcode==Unwilling){
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    state=stop_connection;
	  }
	  else{
	    System.out.println("unknow opcode("+opcode+") at "+state); 
	    return;
	  }
	}
	catch(InterruptedIOException ie){ // timeout
	  state=query;
	  return;
	}
	break;

      case broadcast:
	io.putShort(1);
	io.putShort(BroadcastQuery); // BroadcastQuery
	io.putShort(1);
	io.putShort(0);
	io.flush();
	state=collect_broadcast_query;
	break;

      case collect_broadcast_query:
	try{
	  foo=io.getShort();
	  opcode=foo=io.getShort();
	  len=io.getShort();
	  if(opcode==Willing){
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    String target=new String(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);

	    if(target.length()==0){
	      target=new String(buf, 0, foo);
	      target=target.substring(target.indexOf(' ')+1);
	    }

	    io.close();
	    io=new UDPIOByte(target, XDMCPcp_port);
	    io.setTimeout(timeout);

	    state=update_broadcast_willing;
	  }
	  else{
	    io.getByte(buf, 0, len);
	    state=start;
	  }
	  //state=start_connection;
	}
	catch(InterruptedIOException ie){ // timeout
	  state=start;
	}
	break;

      case update_broadcast_willing:
	state=start_connection;
	//state=collect_broadcast_query;
	break;

      case indirect:
	io.putShort(1);
	io.putShort(IndirectQuery); // IndirectQuery
	io.putShort(1);
	io.putShort(0);
	io.flush();
	state=collect_indirect_query;
	break;

      case collect_indirect_query:
	try{
	  foo=io.getShort();
	  opcode=foo=io.getShort();
	  len=foo=io.getShort();
	  if(opcode==Willing){
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    String wilinghost=new String(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);

	    if(io!=null) io.close();
	    io=new UDPIOByte(wilinghost, XDMCPcp_port); 
	    io.setTimeout(timeout);
	    Acl.parse("+"+wilinghost);

//	    state=update_indirect_willing;
	    state=start_connection;
	  }
	  else{
	    io.getByte(buf, 0, len);
	    state=indirect;
	  }
	  //state=start_connection;
	}
	catch(InterruptedIOException ie){ // timeout
	  state=indirect;
	}
	break;

      case update_indirect_willing:
	state=collect_indirect_query;
	break;

      case start_connection:
	io.putShort(1);
	io.putShort(Request);
	io.putShort(15+
		    myaddress.length+
		    manu_display_id.length+
                    2+authname.length
		    );
	io.putShort(displayNum);
	io.putByte((byte)1);                            
	io.putShort(0);                       // connection Type, inet
	io.putByte((byte)1);
	io.putShort(myaddress.length);
	io.putByte(myaddress, 0, myaddress.length);
	io.putShort(0);                       // len of authentification name
	io.putShort(0);                       // len of authentification data
	io.putByte((byte)1);                  // count of authorization names
	io.putShort(authname.length);         
	io.putByte(authname, 0, authname.length);
	io.putShort(manu_display_id.length);  // len of manifucture id
	io.putByte(manu_display_id, 0, manu_display_id.length);
	io.flush();
	state=await_request_response;
	break;

      case await_request_response:
	try{
	  foo=io.getShort();
	  opcode=io.getShort();
	  len=io.getShort();
	  if(opcode==Accept){
	    sessionID=foo=io.getInt();
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);

            String bar=null;
            if(foo>0) bar=new String(buf, 0, foo);

	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
            if(bar!=null && bar.equals("MIT-MAGIC-COOKIE-1") && 
               foo>0){
   	      mitcookie=new byte[foo];
              System.arraycopy(buf, 0, mitcookie, 0, foo);
	    }
            else{
  	      mitcookie=null;
	    }
	    state=manage;
	  }
	  else if(opcode==Decline){
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    state=stop_connection;
	  }
	  else{
	    System.out.println("unknow opcode("+opcode+") at "+state);
	    return;
	  }
	}
	catch(InterruptedIOException ie){ // timeout
	  state=start_connection;
	  return;
	}
	break;

      case manage:
	io.putShort(1);
	io.putShort(Manage);
	io.putShort(8+display_class.length);
	io.putInt(sessionID);
	io.putShort(displayNum);
	io.putShort(display_class.length);
	io.putByte(display_class, 0, display_class.length);
	io.flush();
	state=await_manage_response;
	break;

      case await_manage_response:
	try{
	  foo=io.getShort();
	  opcode=foo=io.getShort();
	  len=foo=io.getShort();
	  if(opcode==Refuse){
	    int id=io.getInt();
	    if(sessionID==id){
	      state=start_connection;
	    }
	    else{
	      return; 
	    }
	  }
	  else if(opcode==Failed){
	    int id=io.getInt();
	    foo=io.getShort();
	    io.getByte(buf, 0, foo);
	    if(sessionID==id){
	      state=stop_connection;
	    }
	    else{
	      System.out.println("await_manage_response: invalid failed "+id); 
	      return; 
	    }
	  }
	  else {
	    System.out.println("unknow opcode("+opcode+") at "+state);
	    return;
	  }
	}
	catch(InterruptedIOException ie){ // timeout
	  try{Thread.sleep(1000);}
	  catch(Exception e){}
	  if(fstclient==-1){
	    state=manage;
	  }
	  else{
	    state=run_session;
	  }
	}
	break;

      case stop_connection:
	state=start;
	break;

      case run_session:
	while(true){
	  try{Thread.sleep(10000);}
	  catch(Exception e){}
	  if(state==reset_display)break;
	}
	//state=keep_alive;
	state=reset_display;
	break;

      case keep_alive:
	io.putShort(1);
	io.putShort(KeepAlive);
	io.putShort(6);
	io.putShort(displayNum);
	io.putInt(sessionID);
	io.flush();
	state=await_alive;
	break;

      case await_alive:
	foo=io.getShort();
	opcode=io.getShort();
	len=io.getShort();
	int running=foo=io.getByte();
	int id=io.getInt();
	if(running!=0 && id==sessionID){ // running
	  state=run_session;
	}
	else{
	  state=reset_display;
	}
	break;

      case reset_display:
	state=start;
	break;

      default:
	System.out.println("??");
      }
    }
  }

  class UDPIOByte{
    InetAddress address;
    DatagramSocket socket = null;
    DatagramPacket sndpacket;
    DatagramPacket recpacket;
    byte[] buf = new byte[1024];
    String host;
    int port;
    byte[] inbuffer=new byte[1024];
    byte[] outbuffer=new byte[1024];
    int instart=0, inend=0, outindex=0;
    /*private*/ byte[] ba;
    /*private*/ byte[] sa;
    /*private*/ byte[] ia;

    UDPIOByte(String host, int port){ 
      this.host=host;
      this.port=port;
      try{
	address = InetAddress.getByName(host);
//	socket = new DatagramSocket();
	socket = new DatagramSocket(6001); // dummy...
      }
      catch(Exception e){System.err.println(e);}
      ba=new byte[1]; sa=new byte[2]; ia=new byte[8]; 
      recpacket = new DatagramPacket(buf, 1024);
      sndpacket=new DatagramPacket(outbuffer, 0, address, port);
    }

    void setTimeout(int i){
      try{socket.setSoTimeout(i);}
      catch(Exception e){System.out.println(e);}
    }

    int getByte() throws java.io.IOException{
      if((inend-instart)<1){ read(1); }
      return inbuffer[instart++]&0xff;
    }

    void getByte(byte[] array) throws java.io.IOException{
      getByte(array, 0, array.length);
    }

    void getByte(byte[] array, int begin, int length) throws java.io.IOException{
      int i=0;
      while(true){
	if((i=(inend-instart))<length){
	  if(i!=0){
	    System.arraycopy(inbuffer, instart, array, begin, i);
	    begin+=i;
	    length-=i;
	    instart+=i;
	  }
	  read(length);
	  continue;
	}
	System.arraycopy(inbuffer, instart, array, begin, length);
	instart+=length;
	break;
      }
    }

    int getShort() throws java.io.IOException{
      if((inend-instart)<2){ read(2); }
      int s=0;
      s=inbuffer[instart++]&0xff;
      s = ((s<<8)&0xffff) | (inbuffer[instart++]&0xff);
      return s;
    }

    int getInt() throws java.io.IOException{
      if((inend-instart)<4){ read(4); }
      int i=0;
      i=inbuffer[instart++]&0xff;
      i = ((i<<8)&0xffff) | (inbuffer[instart++]&0xff);
      i = ((i<<8)&0xffffff) | (inbuffer[instart++]&0xff);
      i = (i<<8) | (inbuffer[instart++]&0xff);
      return i;
    }

    void getPad(int n) throws java.io.IOException{
      int i;
      while (n > 0){
	if((i=inend-instart)<n){ 
	  n-=i;
	  instart+=i;
	  read(n); 
	  continue;
	}
	instart+=n;
	break;
      }
    }

    void read(int n) throws java.io.IOException{
      if (n>inbuffer.length){
	n=inbuffer.length;
      }
      instart=inend=0;
      int i;
      while(true){
	recpacket = new DatagramPacket(buf, 1024);
	socket.receive(recpacket);
	i=recpacket.getLength();
	System.arraycopy(recpacket.getData(), 0, inbuffer, inend, i);
	// i=in.read(inbuffer, inend, inbuffer.length-inend);
	if(i==-1){
	  throw new IOException();
	}
	inend+=i;
	if(n<=inend)break;
      }
    }

    void putByte(byte val) throws java.io.IOException{
      if((outbuffer.length-outindex)<1){ flush(); }
      outbuffer[outindex++]=val;
    }

    void putByte(int val) throws java.io.IOException{
      putByte((byte)val);
    }

    void putByte(byte[] array) throws java.io.IOException{
      putByte(array, 0, array.length);
    }

    void putByte(byte[] array, int begin, int length) throws java.io.IOException{
      if(length<=0) return;
      int i=0;
      while(true){
	if((i=(outbuffer.length-outindex))<length){
	  if(i!=0){
	    System.arraycopy(array, begin, outbuffer, outindex, i);
	    begin+=i;
	    length-=i;
	    outindex+=i;
	  }
	  flush();
	  continue;
	}
	System.arraycopy(array, begin, outbuffer, outindex, length);
	outindex+=length;
	break;
      }
    }

    void putShort(int val) throws java.io.IOException{
      if((outbuffer.length-outindex)<2){
	flush();
      }
      outbuffer[outindex++]=(byte)((val >> 8)&0xff);
      outbuffer[outindex++]=(byte)(val&0xff);
    }

    void putInt(int val) throws java.io.IOException{
      if((outbuffer.length-outindex)<4){
	flush();
      }
      outbuffer[outindex++]=(byte)((val >> 24) & 0xff);
      outbuffer[outindex++]=(byte)((val >> 16) & 0xff);
      outbuffer[outindex++]=(byte)((val >> 8) & 0xff);
      outbuffer[outindex++]=(byte)((val) & 0xff);
    }

    void putPad(int n) throws java.io.IOException{
      int i;
      while(true){
	if((i=(outbuffer.length-outindex))<n){
	  if(i!=0){
	    outindex+=i;
	    n-=i;
	  }
	  flush();
	  continue;
	}
	outindex+=n;
	break;
      }
    }

    synchronized void flush() throws java.io.IOException{
      if(outindex==0)return;
      sndpacket.setLength(outindex);
      socket.send(sndpacket);
      outindex=0;
    }
    void close() throws java.io.IOException{
      socket.close();
    }
  }

  public static void main(String[] args) {

    String usage="usage: [-query|-broadcast] address -display displayname";

    int op=-1;
    String address=null;
    String displayaddress=null;
    int displaynum=0;

    if(args.length==0){
      System.err.println(usage);
      System.exit(-1); 
    }      

    for(int i=0; i<args.length; i++){
      if(args[i].equals("-query")){
        op=Query; 
        i++;
        address=args[i];
        continue;
      }
      if(args[i].equals("-broadcast")){
        op=BroadcastQuery; 
        i++;
        address=args[i];
        continue;
      }
      if(args[i].equals("-display")){
        i++;
        displayaddress=args[i].substring(0, args[i].indexOf(":"));
        try{
          String foo=args[i].substring(args[i].indexOf(":")+1, args[i].length());
          foo=foo.substring(0, 
                            (foo.indexOf(".") == -1 ? 
                             foo.length() : 
                             foo.indexOf(".")));
          displaynum=Integer.parseInt(foo);
	}
        catch(Exception e){}
      }
    }

    if(op==-1 || address==null || displayaddress==null){
      System.err.println(usage);
      System.exit(-1); 
    } 

    XDMCP foo=new XDMCP(op, address, displayaddress, displaynum);
    foo.start();
  }
}
