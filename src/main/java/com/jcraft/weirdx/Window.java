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

import java.awt.*;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
                       
@SuppressWarnings("unused")
class Window extends Drawable {
	private static Log LOG = LogFactory.getLog(Window.class);
	
  static Object LOCK=Window.class;

  private static final int CWBackPixmap=(1<<0);
  private static final int CWBackPixel=(1<<1);
  private static final int CWBorderPixmap=(1<<2);
  private static final int CWBorderPixel=(1<<3);
  private static final int CWBitGravity=(1<<4);
  private static final int CWWinGravity=(1<<5);
  private static final int CWBackingStore=(1<<6);
  private static final int CWBackingPlanes=(1<<7);
  private static final int CWBackingPixel=(1<<8);
  private static final int CWOverrideRedirect=(1<<9);
  private static final int CWSaveUnder=(1<<10);
  private static final int CWEventMask=(1<<11);
  private static final int CWDontPropagate=(1<<12);
  private static final int CWColormap=(1<<13);
  private static final int CWCursor=(1<<14);

  private static final int CWX=(1<<0);
  private static final int CWY=(1<<1);
  private static final int CWWidth=(1<<2);
  private static final int CWHeight=(1<<3);
  private static final int CWBorderWidth=(1<<4);
  private static final int CWSibling=(1<<5);
  private static final int CWStackMode=(1<<6);

  private static final int ChangeMask=(CWX | CWY | CWWidth | CWHeight);
  private static final int IllegalInputOnlyConfigureMask=CWBorderWidth;

  private static final int CopyFromParent=0;
  private static final int InputOutput=1;
  private static final int InputOnly=2;

  private static final int NotifyNormal=0;
  private static final int NotifyGrab=1;
  private static final int NotifyUngrab=2;
  private static final int NotifyWhileGrabbed=3;

  private static final int NotifyAncestor=0;
  private static final int NotifyVirtual=1;
  private static final int NotifyInferior=2;
  private static final int NotifyNonlinear=3;
  private static final int NotifyNonlinearVirtual=4;
  private static final int NotifyPointer=5;
  private static final int NotifyPointerRoot=6;
  private static final int NotifyDetailNone=7;

  private static final int RevertToNone=0;
  private static final int RevertToPointerRoot=1;
  private static final int RevertToParent=2;

  private static final int NoneWin=0;
  private static final int PointerRootWin=1;
  private static final int PointerRoot=1;

  private static final int FollowKeyboard=3;
  private static final int FollowKeyboardWin=3;

  private static final int AsyncPointer=0;
  private static final int SyncPointer=1;
  private static final int ReplayPointer=2;
  private static final int AsyncKeyboard=3;
  private static final int SyncKeyboard=4;
  private static final int ReplayKeyboard=5;
  private static final int AsyncBoth=6;
  private static final int SyncBoth=7;

  private static final int GrabModeSync=0;
  private static final int GrabModeAsync=1;

  private static final int GrabSuccess=0;
  private static final int AlreadyGrabbed=1;
  private static final int GrabInvalidTime=2;
  private static final int GrabNotViewable=3;
  private static final int GrabFrozen=4;

  static int deltaSaveUndersViewable=0;
  static int defaultBackingStore=0;

  static int AnyModifier=(1<<15);
  static int AnyKey=0;

  private static final int Above=0;
  private static final int Below=1;
  private static final int TopIf=2;
  private static final int BottomIf=3;
  private static final int Opposite=4;

  private static final int Restack=0;
  private static final int Move=1;
  private static final int Resize=2;
  private static final int Reborder=3;

//  private static final int NOT_GRABBED=0;
//  private static final int THAWED=1;
//  private static final int THAWED_BOTH=2;
//  private static final int FREEZE_NEXT_EVENT=3;
//  private static final int FREEZE_BOTH_NEXT_EVENT=4;
//  private static final int FROZEN=5;
//  private static final int FROZEN_NO_EVENT=5;
//  private static final int FROZEN_WITH_EVENT=6;
//  private static final int THAW_OTHERS=7;

  private static final int backgroundStateOffset=0;
  private static final int backgroundState=(3<<0);
  private static final int ParentRelative=(1<<backgroundStateOffset);
  private static final int BackgroundPixel=(2<<backgroundStateOffset);
  private static final int BackgroundPixmap=(3<<backgroundStateOffset);
  private static final int borderIsPixelOffset=2;
  private static final int borderIsPixel=(1<<borderIsPixelOffset);
  private static final int cursorIsNoneOffset=3;
  private static final int cursorIsNone=(1<<cursorIsNoneOffset);
  private static final int backingStoreOffset=4;
  private static final int backingStore=(3<<backingStoreOffset);
  private static final int saveUnderOffset=6;
  private static final int saveUnder=(1<<saveUnderOffset);
  private static final int DIXsaveUnderOffset=7;
  private static final int DIXsaveUnder=(1<<DIXsaveUnderOffset);
  private static final int bitGravityOffset=8;
  private static final int bitGravity=(15<<bitGravityOffset);
  private static final int ForgetGravity=0;
  private static final int winGravityOffset=12;
  private static final int winGravity=(15<<winGravityOffset);
  private static final int ForegetGravity=0;
  private static final int NorthWestGravity=1;
  private static final int NorthGravity=2;
  private static final int NorthEastGravity=3;
  private static final int WestGravity=4;
  private static final int CenterGravity=5;
  private static final int EastGravity=6;
  private static final int SouthWestGravity=7;
  private static final int SouthGravity=8;
  private static final int SouthEastGravity=9;
  private static final int StaticGravity=10;
  private static final int UnmapGravity=0;
  private static final int overrideRedirectOffset=16;
  private static final int overrideRedirect=(1<<overrideRedirectOffset);
  private static final int visibilityOffset=17;
  private static final int visibility=(3<<visibilityOffset);
  private static final int VisibilityUnobscured=0;
  private static final int VisibilityPartiallyObscured=1;
  private static final int VisibilityFullyObscured=2;
  private static final int VisibilityNotViewable=3;
  private static final int mappedOffset=19;
  private static final int mapped=(1<<mappedOffset);
  private static final int realizedOffset=20;
  private static final int realized=(1<<realizedOffset);
  private static final int viewableOffset=21;
  private static final int viewable=(1<<viewableOffset);
  private static final int dontPropagateOffset=22;
  private static final int dontPropagate=(7<<dontPropagateOffset);
  private static final int forcedBSOffset=25;
  private static final int forcedBS=(1<<forcedBSOffset);

  private static final int DBE_FRONT_BUFFER=1;
  private static final int DBE_BACK_BUFFER=0;
  private static final int dstBufferOffset=26;
  private static final int dstBuffer=(1<<dstBufferOffset);
  private static final int srcBufferOffset=27;
  private static final int srcBuffer=(1<<srcBufferOffset);

  private static final int PlaceOnTop=0;
  private static final int PlaceOnBottom=1;

  private static final int RaiseLowest=0;
  private static final int LowerHighest=1;

  static final Focus focus=new Focus();
  static Sprite sprite=new Sprite();
  static int spriteTraceGood=1;
  static Window[] spriteTrace=new Window[10];
  static Point gpoint=new Point();
  static Grab grab=null;

  static Class<?> dDXWindow = null;
  static{
    try{ 
    	dDXWindow = Class.forName("com.jcraft.weirdx.DDXWindowImp");
    }
    catch(Exception e){LOG.error(e);}
  }
  static void installDDXWindow(String name){
    Class<?> c=null;
    try{if(name.startsWith("com.jcraft.weirdx."))c=Class.forName(name);}
    catch(Exception e){LOG.error(e);}
    if(c!=null) dDXWindow=c;
  }

  Client client;
  Window parent;
  Window nextSib;
  Window prevSib;
  Window firstChild;
  Window lastChild;

  Point origin=new Point();
  int borderWidth;
  int deliverableEvents;
  int eventMask;
  Pix background=new Pix();
  Pix border=new Pix();
  WindowOpt optional;
  int attr;
  DDXWindow ddxwindow;

  int frame_x=-1;
  int frame_y=-1;
  int frame_width=-1;
  int frame_height=-1;

  Window(int id){
    super(id, RT_WINDOW);
  }

  Window(int wid, Window prnt,
	 int x, int y, int width, int height, int bwidth,
	 int clss, byte depth, Client client, int visual, int msk)
    throws IOException {
    this(wid);
    WindowOpt opt;
    this.client=client;
    this.parent=prnt;
    screen=prnt.screen;

    if(clss==CopyFromParent) clss=prnt.clss;
    this.clss=clss;

    if((clss!=InputOutput) && (clss!=InputOnly)){
      client.errorValue=clss;
      client.errorReason=2; // BadValue;
      return;
    }

    if((clss!=InputOnly) && (prnt.clss==InputOnly)){
      client.errorValue=clss;
      client.errorReason=8; // BadMatch;
      return;
    }
    if((clss==InputOnly) && ((bwidth!=0) || (depth!=0))){
      client.errorValue=0;
      client.errorReason=8; // BadMatch;
      return;
    }

    if((clss==InputOutput) && (depth==0)){
      depth=prnt.depth;
    }

    opt=prnt.optional;
    if(opt==null){
      opt=prnt.findOptional().optional;
    }
    if(visual==CopyFromParent){
      visual=opt.visual;
    }
    if((visual!=opt.visual) || (depth!=prnt.depth)){
      boolean foo=false;
      Depth pdepth;
      for(int i=0; i<screen.depth.length; i++){
	pdepth=screen.depth[i];
	if(depth==pdepth.depth || depth==0){
          if(pdepth.visual!=null){
	    for(int j=0; j<pdepth.visual.length; j++){
	      if(visual==pdepth.visual[j].id){
		foo=true;
		break;
	      }
	    }
	  }
	}
      }
      if(!foo){
	client.errorValue=0;
	client.errorReason=8; // BadMatch;
	return;
      }
    }
    if(((msk & (CWBorderPixmap | CWBorderPixel))==0) &&
	(clss !=InputOnly) &&
	(depth !=prnt.depth)){
      client.errorValue=0;
      client.errorReason=8; // BadMatch;
      return;
    }

    this.depth=depth;
    if(depth==prnt.depth){
      this.bitsPerPixel=prnt.bitsPerPixel;
    }
    else{
      int ii=0;
      while(ii< Format.format.length){
	if(Format.format[ii].depth==screen.rootDepth)break;
	ii++;
      }
      if(ii==Format.format.length){
	// ???
	ii=0;
      }
      this.bitsPerPixel=Format.format[ii].bpp;
    }

    this.type=prnt.type;
    if(clss==InputOnly) this.type=UNDRAWABLE_WINDOW;

    setDefault();

    if(visual!=opt.visual){
      makeOptional();
      this.optional.visual=visual;
      this.optional.colormap=screen.defaultColormap;
    }
    this.borderWidth=bwidth;
    attr&=~backgroundState;
    attr&=~borderIsPixel;
    attr|=(prnt.attr&borderIsPixel);

    this.border=prnt.border.dup();

    if((attr&borderIsPixel)==0){
      this.border.pixmap.ref();
    }
    this.origin.x=(short)(x+bwidth);
    this.origin.y=(short)(y+bwidth);
    this.width=width;
    this.height=height;
    this.x=(short)(prnt.x+x+bwidth);
    this.y=(short)(prnt.y+y+bwidth);

    synchronized(LOCK){
      this.nextSib=prnt.firstChild;
      if(prnt.firstChild!=null) prnt.firstChild.prevSib=this;
      else  prnt.lastChild=this;
      prnt.firstChild=this;
    }

    if((msk&CWEventMask)==0){
      recalculateDeliverableEvents();
    }

    msk&=0x7fff;
    if(msk!=0){
      changeAttr(client, msk);
    }

    if(client.errorReason!=0){
      this.delete();
      return;
    }

    if((msk&CWBackingStore)!=0 &&
       (defaultBackingStore!=0)){
      attr&=~backingStore;
      attr|=(defaultBackingStore<<backingStoreOffset);
      attr|=forcedBS;
    }

    //  ddxwindow=new DDXWindow();
    try{ ddxwindow=(DDXWindow)dDXWindow.newInstance();}
    catch(Exception e){ LOG.error(e); /*ddxwindow=new DDXWindow();*/ }
    ddxwindow.init(this);

    try{
      if(screen.windowmode!=WeirdX.InBrowser && prnt==screen.root){  
        final java.awt.Window frame=getFrame();
	/*
	if(frame instanceof JFrame){                               
	  ((JFrame)frame).setJMenuBar(null);                       
	  ((JFrame)frame).getContentPane().setLayout(null);        
  	  ((JFrame)frame).setResizable(false);                                 
	}                                                          
	else*/ if(frame instanceof Frame){
	  ((Frame)frame).setMenuBar(null);                                  
  	  ((Frame)frame).setResizable(true);
	}                                                          

	ddxwindow.setLocation(0, 0);                               

//	if(frame instanceof JFrame){                               
//	  ((JFrame)frame).getContentPane().add((java.awt.Component)ddxwindow);
//	}                                                          
//	else{                                                      
	  frame.add((java.awt.Component)ddxwindow);
//	}                                                          

	frame.pack();                                            
	Insets insets=frame.getInsets();                         
	/*
	frame.setSize(this.width+this.borderWidth*2+             
		      insets.left+insets.right,                  
		      this.height+this.borderWidth*2+            
		      insets.bottom+insets.top);                 
	*/
	frame.setLocation(this.origin.x-this.borderWidth+        
			  parent.borderWidth,                    
			  this.origin.y-this.borderWidth+        
			  parent.borderWidth);                   

        if(frame instanceof Frame){
          addWindowListener((java.awt.Frame)frame);
          addComponentListener((java.awt.Frame)frame);
	}
      }                                                            
      else {             
	ddxwindow.setLocation(origin.x-borderWidth+parent.borderWidth,
			      origin.y-borderWidth+parent.borderWidth);
	prnt.ddxwindow.add((java.awt.Component)ddxwindow, 0);
      }                                                            
      if((attr&cursorIsNone)==0){
	Cursor cur=getCursor();
	if(cur!=null){
	  ddxwindow.setCursor(cur.cursor);
	}
      }
      if(bwidth > 0){
	ddxwindow.setBorderPixmap(border.pixmap);
      }
    }
    catch(Exception ee){
      //System.out.println("error: Window?? "+ee);
    }

    if(prnt.subSend()){
      client.cevent.mkCreateNotify(prnt.id, id, x, y, width, height,
				   borderWidth,
				   (attr&overrideRedirect)!=0?0:1);
      prnt.sendEvent(client.cevent, 1, null);
    }
  }

