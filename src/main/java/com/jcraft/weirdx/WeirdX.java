/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - start point.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
                                 

@SuppressWarnings("serial")
public final class WeirdX extends Applet {

	private static Log LOG = LogFactory.getLog(WeirdX.class);

	static final Object LOCK = WeirdX.class;

	static final int major = 11;
	static final int minor = 0;
	static final int releaseNumber = 1032; // 1.0.32
	static final int motionBufferSize = 0;
	static final int maxRequestLength = 65535;
	static final byte[] vendor = "JCraft,Inc.".getBytes();

	static Screen[] screen = null;
	static Format[] format = null;

  static int imageByteOrder=1;        // 0=LSB, 1=MSB
  static int bitmapBitOrder=1;        // 0=LSB, 1=MSB
  static final int bitmapScanUnit=32; //32
  static final int bitmapScanPad=32;  //32

  private static short width = 768;
  private static short height = 576;

  private static String visuals = "PseudoColor8";
  private static String keymap = "101";
  private int displayNumber = 2;
  static String myAddress=null;

  static boolean threeButton=false;
  static boolean copypaste=false;

  static Client serverClient=null;
  String mode = "InBrowser";
  String logo = null;

  //static String extension=null;
  static String extension="DummySHAPE";

  static String charset=null;


  static XRexec xrexec=null;                              
  static String jesd=null;
  static String sxrexec=null;
  static String ssshrexec=null;

  static String jdxpc=null;
  static int jdxpcport=4000;
  static String jdxpcsocket=null;
  static String jdxpcserverproxy="com.jcraft.jdxpc.ServerProxy";

  static String alphaBackground=null;                             

  static AppletContext acontext=null;

  //static ServerSocket displaysocket=null;
  static DisplaySocket displaysocket=null;
  static Class<?> displaySocketClass=com.jcraft.weirdx.DisplaySocket6k.class;

  static XDMCP xdmcp=null;
  static String xdmcpmode=null;                           
  static String xdmcpaddr=null;                           

  static WeirdX weirdx=null;

  static final int InBrowser=0;
  static final int Rootless=1;
  static final int RootlessWM=2;

