package com.oxande.lynx;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.weirdx.Acl;
import com.jcraft.weirdx.Client;
import com.jcraft.weirdx.CopyPaste;
import com.jcraft.weirdx.Depth;
import com.jcraft.weirdx.DisplaySocket;
import com.jcraft.weirdx.Extension;
import com.jcraft.weirdx.Format;
import com.jcraft.weirdx.Keyboard;
import com.jcraft.weirdx.Keymap;
import com.jcraft.weirdx.Keymodifier;
import com.jcraft.weirdx.LogoImage;
import com.jcraft.weirdx.RootWindow;
import com.jcraft.weirdx.Screen;
import com.jcraft.weirdx.Visual;
import com.jcraft.weirdx.WeirdX;
import com.jcraft.weirdx.WeirdXBeanInfo;
import com.jcraft.weirdx.XColormap;
import com.jcraft.weirdx.XCursor;
import com.jcraft.weirdx.XDMCP;
import com.jcraft.weirdx.XFont;
import com.jcraft.weirdx.XPixmap;
import com.jcraft.weirdx.XResource;
import com.jcraft.weirdx.XRexec;
import com.jcraft.weirdx.XWindow;

public class LynX extends WeirdX {
	private static Log LOG = LogFactory.getLog(LynX.class);

	public static void main(String args[]) {
		String s;
		WeirdX weirdx = new WeirdX();

		LOG.info("Starting WeirdX...");
		    
		Properties props = new Properties();
		try {
			InputStream rs = null;
		    /* accept a command line argument of a URL from which to get
		       properties. This is required because of a bug in netscape, where
		       it refuses to properly load resources in certain cases */
		    if (args!=null && args.length >= 2 && args[1]!=null) {
		    	URL propsurl=new URL(args[1]);
		        rs = propsurl.openStream();
		    }
		    else {
		    	rs = weirdx.getClass().getResourceAsStream("/props");
		    }
		    
		    if( rs != null){
		        props.load(rs);
		    }
		}
		catch( Exception e ){
			LOG.error(e);
		}

		try {
			String root=props.getProperty("user.dir", null);
		    File guess=new File(new File(root, "config"), "props");
		    props.load(new FileInputStream(guess));
		}
		catch(Exception e){ 
			LOG.error(e);
		}

		Properties sprops=null;
		try {
			sprops=System.getProperties();
		} 
		catch (Exception e) {
		    LOG.error("Unable to read system properties: "+e);
		    sprops=new Properties();
		}
		
		for (Object e : props.keySet()) {
			String key = (String) e;
			if (key.startsWith("weirdx.") && sprops.get(key) == null) {
				sprops.put(key, (String) (props.get(key)));
			}
		}

		try {
			System.setProperties(sprops);
			props = System.getProperties();
		} catch (Exception e) {
			LOG.error("Error updating system properties: " + e);
		}

		try {
			try {
				s = (String)props.get("weirdx.ddxwindow");
				if( s!= null){ 
					XWindow.installDDXWindow(s); 
				}
		    }
		    catch(Exception ee){ 
		    	LOG.error(ee);
		    }

			try {
				s = (String) props.get("weirdx.display.width");
				if (s != null) {
					weirdx.width = Short.parseShort(s);
				}
			} catch (Exception ee) {
				LOG.error(ee);
			}

			try {
				s = (String) props.get("weirdx.display.height");
				if (s != null) {
					weirdx.height = Short.parseShort(s);
				}
			} catch (Exception ee) {
				LOG.error(ee);
			}

			/* support for autodetect of screen size */
			try {
				s = (String) props.get("weirdx.display.autosize");
				if (s != null) {
					if (Boolean.valueOf(s).booleanValue()) {
						/* auto-calculate screen size */

						Toolkit toolkit = Toolkit.getDefaultToolkit();
						Dimension ScrSize = toolkit.getScreenSize();
						int widthreduce = 20;
						int heightreduce = 60;

						try {
							s = (String) props
									.get("weirdx.display.autosize.widthreduce");
							if (s != null) {
								widthreduce = Short.parseShort(s);
							}
						} catch (Exception ee) {
							LOG.warn(ee);
						}

						try {
							s = (String) props
									.get("weirdx.display.autosize.heightreduce");
							if (s != null) {
								heightreduce = Short.parseShort(s);
							}
						} catch (Exception ee) {
						}

						weirdx.width = (short) (ScrSize.width - widthreduce);
						weirdx.height = (short) (ScrSize.height - heightreduce);
					}
				}
			} catch (Exception ee) {
				LOG.error(ee);
			}

			try {
				s = (String) props.get("weirdx.display.visual");
				if (s != null) {
					weirdx.visuals = s;
				}
			} catch (Exception ee) {
				LOG.warn(ee);
			}

			try {
				s = (String) props.get("weirdx.windowmode");
				if (s != null) {
					weirdx.mode = s;
				}
			} catch (Exception ee) {
				LOG.warn(ee);
			}

			try {
				s = (String) props.get("weirdx.myaddress");
				if (s != null) {
					myAddress = s;
				}
			} catch (Exception ee) {
				LOG.warn(ee);
			}

		      try{
			s=(String)props.get("weirdx.displaynum");
			if(s!=null){ weirdx.displayNumber=Integer.parseInt(s);}
		      }
		      catch(Exception ee){ 
		        LOG.warn(ee);
		      }

			try {
				s = (String) props.get("weirdx.display.acl");
				if (s != null) {
					Acl.parse(s);
				}
			} catch (Exception ee) {
				LOG.warn(ee);
			}

			try {
				s = (String) props.get("weirdx.display.threebutton");
				if (s.equals("yes")) {
					threeButton = true;
				}
			} catch (Exception ee) {
				LOG.warn(ee);
			}

			try {
				s = (String) props.get("weirdx.display.copypaste");
				if (s.equals("yes")) {
					copypaste = true;
				}
			} catch (Exception ee) {
				LOG.warn(ee);
			}
		             
			try {
				s = (String) props.get("weirdx.display.keymap");
				if (s != null) {
					keymap = s;
				}
			} catch (Exception ee) {
				LOG.warn(ee);
			}                                                     

			try {
				s = (String) props.get("weirdx.display.charset");
				if (s != null && s.length() > 0) {
					charset = s;
				}
			} catch (Exception ee) {
				LOG.warn(ee);
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
		        LOG.warn(ee);
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
//		            int balpha=Integer.parseInt(s); 
//		            balpha&=0xff;
//		            if(balpha!=255){
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
//			    }
		          }
		          catch(Exception ee){LOG.error(ee);}
		        }
		      }                                                           
		      catch(Exception ee){                                        
		        LOG.error(ee);                                 
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
		              LOG.warn(s+" is used for DisplaySocket");
		          }
		          catch(Exception e){
		            LOG.error(e);
		            displaySocketClass=com.jcraft.weirdx.DisplaySocket6k.class;
		          }
			}                                                         
		      }                                                           
		      catch(Exception ee){                                        
		        //System.err.println(ee);                                 
		      }

		    }
		    catch(Exception e){
		    	LOG.error(e);
		    }

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

}