  private final void changeAttr(Client c, int vmask) throws IOException{
    int foo;
    boolean borderRelative=false;
    int index=0;
    int mask=vmask;
    IO io=c.client;

    while(mask!=0 && c.length!=0){
      index=lowbit(mask);
      mask&=~index;
      c.length--;
      switch(index){
      case CWBackPixmap:
	foo=io.readInt();
	if((attr&backgroundState)==ParentRelative) borderRelative=true;
	if(foo==0){
	  if((attr&backgroundState)==BackgroundPixmap){
	    if(background.pixmap==null){
	      //System.err.println("BackPixmap: error!!");
	    }
	    else{
	      try{background.pixmap.delete();}catch(Exception e){}
	      background.pixmap=null;
	    }
	  }
	  attr=attr&(~backgroundState);
	} 
	else if(foo==ParentRelative){
	  if(parent!=null && depth!=parent.depth){
	  }
	  if((attr&backgroundState)==BackgroundPixmap){
	    if(background.pixmap==null){
	      //System.err.println("BackPixmap: error!!");
	    }
	    else{
	      try{background.pixmap.delete();}catch(Exception e){}
	      background.pixmap=null;
	    }
	  }
	  if(parent==null){
	  }
	  else{
	    attr=(attr&~backgroundState)|(backgroundState&ParentRelative);
	  }
	  borderRelative=true;
	}
	else{
	  Pixmap pixmap=(Pixmap)Resource.lookupIDByType(foo, 
							Resource.RT_PIXMAP);
	  if(pixmap!=null){
	    if(pixmap.depth!=depth ||
	       pixmap.screen!=screen){
	      //System.err.println("error!!");
	    }
	    if((attr&backgroundState)==BackgroundPixmap){
	      if(background.pixmap==null){
		//System.err.println("BackPixmap: error!!");
	      }
	      else{
		try{background.pixmap.delete();}catch(Exception e){}
		background.pixmap=null;
	      }
	    }
	    attr=(attr&~backgroundState)|(backgroundState&BackgroundPixmap);
	    background.pixmap=pixmap;
	    background.img=pixmap.getImage(this);

	    pixmap.ref();
	  }
	  else{
	    //System.err.println("error!!");
	  }
	}
	break;
      case CWBackPixel:
	foo=io.readInt();
	if((attr&backgroundState)==ParentRelative){
	  borderRelative=true;
	}
	if((attr&backgroundState)==BackgroundPixmap){
	  if(background.pixmap==null){
	    //System.err.println("BackPixel: error!!");
	  }
	  else{
	    try{background.pixmap.delete();}catch(Exception e){}
	    background.pixmap=null;
	  }
	}
	attr&=~backgroundState;
	attr|=BackgroundPixel;
	background.pixel=foo;
	break;
      case CWBorderPixmap:
	foo=io.readInt();
	if(foo==CopyFromParent){
	  if(parent==null || depth!=parent.depth){
	    //System.err.println("err!!");
	  }
	  if((attr&borderIsPixel)==0){
	    if(border.pixmap==null){
	      //System.err.println("BorderPixmap: error!!");
	    }
	    else{
	      try{border.pixmap.delete();}catch(Exception e){}
	      border.pixmap=null;
	    }
	  }

	  border=parent.border.dup();
	  attr&=~borderIsPixel;
	  attr|=(parent.attr&borderIsPixel);
	  if((attr&borderIsPixel)!=0){
	  }
	  else{
	    if(border.pixmap==null){
	      //System.err.println("BorderPixmap: error!!!");
	    }
	    else{
	      border.pixmap.ref();
	    }
	  }
	}
	else{
	  Pixmap pixmap=(Pixmap)Resource.lookupIDByType(foo, Resource.RT_PIXMAP);
	  if(pixmap!=null){
	    if((pixmap.depth!=depth) || (pixmap.screen!=screen)){
	      //System.err.println("error!!");
	    }
	    if((attr&borderIsPixel)==0){
	      if(border.pixmap==null){
		//System.err.println("BorderPixmap: error!!");
	      }
	      else{
		try{border.pixmap.delete();}catch(Exception e){}
		border.pixmap=null;
	      }
	    }
	    attr&=~borderIsPixel;
	    border.pixmap=pixmap;
	    pixmap.ref();
	  }
	  else{
	    //System.err.println("error!!");
	  }
	}
	break;
      case CWBorderPixel:
	foo=io.readInt();
	if((attr&borderIsPixel)==0){
	  if(border.pixmap==null){
	    //System.err.println("BorderPixmap: error!!");
	  }
	  else{
	    try{border.pixmap.delete();}catch(Exception e){}
	    border.pixmap=null;
	  }
	}
	attr|=borderIsPixel;
	border.pixel=foo;
	break;
      case CWBitGravity:
	foo=io.readInt();
	foo&=0xff;

	if(foo>StaticGravity){
	  //System.err.println("error!!");
	}
	attr&=~bitGravity;
	attr|=(foo<<bitGravityOffset);
	
	break;
      case CWWinGravity:
	foo=io.readInt();
	foo&=0xff;

	if(foo>StaticGravity){
	  //System.err.println("error!!");
	}
	attr&=~winGravity;
	attr|=(foo<<winGravityOffset);

	break;
      case CWBackingStore:
	foo=io.readInt();
	foo&=0xff;

	if(foo!=0 && foo!=1 && foo!=2){
	  //System.err.println("error!!");    
	}
	attr&=~backingStore;
	attr|=(foo<<backingStoreOffset);
	attr&=~forcedBS;
	break;
      case CWBackingPlanes:
	foo=io.readInt();
	if(optional!=null || foo!=~0){
	  makeOptional();
	  optional.backingBitPlanes=foo;
	}
	break;
      case CWBackingPixel:
	foo=io.readInt();
	if(optional!=null || foo!=0){
	  makeOptional();
	  optional.backingPixel=foo;
	}
	break;
      case CWOverrideRedirect:
	foo=io.readInt();
	foo&=0xff;
	if((foo!=0) && (foo!=1)){
	  //System.err.println("error!");
	}
	//if(foo==1){ attr|=overrideRedirect;}
	attr&=~overrideRedirect;
	attr|=(foo<<overrideRedirectOffset);
	break;
      case CWSaveUnder:
	foo=io.readInt();
	foo&=0xff;

	if((foo!=0) && (foo!=1)){
	  //System.err.println("error !!");
	}
	if(parent!=null && ((attr&saveUnder) !=(foo<<saveUnderOffset)) &&
	   (attr&viewable)!=0){
	  if((attr&saveUnder)!=0)  deltaSaveUndersViewable--;
	  else deltaSaveUndersViewable++;
	  attr&=~saveUnder;
	  attr|=(foo<<saveUnderOffset);
	  if(firstChild!=null){
	  }
	  else{
	  }
	}
	else{
	  attr&=~saveUnder;
	  attr|=(foo<<saveUnderOffset);
	}
	break;
      case CWEventMask:
	foo=io.readInt();
	setEventMask(c, foo);
	break;
      case CWDontPropagate:
	foo=io.readInt();
	eventSuppress(foo);
	break;
      case CWColormap:
	foo=io.readInt();
        if(foo==0){  // CopyFromParent
  	  if(parent!=null &&
	     (optional==null || 
              optional.visual==(parent.optional!=null ?
   			        parent.optional.visual :
				parent.findOptional().optional.visual))){
            foo=parent.getColormap().id;
	  }
	}
        if(foo==0){
          c.errorReason=8; //BadMatch
	  c.errorValue=foo;
	  return;
	}
	Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
        if(cmap==null){
          c.errorReason=12; // BadColor
	  c.errorValue=foo;
	  return;
	}
	makeOptional();
	optional.colormap=cmap;
	c.cevent.mkColormapNotify(id, foo, 1, 1);
	sendEvent(c.cevent, 1, null);
	break;
      case CWCursor:
	foo=io.readInt();
	Cursor cur=null, old=null;
	if(foo==0){
	  if(this==screen.root){ cur=Cursor.rootCursor; }
	  else{ cur=null; }
	}
	else{
	  cur=(Cursor)Resource.lookupIDByType(foo, Resource.RT_CURSOR);
	  if(cur==null){
	    c.errorReason=6; //BadCursor
	    c.errorValue=foo;
	    return;
	  }
	}
	if(cur!=getCursor()){
	  if(cur==null){
	    attr|=cursorIsNone;
	    if(optional!=null){
	      old=optional.cursor;
	      optional.cursor=null;
	    }
	  }
	  else{
	    makeOptional();
  	    old=optional.cursor;
	    optional.cursor=cur;
	    attr&=~cursorIsNone;
            if(ddxwindow!=null){
  	      ddxwindow.setCursor(cur.cursor);
	    }
	  }
        }
	break;
      default:
	c.length++;
	c.errorValue=vmask;
	c.errorReason=2; // BadValue;
      }
      //try{HexStream.err.hexInt(attr); System.out.println(" <-attr");}
      //catch(Exception e){}
      if(c.errorReason!=0){
	return;
      }
    }

    if(c.length!=0){
      c.errorValue=vmask;
      c.errorReason=2; // BadValue;
    }
    return;
  }

  private int eventSuppress(int mask){
    int i=0, free;
    if((attr&dontPropagate)!=0){
      DontPropagate.refc[(attr&dontPropagate)>>dontPropagateOffset]--;
    }
    if(mask!=0){
      i=DontPropagate.store(mask);
    }
    if(i!=0 || mask==0){
      attr&=~dontPropagate;
      attr|=(i<<dontPropagateOffset);
      if(i!=0){
	DontPropagate.refc[i]++;
      }
      if(optional!=null){
	optional.dontPropagateMask=mask;
      }
    }
    else {
      makeOptional();
      attr&=~dontPropagate;
      optional.dontPropagateMask=mask;
    }
    recalculateDeliverableEvents();
    return 0;
  }

  static final void reqCirculateWindow(Client c) throws IOException{
    int n, foo;
    IO io=c.client;

    int direction=c.data;
    foo=io.readInt();

    c.length-=2;

    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }
    if(direction !=RaiseLowest && direction !=LowerHighest){
      c.errorValue=direction;
      c.errorReason=2; // BadValue
      return;
    }

    synchronized(LOCK){
      Window win=null;
      Window first=w.firstChild;
      if(direction==RaiseLowest){
	win=w.lastChild;
	while((win !=null) && !(win.isMapped())){ win=win.prevSib; }
	if(win==null) return;
      }
      else {
	win=first; 
	while((win!=null) && !(win.isMapped())){ win=win.nextSib; }
	if(win==null) return;
      }
      c.cevent.mkCirculateRequest(w.id, win.id,
				  (direction==RaiseLowest) ? 
				  PlaceOnTop : PlaceOnBottom);
      if(w.redirectSend()){
	if(w.sendEvent(c.cevent, 1, Event.SubstructureRedirectMask, c)==1)
	  return;
      }
      c.cevent.mkCirculateNotify(win.id, w.id, w.id, 
				 (direction==RaiseLowest) ? 
				 PlaceOnTop : PlaceOnBottom);
      win.sendEvent(c.cevent, 1, null);
      win.reflectStackChange((direction==RaiseLowest) ? first : null);
    }
    return;
  }

  static final void reqAllowEvents(Client c) throws IOException{
    int foo, mode;
    IO io=c.client;
    mode=c.data;
    foo=io.readInt();
    switch(mode){
    case AsyncPointer:
      break;
    case SyncPointer:
      break;
    case ReplayPointer:
      break;
    case AsyncKeyboard:
      break;
    case SyncKeyboard:
      break;
    case ReplayKeyboard:
      break;
    case AsyncBoth:
      break;
    case SyncBoth:
      break;
    default:
    }
    Window.sprite.hot.state=0;
  }

  static final void reqGetMotionEvents(Client c) throws IOException{
    int foo, n;
    IO io=c.client;

    foo=io.readInt();
    Window w=c.lookupWindow(foo);
    foo=io.readInt();
    foo=io.readInt();
    c.length-=4;
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeInt(0);
      io.writePad(20);
      io.flush();
    }
  }
  static final void reqTranslateCoordinates(Client c) throws IOException{
    int foo;
    IO io=c.client;
    foo=io.readInt();
    Window srcw=c.lookupWindow(foo);
    if(srcw==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow
    }
    foo=io.readInt();
    Window dstw=c.lookupWindow(foo);
    if(dstw==null && c.errorReason==0){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow      
    }
    int x=io.readShort();
    int y=io.readShort(); 
    c.length-=4;
    if(c.errorReason!=0){
      return;
    }

    int gx=x+srcw.x;
    int gy=y+srcw.y;

    int child=0;

    synchronized(LOCK){
      Window win=dstw.firstChild;
      while (win!=null){
	if(((win.attr&mapped)!=0) && win.contains(gx, gy)){
	  child=win.id;
	  win=null;
	}
	else{
	  win=win.nextSib;
	}
      }
    }

    x=gx-dstw.x;
    y=gy-dstw.y;

    synchronized(io){
      io.writeByte(1);
      io.writeByte(1);  // same screen
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeInt(child);
      io.writeShort(x);
      io.writeShort(y);
      io.writePad(16);
      io.flush();
    }
  }

  static final void reqGetScreenSaver(Client c) throws IOException{
    int foo, n;
    IO io=c.client;

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeShort(600);
      io.writeShort(600);
      io.writeByte((byte)1);
      io.writeByte((byte)1);
      io.writePad(18);
      io.flush();
    }
  }

  static final void reqQueryBestSize(Client c) throws IOException{
    int foo, clss;
    IO io=c.client;
    clss=c.data;
    foo=io.readInt();
    c.length-=2;
    Drawable d=c.lookupDrawable(foo);
    if((clss !=0 /*CursorShape*/) && 
       (clss !=1 /*TileShape*/) && 
       (clss !=2 /*StippleShape*/)){
      c.errorValue=clss;
      c.errorReason=2; // BadValue;
      return;
    }
    if(d==null){
      c.errorValue=foo;
      c.errorReason=9; // BadDrawable;
      return;
    }
    foo=io.readShort();
    foo=io.readShort();

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeShort(32);
      io.writeShort(32);
      io.writePad(20);
      io.flush();
    }
  }

  static final void reqDestroySubwindows(Client c) throws IOException{
    int foo;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    Window w=c.lookupWindow(foo);

    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }
    synchronized(LOCK){
      w.unmapSubwindows(c);
      while(w.lastChild!=null){
	Resource.freeResource(w.lastChild.id, Resource.RT_NONE);
      }
    }
  }

  void makeBackgroundTile(int xx, int yy, int w, int h){
    Window win=this;
    int i=(attr&backgroundState);
    if(i==ParentRelative){
      win=parent;
      if(win==null) return;
      i=(win.attr&backgroundState);
    }

    if(i==BackgroundPixmap && win.background.pixmap!=null){
      Image img=win.background.img;
      if(img==null) return;
      if(!win.ddxwindow.isVisible()) return;
      Graphics g=null;
      java.awt.Shape tmp=null;
      if(xx!=0 || yy!=0 || w!=width || h!=height){
	g=getGraphics();
	tmp=g.getClip();
	g.clipRect(xx, yy, w, h);
      }
      if(win==this){
        ddxwindow.fillImage(img, 
			    win.background.pixmap.width, 
			    win.background.pixmap.height);
      }
      else{	
        ddxwindow.fillImage(img, 
                            win.background.pixmap.width, 
                            win.background.pixmap.height,
                            origin.x-borderWidth,
                            origin.y-borderWidth
                            );
      }
      if(g!=null){
	if(tmp==null){ g.setClip(0, 0, width, height); }
        else{g.setClip(tmp);}
      }
    }
    else if(i==BackgroundPixel){
      Colormap cmap=win.getColormap();
      ddxwindow.setBackground(cmap.getColor(win.background.pixel),
                              xx, yy, w, h);
    }
  }

  private void free(){
    SaveSet.delete(this);
    Selection.delete(this);
    try{deleteEvent(true);}
    catch(Exception e){
      //System.out.println(e);
    }

    try{deleteProperties();}
    catch(Exception e){
      //System.out.println(e);
    }

    origin=null;
    if((attr&backgroundState)==BackgroundPixmap){
      if(background.pixmap==null){
	//System.err.println("free: error!!");
      }
      else{
	try{background.pixmap.delete();}catch(Exception e){}
	background.pixmap=null;
      }
    }
    background=null;
    if((attr&borderIsPixel)==0){
      if(border.pixmap==null){
	//System.err.println("free: error!!!");
      }
      else{
	try{border.pixmap.delete();}catch(Exception e){}
	border.pixmap=null;
      }
    }
    border=null;
    optional=null;

    try{ddxwindow.delete();}
    catch(Exception e){}

    if(hasFrame()){
      delFrame();
    }
  }

  @SuppressWarnings("static-access")
