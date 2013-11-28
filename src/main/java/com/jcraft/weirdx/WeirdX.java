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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.applet.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.weirdx.res.XResource;
                                 

@SuppressWarnings("serial")
public class WeirdX extends Applet {

	private static Log LOG = LogFactory.getLog(WeirdX.class);

	static final Object LOCK = WeirdX.class;

	public static final int MAJOR_VERSION = 11;
	public static final int MINOR_VERSION = 0;
	public static final int RELEASE_NUMBER = 1032; // 1.0.32
	static final int motionBufferSize = 0;
	static final int maxRequestLength = 65535;
	
	/**
	 * The vendor.
	 */
	public static final String VENDOR = "JCraft,Inc.";

	static Screen[] screen = null;
	static Format[] format = null;

  static int imageByteOrder=1;        // 0=LSB, 1=MSB
  static int bitmapBitOrder=1;        // 0=LSB, 1=MSB
  static final int bitmapScanUnit=32; //32
  static final int bitmapScanPad=32;  //32

	public short width = 768;
	public short height = 576;

  public String visuals = "PseudoColor8";
  protected static String keymap = "101";
  public int displayNumber = 2;
  protected static String myAddress=null;

  protected static boolean threeButton=false;
  protected static boolean copypaste=false;

  static Client serverClient=null;
  public String mode = "InBrowser";
  public String logo = null;

  //static String extension=null;
  protected static String extension="DummySHAPE";

  protected static String charset=null;


  protected static XRexec xrexec=null;                              
  protected static String jesd=null;
  static String sxrexec=null;
  protected static String ssshrexec=null;

  protected static String jdxpc=null;
  protected static int jdxpcport=4000;
  protected static String jdxpcsocket=null;
  protected static String jdxpcserverproxy="com.jcraft.jdxpc.ServerProxy";

  protected static String alphaBackground=null;                             

  static AppletContext acontext=null;

  //static ServerSocket displaysocket=null;
  static DisplaySocket displaysocket=null;
  protected static Class<?> displaySocketClass=com.jcraft.weirdx.DisplaySocket6k.class;

  protected static XDMCP xdmcp=null;
  static String xdmcpmode=null;                           
  static String xdmcpaddr=null;                           

  protected static WeirdX weirdx=null;

  static final int InBrowser=0;
  static final int Rootless=1;
  static final int RootlessWM=2;