  void weirdx_init( Container container ){
//    try{ displaysocket=new ServerSocket(6000+weirdx.displaynum); }
//    catch(Exception ee){
//      System.out.println(ee+" bye");
//      return;
//    }

    try{
      displaysocket=(DisplaySocket)displaySocketClass.newInstance();
      displaysocket.init(weirdx.displayNumber);
    }
    catch(Exception ee){
      System.out.println(ee+" bye");
      displaysocket=null;
      return;
    }

    Class<?> c=null;
    try{ c=Class.forName("com.jcraft.weirdx.Keymap_"+keymap);}
    catch(Exception e){
      try{ c=Class.forName("com.jcraft.weirdx.Keymap_101"); }
      catch(Exception ee){ }
    }
    try{ if(c!=null) Keymap.km=(Keymap)c.newInstance(); }
    catch(Exception e){ }
    c=null;
    if(!keymap.equals("101")){
      try{ c=Class.forName("com.jcraft.weirdx.Keymodifier_"+keymap); }
      catch(Exception e){ 
      }
    }
    if(c==null){
      try{ c=Class.forName("com.jcraft.weirdx.Keymodifier_gen"); }
      catch(Exception e){ }
    }

    try{ if(c!=null) Keymodifier.kmod=(Keymodifier)c.newInstance(); }
    catch(Exception e){ }

    if(Keymap.km!=null){
      Keyboard.keyboard=new Keyboard(Keymap.km.start, Keymap.km.start+Keymap.km.count-1);
    }

    Client.releaseNumber=releaseNumber;
    Client.vendor=vendor;

    serverClient=new Client();
    serverClient.index=0;
    Client.clients[0]=serverClient;
    Resource.initClientResource(serverClient);

    screen=Screen.init();

    Colormap.init();
    Extension.init(extension);

    Depth[] depth=null;

    Visual[] visual=null;
    Visual defaultv=null;

    {
      List<Depth> depthv=new ArrayList<Depth>();

      if(visuals.indexOf("TrueColor16")!=-1){
        visual=Visual.getTrueColor16(serverClient);
        defaultv=visual[0];
        depthv.add(new Depth(16, visual));

        imageByteOrder=0;
        bitmapBitOrder=0;
      }

      if(defaultv==null && visuals.indexOf("PseudoColor8")!=-1){               
	visual=Visual.getPseudoColor8(serverClient);         
	defaultv=visual[0];                                 
	depthv.add(new Depth(8, visual));            
      }                                                     
      
      if(defaultv==null && visuals.indexOf("StaticGray8")!=-1){                
	visual=Visual.getStaticGray8(serverClient);          
	defaultv=visual[0];                                  
	depthv.add(new Depth(8, visual));             
      }                                                      

      if(defaultv==null){
        visual=Visual.getStaticGray1(serverClient);
        defaultv=visual[0];
        depthv.add(new Depth(1, visual));
      }

      depth=new Depth[depthv.size()];
      for(int i=0; i<depthv.size(); i++){
	depth[i]=(Depth)depthv.get(i);
      }
      depthv.clear();
    }

    int rootid=Resource.fakeClientId(serverClient);
    int colormapid=Resource.fakeClientId(serverClient);

    Font.init(Resource.fakeClientId(serverClient), charset); 

    Cursor.rootCursor=new Cursor(Resource.fakeClientId(serverClient));
    Resource.add(Cursor.rootCursor);

    int fgPixel=1, bgPixel=0;
    if(visuals.indexOf("TrueColor16")!=-1){
	fgPixel=0xffff; bgPixel=0x000000;
    }

    screen[0]=new Screen(rootid,
			 colormapid,
			 fgPixel,
			 bgPixel,
			 0,
			 width,
			 height,
			 width/3,
			 height/3,
			 1,
			 1,
			 defaultv.id,
			 0,
			 0,
                         defaultv.depth.depth,
			 depth);

    if(weirdx.mode.equals("MultiWindow") ||
       weirdx.mode.equals("Rootless")){
      screen[0].windowmode=Rootless; 
    }   
    else if(weirdx.mode.equals("MultiWindowWM") ||
	    weirdx.mode.equals("RootlessWM")){
	screen[0].windowmode=RootlessWM; 
    }   
    else {
      screen[0].windowmode=InBrowser;
    }

    {
      int vcount=0;
      for(int i=0; i<depth.length; i++){
	vcount+=depth[i].getVisual().length;
      }
      visual=new Visual[vcount];
      vcount=0;
      for(int i=0; i<depth.length; i++){
	Visual[] tmp=depth[i].getVisual();
	for(int j=0; j<tmp.length; j++){
	  visual[vcount]=tmp[j];
	  vcount++;
	}
      }
    }

    screen[0].visual=visual;

    if(defaultv.depth.depth==8){
      format=new Format[2];
      format[0]=new Format((byte)1, (byte)1, (byte)32);
      format[1]=new Format((byte)8, (byte)8, (byte)32);
    }
    else if(defaultv.depth.depth==16){
      format=new Format[2];
      format[0]=new Format((byte)1, (byte)1, (byte)32);
      format[1]=new Format((byte)16, (byte)16, (byte)32);
    }
    else{
      format=new Format[1];
      format[0]=new Format((byte)1, (byte)1, (byte)32);
    }

    Format.format=format;
    {
      int len=0;
      for(int i=0; i<screen.length; i++){ len+=screen[i].getLength(); }
      if(format!=null) len+=2*format.length;
      len+=((vendor.length+3)/4);
      len+=8;
      Client.initialLength=len;
    }
    screen[0].defaultColormap=
      Colormap.getColormap(colormapid, screen[0], 
			   defaultv, 0, serverClient);
    Colormap cmap=screen[0].defaultColormap;
    Colormap.installed[0]=cmap;

    if(defaultv.depth.depth!=16){                  
      cmap.flags|=4/*Colormap.BeingCreated*/;
      try{
        cmap.allocColor(serverClient, 0, 0, 0);
        cmap.allocColor(serverClient, 255, 255, 255);
        if(defaultv.clss==0 &&      // StaticGray
  	   defaultv.depth.depth==8){
	  for(int i=1; i<255; i++){                      
	    cmap.allocColor(serverClient, i, i, i);      
	  }                                              
        }
      }
      catch(Exception e){}
      cmap.mkIcm();
      cmap.flags&=~4/*Colormap.BeingCreated*/;
    }

    RootWindow w=null;
    try{ 
      w=new RootWindow(container, screen[0], format, serverClient); 
    }
    catch(Exception e){}

    try{ w.mapWindow(serverClient); }
    catch(Exception e){}

    Pixmap.init(screen);

    if(copypaste)
      CopyPaste.init();

    LogoImage.init(screen);
    LogoImage.loadLogo(logo);

    if(jesd!=null){
      try{
        c=Class.forName("com.jcraft.jesd.Daemon");
        Thread foo=(Thread)c.newInstance();
        foo.start();
      }
      catch(Exception e){ System.out.println("WeirdX: "+e); }
    }
  }

