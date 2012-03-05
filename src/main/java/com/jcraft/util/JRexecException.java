package com.jcraft.util;

public class JRexecException extends Exception {
  public JRexecException () {
    super();
  }
  public JRexecException (String s) {
    super ("JRexec: "+s);
  }
}
