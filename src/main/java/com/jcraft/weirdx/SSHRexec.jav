/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - wrapper for JSch rexec
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
import java.util.*;
import javax.swing.*;
import com.jcraft.jsch.*;

public class SSHRexec implements ActionListener{
  private static final String default_command="/usr/X11R6/bin/xterm";
  private static final Hashtable pool=new Hashtable();

  private final JTextField namehost=new JTextField(20);
  private final JPasswordField passwd=new JPasswordField(8);
  private final JTextField command=new JTextField(20);
  private final JButton rexec=new JButton("REXEC");

  private String display="localhost:2.0";
  private int displaynum=0;    
  private String xhost="127.0.0.1";

  private JSch jsch=null;

  public SSHRexec(int num){
    jsch=new JSch();

    this.displaynum=num;

    JFrame jframe=new JFrame();
    jframe.setTitle("weirdx.sshrexec");
    Container cpane=jframe.getContentPane();
    cpane.setLayout(new GridLayout(0, 1));

    JPanel jpanel=null;

    jpanel=new JPanel();
    jpanel.setBorder(BorderFactory.createTitledBorder("user@host[:port]"));
    jpanel.setLayout(new BorderLayout());
    namehost.setText("");
    namehost.setMinimumSize(new Dimension(50, 25));
    namehost.setEditable(true);
    jpanel.add(namehost, "Center");
    cpane.add(jpanel);
        
    jpanel=new JPanel();
    jpanel.setBorder(BorderFactory.createTitledBorder("command"));
    jpanel.setLayout(new BorderLayout());
    command.setText(default_command);
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
    SSHRexec srexec=new SSHRexec(2);
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {
    if(namehost.getText().length()==0 ||
       command.getText().length()==0){
      return;
    }

    String host=namehost.getText();
    String user=host.substring(0, host.indexOf('@'));
    host=host.substring(host.indexOf('@')+1);
    int port=22;
    if(host.indexOf(':')>0){
      try{
	port=Integer.parseInt(host.substring(host.indexOf(':')+1));
	host=host.substring(0, host.indexOf(':'));
      }
      catch(Exception ee){
	System.err.println(ee);
	return;
      }
    }

    if(user.length()==0 || host.length()==0){
      return;
    }

    try	{
      String key=user+"@"+host+":"+port;
      Session session=(Session)pool.get(key);
      if(session!=null && !session.isConnected()){
	session=null;
      }

      if(session==null){
	session=jsch.getSession(user, host, port);
	session.setX11Host(xhost);
	session.setX11Port(displaynum+6000);

	UserInfo ui=new MyUserInfo();
	session.setUserInfo(ui);
	session.connect();
	pool.put(key, session);
      }

      Channel channel=session.openChannel("exec");
      ((ChannelExec)channel).setCommand(command.getText());
      channel.setXForwarding(true);
      channel.setOutputStream(null);
      channel.connect();
    }
    catch(Exception ee) {
      System.out.println(ee);
    }
  }

  public static class MyUserInfo implements UserInfo{
    public String getPassword(){ return passwd; }
    public boolean promptYesNo(String str){
      Object[] options={ "yes", "no" };
      int foo=JOptionPane.showOptionDialog(null, 
             str,
             "Warning", 
             JOptionPane.DEFAULT_OPTION, 
             JOptionPane.WARNING_MESSAGE,
             null, options, options[0]);
       return foo==0;
    }
  
    String passwd;
    JTextField passwordField=(JTextField)new JPasswordField(20);

    public String getPassphrase(){ return null; }
    public boolean promptPassphrase(String message){ return true; }
    public boolean promptPassword(String message){
      Object[] ob={passwordField}; 
      int result=
	  JOptionPane.showConfirmDialog(null, ob, message,
					JOptionPane.OK_CANCEL_OPTION);
      if(result==JOptionPane.OK_OPTION){
	passwd=passwordField.getText();
	return true;
      }
      else{ return false; }
    }
    public void showMessage(String message){
      JOptionPane.showMessageDialog(null, message);
    }
  }
}
