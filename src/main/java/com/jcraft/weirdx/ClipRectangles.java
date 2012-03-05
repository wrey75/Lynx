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

final class ClipRectangles implements Clip{
  private static final int Unsorted=0;
  private static final int YSorted=1;
  private static final int YXSorted=2;
  private static final int YXBanded=3;
                                         
  int ordering;
  java.awt.Rectangle[] masks=null;
  java.awt.Rectangle mask=null;

  ClipRectangles(int ordering, int size){
    this.ordering=ordering;
    if(size>0){
      masks=new java.awt.Rectangle[size];
    }
  }

  void add(int x, int y, int w, int h){
    int i=0;
    while(i<masks.length){
      if(masks[i]==null)break;
      i++;
    }
    masks[i]=new java.awt.Rectangle(x, y, w, h);
    if(i==masks.length-1){
      if(masks.length==1){
	mask=masks[0];
	return;
      }
      mask=new java.awt.Rectangle(masks[0]);
      for(int j=1; j<masks.length; j++){
	mask.add(masks[j]);
      }
    }
  }

//  void reset(int ordering, int size){
//    this.ordering=ordering;
//    this.size=size;
//    if(size>0){ masks=new java.awt.Rectangle[size]; }
//    else masks=null;
//  }

  boolean validate(){
    return true;
  }

  public Object getMask(){
    return mask;
  }
}




