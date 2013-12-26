/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Streams;

/**
 *
 * @author evan
 */
public class CipherServer extends Thread implements Closeable {
    Logr logger = LogrFactory.getLogger(this);
    CipherContext context;
    SSLServerSocket serverSocket; 
    boolean accepting = true;
    
    public CipherServer() {
    }
    
    public void config(CipherContext context) throws Exception {
        this.context = context;
        this.serverSocket = context.getServerSocket();
    }
    
    @Override
    public void run() {
        while (accepting) {
            try {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                logger.info("socket accepted", socket.getClass(), socket.getRemoteSocketAddress());
                CipherHandler handler = new CipherHandler(context);
                handler.init();
                handler.handle(socket);
            } catch (SocketException e) {
                logger.warn(e.getMessage());
            } catch (Exception e) {
                logger.warn(e, null);
            }
        }      
        Streams.close(serverSocket);
    }  
    
    @Override
    public void close() throws IOException {
        accepting = false;
        Streams.close(serverSocket);
    }
}
