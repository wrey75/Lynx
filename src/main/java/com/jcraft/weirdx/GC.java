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

final class GC extends Resource {
  static final byte GXclear=0;
  static final byte GXand=1;
  static final byte GXandReverse=2;
  static final byte GXcopy=3;
  static final byte GXandInverted=4;
  static final byte GXnoop=5;
  static final byte GXxor=6;
  static final byte GXor=7;
  static final byte GXnor=8;
  static final byte GXequiv=9;
  static final byte GXinvert=10;
  static final byte GXorReverse=11;
  static final byte GXcopyInverted=12;
  static final byte GXorInverted=13;
  static final byte GXnand=14;
  static final byte GXset=15;

  static final int lineStyleOffset=0;
  static final int lineStyle=(3<<lineStyleOffset);
//  private byte lineStyle=LineSolid;
  static final int LineSolid=(0<<lineStyleOffset);
  static final int LineOnOffDash=(1<<lineStyleOffset);
  static final int LineDoubleDash=(2<<lineStyleOffset);

  static final int capStyleOffset=2;
  static final int capStyle=(3<<capStyleOffset);
  //private byte cap_style=CapButt;
  static final int CapNotLast=(0<<capStyleOffset);
  static final int CapButt=(1<<capStyleOffset);
  static final int CapRound=(2<<capStyleOffset);
  static final int CapProjecting=(3<<capStyleOffset);

  static final int joinStyleOffset=4;
  static final int joinStyle=(3<<joinStyleOffset);
  //private byte join_style=JoinMiter;
  static final int JoinMiter=(0<<joinStyleOffset);
  static final int JoinRound=(1<<joinStyleOffset);
  static final int JoinBevel=(2<<joinStyleOffset);

  static final int fillStyleOffset=6;
  static final int fillStyle=(3<<fillStyleOffset);

  //private byte fill_style=FillSolid;
  static final int FillSolid=(0<<fillStyleOffset);
  static final int FillTiled=(1<<fillStyleOffset);
  static final int FillStippled=(2<<fillStyleOffset);
  static final int FillOpaqueStippled=(3<<fillStyleOffset);

  static final int fillRuleOffset=8;
  static final int fillRule=(1<<fillRuleOffset);
//  private byte fill_rule=EvenOddRule;
  static final int EvenOddRule=(0<<fillRuleOffset);
  static final int WindingRule=(1<<fillRuleOffset);

  static final int arcModeOffset=9;
  static final int arcMode=(1<<arcModeOffset);
  //private byte arc_mode;
  static final int ArcChord=(0<<arcModeOffset);
  static final int ArcPieSlice=(1<<arcModeOffset);

  static final int subWindowModeOffset=10;
  static final int subWindowMode=(1<<subWindowModeOffset);
  static final int ClipByChildren=(0<<subWindowModeOffset);
  static final int IncludeInferiors=(1<<subWindowModeOffset);

  static final int graphicsExposuresOffset=11;
  static final int graphicsExposures=(1<<graphicsExposuresOffset);
  static final int tileIsPixelOffset=15;
  static final int tileIsPixel=(1<<tileIsPixelOffset);

  static final int GCFunction           =(1<<0);
  static final int GCPlaneMask          =(1<<1);
  static final int GCForeground         =(1<<2);
  static final int GCBackground         =(1<<3);
  static final int GCLineWidth          =(1<<4);
  static final int GCLineStyle          =(1<<5);
  static final int GCCapStyle           =(1<<6);
  static final int GCJoinStyle		=(1<<7);
  static final int GCFillStyle		=(1<<8);
  static final int GCFillRule		=(1<<9);
  static final int GCTile		=(1<<10);
  static final int GCStipple		=(1<<11);
  static final int GCTileStipXOrigin	=(1<<12);
  static final int GCTileStipYOrigin	=(1<<13);
  static final int GCFont 		=(1<<14);
  static final int GCSubwindowMode	=(1<<15);
  static final int GCGraphicsExposures  =(1<<16);
  static final int GCClipXOrigin	=(1<<17);
  static final int GCClipYOrigin	=(1<<18);
  static final int GCClipMask		=(1<<19);
  static final int GCDashOffset		=(1<<20);
  static final int GCDashList		=(1<<21);
  static final int GCArcMode		=(1<<22);