  	void weirdx_start(Container container) throws ConnectException {
		if( xdmcpmode != null ){
			LOG.debug( "XDMC Mode = " + xdmcpmode );
			if (xdmcpmode.equals("query")) {
				xdmcp = new XDMCP(xdmcpaddr, myAddress, displayNumber);
			} 
			else if (xdmcpmode.equals("broadcast")) {
				xdmcp = new XDMCP(XDMCP.BroadcastQuery, xdmcpaddr, myAddress,
						displayNumber);
			} 
			else if (xdmcpmode.equals("indirect")) {
				xdmcp = new XDMCP(XDMCP.IndirectQuery, xdmcpaddr, myAddress,
						displayNumber);
			}
		}

		if (sxrexec != null && sxrexec.equals("yes")) {
			xrexec = new XRexec(myAddress, displayNumber);
		}                                                            

    weirdx_init(container);

    InputStream in;
    OutputStream out;

    IO client=null;

    if(xdmcp!=null){
      Client.addListener((ClientListener)xdmcp);
      xdmcp.start();
    }

    if(jdxpc!=null){
      (new SpawnJDxpc(this)).start();
    }
    if(ssshrexec!=null){
      if(ssshrexec.equals("yes")){
        (new SpawnSSHRexec(this)).start();
      }                                                          
    } 

    byte[] byte_order=new byte[1];
    try {
      Socket socket=null;
      while(true && weirdx!=null){
	try{ socket=displaysocket.accept(); }
	catch(Exception e){
	  System.out.println(e);
          if(e instanceof NullPointerException){
            weirdx=null;
            break;
          }
	  continue;
	}

	if(!Acl.check(socket.getInetAddress())){
	  System.err.println("ACL warning: unauthorized access from "+
			     socket.getInetAddress());
	  try{socket.close();}catch(Exception e){};
	  continue;
	}

	try{ socket.setTcpNoDelay(true); }
	catch(Exception eeee){
	  //System.out.println(eeee+" tcpnodelay");
	}

	in=socket.getInputStream();
	out=socket.getOutputStream();

	try{in.read(byte_order, 0, 1);}
	catch(Exception e){continue;}

        // 0x6c LSB
        // 0x42 MSB
	if(byte_order[0]==0x6c){ client=new IOLSB(); }
	else if(byte_order[0]==0x42){ client=new IOMSB(); }
	else {
	  System.out.println("protocol: error "+
			     Integer.toHexString(byte_order[0]));
          continue;
	}

        client.setInputStream(in);
        client.setOutputStream(out);

				Client foo = new Client(client);
				if (foo.index != -1) {
					foo.start();
				} else {
					// System.err.println("running over clients table");
				}
			}
		} catch (IOException e) {
			LOG.error( "IO Error: " + e.getLocalizedMessage() );
		}
		// stop(); // ??
	}