private void deleteEvent(boolean freeResources) throws IOException {
    Grab passive;
    if(Window.grab!=null && Window.grab.window==this){
      Window.grab.deactivatePointerGrab();
    }
    if((id==focus.win) && (parent !=null)){
      int focusEventMode=NotifyNormal;
      switch (focus.revert){
      case RevertToNone:
	doFocusEvents(null, id, 0, focusEventMode);
	focus.win=0;
        focus.window=null;
	focus.traceGood=0;
	break;
      case RevertToParent:
	Window win=this;
	do{
	  win=win.parent;
	  focus.traceGood--;
	}
	while (win!=null && !win.isRealized());

	int p=(win==null) ? 0 : win.id;
	doFocusEvents(null, id, p, focusEventMode);
	focus.win=p;
	focus.window=win;
	focus.revert=RevertToNone;
	break;
      case RevertToPointerRoot:
	doFocusEvents(null, id, PointerRootWin, focusEventMode);
	focus.win=PointerRootWin;
        focus.window=null;
	focus.traceGood=0;
	break;
      }
    }

    if(freeResources){
      if((attr&dontPropagate)!=0){
	DontPropagate.refc[(attr&dontPropagate)>>dontPropagateOffset]--;
      }
      OtherClients oc;
      while ((oc=getOtherClients())!=null ){
	Resource.freeResource(oc.id, Resource.RT_NONE);
      }
      while ((passive=getPassiveGrabs())!=null){
	Resource.freeResource(passive.id, Resource.RT_NONE);
      }
    }
  }

  private void deleteProperties() throws IOException{

    Property pProp, pNextProp;
    pProp=getProperty();
    while (pProp!=null){
      client.cevent.mkPropertyNotify(id, 
				     pProp.propertyName,
				     (int)System.currentTimeMillis(),
				     1 //Property.PropertyDelete
				     );
      sendEvent(client.cevent, 1, null);
      pNextProp=pProp.next;
      pProp.data=null;
      pProp=pNextProp;
    }
  }

  private void crushTree() throws IOException {
    Window child, sib, cparent;
    if((child=firstChild)==null) return;
    while (true){
      if(child.firstChild!=null){
	child=child.firstChild;
	continue;
      }
      while (true){
	cparent=child.parent;
	if(child.substrSend()){
	  Event event=new Event();
	  event.mkDestroyNotify(child.id, child.id);
	  try{ child.sendEvent(event, 1, null);}
	  catch(Exception e){
	    //System.out.println("crush: exception -> "+e);
	  }
	}
	try{ Resource.freeResource(child.id, Resource.RT_WINDOW); }
	catch(Exception e){
	  //System.out.println("crush: exception -> "+e);
	}

	sib=child.nextSib;
	child.attr&=~viewable;
	if((child.attr&realized)!=0){
	  child.attr&=~realized;
	  child.attr|=visibility;
	  child.ddxwindow.setVisible(false);

	  if(screen.windowmode!=WeirdX.InBrowser && child.hasFrame()){        
            child.getFrame().setVisible(false);                  
	  }                                                      
	}

	try{child.free();}
	catch(Exception e){
	  //System.out.println("crush: exception -> "+e);
	}

	if((child=sib)!=null) break;

	child=cparent;
	child.firstChild=null;
	child.lastChild=null;
	if(child==this) return;
      }
    }
  }

  void delete() throws IOException{
    synchronized(LOCK){
      try{unmapWindow(false);}
      catch(Exception e){
      }
      try{
        crushTree();
      }
      catch(Exception e){
      }

      if(id!=0 && parent!=null && substrSend()){
        Event event=new Event();
        event.mkDestroyNotify(id, id);
        try{sendEvent(event, 1, null);}
        catch(Exception e){
        }
      }

      if(parent!=null){
	if(parent.firstChild==this) parent.firstChild=nextSib;
	if(parent.lastChild==this) parent.lastChild=prevSib;
	if(nextSib!=null) nextSib.prevSib=prevSib;
	if(prevSib!=null) prevSib.nextSib=nextSib;
      }
      parent=null;
      nextSib=null;
      prevSib=null;
      firstChild=null;
      lastChild=null;
      prevSib=null;

      try{free();}
      catch(Exception e){
      }
    }
  }

  static final void reqDestroyWindow(Client c) throws IOException{
    int foo;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }

    if(w.parent!=null){
      Resource.freeResource(w.id, Resource.RT_NONE);
    }
  }

  static final void reqSendEvent(Client c) throws IOException{
    Window win;
    Window effectiveFocus=null;   // only set if dest==InputFocus
    int foo;
    IO io=c.client;
    int prop=c.data;
    int dest=io.readInt();
    int emask=io.readInt();    
    byte[] bb=new byte[32];
    io.readByte(bb, 0, 32);
    c.length=0;
    Event event=new Event(bb);
    if(c.swap){ event.swap(); }

    if(dest==0/*PointerWindow*/){
      win=sprite.win;
    }
    else{
      win=c.lookupWindow(dest);
    }

    if(win==null){
      c.errorValue=dest;
      c.errorReason=3; // BadWindow;
      return;
    }

    if((prop !=0) && (prop !=1)){
      c.errorValue=prop;
      c.errorReason=2; // BadValue;
      return;
    }

    event.setType((byte)(event.getType()|0x80));
    if(prop!=0){
      for(;win!=null; win=win.parent){
	if(win.sendEvent(event, 1, emask, null, 0)!=0)
	  return;
	emask &=~win.getDontPropagateMask();
	if(emask==0)
	  break;
      }
    }
    else{
      win.sendEvent(event, 1, emask, null, 0);
    }

    return;
  }
  static final void reqGetPointerMapping(Client c) throws IOException{
    int foo,n;
    IO io=c.client;

    synchronized(io){
      io.writeByte(1);
      io.writeByte(0);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writePad(24);
      io.flush();
    }
  }

  static final void reqSetPointerMapping(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    foo=c.data;                 
    n=c.length;
    n--;
    while(n!=0){
      foo=io.readInt();
      n--;
    }

    synchronized(io){
      io.writeByte(1);
      io.writeByte((byte)1);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writePad(24);
      io.flush();
    }
  }
  static final void reqGetPointerControl(Client c) throws IOException{
    int foo,n;
    IO io=c.client;

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeShort(2);
      io.writeShort(1);
      io.writeShort(4);
      io.writePad(18);
      io.flush();
    }
  }

  static final void reqQueryPointer(Client c) throws IOException{
    int n, foo;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }

    int hx=Window.sprite.hot.x;
    int hy=Window.sprite.hot.y;

    Window child=null;
    synchronized(LOCK){
      for(Window ww=Window.sprite.win; ww!=null; ww=ww.parent){
        if(ww.parent==w){
          child=ww;
  	  break;
        }
      }

      if(child!=null){
        if(!child.contains(hx, hy)){
          sprite.win=xy2Window(hx, hy, null);
          child=null;
   	  for(Window ww=sprite.win; ww!=null; ww=ww.parent){
            if(ww.parent==w){
	      child=ww;
	      break;
	    }
	  }
        }
      }
    }

    synchronized(io){
      io.writeByte(1);
      io.writeByte(1);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeInt(w.screen.rootId);

      io.writeInt((child==null? 0 : child.id));
      io.writeShort(hx);
      io.writeShort(hy);
      io.writeShort(hx-w.x);
      io.writeShort(hy-w.y);
      io.writeShort(Window.sprite.hot.state);
//    io.writeShort(0);  // ?????????
      io.writePad(6);
      io.flush();
    }
  }

  static final void reqGetGeometry(Client c) throws IOException{
    IO io=c.client;
    int foo=io.readInt();

    Drawable d=c.lookupDrawable(foo);
    c.length-=2;
    if(d==null){
      c.errorValue=foo;
      c.errorReason=9; // BadValue;
      return;
    }

    synchronized(io){
      io.writeByte(1);
      io.writeByte(d.depth);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeInt(d.screen.rootId);

      if((d.type==UNDRAWABLE_WINDOW)||
         ((d.type==DRAWABLE_WINDOW) && foo==d.id)){
        Window w=(Window)d;
        io.writeShort(w.origin.x-w.borderWidth);
        io.writeShort(w.origin.y-w.borderWidth);
        io.writeShort(w.width);
        io.writeShort(w.height);
        io.writeShort(w.borderWidth);
      }
      else{
        io.writeShort(0);
        io.writeShort(0);
        io.writeShort(d.width);
        io.writeShort(d.height);
        io.writeShort(0);
      }
      io.writePad(10);
      io.flush();
    }
  }

  static final void reqChangeWindowAttributes(Client c) throws IOException{
    int foo;
    IO io=c.client;

    int wid=io.readInt(); 
    int mask=io.readInt(); 

    c.length-=3;

    Window w=c.lookupWindow(wid);
    if(w==null){
      c.errorValue=wid;
      c.errorReason=3; // BadWindow;
      return;
    }

    mask&=0x7fff;
    if(mask!=0){ w.changeAttr(c, mask); }

    if(c.errorReason!=0){
      return;
    }

    if((mask&CWBorderPixmap)!=0 || (mask&CWBorderPixel)!=0){
      w.ddxwindow.setBorderPixmap(w.border.pixmap);
    }
  }

  @SuppressWarnings("static-access")