  static final int CT_NONE              =0;
  static final int CT_PIXMAP            =1;
  static final int CT_REGION            =2;
  static final int CT_UNSORTED          =6;        
  static final int CT_YSORTED           =10;
  static final int CT_YXSORTED          =14;
  static final int CT_YXBANDED          =18;

  Drawable drawable;
  Font font;
  short lineWidth;
  byte function;
  float dash[];
  float dash_phase;
  int attr;
  int time;
  Pix tile;
  Pixmap stipple;
  short tile_stipple_x_origin;
  short tile_stipple_y_origin;
  int clip_x_origin;
  int clip_y_origin;
  Clip clip_mask;
  int fgPixel, bgPixel;

  GC(int id, Drawable dr) {
    super(id, RT_GC);
    this.drawable=dr;
    init();
  }

  private void init(){
    font=Font.dflt;
    lineWidth=1 /*0*/;
    function=GXcopy;
    dash=null;
    dash_phase=0.0f;

    attr=( LineSolid|CapButt|JoinMiter|FillSolid|EvenOddRule
	  |ArcPieSlice|ClipByChildren|graphicsExposures);

    time=0;
    tile=new Pix();
    stipple=null;
    tile_stipple_x_origin=0;
    tile_stipple_y_origin=0;
    clip_x_origin=0;
    clip_y_origin=0;
    clip_mask=null;
    fgPixel=0;
    bgPixel=1;
  }

  static void reqSetDashes(Client c) throws IOException{
    int foo, ordering;
    int n;
    IO io=c.client;
    n=c.length;
    foo=io.readInt();

    c.length-=2;
    GC gc=c.lookupGC(foo);
    if(gc==null){
      c.errorValue=foo;
      c.errorReason=13; // GC
      return;
    }

    foo=io.readShort();
    gc.dash_phase=(float)foo;
    foo=io.readShort();
    if(foo==0){
      gc.dash=null;
      return;
    }

    gc.dash=new float[foo];

    n-=3;
    n*=4;
    for(int i=0; i<foo; i++){
      gc.dash[i]=(float)io.readByte();
      n--;
    }
    if(n>0){
      io.readPad(n);
    }
    gc.time=0;
  }

  static void reqSetClipRectangles(Client c) throws IOException{
    int foo, ordering;
    int n;
    IO io=c.client;

    ordering=c.data;
    n=c.length;
    foo=io.readInt();
    c.length-=2;
    GC gc=c.lookupGC(foo);
    if(gc==null){
      c.errorValue=foo;
      c.errorReason=13; // GC
      return;
    }
    gc.clip_x_origin=io.readShort();
    gc.clip_y_origin=io.readShort();
    c.length--;

    short x, y;
    int w, h;

    n=n-3;

    ClipRectangles cr=null;
    if(gc.clip_mask!=null){
      if(gc.clip_mask instanceof ClipPixmap){
        try{((Pixmap)(gc.clip_mask.getMask())).delete();}
        catch(Exception e){}
        gc.clip_mask=null;
      }	
    }

    if(cr==null){
      cr=new ClipRectangles(ordering, n/2);
      gc.clip_mask=cr;
    }

    while(n!=0){
      x=(short)io.readShort(); 
      y=(short)io.readShort();
      w=io.readShort(); 
      h=io.readShort();

      n-=2;
      cr.add(x, y, w, h);
    }

    cr.validate();
    c.length=0;
  }

