/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - wrapper for JRexec.
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
import java.awt.event.*;
import java.io.*;
import java.net.*;

import com.jcraft.util.*;

//import com.sun.java.swing.*;
import javax.swing.*;                                    

class XRexec implements ActionListener{
  private final JTextField name=new JTextField(8);
  private final JTextField host=new JTextField(20);
  private final JPasswordField passwd=new JPasswordField(8);
  private final JTextField command=new JTextField(20);
  private final JButton rexec=new JButton("REXEC");

  private String display="localhost:2.0";

  XRexec(String myName, int num){
    try{
      InetAddress local=null;
      if(myName!=null&&myName.length()>0){
        local=InetAddress.getByName(myName); 
      }
      else{ local=InetAddress.getLocalHost(); }
      display=local.getHostName()+":"+num+".0";
    }
    catch(Exception e){
      display="localhost:"+num+".0";
      System.out.println(e);
    }    

    JFrame jframe=new JFrame();
    Container cpane=jframe.getContentPane();
    cpane.setLayout(new GridLayout(0, 1));

    JPanel jpanel=null;

    jpanel=new JPanel();
    jpanel.setBorder(BorderFactory.createTitledBorder("Host"));
    jpanel.setLayout(new BorderLayout());
    host.setText("");
    host.setMinimumSize(new Dimension(50, 25));
    host.setEditable(true);
    jpanel.add(host, "Center");
    cpane.add(jpanel);
        
    jpanel=new JPanel();
    jpanel.setBorder(BorderFactory.createTitledBorder("User"));
    jpanel.setLayout(new BorderLayout());
    name.setText("");
    name.setMinimumSize(new Dimension(50, 25));
    name.setEditable(true);
    jpanel.add(name, "Center");
    cpane.add(jpanel);

    jpanel=new JPanel();
    jpanel.setBorder(BorderFactory.createTitledBorder("Password"));
    jpanel.setLayout(new BorderLayout());
    passwd.setMinimumSize(new Dimension(50, 25));
    passwd.setEditable(true);
    jpanel.add(passwd, "Center");
    cpane.add(jpanel);

    jpanel=new JPanel();
    jpanel.setBorder(BorderFactory.createTitledBorder("Command with Absolute Path"));
    jpanel.setLayout(new BorderLayout());
    command.setText("");
    command.setMinimumSize(new Dimension(50, 25));
    command.setEditable(true);
    jpanel.add(command, "Center");
    command.addActionListener(this);
    cpane.add(jpanel);

    jpanel=new JPanel();
    jpanel.add(rexec, "Center");
    rexec.addActionListener(this);
    cpane.add(jpanel);

    jframe.pack();
    jframe.setVisible(true);
  }

  public static void main(String[] arg){
    XRexec xsh=new XRexec(null, 2);
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {
    char [] pass=null;
    try{pass=passwd.getPassword();}catch(Exception ee){}
    if(name.getText().length()==0 ||
       host.getText().length()==0 ||
       pass==null || pass.length==0 ||
       command.getText().length()==0){
      return;
    }

    try	{
      String xcommand=null;

      xcommand="exec /bin/csh -cf \"setenv DISPLAY "+
               display+"; exec "+
               command.getText()+"  < /dev/null >>& /dev/null \"";

      JRexec jrexec=new JRexec(name.getText(), 
			       host.getText(), 
			       new String(pass),
			       xcommand);

      for(int i=0; i<pass.length; i++){pass[i]=(char)0;}

      InputStream in=jrexec.getResult();

      jrexec.doit();

      byte[] b=new byte[1];
      in.read(b, 0, 1);  // success??

      byte[] buf=new byte[1024];
      while(true){
	int len=in.read(buf, 0, buf.length);
	if(len==-1) break;
	if(len==0) break;
//	System.out.print(new String(buf, 0, len));
      }
      jrexec.close();
    }
    catch(Exception ee) {
      System.out.println(ee);
    }
  }
}