static final void reqUngrabPointer(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    foo=io.readInt();
    if((Window.grab!=null) && Window.grab.sameClient(c)){
      Window.grab.deactivatePointerGrab();
    }
  }

  static final void reqUngrabButton(Client c) throws IOException{
    int foo;
    int button, mod;
    IO io=c.client;

    button=c.data;
    foo=io.readInt();

    c.length-=2;
    Window gw=c.lookupWindow(foo);
    if(gw==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }

    mod=io.readShort();
    io.readPad(2);
    c.length--;
    Grab grab=Grab.createGrab(c, gw, 0, 0, 0, 0, mod, 4, button, null);

    if(!grab.deletePassiveGrabFromList()){
      c.errorReason=11; // BadAlloc
    }
  }
  static final void reqGrabButton(Client c) throws IOException{
    int foo, n;
    int pmode, kmode, oe, emask, button, mod;
    IO io=c.client;

    oe=c.data;
    if(oe!=0 && oe!=1){
    }

    foo=io.readInt();
    Window gw=c.lookupWindow(foo);
    if(gw==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
    }
    emask=io.readShort();
    if((emask&~Event.PointerGrabMask)!=0){
    }

    pmode=io.readByte();
    if((pmode!=GrabModeSync) &&
       (pmode!=GrabModeAsync)){
    }

    kmode=io.readByte();
    if((kmode!=GrabModeSync) &&
       (kmode!=GrabModeAsync)){
    }

    foo=io.readInt();
    Window cto=null;
    if(foo!=0){
      cto=c.lookupWindow(foo);
    }

    foo=io.readInt(); 
    button=io.readByte();
    io.readPad(1);
    mod=io.readShort();
    c.length-=6;

    if(c.errorReason!=0){
      return;
    }

    if(gw==null || cto==null){
    }

    Grab grab=Grab.createGrab(c,
				gw, emask,
				oe, kmode, pmode,
				mod, Event.ButtonPress, 
				button, cto);
    grab.addPassiveGrabToList();
  }

  static final void reqGrabPointer(Client c) throws IOException{
    int foo, n;
    int pmode, kmode, oe, emask, time;
    IO io=c.client;

    oe=c.data;
    if(oe!=0 && oe!=1){
      c.errorValue=oe;
      c.errorReason=2; // BadValue;
    }

    foo=io.readInt();
    Window gw=c.lookupWindow(foo);
    if(gw==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow
    }

    emask=io.readShort(); 
    if((emask&~Event.PointerGrabMask)!=0){
      c.errorValue=emask;
      c.errorReason=2; // BadValue;
    }

    pmode=io.readByte();
    if((pmode!=GrabModeSync) && (pmode!=GrabModeAsync)){
      c.errorValue=pmode;
      c.errorReason=2; // BadValue
    }

    kmode=io.readByte();
    if((kmode!=GrabModeSync) && (kmode!=GrabModeAsync)){
      c.errorValue=kmode;
      c.errorReason=2; // BadValue
    }

    foo=io.readInt();
    Window cto=null;
    if(foo!=0){
      cto=c.lookupWindow(foo);
      if(cto==null){
	c.errorValue=foo;
	c.errorReason=3; // BadWindow
      }
    }

    foo=io.readInt();
    Cursor cur=null;
    if(foo!=0){
      cur=(Cursor)Resource.lookupIDByType(foo, Resource.RT_CURSOR);
      if(cur==null){
	c.errorValue=foo;
	c.errorReason=6; //BadCursor
      }
    }

    time=io.readInt();

    c.length-=6;

    if(c.errorReason!=0){
      return;
    }

    int res=0;
    if(Window.grab!=null && c!=Window.grab.getClient()){
      res=AlreadyGrabbed;
    }
    else if((gw.attr&realized)==0||
	    (cto!=null &&
	     !((cto.attr&realized)!=0 &&(cto.width!=0 || cto.height!=0)))){
      res=GrabNotViewable;
    }
    else {
      Grab grab=new Grab(Resource.fakeClientId(c));
      grab.set(c.clientAsMask, gw, oe,
	       emask, kmode, pmode, cto);
      grab.activatePointerGrab((int)System.currentTimeMillis(), true);
      res=GrabSuccess;
    }

    synchronized(io){
      io.writeByte(1);
      io.writeByte(res);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writePad(24);
      io.flush();
    }
  }

  static final void reqCreateWindow(Client c) throws IOException{
    int foo;

    byte depth;
    int x, y, width, height, bwidth, clss;
    int visual;
    int mask;
    IO io=c.client;

    depth=(byte)c.data;

    int wid=io.readInt();

    foo=io.readInt(); 
    Window prnt=c.lookupWindow(foo);

    x=io.readShort();
    y=io.readShort();
    width=io.readShort();
    height=io.readShort();
    bwidth=io.readShort();
    clss=io.readShort();
    visual=io.readInt(); 
    mask=io.readInt(); 

    c.length-=8;

    if(prnt==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
    }

    if((width==0 || height==0) && c.errorReason==0){
      c.errorValue=0;
      c.errorReason=2; // BadValue;
    }

    Window w=null;
    if(c.errorReason==0){
      w=new Window(wid, prnt, x, y, width, height, bwidth, clss, depth,
		     c, visual, mask);
    }

    if(c.errorReason!=0){
      return;
    }
    Resource.add(w);
  }

  int sendEvent(Event event, int count, int filter,
		Grab grab, int mskidx) throws IOException{
    int deliveries=0, nondeliveries=0;
    int attempt;
    Client c=null;
    int deliveryMask=0;
    int type=event.getType();

    if((filter==Event.CantBeFiltered) || 
	((type & Event.EXTENSION_EVENT_BASE)==0)){
      if((filter !=Event.CantBeFiltered) &&
	  (((getOtherEventMask()|eventMask) & filter)==0)){
	return 0;
      }
      attempt=1;
      try{ attempt=client.sendEvent(event, count, eventMask, filter, grab); }
      catch(Exception e){
      }

      if(attempt !=0 ){
	if(attempt > 0){
	  deliveries++;
	  c=client;
	  deliveryMask=eventMask;
	} 
	else
	  nondeliveries--;
      }
    }

    Clients other;
    if(filter !=Event.CantBeFiltered){
      if((type & Event.EXTENSION_EVENT_BASE)!=0){
	OtherInputMasks inputMasks;

	inputMasks=(optional==null?null:optional.otherInputMasks);
	if((inputMasks==null) ||
	    ((inputMasks.inputEvents[mskidx] & filter)==0)){
	  return 0;
	}
	other=(Clients)(inputMasks.inputClients);
      }
      else{ other=(Clients)(optional==null?null:optional.otherClients); }

      for(; other!=null; other=(OtherClients)other.next){
	attempt=1;
	try{
	  if(other.getClient()!=null){
            int mask=(other instanceof OtherClients) ? 
	            ((OtherClients)other).mask :
	            ((InputClients)other).mask[mskidx];
	    attempt=other.getClient().sendEvent(event, count, mask, 
						filter, grab);
	  }
	  else{
	    attempt=0;
	  }
	}
	catch(Exception e){
	}

	if(attempt!=0){
	  if(attempt > 0){
	    deliveries++;
	    c=other.getClient();
	    deliveryMask=((other instanceof OtherClients) ? 
			   ((OtherClients)other).mask :
			   ((InputClients)other).mask[mskidx]);
	  }
	  else
	    nondeliveries--;
	}
      }
    }


    if(c==null){
      if(deliveries!=0){
	return deliveries;
      }
      return nondeliveries;
    }

    if((type==Event.ButtonPress) && (deliveries!=0) && (grab==null)){
      grab=new Grab(Resource.fakeClientId(c));
      grab.set(c.clientAsMask, this,
	       (deliveryMask & Event.OwnerGrabButtonMask),
	       deliveryMask, GrabModeAsync, GrabModeAsync, null);
      grab.activatePointerGrab((int)System.currentTimeMillis(), true);      
    }
    if(deliveries!=0)
      return deliveries;
    return nondeliveries;
  }

  int sendEvent(Event event, int count, Window otherParent)throws IOException{
    int filter;
    int deliveries;

    if(count==0) return 0;

    filter=Event.filters[event.getType()];
    if(((filter & Event.SubstructureNotifyMask)!=0) && 
	(event.getType() !=Event.CreateNotify)){
      event.putEvent(id);
    }

    if(filter !=Event.StructureAndSubMask){
      return sendEvent(event, count, filter, null, 0);
    }

    deliveries=sendEvent(event, 
			   count, 
			   Event.StructureNotifyMask,  
			   null, 0);

    if(parent!=null){
      event.putEvent(parent.id);
      deliveries +=parent.sendEvent(event, 
				     count,
				     Event.SubstructureNotifyMask, 
				     null, 0);
      if(event.getType()==Event.ReparentNotify){
	event.putEvent(otherParent.id);
	deliveries +=otherParent.sendEvent(event, 
					    count,
					    Event.SubstructureNotifyMask,
					    null, 0);
      }
    }
    return deliveries;
  }

  int sendEvent(Event event, int count, 
		int filter, Client c) throws IOException{
    if((eventMask&filter)!=0){
      if(client==c) return 0;
      return client.sendEvent(event, count, eventMask, filter, null);
    }

    OtherClients other=(optional==null) ? null: optional.otherClients;
    for(; other!=null; other=(OtherClients)other.next){
      if((other.mask & filter)!=0){
	if(other.sameClient(c)) return 0;
	if(other.getClient()==null)
	  return 0;
	return other.getClient().sendEvent(event, 
					   count, 
					   other.mask, 
					   filter, null);
      }
    }
    return 2;
  }

  private Window findOptional(){
    Window w=this;
    do{w=w.parent;}while(w.optional==null);
    return w;
  }

  private void setDefault(){
    prevSib=null;
    firstChild=null;
    lastChild=null;
    optional=null;
    attr=0;

    attr|=cursorIsNone;

    attr|=visibility;
    attr|=(NorthWestGravity<<winGravityOffset);

    eventMask=0;
    attr|=(DBE_FRONT_BUFFER<<srcBufferOffset);
    attr|=(DBE_FRONT_BUFFER<<dstBufferOffset);
  }

  static final void reqUnmapSubWindows(Client c) throws IOException{
    int foo;
    IO io=c.client;
    foo=io.readInt();

    c.length-=2;

    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }
    synchronized(LOCK){
      w.unmapSubwindows(c);
    }
  }

  private void unmapSubwindows(Client c) throws IOException {
    if(firstChild==null) return;

    boolean wasRealized=(attr&realized)!=0;
    boolean wasViewable=(attr&viewable)!=0;
    boolean parentNotify=subSend(); 
    boolean anyMarked=false;

    for(Window child=lastChild; child !=null; child=child.prevSib){
      if((child.attr&mapped)!=0){
	if(parentNotify || child.strSend()){
	  c.cevent.mkUnmapNotify(child.id, 0);
	  child.sendEvent(c.cevent, 1, null);
	}

	child.attr&=~mapped;

	if((child.attr&realized)!=0){
	  child.unrealizeTree(false);
        }
      }
    }

    if(wasRealized){
      restructured ();
    }
  }

  static final void reqMapSubWindows(Client c) throws IOException{
    int foo;
    int mask;
    Window firstMapped=null;
    boolean anyMarked=false;

    IO io=c.client;

    foo=io.readInt();

    c.length-=2;

    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }

    boolean parentRedirect=w.parent.redirectSend();
    boolean parentNotify=w.parent.subSend();

    synchronized(LOCK){
      for(Window tmpw=w.firstChild; tmpw!=null; tmpw=tmpw.nextSib){
	if(w.screen.root!=w && w.parent==null) return;
        if((tmpw.attr&mapped)==0){
	  if(parentRedirect && ((tmpw.attr&overrideRedirect)==0)){
	    c.cevent.mkMapRequest(w.id, tmpw.id);
	    if(w.sendEvent(c.cevent, 1, 
			    Event.SubstructureRedirectMask, c)==1){
	      return;
	    }
	  }

	  tmpw.attr|=mapped;

	  if(parentNotify || w.strSend()){
	    c.cevent.mkMapNotify(tmpw.id, 
				 (tmpw.attr&overrideRedirect)>>overrideRedirectOffset);
	    tmpw.sendEvent(c.cevent, 1, null);
	  }
	  if(firstMapped==null) firstMapped=tmpw;
	  if((w.attr&realized)!=0){
	    tmpw.realizeTree();
	  }
	}
      }

      if(firstMapped!=null){
        restructured ();
      }
    }
  }

  static final void reqUnmapWindow(Client c) throws IOException{
    int foo;
    IO io=c.client;

    foo=io.readInt();

    c.length-=2;

    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }
    synchronized(LOCK){
      w.unmapWindow(false);
    }
  }

  private void unrealizeTree(boolean fromConfigure){
    Window child=this;
    while (true){
      if((child.attr&realized)!=0){
	child.attr&=~realized;
        child.attr|=visibility; //VisibilityNotViewable
	child.ddxwindow.setVisible(false);

        if(screen.windowmode!=WeirdX.InBrowser && child.hasFrame()){          
	  child.getFrame().setVisible(false);                    
	}                                                        

        try{child.deleteEvent(false);}
        catch(Exception e){
	  //System.out.println(e);
	}

	if((child.attr&viewable)!=0){
	  child.attr&=~viewable;
	}
	if(child.firstChild!=null){ 
          child=child.firstChild;
	  continue;
	}
      }
      while (child.nextSib==null && (child !=this))
	child=child.parent;
      if(child==this) break;
      child=child.nextSib;
    }

    if(parent!=null && borderWidth>0){
      parent.ddxwindow.draw(origin.x-borderWidth, origin.y-borderWidth,
			    width+2*borderWidth, height+2*borderWidth);
    }
  }

  private void realizeTree() throws IOException {
    Window child=this;
    while (true){
      if((child.attr&mapped)!=0){
	child.attr|=realized;
	child.attr|=(child.clss==InputOutput) ? viewable : 0;
	child.attr&=~visibility;
	child.attr|=(VisibilityUnobscured<<visibilityOffset);
        if(screen.windowmode!=WeirdX.InBrowser && child.hasFrame()){          
          if((0<=child.x || 0<=child.y ||                        
             0<(child.x+child.width) ||                          
             0<(child.y+child.height)) && clss!=2){              
	    child.getFrame().setVisible(true);                    
          }                                                      
	}                                                        
	child.ddxwindow.setVisible(true);
	child.ddxwindow.draw();
	if(((child.eventMask | child.getOtherEventMask()) & 
	     Event.VisibilityChangeMask)!=0){
          try{child.sendVisibilityNotify();}
          catch(Exception e){}
        }
	if(child.firstChild!=null){
	  child=child.firstChild;
	  continue;
	}
      }
      while (child.nextSib==null && (child !=this)){
	child=child.parent;
      }
      if(child==this){
	return;
      }
      child=child.nextSib;
    }
  }

  private void unmapWindow(boolean fromConfigure) throws IOException{
    Screen screen=this.screen;
    boolean wasRealized=((this.attr&realized)!=0);
    boolean wasViewable=((this.attr&viewable)!=0);
    Window layerWin=this;

    if((this.attr&mapped)==0) return;

    if(parent==null) return;
    if(substrSend()){
      Event event=new Event();
      event.mkUnmapNotify(id, (fromConfigure?1:0));
      try{sendEvent(event, 1, null);}
      catch(Exception e){
      }
    }
    this.attr&=~mapped;
    if(wasRealized){
      this.unrealizeTree(fromConfigure);
    }

    if(wasRealized && !fromConfigure){
      restructured ();
    }
  }

  static final void reqMapWindow(Client c) throws IOException{
    int foo;
    IO io=c.client;

    foo=io.readInt();

    c.length-=2;

    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }
    synchronized(LOCK){
      w.mapWindow(c);
    }
  }

  void mapWindow( Client c ) throws IOException{
    if((this.attr&mapped) !=0) return;
    Screen screen = this.screen;    
    if(parent!=null){
      if(((this.attr&overrideRedirect)==0) && (parent.redirectSend())){
	c.cevent.mkMapRequest(parent.id, id);
	int ii=parent.sendEvent(c.cevent, 1, 
				Event.SubstructureRedirectMask, c);
	if(ii==1) return;
      }
      this.attr|=mapped;
      if(substrSend()){
	c.cevent.mkMapNotify(id, 
			     (attr&overrideRedirect)>>overrideRedirectOffset);
	sendEvent(c.cevent, 1, null);
      }
      if((this.parent.attr&realized)==0) return;
      this.realizeTree();
      restructured ();
    }
    else{
      this.attr|=mapped;
      this.attr|=realized;
      this.attr|=(this.clss==InputOutput) ? viewable : 0;
    }
  }

  static void restructured () throws IOException{
    try{checkMotion(null, null);}
    catch(Exception e){
    }
  }

  static boolean checkMotion (Event e, Window hint) throws IOException{
    Window prevSpriteWin=sprite.win;
    sprite.win=xy2Window(sprite.hot.x, sprite.hot.y, hint);
    if(sprite.win !=prevSpriteWin){
      if(prevSpriteWin !=null){
	try{enter_leaveEvent(prevSpriteWin, sprite.win, NotifyNormal);}
	catch(Exception ee){
	  //System.out.println("checkmotion: execption -> "+ee);
	}
      }
      return false;
    }
    return true;
  }

  @SuppressWarnings("static-access")