  static void reqFreeGC(Client c) throws IOException{
    int foo;
    IO io=c.client;

    foo=io.readInt();

    c.length-=2;

    GC gc=c.lookupGC(foo);
    if(gc==null){
      c.errorValue=foo;
      c.errorReason=13; // GC
      return;
    }
    Resource.freeResource(foo, Resource.RT_NONE);
  }
  void delete() throws IOException{
    dash=null;
    if(stipple!=null){
      try{stipple.delete();}catch(Exception e){}
      stipple=null;
    }
    if(clip_mask!=null){
      try{
	if(clip_mask instanceof ClipPixmap){
	  ((Pixmap)(clip_mask.getMask())).delete();
	}
      }catch(Exception e){}
      clip_mask=null;
    }
    if(tile.pixmap!=null){
      try{tile.pixmap.delete();}catch(Exception e){}
      tile.pixmap=null;
      tile=null;
    }
    put(this);
  }
  static void reqCopyGC(Client c) throws IOException{
    int foo;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    GC srcgc=c.lookupGC(foo);
    if(srcgc==null){
      c.errorValue=foo;
      c.errorReason=13; // GC
      return;
    }
    foo=io.readInt();
    c.length--;
    GC dstgc=c.lookupGC(foo);
    if(dstgc==null){
      c.errorValue=foo;
      c.errorReason=13; // GC
      return;
    }
    int msk=io.readInt();
    c.length--;
    msk&=0x7fffff;
    dstgc.copyAttr(c, msk, srcgc);
  }
  static void reqChangeGC(Client c) throws IOException{
    int foo;
    int n;
    IO io=c.client;
    n=c.length;
    foo=io.readInt();
    c.length-=2;
    GC gc=c.lookupGC(foo);
    if(gc==null){
      c.errorValue=foo;
      c.errorReason=13; // GC
      return;
    }
    foo=io.readInt();
    c.length--;
    foo&=0x7fffff;
    if(foo!=0){
      gc.changeAttr(c, foo);
    }
  }
  static void reqCreateGC(Client c) throws IOException{
    int foo;
    IO io=c.client;
    int cid=io.readInt();
    foo=io.readInt();
    c.length-=3;
    Drawable d=c.lookupDrawable(foo);
    if(d==null){
      c.errorValue=foo;
      c.errorReason=9; // Drawable
      return;
    }

    int mask=io.readInt();
    c.length--;
    GC gc=getGC(cid, d);
    mask&=0x7fffff;
    if(mask!=0){
      gc.changeAttr(c, mask);
    }
    Resource.add(gc);
  }

  private void copyAttr(Client c, int vmask, GC srcgc) throws IOException{
    int index=0;
    int foo;
    int mask=vmask;

    while(mask!=0){
      index=lowbit(mask);
      mask&=~index;
      switch(index){
      case GCFunction:
	function=(byte)srcgc.function;
	break;
      case GCPlaneMask:
	break;
      case GCForeground:
	fgPixel=srcgc.fgPixel;
	if((attr&tileIsPixel)==0 && tile.pixmap==null){
	  attr|=tileIsPixel;
	  tile.pixel=fgPixel;
	}
	break;
      case GCBackground:
	bgPixel=srcgc.bgPixel;
	break;
      case GCLineWidth:
	lineWidth=srcgc.lineWidth;
	break;
      case GCLineStyle:
	attr&=~lineStyle;
	attr|=(srcgc.attr&lineStyle);
	break;
      case GCCapStyle:
	attr&=~capStyle;
	attr|=(srcgc.attr&capStyle);
	break;
      case GCJoinStyle:
	attr&=~joinStyle;
	attr|=(srcgc.attr&joinStyle);
	break;
      case GCFillStyle:
	attr&=~fillStyle;
	attr|=(srcgc.attr&fillStyle);
	break;
      case GCFillRule:
	attr&=~fillRule;
	attr|=(srcgc.attr&fillRule);
	break;
      case GCTile:
//	tile=srcgc.tile;
	if((srcgc.attr&tileIsPixel)!=0){
	  tile.pixel=srcgc.tile.pixel;
	}
	else{
	  tile.pixmap=srcgc.tile.pixmap;
	}
	break;
      case GCStipple:
	if(stipple!=null){
	  try{stipple.delete();}catch(Exception e){}
	  stipple=null;
	}
	stipple=srcgc.stipple;
	if(stipple!=null){
	  stipple.ref();
	}
	break;
      case GCTileStipXOrigin:
	tile_stipple_x_origin=srcgc.tile_stipple_x_origin;
	break;
      case GCTileStipYOrigin:
	tile_stipple_y_origin=srcgc.tile_stipple_y_origin;
	break;
      case GCFont:
	font=srcgc.font;
	break;
      case GCSubwindowMode:
	attr&=~subWindowMode;
	attr|=(srcgc.attr&subWindowMode);
	break;
      case GCGraphicsExposures:
	attr&=~graphicsExposures;
	attr|=(srcgc.attr&graphicsExposures);
	break;
      case GCClipXOrigin:
	clip_x_origin=srcgc.clip_x_origin;
	break;
      case GCClipYOrigin:
	clip_y_origin=srcgc.clip_y_origin;
	break;
      case GCClipMask:
	if(clip_mask!=null){
	  try{
	    if(clip_mask instanceof ClipPixmap){
	      ((Pixmap)(clip_mask.getMask())).delete();
	    }
	  }catch(Exception e){}
	  clip_mask=null;
	}
	clip_mask=srcgc.clip_mask;
	if(clip_mask!=null){
	  if(clip_mask instanceof ClipPixmap){
	    ((Pixmap)(clip_mask.getMask())).ref();
	  }
	}
	break;
      case GCDashOffset:
        dash_phase=srcgc.dash_phase;
	break;
      case GCDashList:
        dash=srcgc.dash;
        if(dash!=null){
          float[] bar=new float[dash.length];
          System.arraycopy(dash, 0, bar, 0, bar.length);
          dash=bar;
        }
	break;
      case GCArcMode:
        // not implemented
	break;
      default:
	c.errorValue=vmask;
	c.errorReason=2; // BadValue;
      }
      if(c.errorReason!=0){
	return;
      }
    }

    if(c.length!=0){
      c.errorValue=vmask;
      c.errorReason=2; // BadValue;
    }
    else{
      time=c.seq;
    }
  }