  public void destroy() {
    try{
      try{ Client.closeDownAll();}
      catch(Exception ee){ 
      }
      try{ 
	if(displaysocket!=null){ displaysocket.close();	}
      }
      catch(Exception ee){ 
      }
      displaysocket=null;
      if(xdmcp!=null){
        try{ xdmcp.close(); }
        catch(Exception ee){
        }
//        try{ xdmcp.stop(); }
//        catch(Exception ee){
//        }
        xdmcp=null;
      }
    }
    catch(Exception e){
    }
  }

  public void stop() {
    try{
      try{ Client.closeDownAll(); }
      catch(Exception ee){ 
      }
      if(xdmcp!=null){
        try{ xdmcp.close(); }
        catch(Exception ee){
        }
//        try{ xdmcp.stop(); }
//        catch(Exception ee){
//        }
      }
      try{
	if(displaysocket!=null){ displaysocket.close();	}
      }
      catch(Exception ee){ 
      }
      displaysocket=null;
    }
    catch(Exception e){
    }
  }

  static void close() {
    try{displaysocket.close();}catch(Exception e){}
  }

  public void init(){
    acontext=getAppletContext();

    String s;

    s=getParameter("weirdx.ddxwindow");
    if(s!=null){
      Window.installDDXWindow(s);
    }
    
    s=getParameter("weirdx.windowmode");
    if(s!=null){
      mode=s; 
    }

    s=getParameter("weirdx.displaynum");
    if(s!=null){
      try{ displayNumber=Integer.parseInt(s); }
      catch(Exception ee){System.err.println(ee);}
    }

    weirdx=this;

    s=getParameter("weirdx.display.width");
    if(s==null){ s=getParameter("WIDTH"); }
    if(s!=null){
      try{ width=Short.parseShort(s); }
      catch(Exception e){ }
    }

    s=getParameter("weirdx.display.height");
    if(s==null){ s=getParameter("HEIGHT"); }
    if(s!=null){
      try{ height=Short.parseShort(s); }
      catch(Exception e){ }
    }

    s=getParameter("weirdx.display.visual");
    if(s!=null){ visuals=s; }

//    s=getParameter("weirdx.myaddress");
//    if(s!=null){ myAddress=s; }

    s=getParameter("weirdx.display.keymap");                  
    if(s!=null&&s.length()>0){ keymap=s; }                                 

    s=getParameter("weirdx.display.charset");
    if(s!=null){ charset=s; }

    s=getParameter("weirdx.display.acl");
    if(s!=null){ Acl.parse(s); }

    s=getParameter("weirdx.display.threebutton");
    if(s!=null){
      if(s.equals("yes")){ threeButton=true; }
    }

    s=getParameter("weirdx.display.copypaste");
    if(s!=null){
      if(s.equals("yes")){ copypaste=true; }
    }

    xdmcpmode=getParameter("weirdx.xdmcp.mode");              
    xdmcpaddr=getParameter("weirdx.xdmcp.address");           

    /*
    s=getParameter("weirdx.xdmcp.mode");
    if(s!=null){
      String ss=getParameter("weirdx.xdmcp.address");
      if(s.equals("query")){
        xdmcp=new XDMCP(ss, myAddress, displaynum);
      }
      else if(s.equals("broadcast")){
        xdmcp=new XDMCP(XDMCP.BroadcastQuery, ss, myAddress, displaynum);
      }
      else if(s.equals("indirect")){
        xdmcp=new XDMCP(XDMCP.IndirectQuery, ss, myAddress, displaynum);
      }
    }
    */

    s=getParameter("weirdx.xrexec");
    if(s!=null)sxrexec=s;

    s=getParameter("weirdx.sshrexec");
    if(s!=null)ssshrexec=s;

    s=getParameter("weirdx.jesd");
    if(s!=null && s.equals("yes")){
      jesd=s;
    }

    s=getParameter("weirdx.jdxpc");
    if(s!=null && s.length()!=0 && !s.equals("no")){
      jdxpc=s;
    }

    s=getParameter("weirdx.jdxpcport");
    if(s!=null){
      try{ jdxpcport=Integer.parseInt(s); }
      catch(Exception e){ }
    }

    s=getParameter("weirdx.jdxpc.socket");
    if(s!=null && s.length()!=0 && !s.equals("no")){
      jdxpcsocket=s;
    }

    s=getParameter("weirdx.jdxpc.serverproxy");
    if(s!=null && s.length()!=0 && !s.equals("no")){
      jdxpcserverproxy=s;
    }

    s=getParameter("weirdx.extension");
    if(s!=null)extension=s;

    s=getParameter("weirdx.display.background.alpha");
    if(s!=null){alphaBackground=s;}

/*
    s=getParameter("weirdx.display.background.alpha");
    if(s!=null){
      try{ 
//        int balpha=Integer.parseInt(s); 
//        balpha&=0xff;
//        if(balpha!=255){
          String ss=getParameter("weirdx.display.background.alpha.class");
          if(ss!=null && 
             ss.equals("com.jcraft.weirdx.DDXWindowAlphaBackground2D")){
            int balpha=Integer.parseInt(s); 
            balpha&=0xff;
            if(balpha!=255){
              Window.dDXWindow=DDXWindowAlphaBackground2D.class;
              DDXWindowAlphaBackground2D.setAlpha(balpha);
            }
          }	
          else{
            Window.dDXWindow=DDXWindowAlphaBackground.class;
            DDXWindowAlphaBackground.setAlpha(s);
	  }
//	}
      }
      catch(Exception ee){System.err.println(ee);}
    }
*/

    s=getParameter("weirdx.logo");
    if(s!=null)logo=s; 

    s=getParameter("weirdx.displaysocket");
    if(s!=null){
      try{
        displaySocketClass=Class.forName(s);
        if(!s.equals("com.jcraft.weirdx.DisplaySocket6k"))
          System.out.println(s+" is used for DisplaySocket");
      }
      catch(Exception e){
        System.err.println(e);
        displaySocketClass=com.jcraft.weirdx.DisplaySocket6k.class;
      }
    }
    else{
      displaySocketClass=com.jcraft.weirdx.DisplaySocket6k.class;
    }
  }

