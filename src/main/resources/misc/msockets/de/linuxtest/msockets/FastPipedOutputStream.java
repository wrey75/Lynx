/* MemorySockets
 * A library for thread communication behaving like ordinary Java sockets
 *
 * Copyright (C) 2000 by Marcus Schiesser <info@linuxtest.de> 
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

package de.linuxtest.msockets;

import java.io.*;

public class FastPipedOutputStream extends OutputStream {

	private boolean connected;
	private boolean closed;
	private FastPipedInputStream input;
	private PipeBuffer buffer;

    public FastPipedOutputStream() {
		connected = false;
		closed = true;
    }

    public void connect(FastPipedInputStream in) throws IOException {
        if(connected){
            throw new IOException();
        }
		input = in;
		buffer = input.getBuffer();
		connected = true;
		closed = false;
    }
	
	public boolean isClosed(){
		return closed;
	}

    public void write(int b) throws IOException {
        if (!connected){
            throw new IOException();
        } else if (closed || input.isClosed()) {
            throw new IOException();
        }

        while(buffer.isFull()){
			try{
				Thread.sleep(1);
			} catch( InterruptedException e){
			}
        }
		buffer.write((byte)(b&0xff));
    }

    public void write(byte b[], int off, int len) throws IOException {
        while (len > 0) {
            write(b[off++]);
			len--;
        }
    }

    public void close() throws IOException {
		closed = true;
    }
}