  private void changeAttr(Client c, int vmask) throws IOException{
    int index=0;
    int foo;
    int mask=vmask;
    IO io=c.client;

    while(mask!=0){
      index=lowbit(mask);
      mask&=~index;
      c.length--;
      switch(index){
      case GCFunction:
	foo=io.readInt();
        foo&=0xff;
	function=(byte)foo;
	break;
      case GCPlaneMask:
	foo=io.readInt();
	break;
      case GCForeground:
	foo=io.readInt();
	fgPixel=foo;
	if((attr&tileIsPixel)==0 && tile.pixmap==null){
	  attr|=tileIsPixel;
	  tile.pixel=fgPixel;
	}
//	fgPixel&=0xff;  // depth 8
	break;
      case GCBackground:
	bgPixel=io.readInt();
//	bgPixel&=0xff; // depth 8
	break;
      case GCLineWidth:
	foo=io.readInt();
        foo&=0xffff;
	if(foo==0) foo=1;
	lineWidth=(short)foo;
	break;
      case GCLineStyle:
	foo=io.readInt();
        foo&=0xff;
	foo=(foo<<lineStyleOffset)&lineStyle;
	if(foo!=LineSolid &&
	   foo!=LineOnOffDash &&
	   foo!=LineDoubleDash){
	  c.errorValue=foo>>lineStyleOffset;
	  c.errorReason=2; // BadValue
	  break;
	}	   
	attr&=~lineStyle;
	attr|=foo;
	break;
      case GCCapStyle:
	foo=io.readInt();
        foo&=0xff;
	foo=(foo<<capStyleOffset)&capStyle;
	if(foo!=CapNotLast &&
	   foo!=CapButt &&
	   foo!=CapRound &&
	   foo!=CapProjecting){
	  c.errorValue=foo>>capStyleOffset;
	  c.errorReason=2; // BadValue
	  break;
	}	   
	attr|=foo;

	break;
      case GCJoinStyle:
	foo=io.readInt();
        foo&=0xff;
	foo=(foo<<joinStyleOffset)&joinStyle;
	if(foo!=JoinMiter &&
	   foo!=JoinRound &&
	   foo!=JoinBevel){
	  c.errorValue=foo>>joinStyleOffset;
	  c.errorReason=2; // BadValue
	  break;
	}	   
	attr|=foo;

	break;
      case GCFillStyle:
	foo=io.readInt();
        foo&=0xff;
	attr=(attr&~fillStyle)|(foo<<fillStyleOffset);
	break;
      case GCFillRule:
	foo=io.readInt();
        foo&=0xff;
	break;
      case GCTile:
	foo=io.readInt();
	{
	  Resource o=Resource.lookupIDByType(foo, Resource.RT_PIXMAP);
	  if(o!=null && (o instanceof Pixmap)){
	    Pixmap tmp=(Pixmap)o;
	    if(tmp.depth!=drawable.depth ||
	       tmp.screen!=drawable.screen){
	      c.errorValue=foo;
	      c.errorReason=8; // BadMatch
	      break;
	    }
	    if(tile.pixmap!=null){
	      try{tile.pixmap.delete();}catch(Exception e){}
	    }
	    tile.pixmap=tmp;
	    tile.pixmap.ref();
	    attr&=~tileIsPixel;
	  }
	  else{
	    c.errorValue=foo;
	    c.errorReason=2; // BadValue
	    break;
	  }
	}
	break;
      case GCStipple:
	foo=io.readInt();
	{
	  Resource o=Resource.lookupIDByType(foo, Resource.RT_PIXMAP);
	  if(o!=null && (o instanceof Pixmap)){
	    Pixmap tmp=(Pixmap)o;
	    if(tmp.depth!=1 ||
	       tmp.screen!=drawable.screen){
	      c.errorValue=foo;
	      c.errorReason=8; // BadMatch
	      break;
	    }
	    if(stipple!=null){
	      try{stipple.delete();}catch(Exception e){}
	    }
	    stipple=tmp;
	    stipple.ref();
	  }
	  else{
	    c.errorValue=foo;
	    c.errorReason=2; // BadValue
	    break;
	  }
	}
	break;
      case GCTileStipXOrigin:
	foo=io.readInt();
	tile_stipple_x_origin=(short)(foo&0xffff);
	break;
      case GCTileStipYOrigin:
	foo=io.readInt();
	tile_stipple_y_origin=(short)(foo&0xffff);
	break;
      case GCFont:
	foo=io.readInt();
	font=(Font)Resource.lookupIDByType(foo, RT_FONT);
	if(font==null){
	}
	break;
      case GCSubwindowMode:
	foo=io.readInt();
	foo&=0xff;
	if(foo!=0 && foo!=1){
	}
	attr&=~subWindowMode;
	if(foo==1){ attr|=IncludeInferiors; }
//	else{ attr|=ClipByChildren; }

	break;
      case GCGraphicsExposures:
	foo=io.readInt();
	foo&=0xff;
	if(foo==1) attr|=graphicsExposures;
	if(foo==0) attr&=~graphicsExposures;
	break;
      case GCClipXOrigin:
	foo=io.readInt();
	clip_x_origin=(short)(foo&0xffff);
	break;
      case GCClipYOrigin:
	foo=io.readInt();
	clip_y_origin=(short)(foo&0xffff);
	break;
      case GCClipMask:
	foo=io.readInt();
	if(foo==0){
	  if(clip_mask!=null){
	    try{
	      if(clip_mask instanceof ClipPixmap){
		((Pixmap)(clip_mask.getMask())).delete();
	      }
	    }catch(Exception e){}
	  }
	  clip_mask=null;
	}
	else{
	  Drawable d=c.lookupDrawable(foo);
	  if(d==null || !(d instanceof Pixmap) || d.depth!=1){
	    c.errorValue=foo;
	    c.errorReason=8; // BadMatch
	    break;
	  }
	  if(clip_mask!=null){
	    try{
	      if(clip_mask instanceof ClipPixmap){
		((Pixmap)(clip_mask.getMask())).delete();
	      }
	    }catch(Exception e){}
	  }
	  ((Pixmap)d).ref();
	  clip_mask=new ClipPixmap((Pixmap)d);
	}
	//System.out.println(clip_mask);
	break;
      case GCDashOffset:
	foo=io.readInt();
	foo&=0xffff;
	break;
      case GCDashList:
	foo=io.readInt();
	foo&=0xff;
	break;
      case GCArcMode:
	foo=io.readInt();
	foo&=0xff;
	break;
      default:
	c.length++;
	c.errorValue=vmask;
	c.errorReason=2; // BadValue;
      }
      if(c.errorReason!=0){
	return;
      }
    }

    if(c.length!=0){
      c.errorValue=vmask;
      c.errorReason=2; // BadValue;
    }
    else{
      time=c.seq;
    }
  }

  static private int lowbit(int mask){
    int result=1;
    for(int i=0; i<32; i++){
      if((mask & 1)==1){result=result<<i; break;}
      mask=mask>>1;
    }
    return result;
  }

  static private GC getGC(int id, Drawable d){
    GC gc=get();
    if(gc==null){ return new GC(id, d);}
    gc.id=id; gc.drawable=d;
    gc.init();
    return gc;
  }

  static private GC pool[]=new GC[32];
  static private boolean full=false;

  static private synchronized GC get(){
    GC tmp=null;
    if(full) full=false;
    for(int i=0; i<32; i++){
      if(pool[i]!=null){ tmp=pool[i]; pool[i]=null; break;}
    }
    return tmp;
  }

  static private synchronized void put(GC gc){
    if(full) return;
    for(int i=0; i<32; i++){
      if(pool[i]==null){ pool[i]=gc; return; }
    }
    full=true;
  }
}
