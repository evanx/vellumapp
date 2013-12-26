/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.provider;

import java.io.IOException;
import java.net.Socket;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Streams;
import vellum.io.JsonSockets;
import venigma.server.CipherRequest;
import venigma.server.CipherRequestType;
import venigma.server.CipherResponse;

/**
 *
 * @author evan.summers
 */
public class CipherConnection {
    Logr logger = LogrFactory.getLogger(getClass());
    ClientContext clientContext;
    Socket socket;
    
    public CipherConnection(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    public void open() throws IOException {
        if (socket != null) {
            logger.warn("already open");
            Streams.close(socket);
        }
        this.socket = clientContext.createSocket();
        logger.info("open", socket.getRemoteSocketAddress());
        if (false) {
            CipherResponse response = sendCipherRequest(new CipherRequest(CipherRequestType.PING));
            logger.info("open ping response", response);
        }
    }

    public void close() throws IOException {
        if (socket == null) {
            logger.warn("already closed");
        } else {
            logger.info("close", socket.getRemoteSocketAddress());
            socket.close();
            socket = null;
        } 
    }
    
    public CipherResponse sendCipherRequest(CipherRequest request) throws IOException {
        logger.info("send", request);
        return (CipherResponse) sendSingleRequest(request, CipherResponse.class);
    }
    
    public Object sendSingleRequest(Object request, Class responseClass) throws IOException {
        try {
            if (socket == null) open();
            JsonSockets.write(socket, request);
            Object response = JsonSockets.read(socket, responseClass);
            return response;
        } finally {
            close();
        }
    }

}
