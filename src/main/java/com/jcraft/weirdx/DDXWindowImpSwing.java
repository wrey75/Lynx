/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - Guess.
 *
 * Copyright (C) 1999-2004 JCraft, Inc.
 * Modifications (C) 2000 Marcus Schiesser (ms)
 *
 * 02-Aug-2000: ms modified key events to handle the ALT_GRAPH key
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
import java.awt.image.*;
import java.awt.event.KeyEvent;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;


//import com.sun.java.swing.*;
import javax.swing.*;                                    

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class DDXWindowImpSwing extends JPanel 
                        implements DDXWindow,
                                   MouseListener, MouseMotionListener { 
	static Log LOG = LogFactory.getLog(DDXWindowImpSwing.class);
  static boolean clck_toggle=false;

  private static final byte s=0;           // start
  private static final byte sp=1;          // press
  private static final byte spp=2;         // press, press
  private static final byte sppr=3;        // press, press, release
  private static final int ALT_GR_MASK=32;
  private static byte threeBstate=s;
  private static MouseEvent threeBPressed=null;
  private static boolean threeButton=false;

  private static final int InputOnly=2;
  private static Client serverClient=null;
  private static DDXWindow ddxwindow=null;
  private static final int VK_ALT_GRAPH = 0x15;

  Window window;
  int width, height;
  int bw;

  boolean bwc=false;

  static Event event=new Event();

  Image offi=null;
  Graphics offg=null;

  static int px, py;

  Rectangle exposed=new Rectangle();

  DDXWindowImpSwing(){
  }

  public void init(Window w){
    if(serverClient==null){
      ddxwindow=w.screen.root.ddxwindow;
      threeButton=WeirdX.threeButton;
      serverClient=w.screen.root.client;
    }
    this.window=w;
    this.bw=w.borderWidth;
    bwc=true;
    setSize(w.width, w.height);
    addMouseListener(this);
    addMouseMotionListener(this);
    //addKeyListener(this);
    px=0;
    py=0;
    enableEvents(AWTEvent.KEY_EVENT_MASK);
    setVisible(false); //???
    setLayout(null);

    clck_toggle=false;
  }

  public void setBorder(int bw){
    if(this.bw!=bw){
      this.bw=bw;
      bwc=true;
    }
  }

  public void setVisible(boolean b){
    if(b){
      if(offi==null && window.clss!=InputOnly){
        allocImage();
      }
      if(window!=window.screen.root && !isVisible()){
	exposed.setBounds(0, 0, 0, 0);
      }
      super.setVisible(true);
      if(window.screen.windowmode!=WeirdX.InBrowser && window.hasFrame()){
        java.awt.Window frame=window.getFrame();

        if(frame instanceof java.awt.Frame){
          frame.add("Center", this);
          frame.pack();
          synchronized(Window.LOCK){
            Property p=window.getProperty();
            while(p!=null){
  	      if(p.propertyName==39)break;
              p=p.next;
            } 
            if(p!=null && p.type==31){
	      String title="";
	      if(p.size>0){
		title=new String(p.data);
	      }
	      ((java.awt.Frame)(frame)).setTitle(title);
	    }
          }
	}
        else{
          frame.add(this);
	}

        frame.validate();
        Insets insets=frame.getInsets();
        frame.setSize(window.width+window.borderWidth*2+
                      insets.left+insets.right,
		      window.height+window.borderWidth*2+
                      insets.top+insets.bottom);
        super.setLocation(insets.left, insets.top);
        frame.validate();
      }
    }
    else{
      if(isVisible()){
	super.setVisible(false);
	if(window!=window.screen.root &&
	   window.screen.root.width*window.screen.root.height/4<=
	   window.width*window.height){
	  freeImage();
	  exposed.setBounds(0, 0, 0, 0);
	}
      }
    }
  }

  public void setLocation(int x, int y){
    Point p = getLocation();
    if(p.x==x && p.y==y) return;
    if(window!=null && window.parent!=null){
      int orgx=p.x-window.parent.borderWidth+window.borderWidth;
      int orgy=p.y-window.parent.borderWidth+window.borderWidth;
      int bitgrabity=window.attr&(15<<8);
      if(orgx<0 || orgy<0){
	if(orgx<0 && orgx<x && 
	   (bitgrabity!=(3<<8)) &&    // NorthEast
	   (bitgrabity!=(6<<8)) &&    // East
	   (bitgrabity!=(9<<8))){
	  orgx=(orgx*-1 - (x*-1));
	  exposed.x+=orgx;
	  exposed.width-=orgx;
	  if(exposed.width<0)exposed.width=0;
	}
	if(orgy<0 && orgy<y && 
	   (bitgrabity!=(7<<8)) &&    // NorthEast
	   (bitgrabity!=(8<<8)) &&    // East
	   (bitgrabity!=(9<<8))){
	  orgy=(orgy*-1 - (y*-1));
	  exposed.y+=orgy;
	  exposed.height-=orgy;
	  if(exposed.height<0){exposed.width=0;exposed.height=0;}
	}
      }
    }
    if(window.screen.windowmode!=WeirdX.InBrowser && window.hasFrame()){
      window.getFrame().                                       
	setLocation(window.origin.x-window.borderWidth+        
		    window.parent.borderWidth,                 
		    window.origin.y-window.borderWidth+        
		    window.parent.borderWidth);                
//    if(x<0 && y<0){
//        System.out.println("x="+x+", y="+y+", width="+window.width+
//                           ", height="+window.height);
//    }	
    }                                                            
    else{
      super.setLocation(x, y);
    }
  }

  public void setSize(int w, int h){
    boolean resized=false;
    if(w!=this.width || h!=this.height) resized=true;

    if(!resized && !bwc){
      return;
    }

    int offx=0, offy=0;

    int bitgrabity=window.attr&(15<<8);
    if(bitgrabity!=0){
      if(bitgrabity==(7<<8) ||    // SouthWest
	 bitgrabity==(8<<8) ||    // South
         bitgrabity==(9<<8)){     // SouthEast
	offy=this.height-h;
      }
      if(bitgrabity==(3<<8) ||    // NorthEast
	 bitgrabity==(6<<8) ||    // East
	 bitgrabity==(9<<8)){     // SouthEast
	offx=this.width-w;
      }
    }

    //int originalWidth = this.width, originalHeight = this.height;
    this.width=w;
    this.height=h;
    super.setSize(w+2*bw, h+2*bw);

    bwc=false;

    if(window.screen.windowmode!=WeirdX.InBrowser && window.hasFrame()){
      java.awt.Window frame=window.getFrame();
      frame.validate();
      Insets insets=frame.getInsets();
      frame.setSize(w+2*bw+insets.left+insets.right,   
                    h+2*bw+insets.top+insets.bottom);  
      frame.validate();
    }                                                              

    if(window.clss==InputOnly){
      return;
    }

    if(offi!=null && resized){
      if(w<exposed.x || h<exposed.y){
        exposed.setBounds(0, 0, 0, 0);
      }
      else if(w<(exposed.x+exposed.width) ||
	 h<(exposed.y+exposed.height)){
        exposed.setBounds(exposed.x, exposed.y, 
                          (exposed.width<(w-exposed.x) ? exposed.width : (w-exposed.x)),
                          (exposed.height<(h-exposed.y) ? exposed.height : (h-exposed.y)));
      }

      try{
	Image tmp=offi;
	Graphics tmpg=offg;

	offi=createImage(w,h);
	offg=offi.getGraphics();
	window.makeBackgroundTile(0, 0, w, h);
	if(bitgrabity!=0){
	  offg.drawImage(tmp, 0, 0, this);
	}

	if(offx!=0 || offy!=0){
	  int copyx=0, copyy=0, copyw=w, copyh=h, dx=0, dy=0;
	  if(offy>0){ copyy=offy; copyh=h-offy; dy=offy*-1; }
	  else if(offy<0){ dy=offy*-1; }
	  if(offx>0){ copyx=offx; copyw=w-offx; dx=offx*-1; }
	  else if(offx<0){ dx=offx*-1; }
	  offg.copyArea(copyx, copyy, copyw, copyh, dx, dy);
	}

	if(tmp!=offi){
	  tmp.flush();
	  tmpg.dispose();
	}

	if(bitgrabity==0){
	  exposed.setBounds(0, 0, 0, 0);
          window.makeBackgroundTile(0, 0, width, height);
	}
        window.currentGC=null;
      }
      catch(Exception e){
	LOG.error(e);
	offi=null;
	offg=null;
      }
      catch(java.lang.OutOfMemoryError e){
    	  LOG.error(e);
	offi=null;
	offg=null;
      }
      //System.out.println("DDXW: "+getBackground());
    }
  }

  private void allocImage(){
    try{
      offi=createImage(width, height);
      offg=offi.getGraphics();
      offg.setPaintMode();
      if(window!=window.screen.root){
	window.makeBackgroundTile(0, 0, width, height);
      }
    }
    catch(Exception e){
    	LOG.error(e);
      offi=null;
      offg=null;
    }
    catch(java.lang.OutOfMemoryError e){
    	LOG.error(e);
      offi=null;
      offg=null;
    }
  }

  private void freeImage(){
    if(offi!=null){
      offi.flush();
      offi=null;
    }
    if(offg!=null){
      offg.dispose();
      offg=null;
    }
    window.gmask=0;
  }

  public void setBackground(Color color, int x, int y, int w, int h){
    super.setBackground(color);

    if(offg==null){ return; }
    Color tmp=offg.getColor();
    offg.setColor(color);
    offg.fillRect(x, y, w, h);
    offg.setColor(tmp);
  }

  public void setBackground(Color color){
    setBackground(color, 0, 0, width, height);
  }

  public Dimension getPreferredSize(){
    return getSize();
  }

  public boolean isOptimizedDrawingEnabled() {
    return false;
  }

  public void draw(int x, int y, int width, int height){
    if(!isVisible()) return;
    repaint(x, y, width, height); 
  }

  public void draw(){
    if(!isVisible()) return;
    repaint();
  }

  public void paintBorder(Graphics g){
  }

  public void paintComponent(Graphics g){
    if(window.clss==InputOnly){
      super.paintComponent(g);
      return;
    }

    if(offi==null) return;

    g.drawImage(offi, bw, bw, this);

    Rectangle r=g.getClip().getBounds();
    if(bw>0 &&
       (r.x<=bw || r.y<=bw || 
	(width+bw)<=(r.width+r.x) ||
	(height+bw)<=(r.height+r.y)) ){
      if(window.isBorderPixel()){
	g.setColor(window.getColormap().getColor(window.border.pixel));
      }
      else{
	g.setColor(Color.black);
      }
      for(int i=bw-1;0<=i; i--){
	g.drawRect(i, i, width+2*bw-i*2-1, height+2*bw-i*2-1);
      }
    }

    if(window.screen.windowmode==WeirdX.InBrowser ||
       window==window.screen.root){
      if(LogoImage.logoimage!=null &&
	 window.x+r.x<=LogoImage.logoimagewidth && 
	 window.y+r.y<=LogoImage.logoimageheight){
	g.drawImage(LogoImage.logoimage, 
		    0, 0, 
		    LogoImage.logoimagewidth-(window.x-bw), 
		    LogoImage.logoimageheight-(window.y-bw), 
		    window.x-bw, window.y-bw, 
		    LogoImage.logoimagewidth,
		    LogoImage.logoimageheight,
		    this);
      }
      else{
      }
    }
  }

  public boolean isOpaque(){
    if(isVisible()){
      if(window.clss==InputOnly){
	return false;
      }
      return super.isOpaque();
    }
    return false;
  }

  private final void expunion(Rectangle r) {
    int x1=Math.min(exposed.x, r.x);
    int x2=Math.max(exposed.x+exposed.width, r.x+r.width);
    int y1=Math.min(exposed.y, r.y);
    int y2=Math.max(exposed.y+exposed.height, r.y+r.height);
    exposed.setBounds(x1, y1, x2-x1, y2-y1);
  }

  private final boolean expcontains(int X, int Y, int W, int H) {
    int ww=exposed.width;
    int hh=exposed.height;
    if (ww<=0 || hh<=0 || W<=0 || H<=0) {
      return false;
    }
    int xx=exposed.x;
    int yy=exposed.y;
    return (X>=xx && Y>=yy && X+W<=xx+ww && Y+H<=yy+hh);
  }

  public void update(Graphics g){ LOG.info("update: "); }

  public void paint(Graphics g){
try{                              // ?????
    super.paint(g);
}catch(Exception e){
return;
}
    Client client=window.client;
    if(client==null || client==serverClient) return;
    Rectangle r=g.getClip().getBounds();
    if(exposed.width==0){
      exposed.setBounds(r);
    }
    else if(expcontains(r.x, r.y, r.width, r.height)){
      return;
    }
    else{
      expunion(r);
    }

    event.mkExpose(window.id, r.x, r.y, r.width, r.height, 0);
    try{ window.sendEvent(event, 1, null); }
    catch(Exception ee){}
  }

  public void setBorderPixmap(Pixmap p){
  }

  public void mouseClicked(MouseEvent e){
  }
  public void mouseEntered(MouseEvent e){

    if(window!=null && window.getFrame()!=null){
      java.awt.Window frame=window.getFrame();
      if((frame instanceof java.awt.Frame) &&
	 frame==e.getSource()){
        try{
          if(window.isRealized()){
            Window.setInputFocus(window.client, window.id, 1,
				 (int)System.currentTimeMillis(), false);
	  }
	}
	catch(Exception ee){
		LOG.error(ee);
	}
	return;
      }
    }

    String str=CopyPaste.getString();
    if(!CopyPaste.isOwner() && str!=null){
      synchronized(window.screen.root){
        Property p=window.screen.root.getProperty();
        while(p!=null){
	  if(p.propertyName==9)break;
	  p=p.next;
        }
        if(p!=null){
          p.data=str.getBytes();
          p.size=p.data.length;
	}
      }

      Selection sel=Selection.getSelection(1); // PRIMARY
      if(sel!=null && sel.client!=null){
        int time=(int)System.currentTimeMillis();
        Event event=new Event();
        event.mkSelectionClear(time, sel.wid, sel.selection);
        try{
          sel.client.sendEvent(event,
                               1,
                               Event.NoEventMask,
                               Event.NoEventMask,
                               null);
        }
        catch(Exception ee){ 
          //System.out.println(ee);
        };
        sel.window=window.screen.root;
        sel.wid=window.screen.root.id;
        sel.lastTimeChanged=time;
        sel.client=null;
      }
      CopyPaste.setString(str);
    }

    if(window.id==window.screen.rootId){
      return;
    }

    if(window.isMapped()){
      requestFocus();
      Window.focus.win=window.id;
    }

    int x=e.getX()+window.x;
    int y=e.getY()+window.y;

    Window.sprite.hot.x=x;
    Window.sprite.hot.y=y;

    int mod=e.getModifiers();
    int state=0;
    if((mod & InputEvent.BUTTON1_MASK)!=0) state|=(1<<8);
    if((mod & InputEvent.BUTTON2_MASK)!=0) state|=(1<<9);
    if((mod & InputEvent.BUTTON3_MASK)!=0) state|=(1<<10);
    if((mod & InputEvent.SHIFT_MASK)!=0) state|=1;
    if((mod & InputEvent.CTRL_MASK)!=0) state|=4;
                                  // alt -> state|=8;
    Client client=window.client;
    if(client==null || client==serverClient) return;

    event.mkEnterNotify(0, // Ancestor
			window.screen.rootId, window.id, 0,
			x, y, e.getX(), e.getY(), state, 
			0, // Normal
			0x1|0x02  // focus|same-screen
			);
    try{
      Window.sendDeviceEvent(window, event, Window.grab, null, 1);
    }
    catch(Exception ee){
    }
  }

  public void mouseExited(MouseEvent e){
    if(window==null) return;

    if(window.id==window.screen.rootId){
      return;
    }

    int x=e.getX()+window.x;
    int y=e.getY()+window.y;

    Window.sprite.hot.x=x;
    Window.sprite.hot.y=y;

    int mod=e.getModifiers();
    int state=0;
    if((mod & InputEvent.BUTTON1_MASK)!=0) state|=(1<<8);
    if((mod & InputEvent.BUTTON2_MASK)!=0) state|=(1<<9);
    if((mod & InputEvent.BUTTON3_MASK)!=0) state|=(1<<10);
    if((mod & InputEvent.SHIFT_MASK)!=0) state|=1;
    if((mod & InputEvent.CTRL_MASK)!=0) state|=4;
                                  // alt -> state|=8;
    Client client=window.client;
    if(client==null || client==serverClient) return;

    event.mkLeaveNotify(0, // Ancestor
			window.screen.rootId, window.id, 0,
			x, y, e.getX(), e.getY(), state, 
			0, // Normal
			0x1|0x02  // focus|same-screen
			);
    try{
      Window.sendDeviceEvent(window, event, Window.grab, null, 1);
    }
    catch(Exception ee){
    }
  }

  public void mousePressed(MouseEvent e){
    if(threeButton){
      if(threeBstate==s){
	threeBPressed=e;
	threeBstate=sp;
	return;
      }
      if(threeBstate==sp
	 //       || threeBstate==sppr
	 ){
	threeBPressed=null;
	threeBstate=spp;
	e=new MouseEvent((Component)e.getSource(), e.getID(), e.getWhen(),
			 (e.getModifiers()&(~(InputEvent.BUTTON1_MASK|InputEvent.BUTTON3_MASK)))|InputEvent.BUTTON2_MASK,
			 e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
      }
    }

    procPressed(e);
  }

  private void procPressed(MouseEvent e){
    int x=e.getX()+window.x;
    int y=e.getY()+window.y;
    Window.sprite.hot.x=x;
    Window.sprite.hot.y=y;
    int mod=e.getModifiers();
    if(mod==0){ mod|=InputEvent.BUTTON1_MASK; } // ?????

    int state=0;
    int detail=1;
    if((mod & InputEvent.BUTTON1_MASK)!=0) detail=1;
    if((mod & InputEvent.BUTTON2_MASK)!=0) detail=2;
    if((mod & InputEvent.BUTTON3_MASK)!=0) detail=3;
    if((mod & InputEvent.SHIFT_MASK)!=0) state|=1;
    if((mod & InputEvent.CTRL_MASK)!=0) state|=4;
                                  // alt -> state|=8;

    Event.filters[Event.MotionNotify]=Event.PointerMotionMask|
                                      Event.ButtonMotionMask |
  				      ((Event.Button1Mask>>1)<<detail);

    event.mkButtonPress(detail, window.screen.rootId, window.id, 0,
			x, y, e.getX(), e.getY(), state, 1);

    try{
      if(Window.grab==null){
	if(Window.checkDeviceGrabs(event, 0, 1)){
	  return;
	}
      }
      if(Window.grab!=null)
	Window.sendGrabbedEvent(event, false, 1);
      else
	Window.sendDeviceEvent(window, event, Window.grab, null, 1);
    }
    catch(Exception ee){
    }

    if((mod & InputEvent.BUTTON1_MASK)!=0){ state|=(1<<8);}
    if((mod & InputEvent.BUTTON2_MASK)!=0){ state|=(1<<9);}
    if((mod & InputEvent.BUTTON3_MASK)!=0){ state|=(1<<10);}
//    if((mod & InputEvent.SHIFT_MASK)!=0) state|=1;
//    if((mod & InputEvent.CTRL_MASK)!=0) state|=4;
                                  // alt -> state|=8;

    Window.sprite.hot.state=state;
  }
  public void mouseReleased(MouseEvent e){
    if(threeButton){
      if(threeBstate==sppr){
	threeBPressed=null;
	threeBstate=s;
	return;
      }
      if(threeBstate==sp){
	procPressed(threeBPressed);
	threeBPressed=null;
	threeBstate=s;
      }
      else if(threeBstate==spp){
	threeBPressed=null;
	threeBstate=sppr;
	e=new MouseEvent((Component)e.getSource(), e.getID(), e.getWhen(),
			 (e.getModifiers()&(~(InputEvent.BUTTON1_MASK|InputEvent.BUTTON3_MASK)))|InputEvent.BUTTON2_MASK,
			 e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
      }
    }

    procReleased(e);
  }
  private void procReleased(MouseEvent e){
    int x=e.getX()+window.x;
    int y=e.getY()+window.y;

    Window.sprite.hot.x=x;
    Window.sprite.hot.y=y;

    int mod=e.getModifiers();
    int state=0;
    int detail=0;

    if((mod & InputEvent.BUTTON1_MASK)!=0){ state|=(1<<8); detail=1;}
    if((mod & InputEvent.BUTTON2_MASK)!=0){ state|=(1<<9); detail=2;}
    if((mod & InputEvent.BUTTON3_MASK)!=0){ state|=(1<<10); detail=3;}
    if((mod & InputEvent.SHIFT_MASK)!=0) state|=1;
    if((mod & InputEvent.CTRL_MASK)!=0) state|=4;
                                  // alt -> state|=8;
    Window.sprite.hot.state=0; // ?????
    Event.filters[Event.MotionNotify]=Event.PointerMotionMask/*|
  				      ((Event.Button1Mask>>1)<<detail)*/;
    event.mkButtonRelease(detail, window.screen.rootId, window.id, 0,
			  x, y, e.getX(), e.getY(), state, 1);

    try{
      if(Window.grab!=null) Window.sendGrabbedEvent(event, true, 1);
      else Window.sendDeviceEvent(window, event, Window.grab, null, 1);
    }
    catch(Exception ee){
    }
    Window.grab=null;
  }

  @SuppressWarnings("unused")
public void mouseDragged(MouseEvent e){

    if(threeButton){
      if(threeBstate!=s){
	if(threeBstate==sp){
	  procPressed(threeBPressed);
	  threeBPressed=null;
	  threeBstate=s;
	}
	else if(threeBstate==spp){
	  e=new MouseEvent((Component)e.getSource(), e.getID(), e.getWhen(),
			   (e.getModifiers()&(~(InputEvent.BUTTON1_MASK|InputEvent.BUTTON3_MASK)))|InputEvent.BUTTON2_MASK,
			   e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
	}
	else if(threeBstate==sppr){
	  return;
	}
      }
    }

    int x=e.getX()+window.x;
    int y=e.getY()+window.y;

    Window.sprite.hot.x=x;
    Window.sprite.hot.y=y;

    int mod=e.getModifiers();

// button 1 -> 16
// button 2 -> 8
// button 3 -> 4
// shift -> 1
// control -> 2
// alt -> 12 ?? 

    int state=0;
    int detail=0;

    if((mod & InputEvent.BUTTON1_MASK)!=0){ state|=(1<<8); detail=1;}
    if((mod & InputEvent.BUTTON2_MASK)!=0){ state|=(1<<9); detail=2;}
    if((mod & InputEvent.BUTTON3_MASK)!=0){ state|=(1<<10); detail=3;}
    if((mod & InputEvent.SHIFT_MASK)!=0) state|=1;
    if((mod & InputEvent.CTRL_MASK)!=0) state|=4;
                                  // alt -> state|=8;
    Window.sprite.hot.state=state;

    px=x;
    py=y;

    event.mkMotionNotify(1, window.screen.rootId, /*window.id,*/
			 Window.sprite.win.id, 0, 
			 px, py, 
			 e.getX(),
			 e.getY(),
			 state, 1);


    try{
      if(!Window.checkMotion(event, window)){
	return;
      }
      event.mkMotionNotify(1, window.screen.rootId, /*window.id,*/
			   Window.sprite.win.id, 0, 
			   px, py, 
			   px-Window.sprite.win.x,
			   py-Window.sprite.win.y,
			   //e.getX(),
			   //e.getY(),
			   state, 1);
      if(Window.grab!=null)
	Window.sendGrabbedEvent(event, false, 1);
      else
	Window.sendDeviceEvent(Window.sprite.win, 
				   event, Window.grab, null, 1);
    }
    catch(Exception ee){
    }
  }

  public void mouseMoved(MouseEvent e){
    int x=e.getX()+window.x;
    int y=e.getY()+window.y;

    Window.sprite.hot.x=x;
    Window.sprite.hot.y=y;

    int mod=e.getModifiers();
    int state=0;

    px=x;
    py=y;

    if((mod & InputEvent.BUTTON1_MASK)!=0) state|=(1<<8);
    if((mod & InputEvent.BUTTON2_MASK)!=0) state|=(1<<9);
    if((mod & InputEvent.BUTTON3_MASK)!=0) state|=(1<<10);
    if((mod & InputEvent.SHIFT_MASK)!=0) state|=1;
    if((mod & InputEvent.CTRL_MASK)!=0) state|=4;
                                  // alt -> state|=8;
    Window.sprite.hot.state=state;

    event.mkMotionNotify(0, window.screen.rootId, window.id, 0, 
			 x, y, 
			 x-window.x,
			 y-window.y,
			 state, 1);

    try{
      if(!Window.checkMotion(event, window)){
	return;
      }
      if(Window.grab!=null)
	Window.sendGrabbedEvent(event, false, 1);
      else
	Window.sendDeviceEvent(window, event, Window.grab, null, 1);
    }
    catch(Exception ee){
    }
  }
 
  public void processKeyEvent(KeyEvent e){
    int id=e.getID();
    if(id==KeyEvent.KEY_PRESSED) { keyPressed(e); } 
    else if(id==KeyEvent.KEY_RELEASED) { keyReleased(e); } 
    else if(id==KeyEvent.KEY_TYPED) { keyTyped(e); }
    e.consume(); // ??
  }

  public void keyPressed(KeyEvent e){
    if(!window.isMapped()) return;

    if(e.getKeyCode()==KeyEvent.VK_CAPS_LOCK){
      if(clck_toggle){
        clck_toggle=false;
        Window.sprite.hot.state&=(~1);
      }
      else{
        clck_toggle=true;
        Window.sprite.hot.state|=1;
      }
    }

    // Easter Egg...
    if(window==window.screen.root &&
       0<px && px<3 &&
       0<py && py<3 ){
      if(((e.getModifiers() & InputEvent.CTRL_MASK)!=0) &&
	 e.getKeyCode()==KeyEvent.VK_W){
	LogoImage.toggle();
      }
      else if(((e.getModifiers() & InputEvent.CTRL_MASK)!=0) &&
	      e.getKeyCode()==KeyEvent.VK_E){
	Window.printWindowTree(window.screen.root);
      }
//    else if(((e.getModifiers() & InputEvent.CTRL_MASK)!=0) &&
//	      e.getKeyCode()==KeyEvent.VK_I){
//	if(WeirdX.acontext!=null){
//	  acontext.showDocument(new URL("http://www.weirdx.org/"), "_blank");
//	}
//    }
//    else if(((e.getModifiers() & InputEvent.CTRL_MASK)!=0) &&
//	      e.getKeyCode()==KeyEvent.VK_R){
//	}
//    }
//    else if(((e.getModifiers() & InputEvent.CTRL_MASK)!=0) &&
//	      e.getKeyCode()==KeyEvent.VK_D){
//	}
//    }
//    else if(((e.getModifiers() & InputEvent.CTRL_MASK)!=0) &&
//	      e.getKeyCode()==KeyEvent.VK_X){
//	}
//    }
    }

    Window dest=Window.sprite.win;
    if(Window.focus.window!=null) dest=Window.focus.window;

    if(window.screen.windowmode!=0 &&
       dest==window.screen.root){
       if(Window.focus.window!=null) dest=Window.sprite.win;
       else dest=window;
    }

    if(dest.client==null) return;

    int kcode=Keymap.km.getCode(e);
    event.mkKeyPress(kcode, window.screen.rootId,
		     dest.id, 0,
		     Window.sprite.hot.x, 
		     Window.sprite.hot.y, 
		     Window.sprite.hot.x-window.x,
		     Window.sprite.hot.y-window.y,
                     Window.sprite.hot.state,
		     1);

    try{
      if(Window.grab!=null)
	Window.sendGrabbedEvent(event, false, 1);
      else
	Window.sendDeviceEvent(dest, event, Window.grab, null, 1);
    }
    catch(Exception ee){
    }

    kcode=e.getKeyCode();
    int state=Window.sprite.hot.state;
    if(kcode==KeyEvent.VK_CONTROL){
      if((state&4)==0) state|=4;
    } 
    else if(kcode==KeyEvent.VK_SHIFT){
      if((state&1)==0) state|=1;
    }
    else if(kcode==KeyEvent.VK_ALT){
      if((state&8)==0) state|=8;
    }
    else if(kcode==VK_ALT_GRAPH){
      if((state&ALT_GR_MASK)==0) state|=ALT_GR_MASK;
    } 
    // check for windoze ALT_GR (is equal to ALT+CONTROL), by Marcus.
    if((state&12)==12){
      state-=12;
      state|=ALT_GR_MASK;
    }

    Window.sprite.hot.state=state;
  }
  public void keyReleased(KeyEvent e){
    if ((window == null) || (!window.isMapped())) 
	  return;
    Client client=window.client;
    if(client==null) return;

    int kcode=Keymap.km.getCode(e);
    event.mkKeyRelease(kcode, window.screen.rootId, window.id, 0,
		       Window.sprite.hot.x, 
		       Window.sprite.hot.y, 
		       Window.sprite.hot.x-window.x,
		       Window.sprite.hot.y-window.y,
		       Window.sprite.hot.state,
//		       state,
		       1);

    try{
      if(Window.grab!=null)
	Window.sendGrabbedEvent(event, false, 1);
      else
	Window.sendDeviceEvent(window, event, Window.grab, null, 1);
    }
    catch(Exception ee){
    }

    kcode=e.getKeyCode();
    int state=Window.sprite.hot.state;
    if(kcode==KeyEvent.VK_CONTROL){
      if((state&4)!=0) state-=4;
      // check for windoze ALT_GR (is equal to ALT+CONTROL), by Marcus.
      if((state&ALT_GR_MASK)!=0) state-=ALT_GR_MASK;
    }
    else if(kcode==KeyEvent.VK_SHIFT && !clck_toggle){
      if((state&1)!=0) state-=1;
    }
    else if(kcode==KeyEvent.VK_ALT){
      if((state&8)!=0) state-=8;
    }
    else if(kcode==VK_ALT_GRAPH){
      if((state&ALT_GR_MASK)!=0) state-=ALT_GR_MASK;
    }
    Window.sprite.hot.state=state;
  }

  public void keyTyped(KeyEvent e){
    //System.out.println("event -> "+e);
  }

  public Image getImage(){
    if(offi==null) allocImage();
    return offi;
  }

  public Image getImage(GC gc, int x, int y, int w, int h){
    Image i=getImage();
    if(gc!=null && gc.clip_mask!=null && gc.clip_mask instanceof ClipPixmap){
      TransparentFilter tf=new TransparentFilter(0, 0, (Pixmap)(gc.clip_mask.getMask()));
      i=Toolkit.getDefaultToolkit().
	createImage(new FilteredImageSource(i.getSource(), tf));
    }
    return i;
  }

  public Graphics getGraphics2(){
    if(window.clss==InputOnly){
      return getGraphics();
    }
    if(!isVisible()){
      return null;
    }
//    Graphics g=getGraphics();
    if(offg==null) allocImage();
    Graphics g=offg;
    return g;
  }

  public final Graphics getGraphics(GC gc, int mask){
    if(!isVisible()){
      return null;
    }
    if(offg==null) allocImage();
    Graphics graphics=offg;
    if((mask&GC.GCSubwindowMode)!=0 && 
       (gc.attr&GC.IncludeInferiors)!=0){
      graphics=getGraphics();
      window.currentGC=null;
    }
    else{
      if(gc==window.currentGC &&
	 gc.time==window.gctime &&
	 (mask&~window.gmask)==0){
	//System.out.println("DDXWindow skip");
	return graphics;
      }
      window.gctime=gc.time;
      window.currentGC=gc;
      window.gmask=mask;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec!=null){
	graphics=offg;
      }

      if(rec==null || 
         (rec.x==0 && rec.y==0 && 
          rec.width==window.width && rec.height==window.height)){
//	return graphics;
      }
      else{
        graphics.setClip(rec.x, rec.y, rec.width, rec.height);
      }
    }

    if((mask&GC.GCFunction)!=0){
      Color color=window.getColormap().getColor(gc.fgPixel);
      if(gc.function==GC.GXxor){
	window.gmask&=~GC.GCFunction;
	graphics.setXORMode(new Color((color.getRGB() ^ graphics.getColor().getRGB())&0xffffff));
      }
      else if(gc.function==GC.GXinvert){
	window.gmask&=~GC.GCFunction;
	graphics.setXORMode(window.screen.defaultColormap.getColor(window.background.pixel));
      }
      else{
	graphics.setColor(color);
      }
    }

    if((mask&GC.GCFont)!=0){
      Font font=gc.font;
      graphics.setFont(font.getFont());
    }

    if((mask&GC.GCLineWidth)!=0 ||
       (mask&GC.GCLineStyle)!=0 ||
       (mask&GC.GCCapStyle)!=0 ||
       (mask&GC.GCJoinStyle)!=0 ){
    }
    return graphics;
  }

  public void drawImage(Clip clip, Image img, int dx, int dy, int w, int h) {
    if(clip==null || (clip instanceof ClipPixmap)){ 
      drawImage(img, dx, dy, w, h);
      return;
    }
    ClipRectangles rclip=(ClipRectangles)clip;
    java.awt.Rectangle rec=rclip.masks[0];
    if(offg==null) allocImage();
    java.awt.Shape tmp=offg.getClip();
    if(tmp==null) {
      tmp=new Rectangle(0, 0, window.width, window.height);
    }
    offg.clipRect(rec.x, rec.y, rec.width, rec.height);
    drawImage(img, dx, dy, w, h);
    for(int i=1; i<rclip.masks.length;i++){
      offg.setClip(tmp);
      rec=rclip.masks[i];
      offg.clipRect(rec.x, rec.y, rec.width, rec.height);
      drawImage(img, dx, dy, w, h);
    }
    offg.setClip(tmp);
  }

  public void drawImage(Image img, int dx, int dy, int w, int h) {
    if(offg==null) allocImage();
    offg.drawImage(img, dx, dy, w, h, ddxwindow);
  }
  public void fillImage(Image img, int w, int h) {
    for(int i=0; i<height; i+=h){
      for(int j=0; j<width; j+=w){
        offg.drawImage(img, j, i, ddxwindow);
      }
    }
/*
    offg.drawImage(img, 0, 0, ddxwindow);
    {
      int i=w;
      while(i<width){
	offg.copyArea(0, 0, i, h, i, 0);
	i*=2;
      }
    }
    {
      int j=h;
      while(j<height){
	offg.copyArea(0, 0, width, j, 0, j);
	j*=2;
      }
    }
*/
  }

  public void fillImage(Image img, int w, int h, int offx, int offy) {
    if(offx<0){ while(offx<0){offx+=w;} }
    if(offy<0){ while(offy<0){offy+=h;} }
    if(w<offx){ while(w<offx){offx-=w;} }
    if(h<offy){ while(h<offy){offy-=h;} }
    if(offx==0 && offy==0){
      fillImage(img, w, h);
      return;
    }
    offg.drawImage(img, -offx, -offy, ddxwindow);
    if((-offx+w)<width){
      int i=-offx+w;
      while(i<width){
        offg.drawImage(img, i, -offy, ddxwindow);
	i+=w;
      }
    }
    if((-offy+h)<height){
      int i=-offy+h;
      while(i<height){
        offg.drawImage(img, -offx, i, ddxwindow);
	i+=h;
      }
    }

    offx=(-offx+w); offy=(-offy+h);	
    if(width<=offx && height<=offy) return;

    for(int i=offy; i<height; i+=h){
      for(int j=offx; j<width; j+=w){
        offg.drawImage(img, j, i, ddxwindow);
      }
    }
/*
    offg.drawImage(img, offx, offy, ddxwindow);
    {
      int i=w;
      while(offx+i<width){
        offg.copyArea(offx, offy, i, h, offx+i, offy);
        i*=2;
      }
    }
    {
      int j=h;
      while(offy+j<height){
	offg.copyArea(offx, offy, width, j, offx, offy+j);
	j*=2;
      }
    }
*/
  }


  public void copyArea(Window dst, GC gc, 
		       int srcx, int srcy, int width, int height, 
		       int destx, int desty){
    Graphics g=dst.getGraphics();
    if(g==null) return;

    if(window==dst){ 
      copyArea(srcx, srcy, width, height, destx-srcx, desty-srcy); 
      dst.draw(destx, desty, width, height);
      return;
    }

    Image img=window.getImage(gc, srcx, srcy, width, height);
    if(srcx==0 && srcy==0 && width==window.width && height==window.height){
      dst.ddxwindow.drawImage(gc.clip_mask, img, destx, desty, width, height);
    }
    else{
      java.awt.Shape tmp=g.getClip();
      g.clipRect(destx, desty, width, height);
      dst.ddxwindow.drawImage(gc.clip_mask, img, destx-srcx, desty-srcy, 
                              window.width, window.height);
      if(tmp==null){ g.setClip(0, 0, dst.width, dst.height);}
      else{g.setClip(tmp);}
    }
    dst.draw(destx, desty, width, height);
    if(img!=window.getImage()){
      img.flush();
    }
  }

  public void copyArea(int sx, int sy, int w, int h, int dx, int dy){
    if(offg==null) allocImage();
    if((dx<=0 || w<=dx) && (dy<=0 || h<=dy)){
      offg.copyArea(sx, sy, w, h, dx, dy);
    }
    else{
      if(0<dy && dy<h){
	int ssy=sy+h-dy;
	while(true){
	  if(ssy<sy) break;
	  offg.copyArea(sx, ssy, w, dy, dx, dy);
	  ssy-=dy;
	}
	ssy+=dy;
	if(sy<ssy){ offg.copyArea(sx, sy, w, ssy-sy, dx, dy); }
      }
      else if(0<dx && dx<w){
	int ssx=sx+w-dx;
	while(true){
	  if(ssx<sx) break;
	  offg.copyArea(ssx, sy, dx, h, dx, dy);
	  ssx-=dx;
	}
	ssx+=dx;
	if(sx<ssx){ offg.copyArea(sx, sy, ssx-sx, h, dx, dy); }
      }
    }
  }

  public void delete() throws IOException{
    if(offi!=null){
      offi.flush();
      offi=null;
    }
    if(offg!=null){
      offg.dispose();
      offg=null;
    }
    window=null;
    Container tmp=getParent();
    if(tmp!=null){ tmp.remove(this); }
  }

  public void restoreClip(){
    if(offg!=null) offg.setClip(0, 0, window.width, window.height);	
  }

  public Window getWindow(){
    return window;
  }
}