	void weirdx_init(Container container) {
		// try{ displaysocket=new ServerSocket(6000+weirdx.displaynum); }
		// catch(Exception ee){
		// System.out.println(ee+" bye");
		// return;
		// }

		try {
			displaysocket = (DisplaySocket) displaySocketClass.newInstance();
			displaysocket.init(weirdx.displayNumber);
		} catch (Exception ee) {
			LOG.error(ee + " bye");
			displaysocket = null;
			return;
		}

		Class<?> c = null;
		try {
			c = Class.forName("com.jcraft.weirdx.Keymap_" + keymap);
		} catch (Exception e) {
			try {
				c = Class.forName("com.jcraft.weirdx.Keymap_101");
			} catch (Exception ee) {
			}
		}
		try {
			if (c != null)
				Keymap.km = (Keymap) c.newInstance();
		} catch (Exception e) {
		}
		c = null;
		if (!keymap.equals("101")) {
			try {
				c = Class.forName("com.jcraft.weirdx.Keymodifier_" + keymap);
			} catch (Exception e) {
			}
		}
		if (c == null) {
			try {
				c = Class.forName("com.jcraft.weirdx.Keymodifier_gen");
			} catch (Exception e) {
			}
		}

		try {
			if (c != null)
				Keymodifier.kmod = (Keymodifier) c.newInstance();
		} catch (Exception e) {
		}

		if (Keymap.km != null) {
			Keyboard.keyboard = new Keyboard(Keymap.km.start, Keymap.km.start
					+ Keymap.km.count - 1);
		}

		// Client.releaseNumber = RELEASE_NUMBER;
		// Client.vendor=vendor;

		serverClient = new Client();
		serverClient.index = 0;
		Client.clients[0] = serverClient;
		XResource.initClientResource(serverClient);

		screen = Screen.init();

		XColormap.init();
		Extension.init(extension);

		Depth[] depth = null;

		Visual[] visual = null;
		Visual defaultv = null;

		{
			List<Depth> depthv = new ArrayList<Depth>();

			if (visuals.indexOf("TrueColor16") != -1) {
				visual = Visual.getTrueColor16(serverClient);
				defaultv = visual[0];
				depthv.add(new Depth(16, visual));

				imageByteOrder = 0;
				bitmapBitOrder = 0;
			}

			if (defaultv == null && visuals.indexOf("PseudoColor8") != -1) {
				visual = Visual.getPseudoColor8(serverClient);
				defaultv = visual[0];
				depthv.add(new Depth(8, visual));
			}

			if (defaultv == null && visuals.indexOf("StaticGray8") != -1) {
				visual = Visual.getStaticGray8(serverClient);
				defaultv = visual[0];
				depthv.add(new Depth(8, visual));
			}

			if (defaultv == null) {
				visual = Visual.getStaticGray1(serverClient);
				defaultv = visual[0];
				depthv.add(new Depth(1, visual));
			}

			depth = new Depth[depthv.size()];
			for (int i = 0; i < depthv.size(); i++) {
				depth[i] = (Depth) depthv.get(i);
			}
			depthv.clear();
		}

		int rootid = XResource.fakeClientId(serverClient);
		int colormapid = XResource.fakeClientId(serverClient);

		XFont.init(XResource.fakeClientId(serverClient), charset);

		XCursor.rootCursor = new XCursor(XResource.fakeClientId(serverClient));
		XResource.add(XCursor.rootCursor);

		int fgPixel = 1, bgPixel = 0;
		if (visuals.indexOf("TrueColor16") != -1) {
			fgPixel = 0xffff;
			bgPixel = 0x000000;
		}

		screen[0] = new Screen(rootid, colormapid, fgPixel, bgPixel, 0, width,
				height, width / 3, height / 3, 1, 1, defaultv.id, 0, 0,
				defaultv.depth.depth, depth);

		if (weirdx.mode.equals("MultiWindow") || weirdx.mode.equals("Rootless")) {
			screen[0].windowmode = Rootless;
		} else if (weirdx.mode.equals("MultiWindowWM")
				|| weirdx.mode.equals("RootlessWM")) {
			screen[0].windowmode = RootlessWM;
		} else {
			screen[0].windowmode = InBrowser;
		}

		{
			int vcount = 0;
			for (int i = 0; i < depth.length; i++) {
				vcount += depth[i].getVisual().length;
			}
			visual = new Visual[vcount];
			vcount = 0;
			for (int i = 0; i < depth.length; i++) {
				Visual[] tmp = depth[i].getVisual();
				for (int j = 0; j < tmp.length; j++) {
					visual[vcount] = tmp[j];
					vcount++;
				}
			}
		}

		screen[0].visual = visual;

		if (defaultv.depth.depth == 8) {
			format = new Format[2];
			format[0] = new Format((byte) 1, (byte) 1, (byte) 32);
			format[1] = new Format((byte) 8, (byte) 8, (byte) 32);
		} else if (defaultv.depth.depth == 16) {
			format = new Format[2];
			format[0] = new Format((byte) 1, (byte) 1, (byte) 32);
			format[1] = new Format((byte) 16, (byte) 16, (byte) 32);
		} else {
			format = new Format[1];
			format[0] = new Format((byte) 1, (byte) 1, (byte) 32);
		}

		Format.format = format;
		// {
		// int len=0;
		// for(int i=0; i<screen.length; i++){ len+=screen[i].getLength(); }
		// if(format!=null) len+=2*format.length;
		// len+=((VENDOR.getBytes().length+3)/4);
		// len+=8;
		// Client.initialLength=len;
		// }
		screen[0].defaultColormap = XColormap.getColormap(colormapid,
				screen[0], defaultv, 0, serverClient);
		XColormap cmap = screen[0].defaultColormap;
		XColormap.installed[0] = cmap;

		if (defaultv.depth.depth != 16) {
			cmap.flags |= 4/* Colormap.BeingCreated */;
			try {
				cmap.allocColor(serverClient, 0, 0, 0);
				cmap.allocColor(serverClient, 255, 255, 255);
				if (defaultv.clss == 0 && // StaticGray
						defaultv.depth.depth == 8) {
					for (int i = 1; i < 255; i++) {
						cmap.allocColor(serverClient, i, i, i);
					}
				}
			} catch (Exception e) {
			}
			cmap.mkIcm();
			cmap.flags &= ~4/* Colormap.BeingCreated */;
		}

		RootWindow w = null;
		try {
			w = new RootWindow(container, screen[0], format, serverClient);
		} catch (Exception e) {
		}

		try {
			w.mapWindow(serverClient);
		} catch (Exception e) {
		}

		XPixmap.init(screen);

		if (copypaste)
			CopyPaste.init();

		LogoImage.init(screen);
		LogoImage.loadLogo(logo);

		if (jesd != null) {
			try {
				c = Class.forName("com.jcraft.jesd.Daemon");
				Thread foo = (Thread) c.newInstance();
				foo.start();
			} catch (Exception e) {
				LOG.error("WeirdX: " + e);
			}
		}
	}

	
  	public void weirdx_start(Container container) throws ConnectException {
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

    InputOutput client = null;

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
	  LOG.error(e);
          if(e instanceof NullPointerException){
            weirdx=null;
            break;
          }
	  continue;
	}

	if(!Acl.check(socket.getInetAddress())){
	  LOG.error("ACL warning: unauthorized access from "+
			     socket.getInetAddress());
	  try{socket.close();}catch(Exception e){};
	  continue;
	}

	try{ socket.setTcpNoDelay(true); }
	catch(Exception eeee){
	  //System.out.println(eeee+" tcpnodelay");
	}

	client = null;

	in=socket.getInputStream();
	out=socket.getOutputStream();

	try{in.read(byte_order, 0, 1);}
	catch(Exception e){continue;}

        // 0x6c LSB
        // 0x42 MSB
	if(byte_order[0]==0x6c){ client=new IOLSB(); }
	else if(byte_order[0]==0x42){ client=new IOMSB(); }
	else {
	  LOG.warn("protocol: error "+
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
      XWindow.installDDXWindow(s);
    }
    
    s=getParameter("weirdx.windowmode");
    if(s!=null){
      mode=s; 
    }

    s=getParameter("weirdx.displaynum");
    if(s!=null){
      try{ displayNumber=Integer.parseInt(s); }
      catch(Exception ee){
    	  LOG.error(ee);
      }
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
      catch(Exception ee){
      	LOG.error(ee);
      }
    }
*/

    s=getParameter("weirdx.logo");
    if(s!=null)logo=s; 

    s=getParameter("weirdx.displaysocket");
    if(s!=null){
      try{
        displaySocketClass=Class.forName(s);
        if(!s.equals("com.jcraft.weirdx.DisplaySocket6k"))
          LOG.warn(s+" is used for DisplaySocket");
      }
      catch(Exception e){
        LOG.error(e);
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



  static void resetScreen(int scrn){
    Client.closeDownAll();
    XColormap cmap=screen[0].defaultColormap;

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
          LOG.error("fail to set jdxpcport="+WeirdX.jdxpcport);
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
        LOG.error(e);
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
