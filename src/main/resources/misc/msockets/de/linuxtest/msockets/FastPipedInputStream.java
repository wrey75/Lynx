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

public class FastPipedInputStream extends InputStream {

    private boolean closed;
    private boolean connected;
	private FastPipedOutputStream output;
    private static final int SIZE = 1024;
	private PipeBuffer buffer;

    public FastPipedInputStream() {
		buffer = new PipeBuffer(SIZE);
		connected = false;
		closed = true;
    }

    public void connect(FastPipedOutputStream out) throws IOException {
        out.connect(this);
		output = out;
		closed = false;
		connected = true;
    }

	public boolean isClosed(){
		return closed;
	}

	public PipeBuffer getBuffer(){
		return buffer;
	}

    public int read() throws IOException {
        if (!connected) {
            throw new IOException();
        } else if (closed) {
            throw new IOException();
        }

        while (buffer.isEmpty()) {
            if (output.isClosed()) {
                return -1;
            }
			try{
				Thread.sleep(1);
			} catch( InterruptedException e){
			} 
        }
		return (buffer.read()&0xff);
    }

    public int read(byte b[], int off, int len) throws IOException {
		int offset = 1, c;
        if (len == 0) {
            return 0;
        }
        c = read();
		if(c==-1) return -1;
        b[off] = (byte)c;
        while ((!buffer.isEmpty()) && (offset<len)) {
            b[off + offset] = buffer.read();
            offset++;
        }
        return offset;
    }

    public int available() throws IOException {
		return buffer.available();
    }

    public void close() throws IOException {
		buffer.clear();
        closed = true;
    }
}