  public void start(){
    if(weirdx==null){
      return;
    }
    (new Spawn(this)).start();
  }


  public static void main(String args[]) {
    String s;
    WeirdX weirdx=new WeirdX();

    LOG.info("Starting Lynx...");
    
    Properties props=new Properties();
    try{
      InputStream rs = null;
      /* accept a command line argument of a URL from which to get
         properties. This is required because of a bug in netscape, where
         it refuses to properly load resources in certain cases */
      if (args!=null && args.length >= 2 && args[1]!=null) {
        URL propsurl=new URL(args[1]);
        rs=propsurl.openStream();
      }
      else{
       rs = weirdx.getClass().getResourceAsStream("/props");
      }
      if(rs!=null)
        props.load(rs);
    }
    catch(Exception e){ 
      //System.err.println(e);
    }

    try{
      String root=props.getProperty("user.dir", null);
      File guess=new File(new File(root, "config"), "props");
      props.load(new FileInputStream(guess));
    }
    catch(Exception e){ 
      //System.err.println(e);
    }

    Properties sprops=null;
    try{
      sprops=System.getProperties();
    } 
    catch (Exception e) {
      System.err.println("Unable to read system properties: "+e);
      sprops=new Properties();
    }
    for( Object e: props.keySet() ){
      String key=(String)e;
      //if(key.startsWith("weirdx.") && sprops.get(key)==null){
      //  System.setProperty(key, (String)(props.get(key)));
      //}
      if(key.startsWith("weirdx.") && sprops.get(key)==null){
        sprops.put(key, (String)(props.get(key)));
      }
    }

    try{
      System.setProperties(sprops);
      props=System.getProperties();
    }
    catch (Exception e) {
      System.err.println("Error updating system properties: "+e);
    }

    try{
      try{
	s=(String)props.get("weirdx.ddxwindow");
	if(s!=null){ Window.installDDXWindow(s); }
      }
      catch(Exception ee){ 
        System.err.println(ee);
      }

      try{
	s=(String)props.get("weirdx.display.width");
	if(s!=null){ width=Short.parseShort(s); }
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }

      try{ s=(String)props.get("weirdx.display.height");
	if(s!=null){ height=Short.parseShort(s); }
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }

      /* support for autodetect of screen size */
      try{ 
        s=(String)props.get("weirdx.display.autosize");
        if(s!=null){ 
          if (Boolean.valueOf(s).booleanValue()) {
              /* auto-calculate screen size */
                
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension ScrSize= toolkit.getScreenSize();
            int widthreduce=20;
            int heightreduce=60;
                 
            try{ 
              s=(String)props.get("weirdx.display.autosize.widthreduce");
              if(s!=null){ widthreduce=Short.parseShort(s); }
            } 
            catch(Exception ee){ }

            try{ 
              s=(String)props.get("weirdx.display.autosize.heightreduce");
              if(s!=null){ heightreduce=Short.parseShort(s); }
            } 
            catch(Exception ee){ }

            width=(short)(ScrSize.width-widthreduce);
            height=(short)(ScrSize.height-heightreduce);
          }
        }
      }
      catch(Exception ee){ 
        System.err.println(ee);
      }

      try{
	s=(String)props.get("weirdx.display.visual");
	if(s!=null){ visuals=s; }
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }

      try{
	s=(String)props.get("weirdx.windowmode");
	if(s!=null){
	  weirdx.mode=s;
	}
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }

      try{
	s=(String)props.get("weirdx.myaddress");
	if(s!=null){ myAddress=s; }
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }

      try{
	s=(String)props.get("weirdx.displaynum");
	if(s!=null){ weirdx.displayNumber=Integer.parseInt(s);}
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }

      try{
	s=(String)props.get("weirdx.display.acl");
	if(s!=null){ Acl.parse(s); }
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }

      try{
	s=(String)props.get("weirdx.display.threebutton");
	if(s.equals("yes")){ threeButton=true;}
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }

      try{
	s=(String)props.get("weirdx.display.copypaste");
	if(s.equals("yes")){ copypaste=true;}
      }
      catch(Exception ee){ 
        //System.err.println(ee);
      }
             
      try{                                                        
	s=(String)props.get("weirdx.display.keymap");              
	if(s!=null){ keymap=s; }
      }                                                           
      catch(Exception ee){                                        
	//System.err.println(ee);                                 
      }                                                           

      try{
	s=(String)props.get("weirdx.display.charset");
	if(s!=null&&s.length()>0){ charset=s; }
      }
      catch(Exception ee){
	//System.err.println(ee);
      }

      try{
        s=(String)props.get("weirdx.xdmcp.mode");
        if(s!=null){
	  String ss=(String)props.get("weirdx.xdmcp.address");
          if(s.equals("query")){
            xdmcp=new XDMCP(ss, myAddress, weirdx.displayNumber);
          }
          else if(s.equals("broadcast")){
            xdmcp=new XDMCP(XDMCP.BroadcastQuery, ss, myAddress, weirdx.displayNumber);
          }
          else if(s.equals("indirect")){
            xdmcp=new XDMCP(XDMCP.IndirectQuery, ss, myAddress, weirdx.displayNumber);
          }
	}
      }
      catch(Exception ee){
        //System.err.println(ee);
      }

      try{                                                        
	s=(String)props.get("weirdx.xrexec");                      
	if(s.equals("yes")){ 
	  xrexec=new XRexec(myAddress, weirdx.displayNumber);         
	}                                                         
      }                                                           
      catch(Exception ee){                                        
        //System.err.println(ee);                                 
      }

      try{                                                        
	s=(String)props.get("weirdx.sshrexec");                      
	ssshrexec=s;
      }                                                           
      catch(Exception ee){                                        
        //System.err.println(ee);                                 
      }

      try{
	s=(String)props.get("weirdx.jesd");
	if(s!=null && s.equals("yes")){
	  jesd=s;
	}
      }
      catch(Exception ee){}

      try{
	s=(String)props.get("weirdx.jdxpc");
	if(s!=null && s.length()!=0  && !s.equals("no")){
	  jdxpc=s;
	}
      }
      catch(Exception ee){}

      try{
	  s=(String)props.get("weirdx.jdxpcport");
	  if(s!=null){ jdxpcport=Integer.parseInt(s);}
      }
      catch(Exception ee){}

      try{
        s=(String)props.get("weirdx.jdxpc.socket");
        if(s!=null){ jdxpcsocket=s;}
      }
      catch(Exception ee){}

      try{
        s=(String)props.get("weirdx.jdxpc.serverproxy");
        if(s!=null){ jdxpcserverproxy=s;}
      }
      catch(Exception ee){}

      try{                                                        
	s=(String)props.get("weirdx.extension");
	if(s!=null){                                      
	  WeirdX.extension=s;
	}                                                         
      }                                                           
      catch(Exception ee){                                        
        //System.err.println(ee);                                 
      }

      try{                                                        
	s=(String)props.get("weirdx.display.background.alpha");
        if(s!=null){ alphaBackground=s;}
      }                                                           
      catch(Exception ee){                                        
        //System.err.println(ee);                                 
      }

/*
      try{                                                        
	s=(String)props.get("weirdx.display.background.alpha");
        if(s!=null){
          try{ 
//            int balpha=Integer.parseInt(s); 
//            balpha&=0xff;
//            if(balpha!=255){
  	      String ss=(String)props.get("weirdx.display.background.alpha.class");
              if(ss!=null && 
                ss.equals("com.jcraft.weirdx.DDXWindowAlphaBackground2D")){
                int balpha=Integer.parseInt(s); 
                balpha&=0xff;
                if(balpha!=255){
                  Window.dDXWindow=DDXWindowAlphaBackground2D.class;
                  DDXWindowAlphaBackground2D.setAlpha(balpha);
		}
              }
              else{
                Window.dDXWindow=DDXWindowAlphaBackground.class;
                DDXWindowAlphaBackground.setAlpha(s);
              }
//	    }
          }
          catch(Exception ee){System.err.println(ee);}
        }
      }                                                           
      catch(Exception ee){                                        
        //System.err.println(ee);                                 
      }
*/

      try{                                                        
	s=(String)props.get("weirdx.logo");
	if(s!=null){                                      
	  weirdx.logo=s;
	}                                                         
      }                                                           
      catch(Exception ee){                                        
        //System.err.println(ee);                                 
      }

      try{                                                        
	s=(String)props.get("weirdx.displaysocket");
	if(s!=null){                                      
          try{
            displaySocketClass=Class.forName(s);
            if(!s.equals("com.jcraft.weirdx.DisplaySocket6k"))
              System.out.println(s+" is used for DisplaySocket");
          }
          catch(Exception e){
            System.err.println(e);
            displaySocketClass=com.jcraft.weirdx.DisplaySocket6k.class;
          }
	}                                                         
      }                                                           
      catch(Exception ee){                                        
        //System.err.println(ee);                                 
      }

    }
    catch(Exception e){System.err.println(e);}

    WeirdX.weirdx=weirdx;
    Container container=new Frame("WeirdX");

    ((Frame)container).addWindowListener(
      new WindowAdapter() {                
        public void windowClosed(WindowEvent e){ System.exit(0); }
	public void windowClosing(WindowEvent e){ 
          ((Frame)e.getWindow()).dispose();
          System.exit(0); 
        }
      }
    );

		try {
			weirdx.weirdx_start(container);
		} 
		catch (Exception e) {
			LOG.fatal( "I/O Error: " + e.getLocalizedMessage() );
		}
	}