static void enter_leaveEvent(int type, int mode, int detail,
			       Window win, int child) throws IOException {
    int mask;
    Grab grab=Window.grab;
    if(grab!=null){
      mask=(win==grab.window) ? grab.eventMask : 0;
      if((grab.attr&grab.ownerEvents)!=0){
	mask |=win.getEventMask(grab.getClient());
      }
    }
    else {
      mask=win.eventMask | win.getOtherEventMask();
    }

    if((mask & Event.filters[type])!=0){
      Event event=new Event();
      event.fixUpEventFromWindow(win, 0, sprite.hot.x, sprite.hot.y, false);
      int flags=0;
      flags=(event.getSameScreen()!=0?2:0);

      flags|=1;

      mode=0;
      event.mkPointer(type, detail, (int)System.currentTimeMillis(), child, 
		      sprite.hot.x, sprite.hot.y, sprite.hot.state,
		      mode, flags);
      try{
	if(grab!=null){
	  if(grab.getClient()!=null)
	    grab.getClient().sendEvent(event, 1, mask, Event.filters[type], grab);
	}
	else{
	  win.sendEvent(event, 1, Event.filters[type], null, 0);
	}
      }
      catch(Exception e){
	//System.out.println("enter_leaveEvent: exception -> "+e);
      }

    }
    if((type==Event.EnterNotify) && ((mask & Event.KeymapStateMask)!=0)){
      // KeymapNotify
    }
  }

  private int getEventMask(Client c){
    if(c==null) return 0;
    if(client==c) return eventMask;
    if(client==null) return 0;
    for(OtherClients other=getOtherClients(); 
	 other!=null; 
	 other=(OtherClients)other.next){
      if(other.sameClient(c)) return other.mask;
    }
    return 0;
  }

  private static void enterNotifies(Window ancestor, Window child, 
				    int mode, int detail) throws IOException{
    Window parent=child.parent;
    if(ancestor==parent) return;
    enterNotifies(ancestor, parent, mode, detail);
    enter_leaveEvent(Event.EnterNotify, mode, detail, parent, child.id);
  }

  private static void leaveNotifies(Window child, Window ancestor, 
			    int mode, int detail) throws IOException{
    if(ancestor==child) return;
    for(Window win=child.parent; win !=ancestor; win=win.parent){
      enter_leaveEvent(Event.LeaveNotify, mode, detail, win, child.id);
      child=win;
    }
  }

  static void enter_leaveEvent(Window fromWin, Window toWin, int mode)
    throws IOException{
    if(fromWin==toWin) return;
    if(fromWin.isParent(toWin)){
      try{
	enter_leaveEvent(Event.LeaveNotify, mode, NotifyInferior, fromWin, 0);
      }
      catch(Exception e){
      }
      try{
	enterNotifies(fromWin, toWin, mode, NotifyVirtual);
      }
      catch(Exception e){
      }
      try{
	enter_leaveEvent(Event.EnterNotify, mode, NotifyAncestor, toWin, 0);
      }
      catch(Exception e){
      }
    }
    else if(toWin.isParent(fromWin)){
      try{
	enter_leaveEvent(Event.LeaveNotify, mode, NotifyAncestor, fromWin, 0);
      }
      catch(Exception e){
      }
      try{
	leaveNotifies(fromWin, toWin, mode, NotifyVirtual);
      }
      catch(Exception e){
      }
      try{
	enter_leaveEvent(Event.EnterNotify, mode, NotifyInferior, toWin, 0);
      }
      catch(Exception e){
      }
    }
    else {
      Window common=toWin.commonAncestor(fromWin);
      try{
	enter_leaveEvent(Event.LeaveNotify, mode, NotifyNonlinear, fromWin, 0);
      }
      catch(Exception e){
      }
      try{
	leaveNotifies(fromWin, common, mode, NotifyNonlinearVirtual);
      }
      catch(Exception e){
      }
      try{
	enterNotifies(common, toWin, mode, NotifyNonlinearVirtual);
      }
      catch(Exception e){
      }
      try{
	enter_leaveEvent(Event.EnterNotify, mode, NotifyNonlinear, toWin, 0);
      }
      catch(Exception e){
      }
    }
  }

  boolean contains(int xx, int yy){
    return ddxwindow.contains(xx-x-borderWidth, yy-y-borderWidth);
  }

  static Window xy2Window(int x, int y, Window hint){
    if(hint!=spriteTrace[spriteTraceGood-1]){
      spriteTraceGood=1;
    }
//  else{ System.out.println("skip!!"); }

    Window  win=spriteTrace[spriteTraceGood-1];

    if(((win.attr&mapped)==0) || !win.contains(x, y)){
      win=spriteTrace[0];
      spriteTraceGood=1;
    }

    win=win.firstChild;
    while (win!=null){
      if(((win.attr&mapped)!=0) && win.contains(x, y)){
        if(spriteTraceGood >=spriteTrace.length){
          Window[] foo=new Window[spriteTrace.length+10];
          System.arraycopy(spriteTrace, 0, foo, 0, spriteTrace.length);
	  spriteTrace=foo;
	}
	spriteTrace[spriteTraceGood++]=win;
	win=win.firstChild;
      }
      else{
        win=win.nextSib;
      }
    }

    return spriteTrace[spriteTraceGood-1];
  }

  Cursor getCursor(){
    if(optional!=null) return optional.cursor;
    Window p=findOptional();
    return p.optional.cursor;
  }

  private boolean redirectSend(){
    return ((eventMask|getOtherEventMask()) & Event.SubstructureRedirectMask)!=0;
  }
  private boolean subSend(){
    return ((eventMask|getOtherEventMask()) & Event.SubstructureNotifyMask)!=0;
  }
  private boolean strSend(){
    return ((eventMask|getOtherEventMask()) & Event.StructureNotifyMask)!=0;
  }
  private boolean substrSend(){
    return strSend() || parent.subSend();
  }

  void makeOptional(){
    if(optional!=null) return;
    optional=new WindowOpt();
    optional.dontPropagateMask=DontPropagate.masks[(attr&dontPropagate)>>dontPropagateOffset];
    optional.otherEventMasks=0;
    optional.backingBitPlanes=~0L;
    optional.backingPixel=0;
    if(parent!=null){
      WindowOpt parentOptional=findOptional().optional;
      optional.visual=parentOptional.visual;
      if((attr&cursorIsNone)==0){
	optional.cursor=parentOptional.cursor;
      }
      else {
	optional.cursor=null;
      }
      optional.colormap=parentOptional.colormap;
    }
  }

  private void sendVisibilityNotify() throws IOException{
    Event event=new Event();
    event.mkVisibilityNotify(id, (attr&visibility)>>visibilityOffset);
    sendEvent(event, 1, null);
  }

  static final void reqQueryTree(Client c) throws IOException{
    int foo;
    Window prnt;
    Window w;
    int wid;
    int x, y, width, height, bwidth, clss;
    int visual;
    int mask;
    int rootId;
    IO io=c.client;
    wid=io.readInt(); 
    c.length-=2;
    w=c.lookupWindow(wid);
    if(w==null){
      c.errorValue=wid;
      c.errorReason=3; // BadWindow;
      return;
    }

    synchronized(LOCK){
      int numchild=0;

      for(Window p=w.lastChild; p !=null; p=p.prevSib){
	numchild++;    
      }

      synchronized(io){
        io.writeByte(1);
        io.writePad(1);
        io.writeShort(c.seq);
        io.writeInt(numchild);
        io.writeInt(w.screen.rootId);

        if(w.parent!=null) io.writeInt(w.parent.id);
        else io.writeInt(0);

        io.writeShort(numchild);
        io.writePad(14);

        if(0<numchild){
  	  for(Window p=w.lastChild; p !=null; p=p.prevSib){
	    io.writeInt(p.id);
	  }
        }
	io.flush();
      }
    }
  }

  static final void reqSetInputFocus(Client c) throws IOException{
    int foo, n, reverto;
    IO io=c.client;

    reverto=c.data;
    foo=io.readInt();
    c.length-=2;
    Window win=null;
    if(foo!=0 && foo!=1){
      win=c.lookupWindow(foo);
      if(win==null){
	c.errorValue=foo;
	c.errorReason=3; // BadWindow;
	return;
      }
    }

    int time=io.readInt();
    c.length--;

    setInputFocus(c, foo, reverto, time, false);
  }

  static final void setInputFocus(Client c,
				  int focusId, int revertTo,
				  int time, boolean followOk)
    throws IOException{
    if((revertTo!=RevertToParent) &&
       (revertTo!=RevertToPointerRoot) &&
       (revertTo!=RevertToNone)){
      c.errorValue=revertTo;
      c.errorReason=2; // BadValue
      return;
    }

    int focusWin=0;
    Window win=null;
    if((focusId==0) || (focusId==1)){
      focusWin=focusId;
    }
    else if((win=c.lookupWindow(focusId))==null){
      c.errorValue=focusId;
      c.errorReason=3;
      return;
    }
    else {
      if(!win.isRealized()){
	c.errorReason=8; // BadMatch
 	return;
      }
      focusWin=focusId;
    }

    int mode=grab!=null ? NotifyWhileGrabbed : NotifyNormal;
    doFocusEvents(c, focus.win, focusWin, mode);
    focus.time=time;
    focus.revert=revertTo;
    focus.win=focusWin;
    focus.window=win;

    if((focusWin==0) || (focusWin==1)){
      focus.traceGood=0;
      if(focusWin==1)
	Screen.screen[0].root.ddxwindow.requestFocus();
    }
    else {
      synchronized(LOCK){
        win.ddxwindow.requestFocus();
        int depth=0;
        for(Window tmpw=win; tmpw!=null; tmpw=tmpw.parent) depth++;
        if(depth > focus.traceSize){
  	  focus.traceSize=depth+1;
	  focus.trace=new Window[focus.traceSize];
        }
        focus.traceGood=depth;
        depth--;
        for(Window tmpw=win; tmpw!=null; tmpw=tmpw.parent, depth--){
	  focus.trace[depth]=tmpw;
        }
      }
    }
  }

  static final void reqGetInputFocus(Client c) throws IOException{
    int foo;
    IO io=c.client;

    synchronized(io){
      io.writeByte(1);
      io.writeByte(focus.revert);      // revert-to
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeInt(focus.win);          // focus
      io.writePad(20);     
      io.flush();
    }
  }

  static final void reqGetWindowAttributes(Client c) throws IOException{
    int foo;
    IO io=c.client;

    foo=io.readInt();
    c.length-=2;
    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow
      return;
    }

    synchronized(io){
      io.writeByte(1);

      if((w.attr&forcedBS)!=0 && (w.attr&backingStore)!=2) 
        io.writeByte(0);  // NotUseful
      else 
        io.writeByte((w.attr&backingStore)>>backingStoreOffset);

      io.writeShort(c.seq);
      io.writeInt(3);
      io.writeInt(w.getVisual());
      io.writeShort(w.clss);
      io.writeByte((w.attr&bitGravity)>>bitGravityOffset);
      io.writeByte((w.attr&winGravity)>>winGravityOffset);
      io.writeInt(w.getBackingBitPlanes());
      io.writeInt(w.getBackingPixel());
      io.writeByte((w.attr&saveUnder)>>saveUnderOffset);
 
      if(w.getColormapId()==0) io.writeByte(0);
      else io.writeByte(Colormap.isMapInstalled(w.getColormapId(), w));

      if((w.attr&mapped)==0) io.writeByte(0);  
      else if((w.attr&realized)!=0) io.writeByte(2);
      else io.writeByte(1);

      io.writeByte((w.attr&overrideRedirect)>>overrideRedirectOffset);
      io.writeInt(w.getColormapId());
      io.writeInt(w.eventMask|w.getOtherEventMask());
      io.writeInt(w.getEventMask(c));
      io.writeShort(w.getDontPropagateMask());
      io.writePad(2);
      io.flush();
    }
  }

  static final void reqCopyPlane(Client c) throws IOException{
    int foo;
    Drawable dsrc=null, ddst=null;
    IO io=c.client;
    foo=io.readInt();
    dsrc=c.lookupDrawable(foo);
    if(dsrc==null){
      c.errorValue=foo;
      c.errorReason=9; // Drawable
    }

    int dest=io.readInt();
    ddst=c.lookupDrawable(dest);
    if(ddst==null && c.errorReason==0){
      c.errorValue=dest;
      c.errorReason=9; // Drawable
    }
    foo=io.readInt();
    GC gc=c.lookupGC(foo);
    if(gc==null && c.errorReason==0){
      c.errorValue=foo;
      c.errorReason=13; // BadGC;
    }
    int sx, sy, dx, dy, width, height;

    sx=(short)io.readShort();
    sy=(short)io.readShort();

    int destx, desty;

    destx=(short)io.readShort();
    dx=destx-sx;

    desty=(short)io.readShort();
    dy=desty-sy;

    width=io.readShort();
    height=io.readShort();

    int bplane=io.readInt();
    c.length-=8;

    if(c.errorReason!=0){
      return;
    }

    Graphics g=ddst.getGraphics();
    if(((dsrc instanceof Window) && !((Window)dsrc).ddxwindow.isVisible())){
      g=null;
    }

    if(dsrc.width<=sx || dsrc.height<=sy ||
       (sx+width)<=0 || (sy+height)<=0 ||
       (destx+width)<=0 || (desty+height)<=0){
      g=null;
    }

    if(g!=null){
      if(dsrc instanceof Window){
        if(ddst instanceof Window){
          ((Window)dsrc).ddxwindow.copyArea(((Window)ddst), gc,
	                                    sx, sy, width, height, destx, desty); 
	}                                                          
      } 
      else {
	Image img=null;
	if(ddst instanceof Pixmap){
	  ((Pixmap)dsrc).copyPlane((Pixmap)ddst, gc, 
				   sx, sy, destx, desty, width, height);
	}
	else{
	  img=((Pixmap)dsrc).getImage((Window)ddst, gc, sx, sy, width, height);
	  Window wdst=(Window)ddst;

 	  if(sx==0 && sy==0 && width==dsrc.width && height==dsrc.height){
	    wdst.ddxwindow.drawImage(gc.clip_mask,
				     img, 
                                     destx, desty, 
                                     width, height);
	  }
	  else{
  	    java.awt.Shape tmp=g.getClip();
	    g.clipRect(destx, desty, width, height);
	    wdst.ddxwindow.drawImage(gc.clip_mask,
				     img, 
                                     destx-sx, desty-sy, 
                                     dsrc.width, dsrc.height);
	    if(tmp==null){ g.setClip(0, 0, wdst.width, wdst.height); }
            else{ g.setClip(tmp); }
	  }
	  wdst.draw(destx, desty, width, height);
	  if(img!=((Pixmap)dsrc).getImage()){
	    img.flush();
	  }
	}
      }
    }
  }

  static final void reqCopyArea(Client c) throws IOException{
    int foo;
    Drawable dsrc=null, ddst=null;
    IO io=c.client;

    foo=io.readInt();
    dsrc=c.lookupDrawable(foo);
    if(dsrc==null){
      c.errorValue=foo;
      c.errorReason=9;  // BadDrawable;
    }

    int dest=io.readInt();
    ddst=c.lookupDrawable(dest);
    if(ddst==null && c.errorReason==0){
      c.errorValue=dest;
      c.errorReason=9; // BadDrawable;
    }

    foo=io.readInt();

    GC gc=c.lookupGC(foo);

    if(gc==null && c.errorReason==0){
      c.errorValue=foo;
      c.errorReason=13; // BadGC;
    }

    int sx, sy;
    sx=(short)io.readShort();
    sy=(short)io.readShort();

    int destx, desty;
    destx=(short)io.readShort();
    desty=(short)io.readShort();

    int width, height;
    width=io.readShort();
    height=io.readShort();

    c.length-=7;

    if(c.errorReason!=0){
      return;
    }

    Graphics g=ddst.getGraphics();
    if(((dsrc instanceof Window) && !((Window)dsrc).ddxwindow.isVisible())){
      g=null;
    }

    if(dsrc.width<=sx || dsrc.height<=sy ||
       (sx+width)<=0 || (sy+height)<=0 ||
       (destx+width)<=0 || (desty+height)<=0){
      g=null;
    }

    /*
    LOG.info("copyArea: "+dsrc+" sx="+sx+",sy="+sy+
                                  ", w="+width+", h="+height+" "+
                                   ddst+" destx="+destx+",desty="+desty);
    */

    if(g!=null){
      if(dsrc instanceof Window){

        if(sx<0){sx=0;}
        if(sy<0){sy=0;}
        if(destx<0){destx=0;}
        if(desty<0){desty=0;}

	if(ddst instanceof Window){
          ((Window)dsrc).ddxwindow.
	    copyArea(((Window)ddst), gc,
		     sx, sy, width, height, destx, desty); 
	}
//      else{
//	  ((Window)dsrc).ddxwindow.
//	    copyArea((Pixmap)ddst, gc,
//		     sx, sy, destx, desty, width, height);
//	}
      }
      else {

	if(ddst instanceof Pixmap){
          if(sx<0){sx=0;}
          if(sy<0){sy=0;}
          if(destx<0){destx=0;}
          if(desty<0){desty=0;}

	  ((Pixmap)dsrc).copyArea((Pixmap)ddst, gc, 
				  sx, sy, destx, desty, width, height);
	}
	else{
	  Image img=((Pixmap)dsrc).getImage((Window)ddst, gc, 
					    sx, sy, width, height);

	  Window wdst=(Window)ddst;

	  if(sx==0 && sy==0 && width==dsrc.width && height==dsrc.height){
	    wdst.ddxwindow.drawImage(gc.clip_mask,
				     img, 
                                     destx, desty, width, height);
	  }
	  else{
  	    java.awt.Shape tmp=g.getClip();
	    if(destx>=0 && desty>=0){
  	      g.clipRect(destx, desty, width, height);
	    }
            else{
  	      g.clipRect((destx<0?0:destx),
			 (desty<0?0:desty),
			 (destx<0?width+destx:width),
			 (desty<0?height+desty:height));
	    }
	    wdst.ddxwindow.drawImage(gc.clip_mask,
				     img, destx-sx, desty-sy, 
				     dsrc.width, dsrc.height);
	    if(tmp==null){ g.setClip(0, 0, wdst.width, wdst.height); }
            else{ g.setClip(tmp); }
	  }
	  wdst.draw(destx, desty, width, height);
	  if(img!=((Pixmap)dsrc).getImage()){
	    img.flush();
	  }
	}
      }
    }

    if((gc.attr&GC.graphicsExposures)!=0){
      c.cevent.mkNoExposure(dest, 0, 62);
      c.sendEvent(c.cevent,
		  1, 
		  0,
		  Event.NoEventMask,
		  null);
    }
  }

  static final void reqClearArea(Client c) throws IOException{
    int foo;
    int exposures=c.data;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    Window w=c.lookupWindow(foo);
    if(w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
      return;
    }

    int x, y, width, height;

    x=(short)io.readShort();
    y=(short)io.readShort();
    width=io.readShort();
    if(width==0){width=w.width-x;}
    height=io.readShort();
    if(height==0){height=w.height-y;}

    c.length-=2;

    if(c.errorReason!=0){
      return;
    }

    if(w.getGraphics()!=null){  // isMapped??
      w.makeBackgroundTile(x, y, width, height);
      w.draw(x, y, width+1, height+1);    
    }
 
    if(exposures!=0){
      c.cevent.mkExpose(w.id, x, y, width, height, 0);
      c.sendEvent(c.cevent, 1, 
		  Event.filters[Event.Expose],
		  Event.filters[Event.Expose], Window.grab);
    }
  }
  
  void draw(){
    ddxwindow.draw();
  }

  void draw(int x, int y, int width, int height){
    ddxwindow.draw(x, y, width, height);
  }

  private Window trackParent(){
    Window w=this;
    while(true){
      if(w.optional!=null)break;
      w=w.parent;
    }
    return w;
  }

  private Client getClient(){
    return Client.clients[(((id) & Client.CLIENTMASK) >> Client.CLIENTOFFSET)];
  }

  private int getBackingBitPlanes(){
    if(optional==null)return ~0;
    return (int)optional.backingBitPlanes;
  }

  private int getBackingPixel(){
    if(optional==null)return 0;
    return (int)optional.backingPixel;
  }

  OtherClients getOtherClients(){
    if(optional==null)return null;
    return optional.otherClients;
  }

  static java.awt.Frame owner=new java.awt.Frame();          
  java.awt.Window frame=null;                                

  static private final int poolsize=5;                       
  static java.awt.Window[] frames=new java.awt.Window[poolsize]; 

  java.awt.Window getFrame(){
    if(hasFrame()) return optional.frame;
    if(parent!=screen.root){
      return null;
    }

    makeOptional();
    if(screen.windowmode==WeirdX.RootlessWM &&
       !screen.root.redirectSend() &&
       (attr&(1<<16))==0){
      optional.frame=new java.awt.Frame();     
      optional.frame.addMouseListener((java.awt.event.MouseListener)ddxwindow);
      return optional.frame;
    }

    synchronized(Window.class){                           
      java.awt.Window foo=null;                                
      for(int i=0; i<poolsize; i++){                           
        if(frames[i]!=null){                                   
          foo=frames[i];
          frames[i]=null;
          optional.frame=foo;           
          return foo;
        }                                                      
      }                                 
    }                                 
    optional.frame=new java.awt.Window(owner);     
    return optional.frame;                  
  }
  boolean hasFrame(){
    return (optional!=null && optional.frame!=null);
  }

  void delFrame(){
    if(optional==null || optional.frame==null) return;
    optional.frame.setVisible(false);
    if(screen.windowmode==WeirdX.RootlessWM &&
       optional.frame instanceof java.awt.Frame){
      optional.frame.removeMouseListener((java.awt.event.MouseListener)ddxwindow);
//    optional.frame.dispose();
      optional.frame=null;
      return;
    }

    synchronized(Window.class){                           
      for(int i=0; i<poolsize; i++){                           
        if(frames[i]==null){                                   
          frames[i]=optional.frame;
          optional.frame=null;
          return;                               
        }                                                      
      }                                                        
    }
    optional.frame.dispose();
    optional.frame=null;
  }

  void setProperty(Property p){
    makeOptional();
    optional.userProps=p;
  }

  Property getProperty(){
    if(optional==null)return null;
    return optional.userProps;
  }

  static final void reqConfigureWindow(Client c) throws IOException{
    int foo;
    int n;
    IO io=c.client;
    foo=io.readInt();
    Window win=c.lookupWindow(foo);
    if(win==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow
    }

    int mask=io.readShort();
    io.readPad(2);
    c.length-=3;
    if(c.errorReason!=0){ return; }
    if(((win.clss==InputOnly) && 
	 (mask & IllegalInputOnlyConfigureMask)!=0) ||
	((mask & CWSibling)!=0 && (mask & CWStackMode)==0)){
      c.errorReason=8; // BadMatch
      return;
    }

    int index;
    int x,y;
    int w=win.width;
    int h=win.height;
    int bw=win.borderWidth;
    Window sib=null;

    synchronized(LOCK){
      win=c.lookupWindow(win.id);
      if(win==null){
	c.errorValue=win.id;
	c.errorReason=3; // BadWindow
	return;
      }
      if(win.screen.root!=win && win.parent==null) return;
      if(win.parent!=null){
	x=win.x-win.parent.x-bw;
	y=win.y-win.parent.y-bw;
      }
      else{
	x=win.x;
	y=win.y;
      }
      
      mask&=0x7f;

      int beforex=x, beforey=y;

      int smode=0;
      int tmp=mask;
      while(tmp!=0 && c.length!=0){
	index=lowbit(tmp);
	tmp&=~index;
	switch(index){
	case CWX: 
	  foo=io.readInt();
	  foo=(short)(foo&0xffff);
	  if(foo==x){ mask-=CWX; }
	  else{ x=(short)(foo); }
	  break;
	case CWY: 
	  foo=io.readInt();
	  foo=(short)(foo&0xffff);
	  if(foo==y){ mask-=CWY; }
	  else{ y=(short)(foo); }
	  break;
	case CWWidth: 
	  foo=io.readInt();
	  foo=(foo&0xffff);
	  if(w==foo){ mask-=CWWidth; }
	  else{ w=foo; }
	  break;
	case CWHeight: 
	  foo=io.readInt();
	  foo=(foo&0xffff);
	  if(h==foo){ mask-=CWHeight; }
	  else{ h=foo; }
	  break;
	case CWBorderWidth: 
	  foo=io.readInt();
	  foo=(foo&0xffff);
	  bw=foo;
	  break;
	case CWSibling: 
	  foo=io.readInt();
	  sib=c.lookupWindow(foo);
	  if(sib==null){
	    c.errorValue=foo;
	    c.errorReason=3; // BadWindow
	  }
	  else if((sib.parent!=win.parent) || (sib==win)){
	    c.errorReason=8; // BadMatch
	  }
	  break;
	case CWStackMode: 
	  foo=io.readInt();
	  foo&=0xff;
	  smode=foo;
	  if((smode!=TopIf) && (smode!=BottomIf) &&
	     (smode!=Opposite) && (smode!=Above) && (smode!=Below)){
	    c.errorValue=foo;
	    c.errorReason=2; // BadValue
	  }
	  break;
	default:
	  c.errorValue=mask;
	  c.errorReason=2; // BadValue
	  return;
	}
	c.length--;
	if(c.errorReason!=0){
	  return;
	}
      }
      if(c.length!=0){
	c.errorValue=mask;
	c.errorReason=2; // BadValue
      return;
      }
    
      if(mask==0){
	return;
      }

      if(win.parent==null) return;

      if((mask&CWStackMode)!=0){
	sib=win.getPlaceInTheStack(sib, 
				   win.parent.x+x, win.parent.y+y,
				   w+bw*2, h+bw*2, smode);
      }
      else{ sib=win.nextSib; }

      if(((win.attr&overrideRedirect)==0) &&
	 (win.parent.redirectSend())){
	c.cevent.mkConfigureRequest(((mask&CWStackMode)!=0)?smode:Above,
				    win.parent.id, 
				    win.id, 
				    ((mask&CWSibling)!=0)? sib.id : 0,
				    x, y, w, h, bw, mask);
	if(win.parent.sendEvent(c.cevent, 1,
				Event.SubstructureRedirectMask, c)==1){
	  return;
	}

      }

      int action=Restack;
      if((mask & (CWX|CWY))!=0 && (mask & (CWHeight|CWWidth))==0){
	action=Move;
      }
      else if((mask & ChangeMask)!=0){
	if(w==0 || h==0){
	  c.errorValue=0;
	  c.errorReason=2; // BadBalue;
	  return; 
	}
	action=Resize;
      }

      if(action==Resize){
	boolean sizec=((w!=win.width)||(h!=win.height));
	if(sizec && 
	   (((win.eventMask|win.getOtherEventMask())&Event.ResizeRedirectMask)!=0)){
	  c.cevent.mkResizeRequest(win.id, w, h);
	  if(win.sendEvent(c.cevent, 1,
			   Event.ResizeRedirectMask, c)==1){
	    w=win.width;
	    h=win.height;
	    sizec=false;
	  }
	}
	if(!sizec){
	  if((mask&(CWX|CWY))!=0) action=Move;
	  else if((mask&(CWStackMode|CWBorderWidth))!=0){ action=Restack; }
	  else{ return; }
	}
      }

      if((action==Resize)||
	 (((mask&CWX)!=0)&& x!=beforex) ||
	 (((mask&CWY)!=0)&& y!=beforey) ||
	 (((mask&CWBorderWidth)!=0)&& bw!=win.borderWidth) ||
	 (((mask&CWStackMode)!=0) && win.nextSib!=sib)){
	if(win.substrSend()){
	  c.cevent.mkConfigureNotify(win.id,
				     (sib!=null? sib.id:0),
				     x, y, w, h, bw, 
				     (win.attr&overrideRedirect)!=0?0:1);
	  win.sendEvent(c.cevent, 1, null);
	}

	if((mask&CWBorderWidth)!=0){
	  if(action==Restack){
	    action=Move;
	    win.borderWidth=bw;
	    win.ddxwindow.setBorder(bw);
	  }
	  else if((action==Move) &&
		  ((beforex+win.borderWidth)==x+bw) &&
		  ((beforey+win.borderWidth)==y+bw)){
	    action=Reborder;
	  }
	  else{
	    win.borderWidth=bw;
	    win.ddxwindow.setBorder(bw);
	  }
	}

        if((mask&CWStackMode)!=0){           
	  win.reflectStackChange(sib
                                  );
	}

	if(((mask&CWX)!=0) || ((mask&CWY)!=0) ||
	   ((mask&CWWidth)!=0) || ((mask&CWHeight)!=0)
	   ||((mask&CWBorderWidth)!=0)){
	  win.origin.x=x+bw;
	  win.origin.y=y+bw;

	  int dx,dy, dw, dh;
	  dx=dy=dw=dh=0;
	  dw=w-win.width;
	  dh=h-win.height;
	  win.height=h;
	  win.width=w;

	  int newx=win.parent.x+x+bw;
	  int newy=win.parent.y+y+bw;

	  int oldx, oldy;
	  oldx=win.x;
	  oldy=win.y;

	  x=win.x=newx;
	  y=win.y=newy;

          Window wToValidate=win.moveInStack(sib);
	  if(((mask&CWWidth)!=0) || ((mask&CWHeight)!=0)
	     ||((mask&CWBorderWidth)!=0)){
            if(win.screen.windowmode!=WeirdX.InBrowser && win.hasFrame()){  
	      java.awt.Window frame=win.getFrame();                      
              frame.validate();
	      Insets insets=frame.getInsets();                         
   	      frame.setSize(win.width+win.borderWidth*2+             
		            insets.left+insets.right,                  
		            win.height+win.borderWidth*2+            
		            insets.bottom+insets.top);                 
              frame.validate();
            }                                                  

	    win.ddxwindow.setSize(win.width, win.height);
	  }

	  win.resizeChildrenWinSize(x-oldx, y-oldy, dw, dh);
	  win.ddxwindow.setLocation(win.origin.x-win.borderWidth+win.parent.borderWidth,
                                    win.origin.y-win.borderWidth+win.parent.borderWidth);

        }
	return;
      }
      else{
	return;
      }
    }
  }

  private int getDontPropagateMask(){
    if(optional==null)
      return DontPropagate.masks[(attr&dontPropagate)>>dontPropagateOffset];
    return optional.dontPropagateMask;
  }

  Grab getPassiveGrabs(){
    if(optional==null) return null;
    return optional.passiveGrabs;
  }

  int getOtherEventMask(){
    if(optional==null) return 0;
    return optional.otherEventMasks;
  }

  void setColormap(Colormap cmap){
    makeOptional();
    optional.colormap=cmap;
  }
  Colormap getColormap(){
    return trackParent().optional.colormap;
  }
  private int getColormapId(){
    if(clss==InputOnly) return 0;
    return getColormap().id;
  }

  void setVisual(int id){
    makeOptional();
    optional.visual=id;
  }
  private int getVisual(){
    return trackParent().optional.visual;
  }

  boolean isMapped(){ return (attr&mapped)!=0; }
  boolean isRealized(){ return (attr&realized)!=0; }
  boolean isViewable(){ return (attr&viewable)!=0; }
  boolean isBorderPixel(){ return (attr&borderIsPixel)!=0; }
  void setBorderIsPixel(){ attr|=borderIsPixel; }
  void setBackgroundIsPixel(){ 
    attr&=~backgroundState;
    attr|=BackgroundPixel;
  }

  boolean hasParentRelativeBorder(){
    return (attr&borderIsPixel)==0 && 
           hasBorder() &&                               
           (attr&backgroundState)==ParentRelative;
  }

  boolean hasBorder(){ return borderWidth!=0; }

  private void reflectStackChange(Window sib) throws IOException{
    boolean wasViewable=(attr&viewable)!=0;
    if(parent==null) return;

    Window firstChange=moveInStack(sib);
    if((attr&realized)!=0){
      restructured();
    }
  }

  private int isSiblingAboveMe(Window sib){
    Window win=parent.firstChild;
    while (win!=null){
      if(win==sib) return(Above);
      else if(win==this) return(Below);
      win=win.nextSib;
    }
    return(Below);
  }

  private Window getPlaceInTheStack(Window sib, 
				    int x, int y, int w, int h, int smode){
    if((this==parent.firstChild) && (this==parent.lastChild)) return null;

    Window first=parent.firstChild;
    switch (smode){
    case Above:
      if(sib!=null) return(sib);
      else if(this==first) return(nextSib);
      else return(first);
    case Below:
      if(sib!=null)
	if(sib.nextSib !=this) return(sib.nextSib);
	else return(nextSib);
      else
	return null;
    case TopIf:
      if((((attr&mapped)==0) || (sib!=null && (sib.attr&mapped)==0)))
	return(nextSib);
      else if(sib!=null){
	if((isSiblingAboveMe(sib)==Above))
	  return(first);
	else
	  return(nextSib);
      }
      else 
	return(first);
    case BottomIf:
      if((((attr&mapped)==0) || (sib!=null && (sib.attr&mapped)==0)))
	return(nextSib);
      else if(sib!=null){
	if((isSiblingAboveMe(sib)==Below))
	  return null;
	else
	  return(nextSib);
      }
      else 
	return null;
    case Opposite:
      if((((attr&mapped)==0) || (sib!=null && (sib.attr&mapped)==0)))
	return(nextSib);
      else if(sib!=null){
	if(isSiblingAboveMe(sib)==Above)
	  return(first);
	else
	  return null;
      }
      else 
	return(first);
    default:
      return nextSib;
    }
  }

  private Window moveInStack(Window next){
    Window firstChange=this;
    synchronized(LOCK){
      if(nextSib !=next){
	if(next==null){	                  // to bottom 
	  if(parent.firstChild==this) parent.firstChild=nextSib;
	  firstChange=nextSib;
	  nextSib.prevSib=prevSib;
	  if(prevSib!=null) prevSib.nextSib=nextSib;
	  parent.lastChild.nextSib=this;
	  prevSib=parent.lastChild;
	  nextSib=null;
	  parent.lastChild=this;

	  if(screen.windowmode==WeirdX.InBrowser || parent!=screen.root){
            parent.ddxwindow.add((java.awt.Component)(this.ddxwindow), -1); 
	  }                                                        
	}
	else if(parent.firstChild==next){  // to top
	  firstChange=this;
	  if(parent.lastChild==this) parent.lastChild=prevSib;
	  if(nextSib!=null) nextSib.prevSib=prevSib;
	  if(prevSib!=null) prevSib.nextSib=nextSib;
	  nextSib=parent.firstChild;
	  prevSib=null;
	  next.prevSib=this;
	  parent.firstChild=this;

	  if(screen.windowmode==WeirdX.InBrowser || parent!=screen.root){
	    parent.ddxwindow.add((java.awt.Component)(this.ddxwindow), 0); 
	  }                                                     
	}
	else{			                   // in middle
	  Window pOldNext=nextSib;
	  firstChange=null;
	  if(parent.firstChild==this)
	    firstChange=parent.firstChild=nextSib;
	  if(parent.lastChild==this){
	    firstChange=this;
	    parent.lastChild=prevSib;
	  }
	  if(nextSib!=null) nextSib.prevSib=prevSib;
	  if(prevSib!=null) prevSib.nextSib=nextSib;
	  nextSib=next;
	  prevSib=next.prevSib;
	  if(next.prevSib!=null) next.prevSib.nextSib=this;
	  next.prevSib=this;
	  if(firstChange==null){
	    firstChange=parent.firstChild;
	    while ((firstChange !=this) && (firstChange !=pOldNext))
	      firstChange=firstChange.nextSib;
	  }

	  Component[] component=parent.ddxwindow.getComponents();
	  for(int i=0; i<component.length;i++){
	    if(component[i]==next.ddxwindow){
	      if(i!=0)i--;
	      if(screen.windowmode==WeirdX.InBrowser || parent!=screen.root){
		parent.ddxwindow.add((java.awt.Component)(this.ddxwindow), i);
	      }
	      break;
	    }
	  }
	}
      }
    }
    this.draw();
    return firstChange;
  }

  static final void reqReparentWindow(Client c) throws IOException{
    int foo;
    int x,y;
    IO io=c.client;

    foo=io.readInt();
    Window win=c.lookupWindow(foo);
    if(win==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
    }

    foo=io.readInt();
    Window parent=c.lookupWindow(foo);
    if(parent==null && c.errorReason==0){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
    }

    x=(short)io.readShort();
    y=(short)io.readShort();

    c.length-=4;

    if(c.errorReason!=0) return;
    win.reparent(parent, x, y, c);
  }

  void reparent(Window newparent, int xx, int yy, Client c) 
    throws IOException{
    boolean wasMapped;

    synchronized(LOCK){
      if(parent==null) return;

      int bw=borderWidth;
      wasMapped=((attr&mapped) !=0);
      Window prev;

      makeOptional();

      if(wasMapped){
	unmapWindow(false);
      }

      c.cevent.mkReparentNotify(id, id, newparent.id,
				xx, yy, ((attr&overrideRedirect)!=0 ? 1 : 0));

      sendEvent(c.cevent, 1, newparent);

      prev=parent;
      if(prev.firstChild==this) prev.firstChild=nextSib;
      if(prev.lastChild==this) prev.lastChild=prevSib;

      if(nextSib!=null) nextSib.prevSib=prevSib;
      if(prevSib!=null) prevSib.nextSib=nextSib;

      parent=newparent;

      nextSib=newparent.firstChild;
      prevSib=null;
      if(newparent.firstChild!=null) newparent.firstChild.prevSib=this;
      else newparent.lastChild=this;
      newparent.firstChild=this;

      origin.x=(short)(xx);
      origin.y=(short)(yy);
      x=(short)(xx + newparent.x);
      y=(short)(yy + newparent.y);

      if(screen.windowmode!=WeirdX.InBrowser && hasFrame()){                 
	java.awt.Window frame=getFrame();                                 
        frame.remove((java.awt.Component)ddxwindow);
//	frame.dispose();                                        
	delFrame();                                         
      }                                                         
      if(screen.windowmode!=WeirdX.InBrowser && newparent==screen.root){
        java.awt.Window frame=getFrame();
	/*if(frame instanceof JFrame){                               
	  ((JFrame)frame).setJMenuBar(null);                       
	  ((JFrame)frame).getContentPane().setLayout(null);        
  	  ((JFrame)frame).setResizable(false);                                 
	}                                                          
	else*/ if(frame instanceof Frame){
	  ((Frame)frame).setMenuBar(null);                                  
  	  ((Frame)frame).setResizable(true);
	}                                                          
                                                                
	ddxwindow.setLocation(0, 0);                            
                                    
        frame.add((java.awt.Component)ddxwindow);

        frame.validate();
	Insets insets=frame.getInsets();                      
	frame.setSize(this.width+this.borderWidth*2+          
		      insets.left+insets.right,               
		      this.height+this.borderWidth*2+         
		      insets.bottom+insets.top);              
        frame.validate();

        if(frame instanceof Frame){
          addWindowListener((java.awt.Frame)frame);
          addComponentListener((java.awt.Frame)frame);
	}
      }                                                         
      else{                                                     
	newparent.ddxwindow.add((java.awt.Component)ddxwindow, 0); // ???
	ddxwindow.setLocation(origin.x-borderWidth+parent.borderWidth,
			      origin.y-borderWidth+parent.borderWidth);
      }                                                         

      resizeChildrenWinSize(0, 0, 0, 0);

      if(wasMapped){
	mapWindow(c);
      }

      recalculateDeliverableEvents();
    }
  }

  void recalculateDeliverableEvents(){
    OtherClients others;

    Window child=this;
    while (true){
      if(child.optional!=null){
	child.optional.otherEventMasks=0;
	others=(optional==null)? null: optional.otherClients;
	for(; others!=null; others=(OtherClients)others.next){
	  child.optional.otherEventMasks |=others.mask;
	}
      }

      child.deliverableEvents=child.eventMask|child.getOtherEventMask();
      if(child.parent!=null)
	child.deliverableEvents |=
	  (child.parent.deliverableEvents & Event.PropagateMask 
	   & ~(child.getDontPropagateMask()));

      if(child.firstChild!=null){
	child=child.firstChild;
	continue;
      }

      while (child.nextSib==null && (child !=this))
	child=child.parent;
      if(child==this)
        break;
      child=child.nextSib;
    }
  }

  final Graphics getGraphics(){
    if(clss==InputOnly) return ddxwindow.getGraphics();
    else return ddxwindow.getGraphics2();
  }

  final Graphics getGraphics(GC gc, int mask){   
    if(clss==InputOnly) return ddxwindow.getGraphics();
    else return ddxwindow.getGraphics(gc, mask);
  }

  final Image getImage(){ return ddxwindow.getImage(); }

  final Image getImage(int x, int y, int width, int height){
    return ddxwindow.getImage(null, x, y, width, height);
  }

  final Image getImage(GC gc, int x, int y, int width, int height){
    return ddxwindow.getImage(gc, x, y, width, height);
  }

  private void setEventMask(Client c, int mask){
    int check;
    OtherClients others;
    check=(mask & Event.AtMostOneClient);
    if((check & (eventMask|getOtherEventMask()))!=0){
      if(getClient()!=c && (check & eventMask)!=0){
	c.errorReason=10; // BadAccess
      }
      for(others=getOtherClients(); others!=null; 
	   others=(OtherClients)others.next){
	if(!others.sameClient(c) && (check & others.mask)!=0){
	  c.errorReason=10; // BadAccess
	  return;
	}
      }
    }
    if(client==c){
	check=eventMask;
	eventMask=mask;
    }
    else {
      others=(optional==null)?null: optional.otherClients;
      for( ;others!=null; others=(OtherClients)others.next){
	if(others.sameClient(c)){
	  check=others.mask;
	  if(mask==0){
	    Resource.freeResource(others.id, RT_NONE);
	    return ;
	  }
	  else{
	    others.mask=mask;
	  }
	  recalculateDeliverableEvents();
	  return;
	}
      }
      check=0;
      makeOptional();

      others=new OtherClients(Resource.fakeClientId(c));
      others.mask=mask;
      others.resource=others.id;
      others.window=this;
      others.next=optional.otherClients;
      optional.otherClients=others;
      Resource.add(others);
    }
    recalculateDeliverableEvents();
    return ;
  }

  private static void gravityTranslate (int newx, int newy, 
					int oldx, int oldy, 
					int dw, int dh,
					int gravity, Point point){
    switch (gravity){
    case NorthGravity:
      point.x=newx + dw / 2;
      point.y=newy;
	break;
    case NorthEastGravity:
      point.x=newx + dw;
      point.y=newy;
	break;
    case WestGravity:
      point.x=newx;
      point.y=newy + dh / 2;
	break;
    case CenterGravity:
      point.x=newx + dw / 2;
      point.y=newy + dh / 2;
	break;
    case EastGravity:
      point.x=newx + dw;
      point.y=newy + dh / 2;
	break;
    case SouthWestGravity:
      point.x=newx;
      point.y=newy + dh;
	break;
    case SouthGravity:
      point.x=newx + dw / 2;
      point.y=newy + dh;
	break;
    case SouthEastGravity:
      point.x=newx + dw;
      point.y=newy + dh;
	break;
    case StaticGravity:
      point.x=oldx;
      point.y=oldy;
	break;
    default:
      point.x=newx;
      point.y=newy;
	break;
    }
  }

  private void resizeChildrenWinSize(int dx, int dy, int dw, int dh)
    throws IOException {
    boolean resized=(dw!=0 || dh!=0);
    for(Window sib=firstChild; sib!=null; sib=sib.nextSib){
      if(resized && 
	  (((sib.attr&winGravity)>>winGravityOffset) > NorthWestGravity)){
	int newx=sib.origin.x;
	int newy=sib.origin.y;
	gravityTranslate (newx, newy, newx-dx, newy-dy, dw, dh,
                          (sib.attr&winGravity)>>winGravityOffset,
                          gpoint);
	if(gpoint.x  !=sib.origin.x || gpoint.y !=sib.origin.y){
	  Event event=new Event();
	  event.mkGravityNotify(sib.id, sib.id, 
				gpoint.x - sib.borderWidth,
				gpoint.y - sib.borderWidth
                                );
	  sib.sendEvent(event, 1, null);
          sib.origin.x=gpoint.x;
          sib.origin.y=gpoint.y;
	}
      }
   
      sib.x=x + sib.origin.x;
      sib.y=y + sib.origin.y;
      if(sib.ddxwindow!=null) // ??
      sib.ddxwindow.setLocation(sib.origin.x-sib.borderWidth+sib.parent.borderWidth,
				sib.origin.y-sib.borderWidth+sib.parent.borderWidth);
      Window child=sib.firstChild;
      if(child !=null){
	while (true){
	  child.x=child.parent.x + child.origin.x;
	  child.y=child.parent.y + child.origin.y;
          if(child.ddxwindow!=null) // ??
	  child.ddxwindow.setLocation(child.origin.x-child.borderWidth+child.parent.borderWidth,
				      child.origin.y-child.borderWidth+child.parent.borderWidth);
	  if(child.firstChild!=null){
	    child=child.firstChild;
	    continue;
	  }
	  while (child.nextSib==null && (child !=sib))
	    child=child.parent;
	  if(child==sib) break;
	  child=child.nextSib;
	}
      }
    }
  }

   static boolean checkDeviceGrabs(Event e, int checkFirst, int count)
    throws IOException {

    Window win;
    int i=checkFirst;
    for(; i < spriteTraceGood; i++){
      win=spriteTrace[i];
      if(win.optional!=null &&
	  win.checkPassiveGrabsOnWindow(e, count)){
	return true;
      }
    }
    return false;
  }

  boolean checkPassiveGrabsOnWindow(Event e, int count) throws IOException {
    Grab grab=getPassiveGrabs();

    if(grab==null) return false;

    synchronized(LOCK){
      Grab tempGrab=new Grab(Resource.fakeClientId(client));
      tempGrab.window=this;
      tempGrab.type=e.getType();
      tempGrab.detail.exact=e.getDetail();
      tempGrab.detail.pMask=null;
      tempGrab.modifiersDetail.pMask=null;
      tempGrab.modifiersDetail.exact=e.getState()&0x0f;
      for(; grab!=null; grab=grab.next){
        if(tempGrab.grabMatchesSecond(grab) &&
 	   (grab.confineTo==null ||
	    ((grab.confineTo.attr&realized)!=0 &&
	     (grab.confineTo.x!=0 || grab.confineTo.y!=0)))){
	  grab.activatePointerGrab((int)System.currentTimeMillis(), true);
	  e.fixUpEventFromWindow(grab.window, 0, 
				 Window.sprite.hot.x, Window.sprite.hot.y,
				 true);
	  if(grab.getClient()!=null)
	    grab.getClient().sendEvent(e, count,
				       Event.filters[e.getType()],
				       Event.filters[e.getType()],  grab);
	  return true;
	}
      }
    }
    return false;
  }

  static void sendGrabbedEvent(Event e, boolean deactivateGrab, int count)
    throws IOException{
    Grab grab=Window.grab;
    int deliveries=0;

    if((grab.attr&Grab.ownerEvents)!=0){
      Window focus=null;
      if(focus==null)
	deliveries=sendDeviceEvent(sprite.win, e, grab, null, count);
      else if(focus!=null && 
	       (focus==sprite.win || focus.isParent(sprite.win)))
	deliveries=sendDeviceEvent(sprite.win, e, grab, focus, count);
      else if(focus!=null)
	deliveries=sendDeviceEvent(focus, e, grab, focus, count);
    }

    if(deliveries==0){
      e.fixUpEventFromWindow(grab.window, 0, 
			      sprite.hot.x, sprite.hot.y, true);
      if(grab.getClient()!=null)
	deliveries=grab.getClient().sendEvent(e, count,
						grab.eventMask,
						Event.filters[e.getType()], 
						grab);
    }
  }

  static int sendDeviceEvent(Window win, Event e, 
			     Grab grab, Window stopAt, int count)
    throws IOException {
    int child=0;
    int type=e.getType();
    int filter=Event.filters[type];
    int deliveries=0;

    if((type & Event.EXTENSION_EVENT_BASE)!=0){
      OtherInputMasks inputMasks;
      int mskidx=0;
      inputMasks=(win.optional==null? null: win.optional.otherInputMasks);
      if(inputMasks!=null && 
	  (filter & inputMasks.deliverableEvents[mskidx])==0)
	return 0;
      while (win!=null){
	if(inputMasks!=null && 
	    (inputMasks.inputEvents[mskidx] & filter)!=0){
	  e.fixUpEventFromWindow(win, child, sprite.hot.x, sprite.hot.y, 
				  false);
	  deliveries=win.sendEvent(e, count, filter, grab, mskidx);
	  if(deliveries > 0) return deliveries;
	}
	if((deliveries < 0) ||
	    (win==stopAt) ||
	    (inputMasks!=null &&
	     (filter & inputMasks.dontPropagateMask[mskidx])!=0))
	  return 0;
	child=win.id;
	win=win.parent;
	if(win!=null){
	  inputMasks=(win.optional==null? null : win.optional.otherInputMasks);
	}
      }
    }
    else {
      if((filter & win.deliverableEvents)==0) return 0;

      while (win!=null){
	if(((win.getOtherEventMask()|win.eventMask) & filter)!=0){
	  e.fixUpEventFromWindow(win, child, sprite.hot.x, sprite.hot.y,
				  false);
	  deliveries=win.sendEvent(e, count, filter,  grab, 0);
	  if(deliveries > 0) return deliveries;
	}
	if((deliveries < 0) ||
	    (win==stopAt) || ((filter & win.getDontPropagateMask())!=0))
	  return 0;
	child=win.id;
	win=win.parent;
      }
    }
    return 0;
  }

  boolean isParent(Window w){
    for(w=w.parent; w!=null; w=w.parent){
      if(w==this) return true;
    }
    return false;
  }
  Window commonAncestor(Window w){
    for(w=w.parent; w!=null; w=w.parent){
      if(w.isParent(this)) return w;
    }
    return null;
  }

  private static void doFocusEvents(Client c, int fromWin, int toWin, int mode)
    throws IOException {
    int out, in;

    if(fromWin==toWin) return;

    out=(fromWin==0) ? NotifyDetailNone : NotifyPointerRoot;
    in=(toWin==0) ? NotifyDetailNone : NotifyPointerRoot;

    if((toWin==0) || (toWin==1)){
      if((fromWin==0) || (fromWin==1)){
	if(fromWin==1){
	  focusOutEvents(c, sprite.win, sprite.win.screen.root, 
			 mode, NotifyPointer, true);
	}
      }
      else {
	Window from=(c!=null) ? c.lookupWindow(fromWin) : 
	            (Window)Resource.lookupIDByClass(fromWin, Resource.RC_DRAWABLE);
	if(from==null) return;
	if(from.isParent(sprite.win)){
	  focusOutEvents(c, sprite.win, from, mode, NotifyPointer, false);
	}
	from.sendFocusEvent(c,  Event.FocusOut, mode, NotifyNonlinear);
	focusOutEvents(c, from.parent, null, mode, NotifyNonlinearVirtual, false);
      }

      if(toWin==1){
	focusInEvents(c, sprite.win.screen.root, sprite.win, 
		      null, mode, NotifyPointer, true);
      }
    }
    else {
      if((fromWin==0) || (fromWin==1)){
	if(fromWin==1){
	  focusOutEvents(c, sprite.win, sprite.win.screen.root, 
			 mode, NotifyPointer, true);
	}

	Window to=(c!=null) ? c.lookupWindow(toWin) : 
	            (Window)Resource.lookupIDByClass(toWin, Resource.RC_DRAWABLE);
	if(to==null) return;
	if(to.parent !=null){
	  focusInEvents(c, to.screen.root, to, to, mode,
			NotifyNonlinearVirtual, true);
	}
	to.sendFocusEvent(c, Event.FocusIn, mode, 
		      NotifyNonlinear);
	if(to.isParent(sprite.win)){
	  focusInEvents(c, to, sprite.win, null, mode,
			NotifyPointer, false);
	}
      }
      else {
	Window to=(c!=null) ? c.lookupWindow(toWin) : 
	          (Window)Resource.lookupIDByClass(toWin, Resource.RC_DRAWABLE);
	Window from=(c!=null) ? c.lookupWindow(fromWin) : 
	          (Window)Resource.lookupIDByClass(fromWin, Resource.RC_DRAWABLE);
	if(from==null || to==null) return;
	if(to.isParent(from)){
	  from.sendFocusEvent(c, Event.FocusOut, mode, 
			  NotifyAncestor);
	  focusOutEvents(c, from.parent, to, mode,
			 NotifyVirtual, false);
	  to.sendFocusEvent(c, Event.FocusIn, mode, 
			NotifyInferior);
	  if((to.isParent(sprite.win)) &&
	      (sprite.win !=from) &&
	      (!from.isParent(sprite.win)) &&
	      (!sprite.win.isParent(from))){
	    focusInEvents(c, to, sprite.win, null, mode, NotifyPointer, false);
	  }
	}
	else{
	  if(from.isParent(to)){
	    if((from.isParent(sprite.win)) &&
		(sprite.win !=from) &&
		(!to.isParent(sprite.win)) &&
		(!sprite.win.isParent(to))){
	      focusOutEvents(c, sprite.win, from, mode, NotifyPointer, false);
	    }
	    from.sendFocusEvent(c, Event.FocusOut, mode, NotifyInferior);
	    focusInEvents(c, from, to, to, mode, NotifyVirtual, false);
	    to.sendFocusEvent(c, Event.FocusIn, mode, NotifyAncestor);
	  }
	  else {
	    Window common=to.commonAncestor(from);

	    if(from.isParent(sprite.win)){
	      focusOutEvents(c, sprite.win, from, mode, NotifyPointer, false);
	    }
	    from.sendFocusEvent(c, Event.FocusOut, mode, NotifyNonlinear);
	    if(from.parent !=null){
	      focusOutEvents(c, from.parent, common, mode, NotifyNonlinearVirtual, false);
	    }
	    if(to.parent !=null){
	      focusInEvents(c, common, to, to, mode, NotifyNonlinearVirtual, false);
	    }
	    to.sendFocusEvent(c, Event.FocusIn, mode, NotifyNonlinear);
	    if(to.isParent(sprite.win)){
	      focusInEvents(c, to, sprite.win, null, mode, NotifyPointer, false);
	    }
	  }
	}
      }
    }
  }

  private static void focusOutEvents(Client c, 
				     Window child, Window ancestor,
				     int mode, int detail, boolean doAncestor)
    throws IOException {
    for(Window win=child; win !=ancestor; win=win.parent){
      win.sendFocusEvent(c, Event.FocusOut, mode, detail);
    }
    if(doAncestor){
      ancestor.sendFocusEvent(c, Event.FocusOut, mode, detail);
    }
  }

  private static boolean focusInEvents(Client c,
				       Window ancestor, Window child, Window skipChild,
				       int mode, int detail, boolean doAncestor)
    throws IOException {
    if(child==null){
      return ancestor==null;
    }
    if(ancestor==child){
      if(doAncestor){
	child.sendFocusEvent(c, Event.FocusIn, mode, detail);
      }
      return true;
    }
    if(focusInEvents(c, ancestor, child.parent, skipChild, 
		      mode, detail, doAncestor)){
      if(child !=skipChild){
	child.sendFocusEvent(c, Event.FocusIn, mode, detail);
      }
      return true;
    }
    return false;
  }

  void sendFocusEvent(Client c, int type, int mode, int detail) 
    throws IOException {
    Event event=(c!=null) ? c.cevent : new Event();
    if(type==Event.FocusIn){ event.mkFocusIn(detail, id, mode); }
    else if(type==Event.FocusOut){ event.mkFocusOut(detail, id, mode); }
    else{ return; }
    sendEvent(event, 1, Event.filters[type], null, 0);
  }

  void initAttr(){
    setBackgroundIsPixel();
    background.pixel=screen.white;
    setBorderIsPixel();
    border.pixel=screen.black;
    borderWidth=0;
  }

  void restoreClip(){ ddxwindow.restoreClip(); }

  private static int lowbit(int mask){
    int result=1;
    for(int i=0; i<32; i++){
      if((mask & 1)==1){result=result<<i; break;}
      mask=mask>>1;
    }
    return result;
  }

  static void printWindow(Window p1, int indent){
	  StringBuffer buf = new StringBuffer();
    for(int i=0; i<indent; i++) buf.append(" ");
    try{
      buf.append(Integer.toHexString(p1.id));
//    buf.append(" mapped: "+p1.isMapped());
//    buf.append(", realized: "+p1.isRealized());
      buf.append(" ("+p1.x+","+p1.y+","+p1.width+","+p1.height+")");
//    buf.append(" location: "+p1.ddxwindow.getLocation());
      buf.append(" isVisible: "+p1.ddxwindow.isVisible());
      LOG.error(buf.toString());
    }
    catch(Exception ee){
    	LOG.error(ee);
    }
  }
  static void printChildren(Window p1, int indent){
    Window p2;
    while (p1!=null){
      p2=p1.firstChild;
      printWindow(p1, indent);
      printChildren(p2, indent+4);
      p1=p1.nextSib;
    }
  }
  static void printWindowTree(Window win){
    LOG.error("printWindowTree");
    printWindow(win, 0);
    Window p1;
    for(int i=0; i<1; i++){
	p1=win.firstChild;
	printChildren(p1, 4);
    }
  }

  private void addWindowListener(java.awt.Frame foo){
    final java.awt.Frame frame=foo; 
    frame.addWindowListener(
      new java.awt.event.WindowAdapter() {                
        public void windowClosed(java.awt.event.WindowEvent e){
        }
  	public void windowClosing(java.awt.event.WindowEvent e){
          int message_type=Atom.find("WM_PROTOCOLS");
          int del=Atom.find("WM_DELETE_WINDOW");

          synchronized(Window.LOCK){
            Property p=getProperty();
            while(p!=null){
              if(p.propertyName==message_type)break;
              p=p.next;
            }

            if(p!=null && p.data!=null){
            }
            else{
              try{
                getClient().closeDown();
              }
	      catch(Exception ee){}
              return;
            }
          }

	  if(message_type==0 || del==0) return;

          Event event=new Event();
          event.mkClientMessage(id, message_type);
          byte[] eb=event.event;
          eb[1]=32;

          eb[12]=(byte)(del>>>24);  //protocol
          eb[13]=(byte)(del>>>16);
          eb[14]=(byte)(del>>>8);
          eb[15]=(byte)(del);

          int tm=(int)System.currentTimeMillis();
          eb[16]=(byte)(tm>>>24);  //time
          eb[17]=(byte)(tm>>>16);
          eb[18]=(byte)(tm>>>8);
          eb[19]=(byte)(tm);

          eb[20]=eb[21]=eb[22]=eb[23]=0; 
          eb[24]=eb[25]=eb[26]=eb[27]=0; 
          eb[28]=eb[29]=eb[30]=eb[31]=0; 

          eb[0]|=0x80;

          try{ sendEvent(event, 1, Event.NoEventMask, null, 0); }
          catch(Exception ee){}
	}
      }
    );
  }

  private void addComponentListener(java.awt.Frame foo){
    final java.awt.Frame frame=foo; 
    frame.addComponentListener(
      new java.awt.event.ComponentAdapter() {                
        public void componentResized(java.awt.event.ComponentEvent e){
         if(parent==null)return;
          Rectangle rectangle=frame.getBounds();
          if(rectangle.width==0 ||rectangle.height==0)return;
	  if(frame_width!=rectangle.width || 
             frame_height!=rectangle.height){
            Insets insets=frame.getInsets();
	    synchronized(Window.LOCK){
   	      try{
                Point point=frame.getLocation();
                int ww=rectangle.width-insets.left-insets.right-borderWidth*2;
                int hh=rectangle.height-insets.top-insets.bottom-borderWidth*2;
	        if(ww>0 && hh>0){
                  ddxwindow.setSize(ww, hh);
                  frame.pack();
                  Event event=new Event();
	          event.mkConfigureNotify(id,
                                          id,
				          (rectangle.x+insets.left),
				          (rectangle.y+insets.top),
				          ww,
  	                                  hh,
				          borderWidth,
				          (attr&(1<<16))!=0?1:0);
	          sendEvent(event, 1, null);
                  frame_x=rectangle.x;
                  frame_y=rectangle.y;
                  frame_width=rectangle.width;
                  frame_height=rectangle.height;
		}
	      }
              catch(Exception ee){
                LOG.error(ee);
              }
            }
          }
        }
      }
    );
  }

}

