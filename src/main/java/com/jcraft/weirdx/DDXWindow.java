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
import java.awt.*;

interface DDXWindow extends java.awt.image.ImageObserver {
 //Window window;
 //int width;
 //int height;
 //int bw;
 //boolean bwc;
 //Image offi;
 //Graphics offg;
 //static void init(Screen[] scrn , boolean tb);
 void init(Window w);
 void setBorder(int bw);
 public void setVisible(boolean b);
 public boolean isVisible();
 public boolean contains(int x, int y);
 public java.awt.Component add(java.awt.Component c, int i);
 public void requestFocus();
 public java.awt.Component[] getComponents();
 public void setLocation(int x, int y);
 public void setSize(int w, int h);
 public void setBackground(Color c, int x, int y, int w, int h);
 public void setBackground(Color c);
 public void setCursor(java.awt.Cursor cursor);
 public Image createImage(int w, int h);
 void draw(int x, int y, int w, int h);
 void draw();
//    public void paintBorder(java.awt.Graphics);
//    public void paintComponent(java.awt.Graphics);
//    public boolean isOpaque();
 void setBorderPixmap(Pixmap pixmap);
 Image getImage();
 Image getImage(GC gc, int x, int y, int w, int h);
 Graphics getGraphics();
 Graphics getGraphics2();
 Graphics getGraphics(GC gc, int mask);
 Window getWindow();
 void drawImage(Clip clip, Image img, int dx, int dy, int w, int h);
 void drawImage(Image img, int dx, int dy, int w, int h);
 void fillImage(Image img, int w, int h);
 void fillImage(Image img, int w, int h, int offx, int offy);
 void copyArea(Window dst, GC gc , int srcx, int srcy, 
	       int width, int height, int destx, int desty);
 void copyArea(int sx, int sy, int w, int h, int dx, int dy);
 void delete() throws java.io.IOException;
 void restoreClip();
}
