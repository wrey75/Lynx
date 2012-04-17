package com.jcraft.weirdx;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DisplaySocket6k implements DisplaySocket {
	private static Log LOG = LogFactory.getLog(DisplaySocket6k.class);
	private ServerSocket sock = null;
	private int displayNumber;

	public void init(int displayNumber) throws IOException {
		this.displayNumber = displayNumber;
		sock = new ServerSocket(6000 + displayNumber);
		LOG.info( "Display number " + displayNumber + " initialized." );
	}

	public Socket accept() throws IOException {
		LOG.info( "Accepting new process on display " + this.displayNumber );
		return sock.accept();
	}

	public void close() throws IOException {
		sock.close();
		LOG.info( "Display " + this.displayNumber + " closed." );
		sock = null;
	}
}