class InputClients extends Clients{
  int[] mask;
  InputClients(int id){
    super(id);
    mask=new int[9/*EMASKSIZE*/];
  }
}

class OtherInputMasks {
  int[] deliverableEvents;
  int[] inputEvents;
  int[] dontPropagateMask;
  InputClients inputClients;
  OtherInputMasks(){
    deliverableEvents=new int[9/*EMASKSIZE*/];
    inputEvents=new int[9/*EMASKSIZE*/];
    dontPropagateMask=new int[9/*EMASKSIZE*/];
  }
}

class WindowOpt{
  int visual;
  Cursor cursor;
  Colormap colormap;
  int dontPropagateMask;
  int otherEventMasks;
  OtherClients otherClients;
  Grab passiveGrabs;
  Property userProps;
  long backingBitPlanes;
  long backingPixel;
  OtherInputMasks otherInputMasks;
  java.awt.Window frame;
}

class Sprite {
  Window win;
  HotSpot hot;
  Sprite(){
    hot=new HotSpot();
  }
}

final class HotSpot extends Point{
  int state=0;
}

class DontPropagate{
  static int[] masks=new int[8];
  static int[] refc=new int[8];
  static{
    for(int i=0;i<8;i++){
      masks[i]=refc[i]=0;
    }
  }

  static int store(int mask){
    int i=0;
    int free=0;
    for(i=8, free=0; --i > 0; ){
      if(refc[i]==0){free=i;}
      else if(mask==masks[i])
	break;
    }
    if(i==0 && free!=0){
      i=free;
      masks[i]=mask;
    }
    return i;
  }
}

class Focus{
  int win;
  Window window;
  int revert;
  long time;
  Window[] trace;
  int traceSize;
  int traceGood;
}