  static void resetScreen(int scrn){
    Client.closeDownAll();
    Colormap cmap=screen[0].defaultColormap;

    if((cmap.visual.clss & 1/*DynamicClass*/)!=0){
      cmap.flags|=4/*Colormap.BeingCreated*/;
      cmap.freeAll();
      try{
        cmap.allocColor(serverClient, 0, 0, 0);
	cmap.allocColor(serverClient, 255, 255, 255);
      }
      catch(Exception e){}
      cmap.mkIcm();
      cmap.flags&=~4/*Colormap.BeingCreated*/;
    }
    screen[0].root.initAttr();
  }

  void kill(){
    weirdx=null;
  }

  class Spawn extends Thread{
    WeirdX weirdx;
    Spawn(WeirdX j){
      super();
      weirdx=j;
    }
    public void run(){
      try{ 
        Container container=weirdx;
        weirdx_start(container); 
      }
      catch(Exception e){}
    }
  } 

  class SpawnJDxpc extends Thread{
    WeirdX weirdx;
    SpawnJDxpc(WeirdX weirdx){      
      super();                    
      this.weirdx=weirdx;           
    }                             
    public void run(){            

      try{ 
        Class<?>[] params=new Class[1];
        Object[] args=new Object[1];
	Object foo=null;

        java.lang.reflect.Constructor<?> constructor;

        Class<?> c = Class.forName(jdxpcserverproxy);

        params[0]=int.class;
	// get the constructor (int)
	constructor = c.getConstructor(params);

	args[0]=new Integer(WeirdX.jdxpcport);
	try{ foo = constructor.newInstance(args); }
	catch(java.lang.reflect.InvocationTargetException eee){
          System.err.println("fail to set jdxpcport="+WeirdX.jdxpcport);
          foo=c.newInstance();
        }

	java.lang.reflect.Method method;
        
        params[0]=int.class;
	method=c.getMethod("setServerPort", params);
	args[0]=new Integer(6000+weirdx.displayNumber);
	method.invoke(foo, args);

        params[0]=String.class;
	method=c.getMethod("setServerHost", params);
	args[0]="localhost";
	method.invoke(foo, args);

        if(jdxpcsocket!=null){
          params[0]=String.class;
  	  method=c.getMethod("setSocketClass", params);
	  args[0]=jdxpcsocket;
	  method.invoke(foo, args);
	}

	((Thread)foo).run();
      }
      catch(Exception e){
        System.err.println(e);
      }
    }
  } 

