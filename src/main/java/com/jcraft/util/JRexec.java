package com.jcraft.util;

import java.io.*;
import java.net.*;
import java.lang.*;

public class JRexec implements Runnable {
  private Thread thread=null;

  private InputStream data=null;
  private InputStream in=null;
  private OutputStream out=null;
  private boolean devnull=true;
  private Socket socket=null;

  String user=null;
  String host=null;
  String passwd=null;
  String command=null;

  public JRexec(String user, String host, String passwd, String command)
    throws JRexecException {
    this.user=user; 
    this.host=host;
    this.passwd=passwd; 
    this.command=command;

    try{
      socket = new Socket(host, 512);
      in = socket.getInputStream();
      out = socket.getOutputStream();
    }
    catch(java.net.UnknownHostException e){
      throw new JRexecException(e.toString());
    }
    catch(java.io.IOException e) {
      throw new JRexecException(e.toString());
    }
  }
  public JRexec(String user, String host, String passwd, String command,
	 InputStream data)
    throws JRexecException {
    this(user, host, passwd, command);
    this.data=data;
  }
  public JRexec(String user, String host, String passwd, String command,
	 String data)
    throws JRexecException {
    this(user, host, passwd, command);
    this.data=new ByteArrayInputStream(data.getBytes());
  }

  public synchronized InputStream getResult(){
    if(thread!=null || !devnull) return null;
    devnull=false;
    return in;
  }

  public synchronized void doit(){
    if(thread==null){
      thread=new Thread(this);
      thread.start();
    }
  }

  public void run() {
    try	{

      // port
      out.write(0);

      int chop=8;
      if(user.length()<chop) chop=user.length();
      out.write(user.getBytes(), 0, chop);
      out.write(0);

      chop=8;
      if(passwd.length()<chop) chop=passwd.length();
      out.write(passwd.getBytes(), 0, chop);
      out.write(0);

      chop=4096;
      if(command.length()<chop) chop=command.length();
      out.write(command.getBytes(), 0, chop);
      out.write(0);

      byte[] buf=new byte[1024];

      if(data!=null){
	while(true){
	  int len=data.read(buf, 0, buf.length);
	  if(len==-1) break;
	  out.write(buf, 0, len);
	}
      }

      if(devnull){
	byte[] b=new byte[1];
	in.read(b, 0, 1);  

	if(b[0]==0){   // success
	  while(true){
	    int len=in.read(buf, 0, buf.length);
	    if(len==-1) break;
	    System.out.print(new String(buf, 0, len));
	  }
	}
	else{          // error message
	  while(true){
	    int len=in.read(buf, 0, buf.length);
	    if(len==-1) break;
	    System.out.print(new String(buf, 0, len));
	  }
	}
      }
//      stop();
    }
    catch(IOException e) {
      close();
      System.out.println("IO Error : " +  e );
    }
    return;
  }

  private synchronized void stop(){
    if(thread!=null){
      thread=null;
    }
  }

  public void close(){
    try	{
      in.close();
      out.close();
      socket.close();
    }
    catch(Exception e) {
      System.out.println(e);
    }
  }

  public static void main(String arg[]){
    try	{    
      JRexec jrexec=new JRexec("yamanaka", "localhost", "foo", "du /");
      InputStream in=jrexec.getResult();

      jrexec.doit();

      byte[] b=new byte[1];
      in.read(b, 0, 1);  // 0
      System.out.println("return: "+b[0]);

      byte[] buf=new byte[1024];
      while(true){
	int len=in.read(buf, 0, buf.length);
	if(len==-1) break;
	System.out.print(new String(buf, 0, len));
      }
    }
    catch(Exception e) {
      System.out.println("IO Error : " +  e );
    }
  }
}
