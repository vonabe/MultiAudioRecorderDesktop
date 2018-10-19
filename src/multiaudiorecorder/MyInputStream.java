/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiaudiorecorder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author veniamin
 */
public class MyInputStream extends InputStream{

    private InputStream input;
    
    public void write(byte[]buffer){
        this.input = new ByteArrayInputStream(buffer);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.input.read(b);
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return input.read(b, off, len);
    }

    @Override
    public int read() throws IOException {
        return input.read();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.input.skip(n);
    }

    @Override
    public void close() throws IOException {
        this.input.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.input.mark(readlimit);
    }
    
    
    
}