  	/**
  	 * Spanning SSH Rexec.
  	 * 
  	 * 
  	 */
	class SpawnSSHRexec extends Thread {
		WeirdX weirdx;

		SpawnSSHRexec( WeirdX weirdx ) {
			super();
			this.weirdx = weirdx;
		}

		@SuppressWarnings("unused")
		public void run() {
			LOG.debug( "Running SpawnSSHRexec..." );
			try {
				Class<?>[] params = new Class[1];
				Object[] args = new Object[1];
				Object foo = null;

				java.lang.reflect.Constructor<?> constructor;

				Class<?> c = Class.forName("com.jcraft.weirdx.SSHRexec");
				params[0] = int.class;
				constructor = c.getConstructor(params);

				args[0] = new Integer(weirdx.displayNumber);
				try {
					foo = constructor.newInstance(args);
				} catch (java.lang.reflect.InvocationTargetException eee) {
					LOG.fatal("Failed to set displaynumber "
							+ weirdx.displayNumber);
					foo = c.newInstance();
				}
			} catch (Exception e) {
				LOG.fatal("Exception " + e.getClass().getName() + ": "
						+ e.getLocalizedMessage());
			}
		}
	}

}

class DisplaySocket6k implements DisplaySocket {
	private static Log LOG = LogFactory.getLog(DisplaySocket6k.class);
	private ServerSocket sock = null;
	private int displayNumber;

	public void init(int displayNumber) throws java.io.IOException {
		this.displayNumber = displayNumber;
		sock = new ServerSocket(6000 + displayNumber);
		LOG.info( "Display number " + displayNumber + " initialized." );
	}

	public java.net.Socket accept() throws java.io.IOException {
		LOG.debug( "Accepting new process on display " + this.displayNumber );
		return sock.accept();
	}

	public void close() throws java.io.IOException {
		sock.close();
		LOG.info( "Display " + this.displayNumber + " closed." );
		sock = null;
	}
}
